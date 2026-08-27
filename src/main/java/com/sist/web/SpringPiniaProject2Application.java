package com.sist.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
/*
 *  1. DI
 *  2. AOP
 *  3. Exception
 *  4. Task / Spring-Batch
 *  5. WebSocket : SockJS , STOMP
 *  6. Spring-Security / JWT
 *  7. Front 연동
 *  8. ORM
 *  9. Kafka / Redis
 *  10. Spring AI
 */
@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class SpringPiniaProject2Application {

	public static void main(String[] args) {
		SpringApplication.run(SpringPiniaProject2Application.class, args);
	}

}
