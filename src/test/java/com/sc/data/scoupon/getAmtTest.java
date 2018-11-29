package com.sc.data.scoupon; 
 import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.service.FanliService;

 @RunWith(SpringJUnit4ClassRunner.class) 
 @ContextConfiguration("/spring.xml") 
 @Transactional 
 public class getAmtTest { 
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
     @Rollback(false)
     public void sendMsgTest3() {
    	 AlipayInfo alipayInfo = new AlipayInfo();
//    	 alipayInfo.setAlipay_user_name(alipay_user_name);
//    	 alipayInfo.setReal_name(real_name);
//    	 alipayInfo.setRun_status(run_status);
//    	 alipayInfo.setUser_id(user_id);
		int count = service.alipayInfoSave(alipayInfo );
     } 
 }