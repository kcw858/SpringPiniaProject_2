package com.sist.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sist.web.entity.BootBoard;
import com.sist.web.service.BoardServiceImpl;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class BoardController {
	private final BoardServiceImpl bDao;
	
	@GetMapping("/board/list")
	public String board_list(@RequestParam(value = "page",required = false)String page, Model model)
	{
		if(page == null)
			page = "1";
		
		int curpage = Integer.parseInt(page);
		int rowSize = 10;
		
		Pageable pg = PageRequest.of(curpage-1, rowSize,Sort.by(Sort.Direction.DESC,"no")); //OFFSET ORDER BY
		
		Page<BootBoard> pList = bDao.findAll(pg);
		List<BootBoard> list = new ArrayList<BootBoard>();
		
		if(pList != null && pList.hasContent())
		{
			list = pList.getContent();
		}
		
		//총페이지
		int totalpage = bDao.boardTotalPage();
		
		model.addAttribute("list",list);
		model.addAttribute("curpage",curpage);
		model.addAttribute("totalpage",totalpage);
		model.addAttribute("main_html", "board/list");
		return "main/main";
	}
	
	@GetMapping("board/insert")
	public String board_insert(Model model)
	{
		model.addAttribute("main_html", "board/insert");
		return "main/main";
	}
	
	@PostMapping("/board/insert_ok")
	public String board_insert_ok(@ModelAttribute("vo") BootBoard vo)
	{
		bDao.save(vo);
		return "redirect:/board/list";
	}
	
	@GetMapping("board/detail")
	public String board_detail(@RequestParam("no")int no,Model model)
	{
		BootBoard vo = bDao.findByNo(no);
		vo.setHit(vo.getHit()+1);
		bDao.save(vo); //조회수 증가
		vo=bDao.findByNo(no);
		
		model.addAttribute("no", no);
		model.addAttribute("vo", vo);
		model.addAttribute("main_html", "board/detail");
		return "main/main";
	}
}
