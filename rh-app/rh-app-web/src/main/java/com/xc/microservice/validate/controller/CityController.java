package com.xc.microservice.validate.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.model.bo.UsersBO;
import com.xc.microservice.validate.model.chat.ChatMsg;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.citydata.CityDataResult;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.enums.OperatorFriendRequestTypeEnum;
import com.xc.microservice.validate.model.enums.SearchFriendsStatusEnum;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.model.vo.FriendRequestVO;
import com.xc.microservice.validate.model.vo.LoginVo;
import com.xc.microservice.validate.model.vo.MyFriendsVO;
import com.xc.microservice.validate.model.vo.UsersVO;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.redis.SessionKey;
import com.xc.microservice.validate.service.CityService;
import com.xc.microservice.validate.service.UserService;
import com.xc.microservice.validate.util.aes.SymmetricEncoder;

/**
 * 用户服务
 */
@RestController
@Slf4j
public class CityController {
	
	@Autowired
	private CityService cityService;
	
	
	/**
	 * 通过过户名密码登陆
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	
	@RequestMapping(value="/city/getData", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> getCityData(String cityCode) throws IOException{
		List<CityDataResult>  data = cityService.queryCityData(cityCode);
		return Result.success(data);
	}
	
	
	
	
}
