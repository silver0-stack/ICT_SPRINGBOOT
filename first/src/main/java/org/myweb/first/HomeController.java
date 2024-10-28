package org.myweb.first;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	//common/main.jsp 를 내보내기 위한 요청 메소드
	//스프링부트에서는 jsp 파일을 뷰파일로 지정시에는 반드시 ModelAndView 리턴 형태로 사용해야 함
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public ModelAndView home(ModelAndView mv) {
		mv.setViewName("common/main");  //내보낼 뷰파일명
		return mv;
	}

	@RequestMapping("main.do")
	public ModelAndView forwardMain(ModelAndView mv) {
		mv.setViewName("common/main");  //내보낼 뷰파일명
		return mv;
	}
	
}








