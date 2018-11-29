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
        this.token = "33fe2abf6fef47bea4319da1514edb5f";
    }


    @Test
    public void test() throws IOException{
    	String s = null;
//    	s = this.alipayInfoSaveTest();
//    	s = this.getAlipayInfo();
//    	s = this.getAmtInfo();
//    	s = this.selectUser();
//    	s = this.tradeList();
//    	s = this.payTastSave();
    	s = this.getTradeList();
    	System.out.println(s);
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
