package com.netazoic.covid.ent;

import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.netazoic.covid.Covid19.CVD_DataSrc;
import com.netazoic.ent.ENTException;
import com.netazoic.util.NamedParameterStatement;

public interface ifDataSrcWrapper {

	void setType(ifDataType type);

	void setInsertStatement(NamedParameterStatement nps, Connection con) throws SQLException, ENTException;

	void setCon(Connection con);

	void init(HashMap<String, Object> recMap) throws ENTException;

	void setSrc(ifDataSrc src);

	ifDataSrc getSrc();

	PreparedStatement setupExpireAllStatement(Connection con) throws SQLException;

}
