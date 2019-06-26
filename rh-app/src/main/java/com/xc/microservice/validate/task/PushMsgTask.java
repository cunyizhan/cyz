package com.xc.microservice.validate.task;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xc.microservice.validate.redis.RedisConstant;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.service.PushMsgService;




public class PushMsgTask implements Runnable{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	private ThreadPoolExecutor threadPool;
	private RedisService redisService;
	private PushMsgService pushMsgService;

	public PushMsgTask(ThreadPoolExecutor threadPool,RedisService redisService,PushMsgService pushMsgService) {
		this.threadPool = threadPool;
		this.redisService=redisService;
		this.pushMsgService=pushMsgService;
	}

	@Override
	public void run() {
		try {
			int taskNum = 5;
			List<byte[]> taskList = redisService.getByteFromList(
					RedisConstant.db_redis_push_msg, taskNum);
			if (taskList == null) {
				return;
			}

			logger.info("从"
					+RedisConstant.db_redis_push_msg
					+ "队列中获取" + taskList.size() + "个任务");

			threadPool.execute(new ThreadPoolTask(taskList,redisService,pushMsgService));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("AcquireTask.run()异常:" + e.getMessage());
		}
	}
}
