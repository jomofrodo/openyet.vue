package com.netazoic.covid.ent;

import com.netazoic.covid.ent.JH_TimeSeries.JH_TimeSeriesType;
import com.netazoic.ent.ENTException;
import com.netazoic.ent.rdENT.SRC_ORG;

public class JH_Global_Recovered extends JH_Global_TimeSeries {
	
	
	private static String DATA_URL = "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
	private static String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";

	public JH_Global_Recovered() throws ENTException {

		super();
		this.dataURL = urlBase + DATA_URL;
		this.srcOrg = SRC_ORG.JH_G;
		this.tsType = JH_TimeSeriesType.recovered;
		this.type = this.tsType.getCode();
	}

}
