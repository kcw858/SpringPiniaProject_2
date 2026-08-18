package com.sist.web.vo;

import lombok.Data;

/*
USERID             VARCHAR2(20) 
AUTHORITY NOT NULL VARCHAR2(20)
 */
@Data
public class AuthorityVO {
	private String userid,authority; //권한 -> ROLE_ADMIN (자동으로 ADMIN만 인식)
}
