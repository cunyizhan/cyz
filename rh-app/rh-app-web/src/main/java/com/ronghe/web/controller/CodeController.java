package com.ronghe.web.controller;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ronghe.model.chat.Users;
import com.ronghe.model.result.CodeMsg;
import com.ronghe.model.result.Result;
import com.ronghe.model.vo.LoginVo;
import com.ronghe.core.redis.RedisService;
import com.ronghe.core.redis.SessionKey;
import com.ronghe.core.service.CodeService;
import com.ronghe.core.service.UserService;
import com.ronghe.common.util.http.HttpUtil;

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
	
	@Value("${wangyi.createUrl}")
	private String createUrl;
	
	@Value("${wangyi.refreshTokenUrl}")
	private String refreshTokenUrl;
	
	@Value("${wangyi.appKey}")
	private String appKey;
	
	@Value("${wangyi.appSecret}")
	private String appSecret;
	
	@Value("${wangyi.nonce}")
	private String nonce;
	
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
			if(CodeMsg.SUCCESS.getCode()!=msg.getCode()){
				return Result.error(CodeMsg.PHONE_OR_CODE_ERROR);
			}
			try {
				JSONObject j=loginWangyi(res);
				redisService.set(SessionKey.session, vo.getPhone(),res);
				return Result.success(j);
			} catch (Exception e) {
				log.error("",e);
				return Result.error(CodeMsg.WANGYI_LOGIN_ERROR);
			}
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 通过过户名密码登陆
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	
	@RequestMapping(value="/login/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> user_login(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid LoginVo vo) throws UnsupportedEncodingException, IOException{
		Users user = new Users();
		try {
			log.info("手机号{}",vo.getPhone());
			log.info("密码{}",vo.getPassword());
			user.setPhone(vo.getPhone());
			user.setPassword(vo.getPassword());
			user.setCid(vo.getCid());
			log.info("手机标识{}",vo.getCid());
			Users res = userService.queryUserForLogin(user);
			if(res==null){
				return Result.error(CodeMsg.PHONE_OR_PASSWORD_ERROR);
			}
			try {
				JSONObject j=loginWangyi(res);
				redisService.set(SessionKey.session, vo.getPhone(),res);
				return Result.success(j);
			} catch (Exception e) {
				log.error("",e);
				return Result.error(CodeMsg.WANGYI_LOGIN_ERROR);
			}
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	private Map<String,String> getWangyiParams(){
		Map<String,String> map=new HashMap<String, String>();
		map.put("url", createUrl);
		map.put("appKey", appKey);
		map.put("appSecret", appSecret);
		map.put("nonce", nonce);
		return map;
	}
	
	private JSONObject loginWangyi(Users user)throws Exception{
		Map<String,String> map=getWangyiParams();
		map.put("accid", user.getId());
		String res=HttpUtil.postRequest(map);
		JSONObject js=JSONObject.parseObject(res);
		js=refreshToken(js, user.getId());
		JSONObject json=(JSONObject) JSONObject.toJSON(user);
		js.put("userInfo", json);
		return js;
	}
	
	private JSONObject refreshToken(JSONObject js,String accid)throws Exception{
		if(!"414".equals(js.get("code")+"")){
			return js;
		}
		Map<String,String> map=new HashMap<String, String>();
		map.put("url", refreshTokenUrl);
		map.put("appKey", appKey);
		map.put("appSecret", appSecret);
		map.put("nonce", nonce);
		map.put("accid", accid);
		String res=HttpUtil.postRequest(map);
		return JSONObject.parseObject(res);
	}
}
