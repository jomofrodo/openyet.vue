package com.netazoic.covid.task;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.netazoic.covid.XMLUtil;
import com.netazoic.covid.task.SimpleTask.Task_Param;
import com.netazoic.ent.ServENT;
import com.netazoic.ent.rdENT;
import com.netazoic.ent.ServENT.ENT_Param;
import com.netazoic.ent.ifServENT;
import com.netazoic.util.ParseUtil;
import com.netazoic.util.RemoteDataObj;

public abstract class SimpleTask extends Thread implements ifServENT {
	public DataSource dataSource = null;


	public boolean flgDebug = false;
	public boolean flgDebugTrace = true;

	public ParseUtil parser = new ParseUtil();
	public static Logger logger = LogManager.getLogger(SimpleTask.class);
	HashMap<String, String> settings;
	Connection con;

	public enum Task_Param {
		jdbcURL, jdbcUser, jdbcPwd, configFile
	}
	
	public Connection getConnection (HashMap<String,String> map) throws SQLException {
		String driverManagerURL, driverManagerUser, driverManagerPwd;
		driverManagerURL = map.get(Task_Param.jdbcURL.name());
		driverManagerUser = map.get(Task_Param.jdbcUser.name());
		driverManagerPwd = map.get(Task_Param.jdbcPwd.name());
		return getConnection(driverManagerURL,driverManagerUser,driverManagerPwd);
		
		
	}

	public Connection getConnection(String url, String user, String pwd) throws SQLException {
		Connection con = null;
		if (dataSource != null)
			con = dataSource.getConnection();
		else if (url != null) {
			if (user != null && pwd != null) {
				con = DriverManager.getConnection(url, user, pwd);
			} else {
				con = DriverManager.getConnection(url);
			}
		}
		return con;
	}
	
	public RemoteDataObj getRDO(rdENT ds, Connection con) throws Exception {
		RemoteDataObj rdo = new RemoteDataObj();

		rdo.rdEnt = ds;
		rdo.con = con;
		rdo.mgr = this;
		rdo.init();
		return rdo;
	}

	public void init() throws SQLException {
		con = getConnection(settings);
		logger = LogManager.getLogger(this.getClass());
		parser.templatePath = settings.get(ENT_Param.TemplatePath.name());
		if(parser.templatePath==null) logger.error("Template path not set");
	}
	
	public void logException(Exception ex) {
		logger.error(ex.getMessage());
		if(flgDebugTrace) {
			StackTraceElement[] ste = ex.getStackTrace();
			String stMsg = "";
			for(StackTraceElement st : ste){
				stMsg += st.toString() + "\n";
			}
			logger.debug(stMsg);
		}
	}

	@Override
	public abstract void run();

	public String parseQuery(String tPath, Map<String, Object> map) throws Exception {
		return parser.parseQuery(tPath, map);
	}

	public void setParams(HashMap<String, String> args) {
		settings = new HashMap<String,String>();
		for (String k : args.keySet()) {
			settings.put(k, args.get(k));
		}
		String configFile = args.get(Task_Param.configFile.name());
		if(configFile!=null) {
			HashMap<String, String> argsMap = XMLUtil.ParamMapToHashMap(configFile);
			settings.putAll(argsMap);
		}
		File f = new File(".");
		String templatePath = settings.get(ENT_Param.TemplatePath.name());
		String path = templatePath;
		if(!templatePath.startsWith("/")) {
			path = f.getAbsolutePath() + "/" +  templatePath;
		}
		path = path.replaceAll("\\\\", "/");
		path = path.replaceAll("/\\./", "/");
		f = new File(path);
		settings.put(ENT_Param.TemplatePath.name(), f.getPath());

		


	}
}
