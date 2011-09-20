/**
 * 
 */
package com.youku.top.index.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.torque.TorqueException;

import com.youku.top.recomend.FilterUtils;
import com.youku.search.util.JdbcUtil;
import com.youku.top.DataBase;
import com.youku.top.MyUtil;
import com.youku.top.index.entity.LogYouku;


public class LogManager {
	public static LogManager instance = null;
	
	public static LogManager getInstance()
	{
		if ( null == instance ){
			instance = new LogManager();
		}
		return instance;
	}
	
	private LogManager(){
		
	}
	
	public int getMaxId(String tableName) throws TorqueException
	{
		int result = 0;
		Connection conn = DataBase.getSearchStatConn();
		String sql = "select max(id) as id from "+tableName;
		Statement st = null;
		ResultSet rs = null;
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next())
			{
				result = rs.getInt("id");
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(st);
			JdbcUtil.close(conn);
		}
		
		return result;
	}
	
	public List<Document> getWords(int start,int end,String tableName,String beforeTableName,Connection conn)
	{
		String sql = "select * from "+ tableName +" where id>="+start + " and id<" + end + " and query_type='video' and source='youku'";
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String keyword = null;
			int query_count = 0;
			Document doc = null;
			while (rs.next())
			{

				keyword = rs.getString("keyword");
				query_count = rs.getInt("query_count");
				doc = rsToDocument(rs,keyword,query_count);
				if (doc != null){
					list.add(doc);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(st);
		}
		
		return list;
	}
	
	public List<Document> getWordsAndQuick(int start,int end,String tableName,String beforeTableName,Connection conn)
	{
		String sql = "select * from "+ tableName +" where id>="+start + " and id<" + end + " and query_type='video' and source='youku' and query_count > 15";
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			String keyword = null;
			int query_count = 0;
			Document doc = null;
			while (rs.next())
			{
				keyword = rs.getString("keyword");
				query_count = rs.getInt("query_count");
				doc = rsToDocument(rs,keyword,query_count);
				if (doc != null){
					LogYouku r = getQuickTopVideo(beforeTableName, keyword, conn);
					double rate1 = function_weight(query_count, r.query_count);
					doc.add(new Field("quick_top_rate",String.format("%.5f",rate1),Store.YES, Index.UN_TOKENIZED));
					doc.add(new Field("quick_top_rate_index",rate1>1?"1":"0",Store.YES, Index.UN_TOKENIZED));
					list.add(doc);
				}
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			JdbcUtil.close(rs);
			JdbcUtil.close(st);
		}
		
		return list;
	}
	
	private Document rsToDocument(ResultSet rs,String keyword,int query_count) throws SQLException
	{
		Document doc = null;
		int result = rs.getInt("result");
		if (result > 0 && keyword.length() > 1){
			
			//判断是否有禁忌词
			if (FilterUtils.isFilter(keyword))
				return null;
			
			doc = new Document();
			
			
			doc.add(new Field("id", rs.getString("id"), Store.NO, Index.UN_TOKENIZED));
			doc.add(new Field("keyword", keyword, Store.YES, Index.TOKENIZED));
			doc.add(new Field("keyword_py", MyUtil.formatFuzzyLetter(rs.getString("keyword_py")), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("query_type", rs.getString("query_type") , Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("query_count", String.valueOf(query_count), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("result", String.valueOf(result), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("query_count_index",query_count>50?"1":"0",Store.YES, Index.UN_TOKENIZED));
			
//			float boost = (float) (1 + Math.log10(query_count)/10+Math.log10(result)/100);
//			doc.setBoost(boost);
		}
		return doc;
	}
	
	private LogYouku getQuickTopVideo(String tableName,String keyword,Connection conn){
		LogYouku result = new LogYouku();
		String sql = "select * from "+ tableName +" where query_type='video' and source='youku' and keyword=? ";
		PreparedStatement pst = null;
		ResultSet rst = null;
		try
		{
			pst = conn.prepareStatement(sql);
			pst.setString(1, keyword);
			rst = pst.executeQuery();
			if (rst.next())
			{
				result.id = rst.getInt("id");
				result.keyword = keyword;
				result.keyword_py = rst.getString("keyword_py");
				result.query_count = rst.getInt("query_count");
				result.query_type = rst.getString("query_type");
				result.result = rst.getInt("result");
			}
		}catch(Exception e)
		{
			System.err.println("sql:"+sql+",key:"+keyword);
			e.printStackTrace();
		}
		finally
		{
			JdbcUtil.close(rst);
			JdbcUtil.close(pst);
		}
		return result;
	}
	
	private double function_weight(int query_count1,int query_count2){
		if(query_count2<15) return Math.abs(Math.log(query_count1*1.0/15));
		else if(query_count1<query_count2) return Math.abs(query_count1*1.0/query_count2);
		else if(query_count1==query_count2) return 1;
		else return Math.abs(Math.log10((query_count1*1.0/query_count2)*(query_count1-query_count2)));
	}
}
