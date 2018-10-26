package com.myserver.mock.exchange.fund;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/fund/controller/website/fundcontroller")
public class Fundcontroller {
	
	
	@RequestMapping("/findbypage")
	@ResponseBody
	String findByPage(){
		
		return "";
	}

}
