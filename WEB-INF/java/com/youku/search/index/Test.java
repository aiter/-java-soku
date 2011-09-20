/**
 * 
 */
package com.youku.search.index;

import java.util.Hashtable;



/**
 * @author 1verge
 *
 */
public class Test extends Thread{
	
	private static Object lock = new Object();
	
	protected static org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	int num ;
	Test(int num){
		this.num = num;
	}
	public void run(){
		synchronized(lock){
			Operate.get(num);
		}
	}
	
	
	public static void main(String[] args)
	{
		
	}
	
	

	
}
class Map{
	static int[] set = new int[10000];
	static {
		System.out.println("map init..");
		for (int i=0;i<10;i++){
			set[i]=i;
		}
	}
}

class Operate{
	public static  void get(int num){
		for (int i=0;i<10;i++){
			System.out.println(" thread " + num + "\t i=" +  i +  " value= "+ Map.set[i]);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Map.set[i] = -1;
		}
	}
}