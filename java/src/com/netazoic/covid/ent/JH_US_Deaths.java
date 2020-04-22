package com.netazoic.covid.ent;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.HashMap;

import org.apache.logging.log4j.Logger;

import com.netazoic.covid.Covid19.CVD_DataSrc;
import com.netazoic.covid.ent.JH_TimeSeries.JH_TP;
import com.netazoic.ent.ENTException;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

public class JH_US_Deaths extends JH_US_TimeSeries {
	// UID,iso2,iso3,code3,FIPS,Admin2,Province_State,Country_Region,Lat,Long_,Combined_Key,Population,1/22/20
	
	// Need to define fields locally for setupNPSStatement
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
	
	private int IDX_TS_START = 12;
	
	private static String DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/"
			+ "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_US.csv";

	private enum TP{
		sql_DeleteRemoteRecords("/Data/sql/JH/TimeSeries/US/DeleteRemoteData.sql"), 
		sql_CREATE_COMBINED_RECS("/Data/sql/JH/TimeSeries/US/CreateCombinedRecs.sql");
		
		String tPath;
		TP(String tp){
			this.tPath = tp;
		}
	}

	public JH_US_Deaths() throws ENTException {

		super();
		this.dataURL = DATA_URL;
		this.srcOrg = SRC_ORG.JH_US;
		this.dataSrc = CVD_DataSrc.JH_US_DEATHS;
		this.tsType = JH_TimeSeriesType.dead;
		this.type = this.tsType.getCode();
	}
	
	@Override
	public void initENT() throws ENTException{
		super.initENT();
		this.nit.ctpClass = JH_TP.class;
		nit.ENTITY_NAME = "JH US Deaths Time Series";
		nit.NIT_TABLE = "covid.jh_us_timeseries";
		nit.FLD_NIT_ID = "date";
		this.sql_DeleteRemoteData = TP.sql_DeleteRemoteRecords.tPath;
	}
	
	
	@Override
	public Integer createCombinedRecs() throws Exception {
		HashMap map = new HashMap();
		String q =  parseUtil.parseQuery(TP.sql_CREATE_COMBINED_RECS.tPath,map);
		return SQLUtil.execSQL(q, con);
	}
	
	
	@Override
	public void importRecords(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, Logger logger, Savepoint savePt,
			Connection con, InputStream is) throws IOException, Exception, SQLException {
		
		LocalDate maxDate = getLastUpdateDate(this.dataSrc.getSrcCode(),con);
		super.importRecords(rmdObj,maxDate,ctrObj,IDX_TS_START,logger,savePt,con,is);

	}

}
