package com.youku.top.recomend;

import java.util.ArrayList;
import java.util.List;

public class SelectionSort {
	
	public final static int RETURNSIZE=10;
    /*
     * (non-Javadoc)
     * 
     * @see org.rut.util.algorithm.SortUtil.Sort#sort(int[])
     */
    public List<Entity> sort(List<Entity> data,int len) {
        for (int i = 0; i<data.size()&&i<len; i++){
            int lowIndex = i;
            for (int j = data.size() - 1; j > i; j--) {
                if (data.get(j).getSearchTimes() > data.get(lowIndex).getSearchTimes()) {
                    lowIndex = j;
                }
            }
            swap(data,i,lowIndex);
        }
        if(len>=data.size())
        	return data;
        else{
        	List<Entity> et=new ArrayList<Entity>();
        	for(int i=0;i<RETURNSIZE;i++)
        		et.add(data.get(i));
        	return et;
        }
    }
    public void swap(List<Entity> data, int i, int j) {
    	Entity temp = data.get(i);
    	data.set(i, data.get(j));
    	data.set(j, temp);
    }
    
    public static void main(String[] args) {
    	SelectionSort s=new SelectionSort();
		List<Entity> l=new ArrayList<Entity>();
		Entity e;
		for(int i=0;i<100;i++){
			e=new Entity();
			e.keyword=""+i;
			e.searchTimes=i;
			l.add(e);
		}
		l=s.sort(l, 10);
		System.out.println(l.size());
		for(Entity et:l)System.out.println(et.keyword);
	}
}