package com.xc.microservice.validate.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xc.microservice.validate.config.netty.ChatMsg;
import com.xc.microservice.validate.service.PushMsgService;

/**
 * 查询产品服务
 */
@RestController
public class TestController{
	
	@Autowired
	PushMsgService pushMsgService;
	
	
	
	
	
	
	/**
	 * 测试消息推送
	 */
	@GetMapping(value="/test")
	public void test(String phone){
		pushMsgService.savePushMsgFromRequestToList("1905176FYHZTZW28","19051773DY9S2B7C");
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setMsg("您号啊");
		pushMsgService.savePushMsgFromFriendToList("19051773DY9S2B7C", "1905176FYHZTZW28", chatMsg );
	}
	
	
}
