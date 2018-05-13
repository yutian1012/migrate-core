package com.ipph.migratecore.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.TableService;
import com.ipph.migratecore.util.XmlUtil;

@Controller
@RequestMapping("/tables")
public class TableController {
	
	@Resource
	private TableService tableService;
	
	@RequestMapping("/list")
	public ModelAndView list(){
		ModelAndView mv=new ModelAndView("tables/list");
		
		List<TableModel> tableList=tableService.getList();
		
		mv.addObject("tableList",tableList);
		return mv;
	}
	
	@RequestMapping("/toUploadXml")
	public String toUploadXml(){
		return "tables/uploadXml";
	}
	
	@RequestMapping("/uploadXml")
	public String uploadXml(@RequestParam("tableFile") MultipartFile file){
		if(!file.isEmpty()){
			
			String fileName = file.getOriginalFilename();
			
	        try {
	        	//解析上传的文件并保存到数据库中
	        	tableService.uploadXml(file.getInputStream());
	        	
	        	//转存到文件系统中
	        	File f=new File(XmlUtil.getFilePath() +fileName);
	        	file.transferTo(f); //保存文件
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        }
		return "redirect:/tables/list";
	}
	
	/*@RequestMapping("/migrate/{tableId}")
	@ResponseBody
	public boolean migrate(@PathVariable("tableId")Long tableId){
		
		if(null!=tableId){
			return tableService.migrateTable(tableId,0L);
			
		}
		
		return false;
	}*/
	
	@RequestMapping("/selectTables")
	@ResponseBody
	public List<TableModel> selectTables(){
		return tableService.getList();
	}
	
	@RequestMapping("/record/{tableId}/{dataId}")
	@ResponseBody
	public Map<String,Object> getRecord(@PathVariable("tableId")Long tableId,@PathVariable("dataId")Long dataId){
		if(null!=dataId&&dataId!=0L) {
			return tableService.getRecord(tableId,dataId);
		}
		return null;
	}
}
