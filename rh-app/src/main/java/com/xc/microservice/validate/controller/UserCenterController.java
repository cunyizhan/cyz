package com.xc.microservice.validate.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.model.bo.UsersBO;
import com.xc.microservice.validate.model.chat.ChatMsg;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.entity.TUser;
import com.xc.microservice.validate.model.enums.OperatorFriendRequestTypeEnum;
import com.xc.microservice.validate.model.enums.SearchFriendsStatusEnum;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.model.vo.FriendRequestVO;
import com.xc.microservice.validate.model.vo.LoginVo;
import com.xc.microservice.validate.model.vo.MyFriendsVO;
import com.xc.microservice.validate.model.vo.UsersVO;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.redis.SessionKey;
import com.xc.microservice.validate.service.PushMsgService;
import com.xc.microservice.validate.service.UserService;
import com.xc.microservice.validate.util.aes.SymmetricEncoder;

/**
 * 用户服务
 */
@RestController
@Slf4j
public class UserCenterController {
	
	
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	@Autowired
	RedisService redisService;
	@Autowired
	private UserService userService;
	@Autowired
	private FastDFSClient fastDFSClient;
	
	
	/**
	 * 通过过户名密码登陆
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	
	@RequestMapping(value="/login/login", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> user_login(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid LoginVo vo) throws UnsupportedEncodingException, IOException{
		Users user = new Users();
		try {
			log.info("手机号{}",vo.getPhone());
			log.info("密码{}",vo.getPassword());
			user.setPhone(vo.getPhone());
			user.setPassword(vo.getPassword());
			user.setCid(vo.getCid());
			log.info("手机标识{}",vo.getCid());
			Users res = userService.queryUserForLogin(user);
			if(res!=null){
				redisService.set(SessionKey.session, vo.getPhone(),res);
				return Result.success(res);
			}
			return Result.error(CodeMsg.PHONE_OR_PASSWORD_ERROR);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	/**
	 * 检测用户登录状态
	 * @param request
	 * @param response
	 * @param vo
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@RequestMapping(value="/login/checkLogin", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> checkLogin(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid LoginVo vo) throws UnsupportedEncodingException, IOException{
		try {
			log.info("手机号{}",vo.getPhone());
			log.info("手机标识{}",vo.getCid());
			TUser tuser = redisService.get(SessionKey.session, vo.getPhone(), TUser.class);
			if(tuser!=null){
				if(!tuser.getCid().equals(vo.getCid())){
					return Result.error(CodeMsg.PHONE_OTHER_LOGIN_ERROR);
				}
				return Result.success(tuser);
			}
			return Result.error(CodeMsg.NO_LOGIN);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 用户注册
	 * @param request
	 * @param response
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	
	@RequestMapping(value="/user/regist", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> user_regist(HttpServletRequest request,HttpServletResponse response,@RequestBody@Valid Users user) throws UnsupportedEncodingException, IOException{
		try {
			Boolean isExit = userService.queryPhoneIsExist(user.getPhone());
			user.setCitycode(user.getAddress().hashCode()+"");
			if(isExit){
				return Result.error(CodeMsg.PHONE_EXIT);
			}
			userService.saveUser(user);
			return Result.success(user);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	

	/**
	 * @Description: 上传用户头像
	 */
	@PostMapping("/user/uploadFaceBase64")
	public Result<?> uploadFaceBase64(@RequestBody UsersBO userBO) throws Exception {
		// 获取前端传过来的base64字符串, 然后转换为文件对象再上传
		String base64Data = userBO.getFaceData();
		String userFacePath = baseUrl + userBO.getUserId() + "userface64.png";
		FileUtils.base64ToFile(userFacePath, base64Data);
		// 上传文件到fastdfs
		MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
		
		try {
			String url = fastDFSClient.uploadQRCode(faceFile);
			/*// 获取缩略图的url
			String thump = "_80x80.";
			String arr[] = url.split("\\.");
			String thumpImgUrl = arr[0] + thump + arr[1];*/
			// 更细用户头像
			Users user = new Users();
			user.setId(userBO.getUserId());
			user.setFaceImage("/group1/"+url);
			user.setFaceImageBig("/group1/"+url);
			Users result = userService.updateUserInfo(user);
			return Result.success(result);
		} catch (IOException e) {
			log.error("",e);
			return Result.error(CodeMsg.UPLOAD_FILE_FAIL.fillArgsToken(e.getMessage()));
		}finally{
			//上传完一定要将图片删除
			File file = new File(userFacePath);
			if (file.exists()  && file.isFile()) {
				file.delete();
			}
		}	
	}
	

	/**
	 * @Description: 设置用户昵称
	 */
	@PostMapping("/user/setNickname")
	public Result<?> setNickname(@RequestBody UsersBO userBO) throws Exception {
		try {
			Users user = new Users();
			user.setId(userBO.getUserId());
			user.setNickname(userBO.getNickname());
			Users result = userService.updateUserInfo(user);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 编辑用户信息
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/user/editUserInfo")
	public Result<?> editUserInfo(@RequestBody Map<String,String> map) throws Exception {
		try {
			Users user = new Users();
			user.setId(map.get("userId"));
			user.setUsername(map.get("username"));
			user.setNickname(map.get("nickname"));
			user.setCitycode(map.get("citycode"));
			user.setAddress(map.get("address"));
			user.setAddresscode(map.get("addresscode"));
			user.setEmail(map.get("email"));
			user.setGender(map.get("gender"));
			Users result = userService.updateUserInfo(user);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 修改用户密码
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/user/editUserPassword")
	public Result<?> editUserPassword(@RequestBody Map<String,String> map) throws Exception {
		try {
			Users user = new Users();
			user.setId(map.get("userId"));
			Users user1= userService.getUserInfoById(user.getId());
			String oldPassword=map.get("oldPassword");
			String newPassword=map.get("newPassword");
			if(!user1.getPassword().equals(oldPassword.trim())){
				return Result.error(CodeMsg.OLD_PASSWORD_ERROR);
			}
			user.setPassword(newPassword);
			Users result = userService.updateUserInfo(user);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 短信验证以后修改用户密码
	 * @param map
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/user/editPasswordAfterCode")
	public Result<?> editPasswordAfterCode(@RequestBody Map<String,String> map) throws Exception {
		try {
			Users user = new Users();
			user.setId(map.get("userId"));
			user.setPassword(map.get("password"));
			Users result = userService.updateUserInfo(user);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * @Description: 搜索好友接口, 根据账号做匹配查询而不是模糊查询
	 */
	@PostMapping("/user/search")
	public Result<?> searchUser(@RequestBody Map<String,String> map)
			throws Exception {
		try {
			String myUserId=map.get("myUserId");
			String friendPhone=map.get("friendPhone");
			// 0. 判断 myUserId friendPhone 不能为空
			if (StringUtils.isBlank(myUserId) 
					|| StringUtils.isBlank(friendPhone)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			// 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
			// 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
			// 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
			Integer status = userService.preconditionSearchFriends(myUserId, friendPhone);
			if (status == SearchFriendsStatusEnum.SUCCESS.status) {
				Users user = userService.queryUserInfoByPhone(friendPhone);
				UsersVO userVO = new UsersVO();
				BeanUtils.copyProperties(user, userVO);
				return Result.success(userVO);
			} else {
				String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
				return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg(errorMsg));
			}
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 
	 * @param myUserId
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/user/searchMyFriend")
	public Result<List<Users>> searchMyFriend(@RequestBody Map<String,Object> map)
			throws Exception {
		try {
			// 0. 判断 myUserId friendPhone 不能为空
			String myUserId=map.get("myUserId")+"";
			String searchInfo=map.get("searchInfo")+"";
			List<Users> list=new ArrayList<Users>();
			if (StringUtils.isBlank(myUserId)) {
				return Result.success(list);
			}
			list=userService.queryMyFriend(myUserId, searchInfo);
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}

	/**
	 * @Description: 发送添加好友的请求
	 */
	@PostMapping("/user/addFriendRequest")
	public Result<?> addFriendRequest(@RequestBody Map<String,String> map)
			throws Exception {
		try {
			String myUserId=map.get("myUserId");
			String friendPhone=map.get("friendPhone");
			// 0. 判断 myUserId friendUsername 不能为空
			if (StringUtils.isBlank(myUserId) 
					|| StringUtils.isBlank(friendPhone)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			
			// 前置条件 - 1. 搜索的用户如果不存在，返回[无此用户]
			// 前置条件 - 2. 搜索账号是你自己，返回[不能添加自己]
			// 前置条件 - 3. 搜索的朋友已经是你的好友，返回[该用户已经是你的好友]
			Integer status = userService.preconditionSearchFriends(myUserId, friendPhone);
			if (status == SearchFriendsStatusEnum.SUCCESS.status) {
				userService.sendFriendRequest(myUserId, friendPhone);
			} else {
				String errorMsg = SearchFriendsStatusEnum.getMsgByKey(status);
				return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg(errorMsg));
			}
			return Result.success("发送请求成功");
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * @Description: 查询用户接受到的朋友申请
	 */
	@PostMapping("/user/queryFriendRequests")
	public Result<?> queryFriendRequests(@RequestBody Map<String,String> map) {
		String userId=map.get("userId");
		try {
			// 0. 判断不能为空
			if (StringUtils.isBlank(userId)) {
				return  Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			List<FriendRequestVO> res = userService.queryFriendRequestList(userId);
			if(res==null || res.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无添加"));
			}
			// 1. 查询用户接受到的朋友申请
			log.info("查询请求结果：{}",res);
			return Result.success(res);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	
	/**
	 * @Description: 接受方 通过或者忽略朋友请求
	 */
	@PostMapping("/user/operFriendRequest")
	public Result<?> operFriendRequest(@RequestBody Map<String,String> map) {
		try {
			String acceptUserId=map.get("acceptUserId");
			String sendUserId=map.get("sendUserId");
		    Integer operType=Integer.valueOf(map.get("operType"));
			// 0. acceptUserId sendUserId operType 判断不能为空
			if (StringUtils.isBlank(acceptUserId) 
					|| StringUtils.isBlank(sendUserId) 
					|| operType == null) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			
			// 1. 如果operType 没有对应的枚举值，则直接抛出空错误信息
			if (StringUtils.isBlank(OperatorFriendRequestTypeEnum.getMsgByType(operType))) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			
			if (operType == OperatorFriendRequestTypeEnum.IGNORE.type) {
				// 2. 判断如果忽略好友请求，则直接删除好友请求的数据库表记录
				userService.deleteFriendRequest(sendUserId, acceptUserId);
			} else if (operType == OperatorFriendRequestTypeEnum.PASS.type) {
				// 3. 判断如果是通过好友请求，则互相增加好友记录到数据库对应的表
				//	   然后删除好友请求的数据库表记录
				userService.passFriendRequest(sendUserId, acceptUserId);
				userService.deleteFriendRequest(sendUserId, acceptUserId);
			}
			
			// 4. 数据库查询好友列表
			List<MyFriendsVO> myFirends = userService.queryMyFriends(acceptUserId);
			if(myFirends==null || myFirends.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(myFirends);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * @Description: 查询我的好友列表
	 */
	@PostMapping("/user/myFriends")
	public Result<?> myFriends(@RequestBody Map<String,String> map) {
		String userId=map.get("userId");
		try {
			// 0. userId 判断不能为空
			if (StringUtils.isBlank(userId)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			
			// 1. 数据库查询好友列表
			List<MyFriendsVO> myFirends = userService.queryMyFriends(userId);
			if(myFirends==null || myFirends.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(myFirends);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * @Description: 删除我的好友
	 */
	@PostMapping("/user/delMyFriends")
	public Result<?> delMyFriends(@RequestBody Map<String,String> map) {
		String myUserId=map.get("myUserId");
		String friendUserId=map.get("friendUserId");
		try {
			// 0. userId 判断不能为空
			if (StringUtils.isBlank(myUserId)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			userService.delMyFriends(myUserId, friendUserId);
			// 1. 数据库查询好友列表
			List<MyFriendsVO> myFirends = userService.queryMyFriends(myUserId);
			if(myFirends==null || myFirends.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(myFirends);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	/**
	 * 
	 * @Description: 用户手机端获取未签收的消息列表
	 */
	@PostMapping("/user/getUnReadMsgList")
	public Result<?> getUnReadMsgList(String acceptUserId) {
		try {
			// 0. userId 判断不能为空
			if (StringUtils.isBlank(acceptUserId)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			// 查询列表
			List<TChatMsg> unreadMsgList = userService.getUnReadMsgList(acceptUserId);
			if(unreadMsgList==null || unreadMsgList.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(unreadMsgList);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 
	 * @Description: 查询我所在群组信息
	 */
	@PostMapping("/user/getMyGroupList")
	public Result<?> getMyGroupList(String userId) {
		try {
			// 0. userId 判断不能为空
			if (StringUtils.isBlank(userId)) {
				return Result.error(CodeMsg.SEACH_NUM_ERROR);
			}
			// 查询列表
			List<Groups> groups = userService.queryMyGroups(userId);
			if(groups==null || groups.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(groups);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	
}
