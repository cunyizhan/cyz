package com.xc.microservice.validate.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.entity.TUser;
/**
 *聊天记录
 * @author zk
 *
 */
public interface ChatMsgRepository extends MongoRepository<TChatMsg, String> {
}
