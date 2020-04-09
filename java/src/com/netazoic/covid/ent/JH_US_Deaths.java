package com.netazoic.covid.ent;

import com.netazoic.ent.ENTException;
import com.netazoic.ent.rdENT.SRC_ORG;

public class JH_US_Deaths extends JHTimeSeries {
	
	public String state;
	public String country;
//	private Double lat;
//	private Double lon;
	public String date;
	public Integer ct;
	public String type;
	
	private static String DATA_URL = "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_us.csv";
	private static String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";

	public JH_US_Deaths() throws ENTException {

		super();
		this.dataURL = urlBase + DATA_URL;
		this.srcOrg = SRC_ORG.JH;
		this.tsType = JH_TimeSeriesType.dead;
		this.type = this.tsType.getCode();
	}

}
