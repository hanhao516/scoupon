package com.sc.data.scoupon.service;

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
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.PageUtils;

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
	public Map<String, Object> getUserCredit(String user_id) {
		return shareMapper.getUserCredit(user_id);
	}

	@Override
	public List<Map<String, Object>> childUserAndOrderCount(String pageNo,
			String pageSize, String user_id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", user_id);
        PageUtils.pageLimitSet(pageNo, pageSize, param);
        logger.info("childUserAndOrderCount start：{param：" + param.toString());
        List<Map<String, Object>> list = shareMapper.childUserAndOrderCount(param);
        logger.info("childUserAndOrderCount end：{list：" + list.toString());
        return list;
    }
	@Override
	public Map<String, Object> creditDetail( String user_id) {
		logger.info("creditDetail start：{user_id：" + user_id.toString());
		Map<String, Object> map = shareMapper.creditDetail(user_id);
		logger.info("creditDetail end：{map：" + map.toString());
		return map;
	}

	@Override
	public List<Map<String, Object>> childUserOrders(String pageNo,
			String pageSize, String user_id,String startDate, String endDate, Map<String, Object> order_param) {
		order_param.put("user_id", user_id);
		order_param.put("startDate", startDate);
		order_param.put("endDate", endDate);
        PageUtils.pageLimitSet(pageNo, pageSize, order_param);
        logger.info("childUserOrders start：{param：" + order_param.toString());
        List<Map<String, Object>> list = shareMapper.childUserOrders(order_param);
        logger.info("childUserOrders end：{list：" + list.toString());
		return list;
	}

	@Override
	public int creditToBalance(String user_id, String credit) throws Exception {
		if(StringUtils.isBlank(credit) || StringUtils.isBlank(user_id)){
			return 0;
		}
		//减去积分
		int upCount = shareMapper.upCredit(credit,user_id);
		// 计算积分金额
		Double creditAmt = Double.valueOf(credit);
		//积分转入余额 1 增加余额 2 添加入账记录
		int ctb_count = shareMapper.creditToBalance(user_id,creditAmt.toString());
		
		PayTask payTask = new PayTask();
		payTask.setUser_id(user_id);
		payTask.setType("1");
		payTask.setMoney(creditAmt);
		payTask.setNote("奖励金"+credit);
		int pay_count = fanliMapper.payTastSave(payTask);
		if(upCount!=1 || ctb_count!=1 || pay_count!=1 )
			throw new Exception("upCount="+upCount+", ctb_count="+ctb_count+",pay_count="+pay_count);
		return 1;
	}


}
