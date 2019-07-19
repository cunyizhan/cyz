package com.ronghe.model.console;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


public class ConsoleManageer {
	
    private String maId;

    private String maName;

    private String maStatus;

    private String maPhone;

    private String maEmail;

    private String maPassword;
    
    public String getMaId() {
		return maId;
	}

	public void setMaId(String maId) {
		this.maId = maId;
	}

	public String getMaName() {
		return maName;
	}

	public void setMaName(String maName) {
		this.maName = maName;
	}

	public String getMaStatus() {
		return maStatus;
	}

	public void setMaStatus(String maStatus) {
		this.maStatus = maStatus;
	}

	public String getMaPhone() {
		return maPhone;
	}

	public void setMaPhone(String maPhone) {
		this.maPhone = maPhone;
	}

	public String getMaEmail() {
		return maEmail;
	}

	public void setMaEmail(String maEmail) {
		this.maEmail = maEmail;
	}

	public String getMaPassword() {
		return maPassword;
	}

	public void setMaPassword(String maPassword) {
		this.maPassword = maPassword;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}

	private Date createTime;

    private String creater;

    private String isDelete;

    
}