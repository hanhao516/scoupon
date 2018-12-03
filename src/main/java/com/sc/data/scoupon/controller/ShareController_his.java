package com.sc.data.scoupon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sc.data.scoupon.service.ShareService;

/**
 * Created by zxy on 2018/3/21.
 * 关于分享者的接口
 */

@Controller
@RequestMapping(value = "/share")
public class ShareController_his {
    @Autowired
    private ShareService shareService;
    
    

}
