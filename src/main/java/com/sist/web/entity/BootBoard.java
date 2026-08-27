package com.sist.web.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/*
NO      NOT NULL NUMBER         
NAME    NOT NULL VARCHAR2(51)   
SUBJECT NOT NULL VARCHAR2(4000) 
CONTENT NOT NULL CLOB           
PWD     NOT NULL VARCHAR2(10)   
REGDATE          DATE           
HIT              NUMBER 
 */
@Entity
@Table(name = "bootboard")
@DynamicUpdate
@Data
//=> SQLL 자동처리 => save() => INSERT...
public class BootBoard {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) //자동 증가번호 설정
	//@GeneratedValue(strategy = GenerationType.SEQUENCE,generator="seq명") //기존 시퀀스 사용 
	private int no;
	
	private String name,subject,content;
	
	@Column(insertable = true,updatable = false)
	private String pwd;
	
	private int hit;
	
	@Column(insertable = true,updatable = false,name = "regdate")
	private LocalDateTime regdate;
	
	@PrePersist
	public void persist()
	{
		regdate = LocalDateTime.now();
	}
}
