package com.youku.top.topn.util;

import java.util.ArrayList;
import java.util.List;

import com.youku.top.topn.entity.KeywordComVO;
import com.youku.top.topn.entity.KeywordQueryVO;



public class SeclectSort {
	public List<KeywordQueryVO> sort(List<KeywordQueryVO> data,int len) {
        for (int i = 0; i<data.size()&&i<len; i++){
            int lowIndex = i;
            for (int j = data.size() - 1; j > i; j--) {
                if (data.get(j).getMaxsearchs() > data.get(lowIndex).getMaxsearchs()) {
                    lowIndex = j;
                }
            }
            swap(data,i,lowIndex);
        }
        if(len>=data.size())
        	return data;
        else{
        	List<KeywordQueryVO> et=new ArrayList<KeywordQueryVO>();
        	for(int i=0;i<len;i++)
        		et.add(data.get(i));
        	return et;
        }
    }
    public void swap(List<KeywordQueryVO> data, int i, int j) {
    	KeywordQueryVO temp = data.get(i);
    	data.set(i, data.get(j));
    	data.set(j, temp);
    }
    
    public void sortByLength(List<String> data) {
        for (int i = 0; i<data.size(); i++){
            int lowIndex = i;
            for (int j = data.size() - 1; j > i; j--) {
                if (data.get(j).length() > data.get(lowIndex).length()) {
                    lowIndex = j;
                }
            }
            swapByLength(data,i,lowIndex);
        }
    }
    public void swapByLength(List<String> data, int i, int j) {
    	String temp = data.get(i);
    	data.set(i, data.get(j));
    	data.set(j, temp);
    }
    
    public List<KeywordComVO> sortBySearchs(List<KeywordComVO> data,int len) {
        for (int i = 0; i<data.size()&&i<len; i++){
            int lowIndex = i;
            for (int j = data.size() - 1; j > i; j--) {
                if (data.get(j).getMaxsearchs() > data.get(lowIndex).getMaxsearchs()) {
                    lowIndex = j;
                }
            }
            swapDate(data,i,lowIndex);
        }
        if(len>=data.size())
        	return data;
        else{
        	List<KeywordComVO> et=new ArrayList<KeywordComVO>();
        	for(int i=0;i<3;i++)
        		et.add(data.get(i));
        	return et;
        }
    }
    public void swapDate(List<KeywordComVO> data, int i, int j) {
    	KeywordComVO temp = data.get(i);
    	data.set(i, data.get(j));
    	data.set(j, temp);
    }
}
