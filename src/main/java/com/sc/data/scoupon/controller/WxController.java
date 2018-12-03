package com.sc.data.scoupon.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.service.ShareService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.PatternUtils;
import com.sc.data.scoupon.utils.TbOpenApi;
import com.sc.data.scoupon.utils.WxUtils;



@Controller
@RequestMapping(value = "/wx")  
public class WxController {
	@Autowired
	private FanliService fanliService;
	
	@Autowired
    private ShareService shareService;

//	private static Logger logger = Logger.getLogger(WxController.class);

	@RequestMapping("test.do")
	@ResponseBody
	public String  test(HttpServletRequest req ,HttpServletResponse res) throws IOException{

			Map<String, Object> param = new HashMap<String, Object>();
			List<String> list = fanliService.getMembers(param);
			String URL = req.getRequestURL().toString();
			if(URL.startsWith("https:"))
			{
				list.add("https");
			}  else{
			list.add("http");
			}
		return  JSONObject.toJSONString(list+"--");
	}
	@RequestMapping("getImgUrl.do")
	@ResponseBody
	public String  getImgUrl(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		String desShow_key = "1";
		Map<String, Object> map = fanliService.getStaticByKey(desShow_key);
		if(map.size()>0){
			boolean flag = Boolean.valueOf((String) map.get("flag"));
			map.put("flag", flag);
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	@RequestMapping("getValByKey.do")
	@ResponseBody
	public String  getValByKey(HttpServletRequest req ,HttpServletResponse res
			,String key,String callback) throws IOException{
		Map<String, Object> map = fanliService.getStaticByKey(key);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="searchByTaotoken.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  searchByTaotoken(HttpServletRequest req ,HttpServletResponse res,String taotoken,String token,String callback) throws IOException{
		Map<String , Object> map = new HashMap<String, Object>();
		Map<String , Object> userInfo = fanliService.getUserInfoByToken(token);
		String userId =  userInfo.get("user_id").toString();
		fanliService.setAdzoneId(userId);
//		String userId = "1";
		TbOpenApi t = new TbOpenApi();
		Map<String , Object> item = t.taoTokenExtract(taotoken);
		if(item==null || item.get("item_id")==null){
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		if(item.get("price")==null){
			String priceStr = new PatternUtils().rexString("price=[\\d+\\.]*", item.get("url").toString()).split("=")[1];
			item.put("price", Double.valueOf(priceStr));
		}
		map.putAll(item);
		String itemId = (String) item.get("item_id");
		String pic_url = (String) item.get("pic_url");
		String title = (String) item.get("content");
		String sss = fanliService.heimataokeSearch(userId, itemId);
		if(StringUtils.isBlank(sss)){
			map.put("fanli", false);
			String json = JSONObject.toJSONString(map); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}else {
			map.put("fanli", true);
		}
		Conver c = new Conver();
		map.putAll(c.converMap(sss));
		//设置淘口令 couponLinkTaoToken taoToken
		String coupon_click_url = map.get("coupon_click_url")==null?null:(String) map.get("coupon_click_url");
		String item_url = map.get("item_url")==null?null:(String) map.get("item_url");
		String coupon_info = map.get("coupon_info")==null?null:(String) map.get("coupon_info");
		if(StringUtils.isNotBlank(coupon_info)&&StringUtils.isNotBlank(coupon_click_url)){
			String couponLinkTaoToken = t.taoToken(coupon_click_url, coupon_info,pic_url);
			map.put("couponLinkTaoToken", couponLinkTaoToken);
		}
		if(StringUtils.isNotBlank(item_url)){
			String taoToken = t.taoToken(item_url, title,pic_url);
			map.put("taoToken", taoToken);
		}
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	/**
	 * 小程序登入
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="xcxLogin.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String xcxLogin(HttpServletRequest req ,HttpServletResponse res,String code,
			String token,String wx,String fuid,String nickName,String avatarUrl,String callback) {
		Map<String, Object> return_map = new HashMap<String, Object>();
		Map<String, Object> token_info = fanliService.getTokenInfo(token,null);
		if(token_info!=null && token_info.size() > 0){
			//如果token有效
			return_map.put("token", token);
		}else{
			//token无效，获取
			String appid = "wxe0800622f961422c";
			String appSecret = "66e228710b4ac78f00e0a75b3e27ee27";
			if(StringUtils.isNotBlank(wx)&&wx.equals("1")){
				appid = SysStat.xiaoshuili_wx_appId1;
				appSecret = SysStat.xiaoshuili_wx_appSecret1;
			} else if (StringUtils.isNotBlank(wx)&&wx.equals("2")){
				appid = SysStat.xiaoshuili_wx_appId;
				appSecret = SysStat.xiaoshuili_wx_appSecret;
			}
			//{"session_key":"fU0fFA1yKukNLNu\/zZu1Og==","openid":"oVhVc5eT19_7V61v8Kgn2UspMNIU"}
			Map<String, Object> wx_res = new WxUtils().getWxUserIdByJscode(appid , appSecret, code);
//			Map<String, Object> wx_res = new HashMap<String, Object>();
//			wx_res.put("session_key", "session_key" + new Date().getTime());
//			wx_res.put("openid", "openid" + new Date().getTime());
			
			String session_key = (String) wx_res.get("session_key");
			String openid = (String) wx_res.get("openid");
			wx_res.put("avatarUrl", avatarUrl);
			wx_res.put("nickName", nickName);
			fanliService.apendWxInfo(wx_res,fuid);
			String new_token = fanliService.createToken(openid,session_key);
			return_map.put("token", new_token);
		}
		String json = JSONObject.toJSONString(return_map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
	}
	/**
	 * 更新用户名称和pic
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="upUserInfo.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String upUserInfo(HttpServletRequest req ,HttpServletResponse res,String userPic,
			String userNick,String token,String callback) {
		Map<String, Object> return_map = new HashMap<String, Object>();
		int count = fanliService.upUserPicAndNickByToken(userPic,userNick,token);
		return_map.put("count", count);
		String json = JSONObject.toJSONString(return_map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
	}
	
	/**
	 * 支付宝信息保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="alipayInfoSave.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  alipayInfoSave(HttpServletRequest req ,HttpServletResponse res
			,AlipayInfo alipayInfo,String token,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String user_id = fanliService.getUserIdByToken(token);
		String real_name="";
		String alipay_user_name="";
		if(StringUtils.isNotBlank(req.getParameter("real_name")))
			real_name  = URLDecoder.decode(req.getParameter("real_name"), "UTF-8");
		if(StringUtils.isNotBlank(req.getParameter("alipay_user_name")))
			alipay_user_name =new String(req.getParameter("alipay_user_name").getBytes("ISO-8859-1"),"UTF-8");
		
		alipayInfo.setReal_name(real_name);
		alipayInfo.setAlipay_user_name(alipay_user_name);
		alipayInfo.setUser_id(user_id);
		
		if(StringUtils.isBlank(alipayInfo.getRun_status())){
			alipayInfo.setRun_status(AlipayInfo.unused);
		}
		int count = fanliService.alipayInfoSave(alipayInfo);
		returnMap.put("count", count);
		returnMap.put("msg", "success");
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	/**
	 * 用户支付宝信息查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="getAlipayInfo.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  getAlipayInfo(HttpServletRequest req ,HttpServletResponse res
			,String token,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得userid
		String user_id = fanliService.getUserIdByToken(token);
		
		Map<String, Object> amt_map = fanliService.getAlipayInfo(user_id);
		if(amt_map!=null){
			returnMap.putAll(amt_map);
		}
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 用户佣金查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="getAmtInfo.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  getAmtInfo(HttpServletRequest req ,HttpServletResponse res
			,String token,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得userid
		String user_id = fanliService.getUserIdByToken(token);

		Map<String, Object> amt_map = fanliService.getAmtInfo(user_id);
		List<Map<String, Object>> task_amt_list = fanliService.getTaskAmtInfo(user_id);
		if(amt_map==null){
			returnMap.put("user_id", user_id);
			returnMap.put("total_amt", 0.0);
			returnMap.put("balance", 0.0);
			returnMap.put("draw_money", 0.0);
			returnMap.put("create_time", "");
		}else{
			returnMap.putAll(amt_map);
		}
		if(task_amt_list.get(0)==null){
			returnMap.put("total_task_money", 0.0);
		}else{
			returnMap.put("total_task_money", task_amt_list.get(0).get("total_task_money"));
		}
		String json = JSONObject.toJSONString(returnMap); 
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
	public String selectUser(HttpServletRequest req ,HttpServletResponse res
			,String token,String callback) {
		User user  = new User();
		//获得userid
		String user_id = fanliService.getUserIdByToken(token);
		user.setUser_id(user_id);
		List<Map<String , Object>> users = fanliService.selectUser(user);
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
//		double unused = fanliService.getUnused(user_id);
//		map.put("unused", unused);
		Map<String, Object> adzone_info = fanliService.getAdzoneInfoByUserId(user_id);
		map.putAll(adzone_info);
		Map<String, Object> credit_info  = shareService.getUserCredit(user_id);
		map.putAll(credit_info);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
	}
	  /**
     * 提现列表
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "tradeList.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String tradeList(HttpServletRequest req, HttpServletResponse res,String token,
                            String pageNo, String pageSize, String time, String type,
                             String callback) throws IOException {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(time))
            param.put("time", time);
        if (StringUtils.isNotBlank(type))
            param.put("type", type);
        //获得userid
  		String user_id = fanliService.getUserIdByToken(token);
  		
        param.put("user_id", user_id);
        List<Map<String, Object>> trades = fanliService.tradeList(pageNo, pageSize, param);
        String json = JSONObject.toJSONString(trades);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }
    
    /**
	 * 提现任务保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="payTastSave.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  payTastSave(HttpServletRequest req ,HttpServletResponse res
			,String token,PayTask payTask,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		 //获得userid
  		String user_id = fanliService.getUserIdByToken(token);
  		
		Map<String, Object> alipay_map = fanliService.getAlipayInfo(user_id+"");
		String real_name = (String) alipay_map.get("real_name");
		if(StringUtils.isBlank(real_name)){
			returnMap.put("count", 0);
			returnMap.put("msg", "为了方便验证,真实姓名不能为空，请在右上角的个人中心处更新 ");
			String json = JSONObject.toJSONString(returnMap); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		
		payTask.setUser_id(user_id);
		Map<String, Object> amt_map = fanliService.getAmtInfo(user_id);
		double draw_money = ((BigDecimal) amt_map.get("draw_money")).doubleValue();
		double balance = ((BigDecimal) amt_map.get("balance")).doubleValue();
		if(balance<payTask.getMoney()){
			returnMap.put("count", 0);
			returnMap.put("msg", "task_money too much");
			String json = JSONObject.toJSONString(returnMap); 
			if(StringUtils.isNotBlank(callback))
				return   callback + "(" +json+")";
			else
				return   json;
		}
		int count = 0;
		try {
			payTask.setType("3");//提现中
			count = fanliService.drawTastSave(payTask,balance ,draw_money);
		} catch (Exception e) {
			e.printStackTrace();
		}
		returnMap.put("count", count);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
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
	public String  getTradeList(HttpServletRequest req ,HttpServletResponse res,String token,
			String pageNo,String pageSize,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
		Map<String ,Object> order_param = new HashMap<String, Object>();            //查询条件
		int count =0;												   //订单数量
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
		
		String payStatus = req.getParameter("payStatus");
		if(StringUtils.isNotBlank(payStatus) && !payStatus.equals("null")  )
			order_param.put("payStatus", payStatus);
		String params = req.getParameter("param");
		if(StringUtils.isNotBlank(params)){
			params = new String(params.getBytes("ISO-8859-1"),"UTF-8");
			order_param.put("params", params);
		}
		 //获得userid
  		String user_id = fanliService.getUserIdByToken(token);
  		
		List<Map<String ,Object>>  user_order_list = fanliService.getUserOrders(pageNo, pageSize, startDate, endDate, user_id, order_param);
		returnMap.put("pageNo", pageNo);
		returnMap.put("pageSize", pageSize);
		returnMap.put("list", user_order_list);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * formid保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="saveFormids.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  saveFormids(HttpServletRequest req ,HttpServletResponse res,String token,
			String formids,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
		//准备formid
		if(StringUtils.isNotBlank(formids)){
			String[] formid_arr = formids.split(",");
			if(formid_arr.length>0){
				//获得userid
		  		String user_id = fanliService.getUserIdByToken(token);
		  		int count = fanliService.saveFormids(user_id,formid_arr);
			}
		}
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
	/**
	 * 自用户和订单数查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="childUserAndOrderCount.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  childUserAndOrderCount(HttpServletRequest req ,HttpServletResponse res,String token,
			String pageNo,String pageSize,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
  		String user_id = fanliService.getUserIdByToken(token);
  		List<Map<String ,Object>>  list = shareService.childUserAndOrderCount(pageNo, pageSize,user_id);
		returnMap.put("pageNo", pageNo);
		returnMap.put("pageSize", pageSize);
		returnMap.put("list", list);

		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 自用户和订单数查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="creditDetail.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  creditDetail(HttpServletRequest req ,HttpServletResponse res,String token,
			String callback) throws IOException{
  		String user_id = fanliService.getUserIdByToken(token);
  		Map<String ,Object>  map = shareService.creditDetail(user_id);

		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 子用户订单积分查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="childUserOrders.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  childUserOrders(HttpServletRequest req ,HttpServletResponse res,String token,
			String pageNo,String pageSize,String child_id,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
//  		String user_id = fanliService.getUserIdByToken(token);
  		
		Map<String ,Object> order_param = new HashMap<String, Object>();            //查询条件
		String payStatus = req.getParameter("payStatus");
		if(StringUtils.isNotBlank(payStatus) && !payStatus.equals("null")  )
			order_param.put("payStatus", payStatus);
		String startDate = req.getParameter("startDate");
		String endDate = req.getParameter("endDate");
  		List<Map<String ,Object>>  list = shareService.childUserOrders(pageNo, pageSize, child_id,startDate, endDate,order_param);
		returnMap.put("pageNo", pageNo);
		returnMap.put("pageSize", pageSize);
		returnMap.put("list", list);

		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 积分转入余额
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="creditToBalance.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  creditToBalance(HttpServletRequest req ,HttpServletResponse res,String token,
			String callback) throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
		String user_id = fanliService.getUserIdByToken(token);
		//查出积分
		Map<String, Object> credit_info = shareService.getUserCredit(user_id);
		String credit = credit_info.get("credit")==null?"0":credit_info.get("credit").toString();
		int count = shareService.creditToBalance(user_id,credit);
		returnMap.put("count", count);
		
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
}
