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
import com.ipph.migratecore.model.LogModel;
import com.ipph.migratecore.model.PatentInfoCheck;
import com.ipph.migratecore.service.PatentInfoCheckService;

@Controller
@RequestMapping("patent")
public class PatentController {
	
	@Resource
	private PatentInfoCheckService patentInfoCheckService;
	
	@RequestMapping("/check/{batchLogId}")
	public ModelAndView check(@PathVariable("batchLogId")Long batchLogId,
			@RequestParam(value="size",defaultValue="20")int size,
			@RequestParam(value="page",defaultValue="0")int page) {
		
		//判断是否已经处理过了
		boolean exists=patentInfoCheckService.existsByBatchLogId(batchLogId);
		
		if(!exists) {
			patentInfoCheckService.check(batchLogId);
		}
		
		//获取统计信息
		Pageable pageable=PageRequest.of(page, size);
		
		List<PatentInfoCheck> result=patentInfoCheckService.getList(batchLogId, pageable);
		
		ModelAndView mv=new ModelAndView("/patent/check");
		
		mv.addObject("checkResult",result).addObject("pageable",pageable).addObject("batchLogId",batchLogId);
		
		return mv;
	}
	
	@RequestMapping("/check/result/{batchLogId}")
	public ModelAndView exportPatentProcessResult(@PathVariable("batchLogId")Long batchLogId) {
		
		Pageable pageable=null;//PageRequest.of(0, 20);
		
		//申请数据
		List<PatentInfoCheck> applyFormatList=patentInfoCheckService.getListByBatchLogIdAndTypeAndCostTypeIn(batchLogId,LogMessageEnum.FORMART_EXCEPTION,new String[] {"SQ1","SQ3"},pageable);
		List<PatentInfoCheck> applyNotFoundList=patentInfoCheckService.getListByBatchLogIdAndTypeAndCostTypeIn(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,new String[] {"SQ1","SQ3"},pageable);
		//授权数据
		List<PatentInfoCheck> authFormatList=patentInfoCheckService.getListByBatchLogIdAndTypeAndCostTypeIn(batchLogId,LogMessageEnum.FORMART_EXCEPTION,new String[] {"SQ2","SQ4"},pageable);
		List<PatentInfoCheck> authNotFoundList=patentInfoCheckService.getListByBatchLogIdAndTypeAndCostTypeIn(batchLogId,LogMessageEnum.NOFOUND_EXCEPTION,new String[] {"SQ2","SQ4"},pageable);
		
		Map<String,Object> model=new HashMap<>();
		model.put("applyFormatList", applyFormatList);
		model.put("applyNotFoundList", applyNotFoundList);
		model.put("authFormatList", authFormatList);
		model.put("authNotFoundList", authNotFoundList);
		
		return new ModelAndView(new JxlsExcelView("/export/patentInfoCheck.xls", "导出执行日志"),model);
	}
}
