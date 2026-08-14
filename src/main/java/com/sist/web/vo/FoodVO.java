package com.sist.web.vo;

import lombok.Data;
/*
 NO                                        NOT NULL NUMBER
 NAME                                               VARCHAR2(100)
 TYPE                                               VARCHAR2(100)
 PHONE                                              VARCHAR2(30)
 ADDRESS                                            VARCHAR2(260)
 SCORE                                              NUMBER(2,1)
 PARKING                                            VARCHAR2(200)
 POSTER                                             VARCHAR2(260)
 TIME                                               VARCHAR2(50)
 CONTENT                                            CLOB
 THEME                                              VARCHAR2(4000)
 PRICE                                              VARCHAR2(100)
 LIKECOUNT                                          NUMBER
 JJIMCOUNT                                          NUMBER
 HIT                                                NUMBER
 REPLYCOUNT                                         NUMBER
 
 */
@Data
public class FoodVO {
	private int no;
	private int likecount,jjimcount,hit,replycount;
	private String name,address,phone,parking,poster,time,content,price,type,theme;
	private double score;
}
