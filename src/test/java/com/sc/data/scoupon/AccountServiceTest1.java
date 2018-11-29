package com.sc.data.scoupon; 
 import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.service.FanliService;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 @Transactional 
 public class AccountServiceTest1 { 
     @Autowired 
     private FanliService service; 
    
     @Test 
     public void testGetAcccountById() { 
    	 Map<String, Object> map = new HashMap<String, Object>();
    	 List<String> list = service.getMembers(map );
    	 System.out.println(list);
     } 
 }