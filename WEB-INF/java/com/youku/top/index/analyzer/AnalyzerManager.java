/**
 * 
 */
package com.youku.top.index.analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import com.youku.analyzer.danga.MemCached.MemCachedClient;
import com.youku.analyzer.danga.MemCached.SockIOPool;
import com.youku.top.config.Config;

/**
 * @author william
 *
 */
public class AnalyzerManager {
	private static final String poolname = "test";
	
	private static MyAnalyzer analyzer = new MyAnalyzer(true);
	private static MyAnalyzer analyzer_noformat = new MyAnalyzer(false);
	private static NumberAnalyzer numberAnalyzer = new NumberAnalyzer();
	private static BlankAnalyzer blankAnalyzer = new BlankAnalyzer();
	private static StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
	
	public static MyAnalyzer getMyAnalyzer()
	{
		return analyzer;
	}
	public static MyAnalyzer getMyAnalyzer(boolean format)
	{
		return format?analyzer:analyzer_noformat;
	}
	public static Analyzer getNumberAnalyzer()
	{
		return numberAnalyzer;
	}
	public static Analyzer getBlankAnalyzer()
	{
		return blankAnalyzer;
	}
	public static Analyzer getStandardAnalyzer()
	{
		return standardAnalyzer;
	}
	
	private static SockIOPool pool = null;
	
	public static SockIOPool getAnalysis(String poolname, String fn)
	{
		if (null == pool)
			initAnalysis(poolname,fn);
		return pool;

	}
	
	public static void init()
	{
		initAnalysis(poolname,Config.getAnalyzerServer());
	}
	
	public static synchronized void initAnalysis(String poolname, String fn){
		System.out.println("initAnalysis:"+fn);
		try{
			Vector<String> serverlist = new Vector<String>(5);
			FileReader fr = new FileReader(fn);
			BufferedReader br = new BufferedReader(fr);
			String buf = null;
			while( (buf = br.readLine()) != null )
			{
				buf = buf.trim();
				if( !buf.equals("") ){
					serverlist.add(buf);
				}
			}
			br.close();
			fr.close();
			serverlist.trimToSize();
			if( serverlist.size() <= 0 ){
				return;
			}
			String[] s = serverlist.toArray(new String[0]);

			// initialize the pool for memcache servers
			pool = SockIOPool.getInstance(poolname);
			pool.setServers(s);
			pool.setInitConn(5);
			pool.setMinConn(5);
			pool.setMaxConn(500);
			pool.setMaintSleep(30);
			
			pool.setNagle(false);
			pool.initialize();
			
			System.out.println("pool.initialize() over");
//			com.danga.MemCached.Logger log = com.danga.MemCached.Logger.getLogger( MemCachedClient.class.getName());
//			log.setLevel( 3 );

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static MemCachedClient getMemCachedClient(String poolname)
	{
		if (null == pool)
			initAnalysis(poolname,Config.getAnalyzerServer());
		
		MemCachedClient mc = new MemCachedClient();
		mc.setPoolName(poolname);
		mc.setCompressEnable(false);
		mc.setCompressThreshold(0);
		
		return mc;

	}
	
	private  static String analysis(MemCachedClient mc, String inString){
		Object r = null;
			if(inString != null && inString.length()>0)
				r = mc.tokenize(inString, 4, 0x18, 0);
			else
				r = "";
		return (String)r;
	}

	public static String analyzeWord(String words)
	{
		if (words == null || words.length() == 0)
			return null;
		if (words.length() > 4000)
			words = words.substring(0,4000);
//		char[] w = words.toCharArray();
//		StringBuffer sb =  new StringBuffer();
//		
//		for (int i = 0;i<w.length;i++)
//		{
//			if (Constant.StopWords.getStopSet().contains(w[i]))
//				sb.append(" ");
//			else
//				sb.append(w[i]);
//		}
		MemCachedClient mc = getMemCachedClient(poolname);
		
		String s = analysis(mc, words);
		
		return s;
		
	}
	public static String analyzeNumberWord(String words)
	{
		char[] w = words.toCharArray();
		StringBuffer sb =  new StringBuffer();
		int t = 0;
		for (int i = 0;i<w.length;i++)
		{
			if (w[i]>=48 && w[i]<=57){
				sb.append(" ");
				t++;
			}
			sb.append(w[i]);
		}
		
		if (t == w.length)
			return words;
		return sb.toString();
		
	}
	
	public static String analyzeStandardWord(String words)
	{
		TokenStream ts = standardAnalyzer.tokenStream("",new StringReader(words));
		StringBuffer sb = new StringBuffer();
		Token t = null;
		try {
			while ((t = ts.next()) != null)
			{
				sb.append(t.termText()+" ");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.toString();
	}
}
