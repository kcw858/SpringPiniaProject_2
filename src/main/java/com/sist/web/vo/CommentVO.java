package com.sist.web.vo;
import java.util.*;

import lombok.Data;

@Data
public class CommentVO {
	private int no,fno,page;
	private String id,name,msg,dbday;
	private Date regdate;
}
