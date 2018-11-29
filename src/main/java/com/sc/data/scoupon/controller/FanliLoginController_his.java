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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;



@Controller
@RequestMapping(value = "/al")  
public class FanliLoginController_his {
	@Autowired  
	private FanliService fanliService;  
	
	/**
	 * test
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("test.do")
	@ResponseBody
	public String  test(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		return "test";
	}
	/**
	 * 分享关系查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="shareRelation.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  shareRelation(HttpServletRequest req ,HttpServletResponse res,
			String pageNo, String pageSize,String callback) throws IOException{
        HttpSession session = req.getSession();
        String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
        User param = new User();
       // param.setUser_name(userName);
		List<Map<String , Object>> users = fanliService.selectUser(param);
        String user_id = users.get(0).get("user_id")+"";
		//查询分享关系
        List<Map<String , Object>> shareRelations = fanliService.getshareRelationByUserId(pageNo, pageSize,user_id);
        String json = JSONObject.toJSONString(shareRelations); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 分享订单明细查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="shareOrders.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  shareOrders(HttpServletRequest req ,HttpServletResponse res,
			String pageNo, String pageSize,String callback) throws IOException{
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
		User param = new User();
		//param.setUser_name(userName);
//		param.setUser_name("18651000052");
		List<Map<String , Object>> users = fanliService.selectUser(param);
		String user_id = users.get(0).get("user_id")+"";
		//查询分享关系
		List<Map<String , Object>> shareOrders = fanliService.getshareOrdersByUserId(pageNo, pageSize,user_id);
		String json = JSONObject.toJSONString(shareOrders); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 分享订单明细查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="shareItems.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  shareItems(HttpServletRequest req ,HttpServletResponse res,
			String pageNo, String pageSize,String callback) throws IOException{
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
		User param = new User();
//		param.setUser_name(userName);
		List<Map<String , Object>> users = fanliService.selectUser(param);
		String user_id = users.get(0).get("user_id")+"";
		//查询分享关系
		List<Map<String , Object>> shareItems = fanliService.getshareItemsByUserId( pageNo, pageSize,user_id);
		String json = JSONObject.toJSONString(shareItems); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 是否绑定电话号码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="haveTel.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  haveTel(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
		
		User param  = new User();
//  		param.setUser_name(userName);
//  		param.setUser_name("421004797584343040");
	    List<Map<String , Object>> users = fanliService.selectUser(param);
	    int channel_id = (int) users.get(0).get("channel_id");
		boolean isTel = false ;
		if(channel_id==0){
			if(userName.length()==11){
				isTel = true ;
			}
		}else{
			if(userName.length()==11+("_"+channel_id).length()){
				isTel = true ;
			}
		}
		returnMap.put("flag", isTel);
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 绑定手机号验证码
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="bindTelWSMV.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  bindTelWSMV(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
				//获取页面userName
				String tel = req.getParameter("tel");
				// 获取当前日期
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String today = sdf.format(new Date());
				// 获取当前active
				String active = SysStat.active_bind_tel;
				HttpSession session = req.getSession();
				String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
				
				User param  = new User();
//		  		param.setUser_name(userName);
			    List<Map<String , Object>> users = fanliService.selectUser(param);
			    int channel_id = (int) users.get(0).get("channel_id");
			    if(channel_id!=0){
				    tel = tel + "_" + channel_id;
			    }
				Map<String , Object> returnMap = fanliService.sendMsg(tel, today, active);
				String json = JSONObject.toJSONString(returnMap); 
				if(StringUtils.isNotBlank(callback))
					return   callback + "(" +json+")";
				else
					return   json;
	}
	/**
	 * 为没有绑定手机号的用户绑定手机号
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="bindTel.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  bindTel(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		String tel = req.getParameter("tel");
//		tel="13469875252";
		String userName = tel ;
		String identCode = req.getParameter("identCode");
		HttpSession session = req.getSession();
	    String session_userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
//	    session_userName = "420159237340004352";
  		// 获取当前日期
  		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
  		String today = sdf1.format(new Date());
  		// 获取当前active
  		String active = SysStat.active_bind_tel;
	    //根据userName查询用户个数
  		User param  = new User();
//  		param.setUser_name(session_userName);
	    List<Map<String , Object>> users = fanliService.selectUser(param);
	    int channel_id = (int) users.get(0).get("channel_id"); 
		if(users.size()==1){
//	    if(true){
			int ident_count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
			if(ident_count==1){
//			if(true){
				String wx_id = (String) users.get(0).get("wx_id");
//				String wx_id = "qMcjEOl4bmX1xh3pDnn0";
				Map<String, Object> msg_map = null;
				try {
					msg_map = fanliService.bindWx(tel,userName,wx_id);
				} catch (Exception e) {
					e.printStackTrace();
					returnMap.put("bind_flag", false);
					returnMap.put("msg", "combine fialed : "+ e.getMessage() );
				}
				boolean bind_flag = (boolean) msg_map.get("bind_flag");
				if(bind_flag){
					//重新设置session
					session.removeAttribute("userName");
			        session.setMaxInactiveInterval(30*24*60*60);
			        if(channel_id!=0){
			        	userName = userName+"_"+channel_id;
			        }
					session.setAttribute("userName", userName);
				}
				returnMap.putAll(msg_map);
			}else{
				returnMap.put("bind_flag", false);
				returnMap.put("msg", "error identCode");
			}
		}else{
			returnMap.put("bind_flag", false);
			returnMap.put("msg", "no user exist!");
		}
	   
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 为没有绑定手机号的用户绑定手机号
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="bindTb.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  bindTb(HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
//		String tel = req.getParameter("tel");
//		String userName = tel ;
//		String identCode = req.getParameter("identCode");
//		HttpSession session = req.getSession();
//		String session_userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
//		// 获取当前日期
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//		String today = sdf1.format(new Date());
//		// 获取当前active
//		String active = SysStat.active_bind_tel;
//		//根据userName查询用户个数
//		User param  = new User();
//		param.setUser_name(session_userName);
//		List<Map<String , Object>> users = fanliService.selectUser(param);
//		if(users.size()==1){
////	    if(true){
//			int ident_count=fanliService.getIdentCodeByKey(tel, today, active, identCode);
//			if(ident_count==1){
////			if(true){
//				String wx_id = (String) users.get(0).get("wx_id");
////				String wx_id = "qMcjEOl4bmX1xh3pDnn0";
//				Map<String, Object> msg_map = null;
//				try {
//					msg_map = fanliService.bindWx(tel,userName,wx_id);
//				} catch (Exception e) {
//					e.printStackTrace();
//					returnMap.put("bind_flag", false);
//					returnMap.put("msg", "combine fialed : "+ e.getMessage() );
//				}
//				boolean bind_flag = (boolean) msg_map.get("bind_flag");
//				if(bind_flag){
//					//重新设置session
//					session.removeAttribute("userName");
//					session.setMaxInactiveInterval(30*24*60*60);
//					session.setAttribute("userName", userName);
//				}
//				returnMap.putAll(msg_map);
//			}else{
//				returnMap.put("bind_flag", false);
//				returnMap.put("msg", "error identCode");
//			}
//		}else{
//			returnMap.put("bind_flag", false);
//			returnMap.put("msg", "no user exist!");
//		}
		
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
	@RequestMapping(value="getAlipayInfo.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  getAlipayInfo(HttpServletRequest req ,HttpServletResponse res
			,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
        HttpSession session = req.getSession();
        String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
        User param = new User();
//        param.setUser_name(userName);
//      param.setUser_name("18006788230");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		if(list.size()==1){
			Map<String, Object> map = list.get(0);
			Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
			
			Map<String, Object> amt_map = fanliService.getAlipayInfo(user_id+"");
			if(amt_map!=null){
				returnMap.putAll(amt_map);
			}
			
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
			,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
//		param.setUser_name("18338319095");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		if(list.size()==1){
			Map<String, Object> map = list.get(0);
			Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
			
			Map<String, Object> amt_map = fanliService.getAmtInfo(user_id+"");
			List<Map<String, Object>> task_amt_list = fanliService.getTaskAmtInfo(user_id+"");
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
			returnMap.put("task_amt_list", task_amt_list);
		}
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
		// 获取当前日期
		Map<String, Object> returnMap = new HashMap<String, Object>();
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
//		alipayInfo.setReal_name(new String(alipayInfo.getReal_name().getBytes("ISO-8859-1"),"UTF-8"));
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
		//根据userName查询用户个数
		User param = new User();
//		param.setUser_name(userName);
//    	 param.setUser_name("18338319095");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		if(list.size()>1){
			returnMap.put("count", 0);
			returnMap.put("msg", "too many users:"+list.size());
			String json = JSONObject.toJSONString(returnMap); 
			return callback + "(" +json+")";
		}
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		alipayInfo.setUser_id(user_id+"");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		alipayInfo.setSave_time(time);
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
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
//      param.setUser_name("18338319095");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		if(list.size()>1){
			returnMap.put("count", 0);
			returnMap.put("msg", "too many users:"+list.size());
			String json = JSONObject.toJSONString(returnMap); 
			return callback + "(" +json+")";
		}
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		
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
		
		payTask.setUser_id(user_id+"");
		// 获取当前日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = sdf.format(new Date());
		payTask.setTime(time);
//		payTask.setHasSendMail(PayTask.preSend);
		Map<String, Object> amt_map = fanliService.getAmtInfo(user_id+"");
//		double draw_money =  (double) amt_map.get("draw_money");
		double draw_money = ((BigDecimal) amt_map.get("draw_money")).doubleValue();
//		draw_money = draw_money + payTask.getMoney();
//		double balance = (double) amt_map.get("balance");
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
	 * 订单同步
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="tradeList.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  tradeList(HttpServletRequest req ,HttpServletResponse res,
			String usernick ,String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
        HttpSession session = req.getSession();
        String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
        User param = new User();
//        param.setUser_name("18006788230");
//        param.setUser_name(userName);
		List<Map<String, Object>> list = fanliService.selectUser(param);
		if(list.size() > 1){
			returnMap.put("userName", userName);
			String json = JSONObject.toJSONString(returnMap); 
			return json;
		}
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		
		//更新昵称
		if(StringUtils.isNotBlank(usernick)){
			param.setUser_nick(usernick);
			int nickCount = fanliService.upUsernick(param);
			returnMap.put("nickCount", nickCount);
		}

		String tids = req.getParameter("tids");
		
		Map<String, Object> return_map= fanliService.inserUserOrder(user_id+"",tids);
		
		returnMap.putAll(return_map);
		
		String json = JSONObject.toJSONString(returnMap); 
		return json;
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
		order_param.put("payStatus", payStatus);
		String params = req.getParameter("param");
		if(StringUtils.isNotBlank(params)){
			params = new String(params.getBytes("ISO-8859-1"),"UTF-8");
			order_param.put("params", params);
		}
		//获得HttpSession对象
        HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
		//String userName="999999999";
		User param = new User();
//		param.setUser_name(userName);
//		param.setUser_name("13566568851");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		//int count = fanliService.getCountOfUserOrder(startDate,endDate,user_id+"");
		List<Map<String ,Object>>  user_order_list = fanliService.getUserOrders(pageNo, pageSize, startDate, endDate, user_id + "", order_param);
		count = fanliService.getCountOfUserOrder(startDate, endDate, user_id.toString());
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
	 * 明细查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="drawDetail.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  drawDetail(HttpServletRequest req ,HttpServletResponse res,
			String pageNo, String pageSize,
			String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
		List<Map<String, Object>> list = fanliService.selectUser(param);
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		List<Map<String, Object>> drawDetailList = fanliService.drawDetail( pageNo, pageSize,user_id+"");
		returnMap.put("drawDetailList", drawDetailList);
		returnMap.put("flag", true);
		returnMap.put("msg", "success");
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 未生效明细查询
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="unusedDetail.do",produces="application/javascript;charset=UTF-8")
	@ResponseBody
	public String  unusedDetail(HttpServletRequest req ,HttpServletResponse res,
			String pageNo, String pageSize,
			String callback) throws IOException{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
		List<Map<String, Object>> list = fanliService.selectUser(param);
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		List<Map<String, Object>> unusedDetailList = fanliService.unusedDetail( pageNo, pageSize,user_id+"");
		returnMap.put("unusedDetailList", unusedDetailList);
		returnMap.put("flag", true);
		returnMap.put("msg", "success");
		String json = JSONObject.toJSONString(returnMap); 
		if(StringUtils.isNotBlank(callback))
			return   callback + "(" +json+")";
		else
			return   json;
	}
	/**
	 * 更新用户图片
	 * @param req
	 * @param res
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value="uploadUserPic.do",produces="text/html;charset=UTF-8")
	@ResponseBody
	public String  uploadUserPic(@RequestParam(value = "picFile", required = true) MultipartFile file,
			HttpServletRequest req ,HttpServletResponse res,String callback) throws IOException{
		//获得HttpSession对象
		HttpSession session = req.getSession();
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
//				param.setUser_name("18338319095");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		
		String context = req.getRealPath("/");
		Map<String, Object> returnMap = new HashMap<String, Object>();
        String fileName = this.picName(file.getOriginalFilename(),user_id);

        File targetFile = new File(SysStat.pci_local_path, fileName);  
        if(!targetFile.exists()){  
            targetFile.mkdirs();  
        }else{
        	targetFile.deleteOnExit();
        }
        //保存  
        try {  
            file.transferTo(targetFile);  
            FileUtils.copyFile(new File(SysStat.pci_local_path, fileName), new File(context+"/image/"+fileName));
        } catch (Exception e) {  
            e.printStackTrace();  
        }
        
		
		param.setUser_pic(fileName);
		param.setUser_id(user_id+"");
		int count = fanliService.uploadUserPic(param);
		returnMap.put("count", count);
		String json = JSONObject.toJSONString(returnMap); 
		String URL = req.getRequestURL().toString();  
		if(URL.startsWith("https:"))  
		{  
			return "<script>localStorage.removeItem('user_pic');window.location.href='https://7fanli.com/fanliht/pc/https/index.html#/userCenter?time="+new Date().getTime()+"'</script>";
		}  else{
			return "<script>localStorage.removeItem('user_pic');window.location.href='http://7fanli.com/fanliht/pc/dist/index.html#/userCenter?time="+new Date().getTime()+"'</script>";
		}
		
	}
	/**
	 * 更新用户图片
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
		String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		User param = new User();
//		param.setUser_name(userName);
//		param.setUser_name("18338319095");
		List<Map<String, Object>> list = fanliService.selectUser(param);
		Map<String, Object> map = list.get(0);
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		param.setUser_id(user_id+"");
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
		String context = req.getRealPath("/");
		User user  = new User();
		String userName = req.getParameter("userName");
		if(StringUtils.isBlank(userName)){
			//获得HttpSession对象
			HttpSession session = req.getSession();
			userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName"); 
		}
//		user.setUser_name(userName);
		List<Map<String , Object>> users = fanliService.selectUser(user);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("userName", userName);
		Map<String, Object> map = users.get(0);
		map.remove("password");
		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
		Map<String, Object> aliInfo = fanliService.getAlipayInfo(user_id+"");
		if(aliInfo!=null){
			map.putAll(aliInfo);
		}
		Map<String, Object> amt_map = fanliService.getAmtInfo(user_id+"");
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
		String json = JSONObject.toJSONString(map); 
		if(StringUtils.isNotBlank(callback)){
			return callback + "(" +json+")";
		}else{
			return json;
		}
		
	}
	private String picName(String originalFilename, Long user_id) {
		String[] strs = originalFilename.split("\\.");
		Long time = new Date().getTime();
		String result = user_id+"-"+time+"."+strs[1];
		return result;
	}
}
