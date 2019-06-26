package com.xc.microservice.validate.model.chat;

import java.util.Date;

import javax.validation.constraints.NotNull;


public class Users {
	private String id;
	//用户名
	@NotNull
	private String username;
	//昵称
	@NotNull
	private String nickname;
	//性别
	@NotNull
	private String gender;
	//手机号
	@NotNull
	private String phone;
	//密码
	@NotNull
	private String password;
	
	//邮箱
	private String email;
	//地区编码
	@NotNull
	private String citycode;
	
	private String address;
	
	private String addresscode;
	
	//头像缩率图--聊天用
	private String faceImage;
	//大头像
	private String faceImageBig;
	/**
     * 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    private String qrcode;
    
    //每台设备的唯一标识
    @NotNull
    private String cid;
	
	//注册时间
	private Date registDate;
	//上次登录时间
	private Date lastDate;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getCitycode() {
		return citycode;
	}
	public void setCitycode(String citycode) {
		this.citycode = citycode;
	}
	public String getFaceImage() {
		return faceImage;
	}
	public void setFaceImage(String faceImage) {
		this.faceImage = faceImage;
	}
	public String getFaceImageBig() {
		return faceImageBig;
	}
	public void setFaceImageBig(String faceImageBig) {
		this.faceImageBig = faceImageBig;
	}
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public Date getRegistDate() {
		return registDate;
	}
	public void setRegistDate(Date registDate) {
		this.registDate = registDate;
	}
	public Date getLastDate() {
		return lastDate;
	}
	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddresscode() {
		return addresscode;
	}
	public void setAddresscode(String addresscode) {
		this.addresscode = addresscode;
	}
	
	

}