package com.sc.data.scoupon.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.apache.log4j.Logger;

/**
 * 邮件工具类
 * 
 * @author lhq
 *
 */
public class EmailUtils implements Runnable {

    private final static Logger log = Logger.getLogger("kb.common");
    private static SimpleEmail email = new SimpleEmail();
    public StackTraceElement stacks[];
    public static List<String> emilToList = new ArrayList<String>();

    private StackTraceElement[] getStacks() {
	return stacks;
    }

    private void setStacks(StackTraceElement[] stacks) {
	this.stacks = stacks;
    }

    static {
	newInstance("gbk", "汇分析数据");
    }

    public static void newInstance() {
	newInstance("utf-8", "汇分析数据");
    }

    public static void newInstance(String title) {
	newInstance("utf-8", title);
    }

    private static void huiheNewInstance(String encode, String title) {
    	email = new SimpleEmail();
    	
    	email.setAuthentication("shuju@koolbao.com", "koolma2010");
    	email.setSSL(true);
    	email.setCharset(encode);
		email.setHostName("smtp.exmail.qq.com"); // 服务器
		email.setSmtpPort(465);
    	try {
    		email.setFrom("shuju@koolbao.com", title);
    	} catch (EmailException e) {
    		log.error(e);
    	} // 发送方
    }
    private static void newInstance(String encode, String title) {
	email = new SimpleEmail();
//	rgpqrdhdtdntbgag

//	email.setAuthentication("shuju@koolbao.com", "koolma2010");
	email.setAuthentication("1052680939@qq.com", "rgpqrdhdtdntbgag");
	email.setSSL(true);
	email.setCharset(encode);
	email.setHostName("smtp.qq.com"); // 服务器
	email.setSmtpPort(25);
//	email.setHostName("smtp.exmail.qq.com"); // 服务器
//	email.setSmtpPort(465);
	try {
	    email.setFrom("1052680939@qq.com", title);
//	    email.setFrom("shuju@koolbao.com", title);
	} catch (EmailException e) {
	    log.error(e);
	} // 发送方
    }

    public static boolean multiSend(String title, String msg, String emails , File file) {
    	boolean flag = false;
    	synchronized (msg) {
    		try {
				// 附件
				EmailAttachment attachment = new EmailAttachment();
				attachment.setPath(file.getPath());
				attachment.setDisposition(EmailAttachment.ATTACHMENT);
				attachment.setDescription("点击数据excel文件");
				attachment.setName(file.getName());
				// 邮件
				HtmlEmail  simpleEmail = new HtmlEmail();
				simpleEmail.setHostName("smtp.exmail.qq.com");
				simpleEmail.setSSL(true);
				simpleEmail.setAuthentication("shuju@koolbao.com", "koolma2010");
				simpleEmail.setSmtpPort(465);
				simpleEmail.setFrom("shuju@koolbao.com", "汇分析数据", "utf-8");
				
				String[] email_addr = null;
				if (emails.contains(";")) {
				    email_addr = emails.split(";");
				} else if (emails.contains(",")) {
				    email_addr = emails.split(",");
				} else {
				    email_addr = new String[] { emails };
				}
				for (String e : email_addr) {
					simpleEmail.addTo(e);
				}
				simpleEmail.setCharset("utf-8");
				simpleEmail.setSubject(title);
				simpleEmail.setHtmlMsg("a");
				simpleEmail.attach(attachment);
				   //附件  
//	            if(filepath!=null && filepath.size()>0){  
//	                for(int i=0; i<filepath.size();i++){  
//	                    EmailAttachment attac = new EmailAttachment();  
//	                    attac.setPath(filepath.get(i));  
//	                    multipartemail.attach(attac);  
//	                }  
//	            }  
				simpleEmail.send();
				flag = true;
				System.out.println("发送成功");
			} catch (EmailException e) {
				e.printStackTrace();
			}
    	}
    	return flag;
    }
    public static boolean send(String title, String msg, String emails ) {
	synchronized (msg) {
	    try {
	    	if(StringUtils.isBlank(emails)){
	    		return true;
	    	}
		String[] email_addr = null;
		if (emails.contains(";")) {
		    email_addr = emails.split(";");
		} else if (emails.contains(",")) {
		    email_addr = emails.split(",");
		} else {
		    email_addr = new String[] { emails };
		}
		email.setCharset("utf-8");
		email.setSubject(title);
		for (String e : email_addr) {
			addTo(e);
		}
//		msg = ProgramInfo.GetprogramInfo().addProgramInfo(msg);
		email.setContent(msg, "text/html;charset=utf-8");
		email.send();
	    } catch (Exception e) {
		e.printStackTrace();
		newInstance();
		return false;
	    }
	}
	return true;
    }
    

	/**
     * 添加发送的地址
     * @param to
     */
    public static void addTo(String to){
    	if(StringUtils.isBlank(to)){
    		return ;
    	}
    	for(String item : emilToList){
    		if(to.equals(item)){
    			return ;
    		}
    	}

    	try {
			email.addTo(to);
	    	emilToList.add(to);
		} catch (EmailException e) {
			e.printStackTrace();
		}
    }

    public static void serviceSend(final String title, final String msg,
	    final String addresses) {
	synchronized (msg) {
	    EmailUtils email = new EmailUtils();
	    StackTraceElement stacks[] = (new Throwable()).getStackTrace();
	    email.serviceAddresses = addresses;
	    email.serviceTitle = title;
	    email.serviceMsg = msg;
	    email.serviceStacks = stacks;
	    email.serviceSendAdd = "service@koolbao.com";
	    email.serviceSendpwd = "koolma2010";
	    email.serviceEncode = "utf-8";
	    email.serviceHostName = "smtp.exmail.qq.com";
	    email.serviceForm = "service@koolbao.com";
	    Thread t1 = new Thread(email);
	    t1.start();
	}
	// 发送方
    }

    public static void serviceSend(String title, String msg, String addresses,
	    String serviceSendAdd, String serviceSendpwd, String serviceEncode,
	    String serviceHostName, String serviceForm) {
	synchronized (msg) {
	    EmailUtils email = new EmailUtils();
	    StackTraceElement stacks[] = (new Throwable()).getStackTrace();
	    if (StringUtils.isBlank(addresses)) {
		addresses = "";
	    }
	    if (StringUtils.isBlank(title)) {
		title = "";
	    }
	    if (StringUtils.isBlank(msg)) {
		title = "";
	    }
	    if (StringUtils.isBlank(serviceSendAdd)) {
		serviceSendAdd = "service@koolbao.com";
	    }
	    if (StringUtils.isBlank(serviceSendpwd)) {
		serviceSendAdd = "koolma2010";
	    }
	    if (StringUtils.isBlank(serviceEncode)) {
		serviceEncode = "utf-8";
	    }
	    if (StringUtils.isBlank(serviceHostName)) {
		serviceHostName = "smtp.exmail.qq.com";
	    }
	    if (StringUtils.isBlank(serviceForm)) {
		serviceForm = "service@koolbao.com";
	    }
	    email.serviceAddresses = addresses;
	    email.serviceTitle = title;
	    email.serviceMsg = msg;
	    email.serviceStacks = stacks;
	    email.serviceSendAdd = serviceSendAdd;
	    email.serviceSendpwd = serviceSendpwd;
	    email.serviceEncode = serviceEncode;
	    email.serviceHostName = serviceHostName;
	    email.serviceForm = serviceForm;
	    Thread t1 = new Thread(email);
	    t1.start();
	}
	// 发送方
    }

    private String serviceAddresses = "";
    private String serviceTitle = "";
    private String serviceMsg = "";
    private String serviceSendAdd = "";
    private String serviceSendpwd = "";
    private String serviceEncode = "";
    private String serviceHostName = "";
    private String serviceForm = "";
    private StackTraceElement serviceStacks[] = null;

    public void run() {
	SimpleEmail simpleMail = new SimpleEmail();
	simpleMail.setAuthentication(serviceSendAdd, serviceSendpwd);
	simpleMail.setSSL(true);
	simpleMail.setCharset(serviceEncode);
	simpleMail.setHostName("smtp.exmail.qq.com"); // 服务器
	simpleMail.setSmtpPort(465);
	try {
	    simpleMail.setFrom(serviceForm, serviceTitle);
	    String[] email_addr = null;
	    if (serviceAddresses.contains(";")) {
		email_addr = serviceAddresses.split(";");
	    } else if (serviceAddresses.contains(",")) {
		email_addr = serviceAddresses.split(",");
	    } else {
		email_addr = new String[] { serviceAddresses };
	    }
	    simpleMail.setCharset(serviceEncode);
	    simpleMail.setSubject(serviceTitle);
	    for (String e : email_addr) {
		simpleMail.addTo(e);
	    }
	    String msgs = "";
//	    ProgramInfo info = ProgramInfo.GetprogramInfo();
//	    info.setStack(serviceStacks);
//	    msgs = info.addProgramInfo(serviceMsg);
	    simpleMail.setContent(msgs, "text/html;charset=utf-8");
	    boolean sendIsSuccess = false;
	    int maxCycNum = 10;
	    int cycNum = 0;
	    Exception es = null;
	    while (!sendIsSuccess || cycNum > maxCycNum) {
		try {
		    cycNum++;
		    simpleMail.send();
		    sendIsSuccess = true;
		} catch (Exception e) {
		    es = e;
		    e.printStackTrace();
		    Random r = new Random(10);
		    try {
			Thread.sleep((r.nextInt(60) + 1) * 1000);
		    } catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		}
	    }
	    if (cycNum >= maxCycNum) {
		es.printStackTrace();
	    }
	} catch (EmailException e) {
	    e.printStackTrace();
	    log.error(e);
	}
    }

    public String getServiceAddresses() {
	return serviceAddresses;
    }

    public void setServiceAddresses(String serviceAddresses) {
	this.serviceAddresses = serviceAddresses;
    }

    public String getServiceTitle() {
	return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
	this.serviceTitle = serviceTitle;
    }

    public String getServiceMsg() {
	return serviceMsg;
    }

    public void setServiceMsg(String serviceMsg) {
	this.serviceMsg = serviceMsg;
    }

    public String getServiceSendAdd() {
	return serviceSendAdd;
    }

    public void setServiceSendAdd(String serviceSendAdd) {
	this.serviceSendAdd = serviceSendAdd;
    }

    public String getServiceSendpwd() {
	return serviceSendpwd;
    }

    public void setServiceSendpwd(String serviceSendpwd) {
	this.serviceSendpwd = serviceSendpwd;
    }

    public String getServiceEncode() {
	return serviceEncode;
    }

    public void setServiceEncode(String serviceEncode) {
	this.serviceEncode = serviceEncode;
    }

    public String getServiceHostName() {
	return serviceHostName;
    }

    public void setServiceHostName(String serviceHostName) {
	this.serviceHostName = serviceHostName;
    }

    public String getServiceForm() {
	return serviceForm;
    }

    public void setServiceForm(String serviceForm) {
	this.serviceForm = serviceForm;
    }

    public StackTraceElement[] getServiceStacks() {
	return serviceStacks;
    }

    public void setServiceStacks(StackTraceElement[] serviceStacks) {
	this.serviceStacks = serviceStacks;
    }

    public static void main(String[] args) {
		Random r = new Random();
		for (int i = 0; i < 1; i++){
		    System.out.println("?");
//		    serviceSend("test" + i, "test", "jiajun.yue@huihex.com");
		    send("御膳房数据下载" + i, "呵呵", "1052680939@qq.com");
		 }
		
    }
}
