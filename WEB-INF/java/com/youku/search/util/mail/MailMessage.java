package com.youku.search.util.mail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

public class MailMessage {

	public static class Attachment {
		public String filename;
		public File file;
		public String contentType;

		public DataSource getDataSource() {
			return new DataSource() {

				@Override
				public OutputStream getOutputStream() throws IOException {
					throw new UnsupportedOperationException();
				}

				@Override
				public String getName() {
					return filename;
				}

				@Override
				public InputStream getInputStream() throws IOException {
					return new FileInputStream(file);
				}

				@Override
				public String getContentType() {
					return contentType;
				}
			};
		}
	}

	private String host;
	private int port = 25;
	private boolean needAuth = false;
	private String username;
	private String password;
	private String fromname;
	private String from;
	private String to;
	private String[] cc;

	private List<Attachment> attachments = new ArrayList<Attachment>();

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void addAttachment(String filename, File file) {
		addAttachment(filename, file, null);
	}

	public void addAttachment(String filename, File file, String contentType) {
		Attachment attachment = new Attachment();
		attachment.filename = filename;
		attachment.file = file;
		attachment.contentType = contentType;

		attachments.add(attachment);
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	private String subject;

	private boolean HTMLText = false;
	private String text;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public String getFromname() {
		return fromname;
	}

	public void setFromname(String fromname) {
		this.fromname = fromname;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNeedAuth() {
		return needAuth;
	}

	public void setNeedAuth(boolean needAuth) {
		this.needAuth = needAuth;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public boolean isHTMLText() {
		return HTMLText;
	}

	public void setHTMLText(boolean isHTMLText) {
		this.HTMLText = isHTMLText;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
