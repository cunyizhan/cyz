 package com.xc.microservice.validate.model.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 聊天记录
 * @author 
 *
 */
@Document(collection="t_chat_msg")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'sendUserId': 1,'acceptUserId': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class TChatMsg {
    private String sid;

    private String sendUserId;

    private String acceptUserId;

    private String msg;

    /**
     * 消息是否签收状态
	1：签收
	0：未签收
     */
    private Integer signFlag;

    /**
     * 发送请求的事件
     */
    private Date createTime;
    
    /**
     * msgType 0 是 单对单消息
     * 1 群消息
     */
    private String msgType;
    
    /**
     * groupId 0 是单聊
     * 1. 是群聊 此时groupId和 acceptUserId 一致
     */
    private String groupId;

}