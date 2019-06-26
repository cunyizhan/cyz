package com.xc.microservice.validate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.model.vo.LoginVo;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.redis.SessionKey;
import com.xc.microservice.validate.service.CodeService;
import com.xc.microservice.validate.service.UserService;

/**
 * 查询产品服务
 */
@RestController
@Slf4j
public class CodeController{
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	private UserService userService;	
	
	@Autowired
	private CodeService codeService;
	
	
	/**
	 * 发送验证码
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@PostMapping(value="/zyzs/getCode")
	public Result<?> getCode(String phone){
		CodeMsg msg = codeService.generateCode(phone);
		return Result.msg(msg);
	}
	/**
	 * 对比验证码
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@PostMapping(value="/zyzs/compareCode")
	public Result<?> compareCode( String phone,String code){
		CodeMsg msg = codeService.compareCode(phone, code);
		return Result.msg(msg);
	}
	
	@RequestMapping(value="/login/codeLogin", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> user_code_login(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid LoginVo vo) throws UnsupportedEncodingException, IOException{
		Users user = new Users();
		log.info("手机号{}",vo.getPhone());
		log.info("手机标识{}",vo.getCid());
		log.info("短信验证码{}",vo.getCode());
		user.setPhone(vo.getPhone());
		user.setPassword(vo.getPassword());
		Users res = userService.queryUserForLogin(user);
		CodeMsg msg = codeService.compareCode(user.getPhone(), user.getPassword());
		if(CodeMsg.SUCCESS.getCode()==msg.getCode()){
			redisService.set(SessionKey.session, vo.getPhone(),res);
			return Result.success(res);
		}
		return Result.error(CodeMsg.PHONE_OR_CODE_ERROR);
	}
}
