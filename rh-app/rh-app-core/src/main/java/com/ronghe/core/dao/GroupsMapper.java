package com.ronghe.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ronghe.model.group.GroupRequest;
import com.ronghe.model.group.GroupUsers;
import com.ronghe.model.group.Groups;

@Mapper
public interface GroupsMapper {
	
	
	@Select("SELECT b.* from (SELECT group_id FROM rh_group_users WHERE user_id = #{userId}) as a LEFT JOIN rh_groups as b on a.group_id=b.id")
	public List<Groups> serverQueryMyGroups(String userId);
	
	@Insert("INSERT INTO `rh_group_users` (`id`, `group_id`, `user_id`, `create_time`, `is_delete`)"
			+ " VALUES (#{id},#{groupId},#{userId},now(), '0')")
	public boolean serverInsertUserRelationGroup(GroupUsers groupUsers);
	
	@Delete("DELETE from rh_group_users WHERE group_id=#{groupId} and user_id=#{userId}")
	public boolean serverDeleteUserRelationGroup(Map<String,String> map);
	
	
	@Select("SELECT * from rh_group_users WHERE group_id=#{groupId} and is_delete='0'")
	public List<GroupUsers> serverMemberList(String groupId);
	
	@Select("SELECT * from rh_groups WHERE (group_number like #{searchInfo} or group_name like #{searchInfo}) and is_delete='0'")
	public List<Groups> serverSearchGroups(String searchInfo);	
	
	@Select("SELECT * from rh_groups WHERE id=#{id} and is_delete='0'")
	public Groups serverSearchGroupsById(String id);
	
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
	
	@Update("<script>"
			+ "UPDATE `rh_groups` "
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
	public Integer serverUpdateGroupInfo(Groups group);
	
}
