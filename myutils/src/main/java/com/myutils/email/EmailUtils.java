package com.myutils.email;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.myutils.properties.PropertiesUtils;

public class EmailUtils {

	public static void sendEmai(String toMail, String mailTitle,
			String mailContent) {
		Properties props = new Properties(); // 可以加载一个配置文件
		// 使用smtp：简单邮件传输协议
		props.setProperty("mail.transport.protocol", "smtp");
		props.put("mail.smtp.host", PropertiesUtils.getString("mail.host"));// 存储发送邮件服务器的信息
		props.put("mail.smtp.auth", "true");// 同时通过验证

		try {
			Session session = Session.getInstance(props);// 根据属性新建一个邮件会话
			//session.setDebug(true); //有他会打印一些调试信息。
			MimeMessage message = new MimeMessage(session);// 由邮件会话新建一个消息对象
			// 设置发件人的地址
			message.setFrom(new InternetAddress(PropertiesUtils.getString("mail.sendFrom")));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(toMail));// 设置收件人,并设置其接收类型为TO
			message.setSubject(mailTitle);// 设置标题
			// 设置信件内容
			// message.setText(mailContent); //发送 纯文本 邮件 todo
			message.setContent(mailContent, "text/html;charset=UTF-8"); // 发送HTML邮件，内容样式比较丰富
			message.setSentDate(new Date());// 设置发信时间
			message.saveChanges();// 存储邮件信息
			// 发送邮件
			// Transport transport = session.getTransport("smtp");
			Transport transport = session.getTransport();
			transport.connect(PropertiesUtils.getString("mail.username"), PropertiesUtils.getString("mail.password"));
			transport.sendMessage(message, message.getAllRecipients());// 发送邮件,其中第二个参数是所有已设好的收件人地址
			transport.close();
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
}
