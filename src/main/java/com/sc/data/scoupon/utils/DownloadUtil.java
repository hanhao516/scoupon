package com.sc.data.scoupon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

public class DownloadUtil {
	
	public void downFile(String fileName,String filePath,HttpServletResponse response){
		//1.设置文件ContentType类型，这样设置，会自动判断下载文件类型  
		response.setContentType("application/octet-stream; charset=utf-8");
        //2.设置文件头：最后一个参数是设置下载文件名(假如我们叫a.pdf)  
        response.setHeader("Content-Disposition", "attachment;fileName="+fileName);  
        ServletOutputStream out;  
        //通过文件路径获得File对象(假如此路径中有一个download.pdf文件)  
        File file = new File(filePath + "/" + fileName);  

        try {
			Thread.sleep(1000*10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {  
            FileInputStream inputStream = new FileInputStream(file);  
  
            //3.通过response获取ServletOutputStream对象(out)  
            out = response.getOutputStream();  
  
            int b = 0;  
            byte[] buffer = new byte[512];  
            while (b != -1){  
                b = inputStream.read(buffer);  
                //4.写到输出流(out)中  
                if(b==-1) continue;
                out.write(buffer,0,b);  
            }  

            out.flush(); 
            out.close();  
            inputStream.close();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
	}
}
