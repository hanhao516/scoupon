package com.sc.data.scoupon.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.dao.OaMapper;

/**
 * Created by zxy on 2018/3/21.
 */


@Service
@Transactional(value = "huihex")
public class OaServiceImpl implements OaService{

    @Autowired
    private OaMapper oaMapper;

    @Override
    public Map<String, Object> QueryAlmmOrder(String almmorder) {
        //用来接收返回的数据
        Map<String,Object> resultMap = new HashMap<>();
        //订单的唯一标示
        Map<String,Object> map = new HashMap<>();
        map.put("taobaoTradeParentId", almmorder);
        resultMap = oaMapper.QueryAlmmOrder(map);
        return resultMap;
    }
}
