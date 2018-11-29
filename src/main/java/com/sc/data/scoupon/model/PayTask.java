package com.sc.data.scoupon.model;

public class PayTask {
	private String user_id;
	private String task_id;
	private double money;
// 			交易类型: 
// 		1. 20号打钱 
//		2. 分享收入 
// 		3. 提现中
//		4. 提现完成
	private String type;
	private String time;
	private String note;
	private String reject;
	private String alipay_user_name;
	
	public final static String hasSend = "1";
	public final static String preSend = "0";
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getTask_id() {
		return task_id;
	}
	public void setTask_id(String task_id) {
		this.task_id = task_id;
	}
	public double getMoney() {
		return money;
	}
	public void setMoney(double money) {
		this.money = money;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getAlipay_user_name() {
		return alipay_user_name;
	}
	public void setAlipay_user_name(String alipay_user_name) {
		this.alipay_user_name = alipay_user_name;
	}
	public String getReject() {
		return reject;
	}
	public void setReject(String reject) {
		this.reject = reject;
	}
	public String toString() {
		return "PayTask:{user_id:"+user_id
				+",task_id:"+task_id
				+",money:"+money
				+",type:"+type
				+",time:"+time
				+",note:"+note
				+",reject:"+reject
				+",alipay_user_name:"+alipay_user_name+"}";
	}
}
