package com.netazoic.covid.ent;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.netazoic.covid.ent.JH_TimeSeries.JH_Column;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JH_Global_TimeSeries extends JH_TimeSeries{
	
	public String state;
	public String country;
//	private Double lat;
//	private Double lon;
	public String date;
	public Integer ct;
	public String type;

	public JH_Global_TimeSeries() throws ENTException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Integer expireCombinedRecs() throws SQLException {
		//Expire all records of this type in the combined records table
		String q = "DELETE FROM covid.combined WHERE sourcecode = '" + this.srcOrg.code + "'";
		q += " AND type = '" + this.type + "'";
		return SQLUtil.execSQL(q, con);
	}
	
	@Override
	public void importRecords(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		// Import Global records
		HashMap<String, Object> recMap;
		boolean flgCreate;
		String state;
		String country;
		String dateStr;
		String ctStr;
		LocalDate date;
		Integer ct, ctNew = 0;
		recMap = new HashMap<String,Object>();
		Map<String,String> row;
		String[] dateParts;
		String mo,day,yr;
		String msgInfo;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
		CsvMapper mapper = new CsvMapper();
		CsvSchema schema = CsvSchema.emptySchema().withHeader(); // use first row as header; otherwise defaults are fine
		MappingIterator<Map<String,String>> itr = mapper.readerFor(Map.class)
				.with(schema)
				.readValues(is);
		while (itr.hasNext()) {

			row = itr.next();
			logger.debug(row.toString());
			// access by column name, as defined in the header row...
			Object[] keys =  row.keySet().toArray();
			state = row.get(keys[0]);
			if(state!=null && state.isEmpty()) state = null;
			country = row.get(keys[1]);
			recMap.put(JH_Column.state.name(), state);
			recMap.put(JH_Column.country.name(), country);

			for (int idx = 4; idx< row.size(); idx++) {
				dateStr = (String) keys[idx];
				if(dateStr.length()<8) {
					dateParts = dateStr.split("/");
					mo = dateParts[0];
					day = dateParts[1];
					yr = dateParts[2];
					if(mo.length()<2) mo = "0" + mo;
					if(day.length()<2) day= "0" + day;
					dateStr = mo + "/" + day + "/" + yr;
				}
				date = LocalDate.parse(dateStr, formatter);
				ctStr = row.get(keys[idx]);
				ct = Integer.parseInt(ctStr);
				recMap.put(JH_Column.date.name(), dateStr);
				recMap.put(JH_Column.ct.name(), ct);
				try {
					savePt = con.setSavepoint();
					ctrObj.ctTotalRecords.increment();
					flgCreate = rmdObj.createRemoteDataRecord(recMap,con);
					if(flgCreate) ctrObj.ctNewRecordsCreated.increment();;
					msgInfo = "Processed remote record: " + recMap.toString();
					logger.debug(msgInfo);
					if(ctrObj.ctTotalRecords.value%100 == 0){
						logger.info(msgInfo);
						logger.info(ctrObj.ctTotalRecords.value + " records processed.");
					}
				}catch(SQLException sql) {
					logger.error(sql.getMessage());
					con.rollback(savePt);
					ctrObj.ctBadRecords.increment();
				}
			}
			// Commit after processing each row
			// Otherwise the number of locks on the db grows beyond max capacity
			con.commit();
		}

	}


}
