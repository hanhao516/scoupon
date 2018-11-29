package com.sc.data.scoupon.task;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.MyDateUtil;
import com.sc.data.scoupon.utils.ReadExcel;
import com.sc.data.scoupon.utils.WXQR;


public class AlmmOrderDownTask {

    // 每五秒执行一次
    @Scheduled(cron = "0 0/5 * * * ?")
    public void TaskJob() throws Exception {
    	WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
 		FanliService f = wac.getBean(FanliService.class);
    	System.out.println("AlmmOrderDownTask start");
    	int preSecond = 5*60;
    	String start_time = new MyDateUtil().getTimeXSecond(-preSecond, "yyyy-MM-dd HH:mm:ss");
//    	start_time = "2018-10-14 21:05:00";
		String span = preSecond+"";
		//从黑马查询订单
    	String result = f.heimaOrderSearch(start_time, span);
//    	String result = "{\"n_tbk_order\":[{\"adzone_id\":\"128624451\",\"adzone_name\":\"岳家军\",\"alipay_total_price\":\"29.3100\",\"auction_category\":\"厨房电器\",\"commission\":\"0.00\",\"commission_rate\":\"1.0000\",\"create_time\":\"2018-10-07 21:30:18\",\"income_rate\":\"0.0093\",\"item_num\":1,\"item_title\":\"索爱A304不锈钢电热水壶家用自动断电快壶正品电壶大容量电烧水壶\",\"num_iid\":565743134583,\"order_type\":\"淘宝\",\"pay_price\":\"0.00\",\"price\":\"299.00\",\"pub_share_pre_fee\":\"0.27\",\"seller_nick\":\"热岛电器有限公司\",\"seller_shop_title\":\"热岛电器有限公司\",\"site_id\":\"36092971\",\"site_name\":\"岳家军001\",\"subsidy_fee\":\"0\",\"subsidy_rate\":\"0.0000\",\"subsidy_type\":\"-1\",\"terminal_type\":\"2\",\"tk3rd_type\":\"--\",\"tk_status\":12,\"total_commission_fee\":\"0\",\"total_commission_rate\":\"0.0093\",\"trade_id\":236017952301189741,\"trade_parent_id\":236017952301189741},{\"adzone_id\":\"128624451\",\"adzone_name\":\"岳家军\",\"alipay_total_price\":\"0\",\"auction_category\":\"厨房电器\",\"commission\":\"0.00\",\"commission_rate\":\"1.0000\",\"create_time\":\"2018-10-07 21:29:05\",\"income_rate\":\"0.0093\",\"item_num\":1,\"item_title\":\"索爱A304不锈钢电热水壶家用自动断电快壶正品电壶大容量电烧水壶\",\"num_iid\":565743134583,\"order_type\":\"淘宝\",\"pay_price\":\"0.00\",\"price\":\"299.00\",\"pub_share_pre_fee\":\"0\",\"seller_nick\":\"热岛电器有限公司\",\"seller_shop_title\":\"热岛电器有限公司\",\"site_id\":\"36092971\",\"site_name\":\"岳家军001\",\"subsidy_fee\":\"0\",\"subsidy_rate\":\"0.0000\",\"subsidy_type\":\"-1\",\"terminal_type\":\"2\",\"tk3rd_type\":\"--\",\"tk_status\":13,\"total_commission_fee\":\"0\",\"total_commission_rate\":\"0.0093\",\"trade_id\":236251970989189741,\"trade_parent_id\":236251970989189741}]}";
    	int count = f.dealHeimaData(result);
        System.out.println("AlmmOrderDownTask end");
    }

    public static void main(String[] args) {
    	String result = "{\"n_tbk_order\":[{\"adzone_id\":\"128624451\",\"adzone_name\":\"岳家军\",\"alipay_total_price\":\"29.3100\",\"auction_category\":\"厨房电器\",\"commission\":\"0.00\",\"commission_rate\":\"1.0000\",\"create_time\":\"2018-10-07 21:30:18\",\"income_rate\":\"0.0093\",\"item_num\":1,\"item_title\":\"索爱A304不锈钢电热水壶家用自动断电快壶正品电壶大容量电烧水壶\",\"num_iid\":565743134583,\"order_type\":\"淘宝\",\"pay_price\":\"0.00\",\"price\":\"299.00\",\"pub_share_pre_fee\":\"0.27\",\"seller_nick\":\"热岛电器有限公司\",\"seller_shop_title\":\"热岛电器有限公司\",\"site_id\":\"36092971\",\"site_name\":\"岳家军001\",\"subsidy_fee\":\"0\",\"subsidy_rate\":\"0.0000\",\"subsidy_type\":\"-1\",\"terminal_type\":\"2\",\"tk3rd_type\":\"--\",\"tk_status\":12,\"total_commission_fee\":\"0\",\"total_commission_rate\":\"0.0093\",\"trade_id\":236017952301189741,\"trade_parent_id\":236017952301189741},{\"adzone_id\":\"128624451\",\"adzone_name\":\"岳家军\",\"alipay_total_price\":\"0\",\"auction_category\":\"厨房电器\",\"commission\":\"0.00\",\"commission_rate\":\"1.0000\",\"create_time\":\"2018-10-07 21:29:05\",\"income_rate\":\"0.0093\",\"item_num\":1,\"item_title\":\"索爱A304不锈钢电热水壶家用自动断电快壶正品电壶大容量电烧水壶\",\"num_iid\":565743134583,\"order_type\":\"淘宝\",\"pay_price\":\"0.00\",\"price\":\"299.00\",\"pub_share_pre_fee\":\"0\",\"seller_nick\":\"热岛电器有限公司\",\"seller_shop_title\":\"热岛电器有限公司\",\"site_id\":\"36092971\",\"site_name\":\"岳家军001\",\"subsidy_fee\":\"0\",\"subsidy_rate\":\"0.0000\",\"subsidy_type\":\"-1\",\"terminal_type\":\"2\",\"tk3rd_type\":\"--\",\"tk_status\":13,\"total_commission_fee\":\"0\",\"total_commission_rate\":\"0.0093\",\"trade_id\":236251970989189741,\"trade_parent_id\":236251970989189741}]}";
    	Conver c = new Conver();
    	Map<String, Object> orderMap = c.converMap(result);
		List<Map<String, Object>> n_tbk_orders =  (List<Map<String, Object>>) orderMap.get("n_tbk_order");
		Map<String, Object> user_order =  n_tbk_orders.get(0);
    	Object o = user_order.get("trade_parent_id");
    	System.out.println(o);
    	System.out.println(o.getClass());
	}

}