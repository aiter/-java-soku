package com.youku.search.config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.xml.DOMConfigurator;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.youku.search.index.server.CServer;
import com.youku.search.index.server.CServerManager;
import com.youku.search.index.server.CServerPair;

public class XMLConfigReader extends AbstractXMLConfig {

	public String readString(String path, String defaultValue) {
		Element e = getSingleElement(path);
		if (null != e)
			return e.getTextTrim();
		else
			return defaultValue;

	}
	
	public String readString(String path) {
		return readString(path, "");
	}
	
	@SuppressWarnings("unchecked")
	public List<String> readMultiString(String path) {
		List<String> values = new LinkedList<String>();

		List<Element> list = getMultiElement(path);
		if (null != list && list.size() > 0) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				values.add(element.getTextTrim());
			}
		}

		return values;
	}

	/**
	 * 
	 * 返回配置文件中指定path下含有name/value元素的Map <br>
	 * 
	 * @param path
	 * @return
	 */
	public Map<String, String> readNameValuePair(String path) {
		Map<String, String> result = new HashMap<String, String>();

		List<Element> list = getMultiElement(path);
		if (null != list && list.size() > 0) {
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				result.put(element.element("name").getTextTrim(), element
						.element("value").getTextTrim());
			}
		}

		return result;
	}

	@SuppressWarnings("unchecked")
	public Element getSingleElement(String path) {
		List<Element> list = document.selectNodes(path);
		Element e = null;
		if (null != list && list.size() == 1) {
			e = list.get(0);
		}

		return e;
	}

	@SuppressWarnings("unchecked")
	public List<Element> getMultiElement(String path) {
		List<Element> list = document.selectNodes(path);
		return list;
	}

	public int readInt(String path, int defaultValue) {
		Element e = getSingleElement(path);
		int value = defaultValue;
		try {
			value = Integer.parseInt(e.getTextTrim());
		} catch (NumberFormatException nfe) {
		}

		return value;
	}

	public byte readByte(String path, byte defaultValue) {
		Element e = getSingleElement(path);
		byte value = defaultValue;
		try {
			value = Byte.parseByte(e.getTextTrim());
		} catch (NumberFormatException nfe) {
		}

		return value;
	}
	
	public long readLong(String path, long defaultValue) {
		Element e = getSingleElement(path);
		long value = defaultValue;
		try {
			value = Long.parseLong(e.getTextTrim());
		} catch (NumberFormatException nfe) {
		}

		return value;
	}

	public boolean readBoolean(String path, boolean defaultValue) {
		String value = readString(path);
		if (null != value
				&& (value.equalsIgnoreCase("true") || value
						.equalsIgnoreCase("false"))) {
			return ((value != null) && value.equalsIgnoreCase("true"));
		} else
			return defaultValue;
	}
	
	public void write(String configPath) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		format.setEncoding(this.encoding); // 指定XML编码
		XMLWriter writer = new XMLWriter(new FileWriter(configPath), format);

		writer.write(document);
		writer.close();
	}

}