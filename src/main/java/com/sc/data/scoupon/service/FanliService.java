package com.sc.data.scoupon.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;

public interface FanliService {

	List<Map<String, Object>> selectUserByName(String userName);

	int getIdentCodeByKey(String tel, String today,
			String active, String identCode);

	int insertIdentCode(String tel, String today,
			String active, String identCode);

	int insertUser(User user);

	List<Map<String, Object>> selectUser(User user);

	int selectUserCount(User user);

	int updatePW(User user);

	Map<String, Object> inserUserOrder(String user_id,  String tids);

	int getCountOfUserOrder(String startDate, String endDate, String user_id);

	List<Map<String, Object>> getUserOrders(String pageNo, String pageSize,
			String startDate, String endDate, String user_id, Map<String, Object> order_param);

	int alipayInfoSave(AlipayInfo alipayInfo);

	int payTastSave(PayTask payTask,double balance, double draw_money) throws Exception;

	Map<String, Object> getAmtInfo(String user_id);

	List<Map<String, Object>> getTaskAmtInfo(String user_id);

	Map<String, Object> getAlipayInfo(String user_id);

	int uploadUserPic(User param);

	List<Map<String, Object>> drawDetail(String pageNo, String pageSize,String user_id);
	
	List<Map<String, Object>> drawDetailById(String task_id);

	int upUsernick(User param);

	double getUnused(String user_id);

	List<Map<String, Object>> userList(User user);

	List<Map<String, Object>> tradeList(String pageNo, String pageSize, Map<String, Object> param);

	int upTradeType(PayTask payTask);

	List<Map<String, Object>> incomeDetail(String pageNo, String pageSize,String user_id, String task_id,String lm);

	int drawTastSave(PayTask payTask, double balance, double draw_money) throws Exception;

	List<String> getMembers(Map<String, Object> map);

	List<Map<String, Object>> selectAdminUser(User user);

	int apendWxInfo(Map<String, Object> datamap, String fuid);

	List<Map<String, Object>> selectUserByWxid(String openid);

	List<Map<String, Object>> selectIps();

	String getIdentCode(String tel, String today, String active);

	Map<String, Object> bindWx(String tel, String userName, String wx_id) throws Exception;

	List<Map<String, Object>> unusedDetail(String pageNo, String pageSize,String user_id);

	List<Map<String, Object>> tradeIdDetail(String pageNo, String pageSize,String trade_id);

	String test();

	int saveAdAct(Map<String, Object> param);

	List<Map<String, Object>> selectShareActs(Map<String, Object> param_map);

	int updateAdAct(Map<String, Object> param);

	List<Map<String, Object>> getshareRelationByUserId(String pageNo, String pageSize,String user_id);

	List<Map<String, Object>> getshareOrdersByUserId(String pageNo, String pageSize,String user_id);

	List<Map<String, Object>> getshareItemsByUserId(String pageNo, String pageSize,String user_id);
	
	Map<String, Object> getTaobaoInfo(String code);

	int apendTbUser(Map<String, Object> taobaoInfo);

	Map<String, Object> bindTb(String tel, String userName, String wx_id)
			throws Exception;
	
	int setAdzoneId(String user_id);

	List<Map<String, Object>> selectNoAdzoneUser();

	Map<String, Object> getAdzoneInfoByUserId(String user_id);

	Map<String, Object> sendMsg(String tel, String today, String active);

	List<Map<String, Object>> getChannelInfo(Map<String, Object> param);

	Map<String, Object> createWXQRMap(String share_id, int channel_id) throws IOException;
	
	AlipayFundTransToaccountTransferResponse aliPay(String money, String alipay_username, String username) ;
	
	Map<String, Object>  aliPayTask(String task_id) ;

	Map<String, Object> getWxInfoByToken(String url) throws IOException;

	List<Map<String, Object>> batchTask();

	void childParent(Map<String, Object> childParentMap) throws IOException;

	List<Map<String, Object>> drawDetail(String user_id);

	Map<String, Object> createSiteId();

	Map<String, Object> getCookie(String userId);

	String getToken(String user_id);

	int replaceInPreOrder(List<Map<String, Object>> orders);
	
	int incrementInPreOrder(List<Map<String, Object>> orders);

	List<Map<String, Object>> getIncrementOrders();

	int insertInOrders(List<Map<String, Object>> increment_orders);

	int truncatePreOrder();

	List<Map<String, Object>> incrementUserOrder();

	List<Map<String, Object>> getAllUsefulAlmmUser();

	int upCookieStatu(String status,String username);

	int upOrderStat(Map<String, Object> status_up_orders);

	List<Map<String, Object>> getStatDifOrders();

	List<Map<String, Object>> getNoCookieUsers();

	Map<String, Object> getUsefulSite();

	Map<String, Object> getCookieInfo(Map<String, Object> site_map);

	int insertAdzone(Map<String, Object> map);

	String heimataokeSearch(String user_id,String item_id);
	
	String heimaOrderSearch(String start_time,String span);

	int heimaOrderInsert(List<Map<String, Object>> n_tbk_orders);

	int inserUserOrderByAdzoneid(String string, String string2);

	int dealHeimaData(String result);

	String createToken(String openid, String session_key);

	Map<String, Object> getTokenInfo(String token, String openid);

	Map<String, Object> getUserInfoByToken(String token);

	int upUserPicAndNickByToken(String userPic, String userNick, String token);

	String getUserIdByToken(String token);

	Map<String, Object> getOrderDetail(Map<String, Object> difOrder);

	void dealEndOverStatus(Map<String, Object> difOrder, String payStatus);

	int AppendOrderAmt(Map<String, Object> order_detail);

	int replaceInNewOrder(List<Map<String, Object>> increment_orders);

	Map<String, Object> getStaticByKey(String desShow_key);

	int saveFormids(String user_id, String[] formid_arr);

}