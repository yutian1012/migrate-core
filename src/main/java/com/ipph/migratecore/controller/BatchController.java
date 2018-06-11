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
	/**
	 * 批次列表展示
	 * @return
	 */
	@RequestMapping("/list")
	public ModelAndView list() {
		ModelAndView mv=new ModelAndView("batches/list");
		
		mv.addObject("batchList",batchService.getList());
		return mv;
	}
	/**
	 * 跳转连接
	 * @return
	 */
	@RequestMapping("/toAdd")
	public String toAdd() {
		
		return "batches/add";
	}
	/**
	 * 新建批次
	 * @param batchName
	 * @param tables
	 * @return
	 */
	@RequestMapping("/add")
	public String add(String batchName,String tables) {
		
		if(null==batchName||"".equals(batchName.trim())||null==tables||"".equals(tables.trim())) {
			//校验错误
		}
		String[] tableList=tables.split(";");
		
		batchService.add(batchName, tableList);
		
		return "redirect:/batches/list";
	}
	
	/**
	 * 查看批次信息
	 * @param batchId
	 * @return
	 */
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
	public String migrate(@PathVariable("batchId")Long batchId) {
		
		batchService.migrate(batchId);
		
		return "redirect:/batches/info/"+batchId;
	}
	
	/**
	 * 执行子批次，子批次的执行是针对单独的表进行的
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/migrate/{batchId}/{parentId}/{tableId}")
	public String migrateTable(@PathVariable("batchId")Long batchId,@PathVariable("parentId")Long parentId,@PathVariable("tableId")Long tableId) {
		
		batchService.migrate(batchId,parentId,tableId);
		
		return "redirect:/batches/info/"+batchId;
	}
	/**
	 * 删除批次
	 * @param batchId
	 * @return
	 */
	@RequestMapping("/del/{batchId}")
	public String del(@PathVariable("batchId")Long batchId) {
		batchService.del(batchId);
		return "redirect:/batches/list";
	}
	
}
