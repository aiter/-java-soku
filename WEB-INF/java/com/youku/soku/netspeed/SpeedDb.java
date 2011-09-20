package com.youku.soku.netspeed;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.util.DataFormat;
import com.youku.soku.config.Config;
import com.youku.soku.netspeed.IpArea.Region;

public class SpeedDb {
	
	private static Log logger = LogFactory.getLog(SpeedDb.class);
	
	static final IpArea iparea = IpArea.getInstance();
	
	private static TrieTree rootTree = new TrieTree();
	
	protected static int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	
//	public static Map<Integer,Speed> getSpeedMap(String ip,Map<Integer,String> out) throws IOException{
//		
//		if (sites != null && !sites.isEmpty())
//		{
//			Speed speed = null;
//			
//			Region region = iparea.getRegion(ip);
//			if (region != null){
//				
//				Map<Integer,Speed> map = new HashMap<Integer,Speed>();
//				
//				String city = region.getCity_id();
//				String netseg = region.getNetSegCode();
//				String province = region.getProvince_id();
//				
//				Iterator<com.youku.so.om.Site> it = sites.iterator();
//				while (it.hasNext()){
//					com.youku.so.om.Site s = it.next();
//					StringBuilder builder = new StringBuilder();
//					builder.append(s.getName() );
//					
//					String key = getKey(hour + "",s.getId() + "",province,city,netseg);
//					speed = rootTree.search(key,builder);
//					
//					if (speed == null) {
//						builder.append(":<font color=red size=6>" + DefaultSpeed.getSpeed(s.getId()).getValue() +"</font><font color=333333 size=2>(没有找到匹配的网速，返回默认)</font><br/>");
//						speed = DefaultSpeed.getSpeed(s.getId());
//					}
//					out.put(s.getId(), builder.toString());
//					map.put(s.getId(), speed);
//				}
//				
//				
//				return map;
//			}
//		}
//		
//		return DefaultSpeed.getDefaultMap();
//	}
	
	public static Region getRegion(String ip){
		if (iparea != null){
			return iparea .getRegion(ip);
		}
		return null;
	}
	
	public static Speed getSpeedByRegion(Region region,int site) {
		Speed speed = null;
		if (region != null){
			
			String city = region.getCity_id();
			String netseg = region.getNetSegCode();
			String province = region.getProvince_id();
			
			
			String key = getKey(hour + "",site + "",province,city,netseg);
			speed = rootTree.search(key);
			
			if (site == Site.奇艺网.getValue() && speed != null){
				return Speed.valueOf((int)(speed.getValue()*0.8f));
			}
		}
			
		return speed != null ? speed : DefaultSpeed.getSpeed(site);
	}
	/**
	 * 获取指定各站点的网速
	 * @param ip
	 * @param sites
	 * @return
	 */
	public static Map<Integer,Speed> getSpeedMap(String ip,int[] sites) {
		if (iparea == null) return DefaultSpeed.getDefaultMap();
		
		if (sites != null && sites.length>0)
		{
			Speed speed = null;
			
			Region region = iparea.getRegion(ip);
			
			Map<Integer,Speed> map = new HashMap<Integer,Speed>();
			for (int site:sites)	{
				if (!map.containsKey(site)){
					speed = getSpeedByRegion(region,site);
					
					map.put(site, speed);
				}
			}
			
			return map;
		}
		
		return DefaultSpeed.getDefaultMap();
	}
	
	/**
	 * 获取某站点下某播放地址的网速
	 * @param site
	 * @param url
	 * @return
	 */
	public static Speed getDomainSpeed(int site,String url){
		//是特殊站点并且不是主域名，返回默认网速
		if (DefaultSpeed.isSpecialSite(site) && !DefaultSpeed.isMainDomain(site, url)){
			return DefaultSpeed.getDefaultSpeed();
		}
		return null;
	}
	
	
//	public static Speed getSpeedFromDataTest(String ip,int site){
//		Region region = iparea.getRegion(ip);
//		if (region != null){
//			System.out.println("获取城市:"+region);
//			
//			String city = region.getCity_id();
//			String netseg = region.getNetSegCode();
//			String province = region.getProvince_id();
//			String key = getKey(hour + "",site + "",province,city,netseg);
//			return rootTree.search(key);
//		}		
//		return null;
//		
//		
//	}
	
	public static void main(String[] args){
		initDb("");
//		System.out.println(getSpeedFromDataTest("60.210.99.23",14));
	}
	
	public static TrieTree initDb(String DBPath){
		logger.info("开始初始化网速数据库 ");
		
		if (DBPath == null){
			return null;
		}
	
		TrieTree tree = new TrieTree();
		
		InputStream is =  null;
		BufferedReader br = null;
		try {
//			is= new FileInputStream(file);
			FileInputStream fis = new FileInputStream(DBPath);
			br=new BufferedReader(new InputStreamReader(fis));

				String line = null;
				while ((line = br.readLine()) != null) {
					StringTokenizer itr = new StringTokenizer(line, "/,");
					if (itr.countTokens() == 8){
						String domain = itr.nextToken();
						String province = itr.nextToken();
						String city = itr.nextToken();
						String netseg = itr.nextToken();
						String time = itr.nextToken();
						long down = DataFormat.parseLong(itr.nextToken());
						long cost = DataFormat.parseLong(itr.nextToken());
						
						int times = DataFormat.parseInt(itr.nextToken());
						
						int site = SiteMap.getSiteId(domain.trim());
						if (site > 0)
						{
							String key = getKey(time,site+"",province,city,netseg);
							
							Count count = new Count(down,cost,times);
							
							tree.insert(new Node(site,key,count));
						}		
					}
				}
			
				//计算速度
				compute(tree.getRoot());
				
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		logger.info("结束初始化网速数据库 ");
		return tree;
		
	}
	private static void compute(TrieNode root){
		HashMap<String, TrieNode> children =  root.getChild();
		if(children != null && children.size() > 0)
		{
			Iterator<String> it = children.keySet().iterator();
			while (it.hasNext()){
				String key = it.next();
				TrieNode child = children.get(key);
				
				Count data = child.getData();
					
				child.setSpeed(computeSpeed(data));
				
				child.removeData();
				data = null;
				
				compute(child);
			}
		}
	}
	
	private static String getKey(String... keys){
		StringBuilder builder = new StringBuilder();
		for (String k:keys){
			builder.append(k);
			builder.append("_");
		}
		return builder.deleteCharAt(builder.length()-1).toString();
	}
	
	private static Speed computeSpeed(Count count){
		
		double quotiety = getQuotiety(count.getTimes()); //系数
		int speed = (int)( count.getDown()/count.getCost() *quotiety);
		if (speed <= 0){
			return Speed.valueOf(4);
//			speed = 4;
		}
		else if (speed >= 500){
			return Speed.valueOf(10);
//			speed = 10;
		}
		else{
			return Speed.valueOf(5 + speed/100);
//			speed =  5 + speed/100;
		}
		
//		Speed s = new Speed(speed);
//		s.setQuotiety(quotiety);
//		s.setCount(count);
		
//		return s;
		
	}
	/**
	 * 计算系数
	 * @param count
	 * @return
	 */
	private static double getQuotiety(int count){
		if (count < 10)return 0;
//		if(count > 500)return 1.5d;
//		if (count <= 10)return 1d;
//		return Math.sin((Math.PI/2/500)*count)/2 + 1;
		double d= Math.log10(Math.log10(count));
		return Math.max(d, 0.2f);
	}
	
	protected static void updateTree(String filePath){
		TrieTree tmpTree  = rootTree;
		rootTree = SpeedDb.initDb(filePath);
		tmpTree.destroy();
		tmpTree = null;
	}
	
	static class SiteMap{
		static Map<String,Site> sites = new HashMap<String,Site>();
		
		static {
			sites.put("20", Site.优酷网);
			sites.put("40", Site.土豆网);
			sites.put("41", Site.酷6);
			sites.put("42", Site.新浪网);
			sites.put("43", Site.搜狐);
			sites.put("45", Site.奇艺网);
			sites.put("46", Site.乐视网);
			sites.put("50", Site.CNTV);
			sites.put("53", Site.凤凰网);
			sites.put("54", Site.com56网);
			sites.put("55", Site.六间房);
			sites.put("56", Site.激动网);
			sites.put("57", Site.芒果网);
			
			sites.put("59", Site.中关村在线);
			sites.put("61", Site.琥珀网);
			sites.put("63", Site.电影网);
		}
		
		public static int getSiteId(String site_p2p_code){
			Site site = sites.get(site_p2p_code);
			
			return site != null ? site.getValue() : 0;
		}
	}
	
	
}


