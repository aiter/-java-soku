package com.youku.search.console.vo.chart;

import java.util.ArrayList;
import java.util.List;

public class Labels {
	boolean visible=true;
	List labels=new ArrayList();
	int offset=-1;
	
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public List getLabels() {
		return labels;
	}
	public void setLabels(List labels) {
		this.labels = labels;
	}
	
	
}
