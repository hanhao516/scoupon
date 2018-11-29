package com.sc.data.scoupon.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sc.data.scoupon.dao.FanliMapper;
import com.sc.data.scoupon.dao.ShareMappper;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.utils.StringShield;

/**
 * Created by zxy on 2018/3/21.
 */

@Service
@Transactional(value = "huihex")
public class ShareServiceImpl implements ShareService {
    private static Logger logger = Logger.getLogger(FanliServiceImp.class);
    @Autowired
    private ShareMappper shareMapper;

    @Autowired
    private FanliMapper fanliMapper;

    @Override
    public Long queryFans(String user_name) {
        Map<String,Object> map = new HashMap<>();
        User user = new User();
//        user.setUser_name(user_name);
        List<Map<String, Object>> list = fanliMapper.selectUser(user);

        if(list==null||list.size()==0){
            return 0L;
        }

        Object user_id = list.get(0).get("user_id");
        map.put("user_id",user_id);
        //有结果的map
        Long queryFans= shareMapper.queryFans(map);

        return queryFans;
    }
    @Override
    public List<Map<String, Object>> fansDetailed(Map<String,Object> m) {
        //提供条件的map
        User user = new User();
        Object userName = m.get("userName");
//        user.setUser_name(userName.toString());
        //查询当前用户
        List<Map<String, Object>> list = fanliMapper.selectUser(user);
        if(list==null||list.size()==0){
            return null;
        }
        //查询所有粉丝
        String userId = list.get(0).get("user_id").toString();
        m.put("user_id", userId);
        List<User> listFans = shareMapper.queryListFans(m);
        if(listFans==null||listFans.size()==0){
            return null;
        }
        //接收所有订单
        List<Map<String, Object>> List = new ArrayList<>();
        //查询所有的粉丝订单
        for (User fansUser : listFans) {
            String fansUser_id = fansUser.getUser_id();
            m.put("user_id",fansUser_id);
            //单个粉丝所有的订单
            List<Map<String, Object>>  resultList=  shareMapper.fansDetailed(m);

            //屏蔽所有的商品信息
            for (Map<String, Object> stringObjectMap : resultList) {
                if(stringObjectMap.containsKey("auctionTitle")){
                    String title = StringShield.shield(stringObjectMap.get("auctionTitle").toString(),2);
                    stringObjectMap.put("auctionTitle",title);
                }
                //添加商品订单 返回数据
                List.add(stringObjectMap);
            }
        }
        return List;
    }
    @Override
    public Object monthEstimate(Map<String, Object> map) {
        logger.info("monthEstimate start map:" + map);
        Float money =0.f;
        //查出总金额
        User user =new User();

//        user.setUser_name(map.get("userName").toString());

        List<Map<String, Object>> list = fanliMapper.selectUser(user);

        Object user_id = list.get(0).get("user_id");
        map.put("user_id",user_id);

        Map<String,Object> successMap = shareMapper.monthEstimate(map);

        if(successMap==null||successMap.size()==0){
            return money;
        }

        if(successMap.containsKey("money")){
            money = Float.valueOf(successMap.get("money").toString());
        }
        logger.info("getIdentCodeByKey end：" + money.toString());
        return money;
    }
    @Override
    public Float rewardMoney(String userName,String startTime,String endTime) {
        logger.info("rewardMoney start userName:"+userName +" 起始时间"+ startTime +"结束时间"+endTime );
        User user =new User();

        Float money =0.f;

//        user.setUser_name(userName);
        List<Map<String, Object>> list = fanliMapper.selectUser(user);

        //查出多个用户 user—_name出现问题
        if(list==null&&list.size()==0&&list.size()>1){
            return 0.f;
        }
        Object userId = list.get(0).get("user_id");
        Map<String,Object> map = new HashMap<>();
        map.put("user_id", userId);
        map.put("startTime",startTime);
        map.put("endTime", endTime);
        //统计下级用户集合
        List<User> fansList=shareMapper.queryListFans(map);

        if(fansList==null&&fansList.size()==0){
            return 0.f;
        }
        System.out.println(fansList);
        //下级用户
        for (User u : fansList) {
            Map<String,Object> Map =new HashMap<>();
            Object user_id = u.getUser_id();
            Map.put("user_id",user_id);
            Map.put("startTime", startTime);
            Map.put("endTime", endTime);
            Map<String,Object> successMap = shareMapper.monthEstimate(Map);

            if(successMap==null||successMap.size()==0){
                continue;
            }

            for (String s : successMap.keySet()) {
                if(s.equals("shareMoney")&&successMap.get("shareMoney")!=null){
                     money+=Float.valueOf(successMap.get("shareMoney").toString());
                }else{
                    money+=0.f;
                }
            }
        }
        logger.info("rewardMoney end：" + money.toString());
        return money;
    }

    @Override
    public Map<String, Object> shareHand(String userName) {

       Map<String,Object> resultMap = new HashMap<>();

        User user = new User();
//        user.setUser_name(userName);
        List<Map<String, Object>> list = fanliMapper.selectUser(user);
        if(list==null||list.size()==0){
            return null;
        }
        //获取用户的所有信息
        Map<String, Object> map = list.get(0);
        String name = "";
        if (map.containsKey("wx_name")) {
            name = map.get("wx_name").toString();

        }else if (map.containsKey("user_nick")) {
            name = map.get("user_nick").toString();
        }
        if (StringUtils.isNotBlank(name)) {
            resultMap.put("shareName", name);
        } else {
            name = map.get("user_name").toString();
            name=  StringShield.shield(name, 6);
            resultMap.put("shareName", name);
        }

        if(map.containsKey("user_pic")&&StringUtils.isNotBlank(map.get("user_pic").toString())){
            resultMap.put("sharePic", map.get("user_pic").toString());
        }else{
            resultMap.put("sharePic", "0");
        }
        //得到用户id
        return map;
     }
}
