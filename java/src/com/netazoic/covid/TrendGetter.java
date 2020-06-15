package com.netazoic.covid;

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

import com.netazoic.covid.OpenYet.CVD_TP;
import com.netazoic.ent.ServENT.ENT_Param;
import com.netazoic.util.ParseUtil;
import com.netazoic.util.RSObj;

public class TrendGetter {

	public DataSource dataSource = null;
	public String driverManagerURL, driverManagerUser, driverManagerPwd;
	private String JNIDB_NAME = null;

	private static final Logger logger = LogManager.getLogger(TrendGetter.class);
	public ParseUtil parser = new ParseUtil();
	private Connection con;

	public void init() throws SQLException {
		this.initDataSource();
		this.con = this.getConnection();

	}

	private enum TG_PARAM {
		countrycode, statecode, county
	}

	public void initDataSource() {
		String jndiDB = null;
		try {
			// JNDI data connector
			jndiDB = JNIDB_NAME;
			// if present, should be a string in the form "jdbc/<dbname>"
			// default
			// if(jndiDB == null) jndiDB = "postgres";
			if (jndiDB != null) {
				// Look up the JNDI data source only once at init time
				InitialContext cxt = new InitialContext();
				dataSource = (DataSource) cxt.lookup("java:/comp/env/" + jndiDB);
				if (dataSource == null) {
					throw new ServletException("Data source not found!");
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}

	}

	public Connection getConnection() throws SQLException {
		Connection con = null;
		if (dataSource != null)
			con = dataSource.getConnection();
		else if (driverManagerURL != null) {
			if (driverManagerUser != null && driverManagerPwd != null) {
				con = DriverManager.getConnection(driverManagerURL, driverManagerUser, driverManagerPwd);
			} else {
				con = DriverManager.getConnection(driverManagerURL);
			}
		}
		return con;
	}

	public void GetTrends() {
		String q;
		HashMap<String, Object> settings = new HashMap();
		String tpOpenYet = CVD_TP.sql_GetOpenYetData.tPath;
		String tpCountries = CVD_TP.sql_GetCountries.tPath;
		String tpStates = CVD_TP.sql_GetStates.tPath;
		RSObj rsoOpenYet;
		TrendSet tSet;
		PreparedStatement psWriteTrends = null;
		try {
			q = ParseUtil.parseQuery(tpCountries, settings);
			RSObj rsoCountries = RSObj.getRSObj(q, "countrycode", con);
			// Get trendsets for countries
			for (Map<String, Object> m : rsoCountries.items) {
				String countryCode = (String) m.get("countrycode");
				settings.put(TG_PARAM.countrycode.name(), countryCode);
				q = parser.parseQuery(tpOpenYet, settings);
				rsoOpenYet = RSObj.getRSObj(q, TG_PARAM.countrycode.name(), con);
				try {
					logger.info("Creating status entry for country: " + countryCode);
					tSet = new TrendSet(rsoOpenYet);
					tSet.writeTrendsToDB(psWriteTrends, con);
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

	public static void main(String[] settings) {
		TrendGetter tGetter = new TrendGetter();
		try {
			tGetter.parser.templatePath = "WEB-INF/templates";
			tGetter.parser.appRootPath = "E:/usr/local/webapps/openyet/www/";
			tGetter.driverManagerURL = "jdbc:postgresql://fauci:5432/openyetdb";
			tGetter.driverManagerUser = "openyet";
			tGetter.driverManagerPwd = "odaddado";
			tGetter.init();
			tGetter.GetTrends();
		} catch (Exception ex) {
			logger.error(ex.getMessage());
		}
	}
}
