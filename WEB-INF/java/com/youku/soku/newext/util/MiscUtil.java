package com.youku.soku.newext.util;


//import static com.youku.search.util.StringUtil.filterNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.torque.util.Criteria;

import com.youku.soku.library.load.Series;
import com.youku.soku.library.load.Programme;
//import com.youku.soku.library.orm.Person;
//import com.youku.soku.library.orm.PersonPeer;
//import com.youku.soku.library.orm.PersonRolePeer;
//import com.youku.soku.library.orm.PersonSubject;
//import com.youku.soku.library.orm.PersonSubjectPeer;

public class MiscUtil {

	/**
	 * 返回给定Programme的名字，别名
	 */
	public static Set<String> getAllNames(Programme series) {

		Set<String> all = new LinkedHashSet<String>();

		if (series == null) {
			return all;
		}

		putIfHasText(series.getName(), all);
//		all.addAll(split(series.getAlias()));

		all = toLowerCase(all);

		return all;
	}

	/**
	 * 返回给定Person的名字，别名
	 */
//	public static Set<String> getAllNames(Person person) {
//
//		Set<String> all = new LinkedHashSet<String>();
//
//		if (person == null) {
//			return all;
//		}
//
//		putIfHasText(person.getName(), all);
//		all.addAll(split(person.getAlias()));
//
//		all = toLowerCase(all);
//
//		return all;
//	}

	private static void putIfHasText(String input, Set<String> set) {
		input = (input == null) ? "" : input.trim();
		if (input.length() > 0) {
			set.add(input);
		}
	}

	/**
	 * 返回（名字 + 版本名字）的所有组合
	 */
//	public static Set<String> getAllNames(Names names, String versionName) {
//
//		versionName = filterNull(versionName).trim();
//
//		Set<String> all = new HashSet<String>();
//
//		if (versionName.length() > 0) {
//			Set<String> namesSet = getAllNames(names);
//			for (String name : namesSet) {
//				all.add(name + versionName);
//				all.add(name + " " + versionName);
//
//				all.add(versionName + name);
//				all.add(versionName + " " + name);
//			}
//		}
//
//		all = toLowerCase(all);
//
//		return all;
//	}

	/**
	 * 返回（名字 + 版本名字 + 版本别名）的所有组合
	 */
//	public static Set<String> getAllNames(Names names, String versionName,
//			String versionAlias) throws Exception {
//
//		versionName = filterNull(versionName).trim();
//		versionAlias = filterNull(versionAlias).trim();
//
//		Set<String> versionNames = new HashSet<String>();
//		if (versionName.length() == 0 && versionAlias.length() > 0) {
//			versionNames.add(versionAlias);
//		}
//		if (versionName.length() > 0 && versionAlias.length() == 0) {
//			versionNames.add(versionName);
//		}
//		if (versionName.length() > 0 && versionAlias.length() > 0) {
//			versionNames.add(versionName + versionAlias);
//			versionNames.add(versionName + " " + versionAlias);
//
//			versionNames.add(versionAlias + versionName);
//			versionNames.add(versionAlias + " " + versionName);
//		}
//
//		// ok
//		Set<String> all = new HashSet<String>();
//		if (versionNames.size() > 0) {
//			Set<String> namesSet = getAllNames(names);
//			for (String name : namesSet) {
//				for (String verName : versionNames) {
//					all.add(name + verName);
//					all.add(name + " " + verName);
//
//					all.add(verName + name);
//					all.add(verName + " " + name);
//				}
//			}
//		}
//
//		all = toLowerCase(all);
//
//		return all;
//	}

	/**
	 * 返回（名字 + 年）的所有组合
	 */
//	public static Set<String> getAllNames(Names names, int year)
//			throws Exception {
//
//		String versionName = String.valueOf(year);
//
//		return getAllNames(names, versionName);
//	}

	/**
	 * 返回（名字 + 年 + 月）的所有组合
	 */
//	public static Set<String> getAllNames(Names names, int year, int month)
//			throws Exception {
//
//		String monthString = month < 10 ? ("0" + month) : ("" + month);
//
//		String versionName = String.valueOf(year) + monthString;
//
//		return getAllNames(names, versionName);
//	}

	/**
	 * 返回（名字 + 年 + 月 + 日）的所有组合
	 */
//	public static Set<String> getAllNames(Names names, int year, int month,
//			int day) throws Exception {
//
//		String monthString = month < 10 ? ("0" + month) : ("" + month);
//		String dayString = day < 10 ? ("0" + day) : ("" + day);
//
//		String versionName = String.valueOf(year) + monthString + dayString;
//
//		return getAllNames(names, versionName);
//	}

	/**
	 * 给定的字符串以|分割，将其分割后返回。返回的list不为null
	 */
	public static List<String> split(String input) {

		Set<String> set = new LinkedHashSet<String>();
		if (input != null) {
			String[] array = input.split("[|]");
			for (String s : array) {
				putIfHasText(s, set);
			}
		}

		return new ArrayList<String>(set);
	}

	public static <K, V> V putIfAbsent(Map<K, V> map, K k, V v) {
		if (map.containsKey(k)) {
			return null;
		}

		return map.put(k, v);
	}
	
	
	

	public static Set<String> toLowerCase(Set<String> sets) {
		if (sets == null) {
			return null;
		}

		Set<String> newSets = new HashSet<String>();
		for (String string : sets) {
			newSets.add(string.toLowerCase());
		}

		return newSets;
	}

	public static List<String> toLowerCase(List<String> list) {
		if (list == null) {
			return null;
		}

		List<String> newList = new ArrayList<String>();
		for (String string : list) {
			newList.add(string.toLowerCase());
		}

		return newList;
	}

	

//	public static List<PersonSubject> load(List<Integer> subject_ids,
//			String table) throws Exception {
//
//		List<PersonSubject> personSubjects = new ArrayList<PersonSubject>();
//
//		final int limit = 100;
//		final int size = subject_ids.size();
//
//		for (int start = 0; start < size; start += limit) {
//			int end = Math.min(start + limit, size);
//
//			List<Integer> ids = subject_ids.subList(start, end);
//
//			Criteria criteria = new Criteria();
//			criteria.setDistinct();
//			PersonSubjectPeer.addSelectColumns(criteria);
//			criteria.addIn(PersonSubjectPeer.SUBJECT_ID, ids);
//			criteria.add(PersonSubjectPeer.TAB_NAME, table);
//			criteria.addJoin(PersonPeer.ID, PersonSubjectPeer.FK_PERSON_ID);
//			criteria.addJoin(PersonRolePeer.ID, PersonSubjectPeer.FK_ROLE_ID);
//
//			personSubjects.addAll(PersonSubjectPeer.doSelect(criteria));
//		}
//
//		return personSubjects;
//	}
}
