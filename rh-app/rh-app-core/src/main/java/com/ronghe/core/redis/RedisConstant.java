package com.ronghe.core.redis;

/**
 * 常量
 * @author zk
 *
 */
public class RedisConstant {
	/* 推广人员排名  */	
	public static final String EXTENSION_RANK_DB = "RANK:EXTENSION:DB";
	/* 商家排名  */	
	public static final String SELLERS_RANK_DB = "RANK:SELLERS:DB";
	
	//推送消息队列
	public static final String db_redis_push_msg = "list:push:msg";
	
	
	/* 未通过  */	
	public static final String NO_PASS = "0";
	
	/* 通过  */	
	public static final String PASS = "1";
	
	
}
