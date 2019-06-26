package com.xc.microservice.validate.config.access;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.fastjson.JSON;
import com.xc.microservice.validate.model.entity.TUser;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.redis.AccessKey;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.redis.SessionKey;



@Service
public class AccessInterceptor  extends HandlerInterceptorAdapter{
	
	@Autowired
	private RedisService redisService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
//		doParams(request);
		if(handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod)handler;
			String uri =request.getRequestURI();
			if(uri.contains("login")){
				return true;
			}
			if(uri.contains("regist")){
				return true;
			}
			if(uri.contains("nolimit")){
				return true;
			}
			TUser user = getUser(request, response);
			AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
			UserContext.setUser(user);
			if(accessLimit == null) {
				return true;
			}
			boolean needLogin = accessLimit.needLogin();
			int seconds = accessLimit.seconds();
			int maxCount = accessLimit.maxCount();
			String key = request.getRequestURI();
			if(needLogin) {
				if(user == null) {
					render(response, CodeMsg.NO_LOGIN);
					return false;
				}
				key += "_" + user.getPhone();
			}
			AccessKey ak = AccessKey.withExpire(seconds);
			Integer count = redisService.get(ak, key, Integer.class);
	    	if(count  == null) {
	    		 redisService.set(ak, key, 1);
	    	}else if(count < maxCount) {
	    		 redisService.incr(ak, key);
	    	}else {
	    		render(response, CodeMsg.ACCESS_LIMIT_REACHED);
	    		return false;
	    	}
		}
		return true;
	}
	

	private void render(HttpServletResponse response, CodeMsg cm)throws Exception {
		response.setContentType("application/json;charset=UTF-8");
		OutputStream out = response.getOutputStream();
		String str  = JSON.toJSONString(Result.error(cm));
		out.write(str.getBytes("UTF-8"));
		out.flush();
		out.close();
	}

	private TUser getUser(HttpServletRequest request, HttpServletResponse response) {
		String token = request.getParameter("token");
		if(StringUtils.isEmpty(token)) {
			return null;
		}
		TUser user = redisService.get(SessionKey.session, token, TUser.class);
		return user;
	}
	/**
	 * 处理参数流
	 * @param request
	 */
	private void doParams(HttpServletRequest request){
		 try {
	            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
	            StringBuffer sb=new StringBuffer();
	            String s=null;
	            while((s=br.readLine())!=null){
	                sb.append(s);
	            }
	            JSONObject jsonObject = JSONObject.fromObject(sb.toString());
	            Set<String> set=jsonObject.keySet();
	            for (String k : set) {
	            	request.setAttribute(k, jsonObject.get(k));
				}
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	}
}
