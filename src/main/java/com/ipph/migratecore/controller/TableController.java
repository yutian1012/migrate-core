package com.ipph.migratecore.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
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
	
	@RequestMapping("/selectTables")
	@ResponseBody
	public List<TableModel> selectTables(){
		return tableService.getList();
	}
	/**
	 * 查看记录数据
	 * @param tableId
	 * @param dataId
	 * @return
	 */
	@RequestMapping("/record/{tableId}/{dataId}")
	@ResponseBody
	public Map<String,Object> getRecord(@PathVariable("tableId")Long tableId,@PathVariable("dataId")Long dataId){
		if(null!=dataId&&dataId!=0L) {
			return tableService.getRecord(tableId,dataId);
		}
		return null;
	}
	/**
	 * 删除
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/del/{tableId}")
	public String del(@PathVariable("tableId")Long tableId) {
		if(null!=tableId&&tableId!=0L) {
			tableService.del(tableId);
		}
		return "redirect:/tables/list";
	}
	/**
	 * 查看table定义信息
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/info/{tableId}")
	@ResponseBody
	public TableModel info(@PathVariable("tableId")Long tableId) {
		
		TableModel tableModel=tableService.getById(tableId);
		
		return tableModel;
	}
	
	@RequestMapping("/add")
	public ModelAndView add() {
		
		ModelAndView mv=new ModelAndView("/tables/add");
		
		mv.addObject("sourceTableList",tableService.getSourceTables()).addObject("targetTableList",tableService.getTargetTables());
		mv.addAllObjects(getEnumeration());
		
		return mv;
	}
	/**
	 * 查看表定义信息
	 * @param isSourceTable
	 * @param tableName
	 * @return
	 */
	@RequestMapping("/getTable")
	@ResponseBody
	public com.ipph.migratecore.table.TableMetaModel getTable(@RequestParam("isSourceTable") Boolean isSourceTable,@RequestParam("tableName") String tableName){
		return tableService.getTableByName(isSourceTable,tableName);
	}
	/**
	 * 保存表
	 */
	@RequestMapping("/saveTable")
	public String saveTable(TableModel table) {
		tableService.save(table);
		return "redirect:/tables/add";
	}
	/**
	 * 获取枚举信息，在页面上使用
	 * @return
	 */
	private Map<String,Map<?,?>> getEnumeration(){
		Map<String,Map<?,?>> result=new HashMap<String, Map<?,?>>();
		
		//值来源枚举对象
		result.put("valueType", FieldValueTypeEnum.getEnumValues());
		
		return result;
	}
}
