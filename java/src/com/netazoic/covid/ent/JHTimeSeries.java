package com.netazoic.covid.ent;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;

import com.netazoic.covid.ent.rdENT.DataFmt;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.if_TP;
import com.netazoic.util.NamedParameterStatement;
import com.netazoic.util.SQLUtil;

public class JHTimeSeries extends rdENT<ifDataSrcWrapper>{
	// A Johns Hopkins  time series entry
	
	protected ifDataType tsType;
	DataFmt dataFmt = DataFmt.CSV;
	
	
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

        sql_CREATE_RECORDx("/Data/sql/JH/TimeSeries/psCreateRecord.sql"),
        sql_CREATE_COMBINED_RECS("/Data/sql/JH/TimeSeries/CreateCombinedRecs.sql");

		
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
	public void initENT() throws ENTException{
		this.nit.ctpClass = JH_TP.class;
		nit.ENTITY_NAME = "JH Time Series";
		nit.NIT_TABLE = "covid.jh_timeseries";
		nit.FLD_NIT_ID = "date";
		super.initENT();
		
	}
	
	@Override
	public Integer createCombinedRecs() throws Exception {
		HashMap map = new HashMap();
		String q =  parseUtil.parseQueryFile(JH_TP.sql_CREATE_COMBINED_RECS.tPath,map);
		return SQLUtil.execSQL(q, con);
	}
	
	@Override
	public Long createRecord(HashMap<String, Object> paramMap, Connection con) throws ENTException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRecord(String webuserID, String comments) throws ENTException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getDataURL() {
		return this.dataURL;
	}

	@Override
	public ifDataSrc getSrc() {
		return this.dataSrc;
	
	}

	@Override
	public void setCon(Connection con) {
		this.con = con;
	}

	@Override
	public void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException {
	
		psDeleteRemoteData.setString(1, this.tsType.getCode());
	}

	@Override
	public void setInsertStatement(PreparedStatement ps) throws SQLException {
//		NOT IN USE
		
	}

	@Override
	public void setSrc(ifDataSrc src) {
		this.dataSrc = src;
		
	}

	@Override
	public void setType(ifDataType type) {
		this.tsType = type;
		
	}

	@Override
	public PreparedStatement setupExpireAllStatement(Connection con) throws SQLException {
		String sql = "DELETE FROM covid.jh_timeseries WHERE type = ?";
		PreparedStatement psDeleteRemoteData = con.prepareStatement(sql);
		return psDeleteRemoteData;
	}

	@Override
	public void setUpdateStatement(PreparedStatement psUpdateRemoteData) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SRC_ORG getSrcOrg() {
		return this.srcOrg;
	}
	
	@Override
	public DataFmt getFormat() {
		return this.dataFmt;
	}


		

}
