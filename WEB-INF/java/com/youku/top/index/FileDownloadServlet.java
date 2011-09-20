package com.youku.top.index;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.youku.search.util.DataFormat;
import com.youku.top.LogInfoPrinter;
import com.youku.top.index.db.LogKeywordMgt;

public class FileDownloadServlet extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = LogInfoPrinter.stdlogger;
	
	@Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
	 
		response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream; charset=utf-8");
        String date = request.getParameter("date");
    	
        if(StringUtils.isBlank(date)||!date.matches("\\d{4}_\\d{2}_\\d{2}"))
        	date = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -1), DataFormat.FMT_DATE_YYYY_MM_DD);
        
        String file_name = "keyword_"+date+".txt";
        
        response.addHeader("Content-disposition","attachment; filename="+file_name);
        
        File file = new File(LogKeywordMgt.dir+file_name);
        File lock = new File(LogKeywordMgt.dir+"lock.txt");
        if(!file.exists()){
        	response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "文件没有找到");
        	return;
        }
        if(lock.exists()){
        	response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "文件正在写入中");
        	return;
        }
       
        BufferedOutputStream output = null;
        BufferedInputStream input = null;
        try{
	        output = new BufferedOutputStream(response.getOutputStream());
	        input = new BufferedInputStream(new FileInputStream(LogKeywordMgt.dir+file_name));
	        byte[] buffer = new byte[input.available()];
	        int n = 0;
	        while ((n = input.read(buffer)) > 0) {
	        	output.write(buffer, 0, n);
	        	
	        }
	        response.flushBuffer();
        }catch(Exception e){
        	logger.error(e);
        	response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE, "读取文件出错");
        }finally{
        	if(null!=output)
        		output.close();
        	if(null!=input)
        		input.close();
        }
        
    }
}
