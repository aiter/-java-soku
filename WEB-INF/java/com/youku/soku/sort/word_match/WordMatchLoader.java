package com.youku.soku.sort.word_match;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.youku.aword.dict.Dict;
import com.youku.aword.util.StringUtils;
import com.youku.search.util.DataFormat;
import com.youku.soku.sort.word_match.prefix.PrefixMatcher;
import com.youku.soku.zhidaqu.v2.DictManager;
import com.youku.soku.zhidaqu.v2.Element;
import com.youku.soku.zhidaqu.v2.TokenType;

public class WordMatchLoader {

	static Log logger = LogFactory.getLog(WordMatchLoader.class);

	public static final String dict_movie = "movie.txt";
	public static final String dict_teleplay = "teleplay.txt";
	public static final String dict_anime = "anime.txt";
	public static final String dict_variety = "variety.txt";
	public static final String dict_person = "person.txt";
	public static final String dict_sport = "sports.txt";
	public static final String dict_education = "science.txt";
	public static final String dict_modifiers = "modifiers.dict";

	String dir;

	/**
	 * 如果dir为null，就从classpath中加载数据；否则，就从给定的绝对路径开始加载数据
	 */
	public WordMatchLoader(String dir) {
		this.dir = dir;
	}

	/**
	 * 加载词语匹配字典
	 */
	public Dict load(PrefixMatcher prefixMatcher) throws Exception {
		return loadDict(prefixMatcher);
	}

	Dict loadDict(PrefixMatcher prefixMatcher){

		DictManager dm = new DictManager(new Dict());

		long start = System.currentTimeMillis();
		loadMovieFromFile(dm ,prefixMatcher, dict_movie, TokenType.MOVIE ,false);
		loadMovieFromFile(dm ,prefixMatcher, dict_teleplay, TokenType.TELEPLAY ,false);
		loadMovieFromFile(dm ,prefixMatcher, dict_anime, TokenType.ANIME ,false);
		loadMovieFromFile(dm ,prefixMatcher, dict_variety, TokenType.VARIETY ,false);
		loadMovieFromFile(dm ,prefixMatcher, dict_sport, TokenType.SPORT ,false);
		loadMovieFromFile(dm ,prefixMatcher, dict_education, TokenType.EDUCATION ,false);

		loadPersonFromFile(dm , dict_person);
		loadModifiersFromFile(dm , dict_modifiers);
		
		System.out.println("load finished in "
				+ (System.currentTimeMillis() - start) + " ms");

		return dm.getDict();
	}

	/**
	 * 
	 * @param dictManager
	 * @param dict_file
	 * @param type
	 * @param onlyRight true表示只读取有版权的节目
	 * @throws Exception
	 */
	public void loadMovieFromFile(DictManager dictManager,PrefixMatcher prefixMatcher, String dict_file,
			TokenType type , boolean onlyRight) {

		BufferedReader reader = null;
		int wordCount = 0;

		try {
			logger.info("开始加载字典文件: " + dict_file + "...");

			reader = openReader(dict_file);

			String line = null;
			int cnt=0;
			while ((line = reader.readLine()) != null) {
				cnt++;
				
				String[] arr = line.split("\t");
				if (arr.length==0){
					continue;
				}
				String word = arr[0];
				word = StringUtils.trimSpaces(word);
				if(word==null || word.isEmpty()){
					System.out.println(line);
					continue;
				}
				
				boolean hasRight = false;
				int releaseDate = 0;
				int programId = 0;
				int series = 0;
				
				try{
					if (arr.length >=2 )hasRight = DataFormat.parseInt(arr[1])==1;
					if (arr.length >=3 )releaseDate = formatReleaseDate(arr[2]);
					if (arr.length >=4 )programId = DataFormat.parseInt(arr[3]);
					if (arr.length >=5 )series = DataFormat.parseInt(arr[4]);
				}catch(Exception e){
					logger.error(e);
				}
				
				if (onlyRight && !hasRight){
					continue;
				}
				
				Element element = Element.valueOf(type, hasRight,releaseDate,programId,series);
				
				dictManager.addMovie(word, element);
				
				if ( prefixMatcher!= null)prefixMatcher.addProgram(word, element);
				
				wordCount++;
			}


			logger.info("加载字典完成: " + dict_file + "; words: " + wordCount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}
	}
	private int formatReleaseDate(String date){
		if (date!=null)date = date.replace("-", "");
		try{
			int d = DataFormat.parseInt(date);
			if (d < 10000)
				d = d* 10000;
			
			return d;
		}catch(Exception e){
			return 0;
		}
	}
	public void loadPersonFromFile(DictManager dictManager, String dict_file)
		{

		BufferedReader reader = null;
		int wordCount = 0;

		try {
			logger.info("开始加载字典文件: " + dict_file + "...");
			reader = openReader(dict_file);
			String line = null;
			while ((line = reader.readLine()) != null) {
				String[] arr = line.split("\t");
				if (arr.length==0)
					return;
				String word = arr[0];
				int releaseDate = 0;
				
				try{
					if (arr.length >=3 )releaseDate = formatReleaseDate(arr[2]);
					
					dictManager.addPerson(word,releaseDate);
				}catch(Exception e){
					logger.error(e);
				}
					
			}

			logger.info("加载字典完成: " + dict_file + "; words: " + wordCount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}
	}

	public void loadModifiersFromFile(DictManager dictManager, String dict_file)
			 {

		BufferedReader reader = null;
		int wordCount = 0;

		try {
			logger.info("开始加载字典文件: " + dict_file + "...");

			dictManager.loadModifiers(new FileInputStream(
					new File(dir, dict_file)));

			logger.info("加载字典完成: " + dict_file + "; words: " + wordCount);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeReader(reader);
		}
	}
	
	BufferedReader openReader(String dict_file) throws Exception {

		InputStream inputStream;

		if (dir == null) {
			logger.info("从classpath中加载数据: " + dict_file);
			inputStream = WordMatchLoader.class.getResourceAsStream(dict_file);
		} else {
			File file = new File(dir, dict_file);
			logger.info("从文件系统中加载数据: " + file.getAbsolutePath());
			inputStream = new FileInputStream(file);
		}

		return new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
	}

	void closeReader(Reader reader) {
		try {
			if (reader != null) {
				reader.close();
			}
		} catch (Exception e) {
			logger.error("关闭reader发生异常", e);
		}
	}

}
