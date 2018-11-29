package com.sc.data.scoupon.task;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.sc.data.scoupon.service.FanliService;
import com.sc.data.scoupon.stat.SysStat;
import com.sc.data.scoupon.utils.EmailUtils;
import com.sc.data.scoupon.utils.MyDateUtil;
import com.sc.data.scoupon.utils.ReadExcel;
import com.sc.data.scoupon.utils.WXQR;


public class AlmmCookieKeepTask {
	public FanliService f;
    // 每五秒执行一次
    @Scheduled(cron = "0 0/10 * * * ?")
    public void TaskJob() throws IOException {
        System.out.println("AlmmCookieKeepTask start");
        String url = "https://pub.alimama.com/common/code/getAuctionCode.json?auctionid=537198102252&siteid=36092971&adzoneid=128624451";
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		f = wac.getBean(FanliService.class);
    	List<Map<String, Object>>  allAlmmUser = f.getAllUsefulAlmmUser();
    	if(allAlmmUser == null || allAlmmUser.size()==0) 
    		return;
    	for (Map<String, Object> map : allAlmmUser) {
			String cookies =  (String) map.get("login_cookie");
			String username =  (String) map.get("username");
			WXQR wxqr = new WXQR();
	        String s = wxqr.httpGet(url, new HashMap<String, String>(), cookies);
	        if(s.contains("invalidKey")){
	        	// orderDown
	        	try {
					this.dealOrders(cookies, username);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }else{
	        	//更新cookie失效状态
	        	f.upCookieStatu("1",username);
	        }
	        System.out.println("AlmmCookieKeepTask :"+ s);
    	}
    	//发送cookie失效邮件
    	sendNocookieUsersMail(f);
        System.out.println("AlmmCookieKeepTask end");
    }

	private void sendNocookieUsersMail(FanliService f) {
    	List<Map<String, Object>> noCookieUsers = f.getNoCookieUsers();
    	if(noCookieUsers.size() == 0) return;
    	String users = "";
    	for (int i = 0; i < noCookieUsers.size(); i++) {
    		Map<String, Object> map = noCookieUsers.get(i);
    		users+= map.get("username");
    		users+= ",";
		}
		EmailUtils.send("AlmmCookie失效的用户", users , "1052680939@qq.com,1569084361@qq.com");
	}

	private void dealOrders(String cookies, String username) throws IOException {
		//order excel 下载
        String filePath = this.orderExcelDown(cookies);
        System.out.println("AlmmOrderDownTask:"+username+"excel下载结束，文件为"+filePath);
//      String filePath = "D:/excelOrders/20180830/20180630-20180831.xls";
        //excel数据处理放进order表
        //读取excel
    	ReadExcel obj = new ReadExcel();
		List<Map<String, Object>> orders = obj.readExcel(new File(filePath),getFieldMap());
		System.out.println(orders);
        this.dealAlmmOrders(orders);
		
	}
	
	public void dealAlmmOrders(List<Map<String, Object>> orders) {
		//orders放入临时表中
		int preOrderCount = f.replaceInPreOrder(orders);
		System.out.println(preOrderCount);
		//查询出增量orders
		List<Map<String, Object>> increment_orders = f.getIncrementOrders();
		//修改存量订单状态
		List<Map<String, Object>> status_up_orders = f.getStatDifOrders();
		//增量orders放入order表
		if(status_up_orders.size()!=0){
			for (int i = 0; i < status_up_orders.size(); i++) {
				Map<String, Object> difOrder = status_up_orders.get(i);
				System.out.println(difOrder);
				String payStatus  = difOrder.get("payStatus") + "";
				//判断订单状态 为3的时候需要更新余额，并更新最终返利和服务费
				if("3".equals(payStatus) ){
					f.dealEndOverStatus(difOrder,payStatus);
				}else{
					//只更新状态
					f.upOrderStat(difOrder);
				}
				System.out.println("------------------");
			}
		}
		int count = f.replaceInNewOrder(increment_orders);
		
        //清空pre表
  		f.truncatePreOrder();
	}

	private String orderExcelDown(String cookies) throws IOException {
    	//下载最近2个月的excel
 		MyDateUtil md = new MyDateUtil();
    	String startTime=md.getXDay(-7, "yyyy-MM-dd");
    	String endTime=md.getXDay(0, "yyyy-MM-dd");
    	//https://pub.alimama.com/report/getTbkPaymentDetails.json?
    	//queryType=1&payStatus=&DownloadID=DOWNLOAD_REPORT_INCOME_NEW&startTime=2018-08-23&endTime=2018-08-29
    	String excel_url = "https://pub.alimama.com/report/getTbkPaymentDetails.json"
    			+ "?queryType=1"
    			+ "&payStatus="
    			+ "&DownloadID=DOWNLOAD_REPORT_INCOME_NEW"
    			+ "&startTime="+startTime
    			+ "&endTime="+endTime;
    	String downFilePath = SysStat.excel_order_path+File.separator+endTime.replaceAll("-", "");
    	String fileName = startTime.replaceAll("-", "")+"-"+endTime.replaceAll("-", "")+".xls";
		WXQR wxqr = new WXQR();
		wxqr.fileDown(excel_url, cookies
				, downFilePath, fileName);
		return downFilePath+File.separator+fileName;
    }
    public Map<String , String> getFieldMap(){
    	Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("商品单价","payPrice");
        keyMap.put("创建时间","createTime");
        keyMap.put("佣金比率","finalDiscountToString");
        keyMap.put("预估收入","tkPubShareFeeString");
        keyMap.put("订单状态","payStatus");
        keyMap.put("商品数","auctionNum");
        keyMap.put("付款金额","realPayFee");
        keyMap.put("补贴比率","tkShareRate");
        keyMap.put("补贴类型","tkShareRateToString");
        keyMap.put("分成比率","shareRate");
        keyMap.put("商品ID","auctionId");
        keyMap.put("订单类型","tkBizTag");
        keyMap.put("效果预估","feeString");
        keyMap.put("来源媒体ID","siteid");
        keyMap.put("佣金金额","finalDiscountFeeString");
        keyMap.put("结算金额","realPayFeeString");
        keyMap.put("商品信息","auctionTitle");
        keyMap.put("类目名称","category");
        keyMap.put("所属店铺","exShopTitle");
        keyMap.put("补贴金额","tkShareRate");
        keyMap.put("广告位ID","adzoneid");
        keyMap.put("收入比率","discountAndSubsidyToString");
        keyMap.put("结算时间","earningTime");
        keyMap.put("掌柜旺旺","exNickName");
        keyMap.put("订单编号","taobaoTradeParentId");
        keyMap.put("成交平台","terminalType");
        keyMap.put("技术服务费比率","tkAlimmShareRate");
        return keyMap;
    }
}