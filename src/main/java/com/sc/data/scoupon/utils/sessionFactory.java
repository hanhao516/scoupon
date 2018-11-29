package com.sc.data.scoupon.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class sessionFactory {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}

	private static SqlSessionFactory sqlSessionFactory;

	/**
	 * 
	 * @param spring对象名
	 * @return Object
	 */
	public static SqlSession GetSession() {

		try {
			if (sqlSessionFactory == null) {
				String resource = "Mybatis_configuration.xml";
				InputStream inputStream = null;
				inputStream = Resources.getResourceAsStream(resource);
				sqlSessionFactory = new SqlSessionFactoryBuilder()
						.build(inputStream);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SqlSession sq = sqlSessionFactory.openSession(true);
		return sq;
	}
}
