package com.xc.microservice.validate.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.xc.microservice.validate.config.fastdfs.FastDFSClient;
import com.xc.microservice.validate.config.fastdfs.FileUtils;
import com.xc.microservice.validate.model.chat.Users;
import com.xc.microservice.validate.model.group.GroupChatContentDto;
import com.xc.microservice.validate.model.group.GroupRequest;
import com.xc.microservice.validate.model.group.Groups;
import com.xc.microservice.validate.model.result.CodeMsg;
import com.xc.microservice.validate.model.result.Result;
import com.xc.microservice.validate.service.GroupChatMsgService;
import com.xc.microservice.validate.service.GroupService;

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
	private GroupChatMsgService groupChatMsgService;
	
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
	@PostMapping("/group/getNoReadChatMsgList")
	public Result<?> getNoReadChatMsgList(@RequestParam("acceptUserId") String acceptUserId) {
		List<GroupChatContentDto> list = groupChatMsgService.getNoReadChatMsgList(acceptUserId);
		return Result.success(list);
	}
	
	
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
	@PostMapping("/group/uploadFaceBase64")
	public Result<?> uploadFaceBase64(@RequestBody Map<String,String> map) throws Exception {
		// 获取前端传过来的base64字符串, 然后转换为文件对象再上传
		String base64Data = map.get("faceData");
		String userFacePath = baseUrl + map.get("groupId") + "userface64.png";
		FileUtils.base64ToFile(userFacePath, base64Data);
		// 上传文件到fastdfs
		MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
		
		try {
			String url = fastDFSClient.uploadQRCode(faceFile);
			/*// 获取缩略图的url
			String thump = "_80x80.";
			String arr[] = url.split("\\.");
			String thumpImgUrl = arr[0] + thump + arr[1];*/
			// 更改组群头像
			Groups g=new Groups();
			g.setId(map.get("groupId"));
			g.setGroupFaceimageBig("/group1/"+url);
			Groups result = groupService.updateGroupInfo(g);
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
}
