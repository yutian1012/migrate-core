package com.ipph.migratecore.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ipph.migratecore.excel.ExcelRead;
import com.ipph.migratecore.model.PatentUploadModel;
import com.ipph.migratecore.service.PatentUploadService;
import com.ipph.migratecore.util.XmlUtil;

@Controller
@RequestMapping("patent")
public class PatentUploadExcelController {
	
	@Resource
	private PatentUploadService patentUploadService;
	
	@RequestMapping("/toUpload")
	public String toUpload() {
		
		return "patent/toUpload";
	}
	/**
	 * 上传excel文档
	 * @param file
	 * @return
	 * @throws ParseException 
	 */
	@RequestMapping("/uploadXls")
	public String uploadXls(@RequestParam("patentFile") MultipartFile file) throws ParseException{
		if(!file.isEmpty()){
			
			String fileName = file.getOriginalFilename();
	        try {
	        	//解析上传的文件并保存到数据库中
	        	ExcelRead excelRead=new ExcelRead(8);
	        	
	        	List<Map<String,String>> result=excelRead.readExcel(file);
	        	patentUploadService.uploadXls(result);
	        	
	        	result.clear();
	        	
	        	//转存到文件系统中
	        	File f=new File(XmlUtil.getFilePath() +fileName);
	        	file.transferTo(f); //保存文件
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }
		return "redirect:/patent/toUpload";
	}
	
	@RequestMapping("/list")
	@ResponseBody
	public List<PatentUploadModel> list(@RequestParam(value="size",defaultValue="20")int size,
			@RequestParam(value="page",defaultValue="0")int page,
			HttpServletRequest request) {
		
		Pageable pageable=PageRequest.of(page, size);
		return patentUploadService.list(pageable);
	}
	
	
}
