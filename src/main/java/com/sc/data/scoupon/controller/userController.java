package com.sc.data.scoupon.controller;

import java.io.File;
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

import org.apache.commons.io.FileUtils;
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
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.MD5Util;



@Controller
@RequestMapping(value = "/user")  
public class userController {
	@Autowired
	private FanliService fanliService;

	private static Logger logger = Logger.getLogger(userController.class);

	/**
	 * 查询订单信息订单
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="logout.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  logout(HttpServletRequest req ,HttpServletResponse res,
			String pageNo,String pageSize,String callback) throws IOException{
		//获得HttpSession对象
        HttpSession session = req.getSession();
		session.removeAttribute(SysStat.USER_SESSION_KEY);
		//int count = fanliService.getCountOfUserOrder(startDate,endDate,user_id+"");
		Map<String, Object> returnMap = new HashMap<String, Object>(); //返回结果
		returnMap.put("flag", true);
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
	public String  getTradeList(HttpServletRequest req ,HttpServletResponse res,
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
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userId = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		//int count = fanliService.getCountOfUserOrder(startDate,endDate,user_id+"");
		List<Map<String ,Object>>  user_order_list = fanliService.getUserOrders(pageNo, pageSize, startDate, endDate, userId, order_param);
		count = fanliService.getCountOfUserOrder(startDate, endDate, userId);
		returnMap.put("pageNo", pageNo);
		returnMap.put("pageSize", pageSize);
		double d = ((double)count/Double.parseDouble(pageSize));
		returnMap.put("pageSum", Math.ceil(d));
		returnMap.put("count", count);
		returnMap.put("list", user_order_list);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 登陆短信接口
	 */
	@RequestMapping("alipayInfoSMV.do")
	@ResponseBody
	public String  alipayInfoSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		//获取页面userName
		String tel = req.getParameter("tel");
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
	 * 支付宝信息保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="alipayInfoSave.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  alipayInfoSave(HttpServletRequest req ,HttpServletResponse res
			,AlipayInfo alipayInfo,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String identCode  = req.getParameter("identCode");
		// 获取当前日期
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		String today = sdf1.format(new Date());
		// 获取当前active
		String active = SysStat.active_update_alipay;
		User user = new User();
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		user.setUser_id(user_id);
		List<Map<String , Object>> users = fanliService.selectUser(user);
		int ident_count=fanliService.getIdentCodeByKey(users.get(0).get("user_tel").toString(), today, active, identCode);
		if(ident_count==1){
			// 获取当前日期
			String real_name="";
			String alipay_user_name="";
			try {
				real_name  = URLDecoder.decode(req.getParameter("real_name"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				alipay_user_name =new String(req.getParameter("alipay_user_name").getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			alipayInfo.setReal_name(real_name);
			alipayInfo.setAlipay_user_name(alipay_user_name);
//			alipayInfo.setReal_name(new String(alipayInfo.getReal_name().getBytes("ISO-8859-1"),"UTF-8"));
			alipayInfo.setUser_id(user_id);
			if(StringUtils.isBlank(alipayInfo.getRun_status())){
				alipayInfo.setRun_status(AlipayInfo.unused);
			}
			int count = fanliService.alipayInfoSave(alipayInfo);
			returnMap.put("count", count);
			returnMap.put("msg", "success");
		}else{
			returnMap.put("count", 0);
			returnMap.put("msg", "error identCode");
		}
		
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
			,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
        HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
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
	 * 提现任务保存
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="payTastSave.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  payTastSave(HttpServletRequest req ,HttpServletResponse res
			,PayTask payTask,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
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
     * 提现列表
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "tradeList.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String tradeList(HttpServletRequest req, HttpServletResponse res,
                            String pageNo, String pageSize, String time, String type,
                            String user_name, String callback) throws IOException {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(time))
            param.put("time", time);
        if (StringUtils.isNotBlank(type))
            param.put("type", type);
        //获得HttpSession对象
      	HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
        param.put("user_id", user_id);
        List<Map<String, Object>> trades = fanliService.tradeList(pageNo, pageSize, param);
        String json = JSONObject.toJSONString(trades);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
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
			,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
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
	public String selectUser(HttpServletRequest req ,HttpServletResponse res,String callback) {
		User user  = new User();
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
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
		double unused = fanliService.getUnused(user_id);
		map.put("unused", unused);
		Map<String, Object> adzone_info = fanliService.getAdzoneInfoByUserId(user_id);
		map.putAll(adzone_info);
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
		
	}
	/**
	 * 更新用户昵称
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="upUsernick.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  upUsernick(HttpServletRequest req ,HttpServletResponse res,
			String user_nick ,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		user_nick = URLDecoder.decode(user_nick, "UTF-8");

		//获得HttpSession对象
		HttpSession session = req.getSession();
		String user_id = session.getAttribute(SysStat.USER_SESSION_KEY)==null?"":(String) session.getAttribute(SysStat.USER_SESSION_KEY);
		User param = new User();
		param.setUser_id(user_id);
		//更新昵称
		if(StringUtils.isNotBlank(user_nick)){
			param.setUser_nick(user_nick);
			int nickCount = fanliService.upUsernick(param);
			returnMap.put("nickCount", nickCount);
		}
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	
}
