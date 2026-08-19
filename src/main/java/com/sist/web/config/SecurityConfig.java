package com.sist.web.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.token.KeyBasedPersistenceTokenService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import com.sist.web.security.LoginFailHandler;
import com.sist.web.security.LoginSuccessHandler;

import lombok.RequiredArgsConstructor;

//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
/*
 *   1. Spring Security 
 *      = 보안을 담당하는 프레임워크 
 *        ---
 *        | 인증 / 인가 
 *          ---   ---
 *                Authorization : 사용자가 누구인지 확인 절차 = 로그인 
 *          Authentication :  인증된 사용자가 사이트에 접근 가능한지 권한 확인 
 *        | 저장 (인증 = 권한 = 저장 = Session)
 *                          -----
 *                          서버가 종료 => 메모리 해제 
 *                          | => Cookie 기반 : JWT 
 *          1) 인증 => 회원에 가입된 자 / 게스트 / 관리자 
 *             |= DispatcherServlet => HandlerMapping
 *              = 인터셉트 
 *              |
 *              사용자 요청 ====== DispatcherServlet => HandlerMapping
 *                        | preHandle()                  | => postHandle()
 *                                                    ViewResolver
 *                                                         | => 
 *                                                       JSP
 *          2) Authentication Filter 
 *               | 책임 전가 
 *          3) Authentication Manager
 *               | 인증 방법 
 *          4) Authentication Provider
 *             DataBase연동 => 5) UserDetailService : 결과값 return
 *                |
 *                4-1) User비교 / 암호 
 *                     | PasswordEncoding 
 *         
 *         /login => permitAll
 *         /admin => hasRole("ROLE_ADMIN")
 *         /user  => hasRole("ROLE_USER")
 *         /board => permitAll
 *         /member => permitAll
 */
public class SecurityConfig {
   private final LoginSuccessHandler loginSuccessHandler;
   private final LoginFailHandler  loginFailHandler;
   private final DataSource dataSource;
   
   // 접근 권한 => SecurityFilterChain
   @Bean
   public SecurityFilterChain filterChain(HttpSecurity http)
   throws Exception
   {
	   // 공격 방어 
	   /*
	    *     CSRF 
	    *       Cross site Request forgery 
	    *       공격자가 인증된 브라우저에서 
	    *       저장된 쿠키나 세션정보를 활용해서 
	    *       => 다른 사이트 요청값 전달 : 위조 
	    *       => 일반 보안 =: csrf.disable()
	    */
	   http
	    .csrf(csrf-> csrf.disable())
	    .authorizeHttpRequests(auth-> auth
	          .requestMatchers("/","/member/**").permitAll()
	          .requestMatchers("/admin/**").hasRole("ADMIN")
	          .anyRequest().permitAll()
	    )
	    .formLogin(form -> form 
	          .loginPage("/member/login")
	          .loginProcessingUrl("/member/login_process")
	          .usernameParameter("userid")
	          .passwordParameter("userpwd")
	          .defaultSuccessUrl("/",false)
	          .successHandler(loginSuccessHandler)
	          .failureHandler(loginFailHandler)
	          .permitAll() 
	    )
	    .rememberMe(remember-> remember
	         .key("my-secret-key")
	         .rememberMeParameter("remember-me")
	         .tokenValiditySeconds(60*60*24)
	         .tokenRepository(persistentTokenRepository())
	    )
	    
	    .logout(logout -> logout 
	          .logoutUrl("/member/logout")
	          .logoutSuccessUrl("/")
	          .invalidateHttpSession(true)
	          .deleteCookies("remember-me","JSESSIONID")
	    );
	    // remember-me
	    return http.build();
	    
   }
   /*
    *   권한 
    *     => permitAll
    *     => hasRole('ROLE_ADMIN')
    *     
    *   login 
    *   logout 
    *   remember-me 
    *   
    */
   // 인증 관리자 
   /*@Bean
   public AuthenticationManager authenticationManager(
       HttpSecurity http,
       BCryptPasswordEncoder passwordEncoder
   ) throws Exception
   {
	   return null;
   }
   @Bean
   public JdbcUserDetailsManager jdbcUserDetailsSevice()
   {
	   return null;
   }
   // 비밀번호 암호화 
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
	   return new BCryptPasswordEncoder();
   }
   // PersistentLogins 등록
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
	   return null;
   }*/
   @Bean
   public AuthenticationManager authenticationManager(
      HttpSecurity http,
      BCryptPasswordEncoder passwordEncoder
   ) throws Exception
   {
	   AuthenticationManagerBuilder builder=
			   http.getSharedObject(AuthenticationManagerBuilder.class);
	   builder
	     .userDetailsService(jdbcUserDetailsService())
	     .passwordEncoder(passwordEncoder());
	   return builder.build();
   }
   @Bean
   public JdbcUserDetailsManager jdbcUserDetailsService() {
	   JdbcUserDetailsManager manager=
			   new JdbcUserDetailsManager(dataSource);
	   manager.setUsersByUsernameQuery(
			   "SELECT userid as username,userpwd as password,enable "
			   +"FROM springmember WHERE userid=?"
	   );
	   manager.setAuthoritiesByUsernameQuery(
			   "SELECT userid as username , authority "
			  +"FROM authority WHERE userid=?"
	   );
	   return manager;
   }
   @Bean
   public BCryptPasswordEncoder passwordEncoder() {
	   return new BCryptPasswordEncoder();
   }
   @Bean
   public PersistentTokenRepository persistentTokenRepository() {
       JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
       repo.setDataSource(dataSource);
       return repo;
   }
   
}