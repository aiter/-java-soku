package com.youku.search.util.sort;

public class QuickSort implements SortUtil.Sort{

    /* (non-Javadoc)
     * @see org.rut.util.algorithm.SortUtil.Sort#sort(int[])
     */
    public void sort(float[] data) {
        quickSort(data,0,data.length-1);        
    }
    private void quickSort(float[] data,int i,int j){
        int pivotIndex=(i+j)/2;
        //swap
        SortUtil.swap(data,pivotIndex,j);
        
        int k=partition(data,i-1,j,data[j]);
        SortUtil.swap(data,k,j);
        if((k-i)>1) quickSort(data,i,k-1);
        if((j-k)>1) quickSort(data,k+1,j);
        
    }
    /**
     * @param data
     * @param i
     * @param j
     * @return
     */
    private int partition(float[] data, int l, int r,float pivot) {
        do{
           while(data[++l]<pivot);
           while((r!=0)&&data[--r]>pivot);
           SortUtil.swap(data,l,r);
        }
        while(l<r);
        SortUtil.swap(data,l,r);        
        return l;
    }

}