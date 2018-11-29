package com.sc.data.scoupon.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sc.data.scoupon.stat.SysStat;

public class WxUtils {
	public static void main(String[] args) {
		System.out.println(new WxUtils().getWxUserIdByJscode(SysStat.xiaoshuili_wx_appId
				, SysStat.xiaoshuili_wx_appSecret, "01163Wg31wmb1O1pLhe31s3ah3163Wg7"));
	}
	public Map<String, Object> getWxUserIdByJscode(String appid,String appsecret,String jscode){
		Map<String, Object> wx_map = new HashMap<String, Object>();
		if(StringUtils.isBlank(appid) ||
				StringUtils.isBlank(appsecret) ||
				StringUtils.isBlank(jscode) )
		{
			return wx_map;
		}
		String url = "https://api.weixin.qq.com/sns/jscode2session"
				+ "?appid="+appid
				+ "&secret="+appsecret
				+ "&js_code="+jscode
				+ "&grant_type=authorization_code";
		WXQR wxqr = new WXQR();
		try {
			String result = wxqr.takeJsonFromStr(wxqr.httpGet(url, new HashMap<String, String>() , ""));
			wx_map.putAll(new Conver().converMap(result));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//{"session_key":"fU0fFA1yKukNLNu\/zZu1Og==","openid":"oVhVc5eT19_7V61v8Kgn2UspMNIU"}
		return wx_map;
	}
	
}
