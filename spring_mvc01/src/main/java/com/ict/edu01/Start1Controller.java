package com.ict.edu01;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

// 컨트롤러를 어노테이션 하지 않으면 반드시 Controller를 구현(implements) 해야한다.
public class Start1Controller implements Controller {
	
	// 실행하는 메서드
	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// servlet-context.xml 되돌아 갔다가 ViewResolver 에 들어가는 이름
		// (단순하게 말하면 결과를 보여줄 jsp 이름)
		
		// 방법1)
		//ModelAndView mv = new ModelAndView();
		//mv.setViewName("result1");
		              //되돌아갈 jsp 이름 "result1"
		
		// 방법2)
		ModelAndView mv = new ModelAndView("result1");
		
		// 일처리 (= 비즈니스로직) => 서비스(interface) => 서비스임플(클래스) => DAO(interface) DAO임플(클래스)  => DB
        //                     |<========= 비즈니스 레이어 ========>|   |<======= Repository 레이어 ======>|
		
		// JSP에서 표현하기 위해서 값 저장하는 방법
		
		// 1. request 이용(JSP MVC와 같음)
		request.setAttribute("name", "홍길동");
		
		// 2. ModelAndView 이용 (default)
		mv.addObject("age", 27);
		
		// 3. session (로그인 처리할 때만 사용)
		request.getSession().setAttribute("phone", "010-7777-9999");
		
		return mv;
	}
}
