package com.ronghe.core.redis;

public class MsgKey extends BasePrefix{

	public MsgKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static MsgKey code = new MsgKey(60, "msg:");
}
