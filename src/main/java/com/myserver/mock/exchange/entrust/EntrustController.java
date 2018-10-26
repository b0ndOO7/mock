package com.myserver.mock.exchange.entrust;

import java.util.Map;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableAutoConfiguration
@RequestMapping("/entrust/EntrustController")
public class EntrustController {

	@RequestMapping("/addEntrust")
	@ResponseBody
	String addEntrust(String type, String price, String marketId, String userId){
		//{"type":1,"price":"10","marketId":"80","userId":"{UID}","rangeType":0,"amount":"1"}
		System.out.println("type:" + type);
		System.out.println("price:" + price);
		System.out.println("marketId:" + marketId);
		System.out.println("userId:" + userId);
		return "{\"datas\":{\"entrustId\":\"E6442353268852006912\"},\"resMsg\":{\"code\":\"1\",\"method\":null,\"message\":\"success !\"}}";
	}
	
	
	
	@RequestMapping("/getEntrustById")
	@ResponseBody
	String getEntrustById(@RequestBody String reqjson){
		
		return "";
	}
	
	
	
	@RequestMapping("/cancelEntrust")
	@ResponseBody
	String cancelEntrust(){
		
		return "";
	}
	
	
	
	@RequestMapping("/getUserEntrustList")
	@ResponseBody
	String getUserEntrustList(){
		
		return "";
	}
	
	
	
	@RequestMapping("/getUserEntrustRecordFromCache")
	@ResponseBody
	String getUserEntrustRecordFromCache(){
		
		return "";
	}
	
	
	public void main(){
		
		
	}
	
}
