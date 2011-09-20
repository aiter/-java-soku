package com.youku.search.config;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

public abstract class AbstractXMLConfig {

	protected Document document = null;

	protected SAXReader reader = new SAXReader();

	protected String encoding = "UTF-8";

	public AbstractXMLConfig() {
		reader.setEncoding(this.encoding);
	}
	
	public Document getDocment(){
		return document;
	}
	
	public void setReaderEncoding(String encoding) {
		this.encoding = encoding;
		reader.setEncoding(encoding);
	}
	
	public void load(String file) throws DocumentException {
		load(new File(file));
	}
	
	public void load(File file) throws DocumentException {
		document = reader.read(file);
	}

	public void load(Reader source) throws DocumentException {
		try {
			document = reader.read(source);
		} finally {
			IOUtils.closeQuietly(source);
		}
	}

	public void load(InputStream source) throws DocumentException {
		try {
			document = reader.read(source);
		} finally {
			IOUtils.closeQuietly(source);
		}
	}
	
	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	
}
