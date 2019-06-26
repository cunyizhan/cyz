package com.xc.microservice.validate.model.enums;

/**
 * 
 * @Description: 发送消息的动作 枚举
 */
public enum PushMsgEnum {
	
	ADDREQUEST(1, "添加请求:"),
	FRIENDMSG(2, "好友消息:"),	
	ALL(3, "天下通消息:");
	
	public final Integer type;
	public final String content;
	
	PushMsgEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
