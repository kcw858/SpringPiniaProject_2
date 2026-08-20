package com.sist.web.security;

import java.io.IOException;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class LoginFailHandler implements AuthenticationFailureHandler{

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		String errorMsg="";
		try
		{
			/*String id=request.getParameter("userid");
			String pwd=request.getParameter("userpwd");
			
			int count=mService.memberIdCheck(id);
			if(count==0)
			{
				errorMsg="아이디가 존재하지 않습니다!!";
			}
			else
			{
				MemberVO vo=mService.memberInfodData(id);
				if(encoder.matches(pwd,vo.getUserpwd()))
				{
					errorMsg="로그인되었습니다!!";
				}
				else
				{
					errorMsg="비밀번호가 틀입니다!!";
				}
			}*/
			
			if(exception instanceof BadCredentialsException)
			{
				errorMsg="아이디나 비밀번호가 틀립니다!!";
			}
			else if(exception instanceof InternalAuthenticationServiceException)
			{
				errorMsg="아이디나 비밀번호가 틀립니다!!";
			}
			else if(exception instanceof DisabledException)
			{
				errorMsg="휴먼 계정입니다!!";
			}
		}catch(Exception ex) {}
		request.setAttribute("message", errorMsg);
		request.getRequestDispatcher("/member/login").forward(request, response);
		
	}

	

}