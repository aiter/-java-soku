package com.youku.search.console.vo.chart;


public class Y_Axis {
	String colour="#FF0000";
	int min;
	int max;
	int steps=5000;
	String grid_colour="#FF0000";
	int offset=0;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	
	public int getMin() {
		return min;
	}
	public void setMin(int min) {
		this.min = min;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getSteps() {
		return steps;
	}
	public void setSteps(int steps) {
		this.steps = steps;
	}
	public String getGrid_colour() {
		return grid_colour;
	}
	public void setGrid_colour(String grid_colour) {
		this.grid_colour = grid_colour;
	}
	
}
