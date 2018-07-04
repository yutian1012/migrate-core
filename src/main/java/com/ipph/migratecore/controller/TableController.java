package com.ipph.migratecore.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.common.ExceptionMsg;
import com.ipph.migratecore.common.Response;
import com.ipph.migratecore.enumeration.FieldValueTypeEnum;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.service.TableService;
import com.ipph.migratecore.transformer.TableTransformer;
import com.ipph.migratecore.util.XmlUtil;

@Controller
@RequestMapping("/tables")
public class TableController extends BaseController{
	
	@Resource
	private TableService tableService;
	@Resource
	private TableTransformer tableTransformer;
	
	/**
	 * 跳转list页面
	 * @return
	 */
	@RequestMapping("/toList")
	public String toList(){
		return "tables/list";
	}
	/**
	 * 查看table列表
	 * @return
	 */
	@RequestMapping("/list")
	@ResponseBody
	public Response list(@RequestParam(value="pageNum",defaultValue="0")Integer pageNum,@RequestParam(value="pageSize",defaultValue="20")Integer pageSize){
		
		Pageable pageable=PageRequest.of(pageNum-1, pageSize);
		
		Page<TableModel> result=tableService.getList(pageable);
		
		Map<String,Object> response=new HashMap<>();
		response.put("totalPage", result.getTotalPages());//总页数
		response.put("tableList", result.getContent());//获取数据集
		
		return result(ExceptionMsg.SUCCESS,response);
		
	}
	/**
	 * 跳转上传xml页面
	 * @return
	 */
	@RequestMapping("/toUploadXml")
	public String toUploadXml(){
		return "tables/uploadXml";
	}
	/**
	 * 上次xml配置文件
	 * @param file
	 * @return
	 */
	@RequestMapping("/uploadXml")
	@ResponseBody
	public Response uploadXml(@RequestParam("tableFile") MultipartFile file){
		if(file.isEmpty()) {
			return result(ExceptionMsg.FileEmpty);
		}
		
		String fileName = file.getOriginalFilename();
		try {
			//解析上传的文件并保存到数据库中
			tableService.uploadXml(file.getInputStream());
			
			//转存到文件系统中
			File f=new File(XmlUtil.getFilePath() +fileName);
			file.transferTo(f); //保存文件
		} catch (IOException e) {
			return result(ExceptionMsg.FAILED);
		}
		return result(ExceptionMsg.SUCCESS);
	}
	/**
	 * 删除
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/del/{tableId}")
	@ResponseBody
	public Response del(@PathVariable("tableId")Long tableId) {
		if(null!=tableId&&tableId!=0L) {
			tableService.del(tableId);
		}
		return result(ExceptionMsg.SUCCESS);
	}
	/**
	 * 添加表配置
	 * @return
	 */
	@RequestMapping("/add")
	public ModelAndView add(@RequestParam(value="tableId",defaultValue="0")Long tableId) {
		
		ModelAndView mv=new ModelAndView("/tables/add");
		
		mv.addObject("sourceTableList",tableService.getSourceTables()).addObject("targetTableList",tableService.getTargetTables())
			.addObject("tableId",tableId);
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
	public Response getTable(@RequestParam("fromTable") String fromTable,@RequestParam("toTable") String toTable){
		//获取数据对象
		Map<String,Object> tableData=new Hashtable<>();
		if(null!=fromTable) {
			tableData.put("fromTable",tableService.getMetaTableByName(true,fromTable));
		}
		if(null!=toTable) {
			tableData.put("toTable",tableService.getMetaTableByName(false, toTable));
		}
		return result(ExceptionMsg.SUCCESS,tableData);
	}
	/**
	 * 保存表
	 */
	@RequestMapping(value="/saveTable",method=RequestMethod.POST,consumes=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public Response saveTable(@RequestBody TableModel table) {
		tableService.saveTableJson(table);
		return result(ExceptionMsg.SUCCESS);
	}
	
	/**
	 * 查看table定义信息
	 * @param tableId
	 * @return
	 */
	@RequestMapping("/info/{tableId}")
	@ResponseBody
	public Response info(@PathVariable("tableId")Long tableId) {
		
		TableModel tableModel=tableService.getById(tableId);
		/*if(null!=tableModel) {
			tableTransformer.parseTable(tableModel);
		}*/
		Map<String,Object> data=new HashMap<>();
		if(null!=tableModel) {
			//获取from表和to表信息
			data.put("fromTable",tableService.getMetaTableByName(true,tableModel.getFrom()));
			data.put("toTable",tableService.getMetaTableByName(false,tableModel.getTo()));
			data.put("table", tableModel);
		}
		return result(ExceptionMsg.SUCCESS,data);
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
