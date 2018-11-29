package com.sc.data.scoupon.model;

public class IdentCode {
	private String tel;
	private String day;
	private String active;
	private String identCode;
	
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getIdentCode() {
		return identCode;
	}
	public void setIdentCode(String identCode) {
		this.identCode = identCode;
	}
	public String getActive() {
		return active;
	}
	public void setActive(String active) {
		this.active = active;
	}
	public String toString() {
		return "IdentCode:{tel:"+tel+",day:"+day+",active:"+active+",identCode:"+identCode+"}";
	}
	

}
