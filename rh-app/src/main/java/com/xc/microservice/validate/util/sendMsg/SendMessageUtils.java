package com.xc.microservice.validate.util.sendMsg;

import java.util.Random;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

public class SendMessageUtils {
	public static String generateCode(){
		Random rand = new Random();
		int a = rand.nextInt(10000);
		String b = ""+a;
		if (b.length()==3) {
			b = "0"+ a;
		}else if (b.length()==2) {
			b = "00"+ a;
		}else if (b.length()==1) {	
			b = "000"+ a;
		}
		return b;
	}
	
	public static String sendMessage(String phone,String code){
		
		//设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		//初始化ascClient需要的几个参数
		final String product = "Dysmsapi";//短信API产品名称
		final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名
		//替换成你的AK
		final String accessKeyId = "hy0UAdO3qljUmuFI";//你的accessKeyId,参考本文档步骤2
		final String accessKeySecret = "bVwDnyTHBDPaz8rol9EOGyS0DKQraP";//你的accessKeySecret，参考本文档步骤2
		//初始化ascClient,暂时不支持多region
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId,
		accessKeySecret);
		try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IAcsClient acsClient = new DefaultAcsClient(profile);
		 //组装请求对象
		 SendSmsRequest request = new SendSmsRequest();
		 //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为20个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
		 request.setPhoneNumbers(phone);
		 //必填:短信签名-可在短信控制台中找到
		 request.setSignName("中烟追溯");
		 //必填:短信模板-可在短信控制台中找到
		 request.setTemplateCode("SMS_76560133");
		 //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		 request.setTemplateParam("{\"code\":\""+code+"\",\"name\":\"中烟追溯用户\"}");
		 //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
		 request.setOutId("yourOutId");
		//请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse;
		try {
			sendSmsResponse = acsClient.getAcsResponse(request);
			System.out.println("发送结果："+sendSmsResponse.getCode()+"---------"+sendSmsResponse.getMessage());
			if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			//请求成功
				return "ok";
			}
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "failed";
	}

}
