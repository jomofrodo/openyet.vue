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

import org.apache.logging.log4j.Logger;

import com.netazoic.covid.OpenYet.CVD_DataSrc;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.ifDataSrc;
import com.netazoic.ent.if_TP;
import com.netazoic.ent.rdENT;
import com.netazoic.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.util.JsonObjectIterator;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class  CTP_Daily extends rdENT<CTP_Daily> {

	/*
    state - State or territory postal code abbreviation.
    positive - Total cumulative positive test results.
    positiveIncrease - Increase from the day before.
    negative - Total cumulative negative test results.
    negativeIncrease - Increase from the day before.
    pending - Tests that have been submitted to a lab but no results have been reported yet.
    totalTestResults - Calculated value (positive + negative) of total test results.
    totalTestResultsIncrease - Increase from the day before.
    hospitalized - Total cumulative number of people hospitalized.
    hospitalizedIncrease - Increase from the day before.
    death - Total cumulative number of people that have died.
    deathIncrease - Increase from the day before.
    dateChecked - ISO 8601 date of the time we saved visited their website
    total - DEPRECATED Will be removed in the future. (positive + negative + pending). Pending has been an unstable value and should not count in any totals.
	 */

	public String state;
	public String country = "US";
	public Integer positive;
	public Integer positiveIncrease;
	public Integer negative;
	public Integer negativeIncrease;
	public Integer pending;
	public Integer totalTestResults;
	public Integer totalTestResultsIncrease;
	public Integer hospitalized;
	public Integer hospitalizedIncrease;
	public Integer death;
	public Integer deathIncrease;
	public String dateChecked;

	private static String DATA_URL = "https://covidtracking.com/api/states/daily";
	private DataFmt dataFmt = DataFmt.JSON;


	@Override
	public void setType(ifDataType type) {
		// not used

	}

	public enum CTP_TP implements if_TP{

		sql_CREATE_RECORD(null),  //Use a NamedParameterStatement
		sql_CREATE_COMBINED_RECS("/Data/sql/CTP/CreateCombinedRecs.sql"),  //NOT IN USE 
		sql_UPDATE_COMBINED_RECS("/Data/sql/CTP/UpdateCombinedRecs.sql");


		public String tPath;

		CTP_TP(String p){
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
	public CTP_Daily() throws ENTException {
		super();
		this.dataURL = DATA_URL;
		this.srcOrg = SRC_ORG.CTP;
		this.dataSrc = CVD_DataSrc.CTP_STATES_DAILY;
		initENT();

	}



	@Override
	public void setCon(Connection con) {
		this.con = con;
	}


	@Override
	public void initENT() throws ENTException{
		this.nit.ctpClass = CTP_Daily.class;
		nit.ENTITY_NAME = "Covid Tracking Projet Daily State values";
		nit.NIT_TABLE = "covid.ctp_statesdaily";
		nit.FLD_NIT_ID = "state";
		super.initENT();

	}

	protected LocalDate getLastUpdateDate(String srcCode, Connection con) throws SQLException {
		// Get the date of the last update
		LocalDate maxDate = null;
		String q = "SELECT max(to_date(substring(datechecked from 1 for 10),'YYYY-MM-DD')) as maxDate FROM ctp_statesdaily";
		String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
		if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
		else maxDate = LocalDate.parse(maxDateS);
		return maxDate;
	}

	@Override
	public String getDataURL() {
		return dataURL;
	}

	@Override
	public Integer createCombinedRecs(LocalDate lastUpdate) throws Exception {
		HashMap map = new HashMap();
		// We don't actually create new combined recs for CTP Daily data - we just update existing records
		//		String q =  parseUtil.parseQueryFile(CTP_TP.sql_CREATE_COMBINED_RECS.tPath,map);
		String q = parseUtil.parseQuery(CTP_TP.sql_UPDATE_COMBINED_RECS.tPath, map);
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
	public void setInsertStatement(PreparedStatement psInsertRemoteData) throws SQLException {
		// NOT USED

	}

	@Override
	public void setSrc(ifDataSrc src) {
		this.dataSrc = src;

	}

	@Override
	public ifDataSrc getSrc() {
		return this.dataSrc;

	}




	@Override
	public void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException {
		// TODO Auto-generated method stub

	}



	@Override
	public PreparedStatement setupExpireAllStatement(Connection con) throws SQLException {
		String sql = "DELETE FROM covid.ctp_statesdaily";
		PreparedStatement psDeleteRemoteData = con.prepareStatement(sql);
		return psDeleteRemoteData;
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
		LocalDate maxDate = getLastUpdateDate(this.dataSrc.getSrcCode(),con);
		importRecords(rmdObj,maxDate,ctrObj,logger,savePt,con,is);

	}



	@Override
	public void importRecords(ifRemoteDataObj rmdObj, LocalDate maxDate, RemoteDataRecordCtr ctrObj, Logger logger,
			Savepoint savePt, Connection con, InputStream is) throws IOException, Exception, SQLException {

		HashMap<String, Object> recMap;
		boolean flgCreate;
		JsonObjectIterator jsonItr = new JsonObjectIterator(is);
		String msgInfo;
		String dateStr;
		LocalDate date;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		while(jsonItr.hasNext()){
			ctrObj.ctTotalRecords.increment();
			recMap = jsonItr.next();
			dateStr = (String) recMap.get("dateChecked");
			dateStr = dateStr.substring(0,dateStr.indexOf("T"));
			date = getDateFromDateStr(dateStr, formatter);
			if(!date.isAfter(maxDate)) continue;
			try{
				flgCreate = rmdObj.createRemoteDataRecord(recMap,con);
				if(flgCreate) ctrObj.ctNewRecordsCreated.increment();
				msgInfo = "Processed remote record: " + recMap.toString();

				logger.debug(msgInfo);
				if(ctrObj.ctTotalRecords.value%100 == 0){
					logger.info(msgInfo);
					logger.info(ctrObj.ctTotalRecords.value + " records processed.");
				}
				savePt = con.setSavepoint();
			}catch(Exception ex){
				ctrObj.ctBadRecords.increment();;
				logger.error(ex.getMessage());
				con.rollback(savePt);
			}
		}
		jsonItr.close();
	}



	private LocalDate getDateFromDateStr(String dateStr, DateTimeFormatter formatter) {
		String[] dateParts;
		String mo;
		String day;
		String yr;
		LocalDate date;
		try { 
			date = LocalDate.parse(dateStr);
			return date;
		}
		catch(Exception ex) {
			// no bueno
		}
		dateParts = dateStr.split("-");
		mo = dateParts[1];
		day = dateParts[2];
		yr = dateParts[0];
		if(mo.length()<2) mo = "0" + mo;
		if(day.length()<2) day= "0" + day;
		dateStr = mo + "/" + day + "/" + yr;
		date = LocalDate.parse(dateStr, formatter);
		return date;
	}




}
