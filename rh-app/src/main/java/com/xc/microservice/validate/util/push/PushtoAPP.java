package com.xc.microservice.validate.util.push;

import java.util.ArrayList;
import java.util.List;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.AppMessage;
import com.gexin.rp.sdk.base.uitls.AppConditions;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;

public class PushtoAPP {    
	//采用"Java SDK 快速入门"， "第二步 获取访问凭证 "中获得的应用配置，用户可以自行替换
    private static String appId = "8m8g0Q2OQkAdxcJ3uSaSu";
    private static String appKey = "LBXjQbEET4Afl6KPEvzNe4";
    private static String masterSecret = "g3huYJ7Vwq7dEtkJQ3gMA1";
	static String host = "http://sdk.open.api.igexin.com/apiex.htm";

    public static void main(String[] args) throws Exception {

    	push("天下通消息:", "科技9点见：百度发布2019年第一季度财报/罗永浩称还会做手机","https://mbd.baidu.com/newspage/data/landingsuper?context=%7B%22nid%22%3A%22news_9178167428734519419%22%7D&n_type=0&p_from=1");
    }
    
    public static void push(String title,String txt,String url){
    	 IGtPush push = new IGtPush(host, appKey, masterSecret);

         LinkTemplate template = linkTemplateDemo(title,txt,url);
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

    public static LinkTemplate linkTemplateDemo(String title,String txt,String url){
        LinkTemplate template = new LinkTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        template.setTitle(title);
        template.setText(txt);
        template.setLogo("icon.png");
        template.setLogoUrl("");
        template.setIsRing(true);
        template.setIsVibrate(true);
        template.setIsClearable(true);
        template.setUrl(url);

        return template;
    }

}