package com.sist.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // 화면 변경
public class RouterController {
	
	@GetMapping("/")
	public String main_main(Model model)
	{
		model.addAttribute("main_html","main/home");
		return "main/main";
	}
}
