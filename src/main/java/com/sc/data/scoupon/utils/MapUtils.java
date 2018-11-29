package com.sc.data.scoupon.utils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;


public class MapUtils {
	
	/**
	 * 把原始Map的id传到KVMap
	 * @param KVMap
	 * @param valueMap
	 */
	public Map<String, Object> transfVal(Map<String, Object> valueMap,Map<String, Object> keyMap){
		Map<String, Object>  returnMap =new HashMap<String, Object>();
		for (Map.Entry<String, Object> property : keyMap.entrySet()) {
			String valueMap_key = (String) property.getValue();
			Object value = valueMap.get(valueMap_key);
			value = value==null||"".equals((value+"").trim())?"0":value+"";
			returnMap.put(property.getKey(), value);
		}
		return returnMap;
	}
	
	public <T> T convertMap(Class<T> type, Map map)
			throws IllegalAccessException, InstantiationException {
		Object obj = type.newInstance(); // 创建 JavaBean 对象
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
					Object value = map.get(propertyName);

					Object[] args = new Object[1];
					args[0] = value;

					descriptor.getWriteMethod().invoke(obj, args);
				}
			}
		} catch (IllegalArgumentException | IntrospectionException
				| InvocationTargetException e) {
			System.out.println(map);
			e.printStackTrace();
		}
		return (T) obj;
	}
	public <T> T convertMap(T obj, Map map)
			throws IllegalAccessException, InstantiationException {
		Class<T> type = (Class<T>) obj.getClass();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
			// 给 JavaBean 对象的属性赋值
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();

				if (map.containsKey(propertyName)) {
					// 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
					Object value = map.get(propertyName);

					Object[] args = new Object[1];
					args[0] = value;

					descriptor.getWriteMethod().invoke(obj, args);
				}
			}
		} catch (IllegalArgumentException | IntrospectionException
				| InvocationTargetException e) {
			System.out.println(map);
			e.printStackTrace();
		}
		return (T) obj;
	}

	/**
	 * 把2个长度相等的数组变成map
	 * @param keyAr
	 * @param valueAr
	 * @return
	 * @throws Exception 
	 */
	public  Map<String, Object> getKeyMap(String[] keyAr,String[] valueAr) throws Exception{ 
		if(keyAr==null || valueAr==null ){
			throw new Exception("keyAr is null or valueAr is null");
		}
		if( keyAr.length != valueAr.length ){
			throw new Exception(" keyAr.length not equal valueAr.length ");
		}
		Map<String, Object> keyMap = new HashMap<String, Object>();
		for (int i = 0; i < keyAr.length; i++) {
			keyMap.put(keyAr[i], valueAr[i]);
		}
		 return keyMap;
	}
}
