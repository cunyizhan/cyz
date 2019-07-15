package com.ronghe.web.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;


import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ronghe.model.citydata.CityDataResult;
import com.ronghe.model.result.Result;
import com.ronghe.core.service.CityService;

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
