package com.youku.search.util.mail;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.youku.search.util.mail.MailMessage.Attachment;

public class MailSender {

	public static void send(MailMessage message) throws Exception {

		// smtp属性
		Properties properties = new Properties();
		if (message.isNeedAuth()) {
			properties.put("mail.smtp.auth", "true");
		}

		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setJavaMailProperties(properties);

		sender.setDefaultEncoding("utf-8");

		sender.setHost(message.getHost());
		sender.setPort(message.getPort());

		if (message.isNeedAuth()) {
			sender.setUsername(message.getUsername());
			sender.setPassword(message.getPassword());
		}

		// 邮件内容
		MimeMessageHelper helper = new MimeMessageHelper(sender
				.createMimeMessage(), true);
		helper.setFrom(message.getFrom(), message.getFromname() == null? message.getFrom():message.getFromname() );
		MimeMailMessage mimeMessage = new MimeMailMessage(helper);

//		mimeMessage.setFrom(message.getFrom());
		mimeMessage.setTo(message.getTo());
		if (message.getCc() != null) {
			mimeMessage.setCc(message.getCc());
		}
		mimeMessage.setSubject(message.getSubject());

		helper.setText(message.getText(), message.isHTMLText());

		if (!message.getAttachments().isEmpty()) {
			for (Attachment a : message.getAttachments()) {
				helper.addAttachment(a.filename, a.getDataSource());
			}
		}

		// 发送！
		sender.send(mimeMessage.getMimeMessage());
	}

	public static void send(String email, String title, String content)
			throws Exception {
		MailMessage message = new MailMessage();

		message.setHost("mail.youku.com");
		message.setPort(25);
		message.setNeedAuth(true);
		message.setUsername("luwei");
		message.setPassword("3ahs12hndw");
		message.setFromname("aaaa");
		message.setFrom("luwei@youku.com");
		message.setTo(email);

		message.setSubject(title);

		message.setHTMLText(true);
		message.setText(content);

		System.out.println("开始发送。。。");
		MailSender.send(message);
		System.out.println("完成！");
	}

	public static void main(String[] args) throws Exception {

		try {
			MailSender.send("luwei@youku.com", "创建专辑索引过程中连接数据库失败", "tetstt");
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
}
