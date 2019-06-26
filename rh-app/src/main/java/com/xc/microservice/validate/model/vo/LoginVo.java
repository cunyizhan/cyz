package com.xc.microservice.validate.model.vo;

import javax.validation.constraints.NotNull;

public class LoginVo {

	@NotNull
	private String phone;
	
	private String  password;
	
	private String code;
	
	private String cid;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}
	
	
}
