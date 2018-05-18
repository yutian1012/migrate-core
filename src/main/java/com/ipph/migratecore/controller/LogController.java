package com.ipph.migratecore.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.enumeration.LogMessageEnum;
import com.ipph.migratecore.enumeration.LogStatusEnum;
import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.service.BatchLogService;
import com.ipph.migratecore.service.BatchService;
import com.ipph.migratecore.service.LogService;

@Controller
@RequestMapping("/logs")
public class LogController {
	
	@Resource
	private BatchLogService batchLogService;
	@Resource
	private LogService logService;
	@Resource
	private BatchService batchService;
	
	/**
	 * 表的执行日志信息
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/table/{batchLogId}/{tableId}")
	public ModelAndView getTableLog(@PathVariable("batchLogId")Long batchLogId,
			@PathVariable("tableId")Long tableId,
			@RequestParam(value="size",defaultValue="20")int size,
			@RequestParam(value="page",defaultValue="0")int page,
			HttpServletRequest request) {
		
		ModelAndView mv=new ModelAndView("logs/log");
		
		String status=request.getParameter("status");
		if("".equals(status)) {
			status=null;
		}
		String messageType=request.getParameter("messageType");
		if("".equals(messageType)) {
			messageType=null;
		}
		
		Pageable pageable=PageRequest.of(page, size);
		
		BatchLogModel batchLogModel=batchLogService.getById(batchLogId);
		
		List<LogModel> tableLogList=logService.getLogs(batchLogId, tableId,
				null!=status?LogStatusEnum.valueOf(status):null,
				null!=messageType?LogMessageEnum.valueOf(messageType):null,pageable);
		
		mv.addObject("tableLogList",tableLogList).addObject("batchLog",batchLogModel)
			.addObject("tableId",tableId).addObject("pageable",pageable);
		
		return mv;
	}
	/**
	 * 查看成功的数据(包含多个批次的累计成功数据)
	 * @param batchLogId
	 * @param tableId
	 * @param size
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping("/table/success/{batchLogId}/{tableId}")
	public ModelAndView getTableSuccessLog(@PathVariable("batchLogId")Long batchLogId,
			@PathVariable("tableId")Long tableId,
			@RequestParam(value="size",defaultValue="20")int size,@RequestParam(value="page",defaultValue="0")int page,
			HttpServletRequest request) {
		
		ModelAndView mv=new ModelAndView("logs/log");
		
		Pageable pageable=PageRequest.of(page, size);
		
		BatchLogModel batchLogModel=batchLogService.getById(batchLogId);
		
		List<LogModel> tableLogList=logService.getSuccessLogs(batchLogId,tableId,pageable);
		
		mv.addObject("tableLogList",tableLogList).addObject("batchLog",batchLogModel)
			.addObject("tableId",tableId).addObject("pageable",pageable);
		
		return mv;
	}
	
	/**
	 * 导出数据
	 * @param batchLogId
	 * @param tableId
	 * @param size
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping("/table/success/export/{batchLogId}/{tableId}")
	public ModelAndView exportSuccess(@PathVariable("batchLogId")Long batchLogId,
			@PathVariable("tableId")Long tableId,
			@RequestParam(value="size",defaultValue="20")int size,
			@RequestParam(value="page",defaultValue="0")int page,
			HttpServletRequest request,HttpServletResponse response) {
		
		Pageable pageable=PageRequest.of(page, size);
		
		List<LogModel> tableLogList=logService.getSuccessLogs(batchLogId,tableId,pageable);
		Map<String, Object> model=new HashMap<>();
		
		model.put("tableLogList", tableLogList);
		
		return new ModelAndView(new JxlsExcelView("/export/logexport.xls", "导出执行日志"),model);
	}
	
	/**
	 * 查看错误的数据
	 * @param batchLogId
	 * @param tableId
	 * @param size
	 * @param page
	 * @param request
	 * @return
	 */
	@RequestMapping("/table/fail/{batchLogId}/{tableId}")
	public ModelAndView getTableFailLog(@PathVariable("batchLogId")Long batchLogId,
			@PathVariable("tableId")Long tableId,@RequestParam(value="size",defaultValue="20")int size,@RequestParam(value="page",defaultValue="0")int page,
			HttpServletRequest request) {
		
		ModelAndView mv=new ModelAndView("logs/log");
		
		Pageable pageable=PageRequest.of(page, size);
		
		BatchLogModel batchLogModel=batchLogService.getById(batchLogId);
		
		List<LogModel> tableLogList=logService.getFailLogs(batchLogId, tableId,pageable);
		
		mv.addObject("tableLogList",tableLogList).addObject("batchLog",batchLogModel)
			.addObject("tableId",tableId).addObject("pageable",pageable);
		
		return mv;
	}
	
}
