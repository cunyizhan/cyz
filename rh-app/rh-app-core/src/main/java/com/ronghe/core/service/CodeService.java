package com.ronghe.core.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ronghe.model.result.CodeMsg;
import com.ronghe.core.redis.MsgKey;
import com.ronghe.core.redis.RedisService;
import com.ronghe.common.util.sendMsg.SendMessageUtils;

@Service
public class CodeService {
	
	@Autowired
	RedisService redisService;
	
	public CodeMsg generateCode(String phone){
		if(redisService.exists(MsgKey.code, phone)){
			return CodeMsg.MSG_NOT_OVER;
		}
		String code = SendMessageUtils.generateCode();
		redisService.set(MsgKey.code, phone, code);
		String res = SendMessageUtils.sendMessage(phone, code);
		if(res.equals("ok")){
			return  CodeMsg.SUCCESS.fillArgsToken(code);
		}
		return  CodeMsg.MSG_SEND_FAIL;
		
	}
	
	public CodeMsg compareCode(String phone,String code){
		if(redisService.exists(MsgKey.code, phone)){
			String redis_code = redisService.get(MsgKey.code, phone, String.class);
			if(redis_code.equals(code)){
				return CodeMsg.SUCCESS;
			}else{
				return CodeMsg.MSG_VALIDATE_FAIL;
			}
		}else{
			return CodeMsg.MSG_VALIDATE_FAIL;
		}
		
	}
	
}
