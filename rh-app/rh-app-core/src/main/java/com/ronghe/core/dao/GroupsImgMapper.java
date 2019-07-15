package com.ronghe.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.ronghe.model.group.Groups;
import com.ronghe.model.group.GroupsImg;

@Mapper
public interface GroupsImgMapper {
	
	@Insert("INSERT INTO `rh_groups_img` (`id`, `group_id`, `img_name`, `create_time`, `img_path`, `img_desc`, `img_sn`)"
			+ " VALUES (#{id},#{groupId},#{imgName},now(), #{imgPath},#{imgDesc},#{imgSn})")
	public boolean serverInsertGroupImg(GroupsImg groupImg);
	
	@Select("select * from rh_groups_img where group_id=#{groupId} order by img_sn")
	public List<GroupsImg> serverSelectGroupImg(String groupId);
	
	@Select("select * from rh_groups_img where id=#{id}")
	public GroupsImg serverGetGroupImg(String id);
	
	@Select("select max(img_sn) from rh_groups_img where group_id=#{groupId}")
	public Integer serverSelectGroupImgSn(String groupId);
	
	@Update("<script>"
			+ "UPDATE `rh_groups_img` "
			+ "<trim prefix='set' suffixOverrides=','>"
			+ "<if test=\"imgDesc!=null and imgDesc!=''\">`img_desc`=#{imgDesc},</if>"
			+ "<if test=\"imgSn!=null and imgSn!=''\">`img_sn`=#{imgSn},</if>"
			+ "</trim>"
			+ "WHERE `id`=#{id}"
			+ "</script>")
	public Integer serverUpdateGroupImg(GroupsImg groupImg);
	
	@Delete("delete from rh_groups_img where group_id=#{groupId}")
	public Integer serverDeleteGroupImg(String groupId);
	
	@Delete("delete from rh_groups_img where id=#{id}")
	public Integer serverDeleteGroupImgById(String id);
}
