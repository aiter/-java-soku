package com.youku.search.monitor.impl;

import java.util.Calendar;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import com.youku.search.monitor.Result;
import com.youku.search.util.DataFormat;
import com.youku.top.data_source.NewSokuTopDataSource;

public class TopMonitor extends CMonitor {

	public TopMonitor() {
		super();
		super.period = getDelayTime(1000 * 60 * 60 * 24L, 9, true);
	}

	public static long getDelayTime(long d, double hour, boolean f) {
		Calendar calCurrent = Calendar.getInstance();
		long now;
		if (f)
			now = calCurrent.get(Calendar.HOUR_OF_DAY) * 60 * 60 * 1000
					+ calCurrent.get(Calendar.MINUTE) * 60 * 1000
					+ calCurrent.get(Calendar.SECOND) * 1000;
		else
			now = calCurrent.get(Calendar.HOUR) * 60 * 60 * 1000
					+ calCurrent.get(Calendar.MINUTE) * 60 * 1000
					+ calCurrent.get(Calendar.SECOND) * 1000;
		long run = (long) (hour * 60 * 60 * 1000);

		if (run < now) {
			run = d - (now - run);
		} else {
			run = run - now;
		}
		return run;
	}

	@Override
	public Result check() {
		int count = 0;
		int b_count = 0;
		Result r = new Result();
		try {
			JdbcTemplate template = new JdbcTemplate(
					NewSokuTopDataSource.INSTANCE);

			String date = DataFormat.formatDate(DataFormat.getNextDate(
					new Date(), -1), DataFormat.FMT_DATE_YYYYMMDD);

			count = template
					.queryForInt("select count(*) from top_words where top_date='"
							+ date + "'");

			b_count = template.queryForInt("select count(*) from rankinfo_"
					+ date.replace("-", "_") + " where fk_programme_id>0");
		} catch (Exception e) {
			r.setOk(false);
			r.setMessage(e.getMessage());
			return r;
		}

		if (count < 1000 || b_count < 100) {
			r.setOk(false);
			r.setMessage("榜单数据生成个数:" + count + " , 目录数据生成个数:" + b_count);
		}

		return r;
	}

}
