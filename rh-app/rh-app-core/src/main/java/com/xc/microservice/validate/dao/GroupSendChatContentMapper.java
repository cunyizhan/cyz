package com.xc.microservice.validate.dao;

import com.xc.microservice.validate.model.group.GroupSendChatContent;


public interface GroupSendChatContentMapper {
    int deleteByPrimaryKey(String id);

    int insert(GroupSendChatContent record);

    int insertSelective(GroupSendChatContent record);

    GroupSendChatContent selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GroupSendChatContent record);

    int updateByPrimaryKeyWithBLOBs(GroupSendChatContent record);

    int updateByPrimaryKey(GroupSendChatContent record);
}