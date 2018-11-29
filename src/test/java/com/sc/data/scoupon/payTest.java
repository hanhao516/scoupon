package com.sc.data.scoupon;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.controller.FanliController_his;
import com.sc.data.scoupon.controller.OaController_his;
import com.sc.data.scoupon.controller.ShareController_his;
import com.sc.data.scoupon.service.FanliService;

/**
 * Created by zxy on 2018/3/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring.xml")
@Transactional
public class payTest {
    @Autowired
    private FanliService service;

    @Autowired
    private  FanliController_his fanliController;
    @Autowired
    private  OaController_his oaController;

    @Autowired
    private ShareController_his shareController;

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
    @Rollback(false)
    public void test1() throws IOException, InterruptedException {
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
    	service.createSiteId();
    	Thread.sleep(2000);
        //String id = "2998";

      //  String s = shareController.queryFans(request, response, "oOon30sjcqPvhG3lv9FvfzkUgtyY", "");
        //Map<String, Object> map = service.aliPayTask(id);
       // String json = shareController.fansDetailed(request, response, "oOon30sjcqPvhG3lv9FvfzkUgtyY", "");

//        String json = shareController.rewardMoney(request, response,  "2018-03-01", "2018-03-22", "");
//        System.out.println("ok");
    }
    @Test
    public void test2() throws IOException {
        //String id = "2998";

        //  String s = shareController.queryFans(request, response, "oOon30sjcqPvhG3lv9FvfzkUgtyY", "");
        //Map<String, Object> map = service.aliPayTask(id);
        // String json = shareController.fansDetailed(request, response, "oOon30sjcqPvhG3lv9FvfzkUgtyY", "");

//        String json = shareController.monthEstimate(request, response, "999999999", "2018-03-01", "2018-03-22", "");
//        System.out.println("ok");
    }
//    @Test
//    public void test2(){
//        String almmorder="136487521993544888";
//        String s = oaController.QueryAlmmOrder(request, response, almmorder, "");
//        System.out.println(s);
//    }
    @Test
    public void test4(){
        String item_id=null;
        String user_id="35";
        String title="英菲克可充电无线鼠标静音无声光电男女生电脑办公笔记本无限游戏";
        String s;
		try {
			s = fanliController.shareQR(request, response, item_id, "", user_id, title);
			 System.out.println(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
    }

}
