package com.sist.web.service;

import java.util.List;

import com.sist.web.vo.FoodVO;

public interface FoodService {
	public List<FoodVO> foodListData(int page);
	
	public int foodTotalPage();
	
	public FoodVO foodDetailData(int no);
	
	public int[] foodPages(int page);
	
	public String aaa();

}
