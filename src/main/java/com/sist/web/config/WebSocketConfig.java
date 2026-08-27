package com.sist.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
/*
 *  interface
 *   => 과거 모든 메소드가 추상 메소드
 *   => 인터페이스는 고정을 한다
 *   	===================
 *   	-> 유지보수가 어렵다 ==> 구현된 메소드 추가 (default)
 *   
 *   interface A
 *   {
 *   	public void disp();			-> 반드시 구현
 *   	
 *   	public default void disp(); 
 *   
 *   	public static void d(){};
 *   }
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig
	implements WebSocketMessageBrokerConfigurer {
	
	@Override
	public void configureMessageBroker(
	    MessageBrokerRegistry registry) {
	
	// /topic => 전체 메세지 
	// /queue => 개인 메세지
	registry.enableSimpleBroker(
	        "/topic",
	        "/queue",
	        "/sub"
	);
	
	// 클라이언트에서 서버 요청 시  ex) /app/chat/public   /app/chat/private
	// 생략하고 인식
	registry.setApplicationDestinationPrefixes(
	        "/app",
	        "/pub"
	);
	
	// /user/queue/chat 
	registry.setUserDestinationPrefix(
	        "/user"
	);
	}
	
	// websocket 연결 주소 지정
	@Override
	public void registerStompEndpoints(
	    StompEndpointRegistry registry) {
	
	registry.addEndpoint("/chat-ws")
			// 모든 사람 접근이 가능
	        .setAllowedOriginPatterns("*")
	        .withSockJS();
	}
}
