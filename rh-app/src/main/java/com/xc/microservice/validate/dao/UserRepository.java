package com.xc.microservice.validate.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.xc.microservice.validate.model.entity.TUser;
/**
 *用户类操作
 * @author zk
 *
 */
public interface UserRepository extends MongoRepository<TUser, String> {
	TUser findByPhone(String phone);
}
