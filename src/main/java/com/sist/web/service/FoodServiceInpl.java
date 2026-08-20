package com.sist.web.service;

import java.util.List;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

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

		fMapper.foodHitIncrement(no);
		return fMapper.foodDetailData(no);
	}

	/*
	 *  1. Mapper : 재료 (DB연동)
	 *  	- JDBC / Mybatis / JPA
	 *  2. Controller / restController : 서빙 (브라우저로 전송)
	 *  	 |				 |
	 *   	화면 변경		 vue / react로 값을 전송
	 *  3. Service : 쉐프 (요청 처리)
	 *  	| DB + OpenAPI
	 */
	@Override
	public int[] foodPages(int page) {
		final int BLOCK = 10;
		int totalpage = fMapper.foodTotalPage();
		int startPage = ((page-1)/BLOCK*BLOCK)+1;
		int endPage = ((page-1)/BLOCK*BLOCK)+BLOCK;
		if(endPage > totalpage)
			endPage = totalpage;
		
		int[] pages = {page,totalpage,startPage,endPage};
		
		return pages;
	}
	
	
}
