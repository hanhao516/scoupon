package com.sc.data.scoupon.stat;

public class SysStat {
	// 登陆方式
	public static final String SMV_login = "1";
	public static final String passwd_login = "2";
	//微信扫码 loginid 的状态
	public static final String loginid_created = "1";
	public static final String loginid_login = "2";
	
	public static final int loginid_redis_db = 2;
	
	public static final String USER_SESSION_KEY = "user_id";
	public static final String OA_Session = "OAJedisSessionId";
	// excel order 文件路径
	public static String excel_order_path = "D:/excelOrders/";
	// 注册行为标志
	public static String active_regist = "1";
	// 注册行为标志
	public static String active_login = "5";
	// 修改密码行为标志
	public static String active_update_passwd = "2";
	// 修支付宝账号标志
	public static String active_update_alipay = "3";
	// 绑定手机号标志
	public static String active_bind_tel = "4";
	
	public static String pci_local_path = "/data/image/";
//	public static String pci_local_path = "c:/image/";
	public static String web_path = "http://tk.7fanli.com:8080/Seven/image/";
	//微信公众号 appid 和appsecret
//			appId = "wx08327828a2ce2fe5"
//			appSecret = "a017122d2e8dd8e0c37bbd4f31e97be4"
	public final static String appId = "wx08327828a2ce2fe5";
	public final static String appSecret = "a017122d2e8dd8e0c37bbd4f31e97be4";
//	public static String web_path = "http://192.168.31.119:8080/Seven/image/";
	public final static String xiaoshuili_wx_appId1 = "wx32468e8bc0549038";
	public final static String xiaoshuili_wx_appSecret1 = "9e0bb894aa02242376314674cda03103";
	public final static String xiaoshuili_wx_appId = "wxe6354caaf965f75c";
	public final static String xiaoshuili_wx_appSecret = "c6a2c090d1bd13bc8c35f7e4c67e525d";

}
