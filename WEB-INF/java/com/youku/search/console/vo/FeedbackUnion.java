package com.youku.search.console.vo;

import java.util.ArrayList;
import java.util.List;

public class FeedbackUnion {
	String operateDate;
	List<SingleOperator> slo=new ArrayList<SingleOperator>();
	public SingleOperator getSingleOperator(){
		return new SingleOperator();
	}
	
	public class SingleOperator{
	String operator;
	int deletenum;
	int dealnum;
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public int getDeletenum() {
		return deletenum;
	}
	public void setDeletenum(int deletenum) {
		this.deletenum = deletenum;
	}
	public int getDealnum() {
		return dealnum;
	}
	public void setDealnum(int dealnum) {
		this.dealnum = dealnum;
	}
	
	}


	public String getOperateDate() {
		return operateDate;
	}


	public void setOperateDate(String operateDate) {
		this.operateDate = operateDate;
	}


	public List<SingleOperator> getSlo() {
		return slo;
	}


	public void setSlo(List<SingleOperator> slo) {
		this.slo = slo;
	}
}
