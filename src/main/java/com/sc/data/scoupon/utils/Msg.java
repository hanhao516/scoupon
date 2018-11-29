package com.sc.data.scoupon.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;


public class Msg {
	
	String url  = "https://eco.taobao.com/router/rest";
	String appkey  = "24577666";
	String secret  = "a094ac14b0e646b619fce5463a439106";

	public static void main(String[] args) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		List<String> list = new ArrayList<String>();
		list.add("17681806561");
		list.add("13416420236");
		Msg msg = new Msg();
		for (int i = 0; i < list.size(); i++) {
			String tel = list.get(i);
			boolean flag = msg.sendMsg(tel, "20171019", "", "333333","七返利");
			String time = sdf.format(new Date());
			System.out.println("电话："+tel+",成功标志:"+flag+",time:"+time);
		}
//		new Msg().sendMsg("18651000052", "20170808", "1", "222222");
//		Map<String, Object> map =new HashMap<String, Object>();
//		map.put("1", 1);
//		String json = JSONObject.toJSONString(map);
//		System.out.println(json);
	}
	/**
	 * 使用http接口发短信
	 * @param identCode 
	 * @param active 
	 * @param today 
	 * @param tel 
	 * @param num
	 * @param length
	 * @return
	 */
	public  boolean sendMsg(String tel, String today, String active, String identCode,String product){
		boolean flag = false;
		int i = 0;
		String err_code = "";
		boolean success = false;
		while(true){
//			{"alibaba_aliqin_fc_sms_num_send_response":{"result":{"err_code":"0","model":"109258953456^1112363398728","msg":"*","success":true},"request_id":"eqfqa37qaz37"}}

			String jsonStr = this.send(tel, identCode, product);
			System.out.println(jsonStr);
			Conver c = new Conver();
			try {
				Map<String, Object> datamap = c.converMap(jsonStr);
				Map<String, Object> alibaba_aliqin_fc_sms_num_send_response = (Map<String, Object>)datamap.get("alibaba_aliqin_fc_sms_num_send_response");
				Map<String, Object> result = (Map<String, Object>) alibaba_aliqin_fc_sms_num_send_response.get("result");
				err_code = (String) result.get("err_code");
				success = (boolean) result.get("success");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(true==success&&"0".equals(err_code)){
				flag = true;
				break;
			}
			i++;
			if(i==6){
				break;
			}
		}
		return flag;
	}
	
	public String send(String tel, String identCode,String product){
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		req.setExtend( "" );
		req.setSmsType( "normal" );
		req.setSmsFreeSignName( "身份验证" );
		req.setSmsParamString( "{code:'"+identCode+"',product:'"+product+"'}" );
		req.setRecNum( tel);
		req.setSmsTemplateCode( "SMS_58105182" );
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return rsp.getBody();
	}
}
