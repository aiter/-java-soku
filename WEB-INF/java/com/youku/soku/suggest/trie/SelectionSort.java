package com.youku.soku.suggest.trie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectionSort implements Serializable{
	
	public final static int RETURNSIZE=10;
    /*
     * (non-Javadoc)
     * 
     * @see org.rut.util.algorithm.SortUtil.Sort#sort(int[])
     */
    public static List<Entity> sort(List<Entity> data,int len) {
        for (int i = 0; i<data.size()&&i<len; i++){
            int lowIndex = i;
            for (int j = data.size() - 1; j > i; j--) {
                if (data.get(j).getQueryCount() > data.get(lowIndex).getQueryCount()) {
                    lowIndex = j;
                }
            }
            if(i != lowIndex) {
            	swap(data,i,lowIndex);
            }
            
        }
        if(len>=data.size())
        	return data;
        else{
        	List<Entity> et=new ArrayList<Entity>();
        	for(int i=0;i<RETURNSIZE;i++) {
        		et.add(data.get(i));
        	}
        	data.clear();
        	data = null;
        	return et;
        }
    }
    public static void swap(List<Entity> data, int i, int j) {
    	Entity temp = data.get(i);
    	data.set(i, data.get(j));
    	data.set(j, temp);
    }
    
  
}