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
import org.apache.torque.util.Criteria;

import com.youku.search.console.pojo.PlayName;
import com.youku.search.console.pojo.PlayNamePeer;
import com.youku.search.console.pojo.TeleplaySpide;
import com.youku.search.console.pojo.TeleplaySpidePeer;
import com.youku.search.console.vo.TeleCate;
import com.youku.search.console.vo.TeleplaySpideVO;
import com.youku.search.util.DataFormat;

public class TeleplaySpideMgt {
	private static TeleplaySpideMgt self = null;
	private TeleplaySpideMgt(){}
	public  static synchronized TeleplaySpideMgt getInstance(){
		
		if(self == null){
			self = new TeleplaySpideMgt();
		}
		return self;
	}
	
	public boolean save(String name) throws Exception{
			if(!isExistInPlayName(name)&&!isExistInTeleplaySpide(name)){
				TeleplaySpide telepayspide=new TeleplaySpide();
				telepayspide.setName(name);
				telepayspide.setCreatetime(DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD));
				telepayspide.setStatus(0);
				telepayspide.save();
				return true;
			}
			else return false;
	}
	
	public void executeSql(String sql){
		try {
			TeleplaySpidePeer.executeStatement(sql,"searchteleplay");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void deleteWhoNotExist() throws TorqueException{
		executeSql("delete from teleplay_spide where status=0 and name in (select name from play_name)");
	}
	
	public boolean isExistInPlayName(String name) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(PlayNamePeer.NAME, name);
		List<PlayName> l = null;
		l = PlayNamePeer.doSelect(criteria);
		if (null != l && l.size() > 0) {
			return true;
		}else return false;
	}
	
	public boolean isExistInTeleplaySpide(String name) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(TeleplaySpidePeer.NAME, name);
		List<TeleplaySpide> l = null;
		l = TeleplaySpidePeer.doSelect(criteria);
		if (null != l && l.size() > 0) {
			return true;
		}else return false;
	}
	
	public TeleplaySpide getTeleplaySpideByID(int id) throws NoRowsException, TooManyRowsException, TorqueException{
		return TeleplaySpidePeer.retrieveByPK(id);
	}
	
	public void updateTeleplaySpide(TeleplaySpide ts,int status) throws TorqueException{
		ts.setStatus(status);
		TeleplaySpidePeer.doUpdate(ts);
	}
	
	public List<TeleplaySpideVO> search(String keyword,int status,int page,int pagesize,Connection conn){
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " and name like '%"
					+ keyword + "%'";
		String sql="select id ,name,createtime from teleplay_spide where status="+status+whereHql+" order by id limit " + (page - 1) * pagesize + "," + pagesize;
		List<TeleplaySpideVO> fbvos=new ArrayList<TeleplaySpideVO>();
		TeleplaySpideVO fbvo=null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				fbvo=new TeleplaySpideVO();
				fbvo.setId(rs.getInt("id"));
				fbvo.setName(rs.getString("name"));
				fbvo.setStatus(status);
				fbvo.setCreatetime(rs.getString("createtime"));
				fbvos.add(fbvo);
			}
		} catch (SQLException e) {
			System.out
			.println("[ERROR] search date ERROR in function search! keyword="
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
						.println("[ERROR] connection close error in function search!");
				e.printStackTrace();
			}
		}
		return fbvos;
	}
	
	public int searchSize(String keyword,int status,Connection conn){
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " and name like '%"
					+ keyword + "%'";
		String sql="select count(*) as num from teleplay_spide where status="+status+whereHql;
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
	
	public static void main(String[] args) {
		 Channel.listInit();
		 TeleCate tc=new TeleCate();
		 tc.setViewbeancate();
		 System.out.println(tc.getViewcatemap().get(100));
	}
}
