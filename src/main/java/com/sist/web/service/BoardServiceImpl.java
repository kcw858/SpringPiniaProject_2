package com.sist.web.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.sist.web.entity.BootBoard;
import com.sist.web.repository.BootBoardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl {
	private final BootBoardRepository bDao;
	
	public Page<BootBoard> findAll(Pageable pg)
	{
		return bDao.findAll(pg);
	}
	
	public int boardTotalPage()
	{
		return (int)(Math.ceil(bDao.count()/10.0));
	}
	
	public BootBoard findByNo(int no)
	{
		return bDao.findByNo(no);
	}
	
	public void save(BootBoard vo)
	{
		bDao.save(vo);
	}
}
