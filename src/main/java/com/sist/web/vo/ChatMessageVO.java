package com.sist.web.vo;

import lombok.Data;
import lombok.Getter;

@Data
public class ChatMessageVO {
	private String sender,receiver,message;
}
