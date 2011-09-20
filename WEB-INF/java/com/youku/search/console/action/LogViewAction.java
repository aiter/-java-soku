package com.youku.search.console.action;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.googlecode.jsonplugin.annotations.JSON;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;
import com.youku.search.console.operate.LogInfoWriter;
import com.youku.search.console.operate.log.LogBaseView;
import com.youku.search.console.vo.Type;
import com.youku.search.console.vo.View30Days;
import com.youku.search.console.vo.chart.Element;
import com.youku.search.console.vo.chart.Title;
import com.youku.search.console.vo.chart.X_Axis;
import com.youku.search.console.vo.chart.Y_Axis;

public class LogViewAction extends ActionSupport implements ServletRequestAware{
	protected Title title;
	protected Title x_legend;
	protected Title y_legend;
	protected String bg_colour;
	protected X_Axis x_axis;
	protected Y_Axis y_axis;
	protected List<Element> elements;
	LogBaseView lv=new LogBaseView();
	String betdate;
	HttpServletRequest request;
	List<View30Days> lvd;

	public List<View30Days> getLvd() {
		return lvd;
	}

	public void setLvd(List<View30Days> lvd) {
		this.lvd = lvd;
	}

	@JSON(serialize=false)
	public HttpServletRequest getRequest() {
		return request;
	}

	public Title getTitle() {
		return title;
	}

	public void setTitle(Title title) {
		this.title = title;
	}

	public Title getX_legend() {
		return x_legend;
	}

	public void setX_legend(Title x_legend) {
		this.x_legend = x_legend;
	}

	public Title getY_legend() {
		return y_legend;
	}

	public void setY_legend(Title y_legend) {
		this.y_legend = y_legend;
	}

	public String getBg_colour() {
		return bg_colour;
	}

	public void setBg_colour(String bg_colour) {
		this.bg_colour = bg_colour;
	}

	public X_Axis getX_axis() {
		return x_axis;
	}

	public void setX_axis(X_Axis x_axis) {
		this.x_axis = x_axis;
	}

	public Y_Axis getY_axis() {
		return y_axis;
	}

	public void setY_axis(Y_Axis y_axis) {
		this.y_axis = y_axis;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	private void requestGet(){
		betdate = request.getParameter("betdate");
		String start=null,end=null;
		if(!StringUtils.isBlank(betdate)){
			String[] d = betdate.split("\\|");
			if(null!=d&&d.length==2){
				start = d[0];
				end= d[1];
			}
		}
		
		lvd = lv.getUnionDate(start,end);
	}
	
	public String total() {
		requestGet();
		title=new Title();
		title.setText("总数");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("total search counts");
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			e.getValues().add(lvd.get(i).getSum());
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----总数");
		return Action.SUCCESS;
	}
	
	public String video() {
		requestGet();
		title=new Title();
		title.setText("视频");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("video search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.VIDEO)==null?0:lvd.get(i).getDatemap().get(Type.VIDEO);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----视频");
		return Action.SUCCESS;
	}
	
	public String folder() {
		requestGet();
		title=new Title();
		title.setText("专辑");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("folder search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.FOLDER)==null?0:lvd.get(i).getDatemap().get(Type.FOLDER);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----专辑");
		return Action.SUCCESS;
	}
	
	public String user() {
		requestGet();
		title=new Title();
		title.setText("用户");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("user search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.USER)==null?0:lvd.get(i).getDatemap().get(Type.USER);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----用户");
		return Action.SUCCESS;
	}
	
	public String bar() {
		requestGet();
		title=new Title();
		title.setText("看吧");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("bar search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.BAR)==null?0:lvd.get(i).getDatemap().get(Type.BAR);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----看吧");
		return Action.SUCCESS;
	}
	
	public String pk() {
		requestGet();
		title=new Title();
		title.setText("PK");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("pk search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.PK)==null?0:lvd.get(i).getDatemap().get(Type.PK);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----pk");
		return Action.SUCCESS;
	}
	
	public String page1Total() {
		requestGet();
		title=new Title();
		title.setText("第一页搜索总数");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("page1 search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.PAGE1TOTAL)==null?0:lvd.get(i).getDatemap().get(Type.PAGE1TOTAL);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----第一页");
		return Action.SUCCESS;
	}
	
	public String advvideo() {
		requestGet();
		title=new Title();
		title.setText("视频高级检索");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("advvideo search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.ADVVIDEO)==null?0:lvd.get(i).getDatemap().get(Type.ADVVIDEO);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1); 
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----视频高级");
		return Action.SUCCESS;
	}
	
	public String advfolder() {
		requestGet();
		title=new Title();
		title.setText("专辑高级检索");
		x_legend =new Title();
		y_legend =new Title();
		x_axis=new X_Axis();
		y_axis= new Y_Axis();
		x_legend.setText("date");
		y_legend.setText("search counts");
		bg_colour="#E0E0FF";
		elements = new LinkedList<Element>();
		Element e=new Element();
		e.setText("advfolder search counts");
		int sum=0;
		String lable ="";
		int m = lvd.size()/30;
		for(int i=0;i<lvd.size();i++){
			sum=lvd.get(i).getDatemap().get(Type.ADVFOLDER)==null?0:lvd.get(i).getDatemap().get(Type.ADVFOLDER);
			e.getValues().add(sum);
			if(m>0){
				if(i==lvd.size()-1)
					lable = lvd.get(i).getDate().substring(5,10);
				else if((i+1)%(m+1)==1)
					lable = lvd.get(i).getDate().substring(5,10);
				else lable="";
			}else lable = lvd.get(i).getDate().substring(5,10);
			x_axis.getLabels().getLabels().add(lable);
		}
		Integer[] l=e.getValues().toArray(new Integer[]{0});
		Arrays.sort(l);
		int min=(int)Math.pow(10, String.valueOf(l[0]).length()-1);
		int max=(int)Math.pow(10, String.valueOf(l[l.length-1]).length()-1);
		int steps=0;
		if(max/min<10) steps=min;
		else {
			min=0;
			steps=((l[l.length-1]/max+1)*max-min)/4;
		}
		y_axis.setMin(min);
		y_axis.setMax((l[l.length-1]/max+1)*max);
		y_axis.setSteps(steps);
		elements.add(e);
		LogInfoWriter.operate_log.info("分类统计图形----专辑高级");
		return Action.SUCCESS;
	}
	
	@JSON(serialize=false)
	@Override
	public void setServletRequest(HttpServletRequest arg0) {
		this.request = arg0;
	}
}
