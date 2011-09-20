package com.youku.search.monitor.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.youku.search.monitor.Monitor;
import com.youku.search.monitor.Person;
import com.youku.search.monitor.Result;
import com.youku.search.util.DataFormat;
import com.youku.top.data_source.SpiderDataSource;

public class SpiderMonitor extends Monitor {

	public SpiderMonitor() {

		super(Person.shifangfang);
		// 每天执行一次
		super.period = 1000 * 60 * 60 * 24;

	}

	@Override
	public Result check() {

		System.out.println("1000 * 60 * 60 * 24 这不是一天啊 。。。。。。。。。。。");
		Result rs = new Result();
		// ContentBuilder cb = new ContentBuilder();
		Date d = DataFormat.getNextDate(new Date(), -1);
		int year = DataFormat.getYear(d);
		int month = DataFormat.getMonth(d);
		int day = DataFormat.getDay(d);

		List<SpiderLog> sls = query(year, month, day, year, month, day);
		String finfo = LogMonitorContentBuild(sls);

		if (!StringUtils.isBlank(finfo)) {
			rs.setMessage("spider网站更新数据异常\n" + finfo);
			rs.setOk(false);
		}

		return rs;
	}

	public static List<SpiderLog> query(int from_year, int from_month,
			int from_day, int to_year, int to_month, int to_day) {

		JdbcTemplate template = new JdbcTemplate(SpiderDataSource.INSTANCE);

		if (to_year == 0) {
			to_year = 4999;
		}
		if (to_month == 0) {
			to_month = 12;
		}
		if (to_day == 0) {
			to_day = 31;
		}

		String format = "select * from zzz_spider_log "
				+ "where log_date >= '%d-%d-%d' and log_date <= '%d-%d-%d' "
				+ "order by log_date desc, site asc";

		String sql = String.format(format, from_year, from_month, from_day,
				to_year, to_month, to_day);

		List<SpiderLog> list = template.query(sql, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				SpiderLog log = new SpiderLog();

				log.setLog_date(rs.getDate("log_date"));
				log.setSite(rs.getString("site"));
				log.setMovie_insert(rs.getInt("movie_insert"));
				log.setMovie_create(rs.getInt("movie_create"));
				log.setMovie_update(rs.getInt("movie_update"));
				log.setMovie_delete(rs.getInt("movie_delete"));

				return log;
			}
		});

		return list;
	}

	public String LogMonitorContentBuild(List<SpiderLog> sls) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table width=\"80%\">");
		sb.append("<tr>");
		sb.append("<td width=\"25%\">");
		sb.append("log_date");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("site");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("movie_insert");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("movie_create");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("movie_update");
		sb.append("</td>");
		sb.append("<td width=\"15%\">");
		sb.append("movie_delete");
		sb.append("</td>");
		sb.append("</tr>");
		for (SpiderLog sl : sls) {
			sb.append("<tr>");
			sb.append("<td>");
			sb.append(sl.getLog_date());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getSite());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getMovie_insert());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getMovie_create());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getMovie_update());
			sb.append("</td>");
			sb.append("<td>");
			sb.append(sl.getMovie_delete());
			sb.append("</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");

		String site = null;
		boolean f = false;
		for (SpiderLog sl : sls) {
			site = sl.getSite();
			if ("tudou".equalsIgnoreCase(site)) {
				f = true;
				if (sl.getMovie_insert() < 10000
						|| sl.getMovie_create() < 10000)
					return sb.toString();
				if (sl.getMovie_insert() - sl.getMovie_create() > 500) {
					return sb.toString();
				}
			}
			if ("sohu".equalsIgnoreCase(site)) {
				if (sl.getMovie_insert() < 500 || sl.getMovie_create() < 500)
					return sb.toString();
			}
			if ("sina".equalsIgnoreCase(site)) {
				if (sl.getMovie_insert() < 100 || sl.getMovie_create() < 100)
					return sb.toString();
			}
			if ("ku6".equalsIgnoreCase(site)) {
				if (sl.getMovie_insert() < 100 || sl.getMovie_create() < 100)
					return sb.toString();
			}
			if ("com56".equalsIgnoreCase(site)) {
				if (sl.getMovie_insert() < 100 || sl.getMovie_create() < 100)
					return sb.toString();
			}
			if (!"tudou".equalsIgnoreCase(site)
					&& sl.getMovie_insert() - sl.getMovie_create() > 150) {
				return sb.toString();
			}
		}

		if (!f)

			return sb.toString();
		else
			return null;
	}

}

class SpiderLog {
	private Date log_date;
	private String site;
	private int movie_insert;
	private int movie_create;
	private int movie_update;
	private int movie_delete;

	public Date getLog_date() {
		return log_date;
	}

	public void setLog_date(Date logDate) {
		log_date = logDate;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public int getMovie_insert() {
		return movie_insert;
	}

	public void setMovie_insert(int movieInsert) {
		movie_insert = movieInsert;
	}

	public int getMovie_create() {
		return movie_create;
	}

	public void setMovie_create(int movieCreate) {
		movie_create = movieCreate;
	}

	public int getMovie_update() {
		return movie_update;
	}

	public void setMovie_update(int movieUpdate) {
		movie_update = movieUpdate;
	}

	public int getMovie_delete() {
		return movie_delete;
	}

	public void setMovie_delete(int movieDelete) {
		movie_delete = movieDelete;
	}

}
