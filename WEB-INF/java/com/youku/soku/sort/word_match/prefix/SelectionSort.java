package com.youku.soku.sort.word_match.prefix;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.youku.soku.sort.word_match.Program;

public class SelectionSort {
	
	public final static int RETURNSIZE=20;
	
    public List<Program> sort(List<Program> data,int limit) {
         Collections.sort(data);
         
         int count = 0;
         Set<Integer> seriesSet = new HashSet<Integer>();
         Set<Integer> programSet = new HashSet<Integer>();
         //去重
			Iterator<Program> it = data.iterator();
			while (it.hasNext()){
				Program program = it.next() ;
				
				//根据节目ID去重
				if (program.getElement().getProgramId() > 0){
					if (programSet.contains(program.getElement().getProgramId())){
						it.remove();
						continue;
					}
					else{
						programSet.add(program.getElement().getProgramId());
					}
				}
				//根据系列ID去重
				if (program.getElement().getSeries() > 0){
					if (seriesSet.contains(program.getElement().getSeries())){
						it.remove();
						continue;
					}
					else{
						seriesSet.add(program.getElement().getSeries());
					}
				}
				
				if (count++ >= limit){
					it.remove();
				}
			}
         
         return data;
    }
   
   
}