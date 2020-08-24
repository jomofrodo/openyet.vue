package com.netazoic.covid.task;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netazoic.covid.XMLUtil;
import com.netazoic.covid.OpenYet.CVD_Param;
import com.netazoic.covid.OpenYet.CVD_TP;
import com.netazoic.covid.task.SimpleTask.Task_Param;
import com.netazoic.ent.ServENT.ENT_Param;
import com.netazoic.util.ParseUtil;
import com.netazoic.util.RSObj;

public class TrendGetter extends SimpleTask{




	private String countryCode = null;

	public void init() throws SQLException {
		super.init();

	}

	private enum TG_PARAM {
		countrycode, statecode, county
	}
	
	private static String USA_COUNTRYCODE = "USA";
	
	private void createCountyEntries(String countryCode, String stateCode, Connection con) {
		String tpCounties = CVD_TP.sql_GetCounties.tPath;
		String ctpOpenYet = CVD_TP.sql_GetOpenYetData.tPath;
		HashMap<String,Object> map = new HashMap();
		map.put(CVD_Param.countryCode.name(), countryCode);
		map.put("statecode", stateCode);
		String county;
		String q;
		RSObj rsoState;
		PreparedStatement psWriteTrends = null;
		TrendSet tSet = null;
		try {
			q = parseQuery(tpCounties,map);
			RSObj rsoCounties = RSObj.getRSObj(q, "countrycode", con);
			for (Map<String,Object> m : rsoCounties.items) {

				county = (String) m.get(TG_PARAM.county.name());
				q = parser.parseQuery(ctpOpenYet,m);
				rsoState = RSObj.getRSObj(q, "statecode",con);
				logger.info("Creating status entry for country: " + countryCode + ", state: " + stateCode + ", county: " + county);
				try {
					tSet = new TrendSet(rsoState);
				}catch (Exception ex) {
					logException(ex);
					continue;
				}
				tSet.writeTrendsToDB(psWriteTrends, con);
			}
		} catch (Exception e) {
			logException(e);
		} finally {
			if (psWriteTrends != null)
				try {
					psWriteTrends.close();
					psWriteTrends = null;
				} catch (Exception ex) {
				}
		}
	}


	private void createStateEntries(String countryCode, Connection con) {
		String tpStates = CVD_TP.sql_GetCountryStates.tPath;
		String ctpOpenYet = CVD_TP.sql_GetOpenYetData.tPath;
		HashMap<String,Object> map = new HashMap();
		map.put(CVD_Param.countryCode.name(), countryCode);
		String stateCode;
		String q;
		RSObj rsoState;
		PreparedStatement psWriteTrends = null;
		TrendSet tSet = null;
		try {
			q = parseQuery(tpStates,map);
			RSObj rsoStates = RSObj.getRSObj(q, "countrycode", con);
			for (Map<String,Object> m : rsoStates.items) {
				stateCode = (String) m.get(TG_PARAM.statecode.name());
				q = parser.parseQuery(ctpOpenYet,m);
				rsoState = RSObj.getRSObj(q, "statecode",con);
				logger.info("Creating status entry for country: " + countryCode + ", state: " + stateCode);
				try {
					tSet = new TrendSet(rsoState);
				}catch (Exception ex) {
					logException(ex);
					continue;
				}
				tSet.writeTrendsToDB(psWriteTrends, con);
				if(countryCode.equals(USA_COUNTRYCODE))createCountyEntries(countryCode,stateCode,con);
			}
		} catch (Exception e) {
			logException(e);
		} finally {
			if (psWriteTrends != null)
				try {
					psWriteTrends.close();
					psWriteTrends = null;
				} catch (Exception ex) {
				}
		}
	}

	public void calcDeathVsCCTrend(String countryCode) {
		HashMap<String,String> map = new HashMap<String,String>();
		
	}

	public void GetTrends() {
		String q;
		HashMap<String, Object> map = new HashMap();
		String tpOpenYet = CVD_TP.sql_GetOpenYetData.tPath;
		String tpCountries = CVD_TP.sql_GetCountries.tPath;
		String tpStates = CVD_TP.sql_GetStates.tPath;
		RSObj rsoOpenYet;
		TrendSet tSet;
		PreparedStatement psWriteTrends = null;
		try {
			if(this.countryCode!=null) 	q = "SELECT * FROM country WHERE countrycode = '" + countryCode + "'";
			else q = ParseUtil.parseQuery(tpCountries, map);
			RSObj rsoCountries = RSObj.getRSObj(q, "countrycode", con);
			// Get trendsets for countries
			for (Map<String, Object> m : rsoCountries.items) {
				String countryCode = (String) m.get("countrycode");
				map.put(TG_PARAM.countrycode.name(), countryCode);
				q = parser.parseQuery(tpOpenYet, map);
				rsoOpenYet = RSObj.getRSObj(q, TG_PARAM.countrycode.name(), con);
				try {
					logger.info("Creating status entry for country: " + countryCode);
					tSet = new TrendSet(rsoOpenYet);
					// Clear any existing entries for current date
					tSet.clearExistingEntries(con);
					tSet.writeTrendsToDB(psWriteTrends, con);
					createStateEntries(countryCode,con);
				} catch (Exception ex) {
					logger.error("Error creating TrendSet for country: " + countryCode);
					logger.error(ex.getMessage());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (psWriteTrends != null)
				try {
					psWriteTrends.close();
					psWriteTrends = null;
				} catch (Exception ex) {
				}
		}
	}
	
	@Override
	public void run() {
		GetTrends();
		
		logger.info("All done");

	}
	
	@Override
	public void setParams(HashMap<String, String> args) {
		super.setParams(args);
		if(args.containsKey(CVD_Param.countryCode.name())) {
			this.countryCode = args.get(CVD_Param.countryCode.name());
		}
	}

	public static void main(String[] settings) {
		TrendGetter tGetter = new TrendGetter();
		HashMap<String,String> map = new HashMap<String,String>();
		try {
			map.put(Task_Param.configFile.name(), "www/WEB-INF/conf/sys.local.xml");
			map.put(CVD_Param.countryCode.name(), "USA");
			tGetter.setParams(map);
			tGetter.init();
			tGetter.run();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}


}
