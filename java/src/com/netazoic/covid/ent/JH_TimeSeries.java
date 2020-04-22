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
import com.netazoic.ent.ENTException;
import com.netazoic.ent.ifDataSrc;
import com.netazoic.ent.ifDataSrcWrapper;
import com.netazoic.ent.if_TP;
import com.netazoic.ent.rdENT;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JH_TimeSeries extends rdENT<ifDataSrcWrapper>{
	protected static final int MAX_BAD_RECORDS = 100;
	// A Johns Hopkins  time series entry
	/*
//	 Two time series tables are for the US confirmed cases and deaths, reported at the county level. 
//	 They are named time_series_covid19_confirmed_US.csv, time_series_covid19_deaths_US.csv, respectively.

//	Three time series tables are for the global confirmed cases, recovered cases and deaths. Australia, Canada and China are reported at 
//	the province/state level. Dependencies of the Netherlands, the UK, France and Denmark are listed under the province/state level.
// 	The US and other countries are at the country level. 
// 	The tables are renamed time_series_covid19_confirmed_global.csv and time_series_covid19_deaths_global.csv, 
// 	and time_series_covid19_recovered_global.csv, respectively.
	 */

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
		state(0), country(1), lat(2), lon(3), date, ct;

		public Integer idx;

		JH_Column(){ }
		JH_Column(int x){
			this.idx = x;
		}
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
	public JH_TimeSeries() throws ENTException {
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
		String q =  parseUtil.parseQuery(JH_TP.sql_CREATE_COMBINED_RECS.tPath,map);
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
	
	public Integer expireCombinedRecs() throws SQLException {
		//Expire all records of this type in the combined records table
		return 0;
	}

	protected LocalDate getLastUpdateDate(String srcCode, Connection con) throws SQLException {
		// Get the date of the last update
		LocalDate maxDate = null;
		String q = "SELECT max(to_date(date,'mm/dd/yy')) as maxDate FROM jh_timeseries WHERE type='" + this.tsType.getCode() +"'";
		String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
		if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
		else maxDate = LocalDate.parse(maxDateS);
		return maxDate;
	}


	@Override
	public ifDataSrc getSrc() {
		return this.dataSrc;

	}

	@Override
	public SRC_ORG getSrcOrg() {
		return this.srcOrg;
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
	public DataFmt getFormat() {
		return this.dataFmt;
	}


	@Override
	public void importRecords(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		// TODO Auto-generated method stub
		
	}


	
	





}
