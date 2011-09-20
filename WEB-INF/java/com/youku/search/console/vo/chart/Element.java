package com.youku.search.console.vo.chart;

import java.util.ArrayList;
import java.util.List;

public class Element {
	// set a default
	String type = "line";
	String colour="#87421F";
	int width=3;
	String text;
	
	// the data values
	List<Integer> values=new ArrayList<Integer>();
   
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	
	public List<Integer> getValues() {
		return values;
	}
	public void setValues(List<Integer> values) {
		this.values = values;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColour() {
		return colour;
	}
	public void setColour(String colour) {
		this.colour = colour;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
