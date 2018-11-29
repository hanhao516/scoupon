package com.sc.data.scoupon;

import org.apache.log4j.Logger;


public class log4jTest {
	private static Logger logger = Logger.getLogger(log4jTest.class); 
	public static void main(String[] args) {
		logger.info("info");
		logger.debug("debug");
		logger.error("error");
	}
}
