package com.sc.data.scoupon.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

public class PageUtils {
	public static void main(String[] args) throws IOException {
	}
	
	public static void pageLimitSet(String pageNo, String pageSize, Map<String, Object> param) {
        if (pageNo == null || pageSize == null)
            return;
        int pn = Integer.parseInt(pageNo);
        int ps = Integer.parseInt(pageSize);
        int down = ps * (pn - 1);
        param.put("pageSize", pageSize);
        param.put("ofset", down + "");
    }
}
