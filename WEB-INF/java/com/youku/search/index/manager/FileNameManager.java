package com.youku.search.index.manager;

import java.util.Date;

import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

public class FileNameManager {
	
	public static String generateFileName(IndexFileType type){
		
		String now =  DataFormat.formatDate(new Date(),DataFormat.FMT_DATE_YYYYMMDDHHMMSS);
		StringBuilder builder = new StringBuilder("index-");
		
		builder.append(type.name())
				.append("-")
				.append(now)
				.append("-")
				.append(StringUtil.randomString(5));
		
		System.out.println("generateFileName="+builder);
		
		return builder.toString();
	}
}


