package com.sc.data.scoupon; 
 import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.utils.Conver;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 @Transactional 
 public class heimaTest { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void sendMsgTest() {
    	String user_id = "3";
		String item_id = "573588455859";
		String sss = service.heimataokeSearch(user_id, item_id);
		Conver c = new Conver();
		System.out.println(c.converMap(sss));
     } 
     public static void main(String[] args) {
    	 System.out.println("mm_125864756_36092971_128624451".split("_")[3]);
     }
 }