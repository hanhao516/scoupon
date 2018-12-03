package com.sc.data.scoupon.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sc.data.scoupon.model.User;

/**
 * Created by zxy on 2018/3/21.
 */
public interface ShareMappper {

	Map<String, Object> getUserCredit(@Param("user_id")String user_id);

	List<Map<String, Object>> childUserAndOrderCount(Map<String, Object> param);

	List<Map<String, Object>> childUserOrders(Map<String, Object> order_param);

	int upCredit(@Param("credit")String credit,@Param("user_id")String user_id);

	int creditToBalance(@Param("user_id")String user_id, @Param("credit")String credit);

	Map<String, Object> creditDetail(@Param("user_id")String user_id);
}
