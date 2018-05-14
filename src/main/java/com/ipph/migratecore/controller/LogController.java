package com.ipph.migratecore.controller;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.BatchModel;
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
	 * 批次执行日志信息
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/batch/{batchId}")
	public ModelAndView getBatchLog(@PathVariable("batchId")Long batchId) {
		ModelAndView mv=new ModelAndView("logs/batchLog");
		
		mv.addObject("batchLogList",batchLogService.getBatchLog(batchId));
		
		return mv;
	}
	
	/**
	 * 表的执行日志信息
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
	 * 表的执行日志信息
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/table/{batchLogId}/{tableId}")
	public ModelAndView getTableLog(@PathVariable("batchLogId")Long batchLogId,
			@PathVariable("tableId")Long tableId,@RequestParam(value="size",defaultValue="20")int size,@RequestParam(value="page",defaultValue="1")int page) {
		
		ModelAndView mv=new ModelAndView("logs/log");
		
		Pageable pageable=PageRequest.of(page, size);
		
		BatchLogModel batchLogModel=batchLogService.getById(batchLogId);
		List<LogModel> tableLogList=logService.getLogs(batchLogId, tableId,pageable);
		
		mv.addObject("tableLogList",tableLogList).addObject("batchLog",batchLogModel)
			.addObject("tableId",tableId).addObject("pageable",pageable);
		
		return mv;
	}
}
