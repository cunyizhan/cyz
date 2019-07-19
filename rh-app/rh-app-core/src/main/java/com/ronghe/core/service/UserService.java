package com.ronghe.core.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.ronghe.common.idwork.Sid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;



//import com.mongodb.WriteResult;
import com.ronghe.common.fastdfs.FastDFSClient;
import com.ronghe.common.fastdfs.FileUtils;
//import com.ronghe.core.config.netty.ChatMsg;
//import com.ronghe.core.config.netty.DataContent;
//import com.ronghe.core.config.netty.UserChannelRel;
import com.ronghe.core.dao.GroupsMapper;
import com.ronghe.core.dao.MyFriendsMapper;
import com.ronghe.core.dao.MyFriendsRequestMapper;
import com.ronghe.core.dao.UserMapper;
import com.ronghe.model.chat.FriendsRequest;
import com.ronghe.model.chat.MyFriends;
import com.ronghe.model.chat.Users;
//import com.ronghe.core.model.entity.TChatMsg;
//import com.ronghe.core.model.enums.MsgActionEnum;
//import com.ronghe.core.model.enums.MsgSignFlagEnum;
//import com.ronghe.core.model.enums.MsgTypeEnum;
import com.ronghe.model.enums.SearchFriendsStatusEnum;
import com.ronghe.model.group.GroupUsers;
import com.ronghe.model.group.Groups;
import com.ronghe.model.vo.FriendRequestVO;
import com.ronghe.model.vo.MyFriendsVO;
import com.ronghe.common.util.QRCodeUtils;
//import com.ronghe.core.util.rh.JsonUtils;


@Service
@Slf4j
public class UserService {
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	
//	@Autowired
//	MongoTemplate mongoTemplate;
	
	@Autowired
	private GroupsMapper groupsMapper;

	@Autowired
	private UserMapper userMapper;
	
//	@Autowired
//	private UserRepository userRepository;
	
	@Autowired
	private MyFriendsMapper myFriendsMapper;
	
	@Autowired
	private MyFriendsRequestMapper friendsRequestMapper;
	
	
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
	/**
	 * 密码登录
	 * @param user
	 * @return
	 */
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
			}
			u.setLastDate(new Date());
			userMapper.serverUpdateUser(u);
			return u;
		}
		return null;
	}
	/**
	 * 短信登录
	 * @param user
	 * @return
	 */
	public Users queryUserForCodeLogin(Users user) {
		List<Users> users =  userMapper.serverQueryByPhone(user.getPhone());
		log.info("查询到用户数量{}",users.size());
		if(users!=null && users.size()>0){
			Users u = users.get(0);
			if(!user.getCid().equals(u.getCid())){
				u.setCid(user.getCid());
			}
			u.setLastDate(new Date());
			userMapper.serverUpdateUser(u);
			return u;
		}
		return null;
	}

	public Users saveSimpleUser(Users user) {
		String userId = sid.nextShort();
		String tempName="用户("+userId.substring(userId.length()-5)+")";
		String qrCodeUrl =uploadImages(userId, user.getPhone());
		user.setQrcode("/group1/"+qrCodeUrl);
		user.setId(userId);
		user.setUsername(tempName);
		user.setNickname(tempName);
		userMapper.serverInsertUser(user);
		return user;
	}
	
	private String uploadImages(String userId,String phone){
		// 为每个用户生成一个唯一的二维码
		String qrCodePath = baseUrl + userId + "qrcode.png";
		//rh_qrcode:[phone]
		qrCodeUtils.createQRCode(qrCodePath, "rh_qrcode:"+ phone);
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
		return qrCodeUrl;
	}
	
	public Users saveUser(Users user) {
		String userId = sid.nextShort();
		String qrCodeUrl =uploadImages(userId, user.getPhone());
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
//		ChatMsg chatMsg = new ChatMsg();
//		chatMsg.setGroupId(user.getCitycode());
//		chatMsg.setMsg("欢迎加入群里");
//		chatMsg.setMsgId(userId);
//		chatMsg.setMsgType(MsgTypeEnum.GROUP+"");
//		chatMsg.setReceiverId(userId);
//		chatMsg.setSenderId(user.getCitycode());
//		saveMsg(chatMsg);
		
		return user;
	}
	
	/**
	 * 获取用户信息
	 * @param userId
	 * @return
	 */
	public Users getUserInfoById(String userId){
		return queryUserById(userId);
	}
	
	public Users updateUserInfo(Users user) {
		userMapper.serverUpdateUser(user);
		return queryUserById(user.getId());
	}
	
	public Users updateUserInfo(Map<String,String> map) {
		Users user=queryUserById(map.get("userId"));
		if(user==null){
			return null;
		}
		isChangeAddress(map, user);
		user.setUsername(map.get("username"));
		user.setNickname(map.get("nickname"));
		user.setCitycode(map.get("citycode"));
		user.setAddress(map.get("address"));
		user.setAddresscode(map.get("addresscode"));
		user.setEmail(map.get("email"));
		user.setGender(map.get("gender"));
		userMapper.serverUpdateUser(user);
		return user;
	}
	
	private void isChangeAddress(Map<String,String> map,Users user){
		if(map.get("address")==null||"".equals(map.get("address").trim())){
			return;
		}
		String address=map.get("address").trim();
		if(address.equals(user.getAddress())){
			return;
		}
		if(user.getAddress()!=null){
			exitGroupUser(address, user.getId());
		}
		joinGroupUser(address, user);
	}
	
	private void joinGroupUser(String address,Users user){
		//保存用户和群关系
		String groupUserId = sid.nextShort();
		GroupUsers groupUsers = new GroupUsers();
		groupUsers.setId(groupUserId);
		groupUsers.setUserId(user.getId());
		groupUsers.setGroupId(address.hashCode()+"");
		groupsMapper.serverInsertUserRelationGroup(groupUsers);
	}
	
	private void exitGroupUser(String address,String userId){
		Map<String,String> map=new HashMap<String,String>();
		map.put("groupId", address.hashCode()+"");
		map.put("userId", userId);
		groupsMapper.serverDeleteUserRelationGroup(map);
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
		
//		Channel sendChannel = UserChannelRel.get(sendUserId);
//		if (sendChannel != null) {
//			// 使用websocket主动推送消息到请求发起者，更新他的通讯录列表为最新
//			DataContent dataContent = new DataContent();
//			dataContent.setAction(MsgActionEnum.PULL_FRIEND.type);
//			
//			sendChannel.writeAndFlush(
//					new TextWebSocketFrame(
//							JsonUtils.objectToJson(dataContent)));
//		}
	}
	
	private void saveFriends(String sendUserId, String acceptUserId) {
		Users user =userMapper.serverQueryById(acceptUserId);
		MyFriends myFriends = new MyFriends();
		String recordId = sid.nextShort();
		myFriends.setId(recordId);
		myFriends.setMyFriendUserId(acceptUserId);
		myFriends.setMyUserId(sendUserId);
		myFriends.setRemark(user.getNickname());
		myFriendsMapper.serverInserMyFriend(myFriends);
	}

	public void updFriendRemark(Map<String,String> map){
		MyFriends myFriends = new MyFriends();
		myFriends.setMyFriendUserId(map.get("myFriendUserId"));
		myFriends.setMyUserId(map.get("myUserId"));
		myFriends.setRemark(map.get("remark"));
		myFriendsMapper.serverUpdateFriend(myFriends);
	}
	
	public List<MyFriendsVO> queryMyFriends(String userId) {
		List<MyFriendsVO> myFirends = myFriendsMapper.serverQueryMyFriendsVO(userId);
		return myFirends;
	}
	
	public List<Map<String,String>> queryPhoneFriends(Map<String,String> map) {
		String phones=map.get("phones");
		List<Users> usersList=userMapper.serverQueryPhonesFriends(phones.split(","));
		List<MyFriendsVO> myFirends = myFriendsMapper.serverQueryMyFriendsVO(map.get("userId"));
		List<Map<String,String>> list=new ArrayList<Map<String,String>>();
		for (Users users : usersList) {
			Map<String,String> m=new HashMap<String, String>();
			m.put("phone", users.getPhone());
			m.put("username", users.getUsername());
			m.put("faceImage", users.getFaceImage());
			m.put("userId", users.getId());
			m.put("nickname", users.getNickname());
			for (MyFriendsVO myFriendsVO : myFirends) {
				if(!myFriendsVO.getFriendUserId().equals(users.getId())){
					continue;
				}
				m.put("isFriend", "0");
				m.put("remark", myFriendsVO.getRemark());
			}
			list.add(m);
		}
		return list;
	}
	
	public void delMyFriends(String userId,String friendId) {
		MyFriends f=new MyFriends();
		f.setMyFriendUserId(friendId);
		f.setMyUserId(userId);
		myFriendsMapper.serverDeleteMyFriend(f);
		f.setMyFriendUserId(userId);
		f.setMyUserId(friendId);
		myFriendsMapper.serverDeleteMyFriend(f);
	}


//	public void updateMsgStatus(List<String> msgIdList){
//		for(String msgId : msgIdList){
//			WriteResult res = mongoTemplate.updateMulti(query(where("sid").is(msgId)), 
//					Update.update("signFlag",MsgSignFlagEnum.signed.type), TChatMsg.class);
//		}
//		
//	}

	
	/**
	 * 查询未签收消息
	 * @param aid
	 * @param openId
	 * @return
	 */
//	public List<TChatMsg> getUnReadMsgList(String acceptUserId){
//		Query query = new Query();
//		if (acceptUserId!=null&&acceptUserId!="") {
//			query.addCriteria(
//				    new Criteria().andOperator(
//				        Criteria.where("acceptUserId").is(acceptUserId),
//				        Criteria.where("signFlag").is(MsgSignFlagEnum.unsign.type)
//				        )
//				    );
//        }
//		return mongoTemplate.find(query, TChatMsg.class);
//	}
	
	public List<Groups> queryMyGroups(String userId){
		List<Groups>  groups = groupsMapper.serverQueryMyGroups(userId);
		return groups;
	}
}
