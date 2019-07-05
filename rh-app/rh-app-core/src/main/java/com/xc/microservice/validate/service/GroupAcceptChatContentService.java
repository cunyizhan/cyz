package com.xc.microservice.validate.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xc.microservice.validate.dao.GroupAcceptChatContentMapper;
import com.xc.microservice.validate.model.enums.MsgSignFlagEnum;



@Service
public class GroupAcceptChatContentService {
	
	@Resource
	private GroupAcceptChatContentMapper groupAcceptChatContentMapper;
	
	/**
	 * 批量签收群聊接收消息
	 * @param msgIdList
	 */
	public void batchUpdateSignStatus(List<String> msgIdList) {
		groupAcceptChatContentMapper.batchUpdateSignStatus(msgIdList, MsgSignFlagEnum.signed.type);
	}

}
