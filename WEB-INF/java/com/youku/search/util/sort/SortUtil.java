package com.youku.search.util.sort;

import java.util.Random;


/**
 * @author treeroot
 * @since 2006-2-2
 * @version 1.0
 */
public class SortUtil {
    public final static int INSERT = 1;
    public final static int BUBBLE = 2;
    public final static int SELECTION = 3;
    public final static int SHELL = 4;
    public final static int QUICK = 5;
    public final static int IMPROVED_QUICK = 6;
    public final static int MERGE = 7;
    public final static int IMPROVED_MERGE = 8;
    public final static int HEAP = 9;

    public static void sort(float[] data) {
        sort(data, IMPROVED_QUICK);
    }
    private static String[] name={
            "insert", "bubble", "selection", "shell", "quick", "improved_quick", "merge", "improved_merge", "heap"
    };
    
    private static Sort[] impl=new Sort[]{
            new InsertSort(),
            new BubbleSort(),
            new SelectionSort(),
            new ShellSort(),
            new QuickSort(),
            new ImprovedQuickSort(),
            new MergeSort(),
            new ImprovedMergeSort(),
            new HeapSort()
    };

    public static String toString(int algorithm){
        return name[algorithm-1];
    }
    
    public static void sort(float[] data, int algorithm) {
        impl[algorithm-1].sort(data);
    }

    public static interface Sort {
        public void sort(float[] data);
    }

    public static void swap(float[] data, int i, int j) {
        float temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
    public static float[] makearr(int num){
		Random random = new Random();
		
		float a[] = new float[num];
		for(int i=0;i<a.length;i++){
			a[i] = random.nextFloat();
		}
		return a;
    }
    public static void print(float[] a){
    	for(int i=0;i<a.length;i++){
    		System.out.print(a[i]+" ");
    	}
    }
    public static void main(String[] args){
    	int num = 3000000;
    	float[] a = null;
    	for(int i=4;i<10;i++){
    	a = SortUtil.makearr(num);
    	long start = System.currentTimeMillis();
    	
    	SortUtil.sort(a,i);
    	long end = System.currentTimeMillis();
    //	SortUtil.print(a);
    	System.out.println(SortUtil.name[i-1]+">>>"+(end-start));
    	}
    //	SortUtil.print(a);

    }
}