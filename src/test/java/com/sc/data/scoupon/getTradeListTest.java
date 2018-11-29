package com.sc.data.scoupon; 
 import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 @Transactional 
 public class getTradeListTest { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void sendMsgTest() {
 		Map<String, Object> returnMap = new HashMap<String, Object>();
 		Map<String ,Object> order_param = new HashMap<String, Object>();

 		String startDate = "2010-07-26";
 		String endDate = "2020-07-26";
 		
 		String payStatus = null;
 		order_param.put("payStatus", payStatus);
 		String params = null;
 		if(StringUtils.isNotBlank(params)){
 			try {
				params = new String(params.getBytes("ISO-8859-1"),"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
 			order_param.put("params", params);
 		}
 		//获得HttpSession对象
 		User param = new User();
// 		param.setUser_name("17778888777777");
 		List<Map<String, Object>> list = service.selectUser(param);
 		
 		Map<String, Object> map = list.get(0);
 		Long user_id = map.get("user_id")==null?0:(Long)map.get("user_id");
 		int count = service.getCountOfUserOrder(startDate,endDate,user_id+"");
 		List<Map<String ,Object>>  user_order_list = service.getUserOrders("1","10",startDate,endDate,user_id+"",order_param);
 		returnMap.put("pageNo", "1");
 		returnMap.put("pageSize", "10");
 		double d = ((double)count/Double.parseDouble("10"));
 		returnMap.put("pageSum", Math.ceil(d));
 		returnMap.put("count", count);
 		returnMap.put("list", user_order_list);
 		String json = JSONObject.toJSONString(returnMap); 
 		System.out.println(json);
 	} 
 }