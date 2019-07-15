package com.ronghe.model.result;


public class CodeMsg {
	
	private int code;
	private String msg;
	private String token;
	public static CodeMsg SUCCESS = new CodeMsg(1000, "成功","%s");
	public static CodeMsg NO_LOGIN = new CodeMsg(1001, "未登录");
	public static CodeMsg GET_WXSESSION_FAIL = new CodeMsg(1002, "未登录");
	public static CodeMsg PHONE_OR_PASSWORD_ERROR = new CodeMsg(1003, "手机号或密码错误","%s");
	public static CodeMsg PHONE_OR_CODE_ERROR = new CodeMsg(1010, "手机号短信验证码错误","%s");
	public static CodeMsg PHONE_EXIT = new CodeMsg(1004, "该手机号已经被注册");
	public static CodeMsg UPLOAD_FILE_FAIL = new CodeMsg(1005, "上传头像失败","%s");
	public static CodeMsg PHONE_OTHER_LOGIN_ERROR = new CodeMsg(1006, "用户已在其它设备登录","%s");
	public static CodeMsg OLD_PASSWORD_ERROR = new CodeMsg(1011, "旧密码错误，修改失败","%s");
	public static CodeMsg WANGYI_LOGIN_ERROR = new CodeMsg(1012, "网易云信登录失败","%s");
	
	
	
	public static CodeMsg SEACH_NUM_ERROR = new CodeMsg(1007, "参数有误");
	public static CodeMsg SEACH_RESULT_ERROR = new CodeMsg(1008, "%s");
	public static CodeMsg SEACH_PARAMS_ERROR = new CodeMsg(1009, "参数有误,请上送手机号码，和手机唯一标识");
	
	public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(400004, "访问太频繁！","%s");
	public static CodeMsg MIN_LIMIT_NUM= new CodeMsg(400005, "每分钟验证次数%s","%s");
	public static CodeMsg MIN_LIMIT_PERSON= new CodeMsg(400006, "每分钟验证次数%s","%s");
	public static CodeMsg DAY_LIMIT_PERSON= new CodeMsg(400007, "每天验证次数%s","%s");
	
	
	//异常
	public static CodeMsg BIND_ERROR = new CodeMsg(200006, "参数校验异常：%s","%s");
	public static CodeMsg SERVER_ERROR = new CodeMsg(200007, "服务端异常","%s");
	public static CodeMsg SESSION_ERROR = new CodeMsg(200008, "验证已过期，请重新扫码","%s");
	
	//关注--黑名单
	public static CodeMsg PERSON_NO_PROBLEM = new CodeMsg(300000, "正常");
	public static CodeMsg PERSON_ERROR = new CodeMsg(300001, "人黑名单","%s");
	public static CodeMsg VALIDATE_CODE_BLACK = new CodeMsg(300002, "追溯码存在于黑名单", "%s");
	public static CodeMsg NO_Subscribe = new CodeMsg(300003, "未关注","%s");
	
	public static CodeMsg SAVE_INFO_ERROR = new CodeMsg(3001, "保存信息失败");
	public static CodeMsg HAVAED_VALIDATE = new CodeMsg(300005, "已验证");
	
	//优惠劵
	public static CodeMsg REUCTION_END = new CodeMsg(4001, "优惠劵已过期");
	
	//优惠劵
	public static CodeMsg REUCTION_NO_STOCK = new CodeMsg(4002, "优惠劵已发完");
	//优惠劵
	public static CodeMsg REUCTION_HANVED_RECIVE = new CodeMsg(4003, "优惠劵已经领取过");
	
	

	public static CodeMsg MSG_SEND_SUCCESS = new CodeMsg(300000, "验证码发送成功");
	public static CodeMsg MSG_SEND_FAIL = new CodeMsg(300001, "验证码发送失败");
	public static CodeMsg MSG_NOT_OVER = new CodeMsg(300006, "一分钟只能发送一次");
	
	public static CodeMsg MSG_VALIDATE_FAIL = new CodeMsg(400001, "校验失败");
	public static CodeMsg EXPECTION_ERROR = new CodeMsg(400002, "操作失败，请联系管理员","%s");
	
	
	private CodeMsg( int code,String msg,String token) {
		this.code = code;
		this.msg = msg;
		this.token = token;
	}
	
	private CodeMsg( int code,String msg) {
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public CodeMsg fillArgs(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		return new CodeMsg(code, message);
	}
	
	public CodeMsg fillArgsToken(Object... args) {
		int code = this.code;
		String message = this.msg;
		String token = String.format(this.token, args);
		return new CodeMsg(code, message,token);
	}
	
	public CodeMsg fillMsg(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args);
		String token = this.token;
		return new CodeMsg(code, message,token);
	}
	
	
	public CodeMsg fillArgsMsgAndToken(Object... args) {
		int code = this.code;
		String message = String.format(this.msg, args[0]);
		String token = String.format(this.token, args[1]);
		return new CodeMsg(code, message,token);
	}

	@Override
	public String toString() {
		return "CodeMsg [code=" + code + ", msg=" + msg + ", token=" + token
				+ "]";
	}

	
	
	
}
