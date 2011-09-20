package com.youku.search.index.query.parser;


public class ParserFactory {
	
	private static BaseParser videoParser = new VideoParser();
	
	public static Parser getVideoParser(){
		return videoParser;
	}
	
	public static void main(String[] args){
		
		System.out.println(videoParser.parse("张靓颖 user:优酷音乐"));
		
	}
}
