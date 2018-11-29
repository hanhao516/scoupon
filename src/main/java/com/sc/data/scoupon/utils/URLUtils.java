package com.sc.data.scoupon.utils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class URLUtils {
	/**
	 * 
	 * @Title: getDomainFormUrl
	 * @Description: 获取域名
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 * @return: String
	 */
	public static String getDomainFormUrl(String url) {
		String result = "";
		if (StringUtils.isBlank(url)) {
			return "";
		}
		try {
			if (!url.startsWith("http")) {
				url = ("http://" + url);
			}
			URL dd = new URL(url);
			result = dd.getHost();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 解析出url参数中的键值对 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
	 * 
	 * @param URL
	 *            url地址
	 * @return url请求参数部分
	 */
	public Map<String, String> uRLRequest(String URLx) {
		Map<String, String> mapRequest = new HashMap<String, String>();

		String[] arrSplit = null;
		if (StringUtils.isBlank(URLx))
			return mapRequest;
		URL u = null;
		try {
			u = new URL(URLx);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (u == null)
			return mapRequest;
		String strUrlParam = u.getQuery();
		if (strUrlParam == null) {
			return mapRequest;
		}
		// 每个键值为一组
		arrSplit = strUrlParam.split("[&]");
		for (String strSplit : arrSplit) {
			try {
				String[] arrSplitEqual = null;
				arrSplitEqual = strSplit.split("[=]");

				// 解析出键值
				if (arrSplitEqual.length > 1) {
					// 正确解析
					mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

				} else {
					if (arrSplitEqual[0] != "") {
						// 只有参数没有值，不加入
						mapRequest.put(arrSplitEqual[0], "");
					}
				}
			} catch (Exception e) {

				System.out.println(URLx);
				e.printStackTrace();
				// TODO: handle exception
			}
		}
		return mapRequest;
	}

	public static void main(String[] args) {
		System.out.print(getDomainFormUrl("www.google.co.uk?jjj=kkk"));
	}

}
