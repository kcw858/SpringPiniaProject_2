package com.sist.web.vo;


/*
NO           NOT NULL NUMBER         
POSTER       NOT NULL VARCHAR2(260)  
TITLE        NOT NULL VARCHAR2(1000) 
CHEF         NOT NULL VARCHAR2(200)  
CHEF_POSTER           VARCHAR2(260)  
CHEF_PROFILE          VARCHAR2(200)  
INFO1        NOT NULL VARCHAR2(30)   
INFO2        NOT NULL VARCHAR2(30)   
INFO3        NOT NULL VARCHAR2(30)   
CONTENT               CLOB           
FOODMAKE     NOT NULL CLOB
 */
import lombok.Data;

@Data
public class RecipeDetailVO {

	private int no;
	private String poster,title,chef,chef_poster,chef_profile,info1,info2,info3,content,foodmake;
}
