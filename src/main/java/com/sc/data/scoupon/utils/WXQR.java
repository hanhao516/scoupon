package com.sc.data.scoupon.utils;
 
 import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

import com.sc.data.scoupon.stat.SysStat;
 
 public class WXQR {
     
     public  Map<String, Object> createWXQRMap(String share_id,String item_id) {
    	 String access_token = this.getAccessToken();
         String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+access_token;
         Map<String, Object> map = new HashMap<String, Object>();
         map.put("expire_seconds", "2592000");
//         map.put("expire_seconds", "604800");
         map.put("action_name", "QR_STR_SCENE");
//         map.put("action_name", "QR_SCENE");
         Map<String, Object> map1 = new HashMap<String, Object>();
         map.put("action_info", map1);
         Map<String, Object> map2 = new HashMap<String, Object>();
         map1.put("scene", map2);
//         map2.put("scene_id", 11111);
         map2.put("scene_str", item_id+"&"+share_id);
         String s = null;
		try {
			s = this.httpPost(url, map, "");
		} catch (IOException e) {
			e.printStackTrace();
		}
         System.out.println(s);
         Map<String, Object> rep = new Conver().converMap(this.takeJsonFromStr(s));
         return rep;
     }
     public  Map<String, Object> createWXQRMap(Long share_id,String access_token) {
    	 if(StringUtils.isBlank(access_token)){
    		 access_token = this.getAccessToken();
    	 }
    	 String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+access_token;
    	 Map<String, Object> map = new HashMap<String, Object>();
    	 map.put("expire_seconds", "2592000");
//         map.put("expire_seconds", "604800");
         map.put("action_name", "QR_SCENE");
    	 Map<String, Object> map1 = new HashMap<String, Object>();
    	 map.put("action_info", map1);
    	 Map<String, Object> map2 = new HashMap<String, Object>();
    	 map1.put("scene", map2);
         map2.put("scene_id", share_id);
    	 String s = null;
    	 try {
    		 s = this.httpPost(url, map, "");
    	 } catch (IOException e) {
    		 e.printStackTrace();
    	 }
    	 System.out.println(s);
    	 Map<String, Object> rep = new Conver().converMap(this.takeJsonFromStr(s));
    	 return rep;
     }
     public  Map<String, Object> createWXQR(String share_id,String item_id) {
    	 Map<String, Object> map  = this.createWXQRMap(share_id, item_id);
    	 String ticket = (String) map.get("ticket");
    	 String url  = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+ticket;
    	 String qr_image_path = this.downImages("d:/img", url);
    	 map.put("qr_image_path", qr_image_path);
    	 return map;
     }
     
    public String getAccessToken() {
        String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+SysStat.appId+"&secret="+SysStat.appSecret;
        String s = null;
		try {
			s = this.httpPost(url, new HashMap<String, Object>(), "");
		} catch (IOException e) {
			e.printStackTrace();
		}
        System.out.println(s);
        
        Map<String, Object> rep = new Conver().converMap(this.takeJsonFromStr(s));
        return (String) rep.get("access_token");
	}
	public static void main(String[] args) {
		//		new WXQR().createWXQR("oOon30pXflNPJkGg2U-2Vl9QvTdQ","562676830629");
		Map<String, String> map = new HashMap<String, String>();
//		map.put("tag", "29");
//		map.put("gcid", "8");
//		map.put("siteid", "41976129");
//		map.put("selectact", "add");
//		map.put("newadzonename", "测试2_1");
//		map.put("t", "1516074272970");
//		map.put("_tb_token_", "77e935891f704");
//		map.put("pvid", "10_60.177.35.211_8615_1516074229043");
//		String url = "https://pub.alimama.com/common/adzone/selfAdzoneCreate.json";
//		String url = "https://pub.alimama.com/common/site/generalize/guideAdd.json";
		String url = "https://pub.alimama.com/common/site/generalize/guideList.json";
//		map.put("name", "测试3");
//		map.put("categoryId", "700");
//		map.put("account1", "测试三");
		map.put("t", "");
		map.put("pvid", "");
		map.put("_tb_token_", "e8a373baea1e7");
		map.put("_input_charset", "utf-8");
		String cookie = "t=123495e82a68d78edb09fd2e3b2d84cc; l=AvHxqG7YAOSddbk4jU6FIiPdgXeOLWUE; account-path-guide-s1=true; 109747463_yxjh-filter-1=true; undefined_yxjh-filter-1=true; 125864756_yxjh-filter-1=true; 127489685_yxjh-filter-1=true; cna=uoDFEL2zZWgCAWUvEZ6NBYwo; qq-best-goods-down-time=1514442096417; 23745785_yxjh-filter-1=true; cookie2=116edb28c4706b2b695a8b3ff673b516; v=0; _tb_token_=e8a373baea1e7; alimamapwag=TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV2luNjQ7IHg2NCkgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzU4LjAuMzAyOS4xMTAgU2FmYXJpLzUzNy4zNg%3D%3D; cookie32=11fe19f516ac5e9582c98cb4cbed9fc0; alimamapw=E3VzRHAAFCYHQHYHRyBxRCRzE3VwRHF1OFQDAQMHA1QHUQACAQ4HAlMBUlcBVgRTU1QBB1YGUlJX%0A; cookie31=MjM3NDU3ODUsJUU5JTg1JUI3JUU1JUFFJTlEJUU2JTk1JUIwJUU2JThEJUFFLHNlcnZpY2VAa29vbGJhby5jb20sVEI%3D; login=W5iHLLyFOGW7aA%3D%3D; apush6e5fb645be3015288a6c6962730dbcc5=%7B%22ts%22%3A1516273385778%2C%22parentId%22%3A1516272789400%7D; isg=BP__m7Y3mVYjgZ_iZRdtpkTcjtNJTEzxxS5txJHB267yoAgilrDv1yf25nBe-Cv-";
		try {
			String ss = new WXQR().httpGet(url, map, cookie);
			System.out.println(ss);
			Map<String , Object> site = new Conver().converMap(new WXQR().takeJsonFromStr(ss));
			List<Map<String, Object>> guideList = (List<Map<String, Object>>) ((Map<String, Object>)site.get("data")).get("guideList");
			for (Map<String, Object> guide : guideList) {
				String name = (String) guide.get("name");//"测试111"
				if("测试111".equals(name)){
					System.out.println(guide);
					String guideID  = new BigDecimal((Double)guide.get("guideID")).toString();
					System.out.println(guideID);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String takeJsonFromStr(String str) {
		if(str==null) return str;
		String a = "";
		String b = "";
		if(str.contains("{") && str.contains("}")){
			a = "{";
			b =	"}";
		}
		else if (str.contains("[") && str.contains("]")){
			a = "[";
			b =	"]";
		}
		String s = str.substring(str.indexOf(a), str.lastIndexOf(b)+1);
		return s ;
	}
	 public String httpGet(String url,Map<String,String> map,String cookie) throws IOException{
		 	String param = "";
		 	for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				param += key+"="+value+"&";
			}
		 	if (StringUtils.isNotBlank(param)){
		 		url = url+"?"+param; 
		 	}
		 	
	        //获取请求连接
	        Connection con = Jsoup.connect(url);
	        //请求头设置，特别是cookie设置
	        con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))"); 
	        con.header("Cookie", cookie);
	        //解析请求结果
	        Document doc=con.ignoreContentType(true).get(); 
	        //获取标题
	        System.out.println(doc.title());
	        return doc.toString();
	    }
	public  String httpPost(String url,Map<String,Object> map,String cookie) throws IOException{
         Connection con = Jsoup.connect(url);
         con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))"); 
//         con.header("Content-Type", "application/json;encoding=utf-8"); 
	     String param = new Conver().parseJason(map);
	     System.out.println(param);
	     con.requestBody(param);
         con.header("Cookie", cookie);
         con.ignoreContentType(true);
         Document doc = con.post();  
         System.out.println(doc);
         return doc.toString();
     }
	public  String httpPostWithOutJson(String url,Map<String,String> map,String cookie) throws IOException{
		//获取请求连接
		Connection con = Jsoup.connect(url);
		con.header("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0))"); 
//         con.header("Content-Type", "application/json;encoding=utf-8"); 
        //遍历生成参数
        if(map!=null){
            for (Entry<String, String> entry : map.entrySet()) {     
               //添加参数
                con.data(entry.getKey(), entry.getValue());
               } 
        }
        //插入cookie（头文件形式）
        con.header("Cookie", cookie);
		con.ignoreContentType(true);
		Document doc = con.post();  
		System.out.println(doc);
		return doc.toString();
	}
	
	 /**
     * 下载图片到指定目录
     *
     * @param filePath 文件路径
     * @param imgUrl   图片URL
     */
    public  String downImages(String filePath, String imgUrl) {
        // 若指定文件夹没有，则先创建
        File dir = new File(filePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 截取图片文件名
        String fileName = UUID.randomUUID().toString().replace("-", "")+".jpg";

        try {
            // 文件名里面可能有中文或者空格，所以这里要进行处理。但空格又会被URLEncoder转义为加号
            String urlTail = URLEncoder.encode(fileName, "UTF-8");
            // 因此要将加号转化为UTF-8格式的%20
            imgUrl = imgUrl.replaceAll("\\+", "\\%20");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 写出的路径
        File file = new File(filePath + File.separator + fileName);

        try {
            // 获取图片URL
            URL url = new URL(imgUrl);
            // 获得连接
            URLConnection connection = url.openConnection();
            // 设置10秒的相应时间
            connection.setConnectTimeout(10 * 1000);
            // 获得输入流
            InputStream in = connection.getInputStream();
            // 获得输出流
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            // 构建缓冲区
            byte[] buf = new byte[1024];
            int size;
            // 写入到文件
            while (-1 != (size = in.read(buf))) {
                out.write(buf, 0, size);
            }
            out.close();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
    public void fileDown(String url,String cookies, String downFilePath,String fileName) throws IOException{
    	// 若指定文件夹没有，则先创建
        File dir = new File(downFilePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    	//Open a URL Stream
    	Response excelResponse = Jsoup.connect(url).header("Cookie", cookies).ignoreContentType(true).execute();
    	// output here
    	FileOutputStream out = (new FileOutputStream(new File(downFilePath+File.separator+fileName)));
    	out.write(excelResponse.bodyAsBytes());           
    	// resultImageResponse.body() is where the image's contents are.
    	out.close();
    }
 }
