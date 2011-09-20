package com.youku.search.console.operate.juji;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.NoRowsException;
import org.apache.torque.TooManyRowsException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.workingdogs.village.Record;
import com.youku.search.console.operate.DataConn;
import com.youku.search.console.pojo.Episode;
import com.youku.search.console.pojo.Feedback;
import com.youku.search.console.pojo.FeedbackPeer;
import com.youku.search.console.pojo.PlayVersion;
import com.youku.search.console.vo.FeedBackHandleVO;
import com.youku.search.console.vo.FeedBackVO;
import com.youku.search.console.vo.FeedbackUnion;
import com.youku.search.console.vo.FeedbackUnion.SingleOperator;
import com.youku.search.util.DataFormat;
import com.youku.search.util.JdbcUtil;

public class FeedBackMgt {
	
	static Log logger = LogFactory.getLog(FeedBackMgt.class);

	private FeedBackMgt() {
	}

	private static FeedBackMgt instance = null;

	public static synchronized FeedBackMgt getInstance() {
		if (null != instance)
			return instance;
		else{
			instance =  new FeedBackMgt();
			return instance;
		}
	}
	
	public boolean save(Episode e,int errorType,String errorContent,String keyword){
		try {
			Feedback fb=getFeedback(e.getId(),e.getVideoId(),errorType);
		if(null!=fb){
			FeedbackPeer.executeStatement("update feedback set feednum=feednum+1 where id="+fb.getId(),"searchteleplay");
		}else{
			fb=new Feedback();
			fb.setFkVersionId(e.getFkVersionId());
			fb.setEpisodeId(e.getId());
			fb.setErrorType(errorType);
			fb.setErrorContent(errorContent);
			fb.setVideoId(DataFormat.parseInt(e.getVideoId()));
			fb.setKeyword(keyword);
			fb.setFeednum(1);
			fb.save();
		}
			return true;
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		}
	}
	
//	select * from (select *,sum(feednum) as totalnum,max(lastModefyDate) as m from feedback where status=0 group by keyword having totalnum<10) a where a.m<=(interval -20 day+ '2009-03-20')  order by a.totalnum desc,a.episode_id asc limit 10;
	
	public Feedback getFeedback(int episodeId,String videoId,int errorCode) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(FeedbackPeer.EPISODE_ID,episodeId);
		criteria.add(FeedbackPeer.VIDEO_ID,videoId);
		criteria.add(FeedbackPeer.ERROR_TYPE,errorCode);
		criteria.add(FeedbackPeer.STATUS,0);
		List<Feedback> fbs = null;
		fbs = FeedbackPeer.doSelect(criteria);
		if(fbs!=null&&fbs.size()>0)return fbs.get(0);
		else return null;
	}
	
	public Feedback getFeedback(int pkFeedbackId) throws NoRowsException, TooManyRowsException, TorqueException{
			return FeedbackPeer.retrieveByPK(pkFeedbackId);
	}
	
	public List<Feedback> getFeedback(int episodeId,int videoId,String descorder) throws TorqueException{
		Criteria criteria = new Criteria();
		criteria.add(FeedbackPeer.EPISODE_ID,episodeId);
		criteria.add(FeedbackPeer.VIDEO_ID,videoId);
		criteria.add(FeedbackPeer.STATUS,0);
		if(null!=descorder&&descorder.trim().length()>0)
			criteria.addDescendingOrderByColumn(FeedbackPeer.FEEDNUM);
		List<Feedback> fbs = null;
		fbs = FeedbackPeer.doSelect(criteria);
		if(fbs!=null&&fbs.size()>0)return fbs;
		else return null;
	}
	
	public List<FeedBackVO> search(String keyword,int page,int pagesize,int order){
		
		String whereHql = "where status=0 ";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql = whereHql+" and keyword like ? ";
		String orderStr=null;
		switch(order){
		case 0:orderStr=" order by totalnum desc,episode_id asc ";break;
		case 1:orderStr=" order by lastModefyDate desc,episode_id asc ";break;
		default:orderStr="";break;
		}
		String sql="select *,sum(feednum) as totalnum,max(lastModefyDate) as m from feedback "+whereHql+" group by keyword "+orderStr+" limit " + (page - 1) * pagesize + "," + pagesize;
		//System.out.println(sql);
		List<FeedBackVO> fbvos=new ArrayList<FeedBackVO>();
		FeedBackVO fbvo=null;
		PreparedStatement pt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = DataConn.getTeleplayConn();
			pt = conn.prepareStatement(sql);
			if (keyword != null && keyword.trim().length() > 0)
				pt.setString(1, "%"+keyword+"%");
			rs=pt.executeQuery();
			List<Feedback> fl=null;
			while(rs.next()){
				fbvo=new FeedBackVO();
				fbvo.setId(rs.getInt("id"));
				fbvo.setFkVersionId(rs.getInt("fk_version_id"));
				fbvo.setVideoId(rs.getInt("video_id"));
				fbvo.setEpisodeId(rs.getInt("episode_id"));
				fbvo.setKeyword(rs.getString("keyword"));
				if(rs.getInt("video_id")>0)
					fbvo.setUrl(EpisodeMgt.getInstance().getEpisodeUrl(rs.getInt("episode_id")));
				else fbvo.setUrl("");
				try {
					fl=getFeedback(fbvo.getEpisodeId(), fbvo.getVideoId(), ""+fbvo.getFeedbacknum());
				} catch (Exception e) {
					logger.error(sql,e);
				}
				if(null!=fl&&fl.size()>0)
					fbvo.setErrorContent(fl.get(0).getErrorContent());
				fbvo.setTotal(rs.getInt("totalnum"));
				fbvo.setLastModefyDate(rs.getString("m"));
				fbvos.add(fbvo);
			}
		} catch (Exception e) {
			logger.error(sql,e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
			JdbcUtil.close(conn);
		}
		return fbvos;
	}
	
	public int searchSize(String keyword){
		String whereHql = "where status=0 ";
		if (keyword != null && keyword.trim().length() > 0)
			whereHql =whereHql+ " and keyword like '%"+keyword+"%' ";
		String sql="select count(a.id) as num from (select id from feedback "+whereHql+" group by keyword ) a ";
		return searchFeedbackSizeReturnNum(sql);
	}
	
	public int searchFeedbackSizeReturnNum(String sql) {
		try {
			List<Record> res = FeedbackPeer.executeQuery(sql,"searchteleplay");
			if(null!=res&&res.size()>0)
				return res.get(0).getValue("num").asInt();
		} catch (Exception e) {
			logger.error(sql,e);
		}
		return 0;
	}
	
	public FeedBackHandleVO search(int episodeId,int pkFeedbackId) throws NoRowsException, TooManyRowsException, TorqueException{
		FeedBackHandleVO fbhvo=new FeedBackHandleVO();
		Feedback f=getFeedback(pkFeedbackId);
		if(null==f)return null;
		List<Feedback> fbl = null;
		Episode e=null;
		PlayVersion pv=null;
		fbl = getFeedback(episodeId, f.getVideoId(), FeedbackPeer.ID);
		if(null==fbl) return null;
		e=EpisodeMgt.getInstance().getEpisodeByID(episodeId);
		if(null==e) return null;
		pv=PlayVersionMgt.getInstance().getPlayVersionByID(e.getFkVersionId());
		fbhvo.setPkFeedbackId(pkFeedbackId);
		fbhvo.getEvo().setId(e.getId());
		
		if(null==pv)return null;
		fbhvo.getEvo().setSubcate(pv.getSubcate());
		if(pv.getSubcate()!=2078){
			fbhvo.getEvo().setName(f.getKeyword());
		}
		else{ 
			fbhvo.getEvo().setName(""+e.getOrderId());
			}
		if(e.getVideoId()!=null&&e.getVideoId().trim().length()>0)
			fbhvo.getEvo().setUrl("http://v.youku.com/v_show/id_"+e.getEncodeVideoId()+".html");
		else fbhvo.getEvo().setUrl("");
		FeedBackVO fbvo=null;
		int total=0;
		for(Feedback fb:fbl){
			fbvo=new FeedBackVO();
			fbvo.setErrorType(fb.getErrorType());
			fbvo.setErrorContent(fb.getErrorContent());
			fbvo.setFeedbacknum(fb.getFeednum());
			fbvo.setLastModefyDate(""+fb.getLastmodefydate());
			total=total+fb.getFeednum();
			fbhvo.getFeedbackVo().add(fbvo);
		}
		fbhvo.setTotal(total);
		return fbhvo;
	}
	
	public void deleteFeedbackByepisodeID(int episodeId,int status,String operator) throws TorqueException{
		FeedbackPeer.executeStatement("update feedback set status="+status+",operator='"+operator+"',operateDate='"+new Timestamp(System.currentTimeMillis())+"' where status=0 and episode_id="+episodeId, "searchteleplay");
	}
	
	public void deleteFeedback() throws TorqueException{
			FeedbackPeer.executeStatement("update feedback set status=1 ,operator='sys',operateDate='"+new Timestamp(System.currentTimeMillis())+"' where status=0 and episode_id not in (select id from episode)","searchteleplay");
			List<Integer> idl=getFeedback();
			for(int pkFeedbackId:idl){
				deleteFeedback(pkFeedbackId);
			}
			FeedbackPeer.executeStatement("update feedback set status=1 ,operator='sys',operateDate='"+new Timestamp(System.currentTimeMillis())+"' where status=0 and episode_id in (select id from episode where isLock=1)","searchteleplay");
	}
	
	public void deleteUpWeekFeedback() throws TorqueException{
		FeedbackPeer.executeStatement("delete from  feedback where status!=0 and operateDate<=(interval -10 day+'"+DataFormat.formatDate(new Date(), DataFormat.FMT_DATE_YYYYMMDD)+"')","searchteleplay");
	}
	
	public void deleteFeedback(int pkFeedbackId){
		try {
			FeedbackPeer.executeStatement("update feedback set status=1,operator='sys',operateDate='"+new Timestamp(System.currentTimeMillis())+"' where id="+pkFeedbackId, "searchteleplay");
		} catch (Exception e) {
			logger.error(pkFeedbackId,e);
		}
	}
	
	public List<Integer> getFeedback(){
		String sql="select a.id as id from feedback a,episode b where a.status=0 and a.episode_id=b.id and a.video_id!=b.video_id";
		List<Record> res = null;
		List<Integer> idl=new ArrayList<Integer>();
		try {
			res = FeedbackPeer.executeQuery(sql,"searchteleplay");
			if(null==res||res.size()<1)
				return idl;
			for(Record r:res){
				idl.add(r.getValue("id").asInt());
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return idl;
	}
	
	
	public List<String[]> getMergeFeedback(Connection conn){
		String sql="select id,episode_id,error_type,max(lastModefyDate) as m, count(*) as num,sum(feednum) as fenum from feedback where status=0 group by episode_id,error_type having num>1";
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String[]> idl=new ArrayList<String[]>();
		String[] ids;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			while(rs.next()){
				ids=new String[5];
				ids[0]=rs.getString("id");
				ids[1]=rs.getString("fenum");
				ids[2]=rs.getString("episode_id");
				ids[3]=rs.getString("error_type");
				ids[4]=rs.getString("m");
				idl.add(ids);
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pst);
		}
		return idl;
	}
	
	public void mergeFeedback(Connection conn){
		List<String[]> idls=getMergeFeedback(conn);
		for(String[] ids:idls){
		try {
			FeedbackPeer.executeStatement("delete from feedback where status=0 and id!="+ids[0]+" and episode_id="+ids[2]+" and error_type="+ids[3],conn);
			FeedbackPeer.executeStatement("update feedback set feednum="+ids[1]+",lastModefyDate='"+ids[4]+"' where id="+ids[0],conn);
		} catch (Exception e) {
			logger.error(e);
		}
		}
	}
	
	public List<FeedbackUnion> getUnionFeedback(Connection conn){
		String sql="select operator,status,count(n) as num,substring(a.o,1,10) as d from (select operator,status,count(*) as n,max(operateDate) as o from feedback where  status!=0 group by episode_id,status,operator,substring(operateDate,1,17) ) a group by operator,status,substring(a.o,1,17) order by substring(a.o,1,10),operator,status";
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<FeedbackUnion> fus=new ArrayList<FeedbackUnion>();
		FeedbackUnion fu = null;
		SingleOperator so = null;
		try {
			pst = conn.prepareStatement(sql);
			rs = pst.executeQuery();
			String d=null;
			String temp=null;
			String o=null;
			String ot=null;
			int deel=0;
			int del=0;
			int tempdeel=0;
			while(rs.next()){
				d=rs.getString("d");
				o=rs.getString("operator");
				tempdeel=rs.getInt("num");
				if(!d.equals(temp)){
					fu=new FeedbackUnion();
					fus.add(fu);
					fu.setOperateDate(d);
					temp=d;
					if(deel!=0||del!=0){
						so.setDealnum(deel);
						so.setDeletenum(del);
						deel=0;
						del=0;
					}else{
						deel=0;
						del=0;
					}
					so=fu.getSingleOperator();
					ot=o;
					fu.getSlo().add(so);
					so.setOperator(o);
				}
				if(!o.equals(ot)){
					if(deel!=0||del!=0){
						so.setDealnum(deel);
						so.setDeletenum(del);
						deel=0;
						del=0;
					}else{
						deel=0;
						del=0;
					}
					so=fu.getSingleOperator();
					ot=o;
					fu.getSlo().add(so);
					so.setOperator(o);
				}
				if(rs.getInt("status")==1)
					del=del+tempdeel;
				else deel=deel+tempdeel;
				
			}
			if(deel!=0||del!=0){
				so.setDealnum(deel);
				so.setDeletenum(del);
				deel=0;
				del=0;
			}
		} catch (SQLException e) {
			System.out.println("[ERROR] sql:"+sql);
			e.printStackTrace();
		} finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pst);
		}
		return fus;
	}
}
