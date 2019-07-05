package com.xc.microservice.validate.model.group;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.xc.microservice.validate.model.chat.Users;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群组接受到的聊天消息
 * @author 
 *
 */
@Builder
@Document(collection="t_group_accept_chat_content")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'sendUserId': 1,'acceptUserId': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class GroupAcceptChatContent {
    private String id;

    private String groupSendContentId;

    private String sendUserId;

    private String acceptUserId;

    private String acceptGroupId;

    private String contentType;

    private Integer signFlag;

    private Date createTime;

    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getGroupSendContentId() {
        return groupSendContentId;
    }

    public void setGroupSendContentId(String groupSendContentId) {
        this.groupSendContentId = groupSendContentId == null ? null : groupSendContentId.trim();
    }

    public String getSendUserId() {
        return sendUserId;
    }

    public void setSendUserId(String sendUserId) {
        this.sendUserId = sendUserId == null ? null : sendUserId.trim();
    }

    public String getAcceptUserId() {
        return acceptUserId;
    }

    public void setAcceptUserId(String acceptUserId) {
        this.acceptUserId = acceptUserId == null ? null : acceptUserId.trim();
    }

    public String getAcceptGroupId() {
        return acceptGroupId;
    }

    public void setAcceptGroupId(String acceptGroupId) {
        this.acceptGroupId = acceptGroupId == null ? null : acceptGroupId.trim();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType == null ? null : contentType.trim();
    }

    public Integer getSignFlag() {
        return signFlag;
    }

    public void setSignFlag(Integer signFlag) {
        this.signFlag = signFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}