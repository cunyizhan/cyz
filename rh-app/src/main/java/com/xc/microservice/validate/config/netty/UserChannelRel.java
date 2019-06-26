package com.xc.microservice.validate.config.netty;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * @Description: 用户id和channel的关联关系处理
 */
public class UserChannelRel {

	// 记录用户的channel
		private static ConcurrentHashMap<String,Channel> usersMap = new ConcurrentHashMap<String,Channel>();
		
		public static void put(String senderId,Channel channel) {
			usersMap.put(senderId, channel);
		}
		
		public static Channel get(String senderId) {
			return usersMap.get(senderId);
		}
		
		public static void output() {
			for (HashMap.Entry<String,Channel> entry:usersMap.entrySet()) {
				System.out.println("userId:"+entry.getKey()+",channelId:"+entry.getValue().id().asLongText());
			}
		}
}
