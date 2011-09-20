package com.youku.top.paihangbang;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.youku.search.util.DataFormat;
import com.youku.top.paihangbang.entity.TypeWord;

public class TypeWordsMgt {

	static Logger logger = Logger.getLogger(TypeWordsMgt.class);
	private static TypeWordsMgt instance = null;

	private TypeWordsMgt() {
		super();
	}

	public static synchronized TypeWordsMgt getInstance() {
		if (null == instance)
			instance = new TypeWordsMgt();
		return instance;
	}

	public int typeWordSaveFromBaidu(final String word, final int cate) {
		return typeWordInsert(word, null, cate);
	}

	private void typeWordUpdate(final String word, final String pic,
			final int cate) {
		String sql = "update type_words set pic=? where keyword = ? and cate = ? and (pic is null or length(pic)=0)";
		try {
			logger.info("channel:" + cate + ",保存图片,pic:" + pic);
			TopDateMgt.newSokuTopDataSource.update(sql,
					new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setString(1, pic);
							ps.setString(2, word);
							ps.setInt(3, cate);
						}
					});
		} catch (Exception e) {
			logger.error("sql:" + sql + ",cate:" + cate + ",word:" + word
					+ ",pic:" + pic, e);
		}
	}

	private int typeWordInsert(final String word, final String pic,
			final int cate) {
		try {
			String sql = "insert ignore into type_words (keyword,cate,pic,create_date) values (?,?,?,?)";
			logger.info("保存至分类字典,word:" + word + ",cate:" + cate + ",pic:"
					+ pic);
			return TopDateMgt.newSokuTopDataSource.update(sql,
					new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setString(1, word);
							ps.setInt(2, cate);
							ps.setString(3, pic);
							ps.setTimestamp(4, new java.sql.Timestamp(System
									.currentTimeMillis()));
						}
					});
		} catch (Exception e) {
			logger.error(e);
		}
		return 0;
	}

	//	
	// public void typeWordSave(final String word,final String pic,final String
	// type){
	// int row = typeWordInsert(word, pic, type);
	// if(row<1)
	// typeWordUpdate(word, pic, type);
	// }

	public Map<String, List<TypeWord>> typewordGetter() {
		Map<String, List<TypeWord>> map = new HashMap<String, List<TypeWord>>();
		String sql = "select * from type_words where state='normal'";
		try {
			List list = TopDateMgt.newSokuTopDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			Map item = null;
			String keyword;
			int programme_id;
			int cate;
			String pic;
			TypeWord tw = null;
			while (iterator.hasNext()) {
				item = (Map) iterator.next();
				cate = DataFormat.parseInt(item.get("cate"));
				keyword = String.valueOf(item.get("keyword"));
				programme_id = DataFormat.parseInt(item.get("programme_id"));
				pic = String.valueOf(item.get("pic"));

				if (0 != cate) {
					if (null == map.get(keyword)) {
						map.put(keyword, new ArrayList<TypeWord>());
					}
					tw = new TypeWord();
					tw.setCate(cate);
					tw.setProgramme_id(programme_id);
					tw.setKeyword(keyword);
					tw.setPic(pic);
					map.get(keyword).add(tw);
				}
			}
		} catch (Exception e) {
			logger.error("sql:" + sql, e);
		}
		return map;
	}

	public Map<Integer, Set<String>> typewordGetterByCate() {
		Map<Integer, Set<String>> map = new HashMap<Integer, Set<String>>();
		String sql = "select cate,keyword from type_words where state='normal'";
		try {
			List list = TopDateMgt.newSokuTopDataSource.queryForList(sql);
			Iterator iterator = list.iterator();
			Map item = null;
			String keyword;
			int cate;
			while (iterator.hasNext()) {
				item = (Map) iterator.next();
				cate = DataFormat.parseInt(item.get("cate"));
				keyword = String.valueOf(item.get("keyword"));

				if (0 == cate || StringUtils.isBlank(keyword))
					continue;

				if (0 != cate) {
					if (null == map.get(cate)) {
						map.put(cate, new HashSet<String>());
					}
					map.get(cate).add(keyword.trim());
				}
			}
		} catch (Exception e) {
			logger.error("sql:" + sql, e);
		}
		return map;
	}

	public List<TypeWord> typewordGet() {
		String sql = "select * from type_words where state='normal'";
		try {
			return TopDateMgt.newSokuTopDataSource.query(sql,
					new TypeWordVOMapper());
		} catch (Exception e) {
			logger.error("sql:" + sql, e);
		}
		return null;
	}

	public String picGetter(final String word) {
		String sql = "select pic from type_words where keyword = ?";
		try {
			return TopDateMgt.newSokuTopDataSource.queryForObject(sql,
					new Object[] { word }, new int[] { Types.VARCHAR },
					String.class);
		} catch (Exception e) {
			logger.error("sql:" + sql + ",keyword:" + word, e);
		}
		return null;
	}

	public void typeWordUpdateProgrammeId(final int id, final int programmeId) {
		String sql = "update type_words set programme_id=? where id=?";
		try {
			logger.info("保存对应节目,programme_id:" + programmeId + ",id:" + id);
			TopDateMgt.newSokuTopDataSource.update(sql,
					new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setInt(1, programmeId);
							ps.setInt(2, id);
						}
					});
		} catch (Exception e) {
			logger.error("sql:" + sql + ",programmeId:" + programmeId + ",id:"
					+ id, e);
		}
	}
}
