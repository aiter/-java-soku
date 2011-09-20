package com.youku.soku.manage.common;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ApplicationAware;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.util.DataFormat;
import com.youku.soku.manage.service.ItemService;
import com.youku.soku.manage.torque.Item;
import com.youku.soku.manage.torque.User;
import com.youku.soku.manage.util.LanguageAndArea;

public class BaseActionSupport extends ActionSupport implements SessionAware,
		ApplicationAware {

	private Logger log = Logger.getLogger(this.getClass());
	/**
	 * <p>
	 * Field to store application context or its proxy.
	 * </p>
	 * <p/>
	 * <p>
	 * The application context lasts for the life of the application. A
	 * reference to the database is stored in the application context at
	 * startup.
	 * </p>
	 */
	private Map application;

	/**
	 * <p>
	 * Store a new application context.
	 * </p>
	 * 
	 * @param value
	 *            A Map representing application state
	 */
	public void setApplication(Map value) {
		application = value;
	}

	/**
	 * <p>
	 * Provide application context.
	 * </p>
	 */
	public Map getApplication() {
		return application;
	}

	// ---- SessionAware ----

	/**
	 * <p>
	 * Field to store session context, or its proxy.
	 * </p>
	 */
	private Map session;

	/**
	 * <p>
	 * Store a new session context.
	 * </p>
	 * 
	 * @param value
	 *            A Map representing session state
	 */
	public void setSession(Map value) {
		session = value;
	}

	/**
	 * <p>
	 * Provide session context.
	 * </p>
	 * 
	 * @return session context
	 */
	public Map getSession() {
		return session;
	}

	private int curUserId = 0;

	public int getCurUserId() {
		if (curUserId == 0) {
			User user = (User) getSession().get(Constants.USER_KEY);
			curUserId = null == user ? 0 : user.getUserId();
		}
		return curUserId;
	}

	public void setCurUserId(int curUserId) {
		this.curUserId = curUserId;
	}

	// -----Task property ------
	/**
	 * <p>
	 * Filed to store workflow task.
	 * </p>
	 * <p/>
	 * <p>
	 * The Task is used to track the state of the CURD workflows. It can be set
	 * to Constants.CREATE, Constants.EDIT, Constants.DELETE as needed.
	 * </p>
	 */
	private String task = null;

	/**
	 * <p>
	 * Provide workflow task
	 * </p>
	 * 
	 * @return Return the task.
	 */
	public String getTask() {
		return task;
	}

	/**
	 * <p>
	 * Store new workflow task
	 * </p>
	 * 
	 * @param value
	 *            The task to set
	 */
	public void setTask(String task) {
		this.task = task;
	}

	protected void copyProperties(Object dest, Object orig)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtils.copyProperties(dest, orig);
	}

	protected List copyProperties(List objectList)
			throws IllegalAccessException, InvocationTargetException {
		List newList = new ArrayList();
		for (Object obj : objectList) {
			Object newObj = new Object();
			copyProperties(newObj, obj);
			newList.add(newObj);
		}

		return newList;
	}

	/**
	 * the item list
	 */
	private static List<Item> itemList;

	/**
	 * get the item list
	 */
	public List<Item> getItemList() throws Exception {
		return ItemService.findItem();
	}

	/**
	 * set the item list
	 */
	public static void setItemList(List aItemList) {
		itemList = aItemList;
	}

	/**
	 * the item id name map key item id value item value
	 */
	private static Map<Integer, String> itemMap;

	/**
	 * get the item id name Map
	 * 
	 * @return
	 */
	public static Map<Integer, String> getItemMap() {
		return itemMap;
	}

	/**
	 * set the item id name Map
	 * 
	 * @param itemMap
	 */
	public static void setItemMap(Map<Integer, String> aItemMap) {
		itemMap = aItemMap;
	}

	private Connection conn;

	private static int connectionCount = 0;

	public void increaseConnectionCount() {
		connectionCount++;
	}

	public void destoryConnection() {
		connectionCount--;
	}

	public int getConnectionCount() {
		return connectionCount;
	}

	protected String encryptPassword(String str) {
		return DigestUtils.md5Hex(str);
	}

	protected String formatLogDate(Date date) {
		return DataFormat.formatDate(date, DataFormat.FMT_DATE_YYYYMMDD_HHMMSS);
	}

	protected String getUserName() {
		User user = (User) getSession().get(Constants.USER_KEY);
		return user.getName();
	}

	public List<String> getLanguageList() {
		return LanguageAndArea.getLanguageList();
	}

	public List<String> getAreaList() {
		return LanguageAndArea.getAreaList();
	}

	public List<String> getCateList() {
		return LanguageAndArea.getCateList();
	}

	protected List<String> getCatesList(String cates) {
		List<String> results = new ArrayList<String>();

		if (cates != null && !cates.equals("") && cates.indexOf("|") > -1) {
			String[] cateArr = cates.split("[|]");
			results = Arrays.asList(cateArr);
		} else if (cates != null && !cates.equals("")) {
			results.add(cates);
		}

		return results;
	}

	protected String buildCatesString(List<String> cateArr) {
		StringBuilder sb = new StringBuilder();
		for (String s : cateArr) {
			sb.append(s).append("|");
		}

		String s = sb.toString();
		if (sb.length() > 1) {
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}
}
