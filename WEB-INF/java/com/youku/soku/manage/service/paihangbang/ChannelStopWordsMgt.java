package com.youku.soku.manage.service.paihangbang;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;

import com.workingdogs.village.Record;
import com.youku.search.util.DataFormat;
import com.youku.soku.library.Utils;
import com.youku.soku.manage.bo.paihangbang.ChannelStopWordsVO;

public class ChannelStopWordsMgt {
private static Logger logger = Logger.getLogger(ChannelStopWordsMgt.class);
	
	private static ChannelStopWordsMgt instance = null;
	private ChannelStopWordsMgt(){
		super();
	}
	
	
	public synchronized static ChannelStopWordsMgt getInstance() {
		if(null==instance)
			instance = new ChannelStopWordsMgt();
		return instance;
	}
	
	public ChannelStopWordsVO getStopWordsByCate(int cate){
		ChannelStopWordsVO stop = null;
		try {
			List<Record> records = BasePeer.executeQuery("select * from channel_stop_words where cate="+cate,"new_soku_top");
			if(null!=records&&records.size()>0){
				Record r = records.get(0);
				stop = new ChannelStopWordsVO();
				stop.setId(r.getValue("id").asInt());
				stop.setCate(r.getValue("cate").asInt());
				String blocked_str = r.getValue("blocked_words").asString();
				if(!StringUtils.isBlank(blocked_str)&&!blocked_str.trim().toLowerCase().equalsIgnoreCase("null")){
					Set<String> blockeds = Utils.parseStr2Set(blocked_str, "\\|");
					if(null!=blockeds&&blockeds.size()>0)
						stop.getBlocked_words().addAll(blockeds);
				}
				String deleted_str = r.getValue("deleted_words").asString();
				if(!StringUtils.isBlank(deleted_str)&&!deleted_str.trim().toLowerCase().equalsIgnoreCase("null")){
					Set<String> deleteds = Utils.parseStr2Set(deleted_str, "\\|");
					if(null!=deleteds&&deleteds.size()>0)
						stop.getDeleted_words().addAll(deleteds);
				}
				stop.setCreateTime(r.getValue("create_date").asUtilDate());
				return stop;
			}
		} catch (Exception e) {
			logger.error("cate:"+cate,e);
		}
		return stop;
		
	}
	
	public int deleteById(int id){
		try {
			return BasePeer.executeStatement("delete from channel_stop_words where id="+id,"new_soku_top");
		} catch (TorqueException e) {
			logger.error("id:"+id,e);
		}
		return 0;
	}
	
	public int updateStopWords(int id,String column,Set<String> words){
		String stopwords = Utils.parse2Str(words);
		if(StringUtils.isBlank(stopwords))
			stopwords = null;
		else
			stopwords = "'"+stopwords.trim()+"'";
		try {
			return BasePeer.executeStatement("update channel_stop_words set "+column+" = "+stopwords+" where id="+id,"new_soku_top");
		} catch (TorqueException e) {
			logger.error("id:"+id+",column:"+column+",words:"+stopwords,e);
		}
		return 0;
	}
	
	public int insertStopWords(int cate,String column,Set<String> words){
		String stopwords = Utils.parse2Str(words);
		if(StringUtils.isBlank(stopwords))
			stopwords = null;
		else
			stopwords = "'"+stopwords.trim()+"'";
		String date = DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
		try {
			return BasePeer.executeStatement("insert into channel_stop_words (cate,"+column+",create_date) values("+cate+","+stopwords+",'"+date+"')","new_soku_top");
		} catch (TorqueException e) {
			logger.error("cate:"+cate+",column:"+column+",words:"+stopwords,e);
		}
		return 0;
	}
	
}
