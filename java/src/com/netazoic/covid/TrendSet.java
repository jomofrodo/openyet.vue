package com.netazoic.covid;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.netazoic.util.RSObj;

/*
 * Calc trends for all combinations of Country/State/County
 */
public class TrendSet {

	// * TrendSet = OYRec
	RSObj rso;
	Double[] xVals, yValsConf, yValsPPos, yValsDeaths;
	Trend confT_bl, confT_w1, confT_w2;
	Trend pposT_bl, pposT_w1, pposT_w2;
	Trend deathsT_bl, deathsT_w1, deathsT_w2;
	Double confTrend, pposTrend, deathsTrend;
	Double confTrendPerc, pposTrendPerc, deathsTrendPerc;
	String statusCode;
	/*
	 * Replacing client-side code: oyRec(newVal) { const vm = this; const oy =
	 * this.oyRec; // this.convertToNumbers(newVal); // Convert strings to numbers
	 * let oyRec = newVal; // Calculate 3 sets of trends - // baseline: 28 - 15 days
	 * ago // week 1: 14-8 days ago // week 2: 7 days ago through now // For each
	 * set, calculate: // confirmed trend // ppositive trend // deaths trend
	 * 
	 * const recsBL = oy.recs.slice(0, 14); const recsWeek1 = oy.recs.slice(14, 21);
	 * const recsWeek2 = oy.recs.slice(21); oy.confT_bl = lt.calcTrend("confirmed",
	 * recsBL); oy.confT_w1 = lt.calcTrend("confirmed", recsWeek1, oy.confT_bl);
	 * oy.confT_w2 = lt.calcTrend("confirmed", recsWeek2, oy.confT_bl);
	 * 
	 * oy.pposT_bl = lt.calcTrend("ppositive", recsBL); oy.pposT_w1 =
	 * lt.calcTrend("ppositive", recsWeek1, oy.pposT_bl); oy.pposT_w2 =
	 * lt.calcTrend("ppositive", recsWeek2, oy.pposT_bl);
	 * 
	 * oy.deathsT_bl = lt.calcTrend("death", recsBL); oy.deathsT_w1 =
	 * lt.calcTrend("death", recsWeek1, oy.deathsT_bl); oy.deathsT_w2 =
	 * lt.calcTrend("death", recsWeek2, oy.deathsT_bl);
	 * 
	 * let temp = new Number(); temp = (oy.confT_w1.trendDelta +
	 * oy.confT_w2.trendDelta) / 2; oy.confTrend = temp.toFixed(0); temp =
	 * (oy.confT_w1.trendPerc + oy.confT_w2.trendPerc) / 2; oy.confTrendPerc =
	 * temp.toFixed(2); temp = (oy.pposT_w1.yAvgDelta + oy.pposT_w2.yAvgDelta) / 2;
	 * oy.pposTrend = temp.toFixed(2); temp = (oy.pposT_w1.yAvgDPerc +
	 * oy.pposT_w2.yAvgDPerc) / 2; oy.pposTrendPerc = temp.toFixed(2); temp =
	 * (oy.deathsT_w1.trendDelta + oy.deathsT_w2.trendDelta) / 2; oy.deathsTrend =
	 * temp.toFixed(0); temp = (oy.deathsT_w1.trendPerc + oy.deathsT_w2.trendPerc) /
	 * 2; oy.deathsTrendPerc = temp.toFixed(2);
	 * 
	 * if (vm.flgDebug) console.log(oyRec); // main stats from last record in
	 * sequence const lastRec = oyRec.recs[oyRec.recs.length - 1];
	 * Object.keys(lastRec).forEach(key => { oyRec[key] = lastRec[key]; }); }
	 */

	private enum KEYS {
		confirmed, ppositive, death
	}

	private enum FIELD {
		countrycode, statecode, county, date
	}

	TrendSet(RSObj rsoOpenYet) throws Exception {

		this.rso = rsoOpenYet;

		HashMap[] recs = (HashMap[]) rsoOpenYet.items;
		String key = KEYS.confirmed.name();
		this.yValsConf = new Double[recs.length];
		this.xVals = new Double[recs.length];
		Double[] yVals = this.yValsConf;
		extractYVals(recs, key, yVals);
		this.yValsPPos = new Double[recs.length];
		yVals = this.yValsPPos;
		key = KEYS.ppositive.name();
		extractYVals(recs, key, yVals);
		this.yValsDeaths = new Double[recs.length];
		yVals = this.yValsDeaths;
		key = KEYS.death.name();
		extractYVals(recs, key, yVals);

		this.calcTrends(rsoOpenYet);

	}

	private int extractYVals(HashMap[] recs, String key, Double[] yVals) {
		String strVal;
		Double val;
		int idx = 0;
		for (HashMap m : recs) {
			strVal = (String) m.get(key);
			if (strVal == null)
				strVal = "0";
			val = Double.valueOf(strVal);
			yVals[idx] = val;
			xVals[idx] = Double.valueOf(idx);
			idx++;
		}
		return idx;
	}

	private class Trend {
		Double[] xVals, yVals, yValsCalc;
		LinearRegression lr;
		Double slope;
		Double intercept;
		Double growthRate;
		Double growthPerc;
		Double growthRateDelta;
		Double growthRateDeltaPerc;

		Trend(Double[] xVals, Double[] yVals, Trend bl) {
			this.xVals = xVals;
			this.yVals = yVals;
			this.lr = new LinearRegression(xVals, yVals);
			this.slope = lr.slope();
			this.intercept = lr.intercept();
			this.yValsCalc = getPredictedVals(xVals, this.lr);

			int ctY = this.yValsCalc.length;

			this.growthRate = calcGrowthRate();
			this.growthPerc = calcPercentChange(yValsCalc[1], yValsCalc[0]);
			if (bl != null) {
				this.growthRateDelta = this.growthRate - bl.growthRate;
				this.growthRateDeltaPerc = calcPercentChange(this.growthRate, bl.growthRate);
			}

		}

		private Double[] getPredictedVals(Double[] xVals, LinearRegression lr) {
			Double[] predicted = new Double[xVals.length];
			int idx = 0;
			for (Double x : xVals) {
				predicted[idx] = lr.slope() * x + lr.intercept();
				idx++;
			}
			return predicted;
		}

		private Double calcGrowthRate() {
			Double val0 = this.yValsCalc[0];
			Double val1 = this.yValsCalc[1];
			Double rateD = (val1 - val0) / (this.xVals[1] - this.xVals[0]);
			return round(rateD);
		}

		private Double calcPercentChange(Double valA, Double valB) {
			Double dPerc;
			if (valB == 0)
				return null;
			dPerc = ((valA - valB) / valB) * 100;
			dPerc = round(dPerc * 100, 2) / 100;
			return dPerc;
		}

	}

	protected double round(double value) {
		return round(value, 0);
	}

	protected double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	private void calcTrends(RSObj rsoOpenYet) throws Exception {

		HashMap<String, Object>[] recsBL, recsWeek1, recsWeek2;

		if (rsoOpenYet.items.length != 28) {
			throw new Exception("Not enough records in rsoOpenYet");
		}
		recsBL = (HashMap<String, Object>[]) Arrays.copyOfRange(rsoOpenYet.items, 0, 14);
		recsWeek1 = (HashMap<String, Object>[]) Arrays.copyOfRange(rsoOpenYet.items, 14, 21);
		recsWeek2 = (HashMap<String, Object>[]) Arrays.copyOfRange(rsoOpenYet.items, 21, 28);

		confT_bl = calcTrend("confirmed", recsBL, null);
		confT_w1 = calcTrend("confirmed", recsWeek1, confT_bl);
		confT_w2 = calcTrend("confirmed", recsWeek2, confT_bl);

		pposT_bl = calcTrend("ppositive", recsBL, null);
		pposT_w1 = calcTrend("ppositive", recsWeek1, pposT_bl);
		pposT_w2 = calcTrend("ppositive", recsWeek2, pposT_bl);

		deathsT_bl = calcTrend("death", recsBL, null);
		deathsT_w1 = calcTrend("death", recsWeek1, deathsT_bl);
		deathsT_w2 = calcTrend("death", recsWeek2, deathsT_bl);

		Double temp;
		temp = (confT_w1.growthRate + confT_w2.growthRate) / 2;
		confTrend = round(temp, 0);
		temp = (confT_w1.growthPerc + confT_w2.growthPerc) / 2;
		confTrendPerc = round(temp, 2);
		if (pposT_w1 != null) {
			temp = (pposT_w1.growthRate + pposT_w2.growthRate) / 2;
			pposTrend = round(temp, 0);
			temp = (pposT_w1.growthPerc + pposT_w2.growthPerc) / 2;
			pposTrendPerc = round(temp, 2);
		}
		temp = (deathsT_w1.growthRate + deathsT_w2.growthRate) / 2;
		deathsTrend = round(temp, 0);
		temp = (deathsT_w1.growthPerc + deathsT_w2.growthPerc) / 2;
		deathsTrendPerc = round(temp, 2);

	}

	private Trend calcTrend(String key, HashMap<String, Object>[] recs, Trend trBaseLine) {
		// Get the LR slope of recs[key]
		Double[] yVals, xVals;
		Double val;
		Double sY = 0D;
		Trend tr;
		String strVal;
		LinearRegression lr;
		yVals = new Double[recs.length];
		xVals = new Double[recs.length];
		int idx = 0;
		for (HashMap m : recs) {
			strVal = (String) m.get(key);
			val = strVal == null ? 0D : Double.valueOf(strVal);
			yVals[idx] = val;
			sY += val;
			xVals[idx] = Double.valueOf(idx + 1);
			idx++;
		}
		if (sY == 0)
			return null;
		tr = new Trend(xVals, yVals, trBaseLine);
		return tr;
	}

	public void writeTrendsToDB(PreparedStatement ps, Connection con) throws SQLException {
		if (ps == null) {
			ps = preparePSWrite(ps, con);
		}
		;
		Map<String, Object> lastRec = this.rso.items[rso.items.length - 1];
		String countryCode, stateCode, county, dateStr;
		countryCode = (String) lastRec.get(FIELD.countrycode.name());
		stateCode = (String) lastRec.get(FIELD.statecode.name());
		county = (String) lastRec.get(FIELD.county.name());
		dateStr = (String) lastRec.get(FIELD.date.name());
		Date date = new Date(dateStr);
		LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		dateStr = lDate.format(formatter);

		java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);
		try {

			ps.setString(1, countryCode);
			ps.setString(2, stateCode);
			ps.setString(3, county);
			ps.setString(4, this.statusCode);
			ps.setDouble(5, confTrend);
			ps.setDouble(6, confTrendPerc);
			if (pposTrend != null) {
				ps.setDouble(7, pposTrend);
				ps.setDouble(8, pposTrendPerc);
			} else {
				ps.setNull(7, java.sql.Types.DOUBLE);
				ps.setNull(8, java.sql.Types.DOUBLE);
			}

			ps.setDouble(9, deathsTrend);
			ps.setDouble(10, deathsTrendPerc);
			ps.setDate(11, sqlDate);

			boolean flgWrite = ps.execute();

		} catch (Exception ex) {
			throw ex;
		}

	}

	static protected PreparedStatement preparePSWrite(PreparedStatement psWriteTrend, Connection con)
			throws SQLException {
		String q = "INSERT INTO covid.open_status (countrycode," + "statecode," + "county," + "statuscode,"
				+ "conftrend," + "conftrendperc," + "ppostrend," + "ppostrendperc," + "deathtrend," + "deathtrendperc,"
				+ "date)\r\n" + "VALUES( ?,?,?,?,?,?,?,?,?,?,?)";

		psWriteTrend = con.prepareStatement(q);
		return psWriteTrend;

	}

}
