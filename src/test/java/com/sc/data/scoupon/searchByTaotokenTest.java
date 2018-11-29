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
import com.sc.data.scoupon.service.FanliService;

/**
 * Created by zxy on 2018/3/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring.xml")
//@Transactional
public class searchByTaotokenTest {
    @Autowired
    private FanliService service;

    @Autowired
    private  WxController ggController;

    //mock模拟session
    private MockHttpSession session;

    //mock模拟request
    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        this.session = new MockHttpSession();
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
    }


    @Test
    public void test(){
        String s;
		try {
			String taotoken = "￥frCtbkik1fH￥";
//			String taotoken = "€rrmUbhebAs1€";
//			String taotoken = "￥6FOdb6oX7k1￥";
//			String taotoken = "￥tfkCb6MEi7x￥";
			s = ggController.searchByTaotoken(request, response, taotoken,"799afa7f24994bc2807c74a0ff5bd9e2", null);
//			s = ggController.xcxLogin(request, response, "6469db450ad240499364d21f466cab99", "",null, null);
			System.out.println("test:" + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }

}
