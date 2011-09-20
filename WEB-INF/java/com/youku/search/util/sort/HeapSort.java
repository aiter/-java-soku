package com.youku.search.util.sort;




/**
 * @author treeroot
 * @since 2006-2-2
 * @version 1.0
 */
public class HeapSort implements SortUtil.Sort{

    /* (non-Javadoc)
     * @see org.rut.util.algorithm.SortUtil.Sort#sort(int[])
     */
    public void sort(float[] data) {
        MaxHeap h=new MaxHeap();
        h.init(data);
        for(int i=0;i<data.length;i++)
            h.remove();
        System.arraycopy(h.queue,1,data,0,data.length);
    }

     private static class MaxHeap{         
        
        void init(float[] data){
            this.queue=new float[data.length+1];
            for(int i=0;i<data.length;i++){
                queue[++size]=data[i];
                fixUp(size);
            }
        }
         
        private int size=0;

        private float[] queue;
                
        public float get() {
            return queue[1];
        }

        public void remove() {
            SortUtil.swap(queue,1,size--);
            fixDown(1);
        }
        //fixdown
        private void fixDown(int k) {
            int j;
            while ((j = k << 1) <= size) {
                if (j < size && queue[j]<queue[j+1])
                    j++; 
                if (queue[k]>queue[j]) //不用交换
                    break;
                SortUtil.swap(queue,j,k);
                k = j;
            }
        }
        private void fixUp(int k) {
            while (k > 1) {
                int j = k >> 1;
                if (queue[j]>queue[k])
                    break;
                SortUtil.swap(queue,j,k);
                k = j;
            }
        }

    }

}