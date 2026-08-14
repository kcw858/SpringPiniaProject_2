package com.sist.web.vo;

import lombok.Data;

@Data
public class RecipeVO {
	private int no;
	private String title,poster,chef,link;
	private int hit;
}
