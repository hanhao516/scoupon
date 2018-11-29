package com.sc.data.scoupon;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import com.sc.data.scoupon.controller.GGController;
import com.sc.data.scoupon.controller.OaController_his;
import com.sc.data.scoupon.controller.ShareController_his;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.task.AlmmCookieKeepTask;
import com.sc.data.scoupon.utils.ReadExcel;

/**
 * Created by zxy on 2018/3/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring.xml")
//@Transactional
public class OrderDownTest {
    @Autowired
    private FanliService service;

    @Autowired
    private  GGController ggController;

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
    public void test1(){
		String filePath = "D:/excelOrders/20181008/TaokeDetail-2018-11-15.xls";
		//excel数据处理放进order表
		//读取excel
		ReadExcel obj = new ReadExcel();
		List<Map<String, Object>> orders = obj.readExcel(new File(filePath),new AlmmCookieKeepTask().getFieldMap());
		System.out.println(orders);
		AlmmCookieKeepTask a = new AlmmCookieKeepTask();
		a.f = service;
		a.dealAlmmOrders(orders);
    }
//    @Test
//    public void test4(){
//    	try {
//			ggController.test(request, response);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    }

}
