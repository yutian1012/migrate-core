package com.ipph.migratecore.controller;

import java.io.OutputStream;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.common.ExceptionMsg;
import com.ipph.migratecore.common.Response;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.model.TableModel;
import com.ipph.migratecore.patent.util.DateFormatUtil;
import com.ipph.migratecore.service.BatchLogService;
import com.ipph.migratecore.service.BatchService;
import com.ipph.migratecore.service.TableService;

@Controller
@RequestMapping("/logs")
public class BatchLogController extends BaseController{
	@Resource
	private BatchLogService batchLogService;
	@Resource
	private BatchService batchService;
	@Resource
	private TableService tableService;
	
	/**
	 * 跳转到正在执行批次列表
	 * @return
	 */
	@RequestMapping("/toBatchLog")
	public String toBatchLog() {
		return "logs/batchLog";
	}
	/**
	 * 获取正在执行的批次集合
	 * @return
	 */
	@RequestMapping("batchLog/list")
	@ResponseBody
	private Response batchLogList(@RequestParam(value="pageNum",defaultValue="0")Integer pageNum,@RequestParam(value="pageSize",defaultValue="20")Integer pageSize,
			@RequestParam(value="isProcessing",defaultValue="false")boolean isProcessing) {
		Pageable pageable=PageRequest.of(pageNum-1, pageSize);
		
		Page<BatchLogModel> result=batchLogService.getList(isProcessing,pageable);
		
		Map<String,Object> response=new HashMap<>();
		if(null!=result) {
			response.put("totalPage", result.getTotalPages());//总页数
			response.put("batchLogList", result.getContent());//获取数据集
		}
		
		return result(ExceptionMsg.SUCCESS,response);
	}
	
	/**
	 * 跳转到已执行的批次列表
	 * @return
	 */
	@RequestMapping("/toBatchLoged")
	public String toBatchLoged() {
		return "logs/batchLoged";
	}
	
	/**
	 * 批次执行日志信息
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/batch/{batchId}")
	public ModelAndView getBatchLog(@PathVariable("batchId")Long batchId) {
		ModelAndView mv=new ModelAndView("logs/batchLog");
		
		mv.addObject("batchLogList",batchLogService.getBatchLogByparentIdIsNull(batchId));
		
		return mv;
	}
	/**
	 * 子批次查询记录
	 * @param batchId
	 * @param batchLogId
	 * @return
	 */
	@RequestMapping("/batch/sub/{batchId}/{batchLogId}")
	public ModelAndView getSubBatchLog(@PathVariable("batchId")Long batchId,@PathVariable("batchLogId")Long batchLogId) {
		ModelAndView mv=new ModelAndView("logs/sub/batchLog");
		
		mv.addObject("batchLogList",batchLogService.getByBatchIdAndParentId(batchId,batchLogId));
		
		return mv;
	}
	
	/**
	 * 批次日志中该批次的表集合，该批次中的每个表都有相应的执行日志
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/tables/{batchLogId}")
	public ModelAndView getTablesLog(@PathVariable("batchLogId")Long batchLogId) {
		ModelAndView mv=new ModelAndView("logs/tablesLog");
		
		BatchLogModel batchLogModel=batchLogService.getById(batchLogId);
		
		BatchModel batchModel=null;
		
		if(null!=batchLogModel) {
			batchModel=batchService.getBatch(batchLogModel.getBatchId());
		}
		
		mv.addObject("batchLogId",batchLogId).addObject("batch",batchModel);
		
		return mv;
	}
	
	/**
	 * 导出sql语句
	 * @throws Exception 
	 */
	@RequestMapping("exportSql/{batchLogId}/{tableId}")
	public void exportSql(@PathVariable("batchLogId")Long batchLogId,@PathVariable("tableId")Long tableId,
			HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		String fileName=null;
		
		TableModel table=tableService.getById(tableId);
		if(null==table) {
			return;
		}
		
		fileName=table.getNote()+DateFormatUtil.format(new Date());
		
		boolean isApply=true;
		
		if(table.getNote().indexOf("申请")==-1) {
			isApply=false;
		}
		
		boolean isPct=true;
		
		if(table.getNote().indexOf("PCT")==-1) {
			isPct=false;
		}
		
		String sql=null;
		
		if(!isPct) {
			sql=batchLogService.getSuccessExecuteSql(batchLogId,tableId,isApply);
		}else {
			//pct数据有2种来源，一种是通过补充的上传专利；另外一种是通过日志信息获取原表信息
			boolean isSource=false;
			if("tpatentallo".equals(table.getFrom())) {
				isSource=true;
			}
			sql=batchLogService.getPctSuccessExecuteSql(batchLogId, tableId, isApply,isSource);
		}
		
		OutputStream os=getOutputStream(request, response, fileName);
		
		if(null!=sql) {
			os.write(sql.getBytes());
		}
		
	}
	
	/**
	 * 获取输出流
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public OutputStream getOutputStream(HttpServletRequest request,HttpServletResponse response,String filename) throws Exception {
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/plain; charset=utf-8");
        response.setHeader("content-disposition","attachment;filename=" + getFileName(request.getHeader("User-Agent").toUpperCase(), filename));
        return response.getOutputStream();
	}
	
	/**
	 * 获取文件名
	 * @param browser
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public static String getFileName(String browser,String fileName)throws Exception{
        if(browser.indexOf("MSIE")!=-1){//IE浏览器
            fileName = new String((fileName+".sql").getBytes("gb2312"), "ISO8859-1");
        }else if(browser.indexOf("FIREFOX")!=-1){//google,火狐浏览器，会自动添加文件扩展名。
            fileName = "=?UTF-8?B?" + (new String (Base64.getEncoder().encode((fileName+".sql").getBytes("UTF-8")))) + "?=";  //火狐文件名空格被截断问题
        }else{
            fileName = new String((fileName+".sql").getBytes("gb2312"), "ISO8859-1");
        }
        return fileName;
	}
}
