/**
 * 
 */
package com.youku.soku.index.manager.db;

import java.sql.Connection;
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

import com.youku.search.recomend.FilterUtils;
import com.youku.soku.util.DataBase;
import com.youku.soku.util.MyUtil;

/**
 * @author william
 *
 */
public class WordManager {
	public static WordManager instance = null;
	
	public static WordManager getInstance()
	{
		if ( null == instance ){
			instance = new WordManager();
		}
		return instance;
	}
	
	private WordManager(){
		
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
			try {
				if (rs!= null)rs.close();
				if (st!= null)st.close();
				if (conn!= null)conn.close();
			} catch (SQLException e) {
			}
		}
		
		return result;
	}
	
	public List<Document> getWords(int start,int end,String tableName,Connection conn)
	{
		String sql = "select * from "+ tableName +" where query_type='video' and source='youku' and id>="+start + " and id<" + end + " and result>10 and query_count>10";
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try
		{
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next())
			{
				Document doc = rsToDocument(rs);
				if (doc != null)
					list.add(doc);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				if (rs!= null)rs.close();
				if (st!= null)st.close();
			} catch (SQLException e) {
			}
		}
		
		return list;
	}
	
	private Document rsToDocument(ResultSet rs) throws SQLException
	{
		Document doc = null;
		int result = rs.getInt("result");
		String keyword = rs.getString("keyword");
		if (result > 0 && keyword.length() > 1){
			
			doc = new Document();
			
			int query_count = rs.getInt("query_count");
			doc.add(new Field("id", rs.getString("id"), Store.NO, Index.UN_TOKENIZED));
			doc.add(new Field("keyword", keyword, Store.YES, Index.TOKENIZED));
			doc.add(new Field("keyword_py_index", MyUtil.formatFuzzyLetter(rs.getString("keyword_py")), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("query_count", String.valueOf(query_count), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("result", String.valueOf(result), Store.YES, Index.UN_TOKENIZED));
			doc.add(new Field("query_count_index",query_count>10?"1":"0",Store.NO, Index.UN_TOKENIZED));
			
			
			float boost = (float) (1 + Math.log10(query_count)/10+Math.log10(result)/100);
			doc.setBoost(boost);
		}
		return doc;
	}
}
