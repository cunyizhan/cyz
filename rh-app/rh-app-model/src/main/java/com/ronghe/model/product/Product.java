package com.ronghe.model.product;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


public class Product {
	
    private Integer productId;

    private String productName;

    private Integer shopId;

    private String productType;

    private Date start_sellTime;

    private Date stop_sellTime;
    
    private Date createTime;

    private Integer productNum;

    private String isDelete;

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public Date getStart_sellTime() {
		return start_sellTime;
	}

	public void setStart_sellTime(Date start_sellTime) {
		this.start_sellTime = start_sellTime;
	}

	public Date getStop_sellTime() {
		return stop_sellTime;
	}

	public void setStop_sellTime(Date stop_sellTime) {
		this.stop_sellTime = stop_sellTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getProductNum() {
		return productNum;
	}

	public void setProductNum(Integer productNum) {
		this.productNum = productNum;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
    
}