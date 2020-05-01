package com.netazoic.covid.ent;

import com.netazoic.covid.OpenYet.CVD_DataSrc;
import com.netazoic.covid.ent.JH_TimeSeries.JH_TimeSeriesType;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.rdENT.SRC_ORG;

public class JH_Global_Recovered extends JH_Global_TimeSeries {
	
	public String state;
	public String country;
//	private Double lat;
//	private Double lon;
	public String date;
	public Integer ct;
	public String type;
	private static String DATA_URL = "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";

	public JH_Global_Recovered() throws ENTException {

		super();
		this.dataURL = urlBase + DATA_URL;
		this.srcOrg = SRC_ORG.JH_G;
		this.dataSrc = CVD_DataSrc.JH_GLOBAL_RECOVER;
		this.tsType = JH_TimeSeriesType.recovered;
		this.type = this.tsType.getCode();
	}

}
