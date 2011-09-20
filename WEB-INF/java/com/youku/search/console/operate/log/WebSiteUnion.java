package com.youku.search.console.operate.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.youku.search.console.operate.DataConn;
import com.youku.search.console.vo.website.Site;
import com.youku.search.console.vo.website.Unions;
import com.youku.search.util.DataFormat;
import com.youku.search.util.StringUtil;

public class WebSiteUnion {
	public static enum Source {
		tudou, ku6, room6, com56, hupo, joy, vodone, sohu, sina, cctv, openv, ifeng, zol
	}
	
	private String getSource(String site){
		site=org.apache.commons.lang.StringUtils.substringBefore(site, ".");
		Source[] sources=Source.values();
		for(Source source:sources){
			if(site.equals(source.name()))
				return source.name();
			if(site.equals("56"))
				return Source.com56.name();
		}
		return null;
	}
	
	private int getResuleSet(ResultSet rs,String column){
		int number=0;
		try {
			number=rs.getInt(column);
		} catch (SQLException e) {
			e.printStackTrace();
			number=0;
		} catch (Exception e) {
			e.printStackTrace();
			number=0;
		}
		return number;
	}
	
	private Unions getUnionsFromList(String uniondate,List<Unions> unionslist){
		for(Unions u:unionslist){
			if(u.getUniondate().equalsIgnoreCase(uniondate))
				return u;
		}
		return null;
	}
	
	public void showSiteUnions(Connection conn,String sql,List<Unions> unionslist){
		PreparedStatement pt = null;
		ResultSet rs = null;
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			String uniondate=null;
			Unions unions=null;
			String source=null;
			int because=0;
			int once_clicks=0;
			while(rs.next()){
				uniondate = rs.getString("uniondate");
				unions=getUnionsFromList(uniondate,unionslist);
				if(null==unions){
					System.err.println("web_views找不到"+uniondate+"的数据");
					continue;
				}
				source = rs.getString("site");
				Site subsite=unions.getSites().get(source);
				if(null==subsite){
					subsite= new Site();
					unions.getSites().put(source, subsite);
				}
				because = getResuleSet(rs, "because");
				once_clicks = getResuleSet(rs, "clicks");
				if(1==because){
					subsite.setNum1(once_clicks);
				}else if(2==because){
					subsite.setNum2(once_clicks);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Unions> showUnions(Connection conn,String sql){
		PreparedStatement pt = null;
		ResultSet rs = null;
		List<Unions> unionslist=new ArrayList<Unions>();
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			Unions unions=null;
			while(rs.next()){
				unions=new Unions();
				unions.setUniondate(rs.getString("uniondate"));
				unions.setSearch_nums(getResuleSet(rs, "searchs"));
				unions.setShow_nums(getResuleSet(rs, "views"));
				unions.setClick_nums(getResuleSet(rs, "clicks"));
				unions.setSearch_nums1(getResuleSet(rs, "searchs1"));
				unions.setShow_nums1(getResuleSet(rs, "views1"));
				unions.setClick_nums1(getResuleSet(rs, "clicks1"));
				unions.setSearch_nums2(getResuleSet(rs, "searchs2"));
				unions.setShow_nums2(getResuleSet(rs, "views2"));
				unions.setClick_nums2(getResuleSet(rs, "clicks2"));
				unionslist.add(unions);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return unionslist;
	}
	
	public void showClickUnions(Connection conn,String sql,Unions unions){
		PreparedStatement pt = null;
		ResultSet rs = null;
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			int clicks = 0;
			String temp;
			int once_clicks = 0;
			String source;
			int because;
			int clicks1 = 0;
			int clicks2 = 0;
			while(rs.next()){
				once_clicks=getResuleSet(rs, "clicks");
				clicks+= once_clicks;
				temp=rs.getString("site");
				source=getSource(temp);
				because=getResuleSet(rs, "because");
				if(null==source){
					System.err.println("sql:"+sql+", site:"+temp);
					source=temp;
//					continue;
				}else{
					Site subsite=unions.getSites().get(source);
					if(null==subsite){
						subsite= new Site();
						unions.getSites().put(source, subsite);
					}
					if(1==because){
						subsite.setNum1(once_clicks);
						clicks1+=once_clicks;
					}else if(2==because){
						subsite.setNum2(once_clicks);
						clicks2+=once_clicks;
					}else {
						subsite.setNum3(once_clicks);
					}
				}
			}
			unions.setClick_nums1(clicks1);
			unions.setClick_nums2(clicks2);
			unions.setClick_nums(clicks);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showViewUnions(Connection conn,String sql,Unions unions){
		PreparedStatement pt = null;
		ResultSet rs = null;
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			int because=0;
			while(rs.next()){
				because=getResuleSet(rs, "because");
				if(1==because)
					unions.setShow_nums1(getResuleSet(rs, "views"));
				else if(2==because)
					unions.setShow_nums2(getResuleSet(rs, "views"));
			}
			unions.setShow_nums(unions.getShow_nums1()+unions.getShow_nums2());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showSearchUnions(Connection conn,String sql,Unions unions){
		PreparedStatement pt = null;
		ResultSet rs = null;
		try{
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			int because=0;
			while(rs.next()){
				because=getResuleSet(rs, "because");
				if(1==because)
					unions.setSearch_nums1(getResuleSet(rs, "sos"));
				else if(2==because)
					unions.setSearch_nums2(getResuleSet(rs, "sos"));
			}
			unions.setSearch_nums(unions.getSearch_nums1()+unions.getSearch_nums2());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(null!=rs)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void doInsertWebunions(Connection conn,String sql){
		PreparedStatement pt = null;
		try {
			pt = conn.prepareStatement(sql);
			pt.execute();
		} catch (SQLException e) {
			System.out.println("error sql:"+sql);
			e.printStackTrace();
		}finally{
			try {
				if(null!=pt)
					pt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String createUnionSql(Unions unions){
        StringBuilder sql = new StringBuilder();
        sql.append("insert ignore into web_views (uniondate,searchs,searchs1,searchs2,views,views1,views2,clicks,clicks1,clicks2");
        sql.append(") values ('");
        sql.append(unions.getUniondate());
        sql.append("',");
        sql.append(unions.getSearch_nums());
        sql.append(",");
        sql.append(unions.getSearch_nums1());
        sql.append(",");
        sql.append(unions.getSearch_nums2());
        sql.append(",");
        sql.append(unions.getShow_nums());
        sql.append(",");
        sql.append(unions.getShow_nums1());
        sql.append(",");
        sql.append(unions.getShow_nums2());
        sql.append(",");
        sql.append(unions.getClick_nums());
        sql.append(",");
        sql.append(unions.getClick_nums1());
        sql.append(",");
        sql.append(unions.getClick_nums2());
        sql.append(")");
        return sql.toString();
	}
	
	public String createSql(Unions unions){
        StringBuilder sql = new StringBuilder();
        for(Entry<String, Site> entry:unions.getSites().entrySet()){
        	if(0!=entry.getValue().getNum1()){
	        	sql.append("insert ignore into web_sites (uniondate,site,because,clicks) values ('");
	        	sql.append(unions.getUniondate());
	            sql.append("','");
	            sql.append(entry.getKey());
	            sql.append("',1,");
	            sql.append(entry.getValue().getNum1());
	            sql.append(");");
        	}
        	if(0!=entry.getValue().getNum2()){
            	sql.append("insert ignore into web_sites (uniondate,site,because,clicks) values ('");
            	sql.append(unions.getUniondate());
                sql.append("','");
                sql.append(entry.getKey());
                sql.append("',2,");
                sql.append(entry.getValue().getNum2());
                sql.append(");");
            	}
        	if(0!=entry.getValue().getNum3()){
            	sql.append("insert ignore into web_sites (uniondate,site,because,clicks) values ('");
            	sql.append(unions.getUniondate());
                sql.append("','");
                sql.append(entry.getKey());
                sql.append("',3,");
                sql.append(entry.getValue().getNum3());
                sql.append(");");
            	}
        }
        return sql.toString();
	}
	
	public Unions getWebunions(Connection conn,String date){
		Unions unions = new Unions();
		unions.setUniondate(date);
		date=date.replace("-", "_");
		showViewUnions(conn, "select because,sum(query_count) as views from query_"+date+" where result>0 and query_type='video' group by because;", unions);
		showSearchUnions(conn, "select because,sum(query_count) as sos from query_"+date+" where query_type='video' group by because;", unions);
		showClickUnions(conn, "select site,sum(click_count) clicks,because from click_"+date+" group by site,because order by site,because;", unions);
		return unions;
	}
	
	public void insertWebunions(String uniondate){
		Connection conn=null;
		try{
			conn=DataConn.getWebLogStatConn();
			Unions unions = getWebunions(conn,uniondate);
//			System.out.println(unions.toString());
			String sql=createUnionSql(unions);
//			System.out.println(sql);
			doInsertWebunions(conn, sql);
			
			String hsqls=createSql(unions);
			String[] hsqlarr = hsqls.split(";");
			for(String hsql:hsqlarr){
				if(!StringUtil.isNotNull(hsql)||hsql.equalsIgnoreCase(";"))continue;
//				System.out.println("-------------");
//				System.out.println(hsql);
				doInsertWebunions(conn, hsql);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			DataConn.releaseConn(conn);
		}
	}
	
	public static void main(String[] args) {
		String uniondate=null;
		if(null==args||args.length<1){
			uniondate=DataFormat.formatDate(DataFormat.getNextDate(new Date(),-1), DataFormat.FMT_DATE_YYYYMMDD);
		}else 
			uniondate=args[0];
		WebSiteUnion wu=new WebSiteUnion();
		wu.insertWebunions(uniondate);
	}
}
