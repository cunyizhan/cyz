package com.xc.microservice.validate.util.push;
import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;

public class PushNotification {
	
	/**
	 * 场景1：某⽤用户发⽣生了了⼀一笔交易易，银⾏行行及时下发⼀一条推送消息给该⽤用户。 
	 * 
	 * 场景2：⽤用户定制了了某本书的预订更更新，当本书有更更新时，需要向该⽤用户及时下发⼀一条更更新提醒信息。
	 *  这些需要向指定某个⽤用户推送消息的场景，即需要使⽤用对单个⽤用户推送消息的接⼝口。

	 */
	
	private static String appId = "8m8g0Q2OQkAdxcJ3uSaSu";
    private static String appKey = "LBXjQbEET4Afl6KPEvzNe4";
    private static String masterSecret = "g3huYJ7Vwq7dEtkJQ3gMA1";

  //别名推送方式
   // static String Alias = "";
    static String host = "http://sdk.open.api.igexin.com/apiex.htm";

//    public static void main(String[] args) throws Exception {
//    	push("优惠多", "您得苹果可以卖到4.5元/斤~~","123");
//	}
    
    public static void push(String title,String txt,String transmissionContent){
   	 	IGtPush push = new IGtPush(host, appKey, masterSecret);
   	 	NotificationTemplate template = notificationTemplateDemo(title,txt,transmissionContent);
        AppMessage message = new AppMessage();
        message.setData(template);

        message.setOffline(true);
        //离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(24 * 1000 * 3600);
        //推送给App的目标用户需要满足的条件
        AppConditions cdt = new AppConditions(); 
        List<String> appIdList = new ArrayList<String>();
        appIdList.add(appId);
        message.setAppIdList(appIdList);
        //手机类型
        List<String> phoneTypeList = new ArrayList<String>();
        //省份
        List<String> provinceList = new ArrayList<String>();
        //自定义tag
        List<String> tagList = new ArrayList<String>();

        cdt.addCondition(AppConditions.PHONE_TYPE, phoneTypeList);
        cdt.addCondition(AppConditions.REGION, provinceList);
        cdt.addCondition(AppConditions.TAG,tagList);
        message.setConditions(cdt); 

        IPushResult ret = push.pushMessageToApp(message,"任务别名_toApp");
        System.out.println(ret.getResponse().toString());
   }

    public static NotificationTemplate notificationTemplateDemo(String title, String txt,String transmissionContent) {
	    NotificationTemplate template = new NotificationTemplate();
	    // 设置APPID与APPKEY
	    template.setAppId(appId);
	    template.setAppkey(appKey);
	    // 设置通知栏标题与内容
	    template.setTitle(title);
	    template.setText(txt);
	    // 配置通知栏图标
	    template.setLogo("icon.png");
	    // 配置通知栏网络图标
	    template.setLogoUrl("");
	    // 设置通知是否响铃，震动，或者可清除
	    template.setIsRing(true);
	    template.setIsVibrate(true);
	    template.setIsClearable(true);
	    // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
	    template.setTransmissionType(1);
	    template.setTransmissionContent(transmissionContent);
	    // 设置定时展示时间
	    // template.setDuration("2015-01-16 11:40:00", "2015-01-16 12:24:00");
	    return template;
	}
	
}
