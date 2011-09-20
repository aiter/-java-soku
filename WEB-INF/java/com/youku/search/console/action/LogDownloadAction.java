package com.youku.search.console.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.Action;
import com.youku.search.console.config.Constants;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.util.MyUtil;

public class LogDownloadAction implements ServletRequestAware{
	
	class Fileinfo{
		String filename;
		String filepath;
		public String getFilename() {
			return filename;
		}
		public String getFilepath() {
			return filepath;
		}
		
	}
	HttpServletRequest request;
	List<Fileinfo> downloadfiles = null;
	String filepath;
	String message;
	String filename;
	public List<Fileinfo> getDownloadfiles() {
		return downloadfiles;
	}
	
	public String getFilename() {
		return filename;
	}

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public InputStream getInputStream() {
		filepath = request.getParameter("filepath");
		String path = MyUtil.getFromBASE64(filepath);
		try {
			File f= new File(path);
			filename = f.getName();
			return new FileInputStream(path);
		} catch (FileNotFoundException e) {
			message = "文件找不到";
			e.printStackTrace();
		}
		return null;
	}
	       
	public String down(){
		LogInfoWriter.operate_log.info("日志下载----下载");
		return Action.SUCCESS;
	}

	
	public String list(){
		File f = new File(Constants.DIRPATH);
		File[] files = null;
		if(f.isDirectory()){
			files = f.listFiles();
		}
		if(null!=files){
			downloadfiles = new ArrayList<Fileinfo>();
			Fileinfo fi = null;
			for(File file:files){
				if(!file.isFile()||!file.getName().endsWith(".zip")) continue;
				fi = new Fileinfo();
				fi.filename = file.getName();
				fi.filepath = MyUtil.getBASE64(file.getPath());
				downloadfiles.add(fi);
			}
		}
		LogInfoWriter.operate_log.info("日志下载----列表");
		return Action.SUCCESS;
	}

	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}

	public HttpServletRequest getRequest() {
		return request;
	}
}
