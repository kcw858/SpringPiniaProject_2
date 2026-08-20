package com.sist.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.sist.web.vo.MemberVO;

@Mapper
@Repository
public interface MemberMapper {
	@Select("SELECT * FROM springmember "
			+ "WHERE userid=#{userid}")
	public MemberVO memberInfoData(String userid);
}
