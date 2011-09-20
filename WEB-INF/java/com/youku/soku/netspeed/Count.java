package com.youku.soku.netspeed;

public class Count {
	long down;
	long cost;
	int times;
	
	Count(long down,long cost,int times){
		this.down = down;
		this.cost = cost;
		this.times = times;
	}
	
	public long getDown() {
		return down;
	}
	public void setDown(long down) {
		this.down = down;
	}
	public long getCost() {
		return cost;
	}
	public void setCost(long cost) {
		this.cost = cost;
	}
	public int getTimes() {
		return times;
	}
	public void setTimes(int times) {
		this.times = times;
	}
	
	public String toString(){
		return down + "/" + cost + "/" +times + "/" + (down/cost);
	}
}
