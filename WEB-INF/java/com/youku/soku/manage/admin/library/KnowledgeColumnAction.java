package com.youku.soku.manage.admin.library;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.json.JSONArray;
import org.json.JSONObject;

import com.youku.soku.knowledge.KnowledgeData;
import com.youku.soku.knowledge.KnowledgeDataLoader;
import com.youku.soku.knowledge.KnowledgePageRender;
import com.youku.soku.knowledge.KnowledgeDataLoader.KnowledgeDataNode;
import com.youku.soku.knowledge.data.KnowledgeDataHolder;
import com.youku.soku.library.load.KnowledgeColumn;
import com.youku.soku.library.load.KnowledgeColumnPeer;
import com.youku.soku.manage.bo.KnowledgeColumnBo;
import com.youku.soku.manage.common.BaseActionSupport;
import com.youku.soku.manage.common.Constants;
import com.youku.soku.manage.common.PageInfo;
import com.youku.soku.manage.datamaintain.KnowledgeDataImport;
import com.youku.soku.manage.exception.DbOperationException;
import com.youku.soku.manage.exception.PageNotFoundException;
import com.youku.soku.manage.service.KnowledgeColumnService;
import com.youku.soku.manage.service.UserOperationService;

public class KnowledgeColumnAction extends BaseActionSupport {

	private Logger log = Logger.getLogger(this.getClass());

	/**
	 * <p>
	 * Create category action
	 * </p>
	 * 
	 * @return The "SUCCESS" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String input() throws Exception {

		try {
			if (getKnowledgeColumnId() == -1) {
				setTask(Constants.CREATE);
				setKnowledgeColumnId(-1);
				return INPUT;
			} else {
				KnowledgeColumn oldKnowledgeColumn = KnowledgeColumnPeer.retrieveByPK(getKnowledgeColumnId());
				setKnowledgeColumn(getKnowledgeColumnBo(oldKnowledgeColumn));
				setKnowledgeColumnId(oldKnowledgeColumn.getId());
				setTask(Constants.EDIT);
				return INPUT;
			}
		} catch (NoRowsException e) {
			throw new PageNotFoundException(getText("error.page.not.found"));
		} catch (TooManyRowsException e) {
			log.error(e.getMessage(), e);
		} catch (TorqueException e) {
			log.error(e.getMessage(), e);
		}
		return INPUT;

	}

	/**
	 * <p>
	 * Insert or update an category
	 * </p>
	 * 
	 * @return The proper result for this mapping
	 * @throw Exception on any error
	 */
	public String save() throws Exception {
		return execute();
	}

	/**
	 * <p>
	 * List the KnowledgeColumns
	 * </p>
	 * 
	 * @return the "list" result for this mapping
	 * @throws Exception
	 *             on any error
	 */
	public String list() throws Exception {

		try {

			PageInfo pageInfo = new PageInfo();
			pageInfo.setPageSize(50);
			if (getPageNumber() == 0) {
				setPageNumber(1);
			}
			pageInfo.setCurrentPageNumber(getPageNumber());

			log.debug("KnowledgeColumnAction getPageNumber() value is: " + getPageNumber());
			log.debug("KnowledgeColumnAction pageInfo current pageNumber value is: " + getPageNumber());
			log.debug("KnowledgeColumnAction getSearchWord is: " + getSearchWord());

			KnowledgeColumnService.findKnowledgeColumnPagination(pageInfo, getSearchWord(), getParentId());

			List knowledgeColumnList = pageInfo.getResults();
			log.debug("how word list is: " + knowledgeColumnList);
			// knowledgeColumnList = KnowledgeColumnPeer.populateObjects(knowledgeColumnList);
			List newList = new ArrayList();
			if (knowledgeColumnList == null) {
				knowledgeColumnList = new ArrayList();
			}
			for (Object obj : knowledgeColumnList) {
				log.debug((KnowledgeColumn) obj);
				newList.add(getKnowledgeColumnBo((KnowledgeColumn) obj));
			}
			pageInfo.setResults(newList);
			log.debug("knowledgeColumnList size is: " + knowledgeColumnList.size());
			setPageInfo(pageInfo);
			setParentColumns(getParentKnowledgeColumn(getParentId()));
			
			try {
				KnowledgeDataNode data = new KnowledgeDataNode();
				data.setName("知识栏目");
				KnowledgeDataLoader loader = new KnowledgeDataLoader();
				loader.loadDataFromDb(data);
				//setTreeView(loader.displayKnowledgeTree(KnowledgeDataHolder.getCurrentThreadLocal()));
				setTreeView(KnowledgePageRender.displayKnowledgeTree(data));
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
				KnowledgeDataHolder.removeCurrentThreadLocal();
			}
			return Constants.LIST;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return Constants.LIST;
	}

	/**
	 * search knowledgeColumn and return the json result;
	 * 
	 * @return the proper result
	 * @throw Exception on any error
	 */
	public String listJson() throws Exception {
		log.debug("KnowledgeColumnAction list json");

		List<KnowledgeColumn> knowledgeColumnList = KnowledgeColumnService.searchKnowledgeColumn(getSearchWord());
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < knowledgeColumnList.size(); i++) {
			JSONObject json = new JSONObject(getKnowledgeColumnBo(knowledgeColumnList.get(i)));
			jsonArray.put(i, json);
		}

		log.debug(jsonArray.toString());

		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		out.print(jsonArray.toString());
		return null;
	}
	
	private List<KnowledgeColumn> getParentKnowledgeColumn(int parentId) throws Exception {
		
		int id = parentId;
		List<KnowledgeColumn> columnList = new ArrayList<KnowledgeColumn>();
		while (id != 0) {
			KnowledgeColumn currentColumn = KnowledgeColumnPeer.retrieveByPK(id);
			columnList.add(currentColumn);
			id = currentColumn.getParentId();					
		}
		/*if(columnList.size() > 1) {
			columnList.remove(0);
		}*/
		Collections.reverse(columnList);
		return columnList;
	}

	/**
	 * <p>
	 * Delete an knowledgeColumn from the persistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String delete() throws Exception {
		log.debug(" ** delete knowledgeColumn action ");
		log.debug(" ====try** delete knowledgeColumn action ");

		return delete(getKnowledgeColumnId());

	}

	/**
	 * <p>
	 * Batch delete an KnowledgeColumn from the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String batchdelete() throws Exception {

		for (int deleteId : getBatchdeleteids()) {
			log.debug("#########delete id is: " + deleteId);
			String result = delete(deleteId);
			if (result == null) {
				return null;
			}
		}
		return SUCCESS;
	}

	private String delete(int id) throws Exception {
		Criteria crit = new Criteria();
		crit.add(KnowledgeColumnPeer.PARENT_ID, id);

		List<KnowledgeColumn> kcList = KnowledgeColumnPeer.doSelect(crit);

		if (kcList.size() > 0) {
			HttpServletResponse response = ServletActionContext.getResponse();
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			String errorMsg = "存在关联子栏目，不能删除";
			out.print("<script type='text/javascript'>alert('" + errorMsg + "'); window.history.back();</script>");
			return null;
		} else {
			KnowledgeColumn knowledgeColumn = KnowledgeColumnPeer.retrieveByPK(id);
			log.debug(knowledgeColumn);
			log.info("Operator: " + getUserName());
			log.info("Delete KnowledgeColumn at " + formatLogDate(new Date()));
			log.info(knowledgeColumn);
			KnowledgeColumnPeer.doDelete(knowledgeColumn);
			return SUCCESS;
		}
	}
	
	public String importData() throws Exception {
		//new KnowledgeDataImport().imporData();		
		return SUCCESS;
	}
	
	public String knowledgeTree() throws Exception {
		try {
			//data.printKnowledgeTree(0, KnowledgeDataHolder.getCurrentThreadLocal());
			KnowledgeData dataMap = KnowledgeDataHolder.getCurrentThreadLocal();
			log.info("dataMap: " + dataMap);
			KnowledgeDataNode data = dataMap.getNodeData("武术");
			log.info(dataMap.getChildColumn("叉烧鸡"));
			log.info(dataMap.getChildColumn("武术"));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			KnowledgeDataHolder.removeCurrentThreadLocal();
		}
		return SUCCESS;
	}

	/**
	 * <p>
	 * Insert or update an knowledgeColumn object to the presistent store
	 * </p>
	 * <p/>
	 * 
	 * @return the proper result
	 * @throws Exception
	 *             on any error
	 */
	public String execute() throws Exception {

		try {

			log.debug("=======execute method=========");
			boolean creating = Constants.CREATE.equals(getTask());
			log.debug("KnowledgeColumn name is: " + getKnowledgeColumn().getName());

			if (creating) {

				List<KnowledgeColumn> knowledgeColumnList = KnowledgeColumnService.findKnowledgeColumnByName(getKnowledgeColumn().getName());
				boolean haveKnowledgeColumn = knowledgeColumnList.size() > 0;
				log.debug("knowledgeColumnList size is:" + knowledgeColumnList.size());

				if (haveKnowledgeColumn) {
					addActionError(getText("error.knowledgeColumnname.unique"));
				}

				getKnowledgeColumn().setCreateTime(new Date());
				getKnowledgeColumn().setUpdateTime(new Date());
				KnowledgeColumn h = getKnowledgeColumnVo(getKnowledgeColumn());
				log.debug(h);
				log.info("Operator: " + getUserName());
				log.info("Save KnowledgeColumn at " + formatLogDate(new Date()));
				log.info(h);
				h.setName(h.getName().trim());
				h.setParentId(getParentId());
				if(getParentId() == 0) {
					h.setLevel(1);
				} else {
					KnowledgeColumn pc = KnowledgeColumnPeer.retrieveByPK(getParentId());
					h.setLevel(pc.getLevel() + 1);
				}
				h.save();
				UserOperationService.logOperateKnowledgeColumn(Constants.USER_OPERATION_ADD, getUserName(), h, "");
			} else {

				log.debug("Task update, knowledgeColumnId is " + getKnowledgeColumn().getId());
				KnowledgeColumn oldKnowledgeColumn = KnowledgeColumnPeer.retrieveByPK(getKnowledgeColumnId());

				if (!oldKnowledgeColumn.getName().equals(getKnowledgeColumn().getName())) {
					List<KnowledgeColumn> knowledgeColumnList = KnowledgeColumnService.findKnowledgeColumnByName(getKnowledgeColumn().getName());
					boolean haveKnowledgeColumn = knowledgeColumnList.size() > 0;
					log.debug("knowledgeColumnList size is:" + knowledgeColumnList.size());

					if (haveKnowledgeColumn) {
						addActionError(getText("error.knowledgeColumnname.unique"));
					}
				}
				log.info("Operator: " + getUserName());
				String detail = "Update KnowledgeColumn at " + formatLogDate(new Date());
				detail += "Before Update: " + oldKnowledgeColumn;
				oldKnowledgeColumn.setName(getKnowledgeColumn().getName().trim());
				oldKnowledgeColumn.setPic(getKnowledgeColumn().getPic());
				oldKnowledgeColumn.setUpdateTime(new Date());
				detail += "after Update: " + oldKnowledgeColumn;
				log.info(detail);
				KnowledgeColumnPeer.doUpdate(oldKnowledgeColumn);

				UserOperationService.logOperateKnowledgeColumn(Constants.USER_OPERATION_ADD, getUserName(), oldKnowledgeColumn, detail);
			}
		} catch (Exception e) {
			log.info(e.getMessage(), e);
			throw new DbOperationException(getText("db.operation.exception"));
		}
		return SUCCESS;
	}

	private KnowledgeColumnBo getKnowledgeColumnBo(KnowledgeColumn knowledgeColumn) throws IllegalAccessException, InvocationTargetException, TorqueException {

		KnowledgeColumnBo knowledgeColumnBo = new KnowledgeColumnBo();
		copyProperties(knowledgeColumnBo, knowledgeColumn);
		
		/*List<KnowledgeColumn> childColumn = KnowledgeColumnService.findKnowledgeColumnByParentId(knowledgeColumn.getId());
		knowledgeColumnBo.setChildColumn(childColumn);*/
		return knowledgeColumnBo;
	}

	private KnowledgeColumn getKnowledgeColumnVo(KnowledgeColumnBo knowledgeColumnBo) throws IllegalAccessException, InvocationTargetException {

		KnowledgeColumn knowledgeColumn = new KnowledgeColumn();
		copyProperties(knowledgeColumn, knowledgeColumnBo);

		return knowledgeColumn;
	}

	/** the object of the KnowledgeColumn **/
	private KnowledgeColumnBo knowledgeColumn;

	/**
	 * <p>
	 * create the KnowledgeColumn object
	 * </p>
	 */
	public KnowledgeColumnBo getKnowledgeColumn() {
		return knowledgeColumn;
	}

	/**
	 * <p>
	 * set the KnowledgeColumn object
	 * </p>
	 * 
	 * @param knowledgeColumn
	 *            KnowledgeColumn ojbect
	 */
	public void setKnowledgeColumn(KnowledgeColumnBo knowledgeColumn) {
		this.knowledgeColumn = knowledgeColumn;
	}

	/**
	 * KnowledgeColumn id, used for update the knowledgeColumn object
	 */
	private int knowledgeColumnId;

	/**
	 * get knowledgeColumn id
	 * 
	 * @return knowledgeColumnId
	 */
	public int getKnowledgeColumnId() {
		return knowledgeColumnId;
	}

	/**
	 * set knowledgeColumn id
	 * 
	 * @param knowledgeColumnId
	 */
	public void setKnowledgeColumnId(int knowledgeColumnId) {
		this.knowledgeColumnId = knowledgeColumnId;
	}

	/**
	 * current page number, for the knowledgeColumn list view
	 */
	private int pageNumber;

	/**
	 * get the current page number
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	/**
	 * set the current page number
	 */
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	/**
	 * the pagination object
	 */
	private PageInfo pageInfo;

	/**
	 * get the pagination object
	 */
	public PageInfo getPageInfo() {
		return pageInfo;
	}

	/**
	 * set the pagination object
	 */
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}

	/**
	 * the value for the word for searching
	 */
	private String searchWord;

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	private int[] batchdeleteids;

	public int[] getBatchdeleteids() {
		return batchdeleteids;
	}

	public void setBatchdeleteids(int[] batchdeleteids) {
		this.batchdeleteids = batchdeleteids;
	}

	private int parentId;

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	private List<KnowledgeColumn> parentColumns;

	public List<KnowledgeColumn> getParentColumns() {
		return parentColumns;
	}

	public void setParentColumns(List<KnowledgeColumn> parentColumns) {
		this.parentColumns = parentColumns;
	}
	
	
	private String treeView;

	public String getTreeView() {
		return treeView;
	}

	public void setTreeView(String treeView) {
		this.treeView = treeView;
	}
	
	
	
}
