package com.xc.microservice.validate.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.config.netty.QRCodeUtils;
import com.xc.microservice.validate.dao.UserGroupsMapper;
import com.xc.microservice.validate.dao.UserMapper;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.enums.MsgSignFlagEnum;
import com.xc.microservice.validate.model.usergroup.UserGroup;
import com.xc.microservice.validate.model.usergroup.UserGroupRequest;
import com.xc.microservice.validate.model.usergroup.UserGroupUsers;
import com.xc.microservice.validate.model.vo.GroupsVO;


@Service
@Slf4j
public class UserGroupService {
	@Autowired
	private UserGroupsMapper userGroupsMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private QRCodeUtils qrCodeUtils;
	
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	/**
	 * 查询未签收消息
	 * @param aid
	 * @param openId
	 * @return
	 */
	public List<TChatMsg> getUnReadMsgList(String acceptUserId){
		Query query = new Query();
		if (acceptUserId!=null&&acceptUserId!="") {
			query.addCriteria(
				    new Criteria().andOperator(
				        Criteria.where("acceptUserId").is(acceptUserId),
				        Criteria.where("signFlag").is(MsgSignFlagEnum.unsign.type)
				        )
				    );
        }
		return mongoTemplate.find(query, TChatMsg.class);
	}
	
	public List<UserGroup> queryMyGroups(String userId){
		List<UserGroup>  groups = userGroupsMapper.serverQueryMyGroups(userId);
		return groups;
	}
	
	/**
	 * 创建群聊
	 * @param param
	 */
	public UserGroup create(Map<String,String> map) {
		UserGroup groups = new UserGroup();
		String groupId = sid.nextShort();
		String userId=map.get("userId");
		String friendIds=map.get("friendIds");
		groups.setId(groupId);
		groups.setGroupName(getGroupName(userId, friendIds));
		groups.setGroupNumber(groupId);
		groups.setUsersNum(friendIds.split(",").length+1);
		groups.setIsDelete("0");
		// 为每个用户生成一个唯一的二维码
		String qrCodePath = baseUrl + groupId + "qrcode.png";
		//rh_qrcode:[phone]
		qrCodeUtils.createQRCode(qrCodePath, "rh_user_group:"+ groupId);
		MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
				
		String qrCodeUrl = "";
		try {
			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
		} catch (Exception e) {
			log.error("",e);
		}finally{
			try {
				//上传完一定要将图片删除
				File file = new File(qrCodePath);
				if (file.exists()  && file.isFile()) {
					file.delete();
				}
			} catch (Exception e2) {
				log.error("",e2);
			}
		}
		groups.setQrcode("/group1/"+qrCodeUrl);	
		userGroupsMapper.serverInsertGroups(groups);
		saveUserGroupUsers(userId, friendIds, groupId);
		return userGroupsMapper.serverQueryMyGroupsById(groupId);
	}
	
	/**
	 * 生成群名(获取前四名成员的名字)
	 * @param userId
	 * @param friendIds
	 * @return
	 */
	private String getGroupName(String userId,String friendIds){
		Users user=userMapper.serverQueryById(userId);
		String[] fids=friendIds.split(",");
		int l=3;
		if(fids.length<3){
			l=fids.length;
		}
		String gName=user.getNickname();
		for (int i = 0; i < l; i++) {
			String n=fids[i];
			Users f=userMapper.serverQueryById(n);
			gName+=","+f.getNickname();
		}
		return gName;
	}
	/**
	 * 储存群成员信息
	 * @param userId
	 * @param friendIds
	 * @param groupId
	 */
	private void saveUserGroupUsers(String userId,String friendIds,String groupId){
		String[] fids=friendIds.split(",");
		saveUserGroupUser(userId, groupId,"0");
		for (String string : fids) {
			saveUserGroupUser(string, groupId,"1");
		}
	}
	
	private void saveUserGroupUser(String userId,String groupId,String level){
		UserGroupUsers gUser=new UserGroupUsers();
		gUser.setGroupId(groupId);
		gUser.setUserId(userId);
		gUser.setUserLevel(level);
		gUser.setId(sid.nextShort());
		userGroupsMapper.serverInsertUserRelationGroup(gUser);
	}
	/**
	 * 邀请好友入群
	 * @param userId
	 * @param friendIds
	 * @param groupId
	 */
	public String inviteUserGroupUser(String userId,String friendIds,String groupId){
		UserGroup ug=userGroupsMapper.serverQueryMyGroupsById(groupId);
		String[] fids=friendIds.split(",");
		for (String string : fids) {
			List<UserGroupUsers> list=userGroupsMapper.serverGetGroupUsers(groupId,string);
			if(list!=null&&list.size()>0){
				Users u = userMapper.serverQueryById(string);
				return "用户【"+u.getNickname()+"】已在群中";
			}
			saveUserGroupUser(string, groupId,"1");
		}
		if(ug.getUsersNum()!=null){
			ug.setUsersNum(ug.getUsersNum()+fids.length);
		}
		userGroupsMapper.serverUpdateUserGroup(ug);
		return "ok";
	}
	
	/**
	 * 获取群成员信息
	 * 检查参数
	 * @param groupId
	 * @return
	 */
	public List<Map<String,String>> memberList(String groupId) {
		List<UserGroupUsers> groupUserss= userGroupsMapper.serverMemberList(groupId);
		List<Map<String,String>> users= new ArrayList<Map<String,String>>(); 
		for(UserGroupUsers groupUsers : groupUserss){
			Map<String,String> map=new HashMap<String, String>();
			Users u = userMapper.serverQueryById(groupUsers.getUserId());
			map.putAll((Map<String,String>)JSONObject.toJSON(u));
			map.put("groupUserId", groupUsers.getId());
			map.put("userLevel", groupUsers.getUserLevel());
			users.add(map);
		}
		return users;
	}

	/**
	 * 获取用户已加入的群
	 * 检查参数
	 * @param userId
	 * @return
	 */
	public List<UserGroup> listByUserId(String userId) {
		return userGroupsMapper.serverListByUserId(userId);
	}
	
	/**
	 * 搜索组群
	 * @param searchInfo
	 * @return
	 */
	public List<UserGroup> serverSearchGroups(String searchInfo) {
		searchInfo="%"+searchInfo+"%";
		return userGroupsMapper.serverSearchGroups(searchInfo);
	}
	
	/**
	 * 入群请求
	 */
	public boolean join(UserGroupRequest  request) {
		request.setId(sid.nextShort());
		boolean flag = userGroupsMapper.serverJoinInsertGroups(request);
		return flag;
	}
	
	/**
	 * 修改群信息
	 * @param userGroup
	 * @return
	 */
	public UserGroup updateUserGroup(UserGroup userGroup){
		userGroupsMapper.serverUpdateUserGroup(userGroup);
		return userGroupsMapper.serverQueryMyGroupsById(userGroup.getId());
	}
	/**
	 * 退出群聊（若是群主便解散群）
	 * @param groupId
	 * @param userId
	 */
	public void deleteGroupUserById(String groupId,String userId){
		UserGroupUsers ugu=userGroupsMapper.serverGroupUserById(groupId, userId);
		if("0".equals(ugu.getUserLevel())){
			userGroupsMapper.serverDeleteGroup(groupId);
			userGroupsMapper.serverDeleteGroupUsers(groupId);
			return;
		}
		userGroupsMapper.serverDeleteGroupUserById(userId, groupId);
		UserGroup ug=userGroupsMapper.serverQueryMyGroupsById(groupId);
		ug.setUsersNum(ug.getUsersNum()-1);
		if(ug.getUsersNum()<2){
			userGroupsMapper.serverDeleteGroup(groupId);
			userGroupsMapper.serverDeleteGroupUsers(groupId);
		}else{
			userGroupsMapper.serverUpdateUserGroup(ug);
		}
	}
	
	/**
	 * 解散群聊
	 * @param groupId
	 */
	public void deleteGroup(String groupId){
		userGroupsMapper.serverDeleteGroup(groupId);
		userGroupsMapper.serverDeleteGroupUsers(groupId);
	}
}
