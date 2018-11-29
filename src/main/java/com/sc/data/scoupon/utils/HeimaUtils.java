package com.sc.data.scoupon.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class HeimaUtils {
	public static void main(String[] args) throws IOException {
//		Map<String, Object> item= new HeimaUtils().almmItemSearch(null, "566554270725");
//		String title = (String) item.get("title");
//		System.out.println(title);
//		String couponInfo = (String) item.get("couponInfo");
//		System.out.println(couponInfo);
	}
//	public Map<String, Object> almmItemSearch(String cookieStr,String itemId) throws IOException{
//		if(StringUtils.isBlank(itemId) )
//		{
//			return null;
//		}
//		WXQR wxqr = new WXQR();
//        String url = "https://pub.alimama.com/items/search.json?q=https://detail.tmall.com/item.htm?id=" + itemId;
//		String s = wxqr.httpGet(url , new HashMap<String, String>(), cookieStr);
//		String result = wxqr.takeJsonFromStr(s);
//		Map<String, Object> map = new Conver().converMap(result);
//		Map<String, Object> data = (Map<String, Object>) map.get("data");
//		List<Map<String, Object>> pageList = (List<Map<String, Object>>) data.get("pageList");
//		if(pageList==null){
//			return new HashMap<String, Object>();
//		} else {
//			return pageList.get(0);
//		}
//	}
	public String itemSearch(String appkey,String appsecret,String sid,String pid,
			String item_id) throws IOException{
		if(StringUtils.isBlank(appkey) ||
				StringUtils.isBlank(appsecret) ||
				StringUtils.isBlank(sid) ||
				StringUtils.isBlank(pid) ||
				StringUtils.isBlank(item_id) )
		{
			return null;
		}
		String url = "https://www.heimataoke.com/api-zhuanlian?appkey="
				+appkey+"&appsecret="
				+ appsecret +"&sid="
				+ sid +"&pid="
				+ pid+"&num_iid="
				+ item_id +"&me=&relation_id=";
		WXQR wxqr = new WXQR();
		String result = wxqr.takeJsonFromStr(wxqr.httpGet(url, new HashMap<String, String>() , ""));
		return result;
	}
	
	public String orderSearch(String appkey,String appsecret,String sid,String start_time,
			String span) throws IOException{
		if(StringUtils.isBlank(appkey) ||
				StringUtils.isBlank(appsecret) ||
				StringUtils.isBlank(sid) ||
				StringUtils.isBlank(start_time) ||
				StringUtils.isBlank(span) )
		{
			return null;
		}
		String url = " https://www.heimataoke.com/api-qdOrder?appkey="+appkey
				+"&appsecret="+ appsecret
				+"&sid="+ sid 
				+ "&start_time=" + start_time
				+ "&span=" + span
				//+ "&page_no=" + page_no
				//+ "&page_size=" + page_size
				+ "&order_query_type=create_time"
				+ "&tk_status=12&order_scene=1";
		WXQR wxqr = new WXQR();
		String result = wxqr.takeJsonFromStr(wxqr.httpGet(url, new HashMap<String, String>() , ""));
		return result;
	}
}
