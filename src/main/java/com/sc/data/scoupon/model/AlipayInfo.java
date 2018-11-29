package com.sc.data.scoupon.model;

public class AlipayInfo {
	private String user_id;
	private String alipay_user_name;
	private String real_name;
	private String save_time;
	private String run_status;
	
	public final static String using = "1";
	public final static String unused = "0";
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getAlipay_user_name() {
		return alipay_user_name;
	}
	public void setAlipay_user_name(String alipay_user_name) {
		this.alipay_user_name = alipay_user_name;
	}
	public String getSave_time() {
		return save_time;
	}
	public void setSave_time(String save_time) {
		this.save_time = save_time;
	}
	public String getRun_status() {
		return run_status;
	}
	public void setRun_status(String run_status) {
		this.run_status = run_status;
	}
	public String getReal_name() {
		return real_name;
	}
	public void setReal_name(String real_name) {
		this.real_name = real_name;
	}
	public String toString() {
		return "AlipayInfo:{user_id:"+user_id
				+",alipay_user_name:"+alipay_user_name
				+",save_time:"+save_time
				+",real_name:"+real_name
				+",run_status:"+run_status+"}";
	}

}