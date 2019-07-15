package com.ronghe.core.dao;

import com.ronghe.model.group.GroupRequest;


public interface GroupRequestMapper {
    int deleteByPrimaryKey(String id);

    int insert(GroupRequest record);

    int insertSelective(GroupRequest record);

    GroupRequest selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(GroupRequest record);

    int updateByPrimaryKey(GroupRequest record);
}