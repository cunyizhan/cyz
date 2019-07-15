package com.ronghe.core.redis;

public class SessionKey extends BasePrefix{

	public SessionKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	//用户登录以后永不过期
	public static SessionKey session = new SessionKey(0, "session:");
}
