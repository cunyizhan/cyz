package com.xc.microservice.validate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.xc.microservice.validate.model.group.GroupRequest;
import com.xc.microservice.validate.model.group.GroupUsers;
import com.xc.microservice.validate.model.group.Groups;

@Mapper
public interface GroupsMapper {
	
	
	@Select("SELECT b.* from (SELECT group_id FROM rh_group_users WHERE user_id = #{userId}) as a LEFT JOIN rh_groups as b on a.group_id=b.id")
	public List<Groups> serverQueryMyGroups(String userId);
	
	@Insert("INSERT INTO `rh_group_users` (`id`, `group_id`, `user_id`, `create_time`, `is_delete`)"
			+ " VALUES (#{id},#{groupId},#{userId},now(), '0')")
	public boolean serverInsertUserRelationGroup(GroupUsers groupUsers);
	
	
	@Select("SELECT * from rh_group_users WHERE group_id=#{groupId} and is_delete='0'")
	public List<GroupUsers> serverMemberList(String groupId);
	
	@Select("SELECT * from rh_groups WHERE (group_number like #{searchInfo} or group_name like #{searchInfo}) and is_delete='0'")
	public List<Groups> serverSearchGroups(String searchInfo);	
	
	@Select("SELECT b.* from (SELECT group_id FROM rh_group_users WHERE user_id=#{userId} and is_delete='0') as a  LEFT JOIN rh_groups as b on a.group_id=b.id")
	public List<Groups> serverListByUserId(String userId);
	
	
	@Insert("INSERT INTO `ronghe`.`rh_groups` "
			+ "(`id`, `group_number`, `group_faceImage_big`, "
			+ "`group_name`, `group_description`, `qrCode`, "
			+ "`users_num`, `create_time`, `is_delete`) "
			+ "VALUES "
			+ "(#{id},#{groupNumber},#{groupFaceimageBig},"
			+ " #{groupName},#{groupDescription},#{qrcode},"
			+ "0, now(), '0')")
	public boolean serverInsertGroups(Groups groups);
	
	@Insert("INSERT INTO `ronghe`.`rh_group_request` "
			+ "(`id`, `accept_group_id`, `send_user_id`, "
			+ "`accept_status`, `create_time`) "
			+ "VALUES (#{id},#{acceptGroupId},#{sendUserId}, '0', now())")
	public boolean serverJoinInsertGroups(GroupRequest request);
	
	
}
