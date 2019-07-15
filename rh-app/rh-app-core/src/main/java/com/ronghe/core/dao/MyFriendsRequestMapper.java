package com.ronghe.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ronghe.model.chat.FriendsRequest;
import com.ronghe.model.vo.FriendRequestVO;



@Mapper
public interface MyFriendsRequestMapper {
	
	
	@Select("select * from rh_my_friend_request where send_user_id = #{sendUserId} and accept_user_id=#{acceptUserId}")
	public List<FriendsRequest> serverQueryMyFriendsRequest(FriendsRequest request);
	
	@Insert("INSERT INTO `ronghe`.`rh_my_friend_request` "
			+ "(`id`, `send_user_id`, `accept_user_id`, `accept_status`, `create_time`) "
			+ "VALUES "
			+ "(#{id}, #{sendUserId}, #{acceptUserId}, '0',now())")
	public void serverInsertFriendsRequest(FriendsRequest request);
	
	@Select("SELECT id as sendUserId,phone as  sendPhone,faceImage as sendFaceImage,username as sendUserName    from (select send_user_id from rh_my_friend_request where accept_user_id=#{acceptUserId} ) as a LEFT JOIN rh_users as b on a.send_user_id=b.id")
	public List<FriendRequestVO> serverQueryMyFriendsRequestVO(String acceptUserId);
	
	@Delete("delete from rh_my_friend_request where send_user_id = #{sendUserId} and accept_user_id=#{acceptUserId}")
	public Integer serverDeleteRequest(FriendsRequest req);
	
	
	
	
	
}
