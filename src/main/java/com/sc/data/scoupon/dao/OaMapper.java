package com.sc.data.scoupon.dao;

import java.util.Map;

/**
 * Created by zxy on 2018/3/21.
 */
public interface OaMapper {

    Map<String,Object> QueryAlmmOrder(Map<String, Object> map);
    Map<String,Object> QueryAlmmCookieByUser(Map<String, Object> map);
}
