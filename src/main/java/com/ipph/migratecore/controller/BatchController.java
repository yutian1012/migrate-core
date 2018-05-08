package com.ipph.migratecore.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.ipph.migratecore.service.BatchService;

@Controller
@RequestMapping("/batches")
public class BatchController {
	
	@Resource
	private BatchService batchService;
	
	@RequestMapping("/list")
	public ModelAndView list() {
		ModelAndView mv=new ModelAndView("batches/list");
		
		mv.addObject("batchList",batchService.getList());
		return mv;
	}
	
	@RequestMapping("/toAdd")
	public String toAdd() {
		
		return "batches/add";
	}
	
	@RequestMapping("/add")
	public String add(String batchName,String tables) {
		
		if(null==batchName||"".equals(batchName.trim())||null==tables||"".equals(tables.trim())) {
			//校验错误
		}
		
		String[] tableList=tables.split(";");
		
		batchService.add(batchName, tableList);
		
		return "redirect:/batches/list";
	}
	
	
	@RequestMapping("/info/{batchId}")
	public ModelAndView getBatch(@PathVariable("batchId")Long batchId) {
		ModelAndView mv=new ModelAndView("batches/info");
		
		mv.addObject("batch",batchService.getBatch(batchId));
		
		return mv;
	}
	/**
	 * 执行批次
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/migrate/{batchId}")
	public ModelAndView migrate(@PathVariable("batchId")Long batchId) {
		
		ModelAndView mv=new ModelAndView("forward:/batches/info");
			
		batchService.migrate(batchId);
		
		mv.addObject("batchId",batchId);
		
		return mv;
	}
}
