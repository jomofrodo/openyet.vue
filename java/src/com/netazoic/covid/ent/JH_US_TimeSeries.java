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
import com.netazoic.covid.Covid19.CVD_DataSrc;
import com.netazoic.ent.ENTException;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JH_US_TimeSeries extends JH_TimeSeries {
	
	/*
	 * UID,iso2,iso3,code3,FIPS,Admin2,Province_State,Country_Region,Lat,Long_,Combined_Key, TS ...
	 */
	
	public Double UID;
	public String iso3;
//	public String code3;
	public Double FIPS;
	public String county;  //Admin2
	public String state;
	public String country;
//	private Double lat;
//	private Double long_;
	public Integer population;
	public String date;
	public Integer ct;
	public String type;
	
	
	public enum JH_US_Source_Column{
		UID(0), iso3(2), FIPS(4),Province_State(6), Country_Region(7), Lat(8), long_(9), date, ct, Admin2, Population;

		public Integer idx;

		JH_US_Source_Column(){ }
		JH_US_Source_Column(int x){
			this.idx = x;
		}
	}

	public enum JH_US_DB_Column{
		uid,
		iso3,
		fips,
		state,
		country,
		lat,
		lon,
		date,
		ct,
		type, population, county,
	}

	public JH_US_TimeSeries() throws ENTException {
		super();
		// TODO Auto-generated constructor stub
	}
	
	protected LocalDate getLastCombinedUpdate(String srcCode, Connection con) throws SQLException {
		// Get the date of the last update
		// US combined records always have sourcecode 'JH_US_CONF'
		LocalDate maxDate = null;
		String q = "SELECT max(date) as maxDate FROM combined WHERE sourcecode = '" + this.dataSrc.getSrcCode() + "'";
		String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
		if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
		else maxDate = LocalDate.parse(maxDateS);
		return maxDate;
	}
	protected LocalDate getLastUpdateDate(String srcCode, Connection con) throws SQLException {
		// Get the date of the last update
		// US combined records always have sourcecode 'JH_US_CONF'
		LocalDate maxDate = null;
		String q = "SELECT max(to_date(date,'mm/dd/yy')) as maxDate FROM jh_us_timeseries WHERE type = '" + this.tsType.getCode() + "'";
		String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
		if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
		else maxDate = LocalDate.parse(maxDateS);
		return maxDate;
	}
	
	public void importRecords(ifRemoteDataObj rmdObj, LocalDate maxDate, RemoteDataRecordCtr ctrObj, int idxTS_Start,Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		HashMap<String, Object> recMap;
		boolean flgCreate;
		String uid;
		String state;
		String country;
		String fips;
		String county;
		String population;
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
			uid = row.get(JH_US_Source_Column.UID.name());
			fips = row.get(JH_US_Source_Column.FIPS.name());
			if(fips.contains(""))fips = "0";
			state = row.get(JH_US_Source_Column.Province_State.name());
			if(state!=null && state.isEmpty()) state = null;
			country = row.get(JH_US_Source_Column.Country_Region.name());
			county = row.get(JH_US_Source_Column.Admin2.name());
			if(county!=null && county.isEmpty()) county = null;
			population = row.get(JH_US_Source_Column.Population.name());
			recMap.put(JH_US_DB_Column.uid.name(), uid);
			recMap.put(JH_US_DB_Column.county.name(), county);
			recMap.put(JH_US_DB_Column.fips.name(), fips);
			recMap.put(JH_US_DB_Column.state.name(), state);
			recMap.put(JH_US_DB_Column.country.name(), country);
			recMap.put(JH_US_DB_Column.population.name(), population);

			for (int idx = idxTS_Start; idx< row.size(); idx++) {
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
				if(!date.isAfter(maxDate)) continue;
				ctStr = row.get(keys[idx]);

				try{
					ct = Integer.parseInt(ctStr);
				}catch(Exception ex) {
					if(ctStr.indexOf(".")>0) {
						ctStr = ctStr.substring(0,ctStr.indexOf("."));   //Sometimes with a .0?
						ct = Integer.parseInt(ctStr);
					} else throw new Exception("Bad ct value: " + ctStr);
				}
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
					if(ctrObj.ctBadRecords.value > MAX_BAD_RECORDS) {
						logger.error("Reached MAX_BAD_RECORDS limit, exiting");
						itr.emptyIterator();
						return;
					}
				}catch(Exception ex) {
					logger.error(ex.getMessage());
					con.rollback(savePt);
					ctrObj.ctBadRecords.increment();
					if(ctrObj.ctBadRecords.value > MAX_BAD_RECORDS) {
						logger.error("Reached MAX_BAD_RECORDS limit, exiting");
						itr.emptyIterator();
						return;
					}
				}finally {
					if(ctrObj.ctTotalRecords.value%1000 == 0){
						logger.warn(ctrObj.ctTotalRecords.value + " records processed.");
					}
				}
			}
			// Commit after processing each row
			// Otherwise the number of locks on the db grows beyond max capacity
			con.commit();
		}

	}


}
