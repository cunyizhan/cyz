package com.xc.microservice.validate.service;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mongodb.WriteResult;
import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.config.netty.ChatMsg;
import com.xc.microservice.validate.config.netty.DataContent;
import com.xc.microservice.validate.config.netty.QRCodeUtils;
import com.xc.microservice.validate.config.netty.UserChannelRel;
import com.xc.microservice.validate.controller.UserCenterController;
import com.xc.microservice.validate.dao.ChatMsgRepository;
import com.xc.microservice.validate.dao.GroupsMapper;
import com.xc.microservice.validate.dao.MyFriendsMapper;
import com.xc.microservice.validate.dao.MyFriendsRequestMapper;
import com.xc.microservice.validate.dao.UserMapper;
import com.xc.microservice.validate.dao.UserRepository;
import com.xc.microservice.validate.model.chat.FriendsRequest;
import com.xc.microservice.validate.model.chat.MyFriends;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.enums.MsgActionEnum;
import com.xc.microservice.validate.model.enums.MsgSignFlagEnum;
import com.xc.microservice.validate.model.enums.MsgTypeEnum;
import com.xc.microservice.validate.model.enums.SearchFriendsStatusEnum;
import com.xc.microservice.validate.model.group.GroupRequest;
import com.xc.microservice.validate.model.group.GroupUsers;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.group.GroupsDto;
import com.xc.microservice.validate.model.vo.FriendRequestVO;
import com.xc.microservice.validate.model.vo.GroupsVO;
import com.xc.microservice.validate.model.vo.MyFriendsVO;
import com.xc.microservice.validate.util.aes.SymmetricEncoder;
import com.xc.microservice.validate.util.rh.JsonUtils;

import org.apache.commons.lang3.StringUtils;

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
	public void create(GroupsVO vo) {
		Groups groups = new Groups();
		String groupId = vo.getAddressCode().hashCode()+"";
		groups.setId(groupId);
		groups.setGroupDescription(vo.getGroupDescription());
		groups.setGroupFaceimageBig(vo.getGroupFaceimageBig());
		groups.setGroupName(vo.getGroupName());
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
		} catch (IOException e) {
				e.printStackTrace();
		}finally{
				//上传完一定要将图片删除
				File file = new File(qrCodePath);
				if (file.exists()  && file.isFile()) {
					file.delete();
				}
		}
		groups.setQrcode("/group1/"+qrCodeUrl);	
		groupsMapper.serverInsertGroups(groups);
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
