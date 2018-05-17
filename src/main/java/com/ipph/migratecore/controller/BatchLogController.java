package com.ipph.migratecore.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.model.BatchLogModel;
import com.ipph.migratecore.model.BatchModel;
import com.ipph.migratecore.service.BatchLogService;
import com.ipph.migratecore.service.BatchService;

@Controller
@RequestMapping("/logs")
public class BatchLogController {
	@Resource
	private BatchLogService batchLogService;
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
		
		mv.addObject("batchLogList",batchLogService.getBatchLogByparentIdIsNull(batchId));
		
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
}
