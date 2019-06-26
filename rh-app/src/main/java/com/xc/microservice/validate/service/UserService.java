package com.xc.microservice.validate.service;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.File;
import java.io.IOException;
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
import com.xc.microservice.validate.model.group.GroupUsers;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.vo.FriendRequestVO;
import com.xc.microservice.validate.model.vo.MyFriendsVO;
import com.xc.microservice.validate.util.aes.SymmetricEncoder;
import com.xc.microservice.validate.util.rh.JsonUtils;


@Service
@Slf4j
public class UserService {
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	@Autowired
	private GroupsMapper groupsMapper;

	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ChatMsgRepository chatMsgRepository;
	
	@Autowired
	private MyFriendsMapper myFriendsMapper;
	
	@Autowired
	private MyFriendsRequestMapper friendsRequestMapper;
	
	@Autowired
	private PushMsgService pushMsgService;
	
	
	
	@Autowired
	private Sid sid;
	
	@Autowired
	private QRCodeUtils qrCodeUtils;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	public boolean queryPhoneIsExist(String phone) {
		
		List<Users> result = userMapper.serverQueryByPhone(phone);
		
		return result != null && result.size()>0 ? true : false;
	}

	public Users queryUserForLogin(Users user) {
		List<Users> users =  userMapper.serverQueryByPhone(user.getPhone());
		log.info("查询到用户数量{}",users.size());
		if(users!=null && users.size()>0){
			Users u = users.get(0);
			//String login_password = SymmetricEncoder.AESEncode(user.getPassword());
			if(!user.getPassword().equals(u.getPassword())){
				return null;
			}
			if(!user.getCid().equals(u.getCid())){
				u.setCid(user.getCid());
				userMapper.serverUpdateUser(user);
			}
			return u;
		}
		return null;
	}
	
	public Users queryUserForCodeLogin(Users user) {
		List<Users> users =  userMapper.serverQueryByPhone(user.getPhone());
		log.info("查询到用户数量{}",users.size());
		if(users!=null && users.size()>0){
			Users u = users.get(0);
			if(!user.getCid().equals(u.getCid())){
				u.setCid(user.getCid());
				userMapper.serverUpdateUser(user);
			}
			return u;
		}
		return null;
	}

	public Users saveUser(Users user) {
		String userId = sid.nextShort();
		// 为每个用户生成一个唯一的二维码
		String qrCodePath = baseUrl + userId + "qrcode.png";
		//rh_qrcode:[phone]
		qrCodeUtils.createQRCode(qrCodePath, "rh_qrcode:"+ user.getPhone());
		MultipartFile qrCodeFile = FileUtils.fileToMultipart(qrCodePath);
		
		String qrCodeUrl = "";
//		try {
//			qrCodeUrl = fastDFSClient.uploadQRCode(qrCodeFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally{
//			//上传完一定要将图片删除
//			File file = new File(qrCodePath);
//			if (file.exists()  && file.isFile()) {
//				file.delete();
//			}
//		}
		user.setQrcode("/group1/"+qrCodeUrl);
		user.setId(userId);
		userMapper.serverInsertUser(user);
		
		//保存用户和群关系
		GroupUsers groupUsers = new GroupUsers();
		groupUsers.setId(userId);
		groupUsers.setUserId(userId);
		groupUsers.setGroupId(user.getCitycode());
		groupsMapper.serverInsertUserRelationGroup(groupUsers);
		
		//群组发送一条未读群消息
		ChatMsg chatMsg = new ChatMsg();
		chatMsg.setGroupId(user.getCitycode());
		chatMsg.setMsg("欢迎加入群里");
		chatMsg.setMsgId(userId);
		chatMsg.setMsgType(MsgTypeEnum.GROUP+"");
		chatMsg.setReceiverId(userId);
		chatMsg.setSenderId(user.getCitycode());
		saveMsg(chatMsg);
		
		return user;
	}

	public Users updateUserInfo(Users user) {
		userMapper.serverUpdateUser(user);
		return queryUserById(user.getId());
	}
	
	public Users queryUserById(String userId) {
		return userMapper.serverQueryById(userId);
	}

	public Integer preconditionSearchFriends(String myUserId, String friendPhone) {

		Users user = queryUserInfoByPhone(friendPhone);
		// 1. 搜索的用户如果不存在，返回[无此用户]
		if (user == null) {
			return SearchFriendsStatusEnum.USER_NOT_EXIST.status;
		}
		
		// 2. 搜索账号是你自己，返回[不能添加自己]
		if (user.getId().equals(myUserId)) {
			return SearchFriendsStatusEnum.NOT_YOURSELF.status;
		}
		
		// 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
		MyFriends myfriend = new MyFriends();
		myfriend.setMyUserId(myUserId);
		myfriend.setMyFriendUserId(user.getId());
		List<MyFriends> myFriends = myFriendsMapper.serverQueryMyFriends(myfriend);
		if (myFriends != null && myFriends.size()>0) {
			return SearchFriendsStatusEnum.ALREADY_FRIENDS.status;
		}
		return SearchFriendsStatusEnum.SUCCESS.status;
	}
	
	
	public Users queryUserInfoByPhone(String phone) {
		List<Users> users =  userMapper.serverQueryByPhone(phone);
		if(users!=null && users.size()>0){
			Users u = users.get(0);
			return u;
		}
		return null;
	}
	
	public List<Users> queryMyFriend(String myUserId,String searchInfo){
		searchInfo="%"+searchInfo+"%";
		return userMapper.serverSearchMyFriend(myUserId, searchInfo);
	}
	
	public void sendFriendRequest(String myUserId, String friendPhone) {
		// 根据用户名把朋友信息查询出来
		Users friend = queryUserInfoByPhone(friendPhone);
		// 1. 查询发送好友请求记录表
		FriendsRequest req = new FriendsRequest();
		req.setSendUserId(myUserId);
		req.setAcceptUserId(friend.getId());
		List<FriendsRequest> friendRequest = friendsRequestMapper.serverQueryMyFriendsRequest(req);
		if (friendRequest == null || friendRequest.size()==0) {
			// 2. 如果不是你的好友，并且好友记录没有添加，则新增好友请求记录
			String requestId = sid.nextShort();
			FriendsRequest request = new FriendsRequest();
			request.setId(requestId);
			request.setSendUserId(myUserId);
			request.setAcceptUserId(friend.getId());
			request.setCreateTime(new Date());
			friendsRequestMapper.serverInsertFriendsRequest(request);
			
			pushMsgService.savePushMsgFromRequestToList(friend.getId(), myUserId);
		}
	}

	
	public List<FriendRequestVO> queryFriendRequestList(String acceptUserId) {
		return friendsRequestMapper.serverQueryMyFriendsRequestVO(acceptUserId);
	}

	public void deleteFriendRequest(String sendUserId, String acceptUserId) {
		FriendsRequest req =new FriendsRequest();
		req.setAcceptUserId(acceptUserId);
		req.setSendUserId(sendUserId);
		friendsRequestMapper.serverDeleteRequest(req);
	}
	public void passFriendRequest(String sendUserId, String acceptUserId) {
		saveFriends(sendUserId, acceptUserId);
		saveFriends(acceptUserId, sendUserId);
		deleteFriendRequest(sendUserId, acceptUserId);
		
		Channel sendChannel = UserChannelRel.get(sendUserId);
		if (sendChannel != null) {
			// 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
			DataContent dataContent = new DataContent();
			dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
			
			sendChannel.writeAndFlush(
					new TextWebSocketFrame(
							JsonUtils.objectToJson(dataContent)));
		}
	}
	
	private void saveFriends(String sendUserId, String acceptUserId) {
		MyFriends myFriends = new MyFriends();
		String recordId = sid.nextShort();
		myFriends.setId(recordId);
		myFriends.setMyFriendUserId(acceptUserId);
		myFriends.setMyUserId(sendUserId);
		myFriendsMapper.serverInserMyFriend(myFriends);
	}

	
	public List<MyFriendsVO> queryMyFriends(String userId) {
		List<MyFriendsVO> myFirends = myFriendsMapper.serverQueryMyFriendsVO(userId);
		return myFirends;
	}

	public String saveMsg(ChatMsg chatMsg) {
		TChatMsg msgDB = new TChatMsg();
		String msgId = sid.nextShort();
		msgDB.setSid(msgId);
		msgDB.setSendUserId(chatMsg.getSenderId());
		msgDB.setAcceptUserId(chatMsg.getReceiverId());
		msgDB.setCreateTime(new Date());
		msgDB.setSignFlag(MsgSignFlagEnum.unsign.type);
		msgDB.setMsg(chatMsg.getMsg());
		msgDB.setMsgType(chatMsg.getMsgType());
		msgDB.setGroupId(chatMsg.getGroupId());
		chatMsgRepository.insert(msgDB);
		return msgId;
	}


	public void updateMsgStatus(List<String> msgIdList){
		for(String msgId : msgIdList){
			WriteResult res = mongoTemplate.updateMulti(query(where("sid").is(msgId)), 
					Update.update("signFlag",MsgSignFlagEnum.signed.type), TChatMsg.class);
		}
		
	}

	
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
}
