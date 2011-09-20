package com.youku.search.store.pic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.youku.search.pool.net.util.Cost;
import com.youku.search.store.pic.MemCachedPic.StoreResult;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.StringUtil;

/**
 * 将视频的截图导入memcached
 * 
 * @author liuyunjian
 */
public class MemCachedPicLoader {
	
	static class VideoPicInfo {
		public String objectId;
		public float seconds;
		public List<String> thumbs;
	}

	static Log logger = LogFactory.getLog(MemCachedPicLoader.class);
	private static final int once_create_number = 5000; //一次创建500条
	

	/**
	 *  从memcached中的保存的值开始，到最大的pk_video
	 */
	public int loadStart()
	{
		int start = MemCachedPic.getMaxid();
		return loadStart(start,0);
	}
	
	/**
	 * 开始加载
	 * @param start 起始位置
	 * @param end   结束位置
	 */
	public int loadStart(int start,int end)
	{
		int total = 0;
		
		int max = 0;
		
		try {
			max = getMaxId();
			//由于转码需要一定的时间。所以不能直接取最大的，减去5万条数据
			max = max-50000;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;
		}
//		logger.info("load pic to memcached start=" + start + " end=" +end);
		
		end = (end>max||end==0)?max:end;
		
		logger.info("db-5万 maxid:" + max+" start=" + start + " end=" +end +" total="+(end-start));
		
		int starttimes = (start+once_create_number-1)/once_create_number;
		int totaltimes = (end+once_create_number-1)/once_create_number;
		
		logger.info("onetime="+once_create_number+" starttimes="+starttimes + "  totaltimes="+totaltimes);
		
		Cost cost = new Cost();
		
		for (int i=starttimes;i<=totaltimes;i++)
		{
			int count = 0;
			if (i==starttimes){
				count = cacheInfo(start,i*once_create_number>end?end:i*once_create_number);
			}
			else if (i == totaltimes){
				count = cacheInfo((i-1)*once_create_number,end);
			}
			else{
				count = cacheInfo((i-1)*once_create_number,i*once_create_number);
			}
			if (count == -1)
			{
				//数据库异常
				total = -1;
				break;
			}
			else
			{
				total += count;
			}
		}
		
		cost.updateEnd();
		
		if(total==-1){
			logger.error("load pic to memcached  start=" + start + " end=" +end +" error");
		}else {
			MemCachedPic.updateMaxid(end);
			logger.info("load pic to memcached start=" + start + " end=" +end +" total="+total+" cost:"+cost.getCost());
		}
		
		return total;
	}

	
	private int cacheInfo(int start,int end) {

		Exception oneException = null;

		Cost dbCost = new Cost();
		List<VideoPicInfo> list;
		try {
			list = getVideoInfoList(start,end);
		} catch (Exception e1) {
			return -1;
		}
		dbCost.updateEnd();

		final int keywordSize = (list == null) ? 0 : list.size();
		if (logger.isDebugEnabled()) {
			logger.info("ID范围: " + start + " --> " + end + " num:" + keywordSize+";dbcost:"+dbCost.getCost());
		}
		if (keywordSize == 0) {
			return 0;
		}

		int number = 0;
		Cost memCost = new Cost();
		for (VideoPicInfo info:list) {
			
			try {
				String cacheKey = MemCachedPic.cacheKey(info.objectId);
				String infoJson = convertInfo(info);
				StoreResult storeResult = MemCachedPic.cacheSet(cacheKey,infoJson);

				if (storeResult != StoreResult.success) {
					logger.error("存储到memcache发生错误：storeResult = " + storeResult
							+ "; cacheKey = " + cacheKey+ "; cacheValue = " + infoJson);
				}else {
					number++;
				}
			} catch (Exception e) {
				// 如果处理某个关键词发生异常，继续处理其余的
				oneException = e;
				logger.error(e.getMessage(), e);
			}
		}
		memCost.updateEnd();
		if (logger.isDebugEnabled()) {
			logger.info("ID范围: " + start + " --> " + end + " num:" + keywordSize+";memcost:"+memCost.getCost());
		}
		
		if(number!=list.size()){
			logger.error("存储到memcache有错误: " + start + " --> " + end + " num:" + (list.size()-number));
		}

		if (oneException != null) {
//			throw oneException;
			return -1;
		}
		
		return number;
	}


	/**
	 * @param info
	 * @return
	 */
	private String convertInfo(VideoPicInfo info) {
		if(info == null || info.seconds<=0 || info.thumbs == null || info.thumbs.isEmpty()){
			return null;
		}
		
		float start = 0;
		float step = info.seconds/8;
		start = step;
		
		JSONArray jsonArray = new JSONArray();
		for (String thumb : info.thumbs) {
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("t", (int)start+"");
				jsonObject.put("p", thumb);
			} catch (JSONException e) {
			}
			start += step;
			start = (start>info.seconds)?info.seconds:start;
			
			jsonArray.put(jsonObject);
		}
		
//		try {
//			return jsonArray.toString(4);
//		} catch (JSONException e) {
			return jsonArray.toString();
//		}
	}




	private static Connection getConnection() {

		Connection connection = null;
		try {
			connection = Torque.getConnection("video");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return connection;
	}


	/**
	 * 
	 */
	private List<VideoPicInfo> getVideoInfoList(int start,int end) throws Exception {
		Connection conn = getConnection();
		ResultSet rs = null;
		PreparedStatement pStmt = null;

		if (conn == null) {
			logger.info("null database Connection");
			return null;
		}

		try {
			String sql = "select * from t_video where pk_video>="+start+" and pk_video<"+end+" and seconds>0 and is_valid=1";
			List<VideoPicInfo> list = new LinkedList<VideoPicInfo>();
			pStmt = conn.prepareStatement(sql);
			rs = pStmt.executeQuery();
			while (rs.next()) {
				VideoPicInfo info = new VideoPicInfo();
				info.objectId = rs.getString("pk_video");
				info.seconds = rs.getFloat("seconds");
				String thumb = "";
				List<String> thrmbsList = new ArrayList<String>();
				for (int i = 2; i <= 7; i++) {
					thumb = rs.getString("thumb"+i);
					thrmbsList.add(thumb);
				}
				info.thumbs = thrmbsList;
				
				list.add(info);
			}

			if (logger.isDebugEnabled()) {
				StringBuilder builder = new StringBuilder();
				builder.append("sql: " + sql + "; ");

				builder.append("search result: "
						+ (list == null ? 0 : list.size()) + "; ");

				logger.debug(builder.toString());
			}

			return list;

		} finally {
			JdbcUtil.close(rs, pStmt, conn);
		}
	}
	
	/**
	 * 获取t_video中最大的视频ID 
	 */
	private int getMaxId() throws TorqueException
	{
		int result = 0;
		Connection conn = getConnection();
		String sql = "select max(pk_video) as pk_video from t_video";
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("pk_video");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JdbcUtil.close(rs, st, conn);
		}
		
		return result;
	}

	


	public static void main(String[] args) {
//		VideoPicInfo info = new VideoPicInfo();
//		info.objectId="3";
//		info.seconds = 100.67f;
//		List<String> thrmbsList = new ArrayList<String>();
//		for (int i = 1; i <= 8; i++) {
//			thrmbsList.add("0130391F4647A28E68E4660000000062656BCF-EF3E-2C78-057A-250C78E9CCE"+i);
//		}
//		info.thumbs = thrmbsList;
//		
//		
//		MemCachedPicLoader meLoader = new MemCachedPicLoader();
//		
//		System.out.println(meLoader.convertInfo(info));
		
		
		if(args.length<3){
			System.out.println(args.length);
			System.out.println("usage:MemCachedPicLoader $log4j $torque $mem_obj $start $end");
			System.exit(0);
		}
		//启动log4j
		try {
			DOMConfigurator.configure(args[0]);
			System.out.println("log4j-config:"+args[0]);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}
		
		//数据库配置
		try {
            Torque.init(args[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
		System.out.println("Torque init ok! "+args[1]);
		
		//配置memcache
		MemCachedPic.init(args[2]);
		System.out.println("mem_object:"+args[2]);
		
		MemCachedPicLoader picLoader = new MemCachedPicLoader();
		if(args.length==5){
			int start = StringUtil.parseInt(args[3], 0);
			int end = StringUtil.parseInt(args[4], 0);
			if(start>0){
				picLoader.loadStart(start,end);
			}else {
				picLoader.loadStart();
			}
		}else if (args.length==4 && (args[3].startsWith("key_")||args[3].startsWith("max_id"))) {
			if(args[3].startsWith("max_id")){
				System.out.println("memcached: stored_max_id:"+MemCachedPic.getMaxid());
			}else if (args[3].startsWith("key_")) {
				String key = args[3].substring(4);
				String cacheKey = MemCachedPic.cacheKey(key);
				System.out.println("key:"+key+" value:"+MemCachedPic.cacheGet(cacheKey));
			}
		}else {
			picLoader.loadStart();
		}
	}

}
