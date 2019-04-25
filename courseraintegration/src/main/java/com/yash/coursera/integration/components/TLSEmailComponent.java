package com.yash.coursera.integration.components;

import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.yash.coursera.integration.helper.EmailBodyUtil;
import com.yash.coursera.integration.model.EmailContents;

@Component
public class TLSEmailComponent {
	
	@Value("${email.username}")
	private String getEmailFromMailId;

	@Value("${email.password}")
	private String getEmailPassword;
	
	@Value("${email.host}")
	private String getEmailHost;
	
	@Value("${email.port}")
	private String getEmailPort;
	
	@Value("${email.auth}")
	private String getEmailAuthentication;
	
	@Value("${email.starttls.status}")
	private String getEmailTLSStatus;
	
	@Value("${email.debug}")
	private String getEmailDebugStatus;
	
	@Value("${email.sendmails}")
	private String getEmailToMailIds;
	
	
	public Boolean sendEmailForInviteAPIFailure(EmailContents emailContents,Map<String,String> userWithErrorMap){
		Boolean mailSendStaus=false;
		EmailBodyUtil emailBodyUtil=new EmailBodyUtil();
		Properties prop = new Properties();
		prop.put("mail.smtp.host", getEmailHost.trim());
		prop.put("mail.smtp.port", getEmailPort.trim());
		prop.put("mail.smtp.auth", getEmailAuthentication.trim());
		prop.put("mail.smtp.starttls.enable", getEmailTLSStatus.trim());
		prop.put("mail.debug", getEmailDebugStatus.trim());
		
		Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getEmailFromMailId.trim(), getEmailPassword.trim());
			}
		});
		
		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(getEmailFromMailId.trim()));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(getEmailToMailIds.trim()));
					message.setSubject("Coursera User SFTP Status " + emailContents.getCurrentDate());
			String msg = emailBodyUtil.settingEmailBody(emailContents, userWithErrorMap);
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");
			 
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			 
			message.setContent(multipart);
			
			Transport.send(message);
			mailSendStaus=true;

		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return mailSendStaus;
	}
}
