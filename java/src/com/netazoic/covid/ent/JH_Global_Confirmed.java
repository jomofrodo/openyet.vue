package com.netazoic.covid.ent;

import com.netazoic.ent.ENTException;
import com.netazoic.ent.ifDataSrcWrapper;

public class JH_Global_Confirmed extends JH_Global_TimeSeries implements ifDataSrcWrapper{

	
	private static String DATA_URL = "csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	private static String urlBase = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/";
	
	private String desc = "Johns Hopkins time series new Confirmed";

	public JH_Global_Confirmed() throws ENTException {

		super();
		this.dataURL = urlBase + DATA_URL;
		this.srcOrg = SRC_ORG.JH_G;
		this.tsType = JH_TimeSeriesType.confirmed;
		this.type = this.tsType.getCode();
	}

}
