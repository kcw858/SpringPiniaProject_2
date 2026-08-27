package com.sist.web.vo;
/*
NO         NOT NULL NUMBER       
BOARD_NO            NUMBER       
ID                  VARCHAR2(20) 
NAME       NOT NULL VARCHAR2(51) 
MSG        NOT NULL CLOB         
REGDATE             DATE         
GROUP_ID            NUMBER       
GROUP_STEP          NUMBER       
GROUP_TAB           NUMBER       
ROOT                NUMBER       
DEPTH               NUMBER       
 */
import java.util.*;

import lombok.Data;

@Data
public class BootCommentVO {
	private int no,board_no,group_id,group_step,group_tab,root,depth,page;
	private String id,name,msg,dbday;
	private Date regdate;
}
