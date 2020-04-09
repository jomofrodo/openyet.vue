package com.netazoic.covid.ent;

import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import com.netazoic.covid.Covid19.CVD_DataSrc;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.rdENT.DataFmt;
import com.netazoic.ent.rdENT.SRC_ORG;
import com.netazoic.util.NamedParameterStatement;

public interface ifDataSrcWrapper {

	void init(HashMap<String, Object> recMap) throws ENTException;

	void setType(ifDataType type);

	void setInsertStatement(NamedParameterStatement nps) throws SQLException, ENTException;

	void setCon(Connection con);

	PreparedStatement setupExpireAllStatement(Connection con) throws SQLException;

	void setSrc(ifDataSrc src);

	ifDataSrc getSrc();
	
	String getDataURL();
	
	SRC_ORG getSrcOrg();

	DataFmt getFormat();
}
