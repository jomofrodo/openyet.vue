package com.netazoic.covid;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netazoic.covid.OpenYet.CVD_DataSrc;
import com.netazoic.covid.OpenYet.CVD_Param;
import com.netazoic.covid.OpenYet.CVD_Route;
import com.netazoic.covid.OpenYet.CVD_TP;
import com.netazoic.covid.ent.CTP_Daily;
import com.netazoic.ent.ServENT;
import com.netazoic.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.ent.rdENT;
import com.netazoic.ent.rdENT.DataFmt;
import com.netazoic.ent.rdENT.SRC_ORG;
import com.netazoic.util.HttpUtil;
import com.netazoic.util.JSONUtil;
import com.netazoic.util.RSObj;
import com.netazoic.util.RemoteDataObj;
import com.netazoic.util.SQLUtil;
import com.netazoic.util.ifRemoteDataObj;

@MultipartConfig
public class OpenYetAdmin extends ServENT {

	private static final Logger logger = LogManager.getLogger(OpenYetAdmin.class);
	AdminHdlr adminHdlr;

	@Override
	public void init(ServletConfig config) throws javax.servlet.ServletException {
		super.init(config);

		adminHdlr = new AdminHdlr();
		defaultRoute = OYA_Route.admin.route;
		routeMap.put(OYA_Route.admin.route, adminHdlr);
		routeMap.put(OYA_Route.adminAlias.route, adminHdlr);
		routeMap.put(OYA_Route.retrieveData.route, new RemoteDataHdlr());
		routeMap.put(OYA_Route.retrieveALLData.route, new RetrieveAllDataHdlr());
		routeMap.put(OYA_Route.remoteDataStats.route, new RemoteDataStats());
		routeMap.put(OYA_Route.createCombinedData.route, new CreateCombinedDataHdlr());
	}


	public RemoteDataObj getRDO(rdENT ds, Connection con) throws Exception {
		RemoteDataObj rdo = new RemoteDataObj();

		rdo.rdEnt = ds;
		rdo.con = con;
		rdo.mgr = this;
		rdo.init();
		return rdo;
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
				rdent.importRecords(rmdObj, ctrObj, logger, savePt, con, is);
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

		}
		if(ctrObj.ctTotalRecords.value > 0) reportImportStats(ctrObj);


		return ctrObj;
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
	
	public enum OYA_Route{
		admin("/admin","Admin home page"),
		adminAlias("/cvd/admin","Alias to admin home page"),
		retrieveData("/cvd/retrieveData", "Retrieve Data"),
		retrieveALLData("/cvd/retrieveALLData","Retrieve all remote data"),
		createCombinedData("/cvd/createCombinedData", "Create combined data recs"),
		remoteDataStats("/cvd/remoteDataStats", "Get stats about remote data already retrieved"),
		;

		public String route;
		public String desc;

		OYA_Route(String r, String d){
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

	public class AdminHdlr extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {

			String tPath = CVD_TP.AdminHome.tPath;
			Map<String,Object> map = new HashMap<String,Object>();
			map.put(CVD_Param.flgAdmin.name(), true);
			parseOutput(map, tPath, response);
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
			LocalDate lastUpdate = getLastUpdateDate(null,con);
			// Representative data sources
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF, CVD_DataSrc.JH_US_CONF};
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF};
			try {
				for(String dsrc : dataSrcs) {
					CVD_DataSrc src = CVD_DataSrc.getSrc(dsrc);
					rdENT rdEnt = src.getEnt();
					srcCode = mapSrcs.get(src.srcCode);
					if(srcCode==null) mapSrcs.put(src.srcCode, src.srcCode);
					RemoteDataObj rdo = getRDO(src.rdEnt,con);
					if(flgExpireRecs) {
						int ctExpired = src.rdEnt.expireCombinedRecs();
						logger.info("Expired " + ctExpired + " existing combined records");
						//						flgExpireRecords = false;
					}
					lastUpdate = getLastUpdateDate(src.srcCode,con);
					logger.info("Create combined data for : " + src.srcCode);
					ctCreated = createCombinedData(lastUpdate,rdo, con);
					ctTotalCreated += ctCreated;
					logger.info("Finished with combined data for: " + src.desc);
					logger.info("Created " + ctCreated + " combined records");

				}

				logger.info("Creating increase stats");
				createIncreaseStats(lastUpdate,con);

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

		@SuppressWarnings("rawtypes")
		private Integer createIncreaseStats(LocalDate lastUpdate, Connection con) throws Exception {
			HashMap map = new HashMap();
			map.put("lastUpdate", lastUpdate.toString());
			String tp = CVD_TP.sql_CreateCombinedIncreaseStats.tPath;
			String q = parseQuery(tp,map);
			Integer ct = SQLUtil.execSQL(q, con);
			return ct;
		}

		private int getCombinedRecordCount(Connection con) throws SQLException {
			String q = "SELECT COUNT(date) as ct FROM covid.combined";
			String ctStr = SQLUtil.execSQL(q, "ct", con);
			Integer ct = Integer.valueOf(ctStr);
			return ct;
		}

		protected LocalDate getLastUpdateDate(String srcCode, Connection con) throws SQLException {
			// Get the date of the last update
			LocalDate maxDate = null;
			String q = "SELECT max(date) as maxDate FROM combined";
			if(srcCode!=null) q += " WHERE sourcecode = '" + srcCode +"'";
			String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
			if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
			else maxDate = LocalDate.parse(maxDateS);
			return maxDate;
		}
		private int expireCombinedRecords(Connection con) throws SQLException {
			String q = "DELETE FROM covid.combined";
			int ct = SQLUtil.execSQL(q, con);
			return ct;

		}	

		private Integer createCombinedData(LocalDate lastUpdate,RemoteDataObj rdo, Connection con) throws Exception {
			String tp = CVD_TP.sql_UpdateCombinedCountryCodes.tPath;
			String q = parseQuery(tp);
			SQLUtil.execSQL(q, con);

			int ctCreated = rdo.createCombinedData(lastUpdate);
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
	}


	public class RetrieveAllDataHdlr extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request,
				HttpServletResponse response, Connection con, HttpSession session)
						throws IOException, Exception {
			HashMap<String,Object> map = new HashMap<String,Object>();
			String country = (String) request.getAttribute(CVD_Param.country.name());
			String state = (String) request.getAttribute(CVD_Param.state.name());
			logger.info("Starting retrieval of all data.");
			Boolean flgExpireExisting = false;
			HashMap<String,Object>retMap = new HashMap<String,Object>();
			RemoteDataRecordCtr ctMap = null;
			CVD_DataSrc[] dataSrcA = CVD_DataSrc.values();

			try {
				for(CVD_DataSrc src : dataSrcA) {
					rdENT rdEnt = src.getEnt();
					rdEnt.init(con);
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
						int ctExpired = rdo.expireAllRemoteDataRecords(null);
						logger.info("Expired " + ctExpired + " existing combined records");
						//					flgExpireExisting = false;
					}


					con.setAutoCommit(false);
					logger.info("Starting retrieval of remote data");
					ctMap = retrieveRemoteData(country,state,rdEnt, rdo, con);
					logger.info("Created " + ctMap.ctNewRecordsCreated.value + " new records for " + src.srcCode );
					retMap.put("cts_" + src.srcCode, ctMap);
					retMap.put("src_" + src.srcCode, src.name());
					retMap.put("srccode_" + src.srcCode, src.srcCode);
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
			Boolean flgExpireExisting = false;
			HashMap<String,Object>retMap = new HashMap<String,Object>();
			RemoteDataRecordCtr ctMap = null;
			String expireExisting = (String) request.getAttribute(CVD_Param.expireExisting.name());
			flgExpireExisting = Boolean.parseBoolean(expireExisting);
			if(dataSrc==null) return;
			dataSrcA = dataSrc.split(":");

			for(String dsrc : dataSrcA) {
				CVD_DataSrc src = CVD_DataSrc.getSrc(dsrc);
				rdENT rdEnt = src.getEnt();
				rdEnt.init(con);
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
					int ctExpired = rdo.expireAllRemoteDataRecords(null);
					logger.info("Expired " + ctExpired + " existing combined records");
					//					flgExpireExisting = false;
				}


				con.setAutoCommit(false);
				logger.info("Starting retrieval of remote data");
				ctMap = retrieveRemoteData(country,state,rdEnt, rdo, con);
				logger.info("Created " + ctMap.ctNewRecordsCreated.value + " new records for " + src.srcCode );
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

	public class RemoteDataStats extends RouteEO{

		@Override
		public void routeAction(HttpServletRequest request, HttpServletResponse response, Connection con,
				HttpSession session) throws IOException, Exception {
			HashMap<String,Object> map = new HashMap<String, Object>();
			String tp = CVD_TP.sql_GetRemoteDataStats.tPath;
			try {
				String q = parser.parseQuery(tp, map);
				RSObj rso = RSObj.getRSObj(q, "datasrccode",con);
				String json = getJSON(rso);
				ajaxResponse(json,response);
			}
			catch(Exception ex) {
				ajaxError(ex,response);
			}

		}

	}
}
