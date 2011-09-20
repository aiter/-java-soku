package com.youku.top.topn;

import java.io.StringWriter;
import java.io.Writer;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.StringUtil;
import com.youku.top.JdbcTemplateFactoray;
import com.youku.top.topn.entity.KeywordComVO;
import com.youku.top.topn.entity.KeywordQueryVO;
import com.youku.top.topn.util.SeclectSort;
import com.youku.top.topn.entity.KeywordVO;

public class TopKeywordSortPrint {

	static Logger logger = Logger.getLogger(TopKeywordSortPrint.class);

	public List<KeywordQueryVO> getTwoTopKeywords(String uniondate,
			String before_uniondate,boolean isExistBeforeDate)  throws Exception{

		List<KeywordQueryVO> keywordList = getTopKeywords(uniondate);
		
		if(!isExistBeforeDate) return keywordList;
		
		List<KeywordVO> kvos = null;
		KeywordComVO kcvo = null;
		int insearchsub = 0;
		int outsearchsub = 0;
		int outclicksub = 0;
		for (KeywordQueryVO kqo : keywordList) {
			try{
				kvos = getBeforeKeywords(before_uniondate, kqo.getKeyword());
				for(KeywordVO kvo:kvos){
					if(null!=kvo){
						if(kvo.getCate()==-1){
							insearchsub = kqo.getInsearchs()-kvo.getSearchs();
							if(kqo.getInsearchs()>0){
								if(insearchsub==0){
									kqo.setIsrate("-");
								}
								if(insearchsub>0){
									kqo.setIsrate(String.format("%.2f",100.0*insearchsub/kqo.getInsearchs()));
									kqo.setIsrateIsIncre(true);
								}
								if(insearchsub<0){
									kqo.setIsrate(String.format("%.2f",100.0*insearchsub/kqo.getInsearchs()));
									kqo.setIsrateIsIncre(false);
								}
							}else
								kqo.setIsrate("/");
							outsearchsub = kqo.getOutsearchs() - kvo.getWeb_searchs();
							if(kqo.getOutsearchs()>0){
								if(outsearchsub==0){
									kqo.setOsrate("-");
								}else
									kqo.setOsrate(String.format("%.2f",100.0*outsearchsub/kqo.getOutsearchs()));
								if(outsearchsub>0){
									kqo.setOsrateIsIncre(true);
								}
								if(outsearchsub<0){
									kqo.setOsrateIsIncre(false);
								}
							}else
								kqo.setOsrate("/");
							outclicksub = kqo.getOutclicks() - kvo.getClicks();
							if(kqo.getOutclicks()>0){
								if(outclicksub==0){
									kqo.setOcrate("-");
								}else
									kqo.setOcrate(String.format("%.2f",100.0*outclicksub/kqo.getOutclicks()));
								if(outclicksub>0){
									kqo.setOcrateIsIncre(true);
								}
								if(outclicksub<0){
									kqo.setOcrateIsIncre(false);
								}
							}else
								kqo.setOcrate("/");
						}else{
							kcvo = kqo.getKeywords().get(kvo.getCate());
							if(null!=kcvo){
								insearchsub = kcvo.getInsearchs()-kvo.getSearchs();
								if(kcvo.getInsearchs()>0){
									if(insearchsub==0){
										kcvo.setIsrate("-");
									}
									if(insearchsub>0){
										kcvo.setIsrate(String.format("%.2f",100.0*insearchsub/kcvo.getInsearchs()));
										kcvo.setIsrateIsIncre(true);
									}
									if(insearchsub<0){
										kcvo.setIsrate(String.format("%.2f",100.0*insearchsub/kcvo.getInsearchs()));
										kcvo.setIsrateIsIncre(false);
									}
								}else
									kcvo.setIsrate("/");
								outsearchsub = kcvo.getOutsearchs() - kvo.getWeb_searchs();
								if(kcvo.getOutsearchs()>0){
									if(outsearchsub==0){
										kcvo.setOsrate("-");
									}else
										kcvo.setOsrate(String.format("%.2f",100.0*outsearchsub/kcvo.getOutsearchs()));
									if(outsearchsub>0){
										kcvo.setOsrateIsIncre(true);
									}
									if(outsearchsub<0){
										kcvo.setOsrateIsIncre(false);
									}
								}else
									kcvo.setOsrate("/");
								outclicksub = kcvo.getOutclicks() - kvo.getClicks();
								if(kcvo.getOutclicks()>0){
									if(outclicksub==0){
										kcvo.setOcrate("-");
									}else
										kcvo.setOcrate(String.format("%.2f",100.0*outclicksub/kcvo.getOutclicks()));
									if(outclicksub>0){
										kcvo.setOcrateIsIncre(true);
									}
									if(outclicksub<0){
										kcvo.setOcrateIsIncre(false);
									}
								}else
									kcvo.setOcrate("/");
							}
						}
					}
				}
				for(Entry<Integer, KeywordComVO> entry:kqo.getKeywords().entrySet()){
					for(KeywordComVO skcvo:entry.getValue().getKeywords()){
						insearchsub = getKeywordsUnionNum("select sum(query_count) from query_youku_"+before_uniondate+" where keyword='"+skcvo.getKeyword()+"'");
						insearchsub = skcvo.getInsearchs() - insearchsub;
						if(skcvo.getInsearchs()>0){
							if(insearchsub==0){
								skcvo.setIsrate("-");
							}
							if(insearchsub>0){
								skcvo.setIsrate(String.format("%.2f",100.0*insearchsub/skcvo.getInsearchs()));
								skcvo.setIsrateIsIncre(true);
							}
							if(insearchsub<0){
								skcvo.setIsrate(String.format("%.2f",100.0*insearchsub/skcvo.getInsearchs()));
								skcvo.setIsrateIsIncre(false);
							}
						}else
							skcvo.setIsrate("/");
						
						outsearchsub = getKeywordsUnionNum("select sum(query_count) from query_soku_"+before_uniondate+" where keyword='"+skcvo.getKeyword()+"'");
						outsearchsub = skcvo.getOutsearchs() - outsearchsub;
						if(skcvo.getOutsearchs()>0){
							if(outsearchsub==0){
								skcvo.setOsrate("-");
							}
							if(outsearchsub>0){
								skcvo.setOsrate(String.format("%.2f",100.0*outsearchsub/skcvo.getOutsearchs()));
								skcvo.setOsrateIsIncre(true);
							}
							if(outsearchsub<0){
								skcvo.setOsrate(String.format("%.2f",100.0*outsearchsub/skcvo.getOutsearchs()));
								skcvo.setOsrateIsIncre(false);
							}
						}else
							skcvo.setOsrate("/");
						
						outclicksub = getKeywordsUnionNum("select sum(click_count) from click_soku_"+before_uniondate+" where keyword='"+skcvo.getKeyword()+"'");
						outclicksub = skcvo.getOutclicks() - outclicksub;
						if(skcvo.getOutclicks()>0){
							if(outclicksub==0){
								skcvo.setOcrate("-");
							}
							if(outclicksub>0){
								skcvo.setOcrate(String.format("%.2f",100.0*outclicksub/skcvo.getOutclicks()));
								skcvo.setOcrateIsIncre(true);
							}
							if(outclicksub<0){
								skcvo.setOcrate(String.format("%.2f",100.0*outclicksub/skcvo.getOutclicks()));
								skcvo.setOcrateIsIncre(false);
							}
						}else
							skcvo.setOcrate("/");
					}
				}
			}catch(Exception e){
				logger.error("取得前段时间数据出错,keyword:"+kqo.getKeyword(), e);
			}
		}
		
		return keywordList;
	}

	public static int getKeywordsUnionNum(String sql){
		try{
			System.out.println(sql);
			return JdbcTemplateFactoray.mergeLogYoukuDataSource.queryForInt(sql);
		}catch(Exception e){
			logger.error(sql, e);
		}
		return 0;
	}
	
	public List<KeywordVO> getBeforeKeywords(String uniondate,
			final String keyword) {
		String sql = "select * from query_union_" + uniondate
				+ " where keyword = ? ";
		return JdbcTemplateFactoray.mergeLogYoukuDataSource.query(sql,
				new PreparedStatementSetter() {
					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setString(1, keyword);
					}
				}, KeywordVOMapper.kwvomapper);
	}

	private List<KeywordQueryVO> getTopKeywords(String uniondate) {

		KeywordWeekPropare kwp = new KeywordWeekPropare();

		List<KeywordQueryVO> keywordList = kwp.getUnsortTopKeywords(uniondate);
		// TODO
		// 入库
		for (KeywordQueryVO kqo : keywordList) {
			doInsertKeywordUnions(kqo, uniondate);
			for (Entry<Integer, KeywordComVO> entry : kqo.getKeywords()
					.entrySet()) {
				doInsertKeywordUnions(entry.getValue(), kqo.getKeyword(), entry
						.getKey(), uniondate);
			}
		}
		
		deelListByrole(keywordList);
		
		SeclectSort ss = new SeclectSort();
		keywordList = ss.sort(keywordList, 500);

		for (KeywordQueryVO kqo : keywordList)
			getSingleTopKeywords(kqo);
		return keywordList;
	}

	private synchronized void deelListByrole(List<KeywordQueryVO> kqolist) {
		float israte = 0;
		KeywordComVO kco = null;
		List<KeywordQueryVO> rmkqolist = new ArrayList<KeywordQueryVO>();
		for (KeywordQueryVO kqo : kqolist) {
			kco = kqo.getKeywords().get(0);
			if (null != kco) {
				if (kqo.getInsearchs() > 0) {
					israte = 100 * kco.getInsearchs() / kqo.getInsearchs();
				}
				if (israte < 30) {
					if (kco.getKeyword().length() >= 2 && israte > 14)
						continue;
					if(null!=kqo.getKeywords().get(8)){
						if(100 * (kqo.getKeywords().get(8).getInsearchs())/kqo.getInsearchs()<10&&israte>20)
							continue;
					}else{
						if(israte>20)
							continue;
					}
					rmkqolist.add(kqo);
				}
			} else {
				rmkqolist.add(kqo);
			}
		}
		kqolist.removeAll(rmkqolist);
	}

	private void doInsertKeywordUnions(final KeywordQueryVO kqo,
			String uniondate) {
		JdbcTemplateFactoray.mergeLogYoukuDataSource
				.update(
						"insert ignore into query_union_"
								+ uniondate
								+ " (keyword,cate,searchs,web_searchs,clicks) values (?,?,?,?,?)",
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement ps)
									throws SQLException {
								ps.setString(1, kqo.getKeyword());
								ps.setInt(2, -1);
								ps.setInt(3, kqo.getInsearchs());
								ps.setInt(4, kqo.getOutsearchs());
								ps.setInt(5, kqo.getOutclicks());
							}
						});
	}

	private void doInsertKeywordUnions(final KeywordComVO kqo,
			final String keyword, final int cate, String uniondate) {
		JdbcTemplateFactoray.mergeLogYoukuDataSource
				.update(
						"insert ignore into query_union_"
								+ uniondate
								+ " (keyword,cate,searchs,web_searchs,clicks) values (?,?,?,?,?)",
						new PreparedStatementSetter() {
							public void setValues(PreparedStatement ps)
									throws SQLException {
								ps.setString(1, keyword);
								ps.setInt(2, cate);
								ps.setInt(3, kqo.getInsearchs());
								ps.setInt(4, kqo.getOutsearchs());
								ps.setInt(5, kqo.getOutclicks());
							}
						});
	}

	private void getSingleTopKeywords(KeywordQueryVO kqo) {
		Set<Integer> keys = kqo.getKeywords().keySet();
		KeywordComVO kqvo = null;
		for (Integer key : keys) {
			kqvo = kqo.getKeywords().get(key);
			if(null==kqvo) continue;
			if (kqo.getInsearchs() != 0)
				kqvo.setUisrate(String.format("%.2f", 100.0
						* kqvo.getInsearchs() / kqo.getInsearchs()));
			else
				kqvo.setUisrate("/");
			if (kqo.getOutsearchs() != 0)
				kqvo.setUosrate(String.format("%.2f", 100.0
						* kqvo.getOutsearchs() / kqo.getOutsearchs()));
			else
				kqvo.setUosrate("/");
			if (kqo.getOutclicks() != 0)
				kqvo.setOcrate(String.format("%.2f", 100.0
						* kqvo.getOutclicks() / kqo.getOutclicks()));
			else
				kqvo.setOcrate("/");
			if (kqo.getOutclicks() != 0)
				kqvo.setUocrate(String.format("%.2f", 100.0
						* kqvo.getOutclicks() / kqo.getOutclicks()));
			else
				kqvo.setUocrate("/");

			getSingleTopKeywordCom(kqvo);
		}
	}

	private void getSingleTopKeywordCom(KeywordComVO kco) {
		for (KeywordComVO kcvo : kco.getKeywords()) {
			if (kco.getInsearchs() != 0)
				kcvo.setUisrate(String.format("%.2f", 100.0
						* kcvo.getInsearchs() / kco.getInsearchs()));
			else
				kcvo.setUisrate("/");
			if (kco.getOutsearchs() != 0)
				kcvo.setUosrate(String.format("%.2f", 100.0
						* kcvo.getOutsearchs() / kco.getOutsearchs()));
			else
				kcvo.setUosrate("/");
			if (kco.getOutclicks() != 0)
				kcvo.setOcrate(String.format("%.2f", 100.0
						* kcvo.getOutclicks() / kco.getOutclicks()));
			else
				kcvo.setOcrate("/");
			if (kco.getOutclicks() != 0)
				kcvo.setOcrate(String.format("%.2f", 100.0
						* kcvo.getOutclicks() / kco.getOutclicks()));
			else
				kcvo.setOcrate("/");
		}
	}

	public static void printFile(String uniondate, String before_date,String test) throws Exception{
		VelocityEngine engine = new VelocityEngine();

		Properties props = new Properties();

		props.put("input.encoding", "utf8");
		props.put("output.encoding", "utf8");

		props.put("resource.loader", "class");
		props
				.put("class.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		// props.put("directive.foreach.counter.name", "velocityCount");

		Template template;
			engine.init(props);
			template = engine.getTemplate("com/youku/top/topn/top500.vm");
			VelocityContext context = new VelocityContext();
			List<KeywordQueryVO> keywordList = null;
			TopKeywordSortPrint kp = new TopKeywordSortPrint();	
			
			boolean isExistBeforeDate = true;
			boolean isExistOutQuery = true;
			boolean isExistOutClick = true;
			boolean isExistBeforeDateOutQuery = true;
			boolean isExistBeforeDateOutClick = true;
			if(getKeywordsUnionNum("select count(*) from query_union_" + before_date)<1){
				isExistBeforeDate = false;
				throw new Exception("比较日期没有数据");
			}
			
			keywordList = kp.getTwoTopKeywords(uniondate,before_date,isExistBeforeDate);
			if(getKeywordsUnionNum("select count(*) from query_union_" + uniondate +" where web_searchs>0")<1){
				isExistOutQuery = false;
//				throw new Exception("外网查询数据缺失");
			}
			if(getKeywordsUnionNum("select count(*) from query_union_" + uniondate +" where clicks>0")<1){
				isExistOutClick = false;
//				throw new Exception("外网点击数据缺失");
			}
			if(getKeywordsUnionNum("select count(*) from query_union_" + before_date +" where web_searchs>0")<1){
				isExistBeforeDateOutQuery = false;
//				throw new Exception("外网查询数据缺失");
			}
			if(getKeywordsUnionNum("select count(*) from query_union_" + before_date +" where clicks>0")<1){
				isExistBeforeDateOutClick = false;
//				throw new Exception("外网点击数据缺失");
			}
			context.put("isExistBeforeDate", isExistBeforeDate);
			context.put("isExistOutClick", isExistOutClick);
			context.put("isExistOutQuery", isExistOutQuery);
			context.put("isExistBeforeDateOutQuery", isExistBeforeDateOutQuery);
			context.put("isExistBeforeDateOutClick", isExistBeforeDateOutClick);
			System.out.println("isExistBeforeDate:"+isExistBeforeDate+",isExistOutQuery:"+isExistOutQuery+",isExistOutClick:"+isExistOutClick+",isExistBeforeDateOutQuery:"+isExistBeforeDateOutQuery+",isExistBeforeDateOutClick:"+isExistBeforeDateOutClick);
			context.put("uniondate", uniondate);
			context.put("beforedate", before_date);
			context.put("keywordList", keywordList);
			Writer writer = new StringWriter();
			template.merge(context, writer);
			StringUtil.getFileFromStr(writer.toString(),
					"/opt/log_analyze/top500/top_" + uniondate + ".html");
	}

	public static void printFile(String uniondate,String before_date) throws Exception{
		printFile(uniondate, before_date,null);
	}
}
