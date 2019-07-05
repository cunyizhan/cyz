package com.xc.microservice.validate.config.task;

import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.xc.microservice.validate.redis.RedisService;
//import com.xc.microservice.validate.service.PushMsgService;
//import com.xc.microservice.validate.task.PushMsgTask;

@Service
public class InitTaskConfig  implements InitializingBean{
	protected final Logger logger = LoggerFactory.getLogger(this.getClass()); 
	
	@Autowired
	private ThreadPoolExecutor threadPool;
	
//	@Autowired
//	private RedisService redisService;
//	
//	@Autowired
//	private PushMsgService pushMsgService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// 从服务器获取任务的 计划任务线程
//		ScheduledExecutorService scheduledService = Executors
//								.newScheduledThreadPool(2);
//		// 执行计划 -- 个推推送消息
//		scheduledService.scheduleWithFixedDelay(new PushMsgTask(threadPool,redisService,pushMsgService),
//						2, 2, TimeUnit.MICROSECONDS);
		
		
		
	}

	
}
