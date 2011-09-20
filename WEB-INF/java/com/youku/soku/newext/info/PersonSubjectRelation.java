package com.youku.soku.newext.info;

//import static com.youku.soku.sort.ext.info.util.MiscUtil.putIfAbsent;
//
import java.io.Serializable;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//import java.util.SortedSet;
//import java.util.TreeSet;
//
//import com.youku.soku.library.orm.Person;
//import com.youku.soku.library.orm.PersonSubject;

/**
 * 某个 tab_name 下的数据
 */
public class PersonSubjectRelation<S> implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 加载 电视、电影等信息时，会先填充 id_personSubject ，最后会根据 id_personSubject 的内容来构造
	 * subject_role_person 和 person_subject 两个map
	 */
//	public Map<Integer, PersonSubject> id_personSubject = new HashMap<Integer, PersonSubject>();
//
//	public Map<S, Map<Integer, Set<Person>>> subject_role_person = new HashMap<S, Map<Integer, Set<Person>>>();
//	public Map<Person, SortedSet<S>> person_subject = new HashMap<Person, SortedSet<S>>();
//
//	public void add(PersonSubject personSubject) {
//		id_personSubject.put(personSubject.getId(), personSubject);
//	}
//
//	public void build(PersonInfo personInfo, Map<Integer, S> subjectMap,
//			Comparator<S> comparator) {
//
//		for (Map.Entry<Integer, PersonSubject> entry : id_personSubject
//				.entrySet()) {
//
//			PersonSubject personSubject = entry.getValue();
//
//			int subjectId = personSubject.getSubjectId();
//			int personId = personSubject.getFkPersonId();
//			int roleId = personSubject.getFkRoleId();
//
//			S subject = subjectMap.get(subjectId);
//			Person person = personInfo.id_person.get(personId);
//
//			// subject_role_person
//			putIfAbsent(subject_role_person, subject,
//					new HashMap<Integer, Set<Person>>());
//
//			Map<Integer, Set<Person>> role_person = subject_role_person
//					.get(subject);
//
//			putIfAbsent(role_person, roleId, new HashSet<Person>());
//
//			role_person.get(roleId).add(person);
//
//			// person_subject
//			putIfAbsent(person_subject, person, new TreeSet<S>(comparator));
//			person_subject.get(person).add(subject);
//		}
//	}
}
