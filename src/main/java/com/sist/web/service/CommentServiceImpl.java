package com.sist.web.service;

import org.springframework.stereotype.Service;
import java.util.*;
import com.sist.web.mapper.*;
import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
	private final CommentMapper mapper;
	
	@Override
	public List<CommentVO> commentListData(int start,int fno) {
		// TODO Auto-generated method stub
		return mapper.commentListData(start,fno);
	}

	@Override
	public int commentRowCount(int fno) {
		// TODO Auto-generated method stub
		return mapper.commentRowCount(fno);
	}

	@Override
	public void commentInsert(CommentVO vo) {
		// TODO Auto-generated method stub
		mapper.commentInsert(vo);
	}

}
