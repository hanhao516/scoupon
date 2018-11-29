package com.sc.data.scoupon; 
 import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 @Transactional 
 public class sendMsgTest { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void sendMsgTest() { 
//    	 Map<String, Object> map = new HashMap<String, Object>();
//    	 String tel = "17681806561_1001";
//		String today = "20180305";
//		String active = SysStat.active_update_alipay;
//		Map<String, Object> res = service.sendMsg(tel, today, active);
//    	 System.out.println(res);
     } 
//     @Test 
//     public void sendMsgTest2() { 
//    	 Map<String, Object> map = new HashMap<String, Object>();
//    	 String tel = "13416420236";
//    	 String today = "20180305";
//    	 String active = SysStat.active_update_alipay;
//    	 Map<String, Object> res = service.sendMsg(tel, today, active);
//    	 System.out.println(res);
//     } 
     @Test 
     public void sendMsgTest3() { 
    		//获取页面userName
			String tel = "15924475520";
			// 获取当前日期
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String today = sdf.format(new Date());
			// 获取当前active
			String active = SysStat.active_bind_tel;
//			HttpSession session = req.getSession();
//			String userName = session.getAttribute("userName")==null?"":(String) session.getAttribute("userName");
			
			User param  = new User();
//	  		param.setUser_name("15924475520");//204920495302
		    List<Map<String , Object>> users = service.selectUser(param);
//		    int channel_id = (int) users.get(0).get("channel_id");
//		    tel = tel + "_" + channel_id;
			Map<String , Object> returnMap = service.sendMsg(tel,today,active);
			String json = JSONObject.toJSONString(returnMap); 
			System.out.println(json);
     } 
 }