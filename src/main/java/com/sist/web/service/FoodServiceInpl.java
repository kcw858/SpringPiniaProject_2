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
	




		@Override
	    public String aaa() {
	        String clientId = "lM3nVlpLHWBh78njBHFa"; //애플리케이션 클라이언트 아이디
	        String clientSecret = "EcQjAxGDhY"; //애플리케이션 클라이언트 시크릿


	        String text = null;
	        try {
	        	text = URLEncoder.encode("감자", StandardCharsets.UTF_8);
	            //text = URLEncoder.encode("감자", "UTF-8");
	        } catch (Exception e) {
	            throw new RuntimeException("검색어 인코딩 실패",e);
	        }


	        String apiURL =
	                "https://openapi.naver.com/v1/search/shop.json"
	                + "?query=" + text
	                + "&display=10"
	                + "&start=1"
	                + "&sort=sim";    // JSON 결과
	        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // XML 결과


	        Map<String, String> requestHeaders = new HashMap<>();
	        requestHeaders.put("X-Naver-Client-Id", clientId);
	        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
	        String responseBody = get(apiURL,requestHeaders);


	        return responseBody;
	    }


	    private static String get(String apiUrl, Map<String, String> requestHeaders){
	        HttpURLConnection con = connect(apiUrl);
	        try {
	            con.setRequestMethod("GET");
	            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
	                con.setRequestProperty(header.getKey(), header.getValue());
	            }


	            int responseCode = con.getResponseCode();
	            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
	                return readBody(con.getInputStream());
	            } else { // 오류 발생
	                return readBody(con.getErrorStream());
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("API 요청과 응답 실패", e);
	        } finally {
	            con.disconnect();
	        }
	    }


	    private static HttpURLConnection connect(String apiUrl){
	        try {
	            URL url = new URL(apiUrl);
	            return (HttpURLConnection)url.openConnection();
	        } catch (MalformedURLException e) {
	            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
	        } catch (IOException e) {
	            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
	        }
	    }


	    private static String readBody(InputStream body){
	    	InputStreamReader streamReader =
	    	        new InputStreamReader(body, StandardCharsets.UTF_8);

	        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
	            StringBuilder responseBody = new StringBuilder();


	            String line;
	            while ((line = lineReader.readLine()) != null) {
	                responseBody.append(line);
	            }


	            return responseBody.toString();
	        } catch (IOException e) {
	            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
	        }
	    }
	
}
