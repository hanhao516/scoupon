package com.sc.data.scoupon.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;
import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.service.OaService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.MyDateUtil;
import com.sc.data.scoupon.utils.RedisUtil;


@Controller
@RequestMapping(value = "/oa")
public class OaController_his {
    @Autowired
    private FanliService fanliService;


    @Autowired
    private OaService oaService;
    /**
     * 用户存在验证
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping("test.do")
    @ResponseBody
    public String test(HttpServletRequest req, HttpServletResponse res) throws IOException {
        return "test";
    }
    /**
     * 用户信息列表
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "userList.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String userList(HttpServletRequest req, HttpServletResponse res,
                           User user, String pageNo, String pageSize,
                           String callback) throws IOException {
        int pn = Integer.parseInt(pageNo);
        int ps = Integer.parseInt(pageSize);
        int down = ps * (pn - 1);
        user.setOfset(down + "");
        user.setPageSize(pageSize);
        List<Map<String, Object>> users = fanliService.userList(user);
        String json = JSONObject.toJSONString(users);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 提现列表
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "tradeList.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String tradeList(HttpServletRequest req, HttpServletResponse res,
                            String user_id, String pageNo, String pageSize, String time, String type,
                            String user_name, String callback) throws IOException {
        Map<String, Object> param = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(time))
            param.put("time", time);
        if (StringUtils.isNotBlank(type))
            param.put("type", type);
        if (StringUtils.isNotBlank(user_name))
            param.put("user_name", user_name);
        if (StringUtils.isNotBlank(user_id))
        	param.put("user_id", user_id);
        List<Map<String, Object>> trades = fanliService.tradeList(pageNo, pageSize, param);
        String json = JSONObject.toJSONString(trades);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 修改提现列表
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "upTradeType.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String upTradeType(HttpServletRequest req, HttpServletResponse res,
                              PayTask payTask,
                              String callback) throws IOException {
        Map<String, Object> back = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(payTask.getReject())) {
            String reject = URLDecoder.decode(payTask.getReject(), "UTF-8");
            payTask.setReject(reject);
            payTask.setType("5");//提现失败
        }
        int count = fanliService.upTradeType(payTask);
        back.put("count", count);
        back.put("msg", count);
        String json = JSONObject.toJSONString(back);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 入账明细
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "incomeDetail.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String incomeDetail(HttpServletRequest req, HttpServletResponse res,
                               String user_id, String task_id, String pageNo, String pageSize,
                               String callback) throws IOException {
        String lm="";
        List<Map<String, Object>> incomes = fanliService.incomeDetail(pageNo, pageSize, user_id, task_id,lm);
        String json = JSONObject.toJSONString(incomes);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 提现明细
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "drawDetail.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String drawDetail(HttpServletRequest req, HttpServletResponse res,
                             String user_id, String pageNo, String pageSize,
                             String callback) throws IOException {
        List<Map<String, Object>> incomes = fanliService.drawDetail(pageNo, pageSize, user_id);
        String json = JSONObject.toJSONString(incomes);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 提现明细
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "tradeIdDetail.do", produces = "application/javascript;charset=utf-8")
    @ResponseBody
    public String tradeIdDetail(HttpServletRequest req, HttpServletResponse res,
                                String id, String pageNo, String pageSize,
                                String callback) throws IOException {
        String trade_id = id;
        List<Map<String, Object>> tradeIdDetailList = fanliService.tradeIdDetail(pageNo, pageSize, trade_id);
        String json = JSONObject.toJSONString(tradeIdDetailList);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

    /**
     * 无线查询用户
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "selectUser.do", produces = "application/javascript;charset=UTF-8")
    @ResponseBody
    public String selectUser(HttpServletRequest req, HttpServletResponse res, String callback) {
        String context = req.getRealPath("/");
        String token = req.getParameter("token");
        Jedis jedis = RedisUtil.getJedis();
        String user_id = jedis.get(token);
        Map<String, Object> map = null;
        if (StringUtils.isNotBlank(user_id)) {
            User user = new User();
            user.setUser_id(user_id);
            List<Map<String, Object>> users = fanliService.selectUser(user);
            map = users.get(0);
            map.remove("password");
            Map<String, Object> aliInfo = fanliService.getAlipayInfo(user_id);
            if (aliInfo != null) {
                map.putAll(aliInfo);
            }
            Map<String, Object> amt_map = fanliService.getAmtInfo(user_id);
            if (amt_map == null) {
                map.put("total_amt", 0.0);
                map.put("balance", 0.0);
                map.put("draw_money", 0.0);
            } else {
                map.putAll(amt_map);
            }
            double unused = fanliService.getUnused(user_id + "");
            map.put("unused", unused);
            String user_pic = (String) map.get("user_pic");
            try {
                if (!new File(context, user_pic).exists()) {
                    try {
                        FileUtils.copyFile(new File(SysStat.pci_local_path, user_pic), new File(context + "/image/" + user_pic));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (user_pic == null) {
                map.put("user_pic", null);
            } else {
                map.put("user_pic", user_pic.contains("http") ? user_pic : SysStat.web_path + user_pic);
            }
        }


        String json = JSONObject.toJSONString(map);
        if (StringUtils.isNotBlank(callback)) {
            return callback + "(" + json + ")";
        } else {
            return json;
        }

    }

    /**
     * 查询订单信息订单
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "getTradeList.do", produces = "application/javascript;charset=UTF-8")
    @ResponseBody
    public String getTradeList(HttpServletRequest req, HttpServletResponse res,
                               String pageNo, String pageSize, String callback, String user_id) throws IOException {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        Map<String, Object> order_param = new HashMap<String, Object>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        if (StringUtils.isBlank(startDate)) {
            startDate = sdf.format(new MyDateUtil().getDayMonthAgo()) + "-01";
        }
        if (StringUtils.isBlank(endDate)) {
            endDate = sdf.format(new Date()) + "-01";
        }
        String payStatus = req.getParameter("payStatus");
        order_param.put("payStatus", payStatus);
        String params = req.getParameter("param");
        if (StringUtils.isNotBlank(params)) {
            params = new String(params.getBytes("ISO-8859-1"), "UTF-8");
            order_param.put("params", params);
        }
        int count = fanliService.getCountOfUserOrder(startDate, endDate, user_id + "");
        List<Map<String, Object>> user_order_list = fanliService.getUserOrders(pageNo, pageSize, startDate, endDate, user_id + "", order_param);
        returnMap.put("pageNo", pageNo);
        returnMap.put("pageSize", pageSize);
        double d = ((double) count / Double.parseDouble(pageSize));
        returnMap.put("pageSum", Math.ceil(d));
        returnMap.put("count", count);
        returnMap.put("list", user_order_list);
        BigDecimal sum = new BigDecimal(0);
        for (int i = 0; i < user_order_list.size(); i++) {
            Map<String, Object> map = user_order_list.get(i);
            BigDecimal finalfee = (BigDecimal) map.get("finalfee");
//			if(finalfee==null) continue;
            sum = sum.add(finalfee);
        }
        returnMap.put("sum", sum);
        String json = JSONObject.toJSONString(returnMap);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }
    /**
     * 批量支付宝付款
     *
     * @param req
     * @param res
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "aliPayBatchTasks.do", produces = "application/javascript;charset=UTF-8")
    @ResponseBody
    public String aliPayBatchTasks(HttpServletRequest req, HttpServletResponse res,
                                   String callback) throws IOException {
     //  List<Map<String, Object>> testList = new ArrayList<>();
        //设置一个失败的list 和 成功的list
        List<String> fail_list = new ArrayList<String>();
        List<String> success_list = new ArrayList<String>();
        //获取所有工单 找到所有需要打款的taskid
        List<Map<String, Object>> list = fanliService.batchTask();
        //for循环一个一个打款 正式list 测试testList
//        for(int i = 0; i < list.size(); i++){
//            Map<String, Object> map = list.get(i);
//            String user_id = map.get("user_id")+"";
//            if(user_id.equals("35")){
//                testList.add(map);
//            }
//        }
      //  list.clear();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> task = list.get(i);
//			String task_id =  task.get("task_id")+"";
            //判断金额是否有问题，是否可以打款
            double valid_money1 = ((BigDecimal) task.get("valid_money1")).doubleValue();
            double valid_money2 = ((BigDecimal) task.get("valid_money2")).doubleValue();
            String user_id = task.get("user_id") + "";
            //查询出要提现用户的支付宝账号 即提现工单
            List<Map<String, Object>> task_list = fanliService.drawDetail(user_id);
            for (int j = 0; j < task_list.size(); j++) {
                Map<String, Object> task_map = task_list.get(j);
                String task_id = task_map.get("id").toString();
                if (valid_money1 == 0 && valid_money2 == 0) {
                    //支付金额
                    Map<String, Object> apl_map = fanliService.aliPayTask(task_id);
                    boolean success = (boolean) apl_map.get("success");
                    //成功或失败处理
                    if (success) {
                        success_list.add(task_id);
                    } else {
                        //失败处理
                        fail_list.add(task_id);
                    }  } else {
                    //失败处理
                    fail_list.add(task_id);
                    //更新提现状态
                    PayTask payTask = new PayTask();
                    payTask.setTask_id(task_id);
                    payTask.setType("3");//提现
//	    		(sumorder + IF(hongbao is null , 0 , hongbao)-total_amt) as valid_money1,
//				balance+draw_money+money-total_amt as valid_money2
                 //(sumorder+hb+share-total_amt!=0 ) or (balance + draw_money + money - total_money!=0);
                    payTask.setReject("此单的金额出现问题");
                 //提现
                    fanliService.upTradeType(payTask);
                }
            }

        }
        Map<String, Object> returnMap = new HashMap<String, Object>();
        returnMap.put("fail_list", fail_list);
        returnMap.put("success_list", success_list);
        String json = JSONObject.toJSONString(returnMap);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }
    @RequestMapping(value = "transferMoney.do", produces = "application/javascript;charset=UTF-8")
    @ResponseBody
    public String transferMoney(HttpServletRequest req, HttpServletResponse res, String callback) {
        try {
            req.setCharacterEncoding("GBK");
        } catch (UnsupportedEncodingException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        String username = "";
        System.out.print(username);
        String alipay_username = "";
        try {
            username = new String(req.getParameter("username").getBytes("ISO-8859-1"), "UTF-8");
            alipay_username = new String(req.getParameter("alipay_username").getBytes("ISO-8859-1"), "UTF-8");

        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        String money = req.getParameter("money");
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
        if (response.isSuccess()) {
            System.out.println("调用成功");
        } else {
            System.out.println("调用失败");
        }
        String json = JSONObject.toJSONString(response);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }


    /*查询用户的订单
     * 2018-03-21
     * */
    @RequestMapping("queryAlmmOrder.do")
    @ResponseBody
    public String QueryAlmmOrder(HttpServletRequest req, HttpServletResponse res, String almmorder,String callback) {
        //用来存入要查询的订单数据
        Map<String,Object> Map = new HashMap<>();
        Map = oaService.QueryAlmmOrder(almmorder);
        String json = JSONObject.toJSONString(Map);
        if (StringUtils.isNotBlank(callback))
            return callback + "(" + json + ")";
        else
            return json;
    }

}
