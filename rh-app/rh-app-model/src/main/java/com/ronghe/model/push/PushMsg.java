package com.ronghe.model.push;

import java.io.Serializable;

//推送消息的实体类
public class PushMsg implements Serializable{

	private static final long serialVersionUID = 6913884843367089353L;
	
	private String cid;
	
	private String msgId;
	
	private String title;
	
	private String msg;
	
	private String url;
	
	private Integer msgType;

	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public Integer getMsgType() {
		return msgType;
	}



	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}



	public String getCid() {
		return cid;
	}



	public void setCid(String cid) {
		this.cid = cid;
	}



	public String getMsgId() {
		return msgId;
	}



	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}



	public String getMsg() {
		return msg;
	}



	public void setMsg(String msg) {
		this.msg = msg;
	}




	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}
	
	
	
	

}
