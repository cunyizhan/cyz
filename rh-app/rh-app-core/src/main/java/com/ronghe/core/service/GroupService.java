package com.ronghe.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.ronghe.common.idwork.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ronghe.common.fastdfs.FastDFSClient;
import com.ronghe.common.fastdfs.FileUtils;
import com.ronghe.core.dao.GroupsImgMapper;
import com.ronghe.core.dao.GroupsMapper;
import com.ronghe.core.dao.UserMapper;
import com.ronghe.model.chat.Users;
//import com.ronghe.model.entity.TChatMsg;
import com.ronghe.model.enums.MsgSignFlagEnum;
import com.ronghe.model.group.GroupRequest;
import com.ronghe.model.group.GroupUsers;
import com.ronghe.model.group.Groups;
import com.ronghe.model.group.GroupsImg;
import com.ronghe.model.result.CodeMsg;
import com.ronghe.model.result.Result;
import com.ronghe.common.util.QRCodeUtils;

@Service
@Slf4j
public class GroupService {
	@Autowired
	private GroupsMapper groupsMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	GroupsImgMapper groupsImgMapper;
	
//	@Autowired
//	MongoTemplate mongoTemplate;
	
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
	
	public Groups getGroupsById(String groupId){
		Groups groups = groupsMapper.serverSearchGroupsById(groupId);
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
	
	public List<GroupsImg> insertGroupImgs(Map map){
		String groupId=map.get("groupId")+"";
		JSONArray array=JSONArray.parseArray(map.get("imgList").toString());
		for (Object o : array) {
			JSONObject js=(JSONObject)o;
			insertGroupImg(js, groupId);
		}
		return groupsImgMapper.serverSelectGroupImg(groupId);
	}
	
	public void insertGroupImg(JSONObject map,String groupId){
		GroupsImg gi=new GroupsImg();
		String id=sid.nextShort();
		String imgPath=uploadImgBase64(groupId, map.getString("base64Data")+"", id);
		gi.setId(id);
		gi.setGroupId(groupId);
		gi.setImgDesc(map.get("imgDesc")+"");
		gi.setImgName(groupId + id+".png");
		gi.setImgPath("/group1/"+imgPath);
		Integer sn=groupsImgMapper.serverSelectGroupImgSn(groupId);
		if(sn==null){
			sn=0;
		}
		gi.setImgSn(sn+1);
		groupsImgMapper.serverInsertGroupImg(gi);
	}
	
	private String uploadImgBase64(String groupId,String base64Data,String id){
		String userFacePath = baseUrl + groupId + id+".png";
		try {
			FileUtils.base64ToFile(userFacePath, base64Data);
			// 上传文件到fastdfs
			MultipartFile faceFile = FileUtils.fileToMultipart(userFacePath);
			String url = fastDFSClient.uploadQRCode(faceFile);
			return url;
		} catch (Exception e) {
			log.error("",e);
			return "";
		}finally{
			try {
				//上传完一定要将图片删除
				File file = new File(userFacePath);
				if (file.exists()  && file.isFile()) {
					file.delete();
				}
			} catch (Exception e2) {
				log.error("",e2);
			}
		}	
	}
	
	public List<GroupsImg> moveGroupImgs(Map map){
		String groupId=map.get("groupId")+"";
		JSONArray array=JSONArray.parseArray(map.get("imgList").toString());
		for (Object o : array) {
			JSONObject js=(JSONObject)o;
			GroupsImg gi=new GroupsImg();
			gi.setId(js.getString("id"));
			gi.setImgSn(Integer.parseInt(js.get("imgSn")+""));
			groupsImgMapper.serverUpdateGroupImg(gi);
		}
		return groupsImgMapper.serverSelectGroupImg(groupId);
	}
	
	public GroupsImg editGroupImgs(Map<String,String> map){
		String imgId=map.get("imgId");
		GroupsImg gi=new GroupsImg();
		gi.setId(imgId);
		gi.setImgDesc(map.get("imgDesc"));
		groupsImgMapper.serverUpdateGroupImg(gi);
		return groupsImgMapper.serverGetGroupImg(imgId);
	}
	
	public List<GroupsImg> delGroupImg(Map<String,String> map){
		String imgId=map.get("imgId");
		String groupId=map.get("groupId");
		groupsImgMapper.serverDeleteGroupImgById(imgId);
		List<GroupsImg> list=groupsImgMapper.serverSelectGroupImg(groupId);
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setImgSn(i+1);
			groupsImgMapper.serverUpdateGroupImg(list.get(i));
		}
		return list;
	}
}
