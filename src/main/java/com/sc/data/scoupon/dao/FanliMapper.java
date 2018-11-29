package com.sc.data.scoupon.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.sc.data.scoupon.model.AlipayInfo;
import com.sc.data.scoupon.model.IdentCode;
import com.sc.data.scoupon.model.PayTask;
import com.sc.data.scoupon.model.User;

public interface FanliMapper {
	
    Map<String,Object> QueryAlmmCookieByUser(Map<String, Object> map);
    
    // old
	List<Map<String, Object>> getIdentCodeByKey(IdentCode identCodeEntity);

	int insertIdentCode(IdentCode identCodeEntity);

	int insertUser(User user);

	List<Map<String, Object>> selectUser(User user);
	
	int updatePW(User user);

	int insertInUserOrder(@Param("data")List<Map<String, Object>> data);
	
	List<String> selectUserOrderId(Map<String, Object> param);

	List<Map<String, Object>> getUserOrders(Map<String, Object> param);

	int alipayInfoSave(AlipayInfo alipayInfo);

	int payTastSave(PayTask payTask);

	Map<String, Object> getAmtInfo(@Param("user_id")String user_id);

	List<Map<String, Object>> getTaskAmtInfo(@Param("user_id")String user_id);

	int updateBalance(Map<String, Object> map);
	
	int updateDrawMoney(Map<String, Object> map);

	Map<String, Object> getAlipayInfo(@Param("user_id")String user_id);

	int uploadUserPic(User param);

	int upUsernick(User param);

	Map<String, Object> getUnused(Map<String, Object> map);

	List<Map<String, Object>> drawDetail(Map<String, Object> map);

	List<Map<String, Object>> userList(User user);

	List<Map<String, Object>> tradeList(Map<String, Object> param);
	
	List<Map<String, Object>> getTradeById(Map<String, Object> param);

	int upTradeType(PayTask payTask);

	List<Map<String, Object>> incomeDetail(Map<String, Object> map);
	
	List<String> getMembers(Map<String, Object> map);

	List<Map<String, Object>> selectAdminUser(User user);

	Integer selectUserCount(Map<String, Object> param);

	int insertWxUser(Map<String, Object> param);

	List<Map<String, Object>> selectUserByOpenid(@Param("openid")String openid);

	List<Map<String, Object>> selectIps();

	int updateUserById(Map<String, Object> param);

	int reBelongOrderToUser(Map<String, Object> param);

	int resetSender(Map<String, Object> param);

	int resetReceiver(Map<String, Object> param);

	int deleteUserById(Map<String, Object> param);

	int AddAmtInfo(Map<String, Object> param);

	List<Map<String, Object>> unusedDetail(Map<String, Object> map);

	List<Map<String, Object>> tradeIdDetail(Map<String, Object> param);

	int saveAdAct(Map<String, Object> param);

	List<Map<String, Object>> selectShareActs(Map<String, Object> param_map);

	int updateAdAct(Map<String, Object> param);

	List<Map<String, Object>> getshareRelationByUserId(Map<String, Object> map);

	List<Map<String, Object>> getshareOrdersByUserId(Map<String, Object> map);

	List<Map<String, Object>> getshareItemsByUserId(Map<String, Object> map);

	int insertTbUser(Map<String, Object> param);

	int countOfAdzoneWhiteList(@Param("user_id")String user_id);

	int countOfAdzoneId(Map<String, Object> map);

	Map<String, Object> selectUsefulSiteId();

	int updateSite(Map<String, Object> param);

	Map<String, Object> siteLessMemberCookieInfo();

	int insertSite(Map<String, Object> map);

	Map<String, Object> getCookieInfo(Map<String, Object> map);

	Map<String, Object> getAdzoneInfo(Map<String, Object> param);

	int insertAdzone(Map<String, Object> map);

	List<Map<String, Object>> selectNoAdzoneUser();

	List<Map<String, Object>> getAdzoneInfoByUserId(@Param("user_id")String user_id);

	List<Map<String, Object>> getChannelInfo(Map<String, Object> param);

	List<Map<String, Object>> batchTask(Map<String, Object> param);

	int replaceInPreOrder(@Param("data")List<Map<String, Object>> orders);
	
	int incrementInPreOrder(@Param("data")List<Map<String, Object>> orders);

	List<Map<String, Object>> getIncrementOrders();

	int insertInOrders(@Param("data")List<Map<String, Object>> increment_orders);

	int truncatePreOrder();

	List<Map<String, Object>> incrementUserOrder();

	int initialAmt(@Param("user_id")String user_id);

	List<Map<String, Object>> getAllUsefulAlmmUser();

	int upCookieStatu(Map<String, Object> map);

	List<Map<String, Object>> getStatDifOrders();

	int upOrderStat(Map<String, Object> map);

	List<Map<String, Object>> getNoCookieUsers();

	int bindAdzoneId(Map<String, Object> map);

	int heimaOrderInsert(@Param("data")List<Map<String, Object>> n_tbk_orders);

	int insertInUserOrderByAdzoneId(Map<String, Object> param);

	int createToken(Map<String, Object> map);

	Map<String, Object> getTokenInfo(Map<String, Object> param);

	Map<String, Object> getUserInfoByToken(@Param("token")String token);

	int upUserPicAndNickByToken(Map<String, Object> param);

	Map<String, Object> getOrderDetail(Map<String, Object> difOrder);

	int AppendOrderAmt(Map<String, Object> order_detail);

	int saveRelation(Map<String, Object> param);

	Map<String, Object> getStaticByKey(@Param("desShow_key")String desShow_key);

}