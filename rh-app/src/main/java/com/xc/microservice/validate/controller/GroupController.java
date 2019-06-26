package com.xc.microservice.validate.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.model.bo.UsersBO;
import com.xc.microservice.validate.model.chat.ChatMsg;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.entity.TChatMsg;
import com.xc.microservice.validate.model.enums.OperatorFriendRequestTypeEnum;
import com.xc.microservice.validate.model.enums.SearchFriendsStatusEnum;
import com.xc.microservice.validate.model.group.GroupChatContentDto;
import com.xc.microservice.validate.model.group.GroupRequest;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.model.vo.FriendRequestVO;
import com.xc.microservice.validate.model.vo.GroupUserVo;
import com.xc.microservice.validate.model.vo.LoginVo;
import com.xc.microservice.validate.model.vo.MyFriendsVO;
import com.xc.microservice.validate.model.vo.UsersVO;
import com.xc.microservice.validate.redis.RedisService;
import com.xc.microservice.validate.redis.SessionKey;
import com.xc.microservice.validate.service.GroupChatMsgService;
import com.xc.microservice.validate.service.GroupService;
import com.xc.microservice.validate.service.PushMsgService;
import com.xc.microservice.validate.service.UserService;
import com.xc.microservice.validate.util.aes.SymmetricEncoder;

/**
 * 用户服务
 */
@RestController
@Slf4j
public class GroupController {
	
	@Autowired
	private GroupService groupService;
	
	private GroupChatMsgService groupChatMsgService;
	
	/**
	 * 
	 * @Description: 查询我所在群组信息
	 */
	@GetMapping("/chat/getMyGroup")
	public Result<?> getMyGroupList(String userId) {
		// 0. userId 判断不能为空
		if (StringUtils.isBlank(userId)) {
			return Result.error(CodeMsg.SEACH_NUM_ERROR);
		}
		// 查询列表
		List<Groups> groups = groupService.queryMyGroups(userId);
		if(groups==null || groups.size()==0){
			return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
		}
		return Result.success(groups);
	}
	
	
	//获取未读消息
	@GetMapping("/group/getNoReadChatMsgList")
	public Result<?> getNoReadChatMsgList(@RequestParam("acceptUserId") String acceptUserId) {
		List<GroupChatContentDto> list = groupChatMsgService.getNoReadChatMsgList(acceptUserId);
		return Result.success(list);
	}
	
	
	@GetMapping("/group/memberList")
	public Result<List<Users>> memberList(@RequestParam("groupId") String groupId) {
		List<Users> list = groupService.memberList(groupId);
		return Result.success(list);
	}
	
	@GetMapping("/group/listByUserId")
	public Result<List<Groups>> listByUserId(@RequestParam("userId") String userId) {
		List<Groups> list = groupService.listByUserId(userId);
		return Result.success(list);
	}
	
	@PostMapping("/group/searchGroups")
	public Result<List<JSONObject>> searchGroups(String searchInfo) {
		List<Groups> list = groupService.serverSearchGroups(searchInfo);
		List<JSONObject> listResult=new ArrayList<JSONObject>();
		for (Groups Groups : list) {
			JSONObject json=JSONObject.fromObject(Groups);
			SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			json.put("createTime", sd.format(Groups.getCreateTime()));
			listResult.add(json);
		}
		return Result.success(listResult);
	}
	
	@PostMapping("/group/join")
	public Result<?> join(@RequestBody GroupRequest request) {
		if(groupService.join(request)){
			Result.success("ok");
		}
		return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("未添加成功"));
	}
}
