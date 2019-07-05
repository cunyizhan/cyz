package com.xc.microservice.validate.config.netty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AcceptMsgIdBindMember {
	
	private String acceptMsgId;
	
	private String memberId;

	
	
	
}
