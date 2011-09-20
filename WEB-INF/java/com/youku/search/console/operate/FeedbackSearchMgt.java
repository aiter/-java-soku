/**
 * 
 */
package com.youku.search.console.operate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;

import com.youku.search.console.pojo.FeedbackSearch;
import com.youku.search.console.pojo.FeedbackSearchPeer;
import com.youku.search.console.vo.FeedbackSearchVO;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;


/**
 * @author 1verge
 *
 */
public class FeedbackSearchMgt {

	private static FeedbackSearchMgt self = null;

	public  static synchronized FeedbackSearchMgt getInstance(){
		
		if(self == null){
			self = new FeedbackSearchMgt();
		}
		return self;
	}
	
	
	public int create(int error_type,String description,String keyword,int page,int creator,String url)
	{
		FeedbackSearch f = new FeedbackSearch();
		
		f.setErrorType(error_type);
		f.setDescription(description);
		f.setKeyword(keyword);
		f.setPage(page);
		f.setCreator(creator);
		f.setCreatetime(new Date());
		f.setUrl(url);
		
//		System.out.println(f);
		try {
			f.save();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return f.getId();
	}
	
	
	public int searchSize(String keyword,int order,int isPrecise,Connection conn){
		String wheresql="";
		if (keyword != null && keyword.trim().length() > 0){
			if(isPrecise==0)
				wheresql =" where keyword like '%"
					+ keyword + "%'";
			else wheresql =" where keyword = '"
				+ keyword + "'";
		}
		String sql=null;
		if(order==1){
			sql="select count(id) as num from feedback_search "+wheresql;
		}else if(order==2){
			sql="select count(*) as num from (select keyword from feedback_search "+wheresql+" group by keyword) a";
		}
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				return rs.getInt("num");
			}
		} catch (SQLException e) {
			System.out
			.println("[ERROR] search date ERROR in function searchSize! keyword="
					+ keyword);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return 0;
	}
	
	public List<FeedbackSearchVO> search(String keyword,int page,int pagesize,int order,int isPrecise,Connection conn){
		List<FeedbackSearchVO> fbs=new ArrayList<FeedbackSearchVO>();
		String wheresql="";
		if (keyword != null && keyword.trim().length() > 0){
			if (keyword != null && keyword.trim().length() > 0){
				if(isPrecise==0)
					wheresql =" where keyword like '%"
						+ keyword + "%'";
				else wheresql =" where keyword = '"
					+ keyword + "'";
			}
		}
		String sql=null;
		if(order==1){
			sql="select * from feedback_search "+wheresql+" order by createtime desc limit " + (page - 1) * pagesize + "," + pagesize;
		}else if(order==2){
			sql="select *,count(id) as num,max(createtime) as maxcreatetime from feedback_search "+wheresql+" group by keyword order by num desc limit " + (page - 1) * pagesize + "," + pagesize;
		}
//		System.out.println("sql:"+sql);
		PreparedStatement pt = null;
		ResultSet rs = null;
		FeedbackSearchVO fb=null;
		try {
			pt = conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				fb=new FeedbackSearchVO();
				fb.setId(rs.getInt("id"));
				fb.setKeywords(rs.getString("keyword"));
				fb.setError_type(rs.getInt("error_type"));
				fb.setDescription(rs.getString("description"));
				fb.setPageno(rs.getInt("page"));
				fb.setCreator(rs.getInt("creator"));
				fb.setUrl(rs.getString("url"));
				if(order==1)
					fb.setCreatetime(rs.getString("createtime"));
				else if(order==2){
					fb.setCreatetime(rs.getString("maxcreatetime"));
					fb.setNum(rs.getInt("num"));
				}
				fbs.add(fb);
			}
		} catch (SQLException e) {
			System.out
			.println("[ERROR] search date ERROR in function search! keyword="
					+ keyword);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return fbs;
	}
	
	public FeedbackSearch getFeedbackSearch(int id,Connection conn) throws NoRowsException, TooManyRowsException, TorqueException{
		return FeedbackSearchPeer.retrieveByPK(id,conn);
	}
	
	public void delete(String id,int order,Connection conn) throws TorqueException{
		String sql="";
		if(order==1){
			sql="delete from feedback_search where id="+id;
		}else if(order==2){
			FeedbackSearch fbs=getFeedbackSearch(DataFormat.parseInt(id,-1), conn);
			if(null!=fbs) sql="delete from feedback_search where keyword='"+fbs.getKeyword()+"'";
		}
		if(null!=sql&&sql.trim().length()>0)
			FeedbackSearchPeer.executeStatement(sql,conn);
	}
	
	public void delete(String[] ids,int order,Connection conn) throws TorqueException{
		if(null!=ids&&ids.length>0){
			for(String id:ids)
				delete(id, order, conn);
		}
	}
}
