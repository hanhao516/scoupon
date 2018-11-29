package com.sc.data.scoupon.service;

import java.util.List;
import java.util.Map;

/**
 * Created by zxy on 2018/3/21.
 */
public interface ShareService {
    Long queryFans(String user_name);
    List<Map<String, Object>> fansDetailed(Map<String,Object> map);

    Object monthEstimate(Map<String, Object> map);

    Float rewardMoney(String userName,String startTime,String endTime);

    Map<String,Object> shareHand(String userName);
}
