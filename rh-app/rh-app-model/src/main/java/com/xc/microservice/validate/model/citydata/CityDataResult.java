package com.xc.microservice.validate.model.citydata;

import java.util.List;

public class CityDataResult {
	
	private String value;
	
	private String text;
	
	private List<VillageVm> children;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<VillageVm> getChildren() {
		return children;
	}

	public void setChildren(List<VillageVm> children) {
		this.children = children;
	}
	
	
	

}
