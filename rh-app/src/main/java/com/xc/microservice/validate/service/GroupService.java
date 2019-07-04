package com.xc.microservice.validate.service;

import java.io.File;
import java.util.ArrayList;
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

import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.config.netty.QRCodeUtils;
import com.xc.microservice.validate.dao.GroupsMapper;
import com.xc.microservice.validate.dao.UserMapper;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.enums.MsgSignFlagEnum;
import com.xc.microservice.validate.model.group.GroupRequest;
import com.xc.microservice.validate.model.group.GroupUsers;
import com.xc.microservice.validate.model.group.Groups;

@Service
@Slf4j
public class GroupService {
	@Autowired
	private GroupsMapper groupsMapper;
	
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
	
	public List<Groups> queryMyGroups(String userId){
		List<Groups>  groups = groupsMapper.serverQueryMyGroups(userId);
		return groups;
	}
	
	/**
	 * 创建群聊
	 * @param param
	 */
	public Groups create(Map<String,String> map) {
		Groups groups = new Groups();
		String groupId = map.get("address").hashCode()+"";
		groups.setId(groupId);
		groups.setGroupDescription( map.get("groupDescription"));
		groups.setGroupFaceimageBig( map.get("groupFaceimageBig"));
		groups.setGroupName( map.get("groupName"));
		groups.setGroupNumber(groupId);
		groups.setIsDelete("0");
		// 为每个用户生成一个唯一的二维码
		String qrCodePath = baseUrl + groupId + "qrcode.png";
		//rh_qrcode:[phone]
		qrCodeUtils.createQRCode(qrCodePath, "rh_group:"+ groupId);
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
		groupsMapper.serverInsertGroups(groups);
		return groupsMapper.serverSearchGroupsById(groupId);
	}
	

	/**
	 * 修改群信息
	 * @param userGroup
	 * @return
	 */
	public Groups updateGroupInfo(Groups group){
		groupsMapper.serverUpdateGroupInfo(group);
		return groupsMapper.serverSearchGroupsById(group.getId());
	}
	
	/**
	 * 获取群成员信息
	 * 检查参数
	 * @param groupId
	 * @return
	 */
	public List<Users> memberList(String groupId) {
		List<GroupUsers> groupUserss= groupsMapper.serverMemberList(groupId);
		List<Users> users= new ArrayList<Users>(); 
		for(GroupUsers groupUsers : groupUserss){
			Users u = userMapper.serverQueryById(groupUsers.getUserId());
			users.add(u);
		}
		return users;
		
	}

	/**
	 * 获取用户已加入的群
	 * 检查参数
	 * @param userId
	 * @return
	 */
	public List<Groups> listByUserId(String userId) {
		return groupsMapper.serverListByUserId(userId);
	}
	
	/**
	 * 搜索组群
	 * @param searchInfo
	 * @return
	 */
	public List<Groups> serverSearchGroups(String searchInfo) {
		searchInfo="%"+searchInfo+"%";
		return groupsMapper.serverSearchGroups(searchInfo);
	}
	
	/**
	 * 入群请求
	 */
	public boolean join(GroupRequest  request) {
		request.setId(sid.nextShort());
		boolean flag = groupsMapper.serverJoinInsertGroups(request);
		return flag;
	}
}
