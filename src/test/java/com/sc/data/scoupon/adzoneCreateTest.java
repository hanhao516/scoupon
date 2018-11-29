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
import com.sc.data.scoupon.model.AlmmAdzone;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 //@Transactional 
 public class adzoneCreateTest { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void sendMsgTest() {
         for (int i = 0; i < 4900 ; i++) {
             //插入数据库
             this.createAdzoneId();
        	 try {Thread.sleep(5000);} catch (InterruptedException e) {e.printStackTrace();}
        	 System.out.println(i);
		}
 	} 
     public void createAdzoneId() {
    	 Map<String, Object> map = new HashMap<String, Object>();

         Map<String, Object> site_map = service.getUsefulSite();
         String site_id = site_map.get("site_id") + "";
         Map<String, Object> cookie_map = service.getCookieInfo(site_map);
         String cookie = (String) cookie_map.get("cookie");
         AlmmAdzone aa = new AlmmAdzone();
         String member_id = site_map.get("member_id") + "";
         aa.setMember_id(member_id);
         aa.setSite_id(site_id);
         
         aa.createADzoneId(cookie);
         String adzone_id = aa.getAdzone_id();
         map.put("adzone_id", adzone_id);
         map.put("site_id", site_id);
         map.put("pid", aa.getPid());
         //插入数据库
         int count = service.insertAdzone(map);
         System.out.println(map);
     }
     public void createSiteId() {
    	 
     }

     
 }