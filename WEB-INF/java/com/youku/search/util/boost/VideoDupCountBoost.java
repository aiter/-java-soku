package com.youku.search.util.boost;

public class VideoDupCountBoost {

	public static final float PI = (float) Math.PI;
	public static final float PI_2 = (float) (Math.PI / 2);

	public static final float MAX_SCORE_DUP = 0.1F;

	//
	public static final float MAX_SCORE = MAX_SCORE_DUP;

	public static final float NORM_FACTOR = (float) (1.0);

	//
	public float dup_count;// 得分：去重数

	public float all() {// 总分
		float all = dup_count;

		return all < 0 ? 0 : all;
	}

	public static VideoDupCountBoost getBoost(int dup_count) {

		VideoDupCountBoost boost = new VideoDupCountBoost();

		// 去重数目
		float score_dup_count = getScoreDupCount(dup_count);

		// OK!
		boost.dup_count = score_dup_count;

		//
		norm(boost);

		return boost;
	}

	public static void norm(VideoDupCountBoost boost) {
		boost.dup_count *= NORM_FACTOR;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("VideoDupCountBoost: { ");

		builder.append("dup_count: " + dup_count);
		builder.append(", ");

		builder.append("all: " + all());

		builder.append(" }");

		return builder.toString();
	}

	/**
	 * 反正切函数
	 */
	private static float getScoreDupCount(int dup_count) {
		float score = (float) (MAX_SCORE_DUP * Math.atan((dup_count - 1)) / PI_2);
		return score;
	}

	public static void main(String[] args) {

		for (int i = -60; i < 60; i++) {
			VideoDupCountBoost boost = getBoost(i);
			System.out.printf("dup_clunt: %d, score: %f, %s\n", i,
					boost.dup_count, boost);
		}
	}
}
