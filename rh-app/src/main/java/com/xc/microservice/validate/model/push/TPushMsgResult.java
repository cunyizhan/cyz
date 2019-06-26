 package com.xc.microservice.validate.model.push;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 聊天记录
 * @author 
 *
 */
@Document(collection="t_push_msg_result")
@Data
@CompoundIndexes({
    @CompoundIndex(def = "{'cid': 1,'createTime': -1}")
})
@NoArgsConstructor
@AllArgsConstructor
public class TPushMsgResult {
    private String cid;

    private String taskId;

    private String msgId;

    private String msg;

    private Integer status;
    
    /**
     * 发送请求的事件
     */
    private Date createTime;

}