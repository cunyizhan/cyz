package com.xc.microservice.validate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.usergroup.UserGroup;
import com.xc.microservice.validate.model.usergroup.UserGroupRequest;
import com.xc.microservice.validate.model.usergroup.UserGroupUsers;

@Mapper
public interface UserGroupsMapper {
	
	
	@Select("SELECT b.* from (SELECT group_id FROM rh_user_group_users WHERE user_id = #{userId}) as a LEFT JOIN rh_user_groups as b on a.group_id=b.id")
	public List<UserGroup> serverQueryMyGroups(String userId);
	
	@Insert("INSERT INTO `rh_user_group_users` (`id`, `group_id`, `user_id`, `create_time`, `is_delete`,`user_level`)"
			+ " VALUES (#{id},#{groupId},#{userId},now(), '0',#{userLevel})")
	public boolean serverInsertUserRelationGroup(UserGroupUsers groupUsers);
	
	
	@Select("SELECT * from rh_user_group_users WHERE group_id=#{groupId} and is_delete='0'")
	public List<UserGroupUsers> serverMemberList(String groupId);
	
	@Select("SELECT * from rh_user_group_users WHERE group_id=#{groupId} and user_id=#{userId} and is_delete='0'")
	public UserGroupUsers serverGroupUserById(@Param("groupId")String groupId,@Param("userId")String userId);
	
	@Select("SELECT * from rh_user_groups WHERE (group_number like #{searchInfo} or group_name like #{searchInfo}) and is_delete='0'")
	public List<UserGroup> serverSearchGroups(String searchInfo);	
	
	@Select("SELECT b.* from (SELECT group_id FROM rh_user_group_users WHERE user_id=#{userId} and is_delete='0') as a  LEFT JOIN rh_user_groups as b on a.group_id=b.id")
	public List<UserGroup> serverListByUserId(String userId);
	
	
	@Insert("INSERT INTO `ronghe`.`rh_user_groups` "
			+ "(`id`, `group_number`, `group_faceImage_big`, "
			+ "`group_name`, `group_description`, `qrCode`, "
			+ "`users_num`, `create_time`, `is_delete`) "
			+ "VALUES "
			+ "(#{id},#{groupNumber},#{groupFaceimageBig},"
			+ " #{groupName},#{groupDescription},#{qrcode},"
			+ "#{usersNum}, now(), '0')")
	public boolean serverInsertGroups(UserGroup groups);
	
	@Insert("INSERT INTO `ronghe`.`rh_user_group_request` "
			+ "(`id`, `accept_group_id`, `send_user_id`, "
			+ "`accept_status`, `create_time`) "
			+ "VALUES (#{id},#{acceptGroupId},#{sendUserId}, '0', now())")
	public boolean serverJoinInsertGroups(UserGroupRequest request);
	
	@Update("<script>"
			+ "UPDATE `rh_user_groups` "
			+ "<trim prefix='set' suffixOverrides=','>"
			+ "<if test=\"groupNumber!=null and groupNumber!=''\">`group_number`=#{groupNumber},</if>"
			+ "<if test=\"groupFaceimageBig!=null and groupFaceimageBig!=''\">`group_faceImage_big`=#{groupFaceimageBig},</if>"
			+ "<if test=\"groupName!=null and groupName!=''\">`group_name`=#{groupName},</if> "
			+ "<if test=\"groupDescription!=null and groupDescription!=''\">`group_description`=#{groupDescription},</if>"
			+ "<if test=\"qrcode!=null and qrcode!=''\">`qrCode`=#{qrcode},</if>"
			+ "<if test=\"usersNum!=null \">`users_num`=#{usersNum},</if>"
			+ "<if test=\"isDelete!=null and isDelete!=''\">`is_delete`=#{isDelete},</if>"
			+ "</trim>"
			+ "WHERE `id`=#{id}"
			+ "</script>")
	public Integer serverUpdateUserGroup(UserGroup userGroup);
	
	@Select("select * from rh_user_groups where id = #{groupId} and is_delete='0'")
	public UserGroup serverQueryMyGroupsById(String groupId);
	
	@Select("select * from rh_user_group_users where group_id= #{groupId} and user_id=#{userId} and is_delete='0'")
	public List<UserGroupUsers> serverGetGroupUsers(@Param("groupId")String groupId,@Param("userId")String userId);
	
	@Delete("delete from rh_user_group_users where group_id= #{groupId} and user_id=#{userId}")
	public int serverDeleteGroupUserById(@Param("groupId")String groupId,@Param("userId")String userId);
	
	@Delete("delete from rh_user_group_users where group_id= #{groupId}")
	public int serverDeleteGroupUsers(String groupId);
	
	@Delete("delete from rh_user_groups where id= #{groupId}")
	public int serverDeleteGroup(String groupId);
	
}
