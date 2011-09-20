package com.youku.search.sort.entity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.torque.Torque;

import com.youku.search.util.JdbcUtil;
import com.youku.search.util.MyUtil;

public class BarCatalogMap {

	public static class BarCatalog {
		public int bar_id;
		public int catalog_id;

		public BarCatalog(int bar_id, int catalog_id) {
			this.bar_id = bar_id;
			this.catalog_id = catalog_id;
		}
	}

	public static class Catalog {
		public int id;
		public int parent_id;
		public String name;

		public Catalog(int id, int parent_id, String name) {
			this.id = id;
			this.parent_id = parent_id;
			this.name = name;
		}
	}

	/**
	 * catalog_id => Catalog
	 */
	public final Map<Integer, Catalog> catalog_map = new TreeMap<Integer, Catalog>();

	/**
	 * bar_id => BarCatalog
	 */
	public final Map<Integer, BarCatalog> bar_map = new TreeMap<Integer, BarCatalog>();

	static Log logger = LogFactory.getLog(BarCatalogMap.class);
	private volatile static BarCatalogMap instance = getFreshCategoryMap();

	private static final long loadFromPeriod = 1000 * 60 * 30;// 30分钟
	static {
		new Timer().schedule(new LoaderTimerTask(), loadFromPeriod,
				loadFromPeriod);
	}

	public static BarCatalogMap getInstance() {
		return instance;
	}

	public List<Catalog> getCatalogs(int bar_id) {
		List<Catalog> list = new LinkedList<Catalog>();

		BarCatalog barCatalog = bar_map.get(bar_id);
		if (barCatalog != null) {
			Catalog catalog = catalog_map.get(barCatalog.catalog_id);

			while (catalog != null) {
				list.add(0, catalog);
				catalog = catalog_map.get(catalog.parent_id);
			}
		}

		return list;
	}

	private static BarCatalogMap getFreshCategoryMap() {
		BarCatalogMap categoryMap = new BarCatalogMap();
		categoryMap.init();
		return categoryMap;
	}

	private void init() {
		init_bar();
		init_catalog();

		if (logger.isDebugEnabled()) {
			logger.debug("加载bar信息结束, " + toString());
		}
	}

	private void init_bar() {
		if (logger.isDebugEnabled()) {
			logger.debug("第1步，加载bar信息...");
		}

		final String sql = "select fk_catalog, fk_bar from t_bar_catalog_map where master = 1 order by fk_bar asc limit ?, ?";

		final int limit = 1000;

		for (int offset = 0; offset < Integer.MAX_VALUE; offset += limit) {
			Connection conn = getConnection();
			ResultSet rs = null;
			PreparedStatement statement = null;

			try {
				statement = conn.prepareStatement(sql);
				statement.setInt(1, offset);
				statement.setInt(2, limit);

				rs = statement.executeQuery();

				int count = 0;
				while (rs.next()) {
					++count;

					int bar_id = rs.getInt("fk_bar");
					int catalog_id = rs.getInt("fk_catalog");

					bar_map.put(bar_id, new BarCatalog(bar_id, catalog_id));
				}

				if (count == 0) {
					break;
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				JdbcUtil.close(rs, statement, conn);
			}
		}
	}

	private void init_catalog() {
		if (logger.isDebugEnabled()) {
			logger.debug("第2步，加载catalog信息...");
		}

		final String sql = "select catalog, pk_catalog, parent_catalog from t_bar_catalog order by pk_catalog asc limit ?, ?";

		final int limit = 1000;

		for (int offset = 0; offset < Integer.MAX_VALUE; offset += limit) {
			Connection conn = getConnection();
			ResultSet rs = null;
			PreparedStatement statement = null;

			try {
				statement = conn.prepareStatement(sql);
				statement.setInt(1, offset);
				statement.setInt(2, limit);

				rs = statement.executeQuery();

				int count = 0;
				while (rs.next()) {
					++count;

					String catalog = MyUtil.getString(rs.getString("catalog"));
					int catalog_id = rs.getInt("pk_catalog");
					int parent_id = rs.getInt("parent_catalog");

					catalog_map.put(catalog_id, new Catalog(catalog_id,
							parent_id, catalog));
				}

				if (count == 0) {
					break;
				}

			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				JdbcUtil.close(rs, statement, conn);
			}
		}
	}

	private Connection getConnection() {

		try {
			return Torque.getConnection("youkubar");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "bar_map: " + bar_map + ", " + "catalog_map: " + catalog_map;
	}

	static class LoaderTimerTask extends TimerTask {
		@Override
		public void run() {
			try {
				logger.info("TimerTask从数据库加载分类信息: 开始执行...");

				instance = getFreshCategoryMap();

				logger.info("TimerTask从数据库加载分类信息: 执行完毕");
			} catch (Exception e) {
				logger.error("TimerTask从数据库加载分类信息: 执行发生异常", e);
			}
		}
	}
}
