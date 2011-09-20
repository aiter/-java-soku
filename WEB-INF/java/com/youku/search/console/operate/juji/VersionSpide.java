package com.youku.search.console.operate.juji;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.console.pojo.PlayVersionPeer;
import com.youku.search.console.pojo.Teleplay;
import com.youku.search.console.pojo.TeleplayPeer;
import com.youku.search.console.pojo.TempPlayVersion;
import com.youku.search.console.pojo.TempPlayVersionPeer;
import com.youku.search.console.teleplay.TeleplayQuery;
import com.youku.search.console.teleplay.VarietyQuery;
import com.youku.search.console.vo.VersionSpideVO;

public class VersionSpide {
	
	static Log logger = LogFactory.getLog(VersionSpide.class);

	private VersionSpide() {
	}

	private static VersionSpide instance = null;

	public static synchronized VersionSpide getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new VersionSpide();
			return instance;
		}
	}
	
	public List<Teleplay> getValidTeleplay(int isValid) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(TeleplayPeer.IS_VALID,isValid);
		return TeleplayPeer.doSelect(criteria);
	}
	
	public boolean nameExistInVersionNames(String vn,int tid) throws Exception{
		String sql="select * from play_version where name like '%"+vn+"%' and fk_teleplay_id="+tid;
//		System.out.println(sql);
		List<Record> res = PlayVersionPeer.executeQuery(sql,"searchteleplay");
		if(null!=res&&res.size()>0)
			return true;
		return false;
	}
	
	public boolean nameExistInTempVersionNames(String vn,int tid) throws Exception{
		Criteria criteria = new Criteria();
		criteria.add(TempPlayVersionPeer.FK_TELEPLAY_ID,tid);
		criteria.add(TempPlayVersionPeer.NAME,vn);
		List<TempPlayVersion> pnl=null;
		pnl=TempPlayVersionPeer.doSelect(criteria);
		if(pnl!=null&&pnl.size()>0)
			return true;
		else return false;
	}
	
	public void versionNameSpide(){
		TeleplayQuery tq=new TeleplayQuery();
		VarietyQuery vq=new VarietyQuery();
		String[] versionNames=null;
		List<Integer> il=null;
		try {
			List<Teleplay> tl=getValidTeleplay(1);
			for(Teleplay t:tl){
				try{
					Set<String> tns = PlayNameMgt.getInstance().getTeleplayNameByIdReturnSet(t.getId());
					if(null==tns||tns.size()<1)continue;
					if(t.getSubcate()!=2078){
						versionNames=tq.getVersionNames(tns);
						if(null==versionNames||versionNames.length<2)continue;
						for(String vn:versionNames){
							if(null==vn||vn.trim().length()<1)continue;
							if(nameExistInVersionNames(vn, t.getId()))
								continue;
							if(nameExistInTempVersionNames(""+vn,t.getId()))
								continue;
							else addTempVersion(vn,t.getCate(),t.getSubcate(),t.getId());
						}
					}else{
						il=vq.getVersions(tns);
						if(null==il||il.size()<2)continue;
						for(int vn:il){
							if(nameExistInVersionNames(""+vn,t.getId()))
								continue;
							if(nameExistInTempVersionNames(""+vn,t.getId()))
								continue;
							else addTempVersion(""+vn,t.getCate(),t.getSubcate(),t.getId());
						}
					}
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addTempVersion(String vn,int cate,int subcate,int tid) throws Exception{
		TempPlayVersion tpv=new TempPlayVersion();
		tpv.setFkTeleplayId(tid);
		tpv.setCate(cate);
		tpv.setSubcate(subcate);
		tpv.setName(vn);
		tpv.save();
	}
	
	public List<VersionSpideVO> search(String keyword,int status,int page,int pagesize,Connection conn){
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " and b.name like '%"
					+ keyword + "%'";
		String sql="select a.id,a.fk_teleplay_id,a.name,a.status,a.update_time,b.name from temp_play_version a,play_name b where a.fk_teleplay_id=b.fk_teleplay_id and b.is_main=1 and a.status="+status+whereHql+" order by a.fk_teleplay_id limit " + (page - 1) * pagesize + "," + pagesize;
		List<VersionSpideVO> vbvos=new ArrayList<VersionSpideVO>();
		VersionSpideVO vbvo=null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement(sql);
			rs=pt.executeQuery();
			while(rs.next()){
				vbvo=new VersionSpideVO();
				vbvo.setId(rs.getInt("a.id"));
				vbvo.setFkTeleplayId(rs.getInt("a.fk_teleplay_id"));
				vbvo.setVersionName(rs.getString("a.name"));
				vbvo.setStatus(status);
				vbvo.setUpdate_time(rs.getString("a.update_time"));
				vbvo.setTeleName(rs.getString("b.name"));
				vbvos.add(vbvo);
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
		return vbvos;
	}
	
	public int searchSize(String keyword,int status,Connection conn){
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = " and b.name like '%"
					+ keyword + "%'";
		String sql="select count(a.id) as num from temp_play_version a,play_name b where a.fk_teleplay_id=b.fk_teleplay_id and b.is_main=1 and a.status="+status+whereHql+" order by a.fk_teleplay_id";
		PreparedStatement pt = null;
		ResultSet rs = null;
		try {
			pt = conn.prepareStatement(sql);
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
	
	public TempPlayVersion getTempPlayVersion(int id) throws NoRowsException, TooManyRowsException, TorqueException{
		return TempPlayVersionPeer.retrieveByPK(id);
	}
	
	public void updateTempPlayVersion(TempPlayVersion tpv,int status) throws TorqueException{
		tpv.setStatus(status);
		TempPlayVersionPeer.doUpdate(tpv);
	}
}
