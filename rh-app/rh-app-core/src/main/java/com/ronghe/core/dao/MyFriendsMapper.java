package com.ronghe.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ronghe.model.chat.MyFriends;
import com.ronghe.model.vo.MyFriendsVO;



@Mapper
public interface MyFriendsMapper {
	
	
	@Select("select * from rh_my_friends where my_user_id = #{myUserId} and my_friend_user_id=#{myFriendUserId} and is_delete='0'")
	public List<MyFriends> serverQueryMyFriends(MyFriends myFriends);
	
	@Insert("INSERT INTO `ronghe`.`rh_my_friends` "
			+ "(`id`, `my_user_id`, `my_friend_user_id`, `create_time`, `is_delete`) "
			+ "VALUES "
			+ "(#{id},#{myUserId},#{myFriendUserId},now(), '0')")
	public void serverInserMyFriend(MyFriends myFriends);
	
	@Select("SELECT b.id as friendUserId,b.username as friendUsername,b.faceImage as friendFaceImage,b.nickname as friendNickname,a.remark as remark from (select my_friend_user_id,remark from rh_my_friends where my_user_id =#{userId}) as a LEFT JOIN rh_users as b on a.my_friend_user_id=b.id")
	public List<MyFriendsVO> serverQueryMyFriendsVO(String userId);
	
	@Delete("delete from rh_my_friends where my_user_id = #{myUserId} and my_friend_user_id=#{myFriendUserId} and is_delete='0'")
	public Integer serverDeleteMyFriend(MyFriends myFriends);
	
	
	@Update("<script>"
			+ "UPDATE `rh_my_friends` "
			+ "<trim prefix='set' suffixOverrides=','>"
			+ "<if test=\"remark!=null and remark!=''\">`remark`=#{remark},</if>"
			+ "</trim>"
			+ "WHERE `my_user_id`=#{myUserId} and `my_friend_user_id`=#{myFriendUserId}"
			+ "</script>")
	public Integer serverUpdateFriend(MyFriends myFriends);
	
}
