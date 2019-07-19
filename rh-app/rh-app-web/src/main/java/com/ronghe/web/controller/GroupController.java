package com.ronghe.web.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.ronghe.common.fastdfs.FastDFSClient;
import com.ronghe.model.chat.Users;
import com.ronghe.model.group.GroupRequest;
import com.ronghe.model.group.Groups;
import com.ronghe.model.group.GroupsImg;
import com.ronghe.model.result.CodeMsg;
import com.ronghe.model.result.Result;
import com.ronghe.core.service.GroupService;

/**
 * 用户服务
 */
@RestController
@Slf4j
public class GroupController {
	
	@Value("${spring.application.baseurl}")
	private String baseUrl;
	
	@Autowired
	private GroupService groupService;
	
	@Autowired
	private FastDFSClient fastDFSClient;
	
	/**
	 * 
	 * @Description: 查询我所在群组信息
	 */
	@PostMapping("/chat/getMyGroup")
	public Result<?> getMyGroupList(@RequestBody Map<String,String> map) {
		String userId=map.get("userId");
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
//	@PostMapping("/group/getNoReadChatMsgList")
//	public Result<?> getNoReadChatMsgList(@RequestParam("acceptUserId") String acceptUserId) {
//		List<GroupChatContentDto> list = groupChatMsgService.getNoReadChatMsgList(acceptUserId);
//		return Result.success(list);
//	}
	
	
	@PostMapping("/group/memberList")
	public Result<List<Users>> memberList(@RequestBody Map<String,String> map) {
		String groupId=map.get("groupId");
		List<Users> list = groupService.memberList(groupId);
		return Result.success(list);
	}
	
	@PostMapping("/group/listByUserId")
	public Result<List<Groups>> listByUserId(@RequestParam("userId") String userId) {
		List<Groups> list = groupService.listByUserId(userId);
		return Result.success(list);
	}
	
	@PostMapping("/group/searchGroups")
	public Result<List<JSONObject>> searchGroups(@RequestBody Map<String,String> map) {
		String searchInfo=map.get("searchInfo");
		List<Groups> list = groupService.serverSearchGroups(searchInfo);
		List<JSONObject> listResult=new ArrayList<JSONObject>();
		for (Groups Groups : list) {
			JSONObject json=(JSONObject)JSONObject.toJSON(Groups);
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
	
	@PostMapping("/group/createGroup")
	public Result<?> createGroup(@RequestBody Map<String,String> map) {
		try {
			Groups g=groupService.create(map);
			return Result.success(g);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("创建失败，请联系管理员"));
		}
	}
	
	@PostMapping("/group/editGroupName")
	public Result<?> editGroupName(@RequestBody Map<String,String> map) {
		try {
			Groups g=new Groups();
			g.setGroupName(map.get("groupName"));
			g.setId(map.get("groupId"));
			g=groupService.updateGroupInfo(g);
			return Result.success(g);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
	
	@PostMapping("/group/editGroupInfo")
	public Result<?> editGroupInfo(@RequestBody Map<String,String> map) {
		try {
			Groups g=new Groups();
			g.setGroupDescription(map.get("groupDescription"));
			g.setId(map.get("groupId"));
			g=groupService.updateGroupInfo(g);
			return Result.success(g);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
	
	/**
	 * @Description: 上传用户头像
	 */
	@PostMapping("/group/uploadFaceImg")
	public Result<?> uploadFaceBase64(@RequestParam("faceFile") MultipartFile faceFile,@RequestParam("groupId") String groupId) throws Exception {
		try {
			Groups g=groupService.getGroupsById(groupId);
			if(g.getGroupFaceimageBig()!=null&&!"".equals(g.getGroupFaceimageBig())){
				fastDFSClient.deleteFile(g.getGroupFaceimageBig());
			}
			String url = fastDFSClient.uploadQRCode(faceFile);
			// 更改组群头像
			g.setGroupFaceimageBig("/group1/"+url);
			Groups result = groupService.updateGroupInfo(g);
			return Result.success(result);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.UPLOAD_FILE_FAIL.fillArgsToken(e.getMessage()));
		}
	}
	/**
	 * 上传图片
	 * @param map
	 * @return
	 */
	@PostMapping("/group/uploadGroupImgs")
	public Result<?> uploadGroupImgs(@RequestBody Map<String,Object> map) {
		try {
			List<GroupsImg> list=groupService.insertGroupImgs(map);
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
	
	/**
	 * 移动图片
	 * @param map
	 * @return
	 */
	@PostMapping("/group/moveGroupImgs")
	public Result<?> moveGroupImgs(@RequestBody Map<String,Object> map) {
		try {
			List<GroupsImg> list=groupService.moveGroupImgs(map);
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
	
	/**
	 * 编辑图片
	 * @param map
	 * @return
	 */
	@PostMapping("/group/editGroupImg")
	public Result<?> editGroupImg(@RequestBody Map<String,String> map) {
		try {
			GroupsImg img=groupService.editGroupImgs(map);
			return Result.success(img);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
	
	/**
	 * 删除图片
	 * @param map
	 * @return
	 */
	@PostMapping("/group/delGroupImg")
	public Result<?> delGroupImg(@RequestBody Map<String,String> map) {
		try {
			List<GroupsImg> list=groupService.delGroupImg(map);
			return Result.success(list);
		} catch (Exception e) {
			log.error("",e);
			return Result.error(CodeMsg.SEACH_RESULT_ERROR.fillMsg("操作失败，请联系管理员"));
		}
	}
}
