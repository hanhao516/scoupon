package com.sc.data.scoupon.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONObject;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.qr.QrcodeUtils;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.task.AlmmCookieKeepTask;
import com.sc.data.scoupon.task.AlmmOrderDownTask;
import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.HeimaUtils;
import com.sc.data.scoupon.utils.MD5Util;
import com.sc.data.scoupon.utils.RedisUtil;
import com.sc.data.scoupon.utils.TbOpenApi;
import com.sc.data.scoupon.utils.WXQR;
import com.sc.data.scoupon.utils.WxUtils;



@Controller
@RequestMapping(value = "/gg")  
public class GGController {
	@Autowired
	private FanliService fanliService;

	private static Logger logger = Logger.getLogger(GGController.class);

	/**
	 * test
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("urlQr.do")
	@ResponseBody
	public String  urlQr(HttpServletRequest req ,HttpServletResponse res,String url,String callback) throws IOException{
		System.out.println(url);
		String path=req.getServletContext().getRealPath("/");
		//将url转换成二维码
		String fileName = UUID.randomUUID().toString().replace("-", "")+".jpg";
		String filePath = path+"/image/qr/"+fileName;
		String logoName =  "image/logo.jpg";

		try {
			QrcodeUtils.gen(url, 
					filePath, 
					path+"/"+logoName, 
					430, 
					430);
		} catch (Exception e) {
			e.printStackTrace();
		};
		//2dcf51a043084ddda8e820dee71e5c1f.jpg
		Map<String , Object> map = new HashMap<String, Object>();
		map.put("qrUrl", "http://xiaoshuili.xyz:8080/scoupon/image/qr/" + fileName );
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	@RequestMapping("test.do")
	@ResponseBody
	public void  test(HttpServletRequest req ,HttpServletResponse res) throws IOException{
//		AlmmCookieKeepTask ackt = new AlmmCookieKeepTask();
		AlmmOrderDownTask ackt = new AlmmOrderDownTask();
		try {
			ackt.TaskJob();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * https://pub.alimama.com/items/search.json?q=https://detail.tmall.com/item.htm?id=537198102252
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("search.do")
	@ResponseBody
	public String  search(HttpServletRequest req ,HttpServletResponse res,String itemId , String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();

		if(StringUtils.isBlank(itemId)){
			map.put("msg", "itemId can not be null");
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		//获得HttpSession对象
        HttpSession session = req.getSession();
		String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?null:(String) session.getAttribute(SysStat.USER_SESSION_KEY);

		Map<String, Object> cookieMap = fanliService.getCookie(userId);
		String cookieStr = (String) cookieMap.get("login_cookie");
		WXQR wxqr = new WXQR();
        String url = "https://pub.alimama.com/items/search.json?q=https://detail.tmall.com/item.htm?id=" + itemId;
		String s = wxqr.httpGet(url , new HashMap<String, String>(), cookieStr);
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +s+")";
		else
			return   s;
	}
	/**
	 * https://pub.alimama.com/common/code/getAuctionCode.json?auctionid=537198102252&siteid=36092971&adzoneid=128624451&t=1500610061916&scenes=1&_input_charset=utf-8
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("getAuctionCode.do")
	@ResponseBody
	public String  getAuctionCode(HttpServletRequest req ,HttpServletResponse res,String itemId,String siteId,String adzoneId,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		
		//获得HttpSession对象
        HttpSession session = req.getSession();
		String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?null:(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		if(StringUtils.isBlank(userId)){
			map.put("msg", "must be logined");
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}

		if(StringUtils.isBlank(itemId) || StringUtils.isBlank(siteId) || StringUtils.isBlank(adzoneId)){
			map.put("msg", "itemId,siteId,adzoneId can not be null");
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		Map<String, Object> cookieMap = fanliService.getCookie(userId);
		String cookieStr = (String) cookieMap.get("login_cookie");
		WXQR wxqr = new WXQR();
        String url = "https://pub.alimama.com/common/code/getAuctionCode.json?auctionid="+itemId+"&siteid="+siteId+"&adzoneid="+adzoneId+"&scenes=1&_input_charset=utf-8";
		String s = wxqr.httpGet(url , new HashMap<String, String>(), cookieStr);
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +s+")";
		else
			return   s;
	}
	/**
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("heimaSearch.do")
	@ResponseBody
	public String  heimaSearch(HttpServletRequest req ,HttpServletResponse res,String itemId,String pid,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?null:(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		if(StringUtils.isBlank(userId) || StringUtils.isBlank(pid)){
			map.put("msg", "pid can not be null or  login ");
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		if(StringUtils.isBlank(itemId)){
			map.put("msg", "itemId can not be null");
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		String sss = fanliService.heimataokeSearch(userId, itemId);
		Conver c = new Conver();
		map.putAll(c.converMap(sss));
		TbOpenApi t = new TbOpenApi();
		Map<String , Object> itemInfo= t.itemInfo(itemId);
		String pict_url = (String) itemInfo.get("pict_url");
		String title = (String) itemInfo.get("title");
		//设置淘口令 couponLinkTaoToken taoToken
		String coupon_click_url = map.get("coupon_click_url")==null?null:(String) map.get("coupon_click_url");
		String item_url = map.get("item_url")==null?null:(String) map.get("item_url");
		String coupon_info = map.get("coupon_info")==null?null:(String) map.get("coupon_info");
		if(StringUtils.isNotBlank(coupon_info)&&StringUtils.isNotBlank(coupon_click_url)){
			String couponLinkTaoToken = t.taoToken(coupon_click_url, coupon_info,pict_url);
			map.put("couponLinkTaoToken", couponLinkTaoToken);
		}
		if(StringUtils.isNotBlank(item_url)){
			String taoToken = t.taoToken(item_url, title,pict_url);
			map.put("taoToken", taoToken);
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	
	@RequestMapping("getCookie.do")
	@ResponseBody
	public String  getCookie(HttpServletRequest req ,HttpServletResponse res,String item_id,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		String siteid = "36092971";
		String adzoneid = "128624451";
		
		//获得HttpSession对象
        HttpSession session = req.getSession();
		String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?null:(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		if(userId != null){
			Map<String , Object> adzone_info = fanliService.getAdzoneInfoByUserId(userId);
			if(adzone_info.size()!=0){
				siteid = adzone_info.get("site_id")+"";
				adzoneid = adzone_info.get("adzone_id")+"";
			}
		}
		Map<String , Object> cookieMap = fanliService.getCookie(userId);
		if (cookieMap!=null&&cookieMap.size()>0) {
			String cookieStr = (String) cookieMap.get("login_cookie");
			map.put("cookie", cookieStr);
			map.put("siteid", siteid);
			map.put("adzoneid", adzoneid);
			map.put("heima", false);
		}else {
			// 通过黑马获取返利信息 每个月更新授权
			String sss = fanliService.heimataokeSearch(userId, item_id);
			Conver c = new Conver();
			map.putAll(c.converMap(sss));
			map.put("heima", true);
			//设置淘口令 couponLinkTaoToken taoToken
			TbOpenApi t = new TbOpenApi();
			Map<String , Object> itemInfo= t.itemInfo(item_id);
			String pict_url = (String) itemInfo.get("pict_url");
			String title = (String) itemInfo.get("title");
			String coupon_click_url = map.get("coupon_click_url")==null?null:(String) map.get("coupon_click_url");
			String item_url = map.get("item_url")==null?null:(String) map.get("item_url");
			String coupon_info = map.get("coupon_info")==null?null:(String) map.get("coupon_info");
			if(StringUtils.isNotBlank(coupon_info)&&StringUtils.isNotBlank(coupon_click_url)){
				String couponLinkTaoToken = t.taoToken(coupon_click_url, coupon_info,pict_url);
				map.put("couponLinkTaoToken", couponLinkTaoToken);
			}
			if(StringUtils.isNotBlank(item_url)){
				String taoToken = t.taoToken(item_url, title,pict_url);
				map.put("taoToken", taoToken);
			}
		}
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
		//获得HttpSession对象
        HttpSession session = req.getSession();
        String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
    	if(StringUtils.isBlank(userId)){
    		map.put("flag", false);
        }else{
        	map.put("flag", true);
        }
    	map.put("userId", userId);
    	String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
       
	}
	/**
	 * 注册短信接口
	 */
	@RequestMapping("registSMV.do")
	@ResponseBody
	public String  registSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
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
	 * 登陆短信接口
	 */
	@RequestMapping("loginSMV.do")
	@ResponseBody
	public String  loginSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today = sdf.format(new Date());
		// 获取当前active
		String active = SysStat.active_login;
		Map<String , Object> returnMap = fanliService.sendMsg(tel, today, active);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 改密码短信接口
	 */
	@RequestMapping("upPasswdSMV.do")
	@ResponseBody
	public String  upPasswdSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
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
		//获取页面userName
		String identCode = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String today = sdf1.format(new Date());
		// 获取当前active
		String active = SysStat.active_regist;
		//根据userName查询用户个数
		User param  = new User();
		param.setUser_tel(user.getUser_tel());
		List<Map<String , Object>> users = fanliService.selectUser(param);
		if(users.size()==0){
			int ident_count=fanliService.getIdentCodeByKey(user.getUser_tel(), today, active, identCode);
			if(ident_count==1){
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
			map.put("count", users.size());
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
		String tel = req.getParameter("user_tel");
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
			user.setUser_tel(tel);
			String passwd = req.getParameter("password");
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
	 * 密码登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("passwdLogin.do")
	@ResponseBody
	public String passwdLogin(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		
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
                session.setAttribute(SysStat.USER_SESSION_KEY, user_map.get("user_id").toString());
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
	 * 验证码登陆
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("SMVLogin.do")
	@ResponseBody
	public String SMVLogin(HttpServletRequest req ,HttpServletResponse res,User user,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		String identCode  = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String today = sdf1.format(new Date());
		// 获取当前active
		String active = SysStat.active_login;
		int ident_count=fanliService.getIdentCodeByKey(user.getUser_tel(), today, active, identCode);
		//设置session对象5分钟失效  TODO
		HttpSession session = req.getSession();
		session.setMaxInactiveInterval(30*24*60*60);
		List<Map<String, Object>> user_list = fanliService.selectUser(user);
		int count  = user_list.size();
		boolean flag = false;
		String msg = "";
		if(count==0){
			msg = "no user";
		}else if (count==1&&ident_count==1){
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
				session.setAttribute(SysStat.USER_SESSION_KEY, user_map.get("user_id").toString());
			}
			flag = true;
			msg = "success";
		}else if (count==1&&ident_count==0){
			msg = "invalid identCode";
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
	 * 查询用户
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="selectUser.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String selectUser(HttpServletRequest req ,HttpServletResponse res,String callback) {
		Map<String, Object> return_map = new HashMap<String, Object>();
		User user  = new User();
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		user.setUser_id(user_id);
		List<Map<String , Object>> users = fanliService.selectUser(user);
		if(users.size()==1){
			Map<String, Object> map = users.get(0);
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
			double unused = fanliService.getUnused(user_id);
			map.put("unused", unused);
			Map<String, Object> adzone_info = fanliService.getAdzoneInfoByUserId(user_id);
			map.putAll(adzone_info);
			return_map.putAll(map);
			return_map.put("login_flag", true);
		}else {
			return_map.put("login_flag", false);
		}
		String json = JSONObject.toJSONString(return_map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
		
	}
	
}
