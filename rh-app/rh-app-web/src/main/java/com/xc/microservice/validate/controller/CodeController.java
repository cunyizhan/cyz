package com.xc.microservice.validate.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

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
	public Result<?> getCode(@RequestBody Map<String, String> map){
		String phone= map.get("phone");
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
	public Result<?> compareCode(@RequestBody Map<String, String> map){
		try {
			 String phone= map.get("phone");
			 String code= map.get("code");
			CodeMsg msg = codeService.compareCode(phone, code);
			return Result.msg(msg);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	@RequestMapping(value="/login/codeLogin", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> user_code_login(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid LoginVo vo) throws UnsupportedEncodingException, IOException{
		try {
			Users user = new Users();
			log.info("手机号{}",vo.getPhone());
			log.info("手机标识{}",vo.getCid());
			log.info("短信验证码{}",vo.getCode());
			user.setPhone(vo.getPhone());
			user.setCid(vo.getCid());
			Users res = userService.queryUserForCodeLogin(user);
			CodeMsg msg = codeService.compareCode(user.getPhone(), user.getPassword());
			if(CodeMsg.SUCCESS.getCode()==msg.getCode()){
				redisService.set(SessionKey.session, vo.getPhone(),res);
				return Result.success(res);
			}
			return Result.error(CodeMsg.PHONE_OR_CODE_ERROR);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
}
