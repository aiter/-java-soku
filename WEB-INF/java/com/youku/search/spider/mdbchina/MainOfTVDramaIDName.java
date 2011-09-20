package com.youku.search.spider.mdbchina;

import java.util.List;

import com.youku.search.spider.mdbchina.entity.SimpleTVDrama;
import com.youku.search.spider.util.Reader;

public class MainOfTVDramaIDName {

	static int startPage = 1;
	static int endPage = 11;
//	static String[] baseURL = {
//			"http://www.mdbchina.cn/sections/genres/tv/default",
//			".html?prefix=default&pc=5403&ps=90&page=", };

	static String[] baseURL = {
	"http://www.mdbchina.cn/cmd/combsch.aspx?c=1&t=36&y=&cx=&tx=%u52A8%u6F2B&yx=&s=1&page="
	 };
	
	public final static String destFileName = "e:/simple_tv_drama2.txt";

	public static void main(String[] args) throws Exception {
		for (int i = startPage; i <= endPage; i++) {
			String url = baseURL[0] +  i;

			System.out.println("request: " + url);

			String response = Reader.read(url, "UTF8");
			List<SimpleTVDrama> list = Parser.parseTVId(response);

			Writer.write(list, destFileName);

			Thread.sleep(1000);
		}
    }
}
