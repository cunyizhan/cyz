package com.xc.microservice.validate.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import javax.annotation.Resource;


import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


import com.gexin.rp.sdk.base.uitls.RandomUtil;
import com.xc.microservice.validate.config.netty.AcceptMsgIdBindMember;
import com.xc.microservice.validate.config.netty.ChatMsg;
import com.xc.microservice.validate.dao.GroupAcceptChatContentMapper;
import com.xc.microservice.validate.dao.GroupSendChatContentMapper;
import com.xc.microservice.validate.dao.GroupUsersMapper;
import com.xc.microservice.validate.model.enums.AcceptTypeEnums;
import com.xc.microservice.validate.model.enums.MsgSignFlagEnum;
import com.xc.microservice.validate.model.group.GroupAcceptChatContent;
import com.xc.microservice.validate.model.group.GroupChatContentDto;
import com.xc.microservice.validate.model.group.GroupSendChatContent;



@Service
public class GroupChatMsgService {

	@Resource
	private GroupSendChatContentMapper groupSendChatContentMapper;
	@Resource
	private GroupAcceptChatContentMapper groupAcceptChatContentMapper;
	@Resource
	private GroupUsersMapper groupUsersMapper;
	/**
	 * 保存群聊发送文字
	 * @param accepetChatContent
	 * @return
	 */
	public String saveGroupSendMsgContent(ChatMsg msg) {
		String uuid = RandomUtil.randomUUID();
		String senderId = msg.getSenderId();
		String groupId = msg.getReceiverId();
		String content = msg.getMsg();
		GroupSendChatContent build = new  GroupSendChatContent();
		build.setId(uuid);
		build.setSendUserId(senderId);
		build.setAcceptGroupId(groupId);
		build.setContent(content);
		build.setContentType(AcceptTypeEnums.TEXT.getType());
		build.setCreateTime(new Date());
		
		groupSendChatContentMapper.insertSelective(build);
		return uuid;
	}
	
	/**
	 * 保存群聊发送图片
	 * @param accepetChatContent
	 * @return
	 */
	public String saveGroupSendImageContent(ChatMsg msg) {
		String uuid = RandomUtil.randomUUID();
		String senderId = msg.getSenderId();
		String groupId = msg.getReceiverId();
		String content = msg.getMsg();
		
		GroupSendChatContent build = new  GroupSendChatContent();
		build.setId(uuid);
		build.setSendUserId(senderId);
		build.setAcceptGroupId(groupId);
		build.setContent(content);
		build.setContentType(AcceptTypeEnums.IMAGE.getType());
		build.setCreateTime(new Date());
		groupSendChatContentMapper.insertSelective(build);
		return uuid;
	}
	
	
	/**
	 * 获取群友们的id
	 * @param acceptId
	 * @return
	 */
	public List<String> getGroupMemberList(String groupId) {
		return groupUsersMapper.memberIdListByGroupId(groupId);
	}
	/**
	 * 批量插入群聊接收消息
	 * @param groupSendMsgContentId
	 * @param groupMemberIdList
	 * @return
	 */
	public List<AcceptMsgIdBindMember> saveBatchGroupAcceptMsgContent(String groupSendContentId,ChatMsg  accepetChatContent, List<String> groupMemberIdList) {
		List<AcceptMsgIdBindMember> bindList = new ArrayList<AcceptMsgIdBindMember>();
		List<GroupAcceptChatContent> groupAcceptChatContentList = new ArrayList<GroupAcceptChatContent>();
		String senderId = accepetChatContent.getSenderId();
		String acceptGroupId = accepetChatContent.getReceiverId();
		String content = accepetChatContent.getMsg();
		groupMemberIdList.stream().forEach(memberId -> {
			String uuid = RandomUtil.randomUUID();
			GroupAcceptChatContent groupAcceptChatContent = GroupAcceptChatContent.builder()
			.id(uuid).groupSendContentId(groupSendContentId)
			.sendUserId(senderId)
			.acceptGroupId(acceptGroupId).acceptUserId(memberId)
			.content(content).contentType(AcceptTypeEnums.TEXT.getType()).signFlag(MsgSignFlagEnum.unsign.type)
			.createTime(new Date()).build();
			AcceptMsgIdBindMember bind = AcceptMsgIdBindMember.builder()
			.acceptMsgId(uuid).memberId(memberId).build();
			groupAcceptChatContentList.add(groupAcceptChatContent);
			bindList.add(bind);
		});
		groupAcceptChatContentMapper.inertBatch(groupAcceptChatContentList);
		return bindList;
	}
	
	/**
	 * 批量插入群聊接收消息图片
	 * @param groupSendContentId
	 * @param accepetChatContent
	 * @param groupMemberIdList
	 * @return
	 */
	public List<AcceptMsgIdBindMember> saveBatchGroupAcceptImageContent(String groupSendContentId,ChatMsg  accepetChatContent, List<String> groupMemberIdList) {
		List<AcceptMsgIdBindMember> bindList = new ArrayList<AcceptMsgIdBindMember>();
		List<GroupAcceptChatContent> groupAcceptChatContentList = new ArrayList<GroupAcceptChatContent>();
		String senderId = accepetChatContent.getSenderId();
		String acceptGroupId = accepetChatContent.getReceiverId();
		String content = accepetChatContent.getMsg();
		groupMemberIdList.stream().forEach(memberId -> {
			String uuid = RandomUtil.randomUUID();
			GroupAcceptChatContent groupAcceptChatContent = GroupAcceptChatContent.builder()
			.id(uuid).groupSendContentId(groupSendContentId)
			.sendUserId(senderId)
			.acceptGroupId(acceptGroupId).acceptUserId(memberId)
			.content(content).contentType(AcceptTypeEnums.IMAGE.getType()).signFlag(MsgSignFlagEnum.unsign.type)
			.createTime(new Date()).build();
			AcceptMsgIdBindMember bind = AcceptMsgIdBindMember.builder()
			.acceptMsgId(uuid).memberId(memberId).build();
			groupAcceptChatContentList.add(groupAcceptChatContent);
			bindList.add(bind);
		});
		groupAcceptChatContentMapper.inertBatch(groupAcceptChatContentList);
		return bindList;
	} 
	
	/**
	 * 获取未读的群聊消息
	 * 获取发送者的信息
	 * @param acceptUserId
	 */
	public List<GroupChatContentDto> getNoReadChatMsgList(String acceptUserId) {
		List<GroupChatContentDto> list = groupAcceptChatContentMapper.getNoReadListByAcceptUserId(acceptUserId, MsgSignFlagEnum.unsign.type);
		return list;
	}
}
