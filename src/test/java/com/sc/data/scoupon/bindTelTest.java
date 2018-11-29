package com.sc.data.scoupon; 
 import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
 public class bindTelTest { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void sendMsgTest() {
 		Map<String, Object> returnMap = new HashMap<String, Object>();
 		String tel = "13416420236";
// 		tel="13469875252";
 		String userName = tel ;
 		String identCode = "214204";
 	    String session_userName = "421004797584343040";
   		// 获取当前日期
   		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
   		String today = sdf1.format(new Date());
   		// 获取当前active
   		String active = SysStat.active_bind_tel;
 	    //根据userName查询用户个数
   		User param  = new User();
//   		param.setUser_name(session_userName);
 	    List<Map<String , Object>> users = service.selectUser(param);
 	    int channel_id = (int) users.get(0).get("channel_id"); 
 		if(users.size()==1){
// 	    if(true){
 			int ident_count=service.getIdentCodeByKey(tel, today, active, identCode);
 			if(ident_count==1){
// 			if(true){
 				String wx_id = (String) users.get(0).get("wx_id");
// 				String wx_id = "qMcjEOl4bmX1xh3pDnn0";
 				Map<String, Object> msg_map = null;
 				try {
 					msg_map = service.bindWx(tel,userName,wx_id);
 				} catch (Exception e) {
 					e.printStackTrace();
 					returnMap.put("bind_flag", false);
 					returnMap.put("msg", "combine fialed : "+ e.getMessage() );
 				}
 				boolean bind_flag = (boolean) msg_map.get("bind_flag");
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
 		System.out.println(json);
 	} 
 }