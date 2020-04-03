package com.netazoic.covid.ent;

import com.netazoic.covid.ent.rdENT.DataFmt;
import com.netazoic.ent.if_SRC_ORG;

public interface ifDataSrc {
	
	public String getURL();

	public DataFmt getFormat();
	
	public if_SRC_ORG getSrcOrganization();

}
