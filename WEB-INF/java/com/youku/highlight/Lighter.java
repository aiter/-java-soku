package com.youku.highlight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Lighter {
	private static final String PREFIX="<span class=\"highlight\">";
	private static final String SUFFIX="</span>";
	static Set<Character> stopSet = new HashSet<Character>();
	private static char[] stopWords = new char[]{
		'\r','\n',',','.','。','？','！','，','；','：',' '
	};
	
	static {
	    for (int i=0;i<stopWords.length;i++)
	    	stopSet.add(stopWords[i]);
	}
	
	public static final Lighter I = new Lighter();
	
	private Lighter(){
	}
	
	private int getLen(char c){
		return c < 128 ? 1 : 2;
	}
	
	public String getBestString(String str, String key, int len){
		return getBestString(str,key,len,null,null);
	}
	
	public String getBestString(String str, String key, int len, String prefixHL,  String suffixHL){
		SentenceList list = parser(str,key);
		if (prefixHL == null)prefixHL = PREFIX;
		if (suffixHL == null)suffixHL = SUFFIX;
		return getBestString(str,list,len,prefixHL,suffixHL);
	}
	
	public SentenceList parser(String str, String keystr){
		int len = str.length();
		
		SentenceList list = new SentenceList();
		
		String[] keys = keystr.split(" ");
		Key[] arrays = new Key[keys.length];
		for (int i = 0;i< keys.length ; i++){
			arrays[i] =new Key( keys[i]);
		}
		
		Sentence sentence = new Sentence(0);
		int token_start = 0 ;
		int token_len = 0;
		
		for (int i = 0 ; i < len ; i++){
			char c = str.charAt(i);
			boolean hit = false;
queryword:	for (Key key:arrays){ //循环所有查询词
				if (key == null || key.chars.length == 0 || len < i + key.chars.length)
					continue;
				for (int j =0 ;j <key.chars.length ; j++ ){
					if (str.charAt(i+j) != key.chars[j]
                               &&  str.charAt(i+j) != key.chars[j]+32 
                               && str.charAt(i+j) != key.chars[j]-32 
					){
						continue queryword;
					}
				}
				
				if (key.chars[0] > 47 && key.chars[0] < 58 && i > 0){ //数字
					if (str.charAt(i-1) > 47 && str.charAt(i-1) < 58){
						continue queryword;
					}
				}
				
				
				if (token_start != i ){
					Token t = new Token(token_start,i,false,token_len);
					sentence.addHitToken(t);
				}
				
				sentence.addHitToken(new Token(i,i + key.chars.length,true,key.len));
				token_len = 0;
				
				token_start = i + key.chars.length ;
				
				i =  i + key.chars.length -1 ;//还自动+1所以先-1
				
				hit = true;
				break;
			}
			if (!hit)
				token_len += getLen(c);
			//分割句子
			if (stopSet.contains(c)){
				Token t = new Token(token_start,i+1,false,token_len);
				sentence.addHitToken(t);
				sentence.setEnd(i+1);
				
				list.add(sentence);
				sentence = new Sentence(i + 1);
				token_start = i + 1;
				token_len = 0;
			}

			if (i >= len-1 && sentence.getStart() <= i){
				if (token_start < len)
					sentence.addHitToken(new Token(token_start,len,false,token_len));
				
				sentence.setEnd(len);
				list.add(sentence);
			}
		}
		
		return list;
	}
	
	public String getBestString(String str,SentenceList list,int len,String prefixHL, String suffixHL){
		if (list == null || list.size() ==0){
			return null;
		}
		StringBuilder builder = new StringBuilder();
		Collections.sort(list);
		
		for (Sentence sentence:list){
			System.out.println(sentence);
		}
		
		len = len*2;
		
		Sentence sentence = list.get(0);
		
		if (sentence.getHitCount() == 0){
			//没有命中
			int i = 0;
			for ( ;i < str.length() && len > 2; i++){
				char c = str.charAt(i);
				builder.append(c);
				len -= getLen(c);
//				System.out.println(builder + ": " + len);
			}
			
			if (str.length() - i > 2){
				builder.append("..");
			}
			else{
				if (i < str.length())
					builder.append(str.charAt(i));
			}
		}
		else{
			//当前句子
			len = getBestStringFromSentence(str, sentence,len,builder,false, prefixHL,  suffixHL);
			
			//往前找
			if (len > 0){
				while (sentence.hasPrevious() && len>0){
					sentence = sentence.previous;
					len = getBestStringFromSentence(str, sentence,len,builder,true, prefixHL,  suffixHL);
				}
			}
			if (len > 0){
				//往后找
				sentence = list.get(0);
				while (sentence.hasNext() && len>0){
					sentence = sentence.next;
					len = getBestStringFromSentence(str, sentence,len,builder,false, prefixHL,  suffixHL);
				}
			}
		}
			
		return builder.toString();
	}
	
	private int getBestStringFromSentence(
			String str,
			Sentence sentence,
			int len,
			StringBuilder buffer,
			boolean front,
			String prefixHL, String suffixHL
			){
		System.out.println("sentence="+str.substring(sentence.getStart(),sentence.getEnd()) + ",len="+sentence.getLen());
		System.out.println("len="+ len);
//		System.out.println("buffer:"+buffer);
		StringBuilder builder = new StringBuilder();
		if (sentence.getHitCount() == 0){
			if (sentence.getLen() <= len){
				builder.append(str.toCharArray(),sentence.getStart(),sentence.getEnd()-sentence.getStart());
				len -= sentence.getLen();
			}else{
				for (int j=sentence.getStart() ; len > 2 ; j++){
					builder.append(str.charAt(j));
					len -= getLen(str.charAt(j));
				}
				if (len == 1)
					builder.append(".");
				else if (len == 2)
					builder.append("..");
				len = 0;
			}
		}
		else
		{
			int first = sentence.getFirstHit();
			List<Token> tokens = sentence.getHitTokens();
			if (len >= sentence.getLen() ){
				for (Token token : tokens){
					if (token.isHit()){
						builder.append(prefixHL);
						builder.append(str.toCharArray(),token.getStart(),token.getEnd()-token.getStart());
						builder.append(suffixHL);
					}
					else
						builder.append(str.toCharArray(),token.getStart(),token.getEnd()-token.getStart());
				}
				len -= sentence.getLen();
			}
			else{
				for (int i = first ;i < tokens.size() && len>0 ; i++){
					Token t = tokens.get(i);
					
					if (len >= t.getLen() ){
						if (t.isHit()){
							builder.append(prefixHL);
							builder.append(str.toCharArray(),t.getStart(),t.getEnd()-t.getStart());
							builder.append(suffixHL);
						}
						else
							builder.append(str.toCharArray(),t.getStart(),t.getEnd()-t.getStart());
						len -= t.len;
					}
					else{
						if (t.isHit()){
							builder.append("..");
						}
						else{
							for (int j=t.getStart();len>2;j++){
								builder.append(str.charAt(j));
								len -= getLen(str.charAt(j));
							}
							builder.append("..");
						}
						len = 0;
					}
				}
			}
		}
		if (front){
			buffer.insert(0,builder);
		}
		else{
			buffer.append(builder);
		}
		return len;
	}
	
	class Key{
		public Key(String k) {
			this.chars = k.toCharArray();
			
			for(char c:chars){
				len += getLen(c);
			}
		}
		char[] chars;
		int len;
		
	}
	
	public static void main(String[] args){
		
//		String key = "拉丁 女子";
//		String str = "拉丁舞儿童女子单项：R  ";
//		String key = "刘 德华";
//		String str = "刘德华-今天刘德华,大天王";
//		String key = "牛 人 mike   tompkins";
//		String str = "adf Mike ";
//		String key= "张 一山";
//		String str = "张一山做客MSN8：最爱王杰 现场歌曲,最爱王杰 现场演唱歌曲";
		String key= "天天向上";
		String str = "【华翔影视】 天天向上20110715（全场）快乐女声12强集体亮相";
		
//		String key= "拉丁舞儿童女子 ";
//		String str = "张一山做客MSN8：拉丁舞儿童女子单项 最爱王杰 现场演唱歌曲";
		
		System.out.println("key="+key);
		System.out.println("str="+str);
		
		
//		for (Sentence sentence : sentences){
//			System.out.println(sentence);
//		}
		System.out.println(Lighter.I.getBestString(str,key,20));
//		
//		long start = System.currentTimeMillis();
//		for (int i =0;i < 1;i++){
//			String s = Lighter.I.getBestString(str, key, 20, "<div class=\"hl\">", "</div>");
////			String s = Lighter.I.getBastString(str, key, 20);
//			System.out.println(s);
//		}
//		long end = System.currentTimeMillis();
//		
//		System.out.println(end - start);
		
//		Iterator<Sentence> it = sentences.iterator();
//		
//		for (Sentence sentence = null; it.hasNext() ; ){
//			sentence = it.next();
//			List<Token> hits = sentence.getHitTokens();
//			System.out.println(str.substring(sentence.getStart(),sentence.getEnd()) );
//			if(hits != null && hits.size() > 0){
//				System.out.println( "sentence.length:" + sentence.getLen()  +" hitCount:" + sentence.getHitCount()  + "," + Arrays.toString(sentence.getHitTokens().toArray()));
//			}
//			else{
//				System.out.println(" hitCount:0");
//			}
//		}
		
	}
}

class SentenceList extends LinkedList<Sentence>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8553166531791469998L;
	
	private int len;
	
	public boolean add (Sentence sentence){
		if (this.size()>0){
			Sentence last = getLast();
			last.setNext(sentence);
			sentence.setPrevious(last);
		}
		
		this.len += sentence.getLen();
		super.add(sentence);
		return true;
	}

	public int getLen() {
		return len;
	}
}

class Sentence implements Comparable<Object> {
	int start = 0;
	int end = 0;
	int len; //实际占有长度，并非字符数
	
	List<Token> hitTokens;
	Sentence previous ;
	Sentence next;
//	Token firstHit;
	int firstHit = -1;
	int hitCount = 0;
	
	Sentence(int start){
		this.start = start;
	}
	Sentence(int start ,int end){
		this.start = start;
		this.end = end;
	}
	public Token focusToken(int position){
		for (Token t:hitTokens){
			if (position >= t.getStart() && position < t.getEnd()){
				return t;
			}
		}
		return null;
	}
	
	public List<Token> getHitTokens() {
		return hitTokens;
	}
	public String toString(){
		StringBuilder builder = new StringBuilder( "start=" + start + ";end="+end + ";hasNext="+hasNext() + ";len="+len);
		
		if (hitTokens != null){
			builder.append("\n tokens:");
			for (Token t:hitTokens){
				builder.append(t).append("\t");
			}
		}
		
		return builder.toString();
	}
	public void addHitToken(Token token){
		if (hitTokens == null){
			hitTokens = new ArrayList<Token>();
		}
		if (token.isHit()){
			if (firstHit == -1){
				firstHit = hitTokens.size();
			}
			hitCount++;
		}
		
		len += token.len;
		hitTokens.add(token);
	}
	
	public int getHitCount(){
		return hitCount;
	}
	
	
	public int getLen() {
		return len;
	}
	public void setLen(int len) {
		this.len = len;
	}
	public int getFirstHit() {
		return firstHit;
	}
	public void setFirstHit(int firstHit) {
		this.firstHit = firstHit;
	}
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	
	public Sentence getPrevious() {
		return previous;
	}
	public void setPrevious(Sentence previous) {
		this.previous = previous;
	}
	public Sentence getNext() {
		return next;
	}
	public void setNext(Sentence next) {
		this.next = next;
	}
	public boolean hasNext(){
		return next!=null;
	}
	public boolean hasPrevious(){
		return previous!=null;
	}
	public Border isBorder(int i){
		Border b = null;
		for (Token t:hitTokens){
			if (i == t.getStart()){
				return i == t.getEnd()-1 ? Border.BOTH :Border.LEFT;
			}
			else if (i == t.getEnd()-1)
				return Border.RIGHT;
				
		}
		return b;
	}
	
	@Override
	public int compareTo(Object o) {
		Sentence a = (Sentence)o;
		return a.getHitCount() - this.getHitCount() ;
	}
	
	
}

enum Border{
	LEFT,RIGHT,INNER,BOTH,NONE
}
class Token{
	Token(int start,int end,boolean hit,int len){
		this.start = start;
		this.end = end;
		this.hit = hit;
		this.len = len;
	}
	int start;
	int end;
	int len;
	boolean hit;
	
	
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}
	

	public boolean isHit() {
		return hit;
	}

	public void setHit(boolean hit) {
		this.hit = hit;
	}

	public int getLen() {
		return len;
	}

	public void setLen(int len) {
		this.len = len;
	}

	public String toString(){
		return "start:" + start + ",end:"+end + ",hit:"+hit + ",len:" +len;
	}
}


