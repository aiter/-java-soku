package com.youku.search.sort.search;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.youku.search.sort.Search;
import com.youku.search.util.Constant;
import com.youku.search.util.Constant.LogCategory;
import com.youku.soku.manage.shield.ShieldInfo;
import com.youku.soku.shield.Filter;
import com.youku.soku.shield.Filter.Source;

/**
 * @author root
 * 
 */
public abstract class AbstractSearch implements Search {

	public static class JSON_STRING {
		public static final String DEL_OK = "{\"del\":\"OK\"}";
		public static final String BAD_WORDS = "{\"total\": -1, \"items\": {} }";
		public static final String ZERO_RESULT = "{\"total\": 0, \"items\": {} }";

		public static final JSONObject OBJECT_DEL_OK;
		public static final JSONObject OBJECT_BAD_WORDS;
		public static final JSONObject OBJECT_ZERO_RESULT;

		static {
			try {
				OBJECT_DEL_OK = new JSONObject(DEL_OK);
				OBJECT_BAD_WORDS = new JSONObject(BAD_WORDS);
				OBJECT_ZERO_RESULT = new JSONObject(ZERO_RESULT);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public enum ShieldChannelTarget {
		VIDEO, FOLDER
	}

	public static final List<Integer> Shield_None = new LinkedList<Integer>();
	public static final List<Integer> Shield_All = new LinkedList<Integer>();

	protected Log logger = LogFactory.getLog(getClass());
	
	protected static final Log statInfoLogger = LogFactory.getLog(LogCategory.ServerStatInfo);
	
	protected int indexPageSize = 50;

	protected boolean needCheckBadWords = true;

	protected String search_name;

	/**
	 * 需要屏蔽的频道类型，如果是null，表示不需要考虑频道因素
	 */
	protected ShieldChannelTarget target;

	public AbstractSearch(String search_name) {
		this(search_name, null);
	}

	public AbstractSearch(String search_name, ShieldChannelTarget target) {
		this.search_name = search_name;
		this.target = target;
	}

	protected ShieldInfo getShieldInfo(String query) {
		ShieldInfo shiedInfo = null;

		if (query != null) {
			try {
				Filter filter = Filter.getInstance();
				shiedInfo = filter.isShieldWord(query, Source.youku);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return shiedInfo;
	}

	/**
	 * 不会返回null，返回 Shield_All 或者需要屏蔽的频道id
	 */
	protected List<Integer> getShieldChannels(String query) {

		ShieldInfo shiedInfo = getShieldInfo(query);

		if (logger.isDebugEnabled()) {
			logger.debug("target: " + target + "; query: " + query
					+ "; shiedInfo: " + shiedInfo);
		}

		if (shiedInfo == null || shiedInfo.isMatched() == false) {
			return Shield_None;
		}

		if (target == null) {
			return Shield_All;
		}

		List<Integer> shieldChannels;
		switch (target) {
		case VIDEO:
			shieldChannels = shiedInfo.getShieldChannel();
			break;

		case FOLDER:
			shieldChannels = shiedInfo.getFolderChannel();
			break;

		default:
			logger.warn("不应该出现这种情况, ShieldChannelTarget: " + target
					+ "; 认为是屏蔽全部频道");
			shieldChannels = null;
			break;
		}

		if (shieldChannels == null || shieldChannels.isEmpty()) {
			return Shield_All;
		}

		return shieldChannels;
	}

}
