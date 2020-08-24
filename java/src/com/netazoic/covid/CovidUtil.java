package com.netazoic.covid;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.netazoic.covid.OpenYet.CVD_TP;
import com.netazoic.util.ParseUtil;
import com.netazoic.util.RemoteDataObj;
import com.netazoic.util.SQLUtil;

public class CovidUtil {
	
	public static ParseUtil parser = new ParseUtil();

	public static LocalDate getFirstUpdateDate(String srcCode, Connection con) throws SQLException {
		// Get the date of the first update
		LocalDate minDate = null;
		String q = "SELECT min(date) as minDate FROM combined";
		if(srcCode!=null) q += " WHERE sourcecode = '" + srcCode +"'";
		String minDateS = SQLUtil.execSQL(q, "minDate", con);
		if(minDateS==null) minDate =  LocalDate.parse("1970-01-01");
		else minDate = LocalDate.parse(minDateS);
		return minDate;
	}

	public static LocalDate getLastUpdateDate(String srcCode, Connection con) throws SQLException {
		// Get the date of the last update
		LocalDate maxDate = null;
		String q = "SELECT max(date) as maxDate FROM combined";
		if(srcCode!=null) q += " WHERE sourcecode = '" + srcCode +"'";
		String maxDateS = SQLUtil.execSQL(q, "maxDate", con);
		if(maxDateS==null) maxDate =  LocalDate.parse("1970-01-01");
		else maxDate = LocalDate.parse(maxDateS);
		return maxDate;
	}
	
	public static Integer expireCombinedRecords(Connection con) throws SQLException {
		String q = "DELETE FROM covid.combined";
		int ct = SQLUtil.execSQL(q, con);
		return ct;

	}	

	public static Integer createCombinedData(LocalDate lastUpdate,RemoteDataObj rdo, Connection con, Logger logger) throws Exception {
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
	
	public static String parseQuery(String tPath) throws Exception {
		Map<String,Object> map = new HashMap();
		return parser.parseQuery(tPath, map);
	}
	
}
