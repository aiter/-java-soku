package com.youku.search.util.sort;

public class ImprovedMergeSort implements SortUtil.Sort {

    private static final int THRESHOLD = 10;

    /*
     * (non-Javadoc)
     * 
     * @see org.rut.util.algorithm.SortUtil.Sort#sort(int[])
     */
    public void sort(float[] data) {
    	float[] temp=new float[data.length];
        mergeSort(data,temp,0,data.length-1);
    }

    private void mergeSort(float[] data, float[] temp, int l, int r) {
        int i, j, k;
        int mid = (l + r) / 2;
        if (l == r)
            return;
        if ((mid - l) >= THRESHOLD)
            mergeSort(data, temp, l, mid);
        else
            insertSort(data, l, mid - l + 1);
        if ((r - mid) > THRESHOLD)
            mergeSort(data, temp, mid + 1, r);
        else
            insertSort(data, mid + 1, r - mid);

        for (i = l; i <= mid; i++) {
            temp[i] = data[i];
        }
        for (j = 1; j <= r - mid; j++) {
            temp[r - j + 1] = data[j + mid];
        }
        float a = temp[l];
        float b = temp[r];
        for (i = l, j = r, k = l; k <= r; k++) {
            if (a < b) {
                data[k] = temp[i++];
                a = temp[i];
            } else {
                data[k] = temp[j--];
                b = temp[j];
            }
        }
    }

    /**
     * @param data
     * @param l
     * @param i
     */
    private void insertSort(float[] data, int start, int len) {
        for(int i=start+1;i<start+len;i++){
            for(int j=i;(j>start) && data[j]<data[j-1];j--){
                SortUtil.swap(data,j,j-1);
            }
        }
    }
}