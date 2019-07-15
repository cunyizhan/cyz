package com.ronghe.model.usergroup;

import java.util.Date;

public class UserGroupRequest {
	
    private String id;

    private String acceptGroupId;

    private String sendUserId;

    private Integer acceptStatus;

    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    

    public String getAcceptGroupId() {
		return acceptGroupId;
	}

	public void setAcceptGroupId(String acceptGroupId) {
		this.acceptGroupId = acceptGroupId;
	}

	public String getSendUserId() {
		return sendUserId;
	}

	public void setSendUserId(String sendUserId) {
		this.sendUserId = sendUserId;
	}

	public Integer getAcceptStatus() {
        return acceptStatus;
    }

    public void setAcceptStatus(Integer acceptStatus) {
        this.acceptStatus = acceptStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}