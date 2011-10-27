package com.youku.soku.newext.redis.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.soku.newext.info.AliasInfo;

public class SerializeUtil <V extends Serializable>{

	private Log logger = LogFactory.getLog(this.getClass());
	public byte[] objectToBytes(V value) {
		if(value == null) {
			return null;
		}
		
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream outputStream = null;
		
		try {
			outputStream = new ObjectOutputStream(arrayOutputStream);
			outputStream.writeObject(value);
		} catch(IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(outputStream != null) {
					outputStream.close();
				}
			} catch(IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return arrayOutputStream.toByteArray();
	}
	
	
	public V bytetoObject(byte[] bytes) {
		if(bytes == null || bytes.length == 0) {
			return null;
		}
		
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
			Object obj = inputStream.readObject();
			return (V) obj;
		} catch(IOException e) {
			logger.error(e.getMessage(), e);
		} catch(ClassNotFoundException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if(inputStream != null) {
					inputStream.close();
				}
			} catch(IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		SerializeUtil<AliasInfo> rc = new SerializeUtil<AliasInfo>();
	}
}
