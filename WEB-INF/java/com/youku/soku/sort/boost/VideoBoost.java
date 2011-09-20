package com.youku.soku.sort.boost;

/**
 * 站外视频
 */
public class VideoBoost {

	public static final double PI = Math.PI;
	public static final double PI_2 = (Math.PI / 2);

	public static final double MAX_SCORE_TIME_LENGTH = 20;

	public static final double MIN_TIME_LENGTH = 0;
	public static final double MAX_TIME_LENGTH = 3600 + 1800;// 一个半小时

	public static final double MAX_SCORE = MAX_SCORE_TIME_LENGTH;

	public static final double NORM_FACTOR = 1.0 / 20.0 * 0.3;

	//
	public double time_length;// 得分：时长

	public double all() {// 总分
		double all = time_length;

		return all < 0 ? 0 : all;
	}

	public static VideoBoost getBoost(double seconds) {

		VideoBoost boost = new VideoBoost();

		// 时长
		double score_time_length = getScoreTimeLength(seconds);

		// OK!
		boost.time_length = score_time_length;

		//
		norm(boost);

		return boost;
	}

	private static void norm(VideoBoost boost) {
		boost.time_length *= NORM_FACTOR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("VideoBoost: { ");

		builder.append("time_length: " + time_length);
		builder.append(", ");

		builder.append("all: " + all());

		builder.append(" }");

		return builder.toString();
	}

	private static double getScoreTimeLength(double seconds) {
		if (seconds < MIN_TIME_LENGTH) {
			seconds = MIN_TIME_LENGTH;
		}

		double extra = 0;
		if (seconds > MAX_TIME_LENGTH) {
			extra = Math.atan(seconds) / PI_2;// 最多1的浮动
			seconds = MAX_TIME_LENGTH;
		}

		double score = MAX_SCORE_TIME_LENGTH / MAX_TIME_LENGTH * seconds
				+ extra;

		return score;

	}

	public static void main(String[] args) {

		VideoBoost boost = new VideoBoost();
		System.out.println(boost);

		System.out.println();

		for (int i = 0; i < 3600; i++) {
			System.out.printf("score_time_length: %s, %s, %s\n", 1.0 * i,
					getScoreTimeLength(i), (float) getScoreTimeLength(i));
			VideoBoost videoBoost = getBoost(i);
			System.out.println(videoBoost);
		}

	}
}
