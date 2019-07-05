package com.xc.microservice.validate.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 用户实体类--用户注册登录
 * @author zk
 *
 */
@Document(collection="t_users")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'phone': 1,'cid': 1,'createDate': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class TUser {
	@Id
	private String id;
	
	private String num;
	//用户名
	private String username;
	private String nickname;
	//性别
	private String gender;
	//手机号
	private String phone;
	//密码
	private String password;
	
	//邮箱
	private String email;
	//地区编码
	private String citycode;
	//头像缩率图--聊天用
	private String faceImage;
	//大头像
	private String faceImageBig;
	
	/**
     * 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    private String qrcode;
    
    //每台设备的唯一标识
    private String cid;
	
	//注册时间
	private String registDate;
	//上次登录时间
	private String lastDate;
	private String isDelete;
	
}
