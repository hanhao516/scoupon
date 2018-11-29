package com.sc.data.scoupon.dao;

import java.util.List;
import java.util.Map;

import com.sc.data.scoupon.model.User;

/**
 * Created by zxy on 2018/3/21.
 */
public interface ShareMappper {
    Long queryFans(Map<String, Object> map);
    List<Map<String, Object>> fansDetailed(Map<String, Object> map);
    User selectChildrenUser(Map<String, Object> Map);
    Map<String,Object> monthEstimate(Map<String, Object> map);

    List<User> queryListFans(Map<String, Object> map);
}
