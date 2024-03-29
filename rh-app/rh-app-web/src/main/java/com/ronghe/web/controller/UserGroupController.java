package com.ronghe.web.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ronghe.common.fastdfs.FastDFSClient;
import com.ronghe.common.fastdfs.FileUtils;
import com.ronghe.model.bo.UsersBO;
import com.ronghe.model.chat.Users;
import com.ronghe.model.group.GroupRequest;
import com.ronghe.model.result.CodeMsg;
import com.ronghe.model.result.Result;
import com.ronghe.model.usergroup.UserGroup;
import com.ronghe.model.usergroup.UserGroupRequest;
import com.ronghe.model.usergroup.UserGroupUsers;
import com.ronghe.core.service.UserGroupService;

/**
 * 用户服务
 */
@RestController
@Slf4j
public class UserGroupController {
	
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	
	@Autowired
	private UserGroupService userGroupService;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	/**
	 * 创建群聊
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	@RequestMapping(value="/userGroup/createGroup", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> createGroup(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) throws UnsupportedEncodingException, IOException{
		try {
			UserGroup groups =userGroupService.create(map);
			return Result.success(groups);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 获取我的群聊
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/searchMyUserGroup", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> searchMyUserGroup(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String userId=map.get("userId");
			List<UserGroup> list=userGroupService.listByUserId(userId);
			if(list==null || list.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 搜索组群
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/searchUserGroup", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> searchUserGroup(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String searchInfo=map.get("searchInfo");
			List<UserGroup> list=userGroupService.serverSearchGroups(searchInfo);
			if(list==null || list.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 获取我的群聊成员
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/searchUserGroupUser", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> searchUserGroupUser(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String groupId=map.get("groupId");
			List<Map<String,String>> list=userGroupService.memberList(groupId);
			if(list==null || list.size()==0){
				return  Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("暂无记录"));
			}
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 邀请朋友加入群
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/inviteUserGroupUser", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> inviteUserGroupUser(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String userId=map.get("userId");
			String friendIds=map.get("friendIds");
			String groupId=map.get("groupId");
			String res=userGroupService.inviteUserGroupUser(userId, friendIds, groupId);
			return Result.success(res);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	@PostMapping("/userGroup/join")
	public Result<?> join(@RequestBody UserGroupRequest request) {
		if(userGroupService.join(request)){
			Result.success("ok");
		}
		return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("未添加成功"));
	}
	
	/**
	 * @Description: 上传用户头像
	 */
	@PostMapping("/userGroup/uploadFaceImg")
	public Result<?> uploadFaceBase64(@RequestParam("faceFile") MultipartFile faceFile,@RequestParam("groupId") String groupId) throws Exception {
		try {
			UserGroup ug=userGroupService.getGroupByid(groupId);
			if(ug.getGroupFaceimageBig()!=null&&!"".equals(ug.getGroupFaceimageBig())){
				fastDFSClient.deleteFile(ug.getGroupFaceimageBig());
			}
			String url = fastDFSClient.uploadQRCode(faceFile);
			ug.setId(groupId);
			ug.setGroupFaceimageBig("/group1/"+url);
			UserGroup result = userGroupService.updateUserGroup(ug);
			return Result.success(result);
		} catch (IOException e) {
			log.error("",e);
			return Result.error(CodeMsg.UPLOAD_FILE_FAIL.fillArgsToken(e.getMessage()));
		}
	}
	
	/**
	 * 编辑群昵称
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/editUserGroupName", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> editUserGroupName(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String groupId=map.get("groupId");
			String groupName=map.get("groupName");
			UserGroup ug=new UserGroup(); 
			ug.setId(groupId);
			ug.setGroupName(groupName);
			UserGroup result = userGroupService.updateUserGroup(ug);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 编辑群信息
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/editUserGroupInfo", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> editUserGroupInfo(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String groupId=map.get("groupId");
			String groupName=map.get("groupName");
			UserGroup ug=new UserGroup(); 
			ug.setId(groupId);
			ug.setGroupName(groupName);
			ug.setGroupDescription(map.get("groupDescription"));
			UserGroup result = userGroupService.updateUserGroup(ug);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 退出群聊
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/exitUserGroupUser", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> exitUserGroupUser(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String userId=map.get("userId");
			String groupId=map.get("groupId");
			userGroupService.deleteGroupUserById(groupId, userId);
			return Result.success("ok");
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
	
	/**
	 * 解散群聊
	 * @param request
	 * @param response
	 * @param map
	 * @return
	 */
	@RequestMapping(value="/userGroup/delUserGroupUser", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public Result<?> delUserGroupUser(HttpServletRequest request,HttpServletResponse response,@RequestBody Map<String,String> map) {
		try {
			String groupId=map.get("groupId");
			userGroupService.deleteGroup(groupId);
			return Result.success("ok");
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.EXPECTION_ERROR);
		}
	}
}
