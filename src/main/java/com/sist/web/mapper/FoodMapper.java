package com.sist.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
/*
 *  스프링에 메모리 할당
 *  1. @Repository : 데이터베이스 연동 => 구분 (저장소)
 *  2. @Service: 요청 처리 => BI
 *  3. @Controller: 화면 변경
 *     @RestController: JavaScript(Ajax,Axios,fetch) 결과값 전송 => JSON / 일반 문자열
 *  4. @Component : AOP / Manager => 일반 클래스
 *  5. @ControllerAdvicce
 *     @RestControllerAdvice ==> 예외처리
 *  6. @Configuration : 자바 환경설정
 *  	 = 스프링 보안 (JWT)
 *   	 = WebSocket 설정
 *   	 = QueryDSL 설정
 *   ------------------------------------------- @Component인데 구분을 하고 있는 것이다
 */

import com.sist.web.vo.FoodVO;
@Mapper
@Repository
public interface FoodMapper {

	/*
	<select id="foodListData" resultType="com.sist.web.vo.FoodVO" parameterType="int">
		SELECT no,name,poster,address
		FROM food
		ORDER BY no ASC
		OFFSET #{start} ROWS FETCH NEXT 12 ROWS ONLY
	</select>
	 */
	public List<FoodVO> foodListData(int start);
	
	//총페이지
	@Select("SELECT CEIL(COUNT(*)/12.0) FROM food")
	public int foodTotalPage();
	
	//상세보기
	@Select("SELECT * FROM food WHERE no=#{no}")
	public FoodVO foodDetailData(int no);
	
	@Update("UPDATE food SET hit=hit+1 WHERE no=#{no}")
	public void foodHitIncrement(int no);
	
}