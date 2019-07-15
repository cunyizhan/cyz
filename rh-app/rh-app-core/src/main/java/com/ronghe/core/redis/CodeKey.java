package com.ronghe.core.redis;

public class CodeKey extends BasePrefix{

	public CodeKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static CodeKey code = new CodeKey(24*60*60, "code:");
}
