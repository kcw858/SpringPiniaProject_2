package com.sist.web.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.sist.web.service.CommentService;

import lombok.RequiredArgsConstructor;
import java.util.*;
import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;

@RestController
@RequiredArgsConstructor
public class CommentRestController {
	private final CommentService cService;
	
	public Map commonsData(int page,int fno)
	{
		Map map = new HashMap();
		int start = (page*10)-10;
		List<CommentVO> list = cService.commentListData(start,fno);
		
		int count = cService.commentRowCount(fno);
		int totalpage = (int)(Math.ceil(count/10.0));
		
		map.put("rList", list);
		map.put("count", count);
		map.put("curpage", page);
		map.put("totalpage", totalpage);
		
		return map;
	}
	
	@GetMapping("/comment/list_vue")
	public ResponseEntity<Map> comment_list(@RequestParam("page")int page,@RequestParam("fno")int fno)
	{
		Map map = new HashMap();
		try
		{
			map = commonsData(page,fno);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	
	//{} => vo    JSON을 객체로 변경 : @RequestBody
	@PostMapping("/comment/insert_vue")
	public ResponseEntity<Map> comment_insert(@RequestBody CommentVO vo,HttpSession session)
	{
		Map map = new HashMap();
		try
		{
			String id = (String)session.getAttribute("userid");
			String name = (String)session.getAttribute("username");
			vo.setId(id);
			vo.setName(name);
			cService.commentInsert(vo);
			map = commonsData(vo.getPage(),vo.getFno());
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	@DeleteMapping("/comment/delete_vue")
	public ResponseEntity<Map> comment_delete(@RequestParam("no")int no,@RequestParam("page")int page,@RequestParam("fno")int fno)
	{
		Map map = new HashMap();
		try
		{
			cService.commentDelete(no);
			map = commonsData(page,fno);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	@PutMapping("/comment/update_vue")
	public ResponseEntity<Map> comment_update(@RequestBody CommentVO vo)
	{
		Map map = new HashMap();
		try
		{
			cService.commentUpdate(vo);
			map = commonsData(vo.getPage(),vo.getFno());
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
}
