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
    	String  token = "ce38ece2801a4f4591d6a3871d84c1d2";
        String s;
		try {
			s = this.xcxLogin(token);
//			s = ggController.getImgUrl(request, response, null);
			System.out.println("test--:" + s);
		} catch (Exception e) {
			e.printStackTrace();
		}
       
    }
//    token 为 null token 不为 null  fuid 为null  fuid 不为 null
    public String xcxLogin(String token) throws IOException{
    	String code = "011IggIA1ubAqc0K6bLA11XeIA1IggIX";
    	String wx = null;
    	String fuid = null;
		String nickName ="yuejiajun";
		String avatarUrl = "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJT1o2KO1Ctia6tx74roCcgxjygmfOC2no2ib29zOH28WJs6q9ysTssYI3WRrQqYqFd7fvicF81LNDNA/132";
		String callback = null;
		String s = ggController.xcxLogin(request, response,
    			code, 
    			token,
    			wx,
    			fuid,
    			nickName,
    			avatarUrl,
				callback);
    	return s;
    }

}
