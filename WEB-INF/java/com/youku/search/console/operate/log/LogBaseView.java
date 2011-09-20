package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.torque.TorqueException;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.SingleDayDate;
import com.youku.search.console.vo.Type;
import com.youku.search.console.vo.View30Days;
import com.youku.search.console.vo.SingleDayDate.Entity;
import com.youku.search.util.DataFormat;

public class LogBaseView {
	public static Map<String,SingleDayDate> lbvmap=new ConcurrentHashMap<String, SingleDayDate>();
	
	public List<View30Days> getUnionDate(String startdate,String enddate){
		List<View30Days>  vl=new ArrayList<View30Days>();
		View30Days v=null;
		String uniondate = DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD);
		String ld = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -30),DataFormat.FMT_DATE_YYYYMMDD);
		String sql = null;
		if(StringUtils.isBlank(startdate)&&StringUtils.isBlank(enddate))
			sql = "select * from query_union where uniondate >= (interval -30 day + '"+uniondate+"') order by uniondate";
		else{
			sql = "select * from query_union where uniondate >=  '"+startdate+"' and uniondate <='"+enddate+"' order by uniondate";
		}
		Connection conn=null;
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			conn=DataConn.getLogStatConn();
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			String date=null;
			int sum=0;
			while(rs.next()){
				if(!rs.getString("uniondate").equals(date)){
					if(sum!=0){
						v.setSum(sum);
						if(null!=v.getDatemap().get("page1Total"))
						v.setRate(String.format("%.2f", v.getDatemap().get("page1Total")*100.00/sum));
						sum=0;
					}
					date=rs.getString("uniondate");
					v= new View30Days();
					v.setDate(date);
					if(ld.compareTo(date)<=0)
						v.setInMounth(true);
					else v.setInMounth(false);
					vl.add(v);
				}
				if(!Type.PAGE1TOTAL.equalsIgnoreCase(rs.getString("type")))
					sum=sum+rs.getInt("unionNum");
				v.getDatemap().put(rs.getString("type"), rs.getInt("unionNum"));
				if(rs.isLast()){
					v.setSum(sum);
					if(null!=v.getDatemap().get("page1Total"))
					v.setRate(String.format("%.2f", v.getDatemap().get("page1Total")*100.00/sum));
				}
			}
		} catch (SQLException e) {
			System.out.println("data read from query_union error!");
			e.printStackTrace();
		} catch (TorqueException e) {
			System.out.println("data read from query_union error!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
				if(null!=conn)
					conn.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getUnionDate!");
				e.printStackTrace();
			}
		}
		return vl;
	}
	
	public SingleDayDate getSingleDayDate(String date){
		String table=new StringBuffer("top100_").append(date).toString();
		SingleDayDate sd=new SingleDayDate();
		Map<String,List<SingleDayDate.Entity>> vm=new HashMap<String, List<Entity>>();
		Connection conn=null;
		try{
			conn=DataConn.getLogStatConn();
			getSingleDayTotalDate(table, vm,conn);
			getSingleDayVideoDate(table,vm,conn);
			getSingleDayFolderDate(table, vm,conn);
			getSingleDayUserDate(table, vm,conn);
			getSingleDayBarDate(table, vm,conn);
//			getSingleDayPkDate(table, vm,conn);
			getSingleDayPage1TotalDate(table, vm,conn);
			getSingleDayAdvvideoDate(table, vm,conn);
			getSingleDayAdvfolderDate(table, vm,conn);
			sd.setDate(date);
			sd.setEm(vm);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(conn);
		}
		return sd;
	}
	
	public void getSingleDayTotalDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count,query_type from "+table+" where kind='total'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				se.setType(rs.getString("query_type"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put("total", sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询搜索总量的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayTotalDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayVideoDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='video'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.VIDEO, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询video的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayVideoDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayFolderDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='folder'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.FOLDER, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询folder的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayFolderDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayUserDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='user'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.USER, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询user的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayUserDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayBarDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='bar'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.BAR, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询bar的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayBarDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayPkDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='pk'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.PK, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询pk的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayPkDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayRingDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='ring'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.RING, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询ring的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayRingDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayAdvvideoDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='advvideo'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.ADVVIDEO, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询advvideo的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayAdvvideoDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayAdvfolderDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count from "+table+" where kind='advfolder'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.ADVFOLDER, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询advfolder的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayAdvfolderDate!");
				e.printStackTrace();
			}
		}
	}
	
	public void getSingleDayPage1TotalDate(String table,Map<String,List<SingleDayDate.Entity>> vm,Connection conn){
		String sql="select keyword,query_count,query_type from "+table+" where kind='page1'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		SingleDayDate.Entity se;
		List<SingleDayDate.Entity> sl=new ArrayList<Entity>();
		try {
			pt=conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				se=new SingleDayDate().Entity();
				se.setKeyword(rs.getString("keyword"));
				se.setCounts(rs.getInt("query_count"));
				se.setType(rs.getString("query_type"));
				sl.add(se);
			}
			if(null!=sl&&sl.size()>0)
			vm.put(Type.PAGE1TOTAL, sl);
		} catch (SQLException e) {
			System.out.println("[ERROR] 从表"+table+"查询page1Total的统计数据出错!");
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				System.out.println("db close error in function getSingleDayPage1TotalDate!");
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		String ld = DataFormat.formatDate(DataFormat.getNextDate(new Date(), -30),DataFormat.FMT_DATE_YYYYMMDD);
		System.out.println(ld.compareTo("2010-02-02"));
	}
}
