package com.youku.search.util.boost;

import com.youku.search.util.DataFormat;

public class VideoBoost_Test {

	public static final int DAY_SECONDS = 24 * 3600;
	public static final int DAY_M_SECONDS = DAY_SECONDS * 1000;

	public static final double PI = Math.PI;
	public static final double PI_2 = (Math.PI / 2);

	public static final int MAX_DAYS = 15;
	public static final double DAYS_OFFSET = 5;
	public static final double MAX_TIME_LENGTH = 3600;
	public static final double MIN_TIME_LENGTH = 0;

	public static final double MAX_SCORE_VV = 1.5;

	public static final double MAX_SCORE_DAYS = 0.001;
	public static final double MAX_SCORE_HD = 0.1;
	public static final double MAX_SCORE_TIME_LENGTH = 0.02;

	//
	public static final double MAX_SCORE = MAX_SCORE_VV + MAX_SCORE_DAYS
			+ +MAX_SCORE_HD + MAX_SCORE_TIME_LENGTH;

	public static final double NORM_FACTOR = 1;

	//
	public double vv;// 得分：15天VV之和
	public double days;// 得分：存在时长
	public double hd;// 得分：清晰度
	public double time_length;// 得分：时长

	public double all() {// 总分
		// double all = vv + days + hd + time_length;
		double all = vv;
		return all < 0 ? 0 : all;
	}

	public static VideoBoost_Test getBoost(double vv, long createtime,
			boolean hd, double seconds) {

		VideoBoost_Test boost = new VideoBoost_Test();

		// vv
		double score_vv = getScoreVV(vv, createtime);
		// 存在时长
		double days = getDays(createtime);
		double score_days = getScoreDays(days);

		// 清晰度
		double score_hd = getScoreHD(hd);

		// 时长
		double score_time_length = getScoreTimeLength(seconds);

		// OK!
		boost.vv = score_vv;
		boost.days = score_days;
		boost.hd = score_hd;
		boost.time_length = score_time_length;

		//
		norm(boost);

		return boost;
	}

	public static void norm(VideoBoost_Test boost) {
		boost.vv *= NORM_FACTOR;
		boost.days *= NORM_FACTOR;
		boost.hd *= NORM_FACTOR;
		boost.time_length *= NORM_FACTOR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("VideoBoost: { ");

		builder.append("vv: " + vv);
		builder.append(", ");

		builder.append("days: " + days);
		builder.append(", ");

		builder.append("hd: " + hd);
		builder.append(", ");

		builder.append("time_length: " + time_length);
		builder.append(", ");

		builder.append("all: " + all());

		builder.append(" }");

		return builder.toString();
	}

	private static double getDays(long createtime) {
		long now = System.currentTimeMillis();

		if (createtime > now) {
			createtime = now;
		}

		double days = (1.0 * now - createtime) / DAY_M_SECONDS;
		return days;
	}

	/**
	 * 反比例函数
	 */
	private static double getScoreDays(double days) {
		double score = MAX_SCORE_DAYS * DAYS_OFFSET / (days + DAYS_OFFSET);
		return score;
	}

	/**
	 * 反正切函数
	 */
	private static double getScoreVV(double vv, long createtime) {
		// v0.01
		// double score = Math.atan(vv / 1000) / PI_2 * MAX_SCORE_VV;

		// v0.02
		// if (vv <= 0) {
		// score = 0;
		// } else if (vv <= 6000) {
		// score = Math.pow(vv, 1.1) / Math.pow(6000, 1.1)
		// * (MAX_SCORE_VV - 0.1);
		// } else {
		// score = (MAX_SCORE_VV - 0.1) + 0.1 * Math.atan(vv) / PI_2;
		// }

		// v0.03
		double days = getDays(createtime);
		if (days <= 0) {
			days = 1;
		} else if (days > MAX_DAYS) {
			days = MAX_DAYS;
		}

		double score = 0;
		if (vv > 1) {
			if (vv > days) {
				score = Math.log(vv / days);
			}
		}
		return score;
	}

	/**
	 * 正比例函数
	 */
	private static double getScoreHD(boolean hd) {
		double score = hd ? MAX_SCORE_HD : 0;
		return score;
	}

	/**
	 * 正比例函数
	 */
	private static double getScoreTimeLength(double seconds) {
		if (seconds < MIN_TIME_LENGTH) {
			seconds = MIN_TIME_LENGTH;
		}

		if (seconds > MAX_TIME_LENGTH) {
			seconds = MAX_TIME_LENGTH;
		}

		double score = MAX_SCORE_TIME_LENGTH / MAX_TIME_LENGTH * seconds;
		return score;
	}

	public static void main(String[] args) {

		VideoBoost_Test boost = new VideoBoost_Test();
		System.out.println(boost);

		System.out.println();
		// for (int i = 0; i < 10000; i += 100) {
		// System.out.printf("score_vv: %g, %g\n", i * 1.0, getScoreVV(i));
		// }
		//
		// System.out.println();
		// for (int i = 0; i < 120; i++) {
		// System.out.printf("score_days %d: %g\n", i, getScoreDays(i));
		// }
		//
		// System.out.println();
		// for (int i = 0; i < 30; i++) {
		// System.out.printf("score_time_length: %g, %g\n", 1.0 * i,
		// getScoreTimeLength(i));
		// }
		// for (double i = MAX_TIME_LENGTH / 3; i < MAX_TIME_LENGTH * 1.3; i +=
		// MAX_TIME_LENGTH / 10) {
		// System.out.printf("score_time_length: %g, %g\n", 1.0 * i,
		// getScoreTimeLength(i));
		// }

		System.out.println();

		System.out.println("------------------------------------------");

		// 1148540657000 2034 0 1 919.0 悬崖边上的刺激
		VideoBoost_Test videoBoost = getBoost(16, DataFormat.parseUtilDate(
				"2008-02-09 00:31:29", 2).getTime(), true, 919);
		System.out.println(videoBoost.vv);
	}
}
