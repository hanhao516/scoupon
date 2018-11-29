package com.sc.data.scoupon.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
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
    @RequestMapping("queryFans.do")
    @ResponseBody
    public String queryFans(HttpServletRequest req, HttpServletResponse res,String callback) {
        String userName = req.getSession().getAttribute("userName").toString();
        Long fanscount = shareService.queryFans(userName);
        HashMap<String,Object> map = new HashMap<>();
        map.put("fanscount",fanscount);
        String json = JSONObject.toJSONString(map);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /*本月预估收入
	 *
	 * */
    @RequestMapping("monthEstimate.do")
    @ResponseBody
    public String monthEstimate(HttpServletRequest req ,HttpServletResponse res,String startTime,String endTime,String callback){
        String userName = req.getSession().getAttribute("userName").toString();
        if(StringUtils.isBlank(userName)){
            return "";
        }
        Map<String,Object> map =new HashMap<>();
        map.put("startTime",startTime);
        map.put("endTime", endTime);
        map.put("userName",userName);
        Object money =  shareService.monthEstimate(map);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("money",money);
        String json = JSONObject.toJSONString(resultMap);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;

    }


    @RequestMapping(value="fansDetailed.do",produces = "application/json; charset=utf-8")
    @ResponseBody()
    public String fansDetailed(HttpServletRequest req, HttpServletResponse res,String callback,String startTime,String endTime) {
      //首先获取群主
      String userName = req.getSession().getAttribute("userName").toString();

      res.setContentType("text/json");

      res.setCharacterEncoding("UTF-8");

      //用来传递参数的map
      Map<String,Object> map = new HashMap<>();

      Map<String,Object> result  = new HashMap<>();
      String json="";

      if (StringUtils.isBlank(userName)) {
          result.put("success",false);
          result.put("message","no user");
          json = JSONObject.toJSONString(result);
          if (StringUtils.isNotBlank(callback))
              return callback + "(" + json + ")";
          else
              return json;
      }
      map.put("startTime",startTime);
      map.put("endTime",endTime);
      map.put("userName",userName);

      List<Map<String, Object>> maps = shareService.fansDetailed(map);

      if(maps==null||maps.size()==0){
          result.put("success",false);
          result.put("message","no order");
          json = JSONObject.toJSONString(result);
          if (StringUtils.isNotBlank(callback))
              return callback + "(" + json + ")";
          else
              return json;
      }
      json = JSONObject.toJSONString(maps);
      if (StringUtils.isNotBlank(callback))
          return callback + "(" + json + ")";
      else
          return json;

  }
    /*
    本月预估奖励金
     */
    @RequestMapping("rewardMoney.do")
    @ResponseBody
    public String rewardMoney(HttpServletRequest req ,HttpServletResponse res,String startTime,String endTime,String callback){

        String userName = req.getSession().getAttribute("userName").toString();

        if(StringUtils.isBlank(userName)){
            return "";
        }

        Float rewardMoney =shareService.rewardMoney(userName,startTime,endTime);

        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("rewardMoney",rewardMoney);
        String json = JSONObject.toJSONString(resultMap);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /*
        获取分享者的 头像 昵称
     */
    @RequestMapping(value="shareHand.do",produces ="application/json; charset=utf-8")
    @ResponseBody
    public  String shareHand(HttpServletRequest req ,HttpServletResponse res,String callback){
        //结果集
        Map<String,Object> resultMap = new HashMap<>();

        String userName = req.getSession().getAttribute("userName")+"";

        resultMap = shareService.shareHand(userName);

        String json = JSONObject.toJSONString(resultMap);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }


}
