package com.ict.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor{
	// preHandle, postHandle, afterCompletion
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		// 리턴값이 boolean이다. 
		// 리턴이 true 일경우 preHandle() 실행후 핸들러에 접근한다. 
		// false일경우 작업을 중단하기 때문에 컨트롤러와 남은 인터셉터가 실행되지않는다. (특정 jsp로 이동)
		
		// 로그인 하면 세션에 로그인 성공했다는 정보를 담거나 사용자 정보를 담자.
		// 로그인 체크를 해서 만약에 로그인이 안된 상태이면 value에 false 저장
		
		// 트루를 쓴 이유 : 만약에 세션이 삭제된 상태라면 새로운 세션을 생성해준다. 
		// 삭제가 안된 상태라면 사용중인 상태로 그대로 전달해 준다. 라는 뜻임
		Object obj = request.getSession(true).getAttribute("LoginChk");
		
		if (obj == null) { // 로그인 안된 상태
			// 자바스크립트 코드 작성
			String script = "<script>alert('로그인이 필요합니다.'); location.href='/sns_login'; </script>";
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write(script);
			response.getWriter().flush();
			
			return false;
		}
		
		return true;
	}

	
	
	/*
	 * @Override public void postHandle(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, ModelAndView modelAndView)
	 * throws Exception { // TODO Auto-generated method stub
	 * HandlerInterceptor.super.postHandle(request, response, handler,
	 * modelAndView); }
	 * 
	 * @Override public void afterCompletion(HttpServletRequest request,
	 * HttpServletResponse response, Object handler, Exception ex) throws Exception
	 * { // TODO Auto-generated method stub
	 * HandlerInterceptor.super.afterCompletion(request, response, handler, ex); }
	 */
}
