package com.ronghe.core.redis;

public class ChannelKey extends BasePrefix{

	public ChannelKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static ChannelKey channel = new ChannelKey(0, "channel:");
}
