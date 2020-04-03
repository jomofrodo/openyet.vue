package com.netazoic.covid;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.netazoic.covid.ent.CTP_Daily;
import com.netazoic.covid.ent.JHTimeSeries;
import com.netazoic.covid.ent.JHTimeSeries.JH_Column;
import com.netazoic.covid.ent.JHTimeSeries.JH_TimeSeriesType;
import com.netazoic.covid.ent.RemoteDataObj;
import com.netazoic.covid.ent.ifDataSrc;
import com.netazoic.covid.ent.ifDataSrcWrapper;
import com.netazoic.covid.ent.ifDataType;
import com.netazoic.covid.ent.ifRemoteDataObj;
import com.netazoic.covid.ent.rdENT;
import com.netazoic.covid.ent.rdENT.DataFmt;
import com.netazoic.ent.RouteAction;
import com.netazoic.ent.ServENT;
import com.netazoic.ent.if_SRC_ORG;
import com.netazoic.util.HttpUtil;
import com.netazoic.util.JSONUtil;
import com.netazoic.util.JsonObjectIterator;
import com.netazoic.util.RSObj;

public class Covid19 extends ServENT {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	RouteAction homeHdlr = new HomeHdlr();

	private static Logger logger = LoggerFactory.getLogger(Covid19.class);

	//creating enum HOME TemPLate?
	public  enum CVD_TP{
		Home("/Home/Home.hbs","Main home page"),
		RetrieveData("/Data/RetrieveData.hbs", "Retrieve Data main page"),
		READ_ME("README.md","Todos/Redux Read Me"), 
		sql_GetCombinedData("/Data/sql/GetCombinedData.sql", "Get combined covid19 data"),
		;
		//Why store template path and description into variables?
		String tPath;
		String desc;
		CVD_TP(String t, String d){
			tPath = t;
			desc = d;
		}
	}


	public enum CVD_DataCt{
		ctRemoteDataRecs, ctNewRemoteData, ctBadRecords
	}

	public enum CVD_Param{
		dataSrc
	}

	public enum CVD_Route{
		home("/home","Show home page"),
		retrieveData("/cvd/retrieveData", "Retrieve Data"),
		getCountryData("/cvd/getData/countries", "Get country table"),
		getCombinedData("/cvd/getData/combined", "Get combined covid19 data")
		;

		public String route;
		public String desc;

		CVD_Route(String r, String d){
			route = r;
			desc = d;
		}
		public static CVD_Route getRoute(String rs) {
			for(CVD_Route r : CVD_Route.values()) {
				if(r.route.equals(rs)) return r;
			}
			return null;
		}

	}

	public enum SRC_ORG implements if_SRC_ORG{
		JH("Johns Hopkins"),
		CTP("Covid Tracking Project");

		String code;
		String srcName;

		SRC_ORG(String n){
			this.code=this.name();
			this.srcName = n;
		}

		@Override
		public String getCode() {
			return this.code;

		}
	}

	public enum CVD_DataSrc  implements ifDataSrc{
		JH_CONF(SRC_ORG.JH,JH_TimeSeriesType.confirmed,DataFmt.CSV,"Johns Hopkins time series new Confirmed","csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv"),
		JH_DEAD(SRC_ORG.JH, JH_TimeSeriesType.dead,DataFmt.CSV,"Johns Hopkins time series new deaths","csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv"),
		JH_RECOVER(SRC_ORG.JH, JH_TimeSeriesType.recovered,DataFmt.CSV, "Johns Hopkins time series new recoveries", "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv"),
		CTP_STATES_DAILY(SRC_ORG.CTP, DataFmt.JSON,"Covid Tracking Project - States Daily","https://covidtracking.com/api/states/daily");

		String srcCode;
		ifDataType type;
		SRC_ORG srcOrg;
		DataFmt dataFmt;
		String desc;
		String url;
		String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";
		CVD_DataSrc(SRC_ORG o, DataFmt f, String d, String u){
			this.srcOrg = o;
			this.dataFmt = f;
			this.desc = d;
			this.srcCode = this.name();
			this.url = u;
		}
		CVD_DataSrc(SRC_ORG o,  ifDataType t,DataFmt f, String d, String u) {
			this.srcCode = this.name();
			this.dataFmt = f;
			this.srcOrg = o;
			this.type = t;
			this.desc = d;
			this.url = this.urlBase + u;
		}

		public String getURL() {
			return this.url;
		}
		@Override
		public DataFmt getFormat() {
			return this.dataFmt;
		}
		@Override
		public if_SRC_ORG getSrcOrganization() {
			return this.srcOrg;
		}

	}

	class MutableInt {
		int value = 0; // 
		public void increment () { ++value;      }
		public void decrement () { --value; }
		public int  get ()       { return value; }
	}


	@Override
	public void init(ServletConfig config) throws javax.servlet.ServletException {
		super.init(config);
		GetDataHdlr getData = new GetDataHdlr();
		defaultRoute = CVD_Route.home.route;
		routeMap.put(CVD_Route.home.route, homeHdlr);
		routeMap.put(CVD_Route.retrieveData.route, new RemoteDataHdlr());
		routeMap.put(CVD_Route.getCountryData.route, getData);
		routeMap.put(CVD_Route.getCombinedData.route, getData);


	}

	public RemoteDataObj getRDO(rdENT ds, Connection con) throws Exception {
		RemoteDataObj rdo = new RemoteDataObj();

		rdo.rdEnt = ds;
		rdo.con = con;
		rdo.mgr = this;
		rdo.init();
		return rdo;
	}

	private Integer importJHData( ifRemoteDataObj rmdObj, MutableInt ctNewRemoteData, MutableInt ctBadRecords,  MutableInt ctRemoteDataRecs,
			Savepoint savePt, Connection con,
			InputStream is) throws IOException, Exception, SQLException {
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
					ctRemoteDataRecs.increment();
					flgCreate = rmdObj.createRemoteDataRecord(recMap,con);
					if(flgCreate) ctNewRemoteData.increment();;
					msgInfo = "Processed remote record: " + recMap.toString();

					logger.info(msgInfo);
					if(ctRemoteDataRecs.value%100 == 0){
						logger.warn(ctRemoteDataRecs.value + " records processed.");
					}
				}catch(SQLException sql) {
					logger.error(sql.getMessage());
					con.rollback(savePt);
					ctBadRecords.increment();
				}
			}
			// Commit after processing each row
			// Otherwise the number of locks on the db grows beyond max capacity
			con.commit();
		}
		return ctNewRemoteData.value;
	}

	private void importJSONData(ifRemoteDataObj rmdObj, MutableInt ctRemoteDataRecs, MutableInt ctNewRemoteData,
			MutableInt ctBadRecords, Savepoint savePt, Connection con, InputStream is)
					throws SQLException, IOException {
		HashMap<String, Object> recMap;
		boolean flgCreate;
		JsonObjectIterator jsonItr = new JsonObjectIterator(is);
		String msgInfo;

		while(jsonItr.hasNext()){
			ctRemoteDataRecs.increment();
			recMap = jsonItr.next();
			try{
				flgCreate = rmdObj.createRemoteDataRecord(recMap,con);
				if(flgCreate) ctNewRemoteData.increment();
				msgInfo = "Processed remote record: " + recMap.toString();

				logger.info(msgInfo);
				if(ctRemoteDataRecs.value%100 == 0){
					logger.info(ctRemoteDataRecs + " records processed.");
				}
				savePt = con.setSavepoint();
			}catch(Exception ex){
				ctBadRecords.increment();;
				logger.error(ex.getMessage());
				con.rollback(savePt);
			}
		}
		jsonItr.close();
	}

	public String reportImportStats(Map<CVD_DataCt,MutableInt> retMap){

		Integer ctRemoteDataRecs = retMap.get(CVD_DataCt.ctRemoteDataRecs).value;
		Integer ctNewRemoteData = retMap.get(CVD_DataCt.ctNewRemoteData).value;
		Integer ctBadRecords = retMap.get(CVD_DataCt.ctBadRecords).value;
		String msg ="Finished importing module records.\r\n";
		msg += "Processed " + ctRemoteDataRecs + " records.\r\n";
		//		       if(ctDuplicate > 0) msg += "Found " + ctDuplicate + " duplicate entries.\r\n";
		if(ctBadRecords > 0) msg += "Found " + ctBadRecords + " invalid records in the input.\r\n";
		if(ctRemoteDataRecs > 0) msg += "A total of " + ctRemoteDataRecs + " module records retrieved from remote source.\r\n";
		if(ctNewRemoteData > 0) msg += "Created " + ctNewRemoteData + " new module records\r\n";
		//        if(ctReturningRemoteData > 0) msg += "Found " + ctReturningRemoteData + " existing records \r\n";
		//        if(ctUpdatedRemoteData > 0) msg += "Updated " + ctUpdatedRemoteData + " existing records \r\n";
		//
		//        if(flgVerbose) System.out.println(msg);
		logger.info(msg);
		return msg;
	}

	public Map<CVD_DataCt,MutableInt> retrieveRemoteData(ifDataSrcWrapper dsw, ifRemoteDataObj rmdObj, Connection con) throws IOException, Exception, SQLException{

		boolean flgAutoCommitAsIFoundIt = con.getAutoCommit();
		Map<CVD_DataCt,MutableInt> retMap = new HashMap<CVD_DataCt,MutableInt>();
		Savepoint savePt;
		//		Integer ctRemoteDataRecs = 0, ctReturningRemoteData=0, ctUpdatedRemoteData=0, ctNewRemoteData=0, ctBadRecords=0;
		retMap.put(CVD_DataCt.ctRemoteDataRecs, new MutableInt());
		retMap.put(CVD_DataCt.ctNewRemoteData, new MutableInt());
		retMap.put(CVD_DataCt.ctBadRecords, new MutableInt());
		MutableInt ctNewRemoteData = retMap.get(CVD_DataCt.ctNewRemoteData);
		MutableInt ctBadRecords = retMap.get(CVD_DataCt.ctBadRecords);
		MutableInt ctRemoteDataRecs = retMap.get(CVD_DataCt.ctRemoteDataRecs);
		HashMap<String,Object> recMap;

		ifDataSrc dataSrc = dsw.getSrc();
		String fqdn = dataSrc.getURL();
		DataFmt dataFmt = dataSrc.getFormat();
		try{

			HttpURLConnection http = HttpUtil.getRemoteHTTPConn(fqdn, flgDebug);

			InputStream is = http.getInputStream();

			if(flgDebug){
				//This will kill the input stream for any further processing
				System.out.print(HttpUtil.getResponseString(is));
			}

			// Everything good to this point,

			con.setAutoCommit(false);
			savePt = con.setSavepoint();

			//And now parse the stream

			// CSV data
			SRC_ORG srcOrg = (SRC_ORG) dataSrc.getSrcOrganization();

			switch(srcOrg) {
			case JH:
				//Johns Hopkins CSV files
				importJHData(rmdObj, ctNewRemoteData, ctBadRecords, ctRemoteDataRecs, savePt, con, is);		
				break;
			case CTP:
				// Covid Tracking Project json files
				importJSONData(rmdObj, ctRemoteDataRecs, ctNewRemoteData, ctBadRecords, savePt, con, is);
			}
			if(dataFmt.equals(DataFmt.CSV)) {

			}
			else if(dataFmt.equals(DataFmt.JSON)) {

			}
		}catch(IOException ex){
			ctRemoteDataRecs.decrement();
			throw ex;
		} catch(Exception ex){
			ctRemoteDataRecs.decrement();
			throw ex;
		}
		finally{
			if(!con.getAutoCommit())con.commit();
			con.setAutoCommit(flgAutoCommitAsIFoundIt);

			shutDown();
		}
		retMap.put(CVD_DataCt.ctRemoteDataRecs, ctRemoteDataRecs);
		retMap.put(CVD_DataCt.ctNewRemoteData, ctNewRemoteData);
		retMap.put(CVD_DataCt.ctBadRecords, ctBadRecords);
		if(ctRemoteDataRecs.value > 0) reportImportStats(retMap);
		return retMap;
	}

	private void shutDown(){
		/*
        if(psSelectRemoteDataID!=null) try{psSelectRemoteDataID.close();psSelectRemoteDataID=null;}catch(Exception ex){}
        if(psInsertRemoteData!=null) try{psInsertRemoteData.close();psInsertRemoteData=null;}catch(Exception ex){}
        if(psSelectRemoteData!=null) try{psSelectRemoteData.close();psSelectRemoteData=null;}catch(Exception ex){}
        if(deleteRemoteData!=null) try{deleteRemoteData.close();deleteRemoteData=null;}catch(Exception ex){}
        if(updateRemoteData!=null) try{updateRemoteData.close();updateRemoteData=null;}catch(Exception ex){}
		 */
	}

	public class HomeHdlr extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			String tPath = CVD_TP.Home.tPath;
			Map<String,Object> map = new HashMap<String,Object>();
			parseOutput(map, tPath, response);
		}	
	}

	public class RemoteDataHdlr extends RouteEO{
		// Retrieve data from external sources
		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {

			String dataSrc = (String) request.getAttribute(CVD_Param.dataSrc.name());
			HashMap<String,Object> map = new HashMap();
			if(dataSrc!=null) {
				CVD_DataSrc src = CVD_DataSrc.valueOf(dataSrc);
				rdENT dsw = null;
				switch(src.srcOrg) {
				case JH:

					dsw = new JHTimeSeries();
					dsw.setSrc(src);
					dsw.setType(src.type);
					break;
				case CTP:
					dsw = new CTP_Daily();
					dsw.setSrc(src);
					break;

				}
				RemoteDataObj rdo = getRDO(dsw, con);
				rdo.expireAllRemoteDataRecords(map);

				Map<CVD_DataCt,MutableInt> retMap = retrieveRemoteData(dsw, rdo, con);
			}
			String tp = CVD_TP.RetrieveData.tPath;

			parseOutput(map, tp, response);
		}

	}

	public class GetDataHdlr extends RouteEO{
		// Retrieve and return data from application DB
		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			String routeString = getRoutePrimary(request);
			String q, tp,json;
			RSObj rso;
			CVD_Route rte = CVD_Route.getRoute(routeString);
			switch(rte) {
			case getCountryData:
				q = "SELECT countrycode, name FROM country ORDER BY name";

				break;
			case getCombinedData:
				tp = CVD_TP.sql_GetCombinedData.tPath;
				q  = parseQuery(tp);
				break;
			default:
				throw new Exception("Could not determine the requested data");
			}

			rso = RSObj.getRSObj(q, "countrycode", con);
			json = JSONUtil.toJSON(rso.items);
			ajaxResponse(json,response);
		}
	}




}
