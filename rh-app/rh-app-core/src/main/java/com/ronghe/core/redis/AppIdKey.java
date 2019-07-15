package com.ronghe.core.redis;

public class AppIdKey extends BasePrefix {

	public AppIdKey(String prefix) {
		super(prefix);
	}
	public static AppIdKey appid = new AppIdKey("appid:");
}
