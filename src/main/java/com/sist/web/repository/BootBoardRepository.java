package com.sist.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sist.web.entity.BootBoard;
import java.util.List;


public interface BootBoardRepository extends JpaRepository<BootBoard, Integer>
{
	public BootBoard findByNo(int no); //상세보기
}
