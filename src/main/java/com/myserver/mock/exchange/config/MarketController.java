package com.myserver.mock.exchange.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/config/controller/website/pricecontroller")
public class MarketController {
	
	@RequestMapping("getByWebId")
	@ResponseBody
	String getByWebId(){
		
		return "";
	}

}
