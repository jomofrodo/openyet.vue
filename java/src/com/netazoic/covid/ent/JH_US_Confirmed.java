package com.netazoic.covid.ent;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.netazoic.covid.Covid19.CVD_TP;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.ifDataSrcWrapper;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JH_US_Confirmed extends JH_US_TimeSeries implements ifDataSrcWrapper{

	private int IDX_TS_START = 11;
	
	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/"
			+ "COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_US.csv";
	
	private String desc = "Johns Hopkins time series new Confirmed";
	/*
	 * UID,iso2,iso3,code3,FIPS,Admin2,Province_State,Country_Region,Lat,Long_,Combined_Key, TS ...
	 */
	
	private enum TP{
		sql_DeleteRemoteRecords("/Data/sql/JH/TimeSeries/US/DeleteRemoteData.sql"),
		sql_CREATE_COMBINED_RECS("/Data/sql/JH/TimeSeries/US/CreateCombinedRecs.sql");
		
		String tPath;
		TP(String tp){
			this.tPath = tp;
		}
	}

	@Override
	public void initENT() throws ENTException{
		super.initENT();
		this.nit.ctpClass = JH_TP.class;
		nit.ENTITY_NAME = "JH US Confirmed Time Series";
		nit.NIT_TABLE = "covid.jh_us_timeseries";
		nit.FLD_NIT_ID = "date";
		this.sql_DeleteRemoteData = TP.sql_DeleteRemoteRecords.tPath;
	}
	

	public JH_US_Confirmed() throws ENTException {

		super();
		this.dataURL = DATA_URL;
		this.srcOrg = SRC_ORG.JH_US;
		this.tsType = JH_TimeSeriesType.confirmed;
		this.type = this.tsType.getCode();
	}
	
	@Override
	public Integer createCombinedRecs() throws Exception {
		HashMap map = new HashMap();
		String q =  parseUtil.parseQueryFile(TP.sql_CREATE_COMBINED_RECS.tPath,map);
		int ctCreated =  SQLUtil.execSQL(q, con);
		
		String tp = CVD_TP.sql_CreateCountryRollups.tPath;
		q = parseUtil.parseQuery(tp,map);
		ctCreated += SQLUtil.execSQL(q, con);
		
		tp = CVD_TP.sql_CreateStateRollups.tPath;
		q = parseUtil.parseQuery(tp,map);
		ctCreated += SQLUtil.execSQL(q, con);
		
		return ctCreated;
	}
	
	public Integer expireCombinedRecs() throws SQLException {
		//Expire all records of this type in the combined records table
//		String q = "DELETE FROM covid.combined WHERE sourcecode = '" + this.srcOrg.code + "'";
//		return SQLUtil.execSQL(q, con);
		return 0;
	}
	@Override
	public void importRecords(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		LocalDate maxDate = getLastUpdateDate(this.srcOrg.code,con);
		super.importRecords(rmdObj,maxDate,ctrObj,IDX_TS_START,logger,savePt,con,is);

	}

}