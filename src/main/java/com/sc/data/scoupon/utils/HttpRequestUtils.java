package com.sc.data.scoupon.utils;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
 
public class HttpRequestUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);    //日志记录
 
    /**
     * httpPost
     * @param url  路径
     * @param jsonParam 参数
     * @return
     */
    public  String httpPost(String url,JSONObject jsonParam){
        return httpPost(url, jsonParam, false);
    }
 
    /**
     * post请求
     * @param url         url地址
     * @param jsonParam     参数
     * @param noNeedResponse    不需要返回结果
     * @return
     */
    public  String  httpPost(String url,JSONObject jsonParam, boolean noNeedResponse){
    	String str = "";
    	//post请求返回结果
        DefaultHttpClient httpClient = new DefaultHttpClient();
        JSONObject jsonResult = null;
        HttpPost method = new HttpPost(url);
        try {
            if (null != jsonParam) {
                //解决中文乱码问题
                StringEntity entity = new StringEntity(jsonParam.toString(), "utf-8");
                entity.setContentEncoding("UTF-8");
                entity.setContentType("application/json");
                method.setEntity(entity);
            }
            HttpResponse result = httpClient.execute(method);
            url = URLDecoder.decode(url, "UTF-8");
            /**请求发送成功，并得到响应**/
            if (result.getStatusLine().getStatusCode() == 200) {
              
                try {
                    /**读取服务器返回过来的json字符串数据**/
                    str = EntityUtils.toString(result.getEntity());
                    if (noNeedResponse) {
                        return null;
                    }
                    /**把json字符串转换成json对象**/
                } catch (Exception e) {
                    logger.error("post请求提交失败:" + url, e);
                }
            }
        } catch (IOException e) {
            logger.error("post请求提交失败:" + url, e);
        }
        return str;
    }
 
 
    /**
     * 发送get请求
     * @param url    路径
     * @return
     */
    public  String httpGet(String url){
    	String strResult  = "";
        //get请求返回结果
        try {
            DefaultHttpClient client = new DefaultHttpClient();
            //发送get请求
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
 
            /**请求发送成功，并得到响应**/
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                /**读取服务器返回过来的json字符串数据**/
                strResult = EntityUtils.toString(response.getEntity());
                /**把json字符串转换成json对象**/
                url = URLDecoder.decode(url, "UTF-8");
            } else {
                logger.error("get请求提交失败:" + url);
            }
        } catch (IOException e) {
            logger.error("get请求提交失败:" + url, e);
        }
        return strResult;
    }
    public Map<String ,String > getParamMap(Map<String, String[]> map) {
		Map<String ,String > param = new HashMap<String, String>();
		for (Map.Entry<String ,String[] > entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue()[0];
			param.put(key, value);
		}
		return param;
	}

	public String getParamStr(Map<String, String> param,boolean encode) {
		String paramStr = "";
		int i = 0;
		for (Map.Entry<String ,String > entry : param.entrySet()) {
			String key = entry.getKey();
			String value = "";
			try {
				value = new String(entry.getValue().getBytes("ISO-8859-1"),"UTF-8");
				if(encode){
					value = URLEncoder.encode(value,"UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			};
			String entryStr = "";
			if(i!=0){
				entryStr = "&"+key+"="+value;
			}else{
				entryStr = key+"="+value;
			}
			paramStr += entryStr;
			i++;
		}
		return paramStr;
	}
}