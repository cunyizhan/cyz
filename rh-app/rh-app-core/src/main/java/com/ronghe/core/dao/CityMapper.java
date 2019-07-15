package com.ronghe.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ronghe.model.citydata.City;
import com.ronghe.model.citydata.Village;

@Mapper
public interface CityMapper {
	
	@Select("SELECT * from rh_city WHERE parentId=#{parentId} and is_delete = '0'")
	public List<City> serverQueryCity(String parentId);
	
	
	@Select("SELECT * from rh_village WHERE parentId=#{parentId} and is_delete = '0'")
	public List<Village> serverQueryVillage(String parentId);
}
