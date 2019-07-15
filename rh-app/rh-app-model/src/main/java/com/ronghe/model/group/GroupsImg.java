package com.ronghe.model.group;

import java.util.Date;



public class GroupsImg {
    private String id;

    private String groupId;
    
    private String imgName;
    
    private String imgPath;
    
    private Date createTime;
    
    private String imgDesc;
    
    private Integer imgSn;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getImgName() {
		return imgName;
	}

	public void setImgName(String imgName) {
		this.imgName = imgName;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getImgDesc() {
		return imgDesc;
	}

	public void setImgDesc(String imgDesc) {
		this.imgDesc = imgDesc;
	}

	public Integer getImgSn() {
		return imgSn;
	}

	public void setImgSn(Integer imgSn) {
		this.imgSn = imgSn;
	}
	
}