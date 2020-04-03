package com.netazoic.covid.ent;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.netazoic.ent.ENTException;
import com.netazoic.ent.if_TP;
import com.netazoic.util.NamedParameterStatement;

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




	@Override
	public void setType(ifDataType type) {
		// not used

	}

	public enum CTP_TP implements if_TP{

		sql_CREATE_RECORD(null); //Use a NamedParameterStatement


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
	

	@Override
	public void setUpdateStatement(PreparedStatement psUpdateRemoteData) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCheckRecordStatement(PreparedStatement psSelectRemoteData) throws SQLException {
		// TODO Auto-generated method stub

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
	public void setInsertStatement(NamedParameterStatement nps, Connection con) throws SQLException, ENTException {
		// TODO Auto-generated method stub
		
	}



	@Override
	public PreparedStatement setupExpireAllStatement(Connection con) throws SQLException {
		String sql = "DELETE FROM covid.ctp_statesdaily";
		PreparedStatement psDeleteRemoteData = con.prepareStatement(sql);
		return psDeleteRemoteData;
	}
		

}
