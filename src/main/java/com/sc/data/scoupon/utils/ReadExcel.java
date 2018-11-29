package com.sc.data.scoupon.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
public class ReadExcel {
    public static void main(String[] args) {
        ReadExcel obj = new ReadExcel();
        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下
        File file = new File("D:/excelOrders/20180630/20180630-20180831.xls");
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("商品单价","payPrice");
        keyMap.put("创建时间","createTime");
        keyMap.put("佣金比率","finalDiscountToString");
        keyMap.put("预估收入","tkPubShareFeeString");
        keyMap.put("订单状态","payStatus");
        keyMap.put("商品数","auctionNum");
        keyMap.put("付款金额","realPayFee");
        keyMap.put("补贴比率","tkShareRate");
        keyMap.put("补贴类型","tkShareRateToString");
        keyMap.put("分成比率","shareRate");
        keyMap.put("商品ID","auctionId");
        keyMap.put("订单类型","tkBizTag");
        keyMap.put("效果预估","feeString");
        keyMap.put("来源媒体ID","siteid");
        keyMap.put("佣金金额","finalDiscountFeeString");
        keyMap.put("结算金额","realPayFeeString");
        keyMap.put("商品信息","auctionTitle");
        keyMap.put("类目名称","category");
        keyMap.put("所属店铺","exShopTitle");
        keyMap.put("补贴金额","tkShareRate");
        keyMap.put("广告位ID","adzoneid");
        keyMap.put("收入比率","discountAndSubsidyToString");
        keyMap.put("结算时间","earningTime");
        keyMap.put("掌柜旺旺","exNickName");
        keyMap.put("订单编号","taobaoTradeParentId");
        keyMap.put("成交平台","terminalType");
        keyMap.put("技术服务费比率","tkAlimmShareRate");
        List excelList = obj.readExcel(file,keyMap);
        for (int i = 0; i < excelList.size(); i++) {
        	Map<String, Object> innerMap = (Map<String, Object>) excelList.get(i);
        	System.out.println(innerMap);
//        	for (Map.Entry<String, Object> entry : innerMap.entrySet()) {
//				System.out.println(entry);
//			}
        }
       }
    
    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public List<Map<String, Object>> readExcel(File file,Map<String, String> fieldMap) {
    	try {
    		// 创建输入流，读取Excel
    		InputStream is = new FileInputStream(file.getAbsolutePath());
    		// jxl提供的Workbook类
    		Workbook wb = Workbook.getWorkbook(is);
    		// Excel的页签数量
    		int sheet_size = wb.getNumberOfSheets();
    		for (int index = 0; index < sheet_size; index++) {
    			List<Map<String, Object>> outerList=new ArrayList<Map<String, Object>>();
    			List<String> fieldName=new ArrayList<String>();
    			// 每个页签创建一个Sheet对象
    			Sheet sheet = wb.getSheet(index);
    			// sheet.getRows()返回该页的总行数
    			for (int i = 0; i < sheet.getRows(); i++) {
    				Map<String, Object> innerMap = new HashMap<String, Object>();
    				// sheet.getColumns()返回该页的总列数
    				for (int j = 0; j < sheet.getColumns(); j++) {
    					String cellinfo = sheet.getCell(j, i).getContents();
    					if(cellinfo.isEmpty()){
    						continue;
    					}
    					if(i==0){
    						fieldName.add(j,cellinfo);
    					}else{
    						if(fieldMap.get(fieldName.get(j))!=null)
    							innerMap.put(fieldMap.get(fieldName.get(j)), cellinfo);
    					}
    				}
    				if(innerMap.size()!=0) outerList.add(innerMap);
    			}
    			return outerList;
    		}
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
    	} catch (BiffException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    // 去读Excel的方法readExcel，该方法的入口参数为一个File对象
    public List<Map<String, Object>> readExcel(File file) {
        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();
            for (int index = 0; index < sheet_size; index++) {
                List<Map<String, Object>> outerList=new ArrayList<Map<String, Object>>();
                List<String> fieldName=new ArrayList<String>();
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数
                for (int i = 0; i < sheet.getRows(); i++) {
                	Map<String, Object> innerMap = new HashMap<String, Object>();
                    // sheet.getColumns()返回该页的总列数
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        if(cellinfo.isEmpty()){
                            continue;
                        }
                        if(i==0){
                        	fieldName.add(j,cellinfo);
                        }else{
                        	innerMap.put(fieldName.get(j), cellinfo);
                        }
                    }
                    if(innerMap.size()!=0) outerList.add(innerMap);
                }
                return outerList;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}