package com.ronghe.common.util.http;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;




import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpUtil{
//   public static void main(String[] args) throws Exception{
//       DefaultHttpClient httpClient = new DefaultHttpClient();
//       String url = "https://api.netease.im/nimserver/user/create.action";
//       HttpPost httpPost = new HttpPost(url);
//
//       String appKey = "a277013540ac77fc862ee171bc9f58c4";
//       String appSecret = "b17537b6ce3b";
//       String nonce =  "12345";
//       String curTime = String.valueOf((new Date()).getTime() / 1000L);
//       String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码
//
//       // 设置请求的header
//       httpPost.addHeader("AppKey", appKey);
//       httpPost.addHeader("Nonce", nonce);
//       httpPost.addHeader("CurTime", curTime);
//       httpPost.addHeader("CheckSum", checkSum);
//       httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//
//       // 设置请求的参数
//       List<NameValuePair> nvps = new ArrayList<NameValuePair>();
//       nvps.add(new BasicNameValuePair("accid", "helloworld"));
//       httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
//
//       // 执行请求
//       HttpResponse response = httpClient.execute(httpPost);
//
//       // 打印执行结果
//       System.out.println(EntityUtils.toString(response.getEntity(), "utf-8"));
//   }
   
   public static String postRequest(Map<String,String> map)throws Exception{
	   	try {
	   	   DefaultHttpClient httpClient = new DefaultHttpClient();
	       String url = map.get("url");
	       HttpPost httpPost = new HttpPost(url);

	       String appKey = map.get("appKey");
	       String appSecret = map.get("appSecret");
	       String nonce =  map.get("nonce");
	       String curTime = String.valueOf((new Date()).getTime() / 1000L);
	       String checkSum = CheckSumBuilder.getCheckSum(appSecret, nonce ,curTime);//参考 计算CheckSum的java代码

	       // 设置请求的header
	       httpPost.addHeader("AppKey", appKey);
	       httpPost.addHeader("Nonce", nonce);
	       httpPost.addHeader("CurTime", curTime);
	       httpPost.addHeader("CheckSum", checkSum);
	       httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");

	       // 设置请求的参数
	       List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	       nvps.add(new BasicNameValuePair("accid",  map.get("accid")));
	       httpPost.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));

	       // 执行请求
	       HttpResponse response = httpClient.execute(httpPost);

	       // 打印执行结果
	       String res=EntityUtils.toString(response.getEntity(), "utf-8");
	       log.info(res);
	       return res;
		} catch (Exception e) {
			log.error("",e);
			throw new Exception();
		}
   }
}