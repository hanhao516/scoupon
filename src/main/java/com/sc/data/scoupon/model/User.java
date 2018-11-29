package com.sc.data.scoupon.model;

public class User {
	private String user_id;
	private String user_tel;
	private String password;
	private String user_pic;
	private String create_time;
	private String user_nick;
	private String wx_id;
	private String tb_uid;
	private String ofset;
	private String pageSize;
	private String ident_code;


	public String getIdent_code() {
		return ident_code;
	}

	public void setIdent_code(String ident_code) {
		this.ident_code = ident_code;
	}

	public String getUser_nick() {
		return user_nick;
	}
	public void setUser_nick(String user_nick) {
		this.user_nick = user_nick;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_tel() {
		return user_tel;
	}
	public void setUser_tel(String user_tel) {
		this.user_tel = user_tel;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getUser_pic() {
		return user_pic;
	}
	public void setUser_pic(String user_pic) {
		this.user_pic = user_pic;
	}
	public String getWx_id() {
		return wx_id;
	}
	public void setWx_id(String wx_id) {
		this.wx_id = wx_id;
	}
	
	public String getTb_uid() {
		return tb_uid;
	}
	public void setTb_uid(String tb_uid) {
		this.tb_uid = tb_uid;
	}
	public String getOfset() {
		return ofset;
	}
	public void setOfset(String ofset) {
		this.ofset = ofset;
	}
	public String getPageSize() {
		return pageSize;
	}
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}
	public String toString() {
		return "User:{user_id:"+user_id
				+",user_tel:"+user_tel
				+",password:"+password
				+",user_pic:"+user_pic
				+",create_time:"+create_time
				+",user_nick:"+user_nick
				+",wx_id:"+wx_id+"}";
	}
}
