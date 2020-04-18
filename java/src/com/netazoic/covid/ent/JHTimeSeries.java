package com.netazoic.covid.ent;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
import com.netazoic.covid.ent.JHTimeSeries.JH_Column;
import com.netazoic.covid.ent.JHTimeSeries.JH_US_DB_Column;
import com.netazoic.covid.ent.JHTimeSeries.JH_US_Source_Column;
import com.netazoic.covid.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.if_TP;
import com.netazoic.ent.rdENT;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JHTimeSeries extends rdENT<ifDataSrcWrapper>{
	private static final int MAX_BAD_RECORDS = 100;
	// A Johns Hopkins  time series entry

	protected ifDataType tsType;
	DataFmt dataFmt = DataFmt.CSV;


	public enum JH_TimeSeriesType implements ifDataType{
		confirmed("C"), dead("D"), recovered("R");

		String code;
		JH_TimeSeriesType(String c){
			this.code = c;
		}
		@Override
		public String getCode() {
			return code;
		}
	}
	public enum JH_Column{
		state(0), country(1), lat(2), lon(3), date, ct, fips;

		public Integer idx;

		JH_Column(){ }
		JH_Column(int x){
			this.idx = x;
		}
	}

	/*
	 * UID,iso2,iso3,code3,FIPS,Admin2,Province_State,Country_Region,Lat,Long_,Combined_Key, TS ...
	 */
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
		type, population, city,
	}

	public enum JH_TP implements if_TP{

		sql_CREATE_RECORDx("/Data/sql/JH/TimeSeries/psCreateRecord.sql"),
		sql_CREATE_COMBINED_RECS("/Data/sql/JH/TimeSeries/CreateCombinedRecs.sql");


		public String tPath;

		JH_TP(String p){
			this.tPath = p;
		}

		@Override
		public String getTP() {
			return tPath;
		}

		@Override
		public void setTP(String p) {
			this.tPath = p;

		}

	}
	public JHTimeSeries() throws ENTException {
		super();
		initENT();
	}


	@Override
	public void initENT() throws ENTException{
		this.nit.ctpClass = JH_TP.class;
		nit.ENTITY_NAME = "JH Time Series";
		nit.NIT_TABLE = "covid.jh_timeseries";
		nit.FLD_NIT_ID = "date";
		super.initENT();

	}

	@Override
	public Integer createCombinedRecs() throws Exception {
		HashMap map = new HashMap();
		String q =  parseUtil.parseQueryFile(JH_TP.sql_CREATE_COMBINED_RECS.tPath,map);
		return SQLUtil.execSQL(q, con);
	}

	@Override
	public Long createRecord(HashMap<String, Object> paramMap, Connection con) throws ENTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRecord(String webuserID, String comments) throws ENTException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDataURL() {
		return this.dataURL;
	}

	@Override
	public ifDataSrc getSrc() {
		return this.dataSrc;

	}

	@Override
	public void setCon(Connection con) {
		this.con = con;
	}

	@Override
	public void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException {

		psDeleteRemoteData.setString(1, this.tsType.getCode());
	}

	@Override
	public void setInsertStatement(PreparedStatement ps) throws SQLException {
		//		NOT IN USE

	}

	@Override
	public void setSrc(ifDataSrc src) {
		this.dataSrc = src;

	}

	@Override
	public void setType(ifDataType type) {
		this.tsType = type;

	}

	@Override
	public PreparedStatement setupExpireAllStatement(Connection con) throws SQLException {
		String sql = "DELETE FROM covid.jh_timeseries WHERE type = ?";
		PreparedStatement psDeleteRemoteData = con.prepareStatement(sql);
		return psDeleteRemoteData;
	}

	@Override
	public void setUpdateStatement(PreparedStatement psUpdateRemoteData) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public SRC_ORG getSrcOrg() {
		return this.srcOrg;
	}

	@Override
	public DataFmt getFormat() {
		return this.dataFmt;
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

					logger.info(msgInfo);
					if(ctrObj.ctTotalRecords.value%100 == 0){
						logger.warn(ctrObj.ctTotalRecords.value + " records processed.");
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

	public void importUSRecords(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, int idxTS_Start,Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		HashMap<String, Object> recMap;
		boolean flgCreate;
		String uid;
		String state;
		String country;
		String fips;
		String city;
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
			city = row.get(JH_US_Source_Column.Admin2.name());
			population = row.get(JH_US_Source_Column.Population.name());
			recMap.put(JH_US_DB_Column.uid.name(), uid);
			recMap.put(JH_US_DB_Column.city.name(), city);
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

					logger.info(msgInfo);

				}catch(SQLException sql) {
					logger.error(sql.getMessage());
					con.rollback(savePt);
					ctrObj.ctBadRecords.increment();
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
