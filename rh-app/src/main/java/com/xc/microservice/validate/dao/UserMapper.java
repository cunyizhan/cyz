package com.xc.microservice.validate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xc.microservice.validate.model.chat.Users;



@Mapper
public interface UserMapper {
	
	
	@Select("select * from rh_users where phone = #{phone} and is_delete='0'")
	public List<Users> serverQueryByPhone(String phone);
	
	@Select("select * from rh_users where id = #{id} and is_delete='0'")
	public Users serverQueryById(String id);
	
	
	@Insert("INSERT INTO `ronghe`.`rh_users` "
			+ "(`id`,`username`,`nickname`, `gender`, `phone`, `password`, "
			+ "`email`, `citycode`, `faceImage`, `faceImageBig`, `qrcode`, `cid`, "
			+ "`regist_date`, `is_delete`,`address`,`addresscode`) "
			+ "VALUES "
			+ "(#{id},#{username},#{nickname},#{gender},#{phone},#{password},"
			+ " #{email},#{citycode},#{faceImage},#{faceImageBig},#{qrcode},#{cid},"
			+ " now(), '0', #{address}, #{addresscode});")
	public void serverInsertUser(Users users);
	
	
	@Update("<script>"
			+ "UPDATE `rh_users` "
			+ "<trim prefix='set' suffixOverrides=','>"
			+ "<if test=\"username!=null and username!=''\">`username`=#{username},</if>"
			+ "<if test=\"gender!=null and gender!=''\">`gender`=#{gender},</if> "
			+ "<if test=\"phone!=null and phone!=''\">`phone`=#{phone},</if>"
			+ "<if test=\"password!=null and password!=''\">`password`=#{password},</if>"
			+ "<if test=\"email!=null and email!=''\">`email`=#{email},</if>"
			+ "<if test=\"citycode!=null and citycode!=''\">`citycode`=#{citycode},</if>"
			+ "<if test=\"faceImage!=null and faceImage!=''\">`faceImage`=#{faceImage},</if>"
			+ "<if test=\"faceImageBig!=null and faceImageBig!=''\">`faceImageBig`=#{faceImageBig},</if>"
			+ "<if test=\"qrcode!=null and qrcode!=''\">`qrcode`=#{qrcode},</if>"
			+ "<if test=\"cid!=null and cid!=''\">`cid`=#{cid},</if>"
			+ "</trim>"
			+ "WHERE `id`=#{id}"
			+ "</script>")
	public Integer serverUpdateUser(Users users);
	
	@Select("select b.* from (select my_friend_user_id from rh_my_friends where my_user_id=#{id} ) as a "
			+ "LEFT JOIN rh_users as b ON a.my_friend_user_id=b.id WHERE b.phone LIKE #{searchInfo} OR b.nickname LIKE #{searchInfo}")
	public List<Users> serverSearchMyFriend(@Param("id") String id,@Param("searchInfo")String searchInfo);
}
