package com.sc.data.scoupon.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.TbkItemInfoGetRequest;
import com.taobao.api.request.TbkItemRecommendGetRequest;
import com.taobao.api.request.TbkTpwdCreateRequest;
import com.taobao.api.request.WirelessShareTpwdQueryRequest;
import com.taobao.api.response.TbkItemInfoGetResponse;
import com.taobao.api.response.TbkItemRecommendGetResponse;
import com.taobao.api.response.TbkTpwdCreateResponse;
import com.taobao.api.response.WirelessShareTpwdQueryResponse;

// taobao.trades.sold.get (查询卖家已卖出的交易数据（根据创建时间）)
public class TbOpenApi {
	public static void main(String[] args) throws ApiException {
//		String taoTokenUrl = "https://uland.taobao.com/coupon/edetail?e=Z8Nq5ctkNc8N+oQUE6FNzK272kV1Hg9GKdkz+t7A8joNNs5FPdxAwc0FiVGJS8/VLylVhMIAxg1HoYUaym6s8l6i97NcswApMtnI/46w4Wg=&af=1&pid=mm_125864756_36092971_128624451";
//		TbOpenApi t = new TbOpenApi();
//		String taoToken = t.taoToken(taoTokenUrl, null);
//		System.out.println(taoToken);
//		System.out.println("https://s.click.taobao.com/t?e=m%3D2%26s%3D8oSvdnssumBw4vFB6t2Z2ueEDrYVVa64LKpWJ%2Bin0XLLWlSKdGSYDuI0xMuideCoMMgx22UI05akcDvh%2FazoTPmepLdEYxipLMI767UxAaEQor27338dFiJGrlHwzACn%2FANN%2FhQSwHOySbHmSI7wOtef%2FroYqRld%2FufIeaShmLvWGPPZ03CRxLnIr3aSU%2Bj5X5DU7svJUQPGDmntuH4VtA%3D%3D&union_lens=lensId:0b832c69_0ba8_1669790c2b2_7e49&ut_sk=1.utdid_null_1540141138808.TaoPassword-Outside.taoketop&sp_tk=77+lV2FFS2JoZVgxdE/vv6U=&spm=a211b4.23496927&visa=13a09278fde22a2e&disablePopup=true&disableSJ=1".contains("s.click.taobao.com"));
//		String content = "【净尔康洗衣机槽清洗剂滚筒波轮内筒夹层清洁剂除垢剂清理粉通用型】http://m.tb.cn/h.3iVK0AX?sm=787e8d 点击链接，再选择浏览器咑閞；或復·制这段描述￥s18XbThupvp￥后到淘寳";
//		content = "￥WaEKbheX1tO￥";
//		content = "￥djdpbQml7Zg￥";
//		content = "￥IitlbQ7qQyj￥";
//		TbOpenApi t = new TbOpenApi();
//		Map<String, Object> taoToken = t.taoTokenExtract(content);
//		System.out.println(taoToken);
		
//		System.out.println(new TbOpenApi().itemInfo("528674725498"));
		String text = " url=https://item.taobao.com/item.htm?ut_sk=1.WcSfMo9O/EYDAEaMD5lixVf9_21380790_1542260657751.Copy.1&id=574686582338&sourceType=item&price=359&origin_price=699&suid=A4C904E3-10FA-415F-951B-2845AFF2E885&un=23292e94d78c38e20f8768acc41474bb&share_crt_v=1&sp_tk=77+lZGpkcGJRbWw3Wmfvv6U=&spm=a211b4.23496927&visa=13a09278fde22a2e&disablePopup=true&disableSJ=1";
		text = " url=https://a.m.taobao.com/i21874619959.htm?price=169-259&sourceType=item&sourceType=item&suid=be5a95f4-8b4a-4705-b2af-281e8bbb5f05&ut_sk=1.WpJiB4PiF4kDAK%2FFb5irheGm_21646297_1542195862125.TaoPassword-WeiXin.1&un=774ceb9a17f8b7fea7c40094137a931a&share_crt_v=1&sp_tk=77+lSWl0bGJRN3FReWrvv6U=&spm=a211b4.23496927&visa=13a09278fde22a2e&disablePopup=true&disableSJ=1";
		String item_id = new TbOpenApi().getItemid(text);
		System.out.println(item_id);
	}
	public String itemurlExtract(String itemurl){
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23496927";
		String secret = "e60dd07d064bda7b9e924be2357bc950";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//		TbkItemClickExtractRequest req = new TbkItemClickExtractRequest();
//		req.setClickUrl("https://s.click.taobao.com/***");
//		TbkItemClickExtractResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		return null;
	}
	public Map<String, Object> taoTokenExtract(String content){
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23496927";
		String secret = "e60dd07d064bda7b9e924be2357bc950";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		WirelessShareTpwdQueryRequest req = new WirelessShareTpwdQueryRequest();
		req.setPasswordContent(content);
		Map<String, Object> res_map = null;
		try {
			WirelessShareTpwdQueryResponse rsp = client.execute(req);
			String body = rsp.getBody();
			if(StringUtils.isNotBlank(body)){
				System.out.println(body);
				Map<String, Object> body_map = new Conver().converMap(body);
				res_map = (Map<String, Object>) body_map.get("wireless_share_tpwd_query_response");
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		String itemurl = (String) res_map.get("url");
		
		if(StringUtils.isNotBlank(itemurl)){
			if(itemurl.contains("s.click.taobao.com")){
				return null;
			} else{
				String item_id = this.getItemid(itemurl);
				res_map.put("item_id", item_id);
			}
		}
		return  res_map;
	}
	private String getItemid(String itemurl) {
		PatternUtils p = new PatternUtils();
		String item_id = p.rexString("i\\d+", itemurl);
		if(item_id==null){
			item_id = p.rexString("id=\\d+", itemurl).split("=")[1];
		}else{
			item_id = item_id.replace("i", "");
		}
		return item_id;
	}
	public String taoToken(String taoTokenUrl,String text,String pic_url){
		if(StringUtils.isBlank(taoTokenUrl))
			return null;
		if(StringUtils.isBlank(pic_url))
			pic_url = "http://ys-n.ys168.com/606178137/lTTfltm7N3T5J6F59P55/logo.jpg";
		if(StringUtils.isBlank(text)){
			text = "小水粒提供";
		}else {
			text = "小水粒提供:"+text;
		}
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23496927";
		String secret = "e60dd07d064bda7b9e924be2357bc950";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		TbkTpwdCreateRequest req = new TbkTpwdCreateRequest();
		req.setText(text);
		req.setUrl(taoTokenUrl);
		req.setLogo(pic_url);
		String taoToken = "";
		try {
			TbkTpwdCreateResponse rsp = client.execute(req);
			String body = rsp.getBody();
			if(StringUtils.isNotBlank(body)){
				System.out.println(body);
				Map<String, Object> body_map = new Conver().converMap(body);
				Map<String, Object> res_map = (Map<String, Object>) body_map.get("tbk_tpwd_create_response");
				Map<String, Object> data_map = (Map<String, Object>) res_map.get("data");
				taoToken = (String) data_map.get("model");
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		System.out.println(taoToken);
		return taoToken;
	}
	public String itemRecommend(){
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23496927";
		String secret = "e60dd07d064bda7b9e924be2357bc950";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		TbkItemRecommendGetRequest req = new TbkItemRecommendGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url");
		req.setNumIid(528674725498L);
		req.setCount(20L);
		req.setPlatform(1L);
		try {
			TbkItemRecommendGetResponse rsp = client.execute(req);
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String itemDetail(){
//		String url = "http://gw.api.taobao.com/router/rest";
//		String appkey = "23496927";
//		String secret = "e60dd07d064bda7b9e924be2357bc950";
//		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
//		ItemDetailGetRequest req = new ItemDetailGetRequest();
//		req.setParamsString("areaId");
//		req.setItemId("543185214071");
//		req.setFields("item,price,delivery,skuBase,skuCore,trade,feature,props,debug");
//		ItemDetailGetResponse rsp = client.execute(req);
//		System.out.println(rsp.getBody());
		return null;
	}
	public Map<String, Object> itemInfo(String itemId){
		String url = "http://gw.api.taobao.com/router/rest";
		String appkey = "23496927";
		String secret = "e60dd07d064bda7b9e924be2357bc950";
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		TbkItemInfoGetRequest req = new TbkItemInfoGetRequest();
//		req.setNumIids("528674725498");
		req.setNumIids(itemId);
		req.setPlatform(1L);
//		req.setIp("11.22.33.43");
		try {
			TbkItemInfoGetResponse rsp = client.execute(req);
			String body = rsp.getBody();
			if(StringUtils.isNotBlank(body)){
				Map<String, Object> body_map = new Conver().converMap(body);
				Map<String, Object> tbk_item_info_get_response = (Map<String, Object>) body_map.get("tbk_item_info_get_response");
				Map<String, Object> results = (Map<String, Object>) tbk_item_info_get_response.get("results");
				List<Map<String, Object>>  n_tbk_item = (List<Map<String, Object>>) results.get("n_tbk_item");
				Map<String, Object> item = n_tbk_item.get(0);
				return item;
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
