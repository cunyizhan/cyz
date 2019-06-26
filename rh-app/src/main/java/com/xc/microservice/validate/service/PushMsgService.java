package com.xc.microservice.validate.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.xc.microservice.validate.SpringUtil;
import com.xc.microservice.validate.config.netty.ChatMsg;
import com.xc.microservice.validate.dao.PushMsgResultRepository;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.enums.PushMsgEnum;
import com.xc.microservice.validate.model.push.PushMsg;
import com.xc.microservice.validate.model.push.TPushMsgResult;
import com.xc.microservice.validate.redis.RedisConstant;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.util.SerializeUtils;
import com.xc.microservice.validate.util.push.PushtoAPP;
import com.xc.microservice.validate.util.push.PushtoSingle;

@Service
@Slf4j
public class PushMsgService {
	
	@Autowired
	RedisService redisService;
	
	@Autowired
	private PushMsgResultRepository pushMsgDao;
	
	@Autowired
	private UserService userService;
	
	
	public void savePushMsgResult(TPushMsgResult result){
		pushMsgDao.save(result);
	}
	
	public void savePushMsgFromFriendToList(String receiverId,String senderId,ChatMsg chatMsg){
		Users receiverUser = userService.queryUserById(receiverId);
		Users sendUser = userService.queryUserById(senderId);
		// TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
		PushMsg pushMsg = new PushMsg();
		pushMsg.setCid(receiverUser.getCid());
		pushMsg.setTitle(PushMsgEnum.FRIENDMSG.content);
		pushMsg.setMsg(sendUser.getNickname()+":"+chatMsg.getMsg());
		pushMsg.setMsgType(PushMsgEnum.FRIENDMSG.type);
		this.saveRedisList(pushMsg, RedisConstant.db_redis_push_msg);
	}
	
	/**
	 * 请求消息
	 * @param receiverId
	 * @param senderId
	 */
	public void savePushMsgFromRequestToList(String senderId,String receiverId){
		Users receiverUser = userService.queryUserById(receiverId);
		Users sendUser = userService.queryUserById(senderId);
		// TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
		PushMsg pushMsg = new PushMsg();
		pushMsg.setCid(receiverUser.getCid());
		pushMsg.setTitle(PushMsgEnum.ADDREQUEST.content);
		pushMsg.setMsg(sendUser.getNickname()+"请求添加您为好友");
		pushMsg.setMsgType(PushMsgEnum.ADDREQUEST.type);
		this.saveRedisList(pushMsg, RedisConstant.db_redis_push_msg);
	}
	
	/**
	 * 保存入待发奖队列->待推送模板队列
	 * @param rank
	 * @param listname
	 * @return
	 */
	public boolean saveRedisList(PushMsg msg,String listname){
		log.info("存入："+listname);
		List<byte[]> list=new ArrayList<byte[]>();
		list.add(SerializeUtils.serialize(msg));
		try {
			redisService.addByteToList(listname,list);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	public void pushMsg(PushMsg record) throws Exception{
		if(record.getMsgType().equals(PushMsgEnum.ADDREQUEST.type)){
			PushtoSingle.push(record.getCid(), PushMsgEnum.ADDREQUEST.content, record.getMsg(),record.getUrl());
		}
		
		if(record.getMsgType().equals(PushMsgEnum.FRIENDMSG.type)){
			PushtoSingle.push(record.getCid(), PushMsgEnum.FRIENDMSG.content, record.getMsg(),record.getUrl());
		}
		if(record.getMsgType().equals(PushMsgEnum.ALL.type)){
			PushtoAPP.push(PushMsgEnum.ALL.content, record.getMsg(),record.getUrl());
		}
	}
	

}
