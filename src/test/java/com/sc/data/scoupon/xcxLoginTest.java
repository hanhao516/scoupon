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
public class xcxLoginTest {
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
//			s = ggController.xcxLogin(request, response,
//					"6469db450ad240499364d21f466cab99", 
//					"",
//					"1",
//					"12",
//					"11111111",
//					"https://wx.qlogo.cn/mmopen/vi_32/pzEvuZrVXeJPu8rh7LOpQ932HFU3JI0I5OPqRbqicDyyHTLDMuiczJQS7ovaxDun8ibrbG5gicYiavgpmoduz9zQicDQ/132",
//					null);
			s = ggController.getImgUrl(request, response, null);
			System.out.println("test--:" + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }

}
