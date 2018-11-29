package com.sc.data.scoupon.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.qr.QrcodeUtils;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.task.AlmmOrderDownTask;
import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.HttpRequestUtils;
import com.sc.data.scoupon.utils.MD5Util;
import com.sc.data.scoupon.utils.RedisUtil;
import com.sc.data.scoupon.utils.UrlAnalysis;
import com.sc.data.scoupon.utils.WXQR;



@Controller
@RequestMapping(value = "/fanli")  
public class FanliController_his {
	@Autowired
	private FanliService fanliService;

	private static Logger logger = Logger.getLogger(FanliController_his.class);

	@RequestMapping("almmOrderDown.do")
	@ResponseBody
	public void  almmOrderDown(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		AlmmOrderDownTask aodt  = new AlmmOrderDownTask();
		try {
			aodt.TaskJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@RequestMapping("test2.do")
	@ResponseBody
	public void  test2(HttpServletRequest req ,HttpServletResponse res,String wx_id) throws IOException{

		List<Map<String, Object>> user = fanliService.selectUserByWxid(wx_id);
		//设置session对象5分钟失效
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(30*24*60*60);
		String user_name= (String) user.get(0).get("user_name");
		//将验证码保存在session对象中,key为validation_code
		session.setAttribute("userName", user_name );
		res.sendRedirect("http://7fanli.com/fanliht/fanli_channel/gounanzhen/index.html");
	}
	@RequestMapping("test3.do")
	@ResponseBody
	public String  test3(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		FanliService f = wac.getBean(FanliService.class);
		Map<String, Object> cookie = f.getCookie(null);
		return cookie.toString();
	}
	/**
	 * 购南针用户登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("GNZUserLogin.do")
	@ResponseBody
	public void  GNZUserLogin(HttpServletRequest req ,HttpServletResponse res,String token,
			String jump_url,String channel_id) throws IOException{
			if(StringUtils.isBlank(channel_id)){
				channel_id = "1001";
			}
			Map<String , Object> channel_param = new HashMap<String, Object>();
			channel_param.put("channel_id", channel_id);
			List<Map<String, Object>> channel_list  = fanliService.getChannelInfo(channel_param);
			String token_url = (String) channel_list.get(0).get("token_url");
	//		token = "97046baa-d83f-4aa6-8019-d41ad8898d75";
	//		"	";
			String url = token_url+token;
			// 随机token通过url获取数据
			Map<String , Object> param = fanliService.getWxInfoByToken(url);
			param.put("channel_id", channel_id);
			Map<String, Object> reslut = null;
			int newUserCount = fanliService.apendWxInfo(param,null);
			reslut.put("newUserCount", newUserCount);
			List<Map<String, Object>> user = fanliService.selectUserByWxid((String) param.get("openid"));
			//设置session对象5分钟失效
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(30*24*60*60);
			String user_name= (String) user.get(0).get("user_name");
			String wx_id= (String) user.get(0).get("wx_id");
		//将验证码保存在session对象中,key为validation_code
			session.setAttribute("userName", user_name);
			if(StringUtils.isBlank(jump_url)){
				res.sendRedirect("http://7fanli.com/fanliht/fanli_channel/gounanzhen/index.html");
			}else{
				String childParentUrl="https://gzh.7fanli.com/shareRelation";
				//解析url
				Map<String, String> urlMap = UrlAnalysis.URLRequest(jump_url);
				if(urlMap.containsKey("wx_id")){
					String parentWxid = urlMap.get("wx_id");
					//查找分享者微信id share_id 确定子父级关系
					Map<String,Object> childParentMap = new HashMap<>();
					childParentMap.put("parentWxid",parentWxid);
					childParentMap.put("wxid",wx_id);
					//调用接口的url
					childParentMap.put("url",childParentUrl);
					childParentMap.put("channel_id",channel_id);
					fanliService.childParent(childParentMap);
				}
				//1 openid 2channel_id 3share_id
				res.sendRedirect(jump_url);
			}
		}

	/**
	 * 购南针用户登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getAdzoneInfo.do")
	@ResponseBody
	public String  getAdzoneInfo(HttpServletRequest req ,HttpServletResponse res,String callback
			,String unionid,String channel_id) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		if(StringUtils.isBlank(unionid) || StringUtils.isBlank(channel_id)){
			reslut.put("flag", false);
			reslut.put("msg", "unionid and channel_id can not be null");
		}else{
			String wx_id = unionid;
			List<Map<String, Object>> user = fanliService.selectUserByWxid(wx_id);
			if (user.size()==0){
				Map<String , Object> param = new HashMap<String, Object>();
				param.put("openid", wx_id);
				param.put("channel_id", channel_id);
				fanliService.apendWxInfo(param,null);
				user = fanliService.selectUserByWxid(wx_id);
			}
			String user_id  = user.size()==0?null:user.get(0).get("user_id")+"";
			reslut = fanliService.getAdzoneInfoByUserId(user_id);
			reslut.put("flag", true);
		}
		
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 获取分享二维码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("shareQR.do")
	@ResponseBody
	public String  shareQR(HttpServletRequest req ,HttpServletResponse res
			,String item_id, String callback,String user_id,String title) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(req.getParameter("title"))){
			title = URLDecoder.decode(req.getParameter("title"), "UTF-8");
			reslut.put("title", title);
		}
		List<Map<String , Object>> users = new ArrayList<Map<String,Object>>();
		if(StringUtils.isBlank(user_id)){
			HttpSession session = req.getSession();
		    String session_userName = session.getAttribute("userName")==null?null:(String) session.getAttribute("userName");
		    //根据userName查询用户个数
	  		User param  = new User();
//	  		param.setUser_name(session_userName);
//	  		param.setUser_name("18338319095");
//	  		param.setUser_name("421004797584343040");
	  		users = fanliService.selectUser(param);
		    user_id = (long) users.get(0).get("user_id") + "";
		}else{
			User param  = new User();
	  		param.setUser_id(user_id);
	  		users = fanliService.selectUser(param);
		}
	    reslut.put("item_id",item_id );
		String share_id = null;
		String qr_url = null;
	    if(users.size()==1){
	    	reslut.put("user_id",user_id );
	    	Map<String , Object> param_map = new HashMap<String, Object>();
	    	param_map.put("item_id", item_id);
	    	param_map.put("user_id", user_id);
	    	//查询share
	    	List<Map<String , Object>> shares = fanliService.selectShareActs(param_map);
	    	if(shares==null || shares.size()<1){
	    		//保存分享行为
	      		int count = fanliService.saveAdAct(reslut);
	      		if(count == 0 ){
	      			reslut.put("msg","saveAdAct fialed" );
	      			String json = JSONObject.toJSONString(reslut); 
	      			if(StringUtils.isNotBlank(callback))
	      				return   callback + "(" +json+")";
	      			else
	      				return   json;
	      		}else{
	      			shares = fanliService.selectShareActs(param_map);
	      		}
	    	}
	    	share_id = shares.get(0).get("share_id")+"";
	    	reslut.put("share_id", share_id);
	    	qr_url = shares.get(0).get("qr_url")==null?null:shares.get(0).get("qr_url").toString();
	    }else{
	    	return null;
	    }
	    int channel_id = (int) users.get(0).get("channel_id");
	    Map<String, Object> c_param = new HashMap<String, Object>();
	    c_param.put("channel_id", channel_id);
	    List<Map<String, Object>> channelInfo = fanliService.getChannelInfo(c_param);
	    String url  = null;
		//如果空去微信请求二维码
		if(StringUtils.isBlank(qr_url)){
			//获得微信关注url
			Map<String, Object> map = fanliService.createWXQRMap(share_id,channel_id);
			url = (String) map.get("url");
			reslut.put("qr_url",url );
			reslut.put("errmsg",map.get("errmsg") );
			try {
				if(StringUtils.isNotBlank(url)){
					fanliService.updateAdAct(reslut);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			url = qr_url;
		}
		if(StringUtils.isNotBlank(url)){
			String path=req.getServletContext().getRealPath("/");
			//将url转换成二维码
			String fileName = UUID.randomUUID().toString().replace("-", "")+".jpg";
			String filePath = path+"image/"+fileName;
			String logoName = channelInfo.size() == 0 || channelInfo.get(0).get("logo") == null || StringUtils.isBlank((String) channelInfo.get(0).get("logo"))?"image/7logo.jpg":"image/"+ channelInfo.get(0).get("logo");

			try {
				QrcodeUtils.gen(url, 
						filePath, 
						path+logoName, 
						430, 
						430);
			} catch (Exception e) {
				e.printStackTrace();
			};
			//将二维码信息保存到数据库？


			//copy 到指定文件夹
			File srcFile = new File(filePath);
			File destFile = new File("/home/wwwroot/7fanli/7fanli.com/fanlitg/images/qr/"+fileName);
			try {
				if(!new File("/home/wwwroot/7fanli/7fanli.com/fanlitg/images/qr/",fileName).exists()){
					try {
						FileUtils.copyFile(srcFile, destFile);
						srcFile.deleteOnExit();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//返回二维码路径
			reslut.put("imgUrl",fileName );
		}
		
		
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
//		//将二维码返回
//		File file = new File(filePath);
//		
//        //判断文件是否存在如果不存在就返回默认图标
//        if(!(file.exists() && file.canRead())) {
//            file = new File(req.getSession().getServletContext().getRealPath("/")
//                    + "resource/icons/auth/root.png");
//        }
//
//        FileInputStream inputStream = new FileInputStream(file);
//        byte[] data = new byte[(int)file.length()];
//        int length = inputStream.read(data);
//        inputStream.close();
//
//        res.setContentType("image/png");
//
//        OutputStream stream = res.getOutputStream();
//        stream.write(data);
//        stream.flush();
//        stream.close();
	}
	/**
	 * 图片放入phpWeb的文件夹下
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("copyImgToPHP.do")
	@ResponseBody
	public String  copyImgToPHP(HttpServletRequest req ,HttpServletResponse res
			,String webImgUrl, String callback) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		String path=req.getServletContext().getRealPath("/");

		String downImgPath = new WXQR().downImages(path, webImgUrl);
		String fileName = downImgPath.substring(downImgPath.lastIndexOf("/")+1);
//		String fileName = downImgPath.substring(downImgPath.lastIndexOf("\\")+1);
		//copy 到指定文件夹
		File srcFile = new File(downImgPath);
		File destFile = new File("/home/wwwroot/7fanli/7fanli.com/fanlitg/images/qr/"+fileName);
		try {
			if(!new File("/home/wwwroot/7fanli/7fanli.com/fanlitg/images/qr/",fileName).exists()){
				try {
					FileUtils.copyFile(srcFile, destFile);
					srcFile.deleteOnExit();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//返回二维码路径
		reslut.put("imgUrl",fileName );
		
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 微信用户数据保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("apendWxInfo.do")
	@ResponseBody
	public String  apendWxInfo(HttpServletRequest req ,HttpServletResponse res , String callback ) throws IOException{
		
		
		String paramJson = new String(req.getParameter("user").getBytes("ISO-8859-1"),"UTF-8");

//		if(paramJson==null){
//			paramJson="{\"openid\":\"oOon30pXflNPJkGg2U-2Vl9QvTdQ\",\"nickname\":\"欧阳明\",\"sex\":1,\"language\":\"zh_CN\",\"city\":\"杭州\",\"province\":\"浙江\",\"country\":\"中国\",\"headimgurl\":\"http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83erfOO7Yiabch3ob48ibic2wp2icibkHm47pNF9ArwaHbO5ItrNKdiaLicrm4GqvUTI1VMuRic7UCWxB9e8aibA/0\",\"privilege\":[]}";
//
//		}
		Conver c = new Conver();
		Map<String, Object> datamap = c.converMap(paramJson);
		Map<String, Object> reslut = null;
		int newUserCount = fanliService.apendWxInfo(datamap,null);
		reslut.put("newUserCount", newUserCount);
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 微信登陆id状态查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("loginStatus.do")
	@ResponseBody
	public String  loginStatus(HttpServletRequest req ,HttpServletResponse res , 
			String loginId ,String callback ) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		//将userName保存到redis 
		Jedis jedis = RedisUtil.getJedis();
		jedis.select(SysStat.loginid_redis_db);
    	String status = jedis.hget(loginId, "status");
    	if(SysStat.loginid_login.equals(status)){
    		String wx_id = jedis.hget(loginId, "wx_id");
    		List<Map<String, Object>> user = fanliService.selectUserByWxid(wx_id);
    		//设置session对象5分钟失效
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(30*24*60*60);
			String user_name= (String) user.get(0).get("user_name");
			//将验证码保存在session对象中,key为validation_code
			session.setAttribute("userName", user_name );
//			jedis.hset(loginId, "user_name", user_name);
			reslut.put("sessionflag", true);
    	}
    	RedisUtil.returnResource(jedis);
		reslut.put("status", status);
		reslut.put("loginId", loginId);
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 微信用户直接登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("wxLogin.do")
	@ResponseBody
	public String  wxLogin(HttpServletRequest req ,HttpServletResponse res , 
			String loginId , String openid, String callback ) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		List<Map<String, Object>> user = fanliService.selectUserByWxid(openid);
		if(user.size()==1){
			//将userName保存到redis 
			Jedis jedis = RedisUtil.getJedis();
			jedis.select(SysStat.loginid_redis_db);
			String status = jedis.hget(loginId, "status");
			if(StringUtils.isNotBlank(status)){
				jedis.hset(loginId, "status", SysStat.loginid_login);
				jedis.hset(loginId, "wx_id", openid);
//				jedis.expire(loginId, 60*2);
				
			}else{
				reslut.put("msg", "null loginId");
				reslut.put("sessionflag", false);
			}
			RedisUtil.returnResource(jedis);
		}else{
			reslut.put("msg", "useless user");
			reslut.put("sessionflag", false);
		}
		reslut.put("loginId", loginId);
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 微信公众号用户直接登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("weChatPublicLogin.do")
	@ResponseBody
	public String  weChatPublicLogin(HttpServletRequest req ,HttpServletResponse res , 
			String loginId , String openid, String callback  ) throws IOException{
		Map<String, Object> reslut = new HashMap<String, Object>();
		//将userName保存到redis 
		Jedis jedis = RedisUtil.getJedis();
		jedis.select(SysStat.loginid_redis_db);
    	String status = jedis.hget(loginId, "status");
    	if(SysStat.loginid_login.equals(status)){
    		String wx_id = jedis.hget(loginId, "wx_id");
    		List<Map<String, Object>> user = fanliService.selectUserByWxid(wx_id);
    		//设置session对象5分钟失效
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(30*24*60*60);
			String user_name= (String) user.get(0).get("user_name");
			//将验证码保存在session对象中,key为validation_code
			session.setAttribute("userName", user_name );
			reslut.put("sessionflag", true);
			res.sendRedirect("http://7fanli.com/fanliht/dist/index.html");
    	}
    	RedisUtil.returnResource(jedis);
		reslut.put("status", status);
		reslut.put("loginId", loginId);
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 微信公众号用户直接登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 * {
	 * "taobao_user_nick": "taobaonick",
	 * "re_expires_in": 0,
	 * "expires_in": 7776000,
	 * "expire_time": 1459911894299,
	 * "r1_expires_in": 1800,
	 * "w2_valid": 1452135894299,
	 * "w2_expires_in": 0,
	 * "w1_expires_in": 1800,
	 * "r1_valid": 1452137694299,
	 * "r2_valid": 1452135894299,
	 * "w1_valid": 1452137694299,
	 * "r2_expires_in": 0,
	 * "token_type": "Bearer",
	 * "refresh_token": "620251524f72a26b6c8ecd1ZZe29bbbxxx",
	 * "open_uid": "AAENArTTACOmNcx4Z-_D0qU2",
	 * "refresh_token_valid_time": 1452135894299,
	 * "access_token": "620141595ca09af54aa5918ZZeafd0c0fe770bb07xxx"
	 * }
	 */
	@RequestMapping("taobaoPublicLogin.do")
	@ResponseBody
	public void  taobaoPublicLogin(HttpServletRequest req ,HttpServletResponse res , 
			String code, String callback  ) throws IOException{
			Map<String, Object> reslut = new HashMap<String, Object>();
			//根据code查询淘宝信息
			Map<String, Object> taobaoInfo = fanliService.getTaobaoInfo(code);
			//添加淘宝用户
			int count = fanliService.apendTbUser(taobaoInfo);
			//查询用户
			String tb_uid =  (String) taobaoInfo.get("taobao_user_id");
			User user = new User();
			user.setTb_uid(tb_uid);
			Map<String, Object> tb_user = fanliService.selectUser(user).get(0);
			//设置session对象5分钟失效
			HttpSession session = req.getSession();
			session.setMaxInactiveInterval(30 * 24 * 60 * 60);
			String user_name= (String) tb_user.get("user_name");
			//将验证码保存在session对象中,key为validation_code
			session.setAttribute("userName", user_name);
			reslut.put("sessionflag", true);
			res.sendRedirect("http://7fanli.com/fanliht/dist/index.html");
	}
	/**
	 * 微信用户数据保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getWxLoginId.do")
	@ResponseBody
	public String  getWxLoginId(HttpServletRequest req ,HttpServletResponse res , String callback ) throws IOException{
		//将userName保存到redis 
		Jedis jedis = RedisUtil.getJedis();
		jedis.select(SysStat.loginid_redis_db);
		String loginId = UUID.randomUUID().toString().replace("-", "");
		jedis.hset(loginId, "status", SysStat.loginid_created);
		jedis.expire(loginId, 60*5);
		RedisUtil.returnResource(jedis);
		
		Map<String, Object> reslut = new HashMap<String, Object>();
		reslut.put("loginId", loginId);
    	
		String json = JSONObject.toJSONString(reslut); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 没登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("unLogin.do")
	@ResponseBody
	public String  unLogin(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		map.put("msg", "unLogin");
		map.put("flag", "false");
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 用户存在验证
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("logout.do")
	@ResponseBody
	public String  logout(HttpServletRequest req ,HttpServletResponse res,
			String callback,String token) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		boolean flag = false ;
		if(StringUtils.isNotBlank(token)){
			Jedis jedis = RedisUtil.getJedis();
			jedis.del(token);
			flag = true ;
			map.put("flag", flag);
			RedisUtil.returnResource(jedis);
		}else{
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 用户存在验证
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("userNameValid.do")
	@ResponseBody
	public String  userNameValid(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String userName = req.getParameter("userName");
		//根据userName查询用户个数
		List<Map<String, Object>> list=fanliService.selectUserByName(userName);
		int count =  list.size();
		map.put("count", count);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 获取短信验证码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("createRegistSMV.do")
	@ResponseBody
	public String createRegistSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_regist;
		Map<String , Object> returnMap = fanliService.sendMsg(tel, today, active);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 验证码确认接口
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("registSMV.do")
	@ResponseBody
	public String registSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_regist;
		//根据userName查询用户个数
		int count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
		map.put("count", count);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 获取短信验证码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("createUPWSMV.do")
	@ResponseBody
	public String createUPWSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_passwd;
		Map<String , Object> returnMap = fanliService.sendMsg(tel, today, active);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 验证码确认接口
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("UPWSMV.do")
	@ResponseBody
	public String UPWSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_passwd;
		//根据userName查询用户个数
		int count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
		map.put("count", count);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 获取短信验证码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("createUpAlipaySMV.do")
	@ResponseBody
	public String createUpAlipaySMV(HttpServletRequest req ,HttpServletResponse res,
			String callback , String token ) throws IOException{
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = "";
		if(StringUtils.isNotBlank(token)){
			Jedis jedis = RedisUtil.getJedis();
			String user_id = jedis.get(token);
			RedisUtil.returnResource(jedis);
			User user  = new User();
			user.setUser_id(user_id);
			List<Map<String , Object>> users = fanliService.selectUser(user);
			tel = (String) users.get(0).get("user_name");
		}else{
			tel = req.getParameter("tel");
		}
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_alipay;
		Map<String , Object> returnMap = fanliService.sendMsg(tel, today, active);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 验证码确认接口
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("upAlipaySMV.do")
	@ResponseBody
	public String upAlipaySMV(HttpServletRequest req ,HttpServletResponse res,
			String callback, String token ) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
//		public List<Map<String, Object>> userNameValid(HttpServletRequest req ,HttpServletResponse res) throws IOException{
		//获取页面userName
		String tel = "";
		if(StringUtils.isNotBlank(token)){
			Jedis jedis = RedisUtil.getJedis();
			String user_id = jedis.get(token);
			RedisUtil.returnResource(jedis);
			User user  = new User();
			user.setUser_id(user_id);
			List<Map<String , Object>> users = fanliService.selectUser(user);
			tel = (String) users.get(0).get("user_tel");
		}else{
			tel = req.getParameter("tel");
		}
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_alipay;
		//根据userName查询用户个数
		int count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
		map.put("count", count);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 新增用户
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("addUser.do")
	@ResponseBody
	public String addUser(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		// user.setUser_name(user.getUser_tel());
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String today = sdf1.format(new Date());
		// 获取当前active
		String active = SysStat.active_regist;
		//根据userName查询用户个数
		User param  = new User();
		// param.setUser_name(user.getUser_tel());
		List<Map<String , Object>> users = fanliService.selectUser(param);
		if(users.size()==0){
			int ident_count=fanliService.getIdentCodeByKey(user.getUser_tel(), today, active, identCode);
			if(ident_count==1){
				// 获取当前日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String time = sdf.format(new Date());
				user.setCreate_time(time);
				String passwd =  MD5Util.MD5(user.getPassword());
				user.setPassword(passwd);
				int user_count = fanliService.insertUser(user);
				map.put("count", user_count);
				map.put("msg", "success");
			}else{
				map.put("count", 0);
				map.put("msg", "error identCode");
			}
		}else{
			map.put("count", 0);
			map.put("msg", "userName exist!");
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	/**
	 * 更新密码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("updatePW.do")
	@ResponseBody
	public String updatePW(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String, Object> map = new HashMap<String, Object>();
		//获取页面userName
		String tel = req.getParameter("tel");
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_passwd;
		//根据userName查询用户个数
		int ident_count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
		if(ident_count==1){
			// 获取当前日期
			User user  = new User();
			String userName = tel;
			String passwd = req.getParameter("passwd");
			//user.setUser_name(userName);
			user.setPassword(MD5Util.MD5(passwd));
			int count =  fanliService.updatePW(user);
			map.put("count", count);
			map.put("msg", "success");
		}else{
			map.put("count", 0);
			map.put("msg", "error identCode");
		}
		
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 设置登陆状态
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("login.do")
	@ResponseBody
	public String login(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		
        user.setPassword(MD5Util.MD5(user.getPassword()));
        //设置session对象5分钟失效  TODO
		HttpSession session = req.getSession();
        session.setMaxInactiveInterval(30*24*60*60);
        List<Map<String, Object>> user_list = fanliService.selectUser(user);
        int count  = user_list.size();
        boolean flag = false;
        String msg = "";
        if(count==0){
        	msg = "no user";
        }else if (count==1){
        	Map<String, Object> user_map = user_list.get(0);
        	String platform = req.getParameter("platform");
        	if("wap".equals(platform)){
        		//将userName保存到redis 
        		Jedis jedis = RedisUtil.getJedis();
        		String wap_key =fanliService.getToken(user_map.get("user_id").toString());
            	jedis.set(wap_key,user_map.get("user_id").toString());
            	jedis.expire(wap_key, 60*60*24*30);
            	RedisUtil.returnResource(jedis);
            	map.put("wap_key", wap_key);
        	}else{
        		//将验证码保存在session对象中,key为validation_code
                //session.setAttribute("userName", user.getUser_name());
        	}
            flag = true;
            msg = "success";
        }else{
        	msg = "too many user";
        }
        map.put("flag", flag+"");
        map.put("msg", msg);
        String json = JSONObject.toJSONString(map); 
        if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 设置登陆状态
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("adminLogin.do")
	@ResponseBody
	public String adminLogin(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		
//		user.setPassword(MD5Util.MD5(user.getPassword()));
		List<Map<String, Object>> user_list = fanliService.selectAdminUser(user);
		int count  = user_list.size();
		boolean flag = false;
		String msg = "";
		if(count==0){
			msg = "its not admin";
		}else if (count==1){
			//将验证码保存在session对象中,key为validation_code
//			session.setAttribute("userName", user.getUser_name());
			//将sessionId保存到redis
			Jedis jedis = RedisUtil.getJedis();
			String OAJedisSessionId = "oa_"+UUID.randomUUID().toString().replace("-", "");
			//jedis.set(OAJedisSessionId, user.getUser_name());
			jedis.expire(OAJedisSessionId, 24*60*60);
			RedisUtil.returnResource(jedis);
			//将OAJedisSessionId 设置到cookie
            Cookie cookie = new Cookie("OAJedisSessionId", OAJedisSessionId);
			cookie.setMaxAge(60*30);
			cookie.setPath("/");
			res.addCookie(cookie );
			flag = true;
			msg = "success";
		}else{
			msg = "too many Admin_user";
		}
		map.put("flag", flag);
		map.put("msg", msg);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	/**
	 * 检查token值
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("checkToken.do")
	@ResponseBody
	public String checkToken(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		String wap_key = req.getParameter("wap_key");
		//将userName保存到redis 
		Jedis jedis = RedisUtil.getJedis();
		String user_id = jedis.get(wap_key);
		RedisUtil.returnResource(jedis);
		boolean flag = false;
		String msg = "";
		if(user_id==null){
			msg = "no token";
		}else{
			User user = new User();
			user.setUser_id(user_id);
			int count = fanliService.selectUserCount(user);
			if(count==0){
				msg = "no user";
			}else if (count==1){
				flag = true;
				msg = "success";
			}else{
				msg = "too many user";
			}
		}
		map.put("flag", flag);
		map.put("msg", msg);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 判断登陆状态
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("isLogined.do")
	@ResponseBody
	public String isLogined(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		boolean returnStr = true;
		//获得HttpSession对象
        HttpSession session = req.getSession();
        String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
        
		Map<String , Object> param = new HashMap<String, Object>();
        List<String> list = fanliService.getMembers(param);
        map.put("mid", list);
        if(StringUtils.isBlank(userName)){
        	returnStr = false;
        	 map.put("flag", returnStr);
     		String json = JSONObject.toJSONString(map); 
    		if(StringUtils.isNotBlank(callback))
    			return   callback + "(" +json+")";
    		else
    			return   json;
        }else{
        	map.put("flag", returnStr);
        	map.put("userName", userName);
        	String json = JSONObject.toJSONString(map); 
    		if(StringUtils.isNotBlank(callback))
    			return   callback + "(" +json+")";
    		else
    			return   json;
        }
       
	}

	
	/**
	 * 转发接口
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="python.do",produces="application/javascript;charset=GBK")
	@ResponseBody
	public String  python(HttpServletRequest req ,HttpServletResponse res,
			String interfaceName,
			String callback) throws IOException{
			HttpRequestUtils hu = new HttpRequestUtils();
			Map<String ,String > param = hu.getParamMap(req.getParameterMap());
			String paramStr = hu.getParamStr(param,true);
			List<Map<String, Object>> ips = fanliService.selectIps();
			int random_index = this.randomIndex(0,ips.size());
			String url = ips.get(random_index).get("ip_port")+"/"+interfaceName+"?"+paramStr;
			String json = hu.httpGet(url);
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
	}
	private  int randomIndex(int min ,int max) {
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
	}
	/**
	 * 用户信息列表
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="userList.do",produces="application/javascript;charset=utf-8")
	@ResponseBody
	public String  userList(HttpServletRequest req ,HttpServletResponse res,
			User user ,String pageNo, String pageSize,
			String callback) throws IOException{
		int pn = Integer.parseInt(pageNo);
		int ps = Integer.parseInt(pageSize);
		int down = ps*(pn-1);
		user.setOfset(down+"");
		user.setPageSize(pageSize);
		List<Map<String , Object>> users = fanliService.userList(user);
		String json = JSONObject.toJSONString(users); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 提现列表
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="tradeList.do",produces="application/javascript;charset=utf-8")
	@ResponseBody
	public String  tradeList(HttpServletRequest req ,HttpServletResponse res,
			String user_id , String time,String type,String pageNo,String pageSize,
			String callback) throws IOException{
		Map<String, Object> param =new HashMap<String, Object>();
		if(StringUtils.isNotBlank(time))
			param.put("time", time);
		if(StringUtils.isNotBlank(type))
			param.put("type", type);
		List<Map<String , Object>> trades = fanliService.tradeList(pageNo,pageSize,param);
		String json = JSONObject.toJSONString(trades); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 修改提现列表
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="upTradeType.do",produces="application/javascript;charset=utf-8")
	@ResponseBody
	public String  upTradeType(HttpServletRequest req ,HttpServletResponse res,
			PayTask payTask ,
			String callback) throws IOException{
		Map<String, Object> back= new HashMap<String, Object>();
//		if(StringUtils.isBlank(payTask.getCust_id())){
//			back.put("count", 0);
//			back.put("msg", "param cust_id is null");
//		}else if(StringUtils.isBlank(payTask.getType())){
//			back.put("count", 0);
//			back.put("msg", "param type is null ");
//		}else{
		if(StringUtils.isNotBlank(payTask.getReject())){
			String reject = URLDecoder.decode(payTask.getReject(), "UTF-8");
			payTask.setReject(reject);
			payTask.setType("5");//提现失败
		}
		int count = fanliService.upTradeType(payTask);
		back.put("count", count);
		back.put("msg", count);
//		}
		String json = JSONObject.toJSONString(back); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 入账明细
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="incomeDetail.do",produces="application/javascript;charset=utf-8")
	@ResponseBody
	public String  incomeDetail(HttpServletRequest req ,HttpServletResponse res,
			String user_id ,String task_id ,String pageNo, String pageSize,
			String callback,String lm) throws IOException{
		List<Map<String , Object>> incomes = fanliService.incomeDetail(pageNo, pageSize, user_id, task_id,lm);
		String json = JSONObject.toJSONString(incomes); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 提现明细
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="drawDetail.do",produces="application/javascript;charset=utf-8")
	@ResponseBody
	public String  drawDetail(HttpServletRequest req ,HttpServletResponse res,
			String user_id ,String pageNo, String pageSize,
			String callback) throws IOException{
		List<Map<String , Object>> incomes = fanliService.drawDetail( pageNo, pageSize,user_id);
		String json = JSONObject.toJSONString(incomes); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 无线查询用户
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="selectUser.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String selectUser(HttpServletRequest req ,HttpServletResponse res,String callback) {
		String context = req.getRealPath("/");
		String token = req.getParameter("token");
		Jedis jedis = RedisUtil.getJedis();
		String user_id = jedis.get(token);
		RedisUtil.returnResource(jedis);
		Map<String, Object> map = null;
		if(StringUtils.isNotBlank(user_id)){
			User user  = new User();
			user.setUser_id(user_id);
			List<Map<String , Object>> users = fanliService.selectUser(user);
			map = users.get(0);
			map.remove("password");
			Map<String, Object> aliInfo = fanliService.getAlipayInfo(user_id);
			if(aliInfo!=null){
				map.putAll(aliInfo);
			}
			Map<String, Object> amt_map = fanliService.getAmtInfo(user_id);
			if(amt_map==null){
				map.put("total_amt", 0.0);
				map.put("balance", 0.0);
				map.put("draw_money", 0.0);
			}else{
				map.putAll(amt_map);
			}
			double unused = fanliService.getUnused(user_id+"");
			map.put("unused", unused);
			String user_pic = (String) map.get("user_pic");

			if(user_pic==null){
				map.put("user_pic","null");
			}else if(user_pic.contains("http")){
				map.put("user_pic",user_pic);
			}else{
				try {
					if(!new File(context,user_pic).exists()){
						try {
							FileUtils.copyFile(new File(SysStat.pci_local_path, user_pic), new File(context+"/image/"+user_pic));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				map.put("user_pic",user_pic.contains("http")?user_pic: SysStat.web_path+user_pic);

			}
		}
		String json = JSONObject.toJSONString(map);
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
		
	}
	/**
	 * 无线查询用户
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="selectUserInfo.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String selectUserInfo(HttpServletRequest req ,HttpServletResponse res,
			String openid,String callback) {
		Map<String, Object> map = new HashMap<String, Object>();
		if(StringUtils.isNotBlank(openid)){
			User user  = new User();
			user.setWx_id(openid);
			List<Map<String , Object>> users = fanliService.selectUser(user);
			map  = users.get(0);
			map.remove("password");
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
		
	}
	/**
	 * 查询订单信息订单
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="getTradeList.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  getTradeList(HttpServletRequest req ,HttpServletResponse res,
			String pageNo,String pageSize,String callback,String user_id) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String ,Object> order_param = new HashMap<String, Object>();
		
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");

		if(StringUtils.isBlank(user_id)){
			String userName = req.getSession().getAttribute("userName").toString();
			if(StringUtils.isBlank(userName)){
				return "";
			}
			User user = new User();
			//user.setUser_name(userName);
			List<Map<String, Object>> list = fanliService.selectUser(user);
			if(list==null&&list.size()==0){
				return "";
			}

			user_id = list.get(0).get("user_id").toString();
		}
		String payStatus = req.getParameter("payStatus");

		if(StringUtils.isNotBlank(payStatus)){
			order_param.put("payStatus", payStatus);
		}
		String params = req.getParameter("param");
		if(StringUtils.isNotBlank(params)){
			params = new String(params.getBytes("ISO-8859-1"),"UTF-8");
			order_param.put("params", params);
		}
		int count = fanliService.getCountOfUserOrder(startDate,endDate,user_id+"");
		List<Map<String ,Object>>  user_order_list = fanliService.getUserOrders(pageNo,pageSize,startDate,endDate,user_id+"",order_param);
		returnMap.put("pageNo", pageNo);
		returnMap.put("pageSize", pageSize);
		double d = ((double)count/Double.parseDouble(pageSize));
		returnMap.put("pageSum", Math.ceil(d));
		returnMap.put("count", count);
		returnMap.put("list", user_order_list);
		BigDecimal sum = new BigDecimal(0) ;
		for (int i = 0; i < user_order_list.size(); i++) {
			Map<String ,Object> map = user_order_list.get(i);
			BigDecimal finalfee = (BigDecimal)map.get("finalfee");
//			if(finalfee==null) continue;
			sum = sum.add(finalfee);
		}
		returnMap.put("sum", sum);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	@RequestMapping(value="transferMoney.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String transferMoney(HttpServletRequest req ,HttpServletResponse res,String task_id ,String callback){
		//判断是否需要进行提现操作
		List<Map<String, Object>> task_type = fanliService.drawDetailById(task_id);
		//当type=3时执行以下提现操作
		Map<String, Object> task = task_type.get(0);
		String type =(String) task.get("type");
		String json = "";
		if ("3".equals(type)){
			String username ="";
			String alipay_username = "";
			try {
				username = new String(req.getParameter("username").getBytes("ISO-8859-1"),"UTF-8");
				alipay_username = new String(req.getParameter("alipay_username").getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			
			String money = req.getParameter("money");
			
			String app_id = "2017091808801998";
	        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC35FlEB7yCIeczAZcFYb6NzDWz9I2Hef1SecID+Nc3mmrwHRbv7+I6i0mdPh+LRiZJXM8Iuf+hkH4iVqwY0AL7LuDNPf5/xfNYfPpn+Z0gSK/0GXWh9z2ZTAbmYsbotAa0bIQx4xrHmcrGzU4SOpzX2jqqHdQN68FPbV61ceTJa1wyDYYRmuZXUE2jQLfBAQ9Ft94d+OU/KsJ1qxpBRha+OMB/B2QHKzB6PJ7jASAiH9bqcJdxdyguPURY5pcEEir/8a9a+C5elrYF184lIIAe2T1RsZwP2d5xWjSyUuNavLwWBpSQV3UWmx3Yj/9k1km8M6211OBcRXbxaoK5+uElAgMBAAECggEBAJjY412JdKVRmsMpmiZuBR4FU8ndBlpKCkYoUBxPFEvyPNqRw0Pxxr9UkP5y6XMw/pfR3X/qYdEfscfG9Mq28xNm9pGB6uy3UzoEv3n23yQ7ZozlMIJMZ9XofH+4MI6xPDVxUTvKAbNQYFx3v2GleEJt8H7/xgdAIvBq/uKf5UOIXdiJzbINtb+UK+dhEwN/xHFKsqLfDFtDcbl95VmjoDzovBLbzh4AGEKBg9LsND+p1xfDS/ENbKB2AtUjM4JU81UU+eunXZMDm+Hpfl9ayyvdN+5FRIyv24OkQ64Jtw8nbNzuXMLb+lHP9lcMo/CLodY/t8tdTChx33WXg6tAC2ECgYEA+cCuI0D+4J75zmGpQkx6X1Nh8A9UvsrFsVwV236GJGEz0nyuxU55tXLBRG1Y0Ocktk8Lsxj3rjVceNnFbUtQs1P+Rqw8y2goSeBDaCihECWjiynlMlaXELDZjthoOZFS3Ggme2cra9QUOhxEAAJZhpyYlWsPZERLhAH+yyYFDp0CgYEAvH3sD99rrcG6XsZdOXn7vWpTF3oBJ1w3ZqzAgpxXG/KIHR/TwicInAgSAsSl1jK5MFMCaLeN930uqSpeKkYUR7/9BQBnkY1sU7FEosarBzeCnDJd7UKkSTxVK6pg6w9O3rIAJDVfk1k3E3NFLupUoqszDjOijhbsa1mscXFOkikCgYAHBAugP+YpBy47RvELRLy3Ss9YgAXAak/NYKnYhaBdC0H6arg6IK84kqWtN2kkTSnx2RyaBlyGz0buuidan5//uZ9N+u6mRCHFmYArP+DuZDBI639dv6L0vBMQeTHMVDHAsUhLdSV6HPYIf9zFJ0u+hU2f/ObsySJZ7fhrWoEP3QKBgEk5FaIY5eirEG5O2tpAI+YyTrMZBye2MCNnyqUyfLhzoCLIQWfz5+lNTUncAJxUOhKmvJHXdIQHEkBPICOF88znrS/rN1CYwtNEUuh1Cu2Tx95Lxqcrs0xr7p424s2NtdLXDS0DuuwvxTB+IAsYpuZGYWAL+QL/rroJLO8o2B/5AoGACjiCaXyjpW8dilOHLzPGQmLr87x8gvDLp28W1kTCsMc2142/6CUJq36teS9nXf827rHOav883/c6qxVEFD2jRiRffYhSzQc74U8Nev7H0YxNvZFF0HfgEBidZYfDTdpkx+/A/gEXjmqZQhpuIBR4g4czyc0W9GZQzQg6pzNcA2Y=";
	        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA/C30yUaUX6WpS6i1fkUzM6YMzijEOQxrf3ig/3tlAD+DjzSXH/XGXlC+EKZu4v1fS7UFsc2YTwyjhH7h69+O512NQO+qrvdutN0dyZc031yBib0S9NSlSx0G+VKcuwZNfVKXZWC45L0rBs9LaiR1Pq3hkueiSzpP586fA5V8YnKdTufesuH7A6sY6TCbmCQY9VII5X5IEhc++KwdHJJwguSSktybuj4AfeOCZQOjKNXbFv/G/LffiZ30FFpgt6H7Q7iGN33h0IWRVdK+bizFam/dQcxp96QKtLKrAe+QWCtlFdLSzzMuprFIb1YR3+yJF+JtCkC/srvooFcC6eTopwIDAQAB";
	        //SDK调用前需要进行初始化
	        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do",app_id,APP_PRIVATE_KEY,"json","GBK",ALIPAY_PUBLIC_KEY,"RSA2");
	        
	        /**
	         * 请求参数:
	         * out_biz_no   商户转账唯一订单号  （不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。）
	         * payee_type   收款方账户类型      （ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式）
	         * payee_account   收款方账户       （使用沙箱商家账号测试：tgodfv6211@sandbox.com）
	         * amount        转账金额
	         * payer_show_name    付款方姓名 （非必填）
	         * payee_real_name    收款方姓名  （非必填）
	         * remark     备注 （非必填）
	         */
	        //使用时间戳作为 商户转账唯一订单号
	        Long timestamp = new Date().getTime();
	        String out_biz_no = Long.toString(timestamp);


	        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
			String param = "{" +
					"\"out_biz_no\":\""+out_biz_no+"\"," +
					"\"payee_type\":\"ALIPAY_LOGONID\"," +
					"\"payee_account\":\""+alipay_username+"\"," +
					"\"amount\":\""+money+"\"," +
					"\"payer_show_name\":\"7fanli\"," +
					"\"payee_real_name\":\""+username+"\"," +
					"\"remark\":\"7fanli转账\"" +
					"  }";
			System.out.println("t_m:"+param);
			logger.info("tm_start param:" + param);
			request.setBizContent(param);
	        AlipayFundTransToaccountTransferResponse response = null;
	        try {
	            response = alipayClient.execute(request);
				System.out.println("t_m:" + response.getSubMsg());
	        } catch (AlipayApiException e) {
	            e.printStackTrace();
	        }
			logger.info("tm_start response:" + response);
	        if(response.isSuccess()){
	            System.out.println("调用成功");
	            //接口调用成功时，更改交易记录表中的状态,调用接口
	            PayTask payTask = new PayTask();
	            payTask.setTask_id(task_id);
	    		payTask.setType("4");//提现 
	    		fanliService.upTradeType(payTask);  
	        } else {
	            System.out.println("调用失败");
	        }
	        json = JSONObject.toJSONString(response);
		}else {
			Map<String, Object> response = new HashMap<String, Object>();
			//type!=3时返回数据
			response.put("success", "false");
			response.put("subMsg", "非提现中的申请");
			json = JSONObject.toJSONString(response);
		}
		
        if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
}
