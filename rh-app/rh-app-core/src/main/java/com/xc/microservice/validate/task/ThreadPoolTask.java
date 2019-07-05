package com.xc.microservice.validate.task;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xc.microservice.validate.model.push.PushMsg;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.service.PushMsgService;
import com.xc.microservice.validate.util.SerializeUtils;

public class ThreadPoolTask implements Runnable {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	private RedisService redisService;
	private PushMsgService pushMsgService;
	private List<byte[]> taskList;

	public ThreadPoolTask(List<byte[]> taskList,RedisService redisService,PushMsgService pushMsgService) {
		this.taskList = taskList;
		this.redisService=redisService;
		this.pushMsgService=pushMsgService;
	}

	@Override
	public void run() {
		try {
			for (byte[] task : taskList) {
				try {
					Object obj = SerializeUtils.unserialize(task);
					if (obj instanceof PushMsg) {
						PushMsg record = (PushMsg) obj;
						pushMsgService.pushMsg(record);
					}
				} catch (Exception e) {
					 e.printStackTrace();
					 logger.error("ThreadPoolTask.run异常:" + e.getMessage());
				}
				
			}
		} catch (Exception e) {
			logger.error("ThreadPoolTask.run异常:" + e.getMessage());
		}
	}
	
	public void sendTemplate(){
		
	}

}
