package com.sc.data.scoupon.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.sc.data.scoupon.dao.FanliMapper;
import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.AlmmAdzone;
import com.sc.data.scoupon.model.IdentCode;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.HeimaUtils;
import com.sc.data.scoupon.utils.MD5Util;
import com.sc.data.scoupon.utils.Msg;
import com.sc.data.scoupon.utils.MyDateUtil;
import com.sc.data.scoupon.utils.RandomCodeUtil;
import com.sc.data.scoupon.utils.SnowflakeIdWorker;
import com.sc.data.scoupon.utils.WXQR;

/**
 * 基础mysql Service抽象类
 * 使用isap数据源及回滚
 *
 * @create 2013-3-11 下午4:27:33
 */
@Service
@Transactional(value = "huihex")
public class FanliServiceImp implements FanliService {

    private static Logger logger = Logger.getLogger(FanliServiceImp.class);

    @Autowired
    private FanliMapper fanliMapper;

    @Override
    public List<Map<String, Object>> selectUserByName(String userName) {
        User user = new User();
//        user.setUser_name(userName);
        return this.selectUser(user);
    }

    @Override
    public int getIdentCodeByKey(String tel, String today, String active,
                                 String identCode) {
        IdentCode identCodeEntity = new IdentCode();
        identCodeEntity.setActive(active);
        identCodeEntity.setDay(today);
        identCodeEntity.setIdentCode(identCode);
        identCodeEntity.setTel(tel);
        logger.info("getIdentCodeByKey start：" + identCodeEntity.toString());
        List<Map<String, Object>> list = fanliMapper.getIdentCodeByKey(identCodeEntity);
        logger.info("getIdentCodeByKey end：" + list.toString());
        return list.size();
    }

    @Override
    public int insertIdentCode(String tel, String today, String active,
                               String identCode) {
        IdentCode identCodeEntity = new IdentCode();
        identCodeEntity.setActive(active);
        identCodeEntity.setDay(today);
        identCodeEntity.setIdentCode(identCode);
        identCodeEntity.setTel(tel);
        logger.info("insertIdentCode start：" + identCodeEntity.toString());
        int count = fanliMapper.insertIdentCode(identCodeEntity);
        logger.info("insertIdentCode end：" + count);
        return count;
    }

    @Override
    public int insertUser(User user) {
        logger.info("insertUser start：" + user.toString());
        int count = fanliMapper.insertUser(user);
        logger.info("insertUser end：" + count);
        if (count == 1) {
            Map<String, Object> user_map = this.selectUser(user).get(0);
            String user_id = user_map.get("user_id") + "";
            fanliMapper.initialAmt(user_id);
        }
        return count;
    }

    private void createAdzoneIdByUserName(String user_name) {
        User user = new User();
//        user.setUser_name(user_name);
        Map<String, Object> user_map = this.selectUser(user).get(0);
        String user_id = user_map.get("user_id").toString();
        this.setAdzoneId(user_id);
    }

    @Override
    public List<Map<String, Object>> selectUser(User user) {
        logger.info("selectUser start：" + user.toString());
        List<Map<String, Object>> list = fanliMapper.selectUser(user);
        logger.info("selectUser end：" + list.toString());
        return list;
    }

    @Override
    public int selectUserCount(User user) {
        logger.info("selectUserCount start：" + user.toString());
        List<Map<String, Object>> list = fanliMapper.selectUser(user);
        logger.info("selectUserCount end：" + list.toString());
        return list.size();
    }

    @Override
    public int updatePW(User user) {
        logger.info("updatePW start：" + user.toString());
        int count = fanliMapper.updatePW(user);
        logger.info("updatePW end：" + count);
        return count;
    }

    @Override
    public Map<String, Object> inserUserOrder(String user_id, String tids) {
        logger.info("inserUserOrder start：{user_id：" + user_id
                        + ",tids:" + tids
        );
        List<Map<String, Object>> inList = new ArrayList<Map<String, Object>>();
        //获得该用户已经存在的order_ids
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("user_id", user_id);
        String[] orderIds = tids.split(",");
        int return_num = 0;
        String return_str = "";
        for (int i = 0; i < orderIds.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String orderId = orderIds[i];
            if (StringUtils.isBlank(orderId)) {
                continue;
            }
            map.put("order_id", orderId);
            map.put("user_id", user_id);
            inList.add(map);
            try {
                logger.info("inserUserOrder insertInUserOrder start：{inList：" + inList.toString());
                int run_count = fanliMapper.insertInUserOrder(inList);
                logger.info("inserUserOrder insertInUserOrder end：" + run_count);
                return_str += orderId + ",";
                return_num += run_count;
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            inList.clear();
        }
        Map<String, Object> return_map = new HashMap<String, Object>();
        return_map.put("insertCount", return_num);
        return_map.put("insertOrders", return_str);
        logger.info("inserUserOrder end：{return_map：" + return_map.toString());
        return return_map;
    }
    @Override
    public int inserUserOrderByAdzoneid(String adzone_id, String order_id) {
    	logger.info("inserUserOrderByAdzoneid start：{adzone_id：" + adzone_id
    			+ ",order_id:" + order_id
    			);
    	//获得该用户已经存在的order_ids
    	Map<String, Object> param = new HashMap<String, Object>();
    	param.put("adzone_id", adzone_id);
    	param.put("order_id", order_id);
		int run_count = fanliMapper.insertInUserOrderByAdzoneId(param);
    	logger.info("inserUserOrderByAdzoneid end：{run_count：" + run_count);
    	return run_count;
    }

    @Override
    public int getCountOfUserOrder(String startDate, String endDate,
                                   String user_id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("user_id", user_id);
        logger.info("getCountOfUserOrder start：{param：" + param.toString());
        List<Map<String, Object>> list = fanliMapper.getUserOrders(param);
        logger.info("getCountOfUserOrder end：{list：" + list.toString());
        int count = list.size();
        return count;
    }

    @Override
    public List<Map<String, Object>> getUserOrders(String pageNo,
                                                   String pageSize, String startDate, String endDate, String user_id, Map<String, Object> order_param) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("startDate", startDate);
        param.put("endDate", endDate);
        param.put("user_id", user_id);
        param.putAll(order_param);
        this.pageLimitSet(pageNo, pageSize, param);
        logger.info("getUserOrders start：{param：" + param.toString());
        List<Map<String, Object>> list = fanliMapper.getUserOrders(param);
        logger.info("getUserOrders end：{list：" + list.toString());
        return list;
    }

    private void pageLimitSet(String pageNo, String pageSize, Map<String, Object> param) {
        if (pageNo == null || pageSize == null)
            return;
        int pn = Integer.parseInt(pageNo);
        int ps = Integer.parseInt(pageSize);
        int down = ps * (pn - 1);
        param.put("pageSize", pageSize);
        param.put("ofset", down + "");
    }

    @Override
    public int alipayInfoSave(AlipayInfo alipayInfo) {
        logger.info("alipayInfoSave start：{alipayInfo：" + alipayInfo.toString());
        int count = fanliMapper.alipayInfoSave(alipayInfo);
        logger.info("alipayInfoSave end：{count：" + count);
        return count;
    }

    @Override
    public int payTastSave(PayTask payTask, double balance, double draw_money) throws Exception {
        logger.info("payTastSave start：{payTask：" + payTask.toString()
                        + ",balance:" + balance
                        + ",draw_money:" + draw_money
        );
        logger.info("payTastSave payTastSave start：{payTask：" + payTask.toString());
        int count = fanliMapper.payTastSave(payTask);
        logger.info("payTastSave payTastSave end：{count：" + count);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", payTask.getUser_id());
        map.put("balance", balance - payTask.getMoney());
        map.put("draw_money", draw_money + payTask.getMoney());
        logger.info("payTastSave updateBalance start：{map：" + map.toString());
        int up_count = fanliMapper.updateBalance(map);
        logger.info("payTastSave updateBalance end：{up_count：" + up_count);
        if (up_count == 0 || count == 0) {
            throw new Exception("updateBalance fialed");
        }
        logger.info("payTastSave end：{count：" + count);
        return count;
    }

    @Override
    public Map<String, Object> getAmtInfo(String user_id) {
        logger.info("getAmtInfo start：{user_id：" + user_id);
        Map<String, Object> map = fanliMapper.getAmtInfo(user_id);
        logger.info("getAmtInfo end：{map：" + map.toString());
        return map;
    }

    @Override
    public List<Map<String, Object>> getTaskAmtInfo(String user_id) {
        logger.info("getTaskAmtInfo start：{user_id：" + user_id);
        List<Map<String, Object>> list = fanliMapper.getTaskAmtInfo(user_id);
        logger.info("getTaskAmtInfo end：{list：" + list.toString());
        return list;
    }

    @Override
    public Map<String, Object> getAlipayInfo(String user_id) {
        logger.info("getAlipayInfo start：{user_id：" + user_id);
        Map<String, Object> map = fanliMapper.getAlipayInfo(user_id);
        logger.info("getAlipayInfo end：{map：" + map);
        return map;
    }

    @Override
    public int uploadUserPic(User param) {
        logger.info("uploadUserPic start：{param：" + param.toString());
        int count = fanliMapper.uploadUserPic(param);
        logger.info("uploadUserPic end：{count：" + count);
        return count;
    }

    @Override
    public List<Map<String, Object>> drawDetail(String pageNo, String pageSize,
                                                String user_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        logger.info("drawDetail start：{map：" + map.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.drawDetail(map);
        logger.info("drawDetail end：{list：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> drawDetailById(String task_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("task_id", task_id);
        logger.info("drawDetailById start：{map：" + map.toString());
        List<Map<String, Object>> list = fanliMapper.drawDetail(map);
        logger.info("drawDetailById end：{list：" + list.toString());
        return list;
    }

    @Override
    public int upUsernick(User param) {
        logger.info("upUsernick start：{param：" + param.toString());
        int count = fanliMapper.upUsernick(param);
        logger.info("upUsernick end：{count：" + count);
        return count;
    }

    @Override
    public double getUnused(String user_id) {
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfm = new SimpleDateFormat("yyyyMM");
        String today = sdfd.format(new Date());
        Calendar c = Calendar.getInstance();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        String downLimit = null;
        if (Integer.parseInt(today.substring(6)) < 20) {
            c.add(Calendar.MONTH, -1);
        }
        Date time = c.getTime();
        downLimit = sdfm.format(time) + "01";
        map.put("downLimit", downLimit);
        logger.info("getUnused start：{map：" + map.toString());
        Map<String, Object> result = fanliMapper.getUnused(map);
        logger.info("getUnused end：{result：" + result.toString());
        Double unused = result.get("unused") == null ? 0.0 : ((BigDecimal) result.get("unused")).doubleValue();
        return unused;
    }

    @Override
    public List<Map<String, Object>> userList(User user) {
        logger.info("userList start：{user：" + user.toString());
        List<Map<String, Object>> list = fanliMapper.userList(user);
        logger.info("userList end：{list：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> tradeList(String pageNo,
                                               String pageSize, Map<String, Object> param) {
        logger.info("tradeList start：{param：" + param.toString());
        this.pageLimitSet(pageNo, pageSize, param);
        List<Map<String, Object>> list = fanliMapper.tradeList(param);
        logger.info("tradeList end：{list：" + list.toString());
        return list;
    }

    @Override
    public int upTradeType(PayTask payTask) {
        logger.info("upTradeType start：{payTask：" + payTask.toString());
        if (StringUtils.isNotBlank(payTask.getType())) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("task_id", payTask.getTask_id());
            logger.info("upTradeType drawDetail start：{map：" + map.toString());
            Map<String, Object> drawDetail = fanliMapper.drawDetail(map).get(0);
            logger.info("upTradeType drawDetail end：{drawDetail：" + drawDetail.toString());
            BigInteger user_id = (BigInteger) drawDetail.get("cust_id");
            String tb_type = (String) drawDetail.get("type");
            String type = payTask.getType();
            if (!tb_type.equals("3")) {
                return 0;
            }
            if (!type.equals(tb_type)) {
                logger.info("upTradeType getAmtInfo start：{user_id：" + user_id);
                Map<String, Object> amt = fanliMapper.getAmtInfo(user_id.toString());
                logger.info("upTradeType getAmtInfo end：{amt：" + amt);
//				double draw_money =  (double) amt.get("draw_money");
//				double balance =  (double) amt.get("balance");
                double draw_money = ((BigDecimal) amt.get("draw_money")).doubleValue();
                double balance = ((BigDecimal) amt.get("balance")).doubleValue();
                double money = ((BigDecimal) drawDetail.get("money")).doubleValue();
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("user_id", user_id.toString());
                if (tb_type.equals("3") && type.equals("4")) {
                    draw_money = draw_money + money;
                    param.put("draw_money", draw_money);
                    logger.info("upTradeType updateDrawMoney start：{param：" + param.toString());
                    int cnt = fanliMapper.updateDrawMoney(param);
                    logger.info("upTradeType updateDrawMoney end：{cnt：" + cnt);
                } else if (tb_type.equals("3") && type.equals("5")) {
                    balance = balance + money;
                    param.put("balance", balance);
                    logger.info("upTradeType updateBalance start：{param：" + param.toString());
                    int cnt = fanliMapper.updateBalance(param);
                    logger.info("upTradeType updateBalance end：{cnt：" + cnt);
                }
            }
        }
        logger.info("upTradeType upTradeType start：{payTask：" + payTask.toString());
        int count = fanliMapper.upTradeType(payTask);
        logger.info("upTradeType end and upTradeType upTradeType end：{count：" + count);
        return count;
    }

    @Override
    public List<Map<String, Object>> incomeDetail(String pageNo, String pageSize,
                                                  String user_id, String task_id,String lm) {
        logger.info("incomeDetail start：{user_id：" + user_id
                        + ",task_id:" + task_id
        );
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

        if(StringUtils.isBlank(lm)){
            lm="";
        }

        if (StringUtils.isNotBlank(task_id)) {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("task_id", task_id);
            logger.info("incomeDetail getTradeById start：{param：" + param.toString());
            Map<String, Object> trade = fanliMapper.getTradeById(param).get(0);
            logger.info("incomeDetail getTradeById end：{trade：" + trade.toString());
            Timestamp time = (Timestamp) trade.get("time");
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(time.getTime());
            c.add(Calendar.MONTH, -1);
            Date date = c.getTime();
            lm = sdf.format(date);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        map.put("lm", lm);
        logger.info("incomeDetail incomeDetail start：{map：" + map.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.incomeDetail(map);
        logger.info("incomeDetail end and incomeDetail incomeDetail end：{list：" + list.toString());
        return list;
    }

    @Override
    public int drawTastSave(PayTask payTask, double balance, double draw_money) throws Exception {
        logger.info("drawTastSave start：{payTask：" + payTask.toString()
                        + ",balance:" + balance
                        + ",draw_money:" + draw_money
        );
        int count = this.payTastSave(payTask, balance, draw_money);
        logger.info("drawTastSave end：{count：" + count);
        return count;
    }

    @Override
    public List<String> getMembers(Map<String, Object> map) {
        logger.info("getMembers start：{map：" + map.toString());
        List<String> list = fanliMapper.getMembers(map);
        logger.info("getMembers end：{list：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> selectAdminUser(User user) {
        logger.info("selectAdminUser start：{user：" + user.toString());
        List<Map<String, Object>> list = fanliMapper.selectAdminUser(user);
        logger.info("selectAdminUser end：{list：" + list.toString());
        return list;
    }

    @Override
    public int apendWxInfo(Map<String, Object> param, String fuid) {
        logger.info("apendWxInfo  start：{param：" + param.toString());
        int newUserCount = 0;
        String openid = (String) param.get("openid");
        param.put("wx_id", openid);
        logger.info("apendWxInfo selectUserCount start：{param：" + param.toString());
        int count = fanliMapper.selectUserCount(param);
        logger.info("apendWxInfo selectUserCount end：{count：" + count);
        if (count < 1) {
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
            long id = idWorker.nextId();
            param.put("user_tel", id + "");
            // 获取当前日期
            logger.info("apendWxInfo insertWxUser start：{param：" + param.toString());
            User user = new User();
            user.setUser_tel(id + "");
            user.setWx_id(openid);
            user.setUser_nick(param.get("nickName")==null?null:param.get("nickName").toString());
            user.setUser_pic(param.get("avatarUrl")==null?null:param.get("avatarUrl").toString());
			newUserCount = this.insertUser(user);
			if(StringUtils.isNotBlank(fuid)&&newUserCount==1){
				this.saveRelation(openid,fuid);
			}
			// 绑定父子关系
            logger.info("apendWxInfo insertWxUser end：{newUserCount：" + newUserCount);
        }
        logger.info("apendWxInfo  end：{newUserCount：" + newUserCount);
        return newUserCount;
    }

    public int saveRelation(String openid, String fuid) {
    	Map<String, Object> param  = new HashMap<String, Object>();
    	param.put("openid", openid);
    	param.put("fuid", fuid);
        logger.info("saveRelation start：{param：" + param.toString());
    	int count = fanliMapper.saveRelation(param);
        logger.info("saveRelation end：{count：" + count);
		return count;
	}

	@Override
    public List<Map<String, Object>> selectUserByWxid(String openid) {
        logger.info("selectUserByWxid start：{openid：" + openid);
        List<Map<String, Object>> list = fanliMapper.selectUserByOpenid(openid);
        logger.info("selectUserByWxid end：{list：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> selectIps() {
        logger.info("selectIps start");
        List<Map<String, Object>> list = fanliMapper.selectIps();
        logger.info("selectIps end：{list：" + list.toString());
        return list;
    }

    @Override
    public String getIdentCode(String tel, String today, String active) {
        logger.info("getIdentCode start：{tel：" + tel
                        + ",today:" + today
                        + ",active:" + active
        );
        String identCode = "";
        while (true) {
            identCode = RandomCodeUtil.createRandom(Long.parseLong(tel), 6);
            logger.info("getIdentCode getIdentCodeByKey start：{identCode：" + identCode);
            int count = this.getIdentCodeByKey(tel, today, active, identCode);
            logger.info("getIdentCode getIdentCodeByKey end：{count：" + count);
            if (count == 0) {
                break;
            }
        }
        logger.info("getIdentCode end：{identCode：" + identCode);
        return identCode;
    }

    @Override
    public Map<String, Object> bindWx(String tel, String userName, String wx_id) throws Exception {
        logger.info("bindWx start：{"
                        + "tel：" + tel
                        + ",userName：" + userName
                        + ",wx_id：" + wx_id
        );

        Map<String, Object> returnMap = new HashMap<String, Object>();
        User user = new User();
        //用手机号查看用户数量
//        user.setUser_name(userName);
        logger.info("bindWx selectUser start：{user：" + user.toString());
        List<Map<String, Object>> tel_users = this.selectUser(user);
        logger.info("bindWx selectUser end：{tel_users：" + tel_users.toString());
        int tel_size = tel_users.size();
        long tel_user_id = tel_users.size() == 0 ? 0 : (long) tel_users.get(0).get("user_id");
        String tel_wx_id = tel_users.size() == 0 ? null : (String) tel_users.get(0).get("wx_id");

        //用微信id查看用户数量
//        user.setUser_name(null);
        user.setWx_id(wx_id);
        logger.info("bindWx selectUser start：{user：" + user.toString());
        List<Map<String, Object>> wx_users = this.selectUser(user);
        logger.info("bindWx selectUser end：{wx_users：" + wx_users.toString());
        int wx_size = wx_users.size();
        long wx_user_id = wx_users.size() == 0 ? 0 : (long) wx_users.get(0).get("user_id");
        String wx_user_tel = wx_users.size() == 0 ? null : (String) wx_users.get(0).get("user_tel");
        int channel_id = wx_users.size() == 0 ? 0 : (int) wx_users.get(0).get("channel_id");
        //如果是其他公众号用户只更新手机号码
        if (channel_id != 0) {
            //微信用户没绑定过更新手机号
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", wx_user_id);
            param.put("user_tel", tel);
            param.put("user_name", userName + "_" + channel_id);
            logger.info("bindWx updateUserById0 start：{param：" + param.toString());
            int i = fanliMapper.updateUserById(param);
            logger.info("bindWx updateUserById0 end：{i：" + i);
            returnMap.put("bind_flag", true);
            returnMap.put("msg", " no tel info , update tel info success ");
        } else {
            //如果微信用户和手机用户都存在》合并，如果微信用户存在手机用户不存在更新手机号,手机号存在，微信号不存在更新微信号
            if (wx_size == 1 && tel_size == 1 && wx_user_id != tel_user_id
                    && StringUtils.isBlank(tel_wx_id) && StringUtils.isBlank(wx_user_tel)) {
                // 已经合并过的不能再合并
                Map<String, Object> param = new HashMap<String, Object>();
                //修改user_info表手机账号的wx_id
                param.put("user_id", tel_user_id);
                param.put("wx_id", wx_id);
                param.put("wx_name", wx_users.size() == 0 ? null : wx_users.get(0).get("wx_name"));
                String tel_user_pic = tel_users.size() == 0 ? null : (String) tel_users.get(0).get("user_pic");
                String wx_user_pic = wx_users.size() == 0 ? null : (String) wx_users.get(0).get("user_pic");
                if (StringUtils.isBlank(tel_user_pic) && StringUtils.isNotBlank(wx_user_pic)) {
                    param.put("user_pic", wx_user_pic);
                }
                logger.info("bindWx updateUserById start：{param：" + param.toString());
                int i = fanliMapper.updateUserById(param);
                logger.info("bindWx updateUserById end：{i：" + i);
                //user_order_tb表将wx_user_id 改为tel_user_id
                param.clear();
                param.put("wx_user_id", wx_user_id);
                param.put("tel_user_id", tel_user_id);
                logger.info("bindWx reBelongOrderToUser start：{param：" + param.toString());
                int j = fanliMapper.reBelongOrderToUser(param);
                logger.info("bindWx reBelongOrderToUser end：{j：" + j);
                //查询微信账户的余额,把wx余额加入到手机账户余额中
                param.clear();
                logger.info("bindWx getAmtInfo start：{wx_user_id：" + wx_user_id);
                Map<String, Object> wx_amt_info = fanliMapper.getAmtInfo(wx_user_id + "");
                logger.info("bindWx getAmtInfo end：{wx_amt_info：" + wx_amt_info.toString());
                String balance = ((BigDecimal) wx_amt_info.get("balance")).toString();
                String total_amt = ((BigDecimal) wx_amt_info.get("total_amt")).toString();
                String draw_money = ((BigDecimal) wx_amt_info.get("draw_money")).toString();
                param.put("user_id", tel_user_id);
                param.put("balance", balance);
                param.put("total_amt", total_amt);
                param.put("draw_money", draw_money);
                logger.info("bindWx AddAmtInfo start：{param：" + param.toString());
                int k = fanliMapper.AddAmtInfo(param);
                logger.info("bindWx AddAmtInfo end：{k：" + k);
                //微信用户红包归属为手机用户
                param.clear();
                param.put("wx_user_id", wx_user_id);
                param.put("tel_user_id", tel_user_id);
                logger.info("bindWx resetSender start：{param：" + param.toString());
                int g = fanliMapper.resetSender(param);
                logger.info("bindWx resetSender end：{g：" + g);
                logger.info("bindWx resetReceiver start：{param：" + param.toString());
                int h = fanliMapper.resetReceiver(param);
                logger.info("bindWx resetSender end：{h：" + h);
                //删除微信用户
                param.clear();
                param.put("user_id", wx_user_id);
                logger.info("bindWx deleteUserById start：{param：" + param.toString());
                int x = fanliMapper.deleteUserById(param);
                logger.info("bindWx deleteUserById end：{x：" + x);
                if (i == 1 && k == 1) {
                    returnMap.put("bind_flag", true);
                    returnMap.put("msg", " combine success ");
                } else {
                    throw new Exception(" wx_user combine tel_user fialed ! ");
                }
            } else if (wx_size == 1 && tel_size == 1 && wx_user_id != tel_user_id
                    && (StringUtils.isNotBlank(tel_wx_id) || StringUtils.isNotBlank(wx_user_tel))) {
                //已经绑定过不能再绑定
                returnMap.put("bind_flag", false);
                returnMap.put("msg", " has combined once , can not combine another ");
            } else if (wx_size == 1 && tel_size == 1 && wx_user_id == tel_user_id) {
                //已经是同一个用户了
                returnMap.put("bind_flag", true);
                returnMap.put("msg", " has combined , only one user now ");
            } else if (wx_size == 1 && tel_size == 0 && StringUtils.isNotBlank(wx_user_tel)) {
                //微信用户已经绑定过不能在更新
                returnMap.put("bind_flag", false);
                returnMap.put("msg", "no tel user and wx user exsit but wx user has tel , cant update ");
            } else if (wx_size == 1 && tel_size == 0 && StringUtils.isBlank(wx_user_tel)) {
                //微信用户没绑定过更新手机号
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("user_id", wx_user_id);
                param.put("user_tel", tel);
                param.put("user_name", userName);
                logger.info("bindWx updateUserById2 start：{param：" + param.toString());
                int i = fanliMapper.updateUserById(param);
                logger.info("bindWx updateUserById2 end：{i：" + i);
                returnMap.put("bind_flag", true);
                returnMap.put("msg", " no tel info , update tel info success ");
            } else if (wx_size == 0 && tel_size == 1 && StringUtils.isNotBlank(tel_wx_id)) {
                //手机号有微信信息 不更新
                returnMap.put("bind_flag", false);
                returnMap.put("msg", "no wx user and tel user exsit but tel user has wxid , cant update ");
            } else if (wx_size == 0 && tel_size == 1 && StringUtils.isBlank(tel_wx_id)) {
                //更新微信号
                Map<String, Object> param = new HashMap<String, Object>();
                param.put("user_id", tel_user_id);
                param.put("wx_id", wx_id);
                logger.info("bindWx updateUserById3 start：{param：" + param.toString());
                int i = fanliMapper.updateUserById(param);
                logger.info("bindWx updateUserById3 end：{i：" + i);
                returnMap.put("bind_flag", true);
                returnMap.put("msg", " no wx info , update wx info success ");
            } else if (tel_size == 0 && tel_size == 0) {
                //wx用户和手机用户都不存在
                returnMap.put("bind_flag", false);
                returnMap.put("msg", " no wx user and no tel user ");
            } else if (tel_size > 1 || tel_size > 1) {
                //存在过多账户
                returnMap.put("bind_flag", false);
                returnMap.put("msg", " user too much ,can not combine ");
            } else {
                //发生异常情况
                returnMap.put("bind_flag", false);
                returnMap.put("msg", " unknown error ");
            }
        }

        logger.info("bindWx end：{returnMap：" + returnMap.toString());
        return returnMap;
    }

    @Override
    public Map<String, Object> bindTb(String tel, String userName, String tb_uid) throws Exception {
        logger.info("bindTb start：{"
                        + "tel：" + tel
//                        + ",userName：" + userName
                        + ",tb_uid：" + tb_uid
        );
        Map<String, Object> returnMap = new HashMap<String, Object>();
        User user = new User();
        //用手机号查看用户数量
//        user.setUser_name(userName);
        logger.info("bindTb selectUser start：{user：" + user.toString());
        List<Map<String, Object>> tel_users = this.selectUser(user);
        logger.info("bindTb selectUser end：{tel_users：" + tel_users.toString());
        int tel_size = tel_users.size();
        long tel_user_id = tel_users.size() == 0 ? 0 : (long) tel_users.get(0).get("user_id");
        String tel_tb_uid = tel_users.size() == 0 ? null : (String) tel_users.get(0).get("tb_uid");
        //用微信id查看用户数量
//        user.setUser_name(null);
        user.setTb_uid(tb_uid);
        logger.info("bindTb selectUser start：{user：" + user.toString());
        List<Map<String, Object>> tb_users = this.selectUser(user);
        logger.info("bindTb selectUser end：{tb_users：" + tb_users.toString());
        int tb_size = tb_users.size();
        long tb_user_id = tb_users.size() == 0 ? 0 : (long) tb_users.get(0).get("user_id");
        String tb_user_tel = tb_users.size() == 0 ? null : (String) tb_users.get(0).get("user_tel");

        //如果微信用户和手机用户都存在》合并，如果微信用户存在手机用户不存在更新手机号,手机号存在，微信号不存在更新微信号
        if (tb_size == 1 && tel_size == 1 && tb_user_id != tel_user_id
                && StringUtils.isBlank(tel_tb_uid) && StringUtils.isBlank(tb_user_tel)) {
            // 已经合并过的不能再合并
            Map<String, Object> param = new HashMap<String, Object>();
            //修改user_info表手机账号的wx_id
            param.put("user_id", tel_user_id);
            param.put("tb_uid", tb_uid);
            logger.info("bindTb updateUserById start：{param：" + param.toString());
            int i = fanliMapper.updateUserById(param);
            logger.info("bindTb updateUserById end：{i：" + i);
            //user_order_tb表将wx_user_id 改为tel_user_id
            param.clear();
            param.put("wx_user_id", tb_user_id);
            param.put("tel_user_id", tel_user_id);
            logger.info("bindTb reBelongOrderToUser start：{param：" + param.toString());
            int j = fanliMapper.reBelongOrderToUser(param);
            logger.info("bindTb reBelongOrderToUser end：{j：" + j);
            //查询微信账户的余额,把wx余额加入到手机账户余额中
            param.clear();
            logger.info("bindTb getAmtInfo start：{wx_user_id：" + tb_user_id);
            Map<String, Object> tb_amt_info = fanliMapper.getAmtInfo(tb_user_id + "");
            logger.info("bindTb getAmtInfo end：{wx_amt_info：" + tb_amt_info.toString());
            String balance = ((BigDecimal) tb_amt_info.get("balance")).toString();
            String total_amt = ((BigDecimal) tb_amt_info.get("total_amt")).toString();
            String draw_money = ((BigDecimal) tb_amt_info.get("draw_money")).toString();
            param.put("user_id", tel_user_id);
            param.put("balance", balance);
            param.put("total_amt", total_amt);
            param.put("draw_money", draw_money);
            logger.info("bindTb AddAmtInfo start：{param：" + param.toString());
            int k = fanliMapper.AddAmtInfo(param);
            logger.info("bindTb AddAmtInfo end：{k：" + k);
            //删除淘宝用户
            param.clear();
            param.put("user_id", tb_user_id);
            logger.info("bindTb deleteUserById start：{param：" + param.toString());
            int x = fanliMapper.deleteUserById(param);
            logger.info("bindTb deleteUserById end：{x：" + x);
            if (i == 1 && k == 1) {
                returnMap.put("bind_flag", true);
                returnMap.put("msg", " combine success ");
            } else {
                throw new Exception(" wx_user combine tel_user fialed ! ");
            }
        } else if (tb_size == 1 && tel_size == 1 && tb_user_id != tel_user_id
                && (StringUtils.isNotBlank(tel_tb_uid) || StringUtils.isNotBlank(tb_user_tel))) {
            //已经绑定过不能再绑定
            returnMap.put("bind_flag", false);
            returnMap.put("msg", " has combined once , can not combine another ");
        } else if (tb_size == 1 && tel_size == 1 && tb_user_id == tel_user_id) {
            //已经是同一个用户了
            returnMap.put("bind_flag", true);
            returnMap.put("msg", " has combined , only one user now ");
        } else if (tb_size == 1 && tel_size == 0 && StringUtils.isNotBlank(tb_user_tel)) {
            //微信用户已经绑定过不能在更新
            returnMap.put("bind_flag", false);
            returnMap.put("msg", "no tel user and wx user exsit but wx user has tel , cant update ");
        } else if (tb_size == 1 && tel_size == 0 && StringUtils.isBlank(tb_user_tel)) {
            //微信用户没绑定过更新手机号
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", tb_user_id);
            param.put("user_tel", tel);
            param.put("user_name", userName);
            logger.info("bindTb updateUserById2 start：{param：" + param.toString());
            int i = fanliMapper.updateUserById(param);
            logger.info("bindTb updateUserById2 end：{i：" + i);
            returnMap.put("bind_flag", true);
            returnMap.put("msg", " no tel info , update tel info success ");
        } else if (tb_size == 0 && tel_size == 1 && StringUtils.isNotBlank(tel_tb_uid)) {
            //手机号有微信信息 不更新
            returnMap.put("bind_flag", false);
            returnMap.put("msg", "no wx user and tel user exsit but tel user has wxid , cant update ");
        } else if (tb_size == 0 && tel_size == 1 && StringUtils.isBlank(tel_tb_uid)) {
            //更新微信号
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("user_id", tel_user_id);
            param.put("tb_uid", tb_uid);
            logger.info("bindTb updateUserById3 start：{param：" + param.toString());
            int i = fanliMapper.updateUserById(param);
            logger.info("bindTb updateUserById3 end：{i：" + i);
            returnMap.put("bind_flag", true);
            returnMap.put("msg", " no wx info , update wx info success ");
        } else if (tel_size == 0 && tel_size == 0) {
            //wx用户和手机用户都不存在
            returnMap.put("bind_flag", false);
            returnMap.put("msg", " no wx user and no tel user ");
        } else if (tel_size > 1 || tel_size > 1) {
            //存在过多账户
            returnMap.put("bind_flag", false);
            returnMap.put("msg", " user too much ,can not combine ");
        } else {
            //发生异常情况
            returnMap.put("bind_flag", false);
            returnMap.put("msg", " unknown error ");
        }
        logger.info("bindTb end：{returnMap：" + returnMap.toString());
        return returnMap;
    }

    @Override
    public List<Map<String, Object>> unusedDetail(String pageNo, String pageSize,
                                                  String user_id) {
        SimpleDateFormat sdfd = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfm = new SimpleDateFormat("yyyyMM");
        String today = sdfd.format(new Date());
        Calendar c = Calendar.getInstance();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        String downLimit = null;
        if (Integer.parseInt(today.substring(6)) < 20) {
            c.add(Calendar.MONTH, -1);
        }
        Date time = c.getTime();
        downLimit = sdfm.format(time) + "01";
        map.put("downLimit", downLimit);
        logger.info("unusedDetail start：{map：" + map.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.unusedDetail(map);
        logger.info("unusedDetail start：{list：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> tradeIdDetail(String pageNo, String pageSize,
                                                   String trade_id) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("trade_id", trade_id);
        logger.info("tradeIdDetail start：{param：" + param.toString());
        this.pageLimitSet(pageNo, pageSize, param);
        List<Map<String, Object>> list = fanliMapper.tradeIdDetail(param);
        logger.info("tradeIdDetail end：{list：" + list.toString());
        return list;
    }

    @Override
    public String test() {
        logger.info("test");
        return "test";
    }

    @Override
    public int saveAdAct(Map<String, Object> param) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("time", sdf.format(new Date()));
        param.put("unuse_time", sdf.format(new MyDateUtil().getDayMonthNext()));
        logger.info("saveAdAct start：" + param.toString());
        int count = fanliMapper.saveAdAct(param);
        logger.info("saveAdAct end：" + count);
        return count;
    }

    @Override
    public List<Map<String, Object>> selectShareActs(
            Map<String, Object> param_map) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param_map.put("time", sdf.format(new Date()));
        logger.info("tradeIdDetail start：{param：" + param_map.toString());
        List<Map<String, Object>> list = fanliMapper.selectShareActs(param_map);
        logger.info("tradeIdDetail end：{list：" + list.toString());
        return list;
    }

    @Override
    public int updateAdAct(Map<String, Object> param) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        param.put("time", sdf.format(new Date()));
        param.put("unuse_time", sdf.format(new MyDateUtil().getDayMonthNext()));
        logger.info("saveAdAct start：" + param.toString());
        int count = fanliMapper.updateAdAct(param);
        logger.info("saveAdAct end：" + count);
        return count;

    }

    @Override
    public List<Map<String, Object>> getshareRelationByUserId(String pageNo, String pageSize, String user_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        logger.info("getshareRelationByUserId start：" + user_id.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.getshareRelationByUserId(map);
        logger.info("getshareRelationByUserId end：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> getshareOrdersByUserId(String pageNo, String pageSize, String user_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        logger.info("getshareOrdersByUserId start：" + user_id.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.getshareOrdersByUserId(map);
        logger.info("getshareOrdersByUserId end：" + list.toString());
        return list;
    }

    @Override
    public List<Map<String, Object>> getshareItemsByUserId(String pageNo, String pageSize, String user_id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        logger.info("getshareItemsByUserId start：" + user_id.toString());
        this.pageLimitSet(pageNo, pageSize, map);
        List<Map<String, Object>> list = fanliMapper.getshareItemsByUserId(map);
        logger.info("getshareItemsByUserId end：" + list.toString());
        return list;
    }

    //	https://oauth.taobao.com/token?
//		grant_type=authorization_code&
//		response_type=code&
//		client_id=24577666&
//		client_secret=a094ac14b0e646b619fce5463a439106&
//		redirect_uri=http://tk.7fanli.com:8080/Seven/fanli/taobaoPublicLogin.do&
//		code=8WOKVfcqHSCXFSNCW6O6ts2Q8137525
    @Override
    public Map<String, Object> getTaobaoInfo(String code) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("grant_type", "authorization_code");
        param.put("response_type", "code");
        param.put("client_id", "24577666");
        param.put("client_secret", "a094ac14b0e646b619fce5463a439106");
        param.put("redirect_uri", "http://tk.7fanli.com:8080/Seven/fanli/taobaoPublicLogin.do");
        param.put("code", code);
        String url = "https://oauth.taobao.com/token";
        String s = null;
        WXQR wxqr = new WXQR();
        logger.info("getTaobaoInfo  httpPost start：" + param.toString());
        try {
            s = wxqr.httpPostWithOutJson(url, param, "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info("getshareItemsByUserId end：" + s);
        return new Conver().converMap(wxqr.takeJsonFromStr(s));
    }

    @Override
    public int apendTbUser(Map<String, Object> map) {
        Map<String, Object> param = new HashMap<String, Object>();
        logger.info("apendTbUser  start：{param：" + map.toString());
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("flag", false);
        String taobao_user_id = (String) map.get("taobao_user_id");
        String taobao_user_nick = null;
        try {
            taobao_user_nick = URLDecoder.decode((String) map.get("taobao_user_nick"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        param.put("tb_uid", taobao_user_id);
        param.put("taobao_user_nick", taobao_user_nick);
        logger.info("apendTbUser selectUserCount start：{param：" + param.toString());
        int count = fanliMapper.selectUserCount(param);
        logger.info("apendTbUser selectUserCount end：{count：" + count);
        int insert_count = 0;
        if (count < 1) {
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
            long id = idWorker.nextId();
            param.put("user_name", id + "");
            // 获取当前日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            param.put("create_time", time);
            logger.info("apendTbUser insertTbUser start：{param：" + param.toString());
            insert_count = this.insertTbUser(param);
            logger.info("apendTbUser insertTbUser end：{insert_count：" + insert_count);
        }
        logger.info("apendTbUser  end：{insert_count：" + insert_count);
        return insert_count;

    }

    private int insertTbUser(Map<String, Object> param) {
        logger.info("insertTbUser start：{param：" + param.toString());
        int insert_count = fanliMapper.insertTbUser(param);
        logger.info("insertTbUser end：{insert_count：" + insert_count);
        if (insert_count == 1) {
            String user_name = (String) param.get("user_name");
            this.createAdzoneIdByUserName(user_name);
        }
        return insert_count;
    }

    @Override
    public int setAdzoneId(String user_id) {
    	int upcount = 0;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("user_id", user_id);
        // 首先查看user_id是否在白名单，否则只能有一个adzoneid
//        int white_list_count = fanliMapper.countOfAdzoneWhiteList(user_id);
        //获取拥有几个adzoneid
        int adzone_count = fanliMapper.countOfAdzoneId(map);
        if (adzone_count == 0) {
            //为一个无主的adzoneid设置userid
        	 upcount = fanliMapper.bindAdzoneId(map);
        } 
        return upcount;
    }
    @Override
    public Map<String, Object> getCookieInfo(Map<String, Object> site_map) {
		return fanliMapper.getCookieInfo(site_map);
	}

	@Override
    public Map<String, Object> getUsefulSite() {
        //选择siteid almm_site找没满的site添加一条记录 ,如果都是满的则添加一条新的site_id 并添加到库中
        Map<String, Object> site_map = fanliMapper.selectUsefulSiteId();
        if (site_map == null || site_map.size() == 0) {
            site_map = this.createSiteId();
        }
        String site_id = (String) site_map.get("site_id");
        //然后查出siteid对应的adzoneid如果满5万则 更新is_full
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("site_id", site_id);
        int site_adzone_count = fanliMapper.countOfAdzoneId(param);
        if (site_adzone_count >= 1500) {
            param.put("is_full", "1");
            int count = fanliMapper.updateSite(param);
        }
        return site_map;
    }
    @Override
    public Map<String, Object> createSiteId() {
        Map<String, Object> return_map = new HashMap<String, Object>();
        //查找拥有siteid最少的menberid的cookie信息
        Map<String, Object> cookie_map = fanliMapper.siteLessMemberCookieInfo();
        String cookie = (String) cookie_map.get("cookie");
        AlmmAdzone aa = new AlmmAdzone();
        aa.createSiteId(cookie);
        return_map.put("site_id", aa.getSite_id());
        return_map.put("member_id", aa.getMember_id());
        //插入数据库
        int count = fanliMapper.insertSite(return_map);
        return return_map;
    }

    @Override
    public List<Map<String, Object>> selectNoAdzoneUser() {
        logger.info("selectNoAdzoneUser start：");
        List<Map<String, Object>> list = fanliMapper.selectNoAdzoneUser();
        logger.info("selectNoAdzoneUser end：{list：" + list.toString());
        return list;
    }

    @Override
    public Map<String, Object> getAdzoneInfoByUserId(String user_id) {
        logger.info("selectNoAdzoneUser start：");
        List<Map<String, Object>> list = fanliMapper.getAdzoneInfoByUserId(user_id);
        Map<String, Object> map = list.size() == 0 ? new HashMap<String, Object>() : list.get(0);
        logger.info("selectNoAdzoneUser end：{list：" + list.toString());
        return map;
    }

    @Override
    public Map<String, Object> sendMsg(String tel, String today, String active) {
        logger.info("sendMsg start：tel" + tel + ",active" + active + ",today" + today);
        //根据电话号码生成随机验证码
        String identCode = this.getIdentCode(tel, today, active);
        //发短信
        String product = "achao";
        boolean flag = new Msg().sendMsg(tel, today, active, identCode, product);
        //根据userName查询用户个数
        int count = this.insertIdentCode(tel, today, active, identCode);
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("identCode", identCode);
        returnMap.put("count", count);
        returnMap.put("msgFlag", flag);
        logger.info("sendMsg end : returnMap " + returnMap.toString());
        return returnMap;
    }

    @Override
    public List<Map<String, Object>> getChannelInfo(Map<String, Object> param) {
        logger.info("getChannelInfo start：param=" + param.toString());
        List<Map<String, Object>> list = fanliMapper.getChannelInfo(param);
        logger.info("getChannelInfo end：{list：" + list.toString());
        return list;
    }

    @Override
    public Map<String, Object> getWxInfoByToken(String url) throws IOException {
        logger.info("getWxInfoByToken start：url=" + url.toString());
        // 随机token通过url获取数据
        WXQR wxqr = new WXQR();
        String ss = wxqr.httpGet(url, new HashMap<String, String>(), "");
        Map<String, Object> user_data = new Conver().converMap(wxqr.takeJsonFromStr(ss));
//		String ss = "{\"openid\":\"oOon30pXflNPJkGg2UHH-2Vl9QvTdQ\",\"nickname\":\"欧阳明\",\"sex\":1,\"language\":\"zh_CN\",\"city\":\"杭州\",\"province\":\"浙江\",\"country\":\"中国\",\"headimgurl\":\"http://wx.qlogo.cn/mmopen/vi_32/DYAIOgq83erfOO7Yiabch3ob48ibic2wp2icibkHm47pNF9ArwaHbO5ItrNKdiaLicrm4GqvUTI1VMuRic7UCWxB9e8aibA/0\",\"privilege\":[]}";
//		Map<String , Object> user_data = new Conver().converMap(ss);
        String wx_id = user_data.get("openid") == null ? null : (String) user_data.get("openid");
        if (StringUtils.isBlank(wx_id)) {
            wx_id = (String) user_data.get("unionId");
        }
        String imageUrl = user_data.get("imageUrl") == null ? null : (String) user_data.get("imageUrl");
        Map<String, Object> reslut = null;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("openid", wx_id);
        param.put("headimgurl", imageUrl);
        logger.info("getWxInfoByToken end：{param：" + param.toString());
        return param;
    }

    @Override
    public Map<String, Object> createWXQRMap(String share_id, int channel_id) throws IOException {
        logger.info("createWXQRMap start：share_id =" + share_id + ";channel_id =" + channel_id);
        WXQR wxqr = new WXQR();
        String access_token = null;
        if (channel_id == 0) {
            access_token = wxqr.getAccessToken();
        } else {
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("channel_id", channel_id);
            List<Map<String, Object>> channelInfo = this.getChannelInfo(param);
            String access_token_url = (String) channelInfo.get(0).get("access_token_url");
            String ss = wxqr.httpGet(access_token_url, new HashMap<String, String>(), "");
            Map<String, Object> user_data = new Conver().converMap(wxqr.takeJsonFromStr(ss));
            access_token = (String) user_data.get("data");
        }
        Map<String, Object> map = wxqr.createWXQRMap(Long.parseLong(share_id), access_token);
        logger.info("createWXQRMap end：{map：" + map.toString());
        return map;
    }

    @Override
    public AlipayFundTransToaccountTransferResponse aliPay(String money, String alipay_username, String username) {
        logger.info("aliPay start：money =" + money + ";alipay_username =" + alipay_username + ";username =" + username);
        String app_id = "2017091808801998";
        String APP_PRIVATE_KEY = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC35FlEB7yCIeczAZcFYb6NzDWz9I2Hef1SecID+Nc3mmrwHRbv7+I6i0mdPh+LRiZJXM8Iuf+hkH4iVqwY0AL7LuDNPf5/xfNYfPpn+Z0gSK/0GXWh9z2ZTAbmYsbotAa0bIQx4xrHmcrGzU4SOpzX2jqqHdQN68FPbV61ceTJa1wyDYYRmuZXUE2jQLfBAQ9Ft94d+OU/KsJ1qxpBRha+OMB/B2QHKzB6PJ7jASAiH9bqcJdxdyguPURY5pcEEir/8a9a+C5elrYF184lIIAe2T1RsZwP2d5xWjSyUuNavLwWBpSQV3UWmx3Yj/9k1km8M6211OBcRXbxaoK5+uElAgMBAAECggEBAJjY412JdKVRmsMpmiZuBR4FU8ndBlpKCkYoUBxPFEvyPNqRw0Pxxr9UkP5y6XMw/pfR3X/qYdEfscfG9Mq28xNm9pGB6uy3UzoEv3n23yQ7ZozlMIJMZ9XofH+4MI6xPDVxUTvKAbNQYFx3v2GleEJt8H7/xgdAIvBq/uKf5UOIXdiJzbINtb+UK+dhEwN/xHFKsqLfDFtDcbl95VmjoDzovBLbzh4AGEKBg9LsND+p1xfDS/ENbKB2AtUjM4JU81UU+eunXZMDm+Hpfl9ayyvdN+5FRIyv24OkQ64Jtw8nbNzuXMLb+lHP9lcMo/CLodY/t8tdTChx33WXg6tAC2ECgYEA+cCuI0D+4J75zmGpQkx6X1Nh8A9UvsrFsVwV236GJGEz0nyuxU55tXLBRG1Y0Ocktk8Lsxj3rjVceNnFbUtQs1P+Rqw8y2goSeBDaCihECWjiynlMlaXELDZjthoOZFS3Ggme2cra9QUOhxEAAJZhpyYlWsPZERLhAH+yyYFDp0CgYEAvH3sD99rrcG6XsZdOXn7vWpTF3oBJ1w3ZqzAgpxXG/KIHR/TwicInAgSAsSl1jK5MFMCaLeN930uqSpeKkYUR7/9BQBnkY1sU7FEosarBzeCnDJd7UKkSTxVK6pg6w9O3rIAJDVfk1k3E3NFLupUoqszDjOijhbsa1mscXFOkikCgYAHBAugP+YpBy47RvELRLy3Ss9YgAXAak/NYKnYhaBdC0H6arg6IK84kqWtN2kkTSnx2RyaBlyGz0buuidan5//uZ9N+u6mRCHFmYArP+DuZDBI639dv6L0vBMQeTHMVDHAsUhLdSV6HPYIf9zFJ0u+hU2f/ObsySJZ7fhrWoEP3QKBgEk5FaIY5eirEG5O2tpAI+YyTrMZBye2MCNnyqUyfLhzoCLIQWfz5+lNTUncAJxUOhKmvJHXdIQHEkBPICOF88znrS/rN1CYwtNEUuh1Cu2Tx95Lxqcrs0xr7p424s2NtdLXDS0DuuwvxTB+IAsYpuZGYWAL+QL/rroJLO8o2B/5AoGACjiCaXyjpW8dilOHLzPGQmLr87x8gvDLp28W1kTCsMc2142/6CUJq36teS9nXf827rHOav883/c6qxVEFD2jRiRffYhSzQc74U8Nev7H0YxNvZFF0HfgEBidZYfDTdpkx+/A/gEXjmqZQhpuIBR4g4czyc0W9GZQzQg6pzNcA2Y=";
        String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA/C30yUaUX6WpS6i1fkUzM6YMzijEOQxrf3ig/3tlAD+DjzSXH/XGXlC+EKZu4v1fS7UFsc2YTwyjhH7h69+O512NQO+qrvdutN0dyZc031yBib0S9NSlSx0G+VKcuwZNfVKXZWC45L0rBs9LaiR1Pq3hkueiSzpP586fA5V8YnKdTufesuH7A6sY6TCbmCQY9VII5X5IEhc++KwdHJJwguSSktybuj4AfeOCZQOjKNXbFv/G/LffiZ30FFpgt6H7Q7iGN33h0IWRVdK+bizFam/dQcxp96QKtLKrAe+QWCtlFdLSzzMuprFIb1YR3+yJF+JtCkC/srvooFcC6eTopwIDAQAB";
        //SDK调用前需要进行初始化
        AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", app_id, APP_PRIVATE_KEY, "json", "GBK", ALIPAY_PUBLIC_KEY, "RSA2");

        /**
         * 请求参数:
         * out_biz_no   商户转账唯一订单号  （不同来源方给出的ID可以重复，同一个来源方必须保证其ID的唯一性。）
         * payee_type   收款方账户类型      （ALIPAY_LOGONID：支付宝登录号，支持邮箱和手机号格式）
         * payee_account   收款方账户       （使用沙箱商家账号测试：tgodfv6211@sandbox.com）
         * amount        转账金额
         * payer_show_name    付款方姓名 （非必填）
         * payee_real_name    收款方姓名  （非必填）
         * remark     备注 （非必填）
         */
        //使用时间戳作为 商户转账唯一订单号
        Long timestamp = new Date().getTime();
        String out_biz_no = Long.toString(timestamp);

        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizContent("{" +
                "\"out_biz_no\":\"" + out_biz_no + "\"," +
                "\"payee_type\":\"ALIPAY_LOGONID\"," +
                "\"payee_account\":\"" + alipay_username + "\"," +
                "\"amount\":\"" + money + "\"," +
                "\"payer_show_name\":\"7fanli\"," +
                "\"payee_real_name\":\"" + username + "\"," +
                "\"remark\":\"7fanli转账\"" +
                "  }");
        AlipayFundTransToaccountTransferResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        logger.info("aliPay end：{response：" + response.getBody());
        return response;
    }

    @Override
    public Map<String, Object> aliPayTask(String task_id) {
        logger.info("aliPayTask start：task_id=" + task_id);
        List<Map<String, Object>> tasks = this.drawDetailById(task_id);
        //工单
        Map<String, Object> task = tasks.get(0);
        String username = (String) task.get("real_name");
        String alipay_username = task.get("alipay_user_name") + "";
        String money = task.get("money") + "";
        //当type=3时执行以下提现操作
        String type = (String) task.get("type");
        Map<String, Object> res = new HashMap<String, Object>();
        if ("3".equals(type)) {
            AlipayFundTransToaccountTransferResponse response = this.aliPay(money, alipay_username, username);
            if (response.isSuccess()) {
                System.out.println("调用成功");
                res.put("success", true);
                //更新task 状态
                //接口调用成功时，更改交易记录表中的状态,调用接口
                PayTask payTask = new PayTask();
                payTask.setTask_id(task_id);
                payTask.setType("4");//提现
                this.upTradeType(payTask);
            } else {
                System.out.println("调用失败");
                res.put("success", false);
                res.put("reject", "抱歉,"+ response.getSubMsg());
                String reject = (String) res.get("reject");
                //失败处理
                //更新提现状态
                PayTask payTask = new PayTask();
                payTask.setTask_id(task_id);
                payTask.setType("5");//提现
                payTask.setReject(reject);
                ;//提现
                this.upTradeType(payTask);
            }
        } else {
            //type!=3时返回数据
            res.put("success", false);
            res.put("reject", "非提现中的申请");
        }

        logger.info("aliPayTask end：{res：" + res.toString());
        return res;
    }



    @Override
    public List<Map<String, Object>> batchTask() {
        logger.info("batchTask start");
        String earningTime = new MyDateUtil().getFormatFirstDayThisMonth();
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("earningTime", earningTime);
        List<Map<String, Object>> list = fanliMapper.batchTask(param);
        logger.info("batchTask end：{list：" + list.toString());
        return list;
    }

    //确定子父级关系
    @Override
    public void childParent(Map<String, Object> childParentMap) throws IOException {
        // https://gzh.7fanli.com/shareRelation?openid=qwerty&share_id=236&channel_id=10001
        logger.info("childParent start childParentMap:"+childParentMap);
        String parentWxid = childParentMap.get("parentWxid").toString();
        List<Map<String, Object>> parentUserlist = this.selectUserByWxid(parentWxid);
        if (parentUserlist != null && parentUserlist.size() > 0) {
            Object userId = parentUserlist.get(0).get("user_id");
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("user_id", userId);
            List<Map<String, Object>> shareList = this.selectShareActs(userMap);
            if (shareList != null && shareList.size() > 0) {
                String shareId = shareList.get(0).get("share_id").toString();
                String url = childParentMap.get("url").toString();
                String channel_id = childParentMap.get("channel_id").toString();
                String wxid = childParentMap.get("wxid").toString();
                //  https://gzh.7fanli.com/shareRelation?openid=qwerty&share_id=236&channel_id=10001
                url = url + "?openid=" + wxid + "&share_id=" + shareId + "&channel_id=" + channel_id;
                WXQR wxqr = new WXQR();
                String s = wxqr.httpGet(url, new HashMap<String, String>(), "");
                logger.info("childParent end：{list：" + s.toString());
            }

        }
    }
    ////查询出要提现用户的支付宝账号 即提现工单

    @Override
    public List<Map<String, Object>> drawDetail(String user_id){
        //查询出要提现用户的支付宝账号 即提现工单
        Map map = new HashMap<>();
        map.put("user_id", user_id);
        map.put("type", "3");
        List<Map<String, Object>> task_list = fanliMapper.drawDetail(map);
        return task_list;
    }
    public static void main(String[] args) {
    	FanliServiceImp f = new FanliServiceImp();
    	f.createSiteId();
	}

	@Override
	public Map<String , Object> getCookie(String userId) {
		Map<String , Object> param = new HashMap<String, Object>();
		param.put("user_id", userId);
		Map<String , Object> map  = fanliMapper.QueryAlmmCookieByUser(param);
		return map;
	}
	@Override
	public String getToken(String user_id) {
		String token = MD5Util.MD5(user_id + "-" +new Date().getTime());
		return  token;
	}

	@Override
	public int replaceInPreOrder(List<Map<String, Object>> orders) {
		return fanliMapper.replaceInPreOrder(orders);
	}
	
	@Override
	public int incrementInPreOrder(List<Map<String, Object>> orders) {
		return fanliMapper.incrementInPreOrder(orders);
	}

	@Override
	public List<Map<String, Object>> getIncrementOrders() {
		return fanliMapper.getIncrementOrders();
	}

	@Override
	public int insertInOrders(List<Map<String, Object>> increment_orders) {
		return fanliMapper.insertInOrders(increment_orders);
	}

	@Override
	public int truncatePreOrder() {
		return fanliMapper.truncatePreOrder();
	}

	@Override
	public List<Map<String, Object>> incrementUserOrder() {
		return fanliMapper.incrementUserOrder();
	}

	
	@Override
	public int upOrderStat(Map<String, Object> status_up_order) {
		if(status_up_order==null || status_up_order.size()==0)
			return 0;
		return fanliMapper.upOrderStat(status_up_order);
	}
	@Override
	public List<Map<String, Object>> getStatDifOrders() {
		return fanliMapper.getStatDifOrders();
	}

	@Override
	public List<Map<String, Object>> getAllUsefulAlmmUser() {
		return fanliMapper.getAllUsefulAlmmUser();
	}

	@Override
	public int upCookieStatu(String status,String username) {
		Map<String, Object> map =  new HashMap<String, Object>();
		map.put("status", status);
		map.put("username", username);
		return fanliMapper.upCookieStatu(map);
	}

	@Override
	public List<Map<String, Object>> getNoCookieUsers() {
		return fanliMapper.getNoCookieUsers();
	}

	@Override
	public int insertAdzone(Map<String, Object> map) {
		return fanliMapper.insertAdzone(map);
	}

	@Override
	public String heimataokeSearch(String user_id,String item_id) {
		String appkey="728517527";
		String appsecret="648a2859eede9287aeab53bfa6ac3184";
		String sid="3669";
		String pid = "";
		if(user_id == null){
			pid = "mm_125864756_36092971_128624451";
		}else{
			Map<String , Object> map = this.getAdzoneInfoByUserId(user_id);
			pid = map.get("pid").toString();
		}
		HeimaUtils hm = new HeimaUtils();
		String result = "";
		try {
			result = hm.itemSearch(appkey, appsecret, sid, pid, item_id);
			if(result.contains("error_response")){
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String heimaOrderSearch(String start_time, String span) {
		String appkey="728517527";
		String appsecret="648a2859eede9287aeab53bfa6ac3184";
		String sid="3669";
		if(StringUtils.isBlank(start_time) || StringUtils.isBlank(span)){
			return null;
		}
		HeimaUtils hm = new HeimaUtils();
		String result = "";
		try {
			result = hm.orderSearch(appkey, appsecret, sid, start_time, span);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public int heimaOrderInsert(List<Map<String, Object>> n_tbk_orders) {
		
		return fanliMapper.heimaOrderInsert(n_tbk_orders);
	}

	@Override
	public int dealHeimaData(String result) {
		int deal_count = 0;
		//订单插入表
    	Conver c = new Conver();
    	Map<String, Object> orderMap = c.converMap(result);
    	if (orderMap.size() == 0) {
    		return 0 ;
    	}else{
    		List<Map<String, Object>> n_tbk_orders =  (List<Map<String, Object>>) orderMap.get("n_tbk_order");
    		//插入almm_order订单表
    		if(n_tbk_orders!=null && n_tbk_orders.size()>0){
    			int count = this.heimaOrderInsert(n_tbk_orders);
    			System.out.println(count);
    		}
    		for (int i = 0; i < n_tbk_orders.size(); i++) {
    			Map<String, Object> user_order =  n_tbk_orders.get(i);
    			int count = this.inserUserOrderByAdzoneid(user_order.get("adzone_id").toString(), user_order.get("trade_parent_id").toString());
    			System.out.println(count);
    		}
    		deal_count = n_tbk_orders.size();
    	}
    	return deal_count;
	}

	@Override
	public String createToken(String openid, String session_key) {
		String token = UUID.randomUUID().toString().replaceAll("-", "");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("openid", openid);
		map.put("session_key", session_key);
		map.put("token", token);
		int count = fanliMapper.createToken(map);
		if(count == 1){
			return token;
		}else{
			return null;
		}
	}

	@Override
	public Map<String, Object> getTokenInfo(String token, String openid) {
		Map<String, Object> res = null;
		if(StringUtils.isNotBlank(token) || StringUtils.isNotBlank(openid)){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("token", token);
			param.put("openid", openid);
			res = fanliMapper.getTokenInfo(param);
		}
		return res;
	}

	@Override
	public Map<String, Object> getUserInfoByToken(String token) {
		return fanliMapper.getUserInfoByToken(token);
	}

	@Override
	public int upUserPicAndNickByToken(String userPic, String userNick,
			String token) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("user_pic", userPic);
		param.put("user_nick", userNick);
		param.put("token", token);
		return fanliMapper.upUserPicAndNickByToken(param);
	}

	@Override
	public String getUserIdByToken(String token) {
		Map<String, Object> user = this.getUserInfoByToken(token);
		return user == null?null: user.get("user_id") + "";
	}

	@Override
	public Map<String, Object> getOrderDetail(Map<String, Object> difOrder) {
		return fanliMapper.getOrderDetail(difOrder);
	}

	@Override
	public void dealEndOverStatus(Map<String, Object> difOrder, String payStatus) {
		Map<String, Object> order_detail = this.getOrderDetail(difOrder);
		BigDecimal feeString = (BigDecimal) order_detail.get("feeString");
		BigDecimal tb_service_fee = feeString.multiply(new BigDecimal(0.1));
		BigDecimal service_fee = feeString.multiply(new BigDecimal(0.2));
		BigDecimal finalfee = feeString.subtract(service_fee).subtract(tb_service_fee);
		order_detail.put("tb_service_fee", tb_service_fee);
		order_detail.put("service_fee", service_fee);
		order_detail.put("finalfee", finalfee);
		order_detail.put("payStatus", payStatus);
		System.out.println(order_detail);
		this.upOrderStat(order_detail);
		this.AppendOrderAmt(order_detail);
	}
	@Override
	public int AppendOrderAmt(Map<String, Object> order_detail) {
		return fanliMapper.AppendOrderAmt(order_detail);
	}

	@Override
	public int replaceInNewOrder(List<Map<String, Object>> increment_orders) {
		int inc_count = 0; 
		//增量orders放入order表
		if (increment_orders!=null && increment_orders.size()!=0) 
			inc_count = this.insertInOrders(increment_orders);
		//用户订单关系提取
		for (int i = 0; i < increment_orders.size(); i++) {
			Map<String, Object> user_order =  increment_orders.get(i);
			try {
				int count = this.inserUserOrderByAdzoneid(user_order.get("adzoneid")+"", user_order.get("taobaoTradeParentId")+"");
				System.out.println(count);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return inc_count;
	}

	@Override
	public Map<String, Object> getStaticByKey(String desShow_key) {
		return fanliMapper.getStaticByKey(desShow_key);
	}

	@Override
	public int saveFormids(String user_id, String[] formid_arr) {
		// TODO Auto-generated method stub
		int in_count = 0;
		//查询formid是否大于10个  大于则不执行插入
//		int  exit_count = fanliMapper.getFormidCount(user_id);
//		if(exit_count < 10 ){
//			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//			for (int i = 0; i < formid_arr.length; i++) {
//				Map<String, Object> map = new HashMap<String, Object>();
//				map.put("user_id", user_id);
//				map.put("formid", formid_arr[i]);
//			}
//			in_count = fanliMapper.saveFormids(list);
//		}
		
		return in_count ;
	}
}
