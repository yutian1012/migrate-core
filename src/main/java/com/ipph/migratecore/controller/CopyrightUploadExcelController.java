package com.ipph.migratecore.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ipph.migratecore.common.ExceptionMsg;
import com.ipph.migratecore.common.Response;
import com.ipph.migratecore.excel.ExcelRead;
import com.ipph.migratecore.service.CopyrightUploadService;

@Controller
@RequestMapping("copyright")
public class CopyrightUploadExcelController extends BaseController{
	
	@Resource
	private CopyrightUploadService copyrightUploadService;
	
	@RequestMapping("/toUpload")
	public String toUpload() {
		
		return "copyright/toUpload";
	}
	
	/**
	 * 上传excel文档
	 * @param file
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/uploadXls")
	@ResponseBody
	public Response uploadXls(@RequestParam("copyrightFile") MultipartFile file) throws ParseException{
		if(!file.isEmpty()){
			
			String fileName = file.getOriginalFilename();
	        try {
	        	//解析上传的文件并保存到数据库中
	        	ExcelRead excelRead=new ExcelRead(8);
	        	
	        	List<Map<String,String>> result=excelRead.readExcel(file);
	        	copyrightUploadService.uploadXls(result);
	        	
	        	result.clear();
	        	
	        	//转存到文件系统中
	        	File f=new File("C:/attachment/" +fileName);
	        	file.transferTo(f); //保存文件
	        	
	        } catch (Exception e) {
	        	e.printStackTrace();
				return result(ExceptionMsg.FAILED);
			}
        }
		return result(ExceptionMsg.SUCCESS);
	}
}
