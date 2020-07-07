package com.netazoic.covid;

import java.io.File;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {

	private static final Logger logger = LogManager.getLogger(OpenYet.class);
	static boolean flgDebug = true;

	public static HashMap<String, String> ParamMapToHashMap(String filePath) {
		HashMap<String, String> hMap = new HashMap<String, String>();
		File file = new File(filePath);

		if (file.exists()) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			try {
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(file);
				Element documentElement = document.getDocumentElement();
				NodeList sList = documentElement.getElementsByTagName("context-param");
				if (sList != null && sList.getLength() > 0) {
					for (int i = 0; i < sList.getLength(); i++) {
						Node node = sList.item(i);
						if (node.getNodeType() == Node.ELEMENT_NODE) {
							Element e = (Element) node;

							NodeList nodeList = e.getElementsByTagName("param-name");

							String paramName = nodeList.item(0).getChildNodes().item(0).getNodeValue();
							paramName = paramName.trim();

							nodeList = e.getElementsByTagName("param-value");
							String paramVal = nodeList.item(0).getChildNodes().item(0).getNodeValue();
							paramVal = paramVal.trim();
							
							hMap.put(paramName, paramVal);
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
				if (flgDebug)
					logger.error(e.getStackTrace());
			}
		} else {
			logger.error("File not found: "  + filePath);
		}
		
		return hMap;

	}
}
