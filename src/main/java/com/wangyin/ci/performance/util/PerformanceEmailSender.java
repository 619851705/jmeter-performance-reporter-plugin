/**
 * 
 */
package com.wangyin.ci.performance.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.wangyin.ci.performance.entity.EmailGeneralParam;

/**
 * @author wyhubingyin
 * @date 2014年7月15日
 */
public class PerformanceEmailSender {
	
	private  Session session;
	private String addresser;
	
	public PerformanceEmailSender(String username, String password, String addresser,Properties props) {
		init(username,password,addresser,props);
		this.addresser=addresser;
	}

	private void init(final String username, final String password, String addresser,Properties props) {
		session = Session.getInstance(props, new Authenticator() {
        	@Override
        	protected PasswordAuthentication getPasswordAuthentication() {
        		return new PasswordAuthentication(username,password);
        	}
		});
		
	}
	
	
	public synchronized void sendMail(String recipient, String subject, String content,List<File> files) throws Exception {
        	
          
		Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(addresser));
                String[] recipients = recipient.split(";");
                InternetAddress[] recipientAddresses = new InternetAddress[recipients.length];
                for (int i = 0; i < recipients.length; i++) {
                    if (!recipients[i].contains("@")) {
                        recipientAddresses[i] = new InternetAddress(recipients[i] + EmailGeneralParam.EMAIL_DEFAULT_SUFFIX);
                    } else {
                        recipientAddresses[i] = new InternetAddress(recipients[i]);
                    }
                }
                message.setRecipients(RecipientType.TO, recipientAddresses);
           
            DateFormat dfm=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date=new Date();
            message.setSubject(subject+"-"+dfm.format(date));
            message.setSentDate(date);
            Multipart emailMultipart = new MimeMultipart("mixed");
            emailMultipart.addBodyPart(createContent(content,files));
            for(File file:files){
            	 emailMultipart.addBodyPart(createAttachment(file));
            }
           
            message.setContent(emailMultipart);
            message.saveChanges();
            Transport.send(message);
    }

	private MimeBodyPart createAttachment(File fileName) throws Exception {  
	       MimeBodyPart attachmentPart = new MimeBodyPart();  
	        FileDataSource fds = new FileDataSource(fileName);  
	        attachmentPart.setDataHandler(new DataHandler(fds));  
	        attachmentPart.setFileName(MimeUtility.encodeText(fileName.getName(),"UTF-8","B"));  
	        return attachmentPart;  
	    }  


private MimeBodyPart createContent(String body, List<File> files)  
         throws Exception {  
     // 用于保存最终正文部分  
     MimeBodyPart contentBody = new MimeBodyPart();  
     // 用于组合文本和图片，"related"型的MimeMultipart对象  
     MimeMultipart contentMulti = new MimeMultipart("related");  

     // 正文的文本部分  
     MimeBodyPart textBody = new MimeBodyPart();  
     textBody.setContent(body, "text/html;charset=utf-8");  
     contentMulti.addBodyPart(textBody);  

   // 正文的图片部分  
     for(File file:files){
    	 	if(file.getName().endsWith(".png")){
    		 MimeBodyPart jpgBody = new MimeBodyPart();  
             FileDataSource fds = new FileDataSource(file);  
              jpgBody.setDataHandler(new DataHandler(fds));  
              jpgBody.setContentID(file.getName());
              jpgBody.setFileName(MimeUtility.encodeText(file.getName(),"UTF-8","B"));
              contentMulti.addBodyPart(jpgBody);  
    	 	}
     	 
     }

     // 将上面"related"型的 MimeMultipart 对象作为邮件的正文  
     contentBody.setContent(contentMulti);  
     return contentBody;  
}  
	
public static void main(String[] args) throws Exception {
	PerformanceEmailSender performanceEmailSender=new PerformanceEmailSender(EmailGeneralParam.EMAIL_USER, EmailGeneralParam.EMAIL_PASSWORD,EmailGeneralParam.EMAIL_ADDRESSER, EmailGeneralParam.getEmailProperties());
    File file=new File("d:/TestFile");
    List<File> lFiles=new ArrayList<File>();
    for(File f:file.listFiles()){
    	lFiles.add(f);
    }
    StringBuilder sBuilder=new StringBuilder();
	sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_HEAD);
	for(File ff:lFiles){
		if(ff.getName().endsWith(".png")){
		sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_BODYIMG.replace("${cid}", ff.getName()));
		}
	}
	sBuilder.append(EmailGeneralParam.EMAIL_CONTENT_END);
	performanceEmailSender.sendMail("wyhubingyin", "Test", sBuilder.toString(), lFiles);
}
}
