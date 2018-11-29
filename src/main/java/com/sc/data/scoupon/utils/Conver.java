package com.sc.data.scoupon.utils;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * @ClassName: Conver
 * @Description: TODO
 * @author: lhq
 * @date: Dec 28, 2016 4:54:47 PM
 */
public class Conver {

	public static void main(String[] args) throws ParseException {
//		JsonReaderInternalAccess
//		{\"openid\":\"OPENID\","
//				+ "\"nickname\":NICKNAME,"
//				+ "\"sex\":\"1\","
//				+ "\"province\":\"PROVINCE\""
//				+ "\"city\":\"CITY\","
//				+ "\"country\":\"COUNTRY\","
//				+ "\"headimgurl\":\"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46\","
//				+ "\"privilege\":[\"PRIVILEGE1\",\"PRIVILEGE2\"],"
//				+ "\"unionid\":\"o6_bmasdasdsad6_2sgVt7hMZOPfL\"}
//		String paramJson="{\"openid\":\"OPENID\",\"headimgurl\":\"http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46\",\"privilege\":[\"PRIVILEGE1\",\"PRIVILEGE2\"]}";
		String paramJson="{\"openid\":\"oOon30n6z6WT5cdasO61YgObSzbo\",\"nickname\":\"周\",\"sex\":1,\"language\":\"zh_CN\",\"city\":\"杭州\",\"province\":\"浙江\",\"country\":\"中国\",\"headimgurl\":\"http://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTJx9KhMvcuNtwcPHaTsXYZr8cZJWibMLSAFnATMibOOjKcHj8Vye7nUicl5Hb4mLMnhg1S7C6wFiaDz0w/0\",\"privilege\":[]}";
//		String txt = "{\"creative\":{\"creativeId\":\"107509016\",\"creativeSize\":\"0x0\",\"creativeCategoryId\":\"70901\",\"creativeFormat\":4,\"creativeName\":\"1.87\u526f\u672c.flv\",\"apiFramework\":\"1\",\"fileType\":\"flv\",\"creativeUrl\":\"\",\"clickUrl\":\"\",\"duration\":\"\",\"advertiserIds\":\"14741514\",\"clickThroughUrl\":\"\",\"clickTrackUrl\":\"\"}}";
		Conver c = new Conver();
		Map<String, Object> map = c.converMap(paramJson);
		System.out.println(map);
//		map = c.gson.fromJson( txt , new TypeToken<Map<String, Object>>(){}.getType());
//		System.out.println(map.get("creative"));
		long l = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-05-19 13:44:44").getTime();
		System.out.println(l);
	}

	Gson gson = new Gson();

	@SuppressWarnings("unchecked")
	public Map<String, Object> converMap(String txt) {
		try {
			return gson.fromJson(txt,
					(new HashMap<String, Object>()).getClass());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(txt);
		}
		return new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	public <T> T converToBean(String json, T t) {
		try {
			return (T) gson.fromJson(json, t.getClass());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(json);
		}
		return t;
	}

	public String parseJason(Map<String, Object> map) {
		return gson.toJson(map);
	}
	public <T> String parseJson(T t) {
		return gson.toJson(t);
	}
	

	public <T> T convertMap(Class<T> type, Map map)
			throws IllegalAccessException, InstantiationException {
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属�?
			// �?JavaBean 对象的属性赋�?
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					// 下面�?��可以 try 起来，这样当�?��属�?赋�?失败的时候就不会影响其他属�?赋�?�?
					Object value = map.get(propertyName);

					Object[] args = new Object[1];
					args[0] = value;

					descriptor.getWriteMethod().invoke(obj, args);
				}
			}
		} catch (Exception e) {
			System.out.println(map);
			e.printStackTrace();
		}
		return (T) obj;
	}
	public <T> T convertMap(T obj, Map map)
			throws IllegalAccessException, InstantiationException {
		Class<T> type = (Class<T>) obj.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属�?
			// �?JavaBean 对象的属性赋�?
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					// 下面�?��可以 try 起来，这样当�?��属�?赋�?失败的时候就不会影响其他属�?赋�?�?
					Object value = map.get(propertyName);

					Object[] args = new Object[1];
					args[0] = value;

					descriptor.getWriteMethod().invoke(obj, args);
				}
			}
		} catch (Exception  e) {
			System.out.println(map);
			e.printStackTrace();
		}
		return (T) obj;
	}
	

}
