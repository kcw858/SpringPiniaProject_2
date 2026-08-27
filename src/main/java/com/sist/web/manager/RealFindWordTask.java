package com.sist.web.manager;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RealFindWordTask {
	@Async //비동기
	@Scheduled(fixedRate = 60*3*1000) //3분
	public void task()
	{
		
	}
}
