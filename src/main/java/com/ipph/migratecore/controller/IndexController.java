package com.ipph.migratecore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
	/*@RequestMapping("/")
	public String index() {
		return "index";
	}*/
	
	@RequestMapping("/home")
	public String home() {
		return "home";
	}
	
	@RequestMapping("/layout")
	public String layout() {
		return "fragments/layout";
	}
}
