package com.sist.web.restcontroller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import com.sist.web.mapper.*;

@RestController
@RequiredArgsConstructor
public class BoardCommentRestController {
	private final BoardCommentMapper bMapper;
	private final SimpMessagingTemplate template;
	
	// => insert,update,delete ===> 화면데이터 갱신
	public Map commonsListData(int page,int board_no)
	{
		Map map = new HashMap();
		
		int start = (page*10)-10;
		map.put("start", start);
		map.put("board_no", board_no);
		
		List<BootCommentVO> list = bMapper.boardCommentListData(map);
		
		int count = bMapper.boardCommentCount(board_no);
		
		int totalpage = (int)(Math.ceil(count/10.0));
		
		map = new HashMap();
		
		map.put("list", list);
		map.put("curpage", page);
		map.put("totalpage", totalpage);
		map.put("count", count);
		
		return map;
	}
	
	@Async
	@GetMapping("/reply/list_vue")
	public ResponseEntity<Map> reply_list_vue(@RequestParam("board_no")int board_no,@RequestParam("page")int page)
	{
		Map map = new HashMap();
		
		try
		{
			map = commonsListData(page,board_no);
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
	
	@Async
	@PostMapping("/reply/insert_vue")
	/*
	 *  @RequestBody : JSON을 객체형
	 *  @ResponseBody : 객체를ㄹ JSON으로 변경해서 브라우저로 전송
	 */
	public ResponseEntity<Map> reply_insert_vue(@RequestBody BootCommentVO vo,HttpSession session)
	{
		System.out.println(vo);
		Map map = new HashMap();
		try
		{
			String id = (String)session.getAttribute("userid");
			String name = (String)session.getAttribute("username");
			
			vo.setId(id);
			vo.setName(name);
			
			bMapper.boardCommentInsert(vo);
			
			map = commonsListData(vo.getPage(),vo.getBoard_no());
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("/reply/reply_reply_insert_vue")
	public ResponseEntity<Map> reply_reply_insert_vue(@RequestBody BootCommentVO vo,HttpSession session)
	{
		Map map = new HashMap();
		
		try
		{
			//상위 댓글의 정보 읽기
			BootCommentVO pvo = bMapper.boardParentInfoData(vo.getNo());
			bMapper.boardGroupStepIncrement(pvo.getGroup_id(), pvo.getGroup_step());
			vo.setGroup_id(pvo.getGroup_id());
			vo.setGroup_step(pvo.getGroup_step()+1);
			vo.setGroup_tab(pvo.getGroup_tab()+1);
			vo.setRoot(vo.getNo());
			String id = (String)session.getAttribute("userid");
			String name = (String)session.getAttribute("username");
			
			vo.setId(id);
			vo.setName(name);
			bMapper.boardCommentReReply(vo);
			bMapper.boardDepthIncrement(vo.getNo());
			
			if(!pvo.getId().equals(vo.getId()))
			{
				//본인댓글이 아니면 
				template.convertAndSend(
						"/sub/notice/"+pvo.getId(),
						"[🎉댓글 알람]"+vo.getId()+"님이 댓글을 달았습니다!!"
				);
			}
			
			map = commonsListData(vo.getPage(), vo.getBoard_no());
		}catch(Exception ex)
		{
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		
		return ResponseEntity.ok(map);
	}
}
