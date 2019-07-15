package com.ronghe.web.filter;

import com.ronghe.model.entity.TUser;






public class UserContext {
	
	private static ThreadLocal<TUser> userHolder = new ThreadLocal<TUser>();
	
	public static void setUser(TUser fans) {
		userHolder.set(fans);
	}
	
	public static TUser getUser() {
		return userHolder.get();
	}

}
