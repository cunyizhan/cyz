package com.ronghe.core.service;


import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ronghe.core.dao.CityMapper;
import com.ronghe.model.citydata.City;
import com.ronghe.model.citydata.CityDataResult;
import com.ronghe.model.citydata.Village;
import com.ronghe.model.citydata.VillageVm;


@Service
public class CityService {
	
	@Autowired
	private CityMapper cityMapper;
	
	public List<CityDataResult> queryCityData(String cityCode){
		List<CityDataResult> res = new ArrayList<CityDataResult>();
		List<City> citys = cityMapper.serverQueryCity(cityCode);
		if(citys!=null && citys.size()>0){
			for(City city : citys){
				CityDataResult temp_city_data= new CityDataResult();
				temp_city_data.setText(city.getName());
				temp_city_data.setValue(city.getId()+"");
				List<Village> villages = cityMapper.serverQueryVillage(city.getId()+"");
				List<VillageVm> temp_villages = new ArrayList<VillageVm>();
				if(villages != null && villages.size()>0){
					for(Village village : villages){
						VillageVm vm =new VillageVm();
						vm.setText(village.getName());
						vm.setValue(village.getId()+"");
						temp_villages.add(vm);
					}
				}
				temp_city_data.setChildren(temp_villages);
				res.add(temp_city_data);
			}
		}
		
		return res;
	}
	
}
