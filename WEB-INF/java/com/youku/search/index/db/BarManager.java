/**
 * 
 */
package com.youku.search.index.db;

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

import com.youku.search.index.entity.Video;
import com.youku.search.util.Database;
import com.youku.search.util.MyUtil;

/**
 * @author william
 * 
 */
public class BarManager {
	
	protected org.apache.log4j.Logger _log = org.apache.log4j.Logger.getLogger("INDEXLOG");
	
	public static BarManager instance = null;

	public static final int subject_limit_number = 3;

	public static BarManager getInstance() {
		if (null == instance) {
			instance = new BarManager();
		}
		return instance;
	}

	private BarManager() {

	}

	public List<Document> getBars(int start, int end, Connection conn) {
		String sql = "select pk_bar,bar_name,count_subject from t_bar,t_bar_stat where t_bar.pk_bar=t_bar_stat.fk_bar and closed=0 and pk_bar>=" + start
				+ " and pk_bar<" + end;
		_log.info(sql);
		Statement st = null;
		ResultSet rs = null;
		List<Document> list = new ArrayList<Document>();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				Document doc = barRsToDocument(rs);
				list.add(doc);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}

		return list;
	}

	private Document barRsToDocument(ResultSet rs) throws SQLException {
		Document doc = new Document();
		doc.add(new Field("pk_bar", rs.getString("pk_bar"), Store.YES,
						Index.NO));
		doc.add(new Field("bar_name", MyUtil
				.getString(rs.getString("bar_name")), Store.YES,
				Index.TOKENIZED));
		
		float boost = computeBoost(rs.getInt("count_subject"));
		doc.add(new Field("boost", String.valueOf(boost), Store.YES,
				Index.NO));
		return doc;
	}
	
	private float computeBoost(int postcount)
	{
		if (postcount == 0)
			return 0f;
		else if (postcount > 0 && postcount <= 20)
			return 0.1f;
		else if (postcount > 20 && postcount <= 100)
			return 0.2f;
		else if (postcount > 100 && postcount <= 500)
			return 0.3f;
		else if (postcount > 500)
			return 0.4f;
		else
			return 0f;
	}
	
	private void getBarCatalog(Document doc,List<Integer> bar_catalog_ids,List<String> bar_catalog_names, Connection conn) {
		String sql = "select a.fk_catalog as a_fk_catalog ,b.catalog as b_catalog,b.parent_catalog as b_parent_catalog from t_bar_catalog_map a,t_bar_catalog b where a.fk_bar="
				+ doc.get("pk_bar") + " and a.fk_catalog=b.pk_catalog ";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				bar_catalog_ids.add(rs.getInt("a_fk_catalog"));
				bar_catalog_names.add(MyUtil.getString(rs.getString("b_catalog")));
				getParentCatalogList(rs.getInt("b_parent_catalog"),bar_catalog_ids,bar_catalog_names, conn);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}
	}

	public Document barCatalogToDocument(Document doc, Connection conn) {
		List<Integer> bar_catalog_ids=new ArrayList<Integer>();
		List<String> bar_catalog_names=new ArrayList<String>();
		getBarCatalog(doc,bar_catalog_ids,bar_catalog_names, conn);
		for (int i=bar_catalog_ids.size()-1,j=1;i>=0;i--,j++) {
			doc.add(new Field("bar_catalog_id_"+j, "" + bar_catalog_ids.get(i),
					Store.YES, Index.NO));
			doc.add(new Field("bar_catalog_name_"+j, bar_catalog_names.get(i),
					Store.YES, Index.NO));
		}
		return doc;
	}

	private void getParentCatalogList(int parentcatalog,
			List<Integer> bar_catalog_ids,List<String> bar_catalog_names, Connection conn) {
		ResultSet rs = null;
		Statement st = null;
		try {
			st = conn.createStatement();
			for (rs = getParentCatalog(parentcatalog, st); null != rs
					&& rs.next(); rs = getParentCatalog(parentcatalog, st)) {
				parentcatalog = rs.getInt("parent_catalog");
				bar_catalog_ids.add(rs.getInt("pk_catalog"));
				bar_catalog_names.add(MyUtil.getString(rs.getString("catalog")));
				if (null != rs)
					rs.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}
	}

	private ResultSet getParentCatalog(int parentcatalog, Statement st) {
		String sql = "select pk_catalog,catalog,parent_catalog from t_bar_catalog where pk_catalog="
				+ parentcatalog;
		ResultSet rs = null;
		try {
			rs = st.executeQuery(sql);
			return rs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

//	public Document getLastSubject(Document doc, Connection conn) {
//		String sql = "select pk_subject,subject,last_poster_id,last_poster_name,last_post_time from t_bar_subject where fk_bar="
//				+ doc.get("pk_bar")
//				+ " and deleted=0 order by last_post_time desc limit "
//				+ subject_limit_number;
//		Statement st = null;
//		ResultSet rs = null;
//		try {
//			st = conn.createStatement();
//			rs = st.executeQuery(sql);
//			int i=1;
//			while (rs.next()) {
//				doc.add(new Field("subject_id_"+i,
//						rs.getString("pk_subject"), Store.YES, Index.NO));
//				doc.add(new Field("subject_"+i, MyUtil.getString(rs
//						.getString("subject")), Store.YES, Index.NO));
//				doc.add(new Field("subject_"+i+"_last_poster_id", rs
//						.getString("last_poster_id"), Store.YES, Index.NO));
//				doc.add(new Field("subject_"+i+"_last_poster_name", rs
//						.getString("last_poster_name"), Store.YES, Index.NO));
//				doc.add(new Field("subject_"+i+"_last_post_time", rs
//						.getString("last_post_time"), Store.YES, Index.NO));
//				i++;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (rs != null)
//					rs.close();
//				if (st != null)
//					st.close();
//			} catch (SQLException e) {
//			}
//		}
//		return doc;
//	}

	private Document getLastVideo(Document doc, Connection conn) {
		String sql = "select b.videoid from t_bar a,t_bar_subject b where a.pk_bar="
				+ doc.get("pk_bar")
				+ " and a.pk_bar=b.fk_bar and b.videoid!=0 order by b.last_post_time desc limit "
				+ subject_limit_number;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			Video video = null;
			int i=1;
			while (rs.next()) {
				video = VideoManager.getInstance().getVideo(
						rs.getInt("b.videoid"));
				if (video != null) {
					doc.add(new Field("videoId_"+i, ""+video.vid, Store.YES,
							Index.NO));
					doc.add(new Field("videoLogo_"+i, video.logo, Store.YES,
							Index.NO));
					doc.add(new Field("encodeVid_"+i, video.encodeVid,
							Store.YES, Index.NO));
					doc.add(new Field("videoTitle_"+i, video.title,
							Store.YES, Index.NO));
					i++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}
		return doc;
	}

	public Document getBarStatus(Document doc, Connection conn) {
		String sql = "select * from t_bar_stat where fk_bar="
				+ doc.get("pk_bar");
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				doc.add(new Field("count_member", rs.getString("count_member"),
						Store.YES, Index.NO));
				doc.add(new Field("count_subject", rs
						.getString("count_subject"), Store.YES, Index.NO));
				doc.add(new Field("count_video", rs.getString("count_video"),
						Store.YES, Index.NO));
				doc.add(new Field("total_pv", rs.getString("total_pv"),
						Store.YES, Index.NO));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
			} catch (SQLException e) {
			}
		}
		return doc;
	}

	public int getMaxId() throws TorqueException {
		int result = 0;
		Connection conn = Database.getBarConnection();
		String sql = "select max(pk_subject) as pk_subject from t_bar_subject";
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				result = rs.getInt("pk_subject");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (st != null)
					st.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
			}
		}

		return result;
	}
}
