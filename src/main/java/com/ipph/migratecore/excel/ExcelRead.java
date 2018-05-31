package com.ipph.migratecore.excel;

import java.io.IOException;  
import java.io.InputStream;  
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;  
import org.apache.poi.hssf.usermodel.HSSFRow;  
import org.apache.poi.hssf.usermodel.HSSFSheet;  
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;  
import org.apache.poi.xssf.usermodel.XSSFRow;  
import org.apache.poi.xssf.usermodel.XSSFSheet;  
import org.apache.poi.xssf.usermodel.XSSFWorkbook;  
import org.springframework.web.multipart.MultipartFile;
/**
 * 从第二行开始读取，第一行默认为标题行
 * 读取的列数需要指定
 */
public class ExcelRead {
	public int totalRows; //sheet中总行数  
    public int totalCells; //每一行总单元格数  
    public ExcelRead(int totalCells) {
    	this.totalCells=totalCells;
    }
    /** 
     * read the Excel .xlsx,.xls 
     * @param file jsp中的上传文件 
     * @return 
     * @throws IOException  
     */  
    public List<Map<String,String>> readExcel(MultipartFile file) throws IOException {  
        if(file==null||ExcelUtil.EMPTY.equals(file.getOriginalFilename().trim())){  
            return null;  
        }else{  
            String postfix = ExcelUtil.getPostfix(file.getOriginalFilename());  
            if(!ExcelUtil.EMPTY.equals(postfix)){  
                if(ExcelUtil.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)){  
                    return readXls(file);  
                }else if(ExcelUtil.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)){  
                    return readXlsx(file);  
                }else{                    
                    return null;  
                }  
            }  
        }  
        return null;  
    }  
    /** 
     * read the Excel 2010 .xlsx 
     * @param file 
     * @param beanclazz 
     * @param titleExist 
     * @return 
     * @throws IOException  
     */  
    public List<Map<String,String>> readXlsx(MultipartFile file){  
    	List<Map<String,String>> list = new ArrayList<Map<String,String>>();  
        // IO流读取文件  
        InputStream input = null;  
        XSSFWorkbook wb = null;  
        Map<String,String> rowMap = null;  
        try {  
            input = file.getInputStream(); 
            // 创建文档  
            wb = new XSSFWorkbook(input);                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
                XSSFSheet xssfSheet = wb.getSheetAt(numSheet);  
                if(xssfSheet == null){  
                    continue;  
                }  
                totalRows = xssfSheet.getLastRowNum();                
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
                    XSSFRow xssfRow = xssfSheet.getRow(rowNum);  
                    if(xssfRow!=null){  
                    	rowMap = new HashMap<String,String>(totalRows);  
                        totalCells = xssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(int c=0;c<totalCells;c++){  
                            XSSFCell cell = xssfRow.getCell(c);  
                            if(cell==null){  
                            	rowMap.put(c+"",ExcelUtil.EMPTY);    
                                continue;  
                            }                             
                            rowMap.put(c+"",ExcelUtil.getXValue(cell).trim());  
                        }     
                    list.add(rowMap);                                            
                    }  
                }  
            }  
            return list;  
        } catch (IOException e) {             
            e.printStackTrace();  
        } finally{  
            try {  
                input.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
          
    }  
    /** 
     * read the Excel 2003-2007 .xls 
     * @param file 
     * @param beanclazz 
     * @param titleExist 
     * @return 
     * @throws IOException  
     */  
    public List<Map<String,String>> readXls(MultipartFile file){   
        List<Map<String,String>> list = new ArrayList<Map<String,String>>();  
        // IO流读取文件  
        InputStream input = null;  
        HSSFWorkbook wb = null;
        Map<String,String> rowMap = null;  
        try {  
            input = file.getInputStream();  
            // 创建文档  
            wb = new HSSFWorkbook(input);                         
            //读取sheet(页)  
            for(int numSheet=0;numSheet<wb.getNumberOfSheets();numSheet++){  
                HSSFSheet hssfSheet = wb.getSheetAt(numSheet);
                if(hssfSheet == null){
                    continue;  
                }  
                totalRows = hssfSheet.getLastRowNum();                
                //读取Row,从第二行开始  
                for(int rowNum = 1;rowNum <= totalRows;rowNum++){  
                    HSSFRow hssfRow = hssfSheet.getRow(rowNum);  
                    if(hssfRow!=null){  
                        rowMap = new HashMap<String,String>(totalRows);  
                        totalCells = hssfRow.getLastCellNum();  
                        //读取列，从第一列开始  
                        for(short c=0;c<totalCells;c++){  
                            HSSFCell cell = hssfRow.getCell(c);  
                            if(cell==null){  
                                rowMap.put(c+"",ExcelUtil.EMPTY);  
                                continue;
                            }                             
                            rowMap.put(c+"",ExcelUtil.getHValue(cell).trim());  
                        }          
                        list.add(rowMap);  
                    }                     
                }  
            }  
            return list;  
        } catch (IOException e) {             
            e.printStackTrace();  
        } finally{  
            try {  
                input.close();  
            } catch (IOException e) {  
                e.printStackTrace();  
            }  
        }  
        return null;  
    }  
}
