package com.netazoic.covid.ent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

import com.netazoic.ent.ENTException;
import com.netazoic.ent.if_TP;
import com.netazoic.util.NamedParameterStatement;

public class JHTimeSeries extends rdENT<ifDataSrcWrapper>{
	// A Johns Hopkins  time series entry
	
	public String state;
	public String country;
//	private Double lat;
//	private Double lon;
	public String date;
	public Integer ct;
	public String type;
	
	private ifDataSrc dataSrc;
	private ifDataType tsType;
	
	public enum JH_TimeSeriesType implements ifDataType{
		confirmed("C"), dead("D"), recovered("R");
		
		String code;
		JH_TimeSeriesType(String c){
			this.code = c;
		}
		@Override
		public String getCode() {
			return code;
		}
	}
	public enum JH_Column{
		state(0), country(1), lat(2), lon(3), date, ct;

		public Integer idx;

		JH_Column(){ }
		JH_Column(int x){
			this.idx = x;
		}
	}
	
	public enum JH_TP implements if_TP{

        sql_CREATE_RECORDx("/Data/sql/JH/TimeSeries/psCreateRecord.sql");

		
		public String tPath;
		
		JH_TP(String p){
			this.tPath = p;
		}

		@Override
		public String getTP() {
			return tPath;
		}

		@Override
		public void setTP(String p) {
			this.tPath = p;
			
		}
		
	}
	public JHTimeSeries() throws ENTException {
		super();
		initENT();
	}

	@Override
	public void init(HashMap<String,Object> recMap) throws ENTException {
		super.init(recMap);
		this.type = this.tsType.getCode();
	}
//		state = (String) recMap.get(JH_Column.state.name());
//		country = (String) recMap.get(JH_Column.country.name());
//		date = (LocalDate) recMap.get(JH_Column.date.name());
//		ct = (Integer) recMap.get(JH_Column.ct.name());
//	}
	
	@Override
	public void initENT() throws ENTException{
		this.nit.ctpClass = JH_TP.class;
		nit.ENTITY_NAME = "JH Time Series";
		nit.NIT_TABLE = "covid.jh_timeseries";
		nit.FLD_NIT_ID = "date";
		super.initENT();
		
	}
	
	@Override
	public void setCon(Connection con) {
		this.con = con;
	}

	@Override
	public void setInsertStatement(PreparedStatement ps) throws SQLException {
		ps.setString(1, this.state);
		ps.setString(2, this.country);
		Date sqlDate = java.sql.Date.valueOf(date);
		ps.setDate(3, sqlDate);
		ps.setInt(4, this.ct);
		ps.setString(5, this.tsType.getCode());
		
	}

	@Override
	public void setUpdateStatement(PreparedStatement psUpdateRemoteData) throws SQLException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public PreparedStatement setupExpireAllStatement(Connection con) throws SQLException {
		String sql = "DELETE FROM covid.jh_timeseries WHERE type = ?";
		PreparedStatement psDeleteRemoteData = con.prepareStatement(sql);
		return psDeleteRemoteData;
	}

	@Override
	public void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException {

		psDeleteRemoteData.setString(1, this.tsType.getCode());
	}

	@Override
	public void setCheckRecordStatement(PreparedStatement psSelectRemoteData) throws SQLException {

		
	}


	@Override
	public void deleteRecord(String webuserID, String comments) throws ENTException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setType(ifDataType type) {
		this.tsType = type;
		
	}

	@Override
	public Long createRecord(HashMap<String, Object> paramMap, Connection con) throws ENTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInsertStatement(NamedParameterStatement nps) throws SQLException, ENTException {
		super.setInsertStatement(nps);
		
	}

	@Override
	public void setSrc(ifDataSrc src) {
		this.dataSrc = src;
		
	}

	@Override
	public ifDataSrc getSrc() {
		return this.dataSrc;
	
	}

	@Override
	public void setInsertStatement(NamedParameterStatement nps, Connection con) throws SQLException, ENTException {
		// TODO Auto-generated method stub
		
	}
		

}
