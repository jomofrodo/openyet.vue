package com.netazoic.covid.ent;



import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.JDBCType;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLType;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.netazoic.ent.ENT;
import com.netazoic.ent.ENTException;
import com.netazoic.util.NamedParameterStatement;

public abstract class rdENT<T> extends ENT<T> implements ifDataSrcWrapper {


	public String sql_ActivateENT;
	public String sql_DeleteRemoteData;
	
	NamedParameterStatement npsCreate;
	ifDataSrc dataSrc;
	
	public enum DataFmt{
		JSON, CSV
	}

	@Override
	public void initENT() throws ENTException{
		super.initENT();
	}


	public rdENT() throws ENTException {
		super();
	}



	public rdENT(HashMap<String,Object> recMap) throws ENTException{
		// Set up rdEnt class vars using values from recMap
	}

	public void init(HashMap<String,Object> recMap) throws ENTException {
		// Do some stuff to set class vars based on recMap
		//List<Field> flds = this.getFields();
		try {
			this.setFieldVals(recMap);
		}catch(Exception ex) {
			throw new ENTException(ex);
		}
	}

	private String setupNPSInsertQuery() throws SQLException {
		String q;
		List<Field> flds = this.getLocalFields(new LinkedList<Field>(), this.getClass(), true);
		q = "INSERT INTO " + this.nit.NIT_TABLE;
		q += "(";
		String fldName;
		for(Field fld : flds) {
			q += fld.getName() + ",";
		}
		q = q.substring(0,q.lastIndexOf(","));
		q += ") \n";
		q += " VALUES (";
		for(Field fld : flds) {
			q += ":" + fld.getName() + ",";
		}
		q = q.substring(0,q.lastIndexOf(","));
		q += ");";
		return q;
	}
	
	// Check for a local copy of a remote record
	
	
	public NamedParameterStatement setupInsertStatement( Connection con) throws SQLException {
		String q = this.setupNPSInsertQuery();
		NamedParameterStatement nps = new NamedParameterStatement(con,q);
		return nps;
	}


	public void setInsertStatement (NamedParameterStatement nps) throws SQLException, ENTException {
		List<Field> flds = this.getLocalFields(new LinkedList<Field>(), this.getClass(), true);
		Object fldVal = null;
		for(Field fld : flds) {
			Class<?> type = fld.getType();
			SQLType sqlType = null;
			try {
				fldVal = fld.get(this);
//				fldVal = type.getClass().cast(fldVal);
				if(type.equals(LocalDate.class)) sqlType = JDBCType.DATE;
				if(sqlType == null)	nps.setObject(fld.getName(), fldVal);
				else nps.setObject(fld.getName(), fldVal, sqlType);
			} catch(SQLException sqlEx) {
				throw sqlEx;
			} catch (IllegalArgumentException e) {
				throw new ENTException(e);
			} catch (IllegalAccessException e) {
				throw new ENTException(e);
			}
			
		}
		String ret = "done";  //Debugging
	}

	// Check for a local copy of a remote record

	
	public abstract void setInsertStatement(PreparedStatement psInsertRemoteData) throws SQLException;
	// Set values of prepared statment based on class var values


	public abstract void setUpdateStatement(PreparedStatement psUpdateRemoteData) throws SQLException;
	// Set values of prepared statment based on class var values


	public abstract void setExpireAllStatement(PreparedStatement psDeleteRemoteData) throws SQLException;
	// Expire all local copies of remote data

	public abstract void setCheckRecordStatement(PreparedStatement psSelectRemoteData) throws SQLException;
	// Check for a local copy of a remote record




}
