package com.xc.microservice.validate.model.enums;

/**
 * 
 * @Description: 发送消息的动作 枚举
 */
public enum MsgTypeEnum {
	
	GROUP(1, "群聊"),
	SINGLE(0, "单聊");	
	
	public final Integer type;
	public final String content;
	
	MsgTypeEnum(Integer type, String content){
		this.type = type;
		this.content = content;
	}
	
	public Integer getType() {
		return type;
	}  
}
