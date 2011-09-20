package com.youku.soku.shield;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import org.apache.torque.Torque;
import org.apache.torque.TorqueException;
import org.apache.torque.util.Criteria;

import com.youku.search.recomend.DataConn;
import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;
import com.youku.soku.manage.entity.HitRolesConstants;
import com.youku.soku.manage.entity.ShieldWordConstants;
import com.youku.soku.manage.torque.ShieldWordRelation;
import com.youku.soku.manage.torque.ShieldWords;
import com.youku.soku.manage.torque.ShieldWordsPeer;
import com.youku.soku.manage.util.JFConverter;

public class ImportShieldWords {
	
	public void importWords() {
		Connection filterconn = null;
		JFConverter converter = new JFConverter();
		
		String sql="select a.filter_words,a.strategy_addin, a.strategy from t_filter_bad_words a,t_filter_bad_words_scope b where a.pk_filter_bad_words=b.filter_bad_words_id and b.scope='search'";
		PreparedStatement pt=null;
		ResultSet rs=null;
		try {
			pt=filterconn.prepareStatement(sql);
			rs=pt.executeQuery();
			String filter_words;
			int i=0;
			while(rs.next()){
				
				i++;
				filter_words=MyUtil.getString(rs.getString("a.filter_words"));
				String[] single_filter_words=filter_words.split(",");
				String strategy_addin=rs.getString("a.strategy_addin");
				System.out.println(filter_words);
				if("match".equalsIgnoreCase(strategy_addin)){
					
					/*for(String s:single_filter_words) {
						System.out.println(strategy_addin + rs.getString("strategy") + s);
						if(!checkWordsExist(s)) {
							continue;
						}
						ShieldWords sw = new ShieldWords();
						s = converter.traditionalized(s);
						s = s.toLowerCase();
						sw.setWord(s);
						
						sw.setType(ShieldWordConstants.SHIELDWORDTYPE);
						sw.setFkShieldCategoryId(2);
						sw.setHitRole(HitRolesConstants.CONTAINS_HITS);
						sw.setYoukuEffect(1);
						sw.setOthersEffect(1);
						System.out.println(strategy_addin + rs.getString("strategy") + s);
						sw.save();
					}*/
						
				}else{
					for(String s:single_filter_words) {
						if(!checkWordsExist(s)) {
							continue;
						}
						ShieldWords sw = new ShieldWords();
						
						sw.setWord(s);
						
						sw.setType(ShieldWordConstants.SHIELDWORDTYPE);
						sw.setFkShieldCategoryId(2);
						sw.setHitRole(HitRolesConstants.ACCURATE_HITS);
						sw.setYoukuEffect(1);
						sw.setOthersEffect(1);
						System.out.println(strategy_addin + rs.getString("strategy") + s);
						sw.save();
					}
				}
				
				
			}
		} catch (Exception e) {
			System.out.println("Date read from filter database error!");
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
	
	}
	
	public boolean checkWordsExist(String word) throws Exception {
		Criteria crit = new Criteria();
		crit.add(ShieldWordsPeer.WORD, word);
		
		List<ShieldWords> wordsList = ShieldWordsPeer.doSelect(crit);
		
		return wordsList.isEmpty();
	}
	
	public void importWordsCate() {

		Connection filterconn = null;

		String sql="select * from t_bad_words_cate";
		PreparedStatement pt=null;
		ResultSet rs=null;
		JFConverter converter = new JFConverter();
		try {
			pt=filterconn.prepareStatement(sql);
			rs=pt.executeQuery();
			String filter_words;
			int i=0;
			while(rs.next()){
				
				i++;
				filter_words=MyUtil.getString(rs.getString("word"));
				int cate = rs.getInt("cates");
				
				System.out.println(filter_words + "     " + cate);
				ShieldWords sw = new ShieldWords();
				
				filter_words = converter.traditionalized(filter_words);
				filter_words = filter_words.toLowerCase();
				sw.setWord(filter_words);
				sw.setHitRole(HitRolesConstants.ACCURATE_HITS);
				sw.setType(ShieldWordConstants.SHIELDWORDTYPE);
				sw.setFkShieldCategoryId(4);
				sw.setYoukuEffect(1);
				sw.setOthersEffect(0);
				sw.save();
				ShieldWordRelation swr = new ShieldWordRelation();
				swr.setFkWordId(sw.getId());
				swr.setFkShieldChannelId(cate);
				swr.save();
			}
		} catch (Exception e) {
			System.out.println("Date read from filter database error!");
			e.printStackTrace();
		}finally{
			JdbcUtil.close(rs);
			JdbcUtil.close(pt);
		}
	
	
	}
	
	public static void main(String[] args) {
		try {
			Torque.init("/opt/search/WEB-INF/soku-conf/Torque.properties");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		new ImportShieldWords().importWords();
		//new ImportShieldWords().importWordsCate();
		
		try {
			Torque.shutdown();
		} catch (TorqueException e) {
			e.printStackTrace();
		}
	}
}
