package com.sc.data.scoupon;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.controller.WxController;
import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.service.FanliService;

/**
 * Created by zxy on 2018/3/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring.xml")
//@Transactional
public class wxControllerTest {
    @Autowired
    private FanliService service;

    @Autowired
    private  WxController ggController;

    //mock模拟session
    private MockHttpSession session;

    //mock模拟request
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;
    
    private String token;
    
    @Before
    public void setUp() throws Exception {
        this.session = new MockHttpSession();
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.token = "100aa571828a4d1295c730f022261005";
    }


    @Test
    public void test() throws IOException{
    	String s = null;
//    	s = this.alipayInfoSaveTest();
//    	s = this.getAlipayInfo();
//    	s = this.getAmtInfo();
    	s = this.selectUser();
//    	s = this.tradeList();
//    	s = this.payTastSave();
//    	s = this.getTradeList();
//    	s = this.saveFormids();
//    	s = this.childUserAndOrderCount();
//    	s = this.childUserOrders();
//    	try {
//			s = this.creditToBalance();
//		} catch (Exception e) {e.printStackTrace();}
//    	s = this.creditDetail();

    	System.out.println(s);
    }
    
    public String creditDetail() throws IOException{
    	String callback = null;
    	String s = ggController.creditDetail(request, response, token , callback );
    	return s;
    }
    public String saveFormids() throws IOException{
    	String callback = null;
		String formids = "111,222,333";
		String s = ggController.saveFormids(request, response, token,  formids , callback );
    	return s;
    }
    public String childUserAndOrderCount() throws IOException{
    	String pageNo = "1";
    	String pageSize = "5";
    	String callback = null;
    	String s = ggController.childUserAndOrderCount(request, response, token, pageNo, pageSize, callback);
    	return s;
    }
    public String childUserOrders() throws IOException{
    	String pageNo = "1";
    	String pageSize = "5";
    	String callback = null;
    	String child_id = "3";
    	request.setParameter("payStatus", "3");
    	request.setParameter("startDate", "2018-07-29 00:00:00");
    	request.setParameter("endDate", "2018-12-02 00:00:00");
		String s = ggController.childUserOrders(request, response, token, pageNo, pageSize,child_id , callback);
    	return s;
    }
    public String creditToBalance() throws Exception{
    	String callback = null;
    	String s = ggController.creditToBalance(request, response, token, callback);
    	return s;
    }
    public String getTradeList() throws IOException{
    	String pageNo = "1";
		String pageSize = "5";
    	request.setParameter("payStatus", "3");
    	request.setParameter("startDate", "2018-10-29 00:00:00");
    	request.setParameter("endDate", "2018-11-29 00:00:00");
		String s = ggController.getTradeList(request, response, token, pageNo, pageSize, null);
    	return s;
    }
    public String payTastSave() throws IOException{
    	PayTask p = new PayTask();
    	p.setMoney(1);
    	String s = ggController.payTastSave(request, response, token, p, null);
    	return s;
    }
    public String alipayInfoSaveTest() throws IOException{
    	request.setParameter("real_name", "岳加俊");
    	request.setParameter("alipay_user_name", "17681806561");
		String s = ggController.alipayInfoSave(request, response, new AlipayInfo(), token, null);
		return s;
    }
    public String getAlipayInfo() throws IOException{
    	String s = ggController.getAlipayInfo(request, response, token, null);
    	return s;
    }
    public String getAmtInfo() throws IOException{
    	String s = ggController.getAmtInfo(request, response, token, null);
    	return s;
    }
    public String selectUser() throws IOException{
    	String s = ggController.selectUser(request, response, token, null);
    	return s;
    }
    public String tradeList() throws IOException{
    	String pageNo = "1";
		String pageSize = "4";
		String time = null;
		String type = "3";
		String s = ggController.tradeList(request, response, token, pageNo, pageSize, 
    			time, type, null);
    	return s;
    }
}
