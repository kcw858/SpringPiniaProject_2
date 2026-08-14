package com.sist.web.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sist.web.mapper.FoodMapper;
import com.sist.web.vo.FoodVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodServiceInpl implements FoodService{
	private final FoodMapper fMapper;

	@Override
	public List<FoodVO> foodListData(int page) {
		
		int start = (page*12)-12;
		
		return fMapper.foodListData(start);
	}

	@Override
	public int foodTotalPage() {
		
		return fMapper.foodTotalPage();
	}

	@Override
	public FoodVO foodDetailData(int no) {

		return fMapper.foodDetailData(no);
	}

	@Override
	public int[] foodPages(int page) {
		
		int[] pages = {};
		
		return pages;
	}
}
