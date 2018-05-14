package com.ipph.migratecore.util;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import com.ipph.migratecore.model.TableModel;

public class XmlUtilTest {

	@Test
	public void testXmlParse() {
		try {
			List<TableModel> tableList=XmlUtil.parseBySax(XmlUtil.class.getResource("/xml/patent.xml").getPath());
			System.out.println(tableList.size());
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
