package com.xc.microservice.validate.util.push;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IBatch;
import com.gexin.rp.sdk.base.IIGtPush;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;

public class MyBatchPush {
	
	/**
	 * 场景1，对于抽奖活动的应⽤用，需要对已知的某些⽤用户推送中奖消息，就可以通过clientid列列表⽅方式推送 消息。
	 * 
	 * 场景2，向新客⽤用户发放抵⽤用券，提升新客的转化率，就可以事先提取新客列列表，将消息指定发送给这 部分指定CID⽤用户。

	 */
	
    //采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private static String appId = "8m8g0Q2OQkAdxcJ3uSaSu";
    private static String appKey = "LBXjQbEET4Afl6KPEvzNe4";
    private static String masterSecret = "g3huYJ7Vwq7dEtkJQ3gMA1";

    
    //别名推送方式
    // static String Alias = "";
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    
    public static void pushMessageList(List<String> cids, String msg,String type) throws IOException{
    	IIGtPush push = new IGtPush(host, appKey, masterSecret);
        IBatch batch = push.getBatch();
        
        try { 
        	
            for (String cid : cids) {
            	constructClientLinkMsg(cid,msg,batch);
			}	
        } catch (Exception e) {
            e.printStackTrace();
        }
        IPushResult res =   batch.submit();
        System.out.println(res.getResponse().toString());
    }
    

    public static void main(String[] args) {
		try {
			List<String> cids = new ArrayList<String>();
			cids.add("025543ec1647c677aaad0ea512fae7c0");
			pushMessageList(cids, "您好：wo shssssssss", "1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void constructClientTransMsg(String cid, String msg ,IBatch batch) throws Exception {
		
		SingleMessage message = new SingleMessage();
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTransmissionContent(msg);
        template.setTransmissionType(1); // 这个Type为int型，填写1则自动启动app

        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(1 * 1000);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        batch.add(message, target);
	}
	
	private static void constructClientLinkMsg(String cid, String msg ,IBatch batch) throws Exception {
		
		SingleMessage message = new SingleMessage();
		LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTitle("好友请求");
        template.setText(msg);
        template.setLogo("push.png");
        template.setLogoUrl("logoUrl");
        template.setUrl("www.baidu.com");
        
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(1 * 1000);

        // 设置推送目标，填入appid和clientId
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        batch.add(message, target);
	}
}

