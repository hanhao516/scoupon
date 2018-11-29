package com.sc.data.scoupon.utils;

import org.apache.commons.lang.StringUtils;

/**
 * Created by zxy on 2018/3/28.
 */
public class StringShield {

    public  static  String shield(String srcString,int size){

        if(StringUtils.isBlank(srcString)){
            return "**";
        }
        if(srcString.length()<=size){
            return srcString+"**";
        }
        String endString ="*";
        for (int i=size;i<srcString.length()-size;i++){
            endString+="*";
        }

       return srcString.substring(0,size)+endString;

    }


}
