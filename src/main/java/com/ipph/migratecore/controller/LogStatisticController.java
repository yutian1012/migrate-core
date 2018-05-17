package com.ipph.migratecore.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ipph.migratecore.service.LogStatisticService;

@Controller
@RequestMapping("/logs")
public class LogStatisticController {
	@Resource
	private LogStatisticService logStatisticService;
	
	/**
	 * 获取统计信息
	 * @return
	 */
	@RequestMapping("/statistic")
	@ResponseBody
	public Map<String,Object> statistic(@RequestParam("batchLogId")Long batchLogId,@RequestParam("tableId")Long tableId){
		Map<String,Object> result=logStatisticService.statistic(batchLogId,tableId);
		Map<String,Object> subResult=logStatisticService.statisticByParentBatchLog(batchLogId, tableId);
		if(null!=subResult&&subResult.size()>0) {
			if(subResult.containsKey("SUCCESS")) {
				result.put("SUBSUCCESS",subResult.get("SUCCESS"));
			}
		}
		
		return result;
	}
	
	/**
	 * 获取统计信息
	 * @return
	 */
	@RequestMapping("/statistic/error")
	@ResponseBody
	public Map<String,Object> errorStatistic(@RequestParam("batchLogId")Long batchLogId,@RequestParam("tableId")Long tableId){
		Map<String,Object> result=logStatisticService.errorstatistic(batchLogId,tableId);
		
		return result;
	}
}
