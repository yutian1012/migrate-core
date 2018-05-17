package com.ipph.migratecore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.xml.TransferTableHandler;

public class XmlUtil {
	
	public static String getFilePath(){
		String path=XmlUtil.class.getResource("/").getPath();
		
		File filePath=new File(path+"attachment");
		
		if(!filePath.exists()){
			filePath.mkdirs();
		}
		
		return filePath.getPath()+File.separator;
	}
	
	/**
	 * 解析xml
	 * @param path
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static List<TableModel> parseBySax(String path) throws ParserConfigurationException, SAXException, IOException{
		
		//if(! validateXmlByXSD(path)) return null;
		
		InputStream in=new FileInputStream(path);
		
		return parseBySax(in);
	}
	
	
	public static List<TableModel> parseBySax(InputStream in) throws ParserConfigurationException, SAXException, IOException{
		
		//if(! validateXmlByXSD(path)) return null;
		
		// 创建解析工厂  
        SAXParserFactory factory = SAXParserFactory.newInstance();  
        // 创建解析器  
        SAXParser parser = factory.newSAXParser();  
        // 得到读取器  
        XMLReader reader = parser.getXMLReader();
        //设置内容处理器
        TransferTableHandler handler=new TransferTableHandler();
        
        reader.setContentHandler(handler);
        //读取xml文档
        //reader.parse(path);
        
        reader.parse(new InputSource(in));
        
        return handler.getResult();
	}
	
	/**
	 * 判断xml是否符合规范
	 * @param xmlPath
	 * @return
	 */
	public static boolean validateXmlByXSD(String xmlPath){
		FileInputStream fis=null;
		boolean result=false;
        try {
        	SchemaFactory schemaFactory=SchemaFactory.newInstance(XMLConstants.XML_NS_URI);
        	Schema schema=schemaFactory.newSchema(new StreamSource(XmlUtil.class.getResourceAsStream("/xml/tables.xsd")));
        	Validator validator=schema.newValidator();
        	fis=new FileInputStream(xmlPath);
			validator.validate(new StreamSource(fis));
			result=true;
		} catch (Exception e) {
			e.printStackTrace();
			result=false;
		}finally{
			if(null!=fis){
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
        return result;
	}
	
}
