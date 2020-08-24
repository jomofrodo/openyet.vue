package com.netazoic.covid.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;

import com.netazoic.covid.OpenYet.CVD_DataSrc;
import com.netazoic.covid.OpenYet.CVD_Param;
import com.netazoic.covid.XMLUtil;
import com.netazoic.covid.ent.CTP_Daily;
import com.netazoic.ent.ifDataSrcWrapper.RemoteDataRecordCtr;
import com.netazoic.ent.rdENT;
import com.netazoic.ent.rdENT.DataFmt;
import com.netazoic.ent.rdENT.SRC_ORG;
import com.netazoic.util.FileUtil;
import com.netazoic.util.HttpUtil;
import com.netazoic.util.JSONUtil;
import com.netazoic.util.RemoteDataObj;
import com.netazoic.util.ifRemoteDataObj;

public class RetrieveDataTask extends SimpleTask {
	Boolean flgExpireRecs = false;
	
	GetCombinedData combined = new GetCombinedData();
	

	public RemoteDataRecordCtr retrieveRemoteData(String country, String state, rdENT rdent, ifRemoteDataObj rmdObj,
			Connection con) throws IOException, Exception, SQLException {
		boolean flgLocalDebug = false;
		boolean flgAutoCommitAsIFoundIt = con.getAutoCommit();

		Savepoint savePt = null;
		// Integer ctRemoteDataRecs = 0, ctReturningRemoteData=0, ctUpdatedRemoteData=0,
		// ctNewRemoteData=0, ctBadRecords=0;

		RemoteDataRecordCtr ctrObj = new RemoteDataRecordCtr();

		String fqdn = rdent.getDataURL();
		DataFmt dataFmt = rdent.getFormat();
		try {
			SRC_ORG srcOrg = (SRC_ORG) rdent.getSrcOrg();

//			HttpURLConnection http = HttpUtil.getRemoteHTTPConn(fqdn, flgDebug);
//
//			InputStream is = http.getInputStream();
			
			BufferedInputStream is = new BufferedInputStream(new URL(fqdn).openStream());

			// Write the input stream to a local file.  Open that and use that as the input stream for our processing.
			// To avoid timeouts in the https connection.
			File f = FileUtil.WriteInputStreamToFile(is, "data/remotedata/" + srcOrg.getCode() + "_import.json");
			FileInputStream fis = new FileInputStream(f);

			is = new BufferedInputStream(fis);
			if (flgLocalDebug) {
				// This will kill the input stream for any further processing
				System.out.print(HttpUtil.getResponseString(is));
			}

			// Everything good to this point,

			con.setAutoCommit(false);
			savePt = con.setSavepoint();

			// And now parse the stream

			switch (srcOrg) {
			case JH_G:
			case JH_US:
				// Johns Hopkins CSV files
				rdent.importRecords(rmdObj, ctrObj, logger, savePt, con, is);
				break;
			case CTP:
				// Covid Tracking Project json files
				rdent.importRecords(rmdObj, ctrObj, logger, savePt, con, is);
			}
			if (dataFmt.equals(DataFmt.CSV)) {

			} else if (dataFmt.equals(DataFmt.JSON)) {

			}
		} catch (IOException ex) {
			ctrObj.ctTotalRecords.decrement();
			throw ex;
		} catch (Exception ex) {
			if (!con.getAutoCommit())
				con.rollback(savePt);
			ctrObj.ctBadRecords.increment();
			throw ex;
		} finally {
			if (!con.getAutoCommit())
				con.commit();
			con.setAutoCommit(flgAutoCommitAsIFoundIt);

		}
		if (ctrObj.ctTotalRecords.value > 0)
			reportImportStats(ctrObj);

		return ctrObj;
	}

	public String reportImportStats(RemoteDataRecordCtr ctrObj) {

		Integer ctRemoteDataRecs = ctrObj.ctTotalRecords.value;
		Integer ctNewRemoteData = ctrObj.ctNewRecordsCreated.value;
		Integer ctBadRecords = ctrObj.ctBadRecords.value;
		String msg = "Finished importing remote records.\r\n";
		msg += "Processed " + ctRemoteDataRecs + " records.\r\n";
		// if(ctDuplicate > 0) msg += "Found " + ctDuplicate + " duplicate
		// entries.\r\n";
		if (ctBadRecords > 0)
			msg += "Found " + ctBadRecords + " invalid records in the input.\r\n";
		if (ctRemoteDataRecs > 0)
			msg += "A total of " + ctRemoteDataRecs + " module records retrieved from remote source.\r\n";
		if (ctNewRemoteData > 0)
			msg += "Created " + ctNewRemoteData + " new module records\r\n";
		// if(ctReturningRemoteData > 0) msg += "Found " + ctReturningRemoteData + "
		// existing records \r\n";
		// if(ctUpdatedRemoteData > 0) msg += "Updated " + ctUpdatedRemoteData + "
		// existing records \r\n";
		//
		// if(flgVerbose) System.out.println(msg);
		logger.info(msg);
		return msg;
	}

	@Override
	public void run() {
		CVD_DataSrc[] dataSrcA = CVD_DataSrc.values();
		String country = (String) settings.get(CVD_Param.country.name());
		String state = (String) settings.get(CVD_Param.state.name());
		logger.info("Starting retrieval of ALL DATA.");

		HashMap<String, Object> retMap = new HashMap<String, Object>();
		RemoteDataRecordCtr ctMap = null;
		

		try {
			for (CVD_DataSrc src : dataSrcA) {
				// DEBUG
//				if(!src.equals(CVD_DataSrc.JH_US_DEATHS)) continue;
				// !DEBUG
				
				logger.info("Retrieving data for: " + src.name());
				rdENT rdEnt = src.getEnt();
				rdEnt.init(con);
				switch (src.rdEnt.srcOrg) {
				case JH_G:
				case JH_US:
					rdEnt.setSrc(src);
					rdEnt.setType(src.type);
					break;
				case CTP:
					rdEnt = new CTP_Daily();
					rdEnt.setSrc(src);
					break;

				}
				RemoteDataObj rdo = getRDO(rdEnt, con);
				if (flgExpireRecs) {
					int ctExpired = rdo.expireAllRemoteDataRecords(null);
					logger.info("Expired " + ctExpired + " existing combined records");
					// flgExpireExisting = false;
				}
				con.setAutoCommit(false);
				logger.info("Starting retrieval of remote data");
				ctMap = retrieveRemoteData(country, state, rdEnt, rdo, con);
				logger.info("Created " + ctMap.ctNewRecordsCreated.value + " new records for " + src.srcCode);
				retMap.put("cts_" + src.srcCode, ctMap);
				retMap.put("src_" + src.srcCode, src.name());
				retMap.put("srccode_" + src.srcCode, src.srcCode);
			}

			String json = JSONUtil.toJSON(retMap);
			logger.info(json);
			con.commit();
			con.close();
			logger.info("Creating combined data");
			combined.start();
			
		} catch (Exception ex) {
			String msg = ex.toString();
			msg += ":" + ex.getMessage();
			logger.error(msg);
		}

	}

	@Override
	public void setParams(HashMap<String, String> args) {
		String configFile = args.get(Task_Param.configFile.name());
		HashMap<String, String> argsMap = XMLUtil.ParamMapToHashMap(configFile);
		args.putAll(argsMap);
		super.setParams(args);
		combined.setParams(args);   // initialize the combined task
		try {
			combined.init();
		} catch (SQLException e) {
			logger.error(e.getMessage());
		}

		String expireAll = (String) settings.get(CVD_Param.expireAll.name());
		if (expireAll != null)
			flgExpireRecs = Boolean.parseBoolean(expireAll);

	}

	public static void main(String[] args) {
		RetrieveDataTask retriever = new RetrieveDataTask();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put(Task_Param.configFile.name(), "www/WEB-INF/conf/sys.local.xml");
		map.put(CVD_Param.expireAll.name(), "false");

		retriever.setParams(map);
		try {
			retriever.init();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retriever.run();
	}

}
