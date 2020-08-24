package com.netazoic.covid.ent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;

import com.netazoic.covid.LinearRegression;


public class Trend {

		Double[] xVals, yVals, yValsCalc;
		LinearRegression lr;
		Double slope;
		Double intercept;
		Double growthRate;
		Double growthPerc;
		Double growthRateDelta;
		Double growthRateDeltaPerc;

		public Trend(Double[] xVals, Double[] yVals, Trend bl) {
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

		public void clearExistingEntry(String countryCode, String stateCode, String county, String trendCode,
				LocalDate date, Connection con) throws Exception {


			java.sql.Date sqlDate = java.sql.Date.valueOf(date);
			
			String q = "DELETE FROM covid.trend WHERE countrycode = ? AND statecode =? and county = ? and trendCode = ? and date = ?";
			PreparedStatement pStat = null;
			try {
				pStat = con.prepareStatement(q);
				pStat.setString(1, countryCode);
				pStat.setString(2, stateCode);
				pStat.setString(3, county);
				pStat.setString(4, trendCode);
				pStat.setObject(5, sqlDate);
				pStat.execute();
				
			}catch(Exception ex) {
				throw ex;
				
			}finally {
				if(pStat!=null) try { pStat.close();pStat = null;}catch(Exception ex) {}
			}
			
		}

		public void writeTrendToDB(String trendCode, LocalDate date, PreparedStatement ps, String countryCode, String stateCode, String county, Connection con) throws SQLException {
			if (ps == null) {
				ps = preparePSWrite(ps, con);
			}
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			String dateStr = date.format(formatter);

			java.sql.Date sqlDate = java.sql.Date.valueOf(dateStr);
			try {

				ps.setString(1, countryCode);
				ps.setString(2, stateCode);
				ps.setString(3, county);
				ps.setString(4, trendCode);
				ps.setDouble(5, lr.slope());
				ps.setDouble(6, lr.intercept());
				ps.setDouble(7, lr.R2());
				ps.setDouble(8, lr.slopeStdErr());
				ps.setDate(9, sqlDate);

				boolean flgWrite = ps.execute();

			} catch (Exception ex) {
				throw ex;
			}

		}

		static protected PreparedStatement preparePSWrite(PreparedStatement psWriteTrend, Connection con)
				throws SQLException {
			String q = "INSERT INTO covid.trend (countrycode," + "statecode," + "county," + "trendcode,"
					+ "slope," + "intercept," + "r2," + "stderr," + "date)\r\n" + "VALUES( ?,?,?,?,?,?,?,?,?)";

			psWriteTrend = con.prepareStatement(q);
			return psWriteTrend;

		}
}
