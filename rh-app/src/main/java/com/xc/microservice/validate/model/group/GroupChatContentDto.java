package com.xc.microservice.validate.model.group;


import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import com.xc.microservice.validate.model.chat.Users;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 聊天记录
 * @author 
 *
 */
@Document(collection="t_group_chat_msg")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'contentId': 1,'acceptUserId': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class GroupChatContentDto {

	private String contentId;
	
	private Users sender;
	
	private String groupId;
	
	private String content;
	
	private String contentType;
}
