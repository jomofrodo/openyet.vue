package com.netazoic.covid.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;

import com.netazoic.covid.CovidUtil;
import com.netazoic.covid.XMLUtil;
import com.netazoic.covid.OpenYet.CVD_DataSrc;
import com.netazoic.covid.OpenYet.CVD_Param;
import com.netazoic.covid.OpenYet.CVD_TP;
import com.netazoic.covid.task.SimpleTask.Task_Param;
import com.netazoic.ent.ServENT.ENT_Param;
import com.netazoic.ent.rdENT;
import com.netazoic.util.RemoteDataObj;
import com.netazoic.util.SQLUtil;

public class GetCombinedData extends SimpleTask {
	
	CovidUtil covidUtil;
	public Boolean flgExpireRecs = false;
	
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

	@Override
	public void run() {
		try {

			int ctCreated = 0, ctTotalCreated = 0;
			String originCode;

			CVD_DataSrc[] dataSrcs = CVD_DataSrc.values();
			LocalDate lastUpdate = CovidUtil.getLastUpdateDate(null,con);
			LocalDate firstUpdate;
			Long ctDateEntries;
			LinkedList<String> alreadySeen = new LinkedList<String>(); 
			// Representative data sources
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF, CVD_DataSrc.JH_US_CONF};
			//			CVD_DataSrc[] dataSrcs = { CVD_DataSrc.JH_GLBL_CONF};
			try {
				for(CVD_DataSrc src : dataSrcs) {
					// debug
					// if(!src.equals(CVD_DataSrc.JH_US_DEATHS)) continue;
					// !debug
					
					rdENT rdEnt = src.getEnt();
					//Only create combined records once per source origin
					if(alreadySeen.contains(src.originCode)) continue;
					else alreadySeen.push(src.originCode);
					
					RemoteDataObj rdo = getRDO(rdEnt, con);
					if (flgExpireRecs) {
						rdEnt.con = con;
						int ctExpired = rdEnt.expireCombinedRecs();
						logger.info("Expired " + ctExpired + " existing combined records");
						// flgExpireExisting = false;
					}

					lastUpdate = CovidUtil.getLastUpdateDate(src.srcCode,con);
					firstUpdate = CovidUtil.getFirstUpdateDate(src.srcCode,con);
					ctDateEntries = firstUpdate.until(lastUpdate, ChronoUnit.DAYS);
					logger.info("Create combined data for : " + src.srcCode);
					ctCreated = CovidUtil.createCombinedData(lastUpdate,rdo, con, logger);
					ctTotalCreated += ctCreated;
					logger.info("Finished with combined data for: " + src.desc);
					logger.info("Created " + ctCreated + " combined records");

				}

				logger.info("Creating increase stats");
				createIncreaseStats(lastUpdate,con);

				ctCreated = getCombinedRecordCount(con);
				logger.info("Created " + ctTotalCreated + " total new combined records");
				logger.info("Task completed");
			}catch(Exception ex) {
				String msg = ex.toString();
				msg += ":" + ex.getMessage();
				logger.error(msg);
			}

			
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}finally {
			if (con != null) 
				try {
					con.close();} catch (SQLException e) {}
		}
		
	}
	
	@Override
	public void setParams(HashMap<String,String> args) {
		String configFile = args.get(Task_Param.configFile.name());
		HashMap<String, String> argsMap = XMLUtil.ParamMapToHashMap(configFile);
		args.putAll(argsMap);
		super.setParams(args);

		String expireAll = (String) settings.get(CVD_Param.expireAll.name());
		if(expireAll != null) flgExpireRecs = Boolean.parseBoolean(expireAll);
		
		
	}
	
	
	public static void main(String[] args) {
		GetCombinedData getter = new GetCombinedData();
		HashMap<String,String> map = new HashMap<String,String>();

		map.put(Task_Param.configFile.name(), "www/WEB-INF/conf/sys.local.xml");
		map.put(CVD_Param.expireAll.name(), "true");

		getter.setParams(map);
		try {
			getter.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		getter.run();
	}

	
	
}
