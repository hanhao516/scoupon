package com.sc.data.scoupon.service;

import java.util.List;
import java.util.Map;

/**
 * Created by yuejiajun on 2018/3/21.
 */
public interface ShareService {

	Map<String, Object> getUserCredit(String user_id);

	List<Map<String, Object>> childUserAndOrderCount(String pageNo,
			String pageSize, String user_id);

	List<Map<String, Object>> childUserOrders(String pageNo, String pageSize,
			String user_id, String startDate, String endDate, Map<String, Object> order_param);

	int creditToBalance(String user_id, String credit) throws Exception;

	Map<String, Object> creditDetail(String user_id);
}
