package com.netazoic.covid.ent;

import com.netazoic.covid.ent.rdENT.SRC_ORG;
import com.netazoic.ent.ENTException;

public class JH_Dead extends JHTimeSeries {
	
	public String state;
	public String country;
//	private Double lat;
//	private Double lon;
	public String date;
	public Integer ct;
	public String type;
	
	private static String DATA_URL = "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
	private static String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";

	public JH_Dead() throws ENTException {

		super();
		this.dataURL = urlBase + DATA_URL;
		this.srcOrg = SRC_ORG.JH;
		this.tsType = JH_TimeSeriesType.dead;
		this.type = this.tsType.getCode();
	}

}
