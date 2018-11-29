package com.sc.data.scoupon.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.sc.data.scoupon.utils.Conver;
import com.sc.data.scoupon.utils.WXQR;


public class AlmmAdzone {
	private Conver c = new Conver();
	private String adzone_url = "https://pub.alimama.com/common/adzone/selfAdzoneCreate.json";
	private String site_url = "https://pub.alimama.com/common/site/generalize/guideAdd.json";
	private String site_info_url = "https://pub.alimama.com/common/site/generalize/guideList.json";
	public String id;
	public String adzone_id;
	public String site_id;
	public String pid;
	public String member_id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getAdzone_id() {
		return adzone_id;
	}

	public void setAdzone_id(String adzone_id) {
		this.adzone_id = adzone_id;
	}

	public String getSite_id() {
		return site_id;
	}

	public void setSite_id(String site_id) {
		this.site_id = site_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}


	@Override
	public String toString() {
		return "AlmmAdzone [adzone_url=" + adzone_url + ", site_url="
				+ site_url + ", id=" + id + ", adzone_id=" + adzone_id
				+ ", site_id=" + site_id + ", member_id=" + member_id
				+ ", toString()=" + super.toString() + "]";
	}

	@SuppressWarnings("unchecked")
	public boolean createADzoneId(String cookie){
		boolean flag = false;
		//检查user_id
		if(StringUtils.isBlank(site_id)){
			System.out.println("siteid can not be null");
			return flag;
		}
		String t =  new Date().getTime()+"";
		//通过cookie 访问 almm 接口 获得ADzoneId
		Map<String, String> map = new HashMap<String, String>();
		map.put("tag", "29");
		map.put("gcid", "8");
		map.put("siteid", this.site_id+"");
		map.put("selectact", "add");
		map.put("newadzonename", "广告位"+t);
		map.put("t",t);
		map.put("_tb_token_", this.getTbToken(cookie));
		map.put("pvid", "");
		try {
			WXQR w = new WXQR();
			String ss = w.httpPostWithOutJson(this.adzone_url, map, cookie);
			String json = w.takeJsonFromStr(ss);
			Map<String , Object> c_adzone_map = c.converMap(json);
			this.adzone_id = (String) ((Map<String , Object>)c_adzone_map.get("data")).get("adzoneId");
			if(StringUtils.isNotBlank(this.adzone_id)){
				this.pid = "mm_"+this.member_id+"_"+this.site_id+"_"+this.adzone_id;
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	public boolean createSiteId(String cookie){
		boolean flag = false;
		String t =  new Date().getTime()+"";
		String _tb_token_ = this.getTbToken(cookie);
		String name = "媒体"+t;
		//通过cookie 访问 almm 接口 获得siteid
		Map<String, String> map = new HashMap<String, String>();
		map.put("name", name);
		map.put("categoryId", "14");
		map.put("account1","微信"+t);
		map.put("selectact", "add");
		map.put("t", t);
		map.put("_tb_token_", _tb_token_);
		map.put("pvid", "");
		try {
			WXQR w = new WXQR();
			String ss = w.httpPostWithOutJson(this.site_url, map, cookie);
			String json = w.takeJsonFromStr(ss);
			Map<String , Object> c_site_map = c.converMap(json);
			boolean ok = (boolean) c_site_map.get("ok");
			if(ok){
				this.getSiteInfo(_tb_token_, name, cookie);
				flag = true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	@SuppressWarnings("unchecked")
	public boolean getSiteInfo(String _tb_token_, String name ,String cookie){
		boolean flag = false;
		String t =  new Date().getTime()+"";
		//通过cookie 访问 almm 接口 获得siteid
		Map<String, String> map = new HashMap<String, String>();
		map.put("_input_charset", "utf-8");
		map.put("t", t);
		map.put("_tb_token_", _tb_token_);
		map.put("pvid", "");
		try {
			WXQR w = new WXQR();
			String ss = w.httpGet(this.site_info_url, map, cookie);
			Map<String , Object> site = c.converMap(new WXQR().takeJsonFromStr(ss));
			List<Map<String, Object>> guideList = (List<Map<String, Object>>) ((Map<String, Object>)site.get("data")).get("guideList");
			for (Map<String, Object> guide : guideList) {
				String guidename = (String) guide.get("name");//"测试111"
				if(name.equals(guidename)){
					System.out.println(guide);
					this.site_id  = guide.get("guideID").toString();
					this.member_id  = guide.get("memberID").toString();
					flag = true;
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return flag;
	}
	
	private String getTbToken(String cookie) {
		String _tb_token_ = null;
		String[] ss = cookie.split(";");
		for (String string : ss) {
			if(string.contains("_tb_token_")){
				_tb_token_ = string.split("=")[1];
			}
		}
		return _tb_token_;
	}

}
