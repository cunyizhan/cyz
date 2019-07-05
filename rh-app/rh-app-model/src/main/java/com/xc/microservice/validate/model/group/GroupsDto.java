package com.xc.microservice.validate.model.group;

import java.util.List;
import com.xc.microservice.validate.model.chat.Users;
import lombok.Data;


public class GroupsDto extends Groups {

	private List<Users> memberList;
	
	private boolean joined = false;

	public List<Users> getMemberList() {
		return memberList;
	}

	public void setMemberList(List<Users> memberList) {
		this.memberList = memberList;
	}

	public boolean isJoined() {
		return joined;
	}

	public void setJoined(boolean joined) {
		this.joined = joined;
	}
	
	
	
}
