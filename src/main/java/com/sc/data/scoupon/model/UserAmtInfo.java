package com.sc.data.scoupon.model;

public class UserAmtInfo {
	private String user_id;
	private double total_amt;
	private double balance;
	private double	 draw_money;
	private String time;
	
	
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public double getTotal_amt() {
		return total_amt;
	}
	public void setTotal_amt(double total_amt) {
		this.total_amt = total_amt;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public double getDraw_money() {
		return draw_money;
	}
	public void setDraw_money(double draw_money) {
		this.draw_money = draw_money;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String toString() {
		return "UserAmtInfo:{user_id:"+user_id
				+",total_amt:"+total_amt
				+",balance:"+balance
				+",draw_money:"+draw_money
				+",time:"+time+"}";
	}
}
