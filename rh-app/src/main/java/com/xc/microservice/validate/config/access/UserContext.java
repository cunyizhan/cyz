package com.xc.microservice.validate.config.access;

import com.xc.microservice.validate.model.entity.TUser;






public class UserContext {
	
	private static ThreadLocal<TUser> userHolder = new ThreadLocal<TUser>();
	
	public static void setUser(TUser fans) {
		userHolder.set(fans);
	}
	
	public static TUser getUser() {
		return userHolder.get();
	}

}
