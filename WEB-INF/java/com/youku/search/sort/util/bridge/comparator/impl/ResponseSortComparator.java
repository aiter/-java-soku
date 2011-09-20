package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

/**
 * 
 * @author gaosong
 *
 */
public class ResponseSortComparator extends
		AbstractSortComparator<DefaultResponse> {

	{
		orderFields.put("null", Constant.Sort.SORT_SCORE);
		
		// 当按照以下几种方式排序时，C-Server返回的Score会相应有改变，所以这里也是按照score进行排序
		orderFields.put("createtime", Constant.Sort.SORT_SCORE);
        orderFields.put("total_pv", Constant.Sort.SORT_SCORE);
        orderFields.put("total_comment", Constant.Sort.SORT_SCORE);
        orderFields.put("total_fav", Constant.Sort.SORT_SCORE);
        
        orderFields.put("resort_createtime", Constant.Sort.RESORT_CREATE_TIME);
        orderFields.put("resort_15days", Constant.Sort.RESORT_15DAYS);
	}
	
	public static final ResponseSortComparator I = new ResponseSortComparator();

	@Override
	public Comparator<DefaultResponse> getComparator(int orderField,
			boolean reverse) {
		
		if (reverse) {
			switch (orderField) {
			case Constant.Sort.RESORT_15DAYS:
				return HOT_ORDER_REVERSE;
			
			case Constant.Sort.RESORT_CREATE_TIME:
				return CREATE_TIME_ORDER_REVERSE;
			
			case Constant.Sort.SORT_SCORE:
			default:
				return SCORE_ORDER_REVERSE;

			}
		} else {
			switch (orderField) {
			case Constant.Sort.RESORT_15DAYS:
				return HOT_ORDER;
			
			case Constant.Sort.RESORT_CREATE_TIME:
				return CREATE_TIME_ORDER;
			
			case Constant.Sort.SORT_SCORE:
			default:
				return SCORE_ORDER;

			}
		}
	}

	public final static Comparator<DefaultResponse> SCORE_ORDER = new Comparator<DefaultResponse>() {
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return Float.compare(v1.score, v2.score);
		}
	};

	public final static Comparator<DefaultResponse> SCORE_ORDER_REVERSE = new Comparator<DefaultResponse>() {
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return -1 * SCORE_ORDER.compare(v1, v2);
		}
	};
	
	public final static Comparator<DefaultResponse> CREATE_TIME_ORDER = new Comparator<DefaultResponse>() {
		@Override
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return new Long(v1.createdTime).compareTo(v2.createdTime);
		}
	};
	
	public final static Comparator<DefaultResponse> CREATE_TIME_ORDER_REVERSE = new Comparator<DefaultResponse>() {
		@Override
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return -1 * CREATE_TIME_ORDER.compare(v1, v2);
		}
	};
	
	public final static Comparator<DefaultResponse> HOT_ORDER = new Comparator<DefaultResponse>() {
		@Override
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return Float.compare(v1.score15Days, v2.score15Days);
		}
	};
	
	public final static Comparator<DefaultResponse> HOT_ORDER_REVERSE = new Comparator<DefaultResponse>() {
		@Override
		public int compare(DefaultResponse v1, DefaultResponse v2) {
			return -1 * HOT_ORDER.compare(v1, v2);
		}
	};

}
