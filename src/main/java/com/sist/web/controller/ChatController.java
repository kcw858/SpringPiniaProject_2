package com.sist.web.controller;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import com.sist.web.vo.ChatMessageVO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
@Controller
@RequiredArgsConstructor
/*
 *	
 */
public class ChatController {

    private final SimpMessagingTemplate template;
    // 접속자 저장 공간 
    private final Set<String> users=
    		ConcurrentHashMap.newKeySet();
    @MessageMapping("/chat/public")
    // => HttpSession을 포함하면 안된다 (GetMapping)
    @SendTo("/topic/chat")
    public ChatMessageVO publicChat(
    		ChatMessageVO msg,
            Principal p) {

        msg.setSender(p.getName());

        return msg;
    }

    @MessageMapping("/chat/private")
    public void privateChat(
    		ChatMessageVO msg,
            Principal p) {

        String sender = p.getName();

        msg.setSender(sender);

        template.convertAndSendToUser(
                msg.getReceiver(),
                "/queue/chat",
                msg
        );

        template.convertAndSendToUser(
                sender,
                "/queue/chat",
                msg
        );
    }
    @MessageMapping("/chat/join")
    public void join(Principal p)
    {
    	String username=p.getName();
    	users.add(username);
    	template.convertAndSend(
    			"/topic/users",
    			users
    	);
    }
    @GetMapping("/chat/chat")
    public String chat_page(Model model) {
    	model.addAttribute("main_html", "chat/chat");
        return "main/main";
    }
}