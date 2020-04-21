package com.netazoic.covid;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.netazoic.covid.ent.CTP_Daily;
import com.netazoic.covid.ent.JH_TimeSeries.JH_Column;
import com.netazoic.covid.ent.JH_TimeSeries.JH_TimeSeriesType;
import com.netazoic.covid.ent.JH_Global_Confirmed;
import com.netazoic.covid.ent.JH_Global_Deaths;
import com.netazoic.covid.ent.JH_Global_Recovered;
import com.netazoic.covid.ent.JH_US_Confirmed;
import com.netazoic.covid.ent.JH_US_Deaths;
import com.netazoic.covid.ent.ifDataType;
import com.netazoic.ent.RouteAction;
import com.netazoic.ent.ServENT;
import com.netazoic.ent.ifDataSrc;
import com.netazoic.ent.ifDataSrcWrapper;
import com.netazoic.ent.ifDataSrcWrapper.MutableInt;
import com.netazoic.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.ent.rdENT;
import com.netazoic.ent.rdENT.DataFmt;
import com.netazoic.ent.rdENT.SRC_ORG;
import com.netazoic.util.HttpUtil;
import com.netazoic.util.JSONUtil;
import com.netazoic.util.JsonObjectIterator;
import com.netazoic.util.RSObj;
import com.netazoic.util.RemoteDataObj;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

@MultipartConfig
public class Covid19 extends ServENT {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	RouteAction homeHdlr = new HomeHdlr();

	private static final Logger logger = LogManager.getLogger(Covid19.class);

	//creating enum HOME TemPLate?
	public  enum CVD_TP{
		Home("/Home/Home.hbs","Main home page"),
		RetrieveData("/Data/RetrieveData.hbs", "Retrieve Data main page"),
		READ_ME("README.md","Todos/Redux Read Me"), 
		sql_GetCombinedData("/Data/sql/GetCombinedData.sql", "Get combined covid19 data"),
		sql_UpdateCombinedCountryCodes("/Data/sql/UpdateCombinedCountryCodes.sql", "Update country codes in combined"),
		sql_CreateCountryRollups("/Data/sql/CreateCountryRollups.sql","Create summary entries for countries that are broken out by state"),
		sql_CreateStateRollups("/Data/sql/CreateStateRollups.sql","Create summary entries for states that are broken out by county"),
		sql_GetCountries("/Data/sql/GetCountryList.sql","Select list of countries with countrycodes"), 
		sql_GetStates("/Data/sql/GetStateList.sql","Select list of state names/codes"),
		sql_UpdateCombinedStateCodes("/Data/sql/UpdateCombinedStateCodes.sql", "Update state codes to ANSI for US entries"), 
		sql_GetRemoteDataStats("/Data/sql/GetRemoteDataStats.sql","Get stats on all remote data tables"),
		;
		//Why store template path and description into variables?
		public String tPath;
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
		dataSrc, expireAll, expireExisting, country, state
	}

	public enum CVD_Route{
		home("/home","Show home page"),
		retrieveData("/cvd/retrieveData", "Retrieve Data"),
		retrieveALLData("/cvd/retrieveALLData","Retrieve all remote data"),
		createCombinedData("/cvd/createCombinedData", "Create combined data recs"),
		getCountryData("/cvd/getData/countries", "Get country table"),
		getStateData("/cvd/getData/states","Get state table"),
		getCombinedData("/cvd/getData/combined", "Get combined covid19 data"),
		remoteDataStats("/cvd/remoteDataStats", "Get stats about remote data already retrieved")
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

	public enum CVD_DataSrc  implements ifDataSrc{
		JH_GLBL_CONF(JH_Global_Confirmed.class, JH_TimeSeriesType.confirmed,DataFmt.CSV,"Johns Hopkins time series new Confirmed"),
		JH_GLBL_DEATHS(JH_Global_Deaths.class, JH_TimeSeriesType.dead,DataFmt.CSV,"Johns Hopkins time series new deaths"),
		JH_GLBL_RECOVER(JH_Global_Recovered.class, JH_TimeSeriesType.recovered,DataFmt.CSV, "Johns Hopkins time series new recoveries"),
		JH_US_CONF(JH_US_Confirmed.class, JH_TimeSeriesType.confirmed,DataFmt.CSV,"Johns Hopkins time series US new Confirmed"),
		JH_US_DEATHS(JH_US_Deaths.class, JH_TimeSeriesType.dead,DataFmt.CSV,"Johns Hopkins time series US new deaths"),
		//		JH_US_RECOVER(JH_US_Recovered.class, JH_TimeSeriesType.recovered,DataFmt.CSV, "Johns Hopkins time series US new recoveries"),
		CTP_STATES_DAILY( CTP_Daily.class, DataFmt.JSON,"Covid Tracking Project - States Daily");

		String srcCode;
		ifDataType type;
		DataFmt dataFmt;
		Class<ifDataSrcWrapper> dswClass;
		String desc;
		String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";
		rdENT rdEnt;
		CVD_DataSrc( Class cl, DataFmt f, String d){
			this.dswClass = cl;
			this.dataFmt = f;
			this.desc = d;
			this.srcCode = this.name();
			try {
				this.rdEnt = (rdENT) cl.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		CVD_DataSrc(Class cl, ifDataType t,DataFmt f, String d) {
			this.srcCode = this.name();
			this.dswClass = cl;
			this.dataFmt = f;
			this.type = t;
			this.desc = d;
			try {
				this.rdEnt = (rdENT) cl.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public String getURL() {
			return this.rdEnt.getDataURL();
		}
		@Override
		public DataFmt getFormat() {
			return this.dataFmt;
		}

	}

	public Covid19(){
		this.flgDebug = true;
	}




	@Override
	public void init(ServletConfig config) throws javax.servlet.ServletException {
		super.init(config);
		GetDataHdlr getData = new GetDataHdlr();
		defaultRoute = CVD_Route.home.route;
		routeMap.put(CVD_Route.home.route, homeHdlr);
		routeMap.put(CVD_Route.retrieveData.route, new RemoteDataHdlr());
		routeMap.put(CVD_Route.retrieveALLData.route, new RetrieveAllDataHdlr());
		routeMap.put(CVD_Route.getCountryData.route, getData);
		routeMap.put(CVD_Route.getStateData.route, getData);
		routeMap.put(CVD_Route.getCombinedData.route, getData);
		routeMap.put(CVD_Route.remoteDataStats.route, new RemoteDataStats());
		routeMap.put(CVD_Route.createCombinedData.route, new CreateCombinedDataHdlr());


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

	private void importJSONData(ifRemoteDataObj rmdObj, RemoteDataRecordCtr ctrObj, Logger logger, Savepoint savePt, Connection con, InputStream is)
			throws SQLException, IOException {
		HashMap<String, Object> recMap;
		boolean flgCreate;
		JsonObjectIterator jsonItr = new JsonObjectIterator(is);
		String msgInfo;

		while(jsonItr.hasNext()){
			ctrObj.ctTotalRecords.increment();
			recMap = jsonItr.next();
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

	public String reportImportStats(RemoteDataRecordCtr ctrObj){

		Integer ctRemoteDataRecs = ctrObj.ctTotalRecords.value;
		Integer ctNewRemoteData = ctrObj.ctNewRecordsCreated.value;
		Integer ctBadRecords = ctrObj.ctBadRecords.value;
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

	public RemoteDataRecordCtr retrieveRemoteData(String country, String state, rdENT rdent, ifRemoteDataObj rmdObj, Connection con) throws IOException, Exception, SQLException{
		boolean flgLocalDebug = false;
		boolean flgAutoCommitAsIFoundIt = con.getAutoCommit();

		Savepoint savePt = null;
		//		Integer ctRemoteDataRecs = 0, ctReturningRemoteData=0, ctUpdatedRemoteData=0, ctNewRemoteData=0, ctBadRecords=0;

		RemoteDataRecordCtr ctrObj = new RemoteDataRecordCtr();

		String fqdn = rdent.getDataURL();
		DataFmt dataFmt = rdent.getFormat();
		try{

			HttpURLConnection http = HttpUtil.getRemoteHTTPConn(fqdn, flgDebug);

			InputStream is = http.getInputStream();

			if(flgLocalDebug){
				//This will kill the input stream for any further processing
				System.out.print(HttpUtil.getResponseString(is));
			}

			// Everything good to this point,

			con.setAutoCommit(false);
			savePt = con.setSavepoint();

			//And now parse the stream

			// CSV data
			SRC_ORG srcOrg = (SRC_ORG) rdent.getSrcOrg();

			switch(srcOrg) {
			case JH_G:
			case JH_US:
				//Johns Hopkins CSV files
				rdent.importRecords(rmdObj,ctrObj,logger, savePt,con,is);
				break;
			case CTP:
				// Covid Tracking Project json files
				importJSONData(rmdObj, ctrObj, logger, savePt, con, is);
			}
			if(dataFmt.equals(DataFmt.CSV)) {

			}
			else if(dataFmt.equals(DataFmt.JSON)) {

			}
		}catch(IOException ex){
			ctrObj.ctTotalRecords.decrement();
			throw ex;
		} catch(Exception ex){
			con.rollback(savePt);
			ctrObj.ctBadRecords.increment();
			throw ex;
		}
		finally{
			if(!con.getAutoCommit())con.commit();
			con.setAutoCommit(flgAutoCommitAsIFoundIt);

			shutDown();
		}
		if(ctrObj.ctTotalRecords.value > 0) reportImportStats(ctrObj);


		return ctrObj;
	}

	private Integer updateCombinedData(RemoteDataObj rdo, Connection con) throws Exception {
		String tp = CVD_TP.sql_UpdateCombinedCountryCodes.tPath;
		String q = parseQuery(tp);
		SQLUtil.execSQL(q, con);

		int ctCreated = rdo.createCombinedData();
		logger.debug("Created " + ctCreated + " combined records");

		// redo the combined countrycodes
		tp = CVD_TP.sql_UpdateCombinedCountryCodes.tPath;
		q = parseQuery(tp);
		SQLUtil.execSQL(q, con);

		//Update State Codes (US only)
		tp = CVD_TP.sql_UpdateCombinedStateCodes.tPath;
		q = parseQuery(tp);
		SQLUtil.execSQL(q, con);

		return ctCreated;
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

	public class GetDataHdlr extends RouteEO{
		// Retrieve and return data from application DB
		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			String routeString = getRoutePrimary(request);
			String q, tp,json;
			RSObj rso;
			Statement stat = null;
			try {

				CVD_Route rte = CVD_Route.getRoute(routeString);
				switch(rte) {
				case getCountryData:
					tp = CVD_TP.sql_GetCountries.tPath;
					q = parser.parseQuery(tp, requestMap);
					rso = RSObj.getRSObj(q, "countrycode", con);
					break;
				case getStateData:
					tp = CVD_TP.sql_GetStates.tPath;
					q = parser.parseQuery(tp, requestMap);
					rso = RSObj.getRSObj(q, "code", con);
					break;
				case getCombinedData:
					tp = CVD_TP.sql_GetCombinedData.tPath;
					q  = parser.parseQuery(tp,requestMap);
					rso = RSObj.getRSObj(q, "countrycode", con);
					int limitIdx = q.lastIndexOf("LIMIT");
					if(limitIdx > 0) q = q.substring(0, limitIdx);
					q = "SELECT COUNT(country) as ct FROM (" + q + ")vc";
					stat = con.createStatement();
					ResultSet rs   = SQLUtil.execSQL(q, stat);
					rs.next();
					int ct = rs.getInt(1);
					rso.numRows = ct;
					break;
				default:
					throw new Exception("Could not determine the requested data");
				}

				//				json = JSONUtil.toJSON(rso.items);
				json = JSONUtil.toJSON(rso);
				ajaxResponse(json,response);
			}catch(Exception ex) {
				throw ex;
			}finally {
				if(stat!=null)try {stat.close(); stat = null;}catch(Exception ex) {}
			}
		}
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

	public class RemoteDataStats extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request, HttpServletResponse response, Connection con,
				HttpSession session) throws IOException, Exception {
			HashMap<String,Object> map = new HashMap<String, Object>();
			String tp = CVD_TP.sql_GetRemoteDataStats.tPath;
			try {
				String q = parser.parseQueryFile(tp, map);
				RSObj rso = RSObj.getRSObj(q, "datasrccode",con);
				String json = getJSON(rso);
				ajaxResponse(json,response);
			}
			catch(Exception ex) {
				ajaxError(ex,response);
			}

		}

	}
	public class RemoteDataHdlr extends RouteEO{
		// Retrieve data from external sources
		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			boolean flgDebug = true;
			HashMap<String,Object> map = new HashMap();
			String dataSrc = (String) request.getAttribute(CVD_Param.dataSrc.name());
			String[] dataSrcA;
			String country = (String) request.getAttribute(CVD_Param.country.name());
			String state = (String) request.getAttribute(CVD_Param.state.name());
			Boolean flgExpireExisting = false, flgExpireRecords = true;
			HashMap<String,Object>retMap = new HashMap<String,Object>();
			RemoteDataRecordCtr ctMap = null;
			String expireExisting = (String) request.getAttribute(CVD_Param.expireExisting.name());
			flgExpireExisting = Boolean.parseBoolean(expireExisting);
			if(dataSrc==null) return;
			dataSrcA = dataSrc.split(":");

			for(String dsrc : dataSrcA) {
				CVD_DataSrc src = CVD_DataSrc.valueOf(dsrc);
				rdENT rdEnt = src.rdEnt;
				switch(src.rdEnt.srcOrg) {
				case JH_G:
				case JH_US:
					rdEnt.setSrc(src);
					rdEnt.setType(src.type);
					break;
				case CTP:
					rdEnt = new CTP_Daily();
					rdEnt.setSrc(src);
					break;

				}
				RemoteDataObj rdo = getRDO(rdEnt, con);
				if(flgExpireExisting) {
					int ctExpired = rdEnt.expireCombinedRecs();
					logger.info("Expired " + ctExpired + " existing combined records");
					//					flgExpireExisting = false;
				}


				con.setAutoCommit(false);
				logger.info("Starting retrieval of remote data");
				ctMap = retrieveRemoteData(country,state,rdEnt, rdo, con);
				logger.info("Created " + ctMap.ctNewRecordsCreated + " new records for " + src.srcCode );
				retMap.put("cts_" + src.srcCode, ctMap);
				retMap.put("src_" + src.srcCode, src.name());
				retMap.put("srccode_" + src.srcCode, src.srcCode);
			}


			con.setAutoCommit(true);
			logger.info("Finished with remote data retrieval");
			String json = JSONUtil.toJSON(retMap);
			ajaxResponse(json, response);

		}

	}

	public class RetrieveAllDataHdlr extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			HashMap<String,Object> map = new HashMap<String,Object>();
			logger.info("Starting retrieval of all data.");
			String country = (String) request.getAttribute(CVD_Param.country.name());
			String state = (String) request.getAttribute(CVD_Param.state.name());

			try {
				for(CVD_DataSrc src : CVD_DataSrc.values()) {
					RemoteDataObj rdo = getRDO(src.rdEnt,con);
					//					rdo.rdEnt.expireCombinedRecs();
					//					rdo.expireAllRemoteDataRecords(map);
					logger.info("Expired remote records");
					RemoteDataRecordCtr retMap = retrieveRemoteData(country,state,src.rdEnt, rdo, con);
					logger.info("Finished with import for: " + src.desc);
				}

				map.put("all", "done");
				String json = JSONUtil.toJSON(map);
				ajaxResponse(json, response);
			}catch(Exception ex) {
				String msg = ex.toString();
				msg += ":" + ex.getMessage();
				ajaxError(msg, ex, response);
				logger.error(msg);
			}
		}	
	}

	public class CreateCombinedDataHdlr extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			HashMap<String,Object> map = new HashMap<String,Object>();
			logger.info("Generating combined data.");
			Boolean flgExpireRecs = false;
			String expireAll = (String) request.getAttribute(CVD_Param.expireAll.name());
			flgExpireRecs = Boolean.parseBoolean(expireAll);

			int ctCreated = 0, ctTotalCreated = 0;
			HashMap<String,String> mapSrcs = new HashMap<String,String>();
			String srcCode;
			String dataSrc = (String) request.getAttribute(CVD_Param.dataSrc.name());
			String[] dataSrcs = dataSrc.split(":");
			// Representative data sources
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF, CVD_DataSrc.JH_US_CONF};
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF};
			try {
				for(String dsrc : dataSrcs) {
					CVD_DataSrc src = CVD_DataSrc.valueOf(dsrc);
					srcCode = mapSrcs.get(src.srcCode);
					if(srcCode==null) mapSrcs.put(src.srcCode, src.srcCode);
					RemoteDataObj rdo = getRDO(src.rdEnt,con);
					if(flgExpireRecs) {
						int ctExpired = src.rdEnt.expireCombinedRecs();
						logger.info("Expired " + ctExpired + " existing combined records");
						//						flgExpireRecords = false;
					}
					logger.info("Create combined data for : " + src.srcCode);
					ctCreated = updateCombinedData(rdo, con);
					ctTotalCreated += ctCreated;
					logger.info("Finished with combined data for: " + src.desc);
					logger.info("Created " + ctCreated + " combined records");

				}

				ctCreated = getCombinedRecordCount(con);
				logger.info("Created " + ctTotalCreated + " total new combined records");
				map.put("all", "done");
				String json = JSONUtil.toJSON(map);
				ajaxResponse(json, response);
			}catch(Exception ex) {
				String msg = ex.toString();
				msg += ":" + ex.getMessage();
				ajaxError(msg, ex, response);
				logger.error(msg);
			}
		}

		private int getCombinedRecordCount(Connection con) throws SQLException {
			String q = "SELECT COUNT(date) as ct FROM covid.combined";
			String ctStr = SQLUtil.execSQL(q, "ct", con);
			Integer ct = Integer.valueOf(ctStr);
			return ct;
		}

		private int expireCombinedRecords(Connection con) throws SQLException {
			String q = "DELETE FROM covid.combined";
			int ct = SQLUtil.execSQL(q, con);
			return ct;

		}	
	}


}
