package com.youku.search.sort.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.index.entity.DefaultResponse;
import com.youku.search.index.entity.Folder;
import com.youku.search.index.entity.PrimaryKey;
import com.youku.search.index.entity.Video;
import com.youku.search.util.StringUtil;

/**
 * 视频去重
 */
public class DuplicateUtil {

	static Log logger = LogFactory.getLog(DuplicateUtil.class);

	static ThreadLocal<AtomicLong> lastSequence = new ThreadLocal<AtomicLong>();

	/**
	 * 给定一个视频列表list，将此list中的有相同md5的视频去重，结果list中只取第一个出现的视频。<br>
	 * 只取maxDuplicateCount个不重复的作为结果 <br>
	 * 注意：因为重复结果只取第一个出现的，list可能需要先排序。<br>
	 * 
	 * @param list
	 * @param maxDuplicateCount
	 * @return
	 */
	public static <R> List<R> remove(List<R> list, int maxDuplicateCount) {
		
		List<R> duplicateResult = new ArrayList<R>();
		if (null == list) {
			return duplicateResult;
		}
		
		Set<String> md5Set = new HashSet<String>();
		for (R result : list) {
			String md5 = getMd5(result);

			if (md5 == null || md5.trim().length() == 0) {
				continue;
			}
			
			if (md5Set.contains(md5)) {
				continue;
			} else {
				md5Set.add(md5);
				duplicateResult.add(result);
				if (duplicateResult.size() >= maxDuplicateCount) {
					break;
				}
			}
		}
		
		return duplicateResult;
	}
	
	/**
	 * 给定一个专辑列表list，将此list中的有相同md5的专辑去重，结果list中只取第一个出现的专辑。
	 * 
	 * 给定一个视频列表list，将此list中的有相同md5的视频去重，结果list中只取第一个出现的视频。
	 * 
	 * 注意：因为重复结果只取第一个出现的，list可能需要先排序。
	 * 
	 */
	public static <R> DuplicateResult<R> remove(List<R> list) {

		DuplicateResult<R> duplicateResult = new DuplicateResult<R>();

		if (list == null) {
			return duplicateResult;
		}
		
		Map<String, Integer> md5Map = duplicateResult.md5Map;
		Map<R, Integer> objectMap = duplicateResult.objectMap;
		Map<R, List<R>> objectsMap = duplicateResult.objectsMap;
		Map<String, R> md5ObjectMap = new HashMap<String, R>();

		for (R result : list) {
			String md5 = getMd5(result);

			if (md5 == null || md5.trim().length() == 0) {
				continue;
			}

			int newCount;
			if (md5Map.containsKey(md5)) {
				newCount = md5Map.get(md5) + 1;
			} else {
				newCount = 1;
				duplicateResult.result.add(result);
				md5ObjectMap.put(md5, result);
			}

			md5Map.put(md5, newCount);
			objectMap.put(md5ObjectMap.get(md5), newCount);

			if (newCount > 1) {
				List<R> objects = objectsMap.get(md5ObjectMap.get(md5));
				if (objects == null) {
					objects = new ArrayList<R>();
					objectsMap.put(md5ObjectMap.get(md5), objects);
				}
				objects.add(result);
			}
		}

		return duplicateResult;
	}

	/**
	 * 把给定的列表考虑去重，然后重排序
	 */
	public static <R> List<R> resort(List<R> list) {

		DuplicateResult<R> duplicateResult = remove(list);

		List<R> newList = new LinkedList<R>();
		List<R> dupList = new LinkedList<R>();
		for (R t : duplicateResult.result) {
			newList.add(t);

			List<R> dupObjects = duplicateResult.objectsMap.get(t);
			if (dupObjects != null) {
				dupList.addAll(dupObjects);
				if (logger.isDebugEnabled()) {
					logger.debug("--------------- 存在重复，当前MD5=" + getMd5(t) + ", 重复个数=" + dupObjects.size());
				}
			}
		}
		newList.addAll(dupList);

		return newList;
	}

	/**
	 * 给定一个对象，返回它的MD5字符串
	 */
	public static String getMd5(Object object) {

		if (object == null) {
			return null;
		}

		if (object instanceof PrimaryKey) {
			return String.valueOf(((PrimaryKey) object).md5);
		}
		
		if (object instanceof DefaultResponse) {
			return getMd5((DefaultResponse) object);
		}

		if (object instanceof Video) {
			return getMd5((Video) object);
		}

		if (object instanceof Folder) {
			return getMd5((Folder) object);
		}

		if (object instanceof com.youku.soku.index.entity.Video) {
			return getMd5((com.youku.soku.index.entity.Video) object);
		}

		throw new IllegalArgumentException("未知的对象类型：" + object.getClass()
				+ "; " + object);
	}
	
	public static String getMd5(DefaultResponse response){
		if (response == null) {
			return null;
		}
		
		return response.md5;
	}

	/**
	 * 给定一个Video对象，返回它的MD5字符串
	 */
	public static String getMd5(Video video) {

		if (video == null) {
			return null;
		}

		return video.md5;
	}

	/**
	 * 给定一个Folder对象，返回“它的”MD5字符串
	 */
	public static String getMd5(Folder folder) {

		if (folder == null || folder.video_md5 == null
				|| folder.video_md5.length == 0) {
			return null;
		}

		List<String> md5List = new ArrayList<String>();
		for (String md5 : folder.video_md5) {
			md5List.add(StringUtil.filterNull(md5));
		}

		Collections.sort(md5List);

		md5List.add(0, folder.owner + "");// 考虑用户id

		String md5 = Arrays.toString(md5List.toArray());
		return md5;
	}


	/**
	 * 给定一个com.youku.soku.index.Video对象，返回它的MD5字符串
	 */
	public static String getMd5(com.youku.soku.index.entity.Video video) {

		if (video == null) {
			return null;
		}

		String md5 = video.title + "\t" + video.seconds;
		return md5;
	}

	/**
	 * result：去重后的列表
	 */
	public static class DuplicateResult<T> {

		/**
		 * 去重后的结果，没有重复的对象
		 */
		public List<T> result = new ArrayList<T>();

		/**
		 * Map<md5值, 重复数目>
		 */
		public Map<String, Integer> md5Map = new HashMap<String, Integer>();

		/**
		 * Map<对象, 重复数目>
		 */
		public Map<T, Integer> objectMap = new HashMap<T, Integer>();

		/**
		 * Map<对象, 重复对象列表>
		 */
		public Map<T, List<T>> objectsMap = new HashMap<T, List<T>>();
		
		/**
		 * 返回去重后所有的重复对象List
		 * 
		 * @return
		 */
		public List<T> getDuplicateList(){
			List<T> duplicateList = new ArrayList<T>();
			
			Collection<List<T>> duplidateResults = objectsMap.values();
			if (null != duplidateResults && duplidateResults.size() > 0) {
				for (List<T> list : duplidateResults) {
					if (null != list && list.size() > 0) {
						duplicateList.addAll(list);
					}
				}
			}
			
			return duplicateList;
		}
		
		/**
		 * 返回去重后指定的重复对象List <br>
		 * 和compareResults中每个对象进行比较，如果存在重复则返回那些重复的结果
		 * 
		 * @param compareResults 比较集合，注意它必须是当前DuplicateResult.result的子集
		 * @return
		 */
		public List<T> getDuplicateList(List<T> compareResults) {
			List<T> duplicateList = new ArrayList<T>();
			
			for (T c : compareResults) {
				List<T> list = objectsMap.get(c);
				if (null != list && list.size() > 0) {
					duplicateList.addAll(list);
				}
			}
			
			return duplicateList;
		}
	}

	/**
	 * 返回一个唯一的、伪造的MD5值
	 */
	public static String getUniqueRandomMD5() {

		AtomicLong atomicLong = lastSequence.get();
		if (atomicLong == null) {
			atomicLong = new AtomicLong();
			lastSequence.set(atomicLong);
		}

		return "md5.unique.random." + atomicLong.incrementAndGet();
	}

	public static void main(String[] args) {
	}

}
