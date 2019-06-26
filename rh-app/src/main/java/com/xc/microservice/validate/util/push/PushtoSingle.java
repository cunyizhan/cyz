package com.xc.microservice.validate.util.push;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.exceptions.RequestException;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;

public class PushtoSingle {
	
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

    public static void main(String[] args) throws Exception {
		push("025543ec1647c677aaad0ea512fae7c0", "测绘", "我正在测试","#");
	}
    
    
    public static void push(String cid,String title,String msg,String url) throws Exception {
        IGtPush push = new IGtPush(host, appKey, masterSecret);
        LinkTemplate template = linkTemplateDemo(title,msg,"#");
        SingleMessage message = new SingleMessage();
        message.setOffline(true);
        // 离线有效时间，单位为毫秒，可选
        message.setOfflineExpireTime(1 * 3600 * 1000);
        message.setData(template);
        // 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0); 
        Target target = new Target();
        target.setAppId(appId);
        target.setClientId(cid);
        //target.setAlias(Alias);
        IPushResult ret = null;
        try {
            ret = push.pushMessageToSingle(message, target);
        } catch (RequestException e) {
            e.printStackTrace();
            ret = push.pushMessageToSingle(message, target, e.getRequestId());
        }
        if (ret != null) {
            System.out.println(ret.getResponse().toString());
        } else {
            System.out.println("服务器响应异常");
        }
    }
    public static LinkTemplate linkTemplateDemo(String title,String msg,String url) {
        LinkTemplate template = new LinkTemplate();
        // 设置APPID与APPKEY
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 设置通知栏标题与内容
        template.setTitle(title);
        template.setText(msg);
        // 配置通知栏图标
        template.setLogo("icon.png");
        // 配置通知栏网络图标，填写图标URL地址
        template.setLogoUrl("");
        // 设置通知是否响铃，震动，或者可清除
        template.setIsRing(true);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        // 设置打开的网址地址
        template.setUrl(url);
        return template;
    }
}
