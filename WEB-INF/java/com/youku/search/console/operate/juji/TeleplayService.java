package com.youku.search.console.operate.juji;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.youku.search.console.operate.juji.ExcludeMgt;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.PlayName;
import com.youku.search.console.pojo.Teleplay;
import com.youku.search.console.vo.SingleTeleplay;
import com.youku.search.console.vo.SingleVersion;
import com.youku.search.console.vo.TeleUpdateVO;
import com.youku.search.util.JdbcUtil;
public class TeleplayService {
	static Log logger = LogFactory.getLog(TeleplayService.class);

	private TeleplayService() {
	}

	private static TeleplayService instance = null;

	public static synchronized TeleplayService getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new TeleplayService();
			return instance;
		}
	}
	
	public void updateTeleplayVersionCount(){
		List<Teleplay> teleplays = TeleplayMgt.getInstance().getAllTeleplay();
		if(null==teleplays||teleplays.size()<1) return;
		int count = 0;
		for(Teleplay t:teleplays){
			count = PlayVersionMgt.getInstance().getVersionCount(t.getId());
			TeleplayMgt.getInstance().executeSql("update teleplay set version_count="+count+" where id="+t.getId());
		}
	}
	
	public List<SingleTeleplay> searchTeleplayByNameAndStatus(String keyword,
			int status,int isPrecise, int page, int pagesize,Connection conn) {
		List<SingleTeleplay> stl = new ArrayList<SingleTeleplay>();
		String sql = "";
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0){
			if(isPrecise!=1)
				whereHql = "and a.id in (select fk_teleplay_id from play_name where name like ? )";
			else whereHql = "and a.id in (select fk_teleplay_id from play_name where name =?)";
		}
		if (status == 0)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1 "
					+ whereHql + " order by aid";
		else if (status == 1)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
					+ whereHql
					+ " and a.id in (select distinct fk_teleplay_id from episode where video_id='') order by aid";
		else if (status == 2)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
					+ whereHql
					+ "  and a.id in (select distinct c.fk_teleplay_id from play_version c where c.fixed=0) order by aid";
		else if (status == 3)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
					+ whereHql
					+ "  and a.id in (select distinct c.fk_teleplay_id from play_version c where c.fixed=1) order by aid";
		else if (status == 4)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
					+ whereHql
					+ " and a.id not in (select c.fk_teleplay_id from play_version c,episode d where c.id=d.fk_version_id) order by aid";
		else if(status==5)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
				+ whereHql
				+ " and a.is_valid=1  order by aid";
		else if(status==6)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where 1=1  "
				+ whereHql
				+ " and a.is_valid=0  order by aid";
		else if(status==7)
			sql = "select a.id as aid,a.is_valid as isvalid from teleplay a where a.subcate=2078  "
				+ whereHql
				+ " order by aid";
		else return stl;
		sql = sql + " limit " + (page - 1) * pagesize + "," + pagesize;
		PreparedStatement pt = null;
		ResultSet rs = null;
		SingleTeleplay st = null;
		SingleVersion sv = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		int aid = -1;
		try {
			pt = conn.prepareStatement(sql);
			if (keyword != null && keyword.trim().length() > 0){
				if(isPrecise!=1)
					pt.setString(1, "%"+keyword+"%");
				else pt.setString(1, keyword);
			}
			rs = pt.executeQuery();
			while (rs.next()) {
					aid=rs.getInt("aid");
					st = new SingleTeleplay();
					stl.add(st);
					st.setId(aid);
					st.setIsvalid(rs.getInt("isvalid"));
					searchPlayNameByTeleplayid(aid, st);
			}
			if (null != stl && stl.size() > 0) {
				String tempsql = "";
				String sql1 = "";
				String tempgroup = " order by c.order_id asc";
				if (status == 1) {
					tempsql = "select distinct c.* from play_version c,episode d where c.id=d.fk_version_id and d.video_id='' and c.fk_teleplay_id=";
				}
				else if (status == 2) {
					tempsql = "select c.* from play_version c where c.fixed=0 and c.fk_teleplay_id=";
				}
				else if (status == 3) {
					tempsql = "select c.* from play_version c where c.fixed=1 and c.fk_teleplay_id=";
				}
				else if (status == 4 || status == 0||status==5||status==6||status==7)
					tempsql = "select c.* from play_version c where c.fk_teleplay_id=";
				else return stl;
				for (int i = 0; i < stl.size(); i++) {
					sql1 = tempsql + stl.get(i).getId() + tempgroup;
					pst = conn.prepareStatement(sql1);
					rs1 = pst.executeQuery();
					while (rs1.next()) {
						sv = new SingleVersion();
						sv.setPid(rs1.getInt("c.id"));
						sv.setCate(rs1.getInt("c.cate"));
						sv.setSubcate(rs1.getInt("c.subcate"));
						sv.setVersionname(rs1.getString("c.name"));
						sv.setAlias(rs1.getString("c.alias"));
						sv.setFixed(rs1.getInt("c.fixed"));
						sv.setTotal_Count(rs1.getInt("c.total_count"));
						sv.setEpisode_count(rs1.getInt("c.episode_count"));
						sv.setCatestr();
						sv.setSubcatestr();
						stl.get(i).getSvl().add(sv);
					}
				}

			}
		} catch (Exception e) {
			logger.error(sql,e);
		} finally {
			JdbcUtil.close(rs1);
			JdbcUtil.close(pst);
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return stl;
	}
	public int searchTeleplayMaxSizeByNameAndStatus(String keyword, int status,int isPrecise,Connection conn) {
		String sql = "";
		String whereHql = "";
		if (keyword != null && keyword.trim().length() > 0){
			if(isPrecise!=1)
				whereHql = "and id in (select fk_teleplay_id from play_name where name like '%"+keyword+"%' )";
			else whereHql = "and id in (select fk_teleplay_id from play_name where name ='"+keyword+"')";
		}
		if (status == 0)
			sql = "select count(*) as num from teleplay where 1=1 " + whereHql;
		else if (status == 1)
			sql = "select count(*) as num from teleplay where 1=1 "
					+ whereHql
					+ " and id in (select distinct fk_teleplay_id from episode where video_id='')";
		else if (status == 2)
			sql = "select count(*) as num from teleplay where 1=1 "
					+ whereHql
					+ "  and id in (select distinct fk_teleplay_id from play_version where fixed=0)";
		else if (status == 3)
			sql = "select count(*) as num from teleplay where 1=1 "
					+ whereHql
					+ "  and id in (select distinct fk_teleplay_id from play_version where fixed=1)";
		else if (status == 4)
			sql = "select count(*) as num from teleplay where 1=1 "
					+ whereHql
					+ " and id not in (select c.fk_teleplay_id from play_version c,episode d where c.id=d.fk_version_id)";
		else if(status==5)
			sql = "select count(*) as num from teleplay where is_valid=1 "
				+ whereHql;
		else if(status==6)
			sql = "select count(*) as num from teleplay where  is_valid=0 "
				+ whereHql;
		else if(status==7)
			sql = "select count(*) as num from teleplay where  subcate=2078 "
				+ whereHql;
		else return 0;
		return TeleplayMgt.getInstance().searchTeleplaySizeReturnNum(sql);
	}
	
	public List<SingleTeleplay> searchAllTeleplay(int page, int pagesize,Connection conn) {
		List<SingleTeleplay> stl = new ArrayList<SingleTeleplay>();
		String sql = "select a.id as aid,a.is_valid as isvalid from teleplay a order by aid limit "
				+ (page - 1) * pagesize + "," + pagesize;
		PreparedStatement pt = null;
		ResultSet rs = null;
		SingleTeleplay st = null;
		SingleVersion sv = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		int aid = -1;
		try {
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			while (rs.next()) {
					aid = rs.getInt("aid");
					st = new SingleTeleplay();
					stl.add(st);
					st.setId(aid);
					st.setIsvalid(rs.getInt("isvalid"));
					searchPlayNameByTeleplayid(aid, st);
				}
			if (null != stl && stl.size() > 0) {
				for (int i = 0; i < stl.size(); i++) {
					String sql1 = "select * from play_version where fk_teleplay_id="
							+ stl.get(i).getId() +" order by order_id asc";
					pst = conn.prepareStatement(sql1);
					rs1 = pst.executeQuery();
					while (rs1.next()) {
						sv = new SingleVersion();
						sv.setPid(rs1.getInt("id"));
						sv.setCate(rs1.getInt("cate"));
						sv.setSubcate(rs1.getInt("subcate"));
						sv.setVersionname(rs1.getString("name"));
						sv.setAlias(rs1.getString("alias"));
						sv.setFixed(rs1.getInt("fixed"));
						sv.setTotal_Count(rs1.getInt("total_count"));
						sv.setEpisode_count(rs1.getInt("episode_count"));
						sv.setCatestr();
						sv.setSubcatestr();
						stl.get(i).getSvl().add(sv);
					}
				}

			}
		} catch (SQLException e) {
			logger.error(sql,e);
			return null;
		} finally {
			JdbcUtil.close(rs1);
			JdbcUtil.close(pst);
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return stl;
	}
	
	public void searchPlayNameByTeleplayid(int tid,SingleTeleplay st) {
		List<PlayName> pns = PlayNameMgt.getInstance().getPlayNamesByTeleplayID(tid);
		if(null==pns) return ;
		StringBuffer sb = new StringBuffer();
		for(PlayName pn:pns){
			if(StringUtils.isBlank(pn.getName()))
				continue;
			if(1==pn.getIsMain()){
				st.setKeyword(pn.getName());
				st.setBid(pn.getId());
			}else{
				if(sb.length()>0)
					sb.append("|");
				sb.append(pn.getName());
				
			}
		}
		if (null != st)
			st.setAliasStr(sb.toString());
	}
	
	public TeleUpdateVO searchTeleplayByID(int id,Connection conn) {
		TeleUpdateVO tuvo = null;
		String sql = "select a.* ,b.id as bid,b.name as bname, b.is_main as ismain from teleplay a ,play_name b where a.id=b.fk_teleplay_id and a.id="
				+ id + " order by b.is_main";
		PreparedStatement pt = null;
		ResultSet rs = null;
		int ismain = 0;
		try {
			pt = conn.prepareStatement(sql);
			rs = pt.executeQuery();
			StringBuffer sb = new StringBuffer();;
			while (rs.next()) {
				ismain = rs.getInt("ismain");
				if(1==ismain){
					tuvo = new TeleUpdateVO();
					tuvo.setVersion_count(rs.getInt("version_count"));
					tuvo.setIs_valid(rs.getInt("is_valid"));
					tuvo.getTc().setSubcate(rs.getInt("subcate"));
					tuvo.getTc().setCate(rs.getInt("cate"));
					tuvo.getTc().setViewbeancate();
					tuvo.setTid(id);
					tuvo.setKeyword(rs.getString("bname"));
					tuvo.setPid(rs.getInt("bid"));
					tuvo.setAliasStr(sb.toString());
					tuvo.setExclude(ExcludeMgt.getInstance().getExclude(id));
				}else{
					if(sb.length()>0)
						sb.append("|");
					sb.append(rs.getString("bname"));
					
				}
			}
		} catch (Exception e) {
			logger.error(sql,e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
		return tuvo;
	}
	
	public String getfullWords(int eid,boolean isView) {
		StringBuffer sbf = new StringBuffer();
		Episode e = EpisodeMgt.getInstance().getEpisodeByID(eid);
		if(null==e||StringUtils.isBlank(e.getName())) return null;
		String tn = PlayNameMgt.getInstance().getTeleplayMainName(e.getFkTeleplayId());
		if(StringUtils.isBlank(tn)) return null;
		sbf.append(tn);
		String pn = null;
		if(isView)
			pn = PlayVersionMgt.getInstance().getViewNameFromVersion(e.getFkVersionId());
		else
			pn = PlayVersionMgt.getInstance().getVersionName(e.getFkVersionId());
		if(!StringUtils.isBlank(pn))
			sbf.append(pn);
		sbf.append(e.getName());
		if (null != sbf && sbf.length() > 0)
			return sbf.toString();
		else
			return null;
	}
}
