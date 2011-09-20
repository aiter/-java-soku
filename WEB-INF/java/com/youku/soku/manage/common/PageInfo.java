package com.youku.soku.manage.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.torque.util.Criteria;

/**
 * Common object used for pagination
 * @author tanxiuguang
 *
 */
public class PageInfo {
	
	private int pageSize;
	
	private int totalRecords;
	
	

	private int currentPageNumber;
	
	private int totalPageNumber;
	
	private List results;
	
	private Map resultsMap;
	
	private boolean hasNextPage;
	
	public int getTotalRecords() {
		return totalRecords;
	}

	public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
	}
	
	public Map getResultsMap() {
		return resultsMap;
	}

	public void setResultsMap(Map resultsMap) {
		this.resultsMap = resultsMap;
	}

	private boolean hasPrevPage;

	public boolean isHasNextPage() {
		return currentPageNumber < totalPageNumber;
	}

	public void setHasNextPage(boolean hasNextPage) {
		this.hasNextPage = hasNextPage;
	}

	public boolean isHasPrevPage() {
		return currentPageNumber > 1;
	}

	public void setHasPrevPage(boolean hasPervPage) {
		this.hasPrevPage = hasPervPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPageNumber() {
		if(currentPageNumber == 0) {
			currentPageNumber = 1;
		}
		return currentPageNumber;
	}

	public void setCurrentPageNumber(int currentPageNumber) {
		this.currentPageNumber = currentPageNumber;
	}

	public int getTotalPageNumber() {
		if(totalPageNumber == 0) {
			totalPageNumber = 1;
		}
		return totalPageNumber;
	}

	public void setTotalPageNumber(int totalPageNumber) {
		this.totalPageNumber = totalPageNumber;
	}

	public List getResults() {
		return results;
	}

	public void setResults(List results) {
		this.results = results;
	}
	
	public void initCrit(Criteria crit) {
		if(getCurrentPageNumber() > totalPageNumber) {
            setCurrentPageNumber(totalPageNumber);
        }
        //LargeSelect largeSelect = new LargeSelect(crit, pageSize);
       
        crit.setOffset(getPageSize() * (getCurrentPageNumber() - 1));
        crit.setLimit(getPageSize());
	}
	
	public <T> List<T> pagingList(List<T> list) {
		if(getCurrentPageNumber() > totalPageNumber) {
           return new ArrayList<T>();
        }
        //LargeSelect largeSelect = new LargeSelect(crit, pageSize);
		int offset = getPageSize() * (getCurrentPageNumber() - 1);
		int limit =  Math.min(list.size(), getPageSize() + offset);
        return list.subList(offset, limit);
	}
	
	public int getOffset() {
		if(getCurrentPageNumber() > totalPageNumber) {
            setCurrentPageNumber(totalPageNumber);
        }
		
		return getPageSize() * (getCurrentPageNumber() -1);
	}
	
	
	public static void main(String[] args) {
		
		System.out.println(Math.ceil((double)4/3));
		List<Integer> list = new ArrayList<Integer>();
		
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		System.out.println(list);
		PageInfo pageInfo = new PageInfo();
		pageInfo.setCurrentPageNumber(3);
		pageInfo.setPageSize(3);
		pageInfo.setTotalPageNumber((int) Math.ceil((double)list.size() / pageInfo.getPageSize()));
		
		
		System.out.println(pageInfo.pagingList(list));
	}
	
}
