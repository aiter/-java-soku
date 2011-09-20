package com.youku.search.console.operate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import com.youku.search.console.pojo.Trietreerecommend;
import com.youku.search.console.pojo.TrietreerecommendPeer;
import com.youku.search.util.DataFormat;

public class TrietreerecommendOperator {
	
	public Trietreerecommend getTrietreerecommend(int id,Connection conn) throws NoRowsException, TooManyRowsException, TorqueException{
		return TrietreerecommendPeer.retrieveByPK(id, conn);
	}
	
	public void save(Trietreerecommend t,Connection conn) throws TorqueException{
		t.save(conn);
	}
	
	public void update(Trietreerecommend t,Connection conn) throws TorqueException{
		TrietreerecommendPeer.executeStatement("update trieTreeRecommend set keyword='"+t.getKeyword()+"',keyword_py='"+t.getKeywordPy()+"',recomend_type='"+t.getRecomendType()+"',query_count="+t.getQueryCount()+",result="+t.getResult()+",starttime='"+DataFormat.formatDate(t.getStarttime(), DataFormat.FMT_DATE_YYYYMMDD)+"',endtime='"+DataFormat.formatDate(t.getEndtime(), DataFormat.FMT_DATE_YYYYMMDD)+"',creator='"+t.getCreator()+"',createtime='"+t.getCreatetime()+"' where id="+t.getId(),conn);
	}
	
	public void delete(String id,Connection conn) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(TrietreerecommendPeer.ID,id);
		TrietreerecommendPeer.doDelete(criteria,conn);
	}
	
	public void deleteArrays(String[] ids,Connection conn){
		for(String id:ids){
			try {
				delete(id, conn);
			} catch (TorqueException e) {
				e.printStackTrace();
			}
		}
	}
	
	public List<Trietreerecommend> search(String keyword,int page,int pagesize,Connection conn){
		List<Trietreerecommend> tl=new ArrayList<Trietreerecommend>();

		Criteria criteria = new Criteria();
		if (keyword != null && keyword.trim().length() > 0)
			criteria.add(TrietreerecommendPeer.KEYWORD,(Object)("%"+keyword+"%"),SqlEnum.LIKE);
		criteria.setLimit(pagesize);
		criteria.setOffset((page - 1) * pagesize);
		try {
			tl=TrietreerecommendPeer.doSelect(criteria,conn);
			
		} catch (TorqueException e) {
			e.printStackTrace();
		}
		
		return tl;
	}
	
	public int searchSize(String keyword,Connection conn){
		String whereHql="";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " where keyword like ? ";
		String sql="select count(*) as num from trieTreeRecommend "+whereHql;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement(sql);
			if (keyword != null && keyword.trim().length() > 0){
				pt.setString(1, "%"+keyword+"%");
			}
			rs=pt.executeQuery();
			if(rs.next()){
				return rs.getInt("num");
			}
		} catch (SQLException e) {
			System.out
			.println("[ERROR] search date ERROR in function searchSize! keyword="
					+ keyword);
			e.printStackTrace();
		} finally {
			try {
				if (null != rs)
					rs.close();
				if (null != pt)
					pt.close();
			} catch (SQLException e) {
				System.out
						.println("[ERROR] connection close error in function searchSize!");
				e.printStackTrace();
			}
		}
		return 0;
	}
}
