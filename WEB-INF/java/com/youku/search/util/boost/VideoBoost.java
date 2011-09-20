package com.youku.search.util.boost;

import java.util.Calendar;

import com.youku.search.util.boost.util.BadWords;

public class VideoBoost {

    public static final int DAY_SECONDS = 24 * 3600;
    public static final int DAY_M_SECONDS = DAY_SECONDS * 1000;

    public static final float PI = (float) Math.PI;
    public static final float PI_2 = (float) (Math.PI / 2);

    public static final float DAYS_OFFSET = 5;
    public static final float MAX_TIME_LENGTH = 3600;
    public static final float MIN_TIME_LENGTH = 0;
    public static final float MAX_HD = Integer.MAX_VALUE;
    public static final float MIN_HD = 0;

    public static final float MAX_SCORE_DAYS = 15;
    public static final float MAX_SCORE_VV_D = 5;
    public static final float MAX_SCORE_FAV_D = 8;
    public static final float MAX_SCORE_COMMENT_D = 6;
    public static final float MAX_SCORE_HD = 5;
    public static final float MAX_SCORE_TIME_LENGTH = 20;

    public static final float MAX_SCORE_TITLE = -100;// 只要带有不符合要求的就直接返回0
    public static final float MAX_SCORE_DESC = -100;

    //
    public static final float MAX_SCORE = MAX_SCORE_DAYS + MAX_SCORE_VV_D
            + MAX_SCORE_FAV_D + MAX_SCORE_COMMENT_D + MAX_SCORE_HD
            + MAX_SCORE_TIME_LENGTH;

    public static final float NORM_FACTOR = (float) (1.0 / 120);

    //
    public float days;// 得分：存在时长
    public float vv_d;// 得分：观看密度、收藏密度、评论密度
    public float fav_d;
    public float comment_d;
    public float hd;// 得分：清晰度
    public float time_length;// 得分：时长
    public float title;// 得分：标题
    public float desc;// 得分：描述

    public float all() {// 总分
        float all = days + vv_d + fav_d + comment_d + hd + time_length + title
                + desc;

        return all < 0 ? 0 : all;
    }

    public static VideoBoost getBoost(long createtime, int total_pv,
            int total_fav, int total_comment, float seconds, String title,
            String desc, boolean hd) {

        VideoBoost boost = new VideoBoost();

        // 存在时长
        float days = getDays(createtime);
        float score_days = getScoreDays(days);

        float whole_days = getWholeDays(days);

        // 观看密度、收藏密度、评论密度
        float vv_d = total_pv / whole_days;
        float fav_d = total_fav / whole_days;
        float comment_d = total_comment / whole_days;

        float score_vv_d = getScoreD(vv_d, MAX_SCORE_VV_D);
        float score_fav_d = getScoreD(fav_d, MAX_SCORE_FAV_D);
        float score_comment_d = getScoreD(comment_d, MAX_SCORE_COMMENT_D);

        // 清晰度
        float score_hd = getScoreHD(hd);

        // 时长
        float score_time_length = getScoreTimeLength(seconds);

        // 标题、描述
        float score_title = getScoreTitle(title);
        float score_desc = getScoreDesc(desc);

        // OK!
        boost.days = score_days;
        boost.vv_d = score_vv_d;
        boost.fav_d = score_fav_d;
        boost.comment_d = score_comment_d;
        boost.hd = score_hd;
        boost.time_length = score_time_length;
        boost.title = score_title;
        boost.desc = score_desc;

        //
        norm(boost);

        return boost;
    }

    public static void norm(VideoBoost boost) {
        boost.days *= NORM_FACTOR;
        boost.vv_d *= NORM_FACTOR;
        boost.fav_d *= NORM_FACTOR;
        boost.comment_d *= NORM_FACTOR;
        boost.hd *= NORM_FACTOR;
        boost.time_length *= NORM_FACTOR;
        boost.title *= NORM_FACTOR;
        boost.desc *= NORM_FACTOR;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("VideoBoost: { ");

        builder.append("days: " + days);
        builder.append(", ");

        builder.append("vv_d: " + vv_d);
        builder.append(", ");

        builder.append("fav_d: " + fav_d);
        builder.append(", ");

        builder.append("comment_d: " + comment_d);
        builder.append(", ");

        builder.append("hd: " + hd);
        builder.append(", ");

        builder.append("time_length: " + time_length);
        builder.append(", ");

        builder.append("title: " + title);
        builder.append(", ");

        builder.append("desc: " + desc);
        builder.append(", ");

        builder.append("all: " + all());

        builder.append(" }");

        return builder.toString();
    }

    private static float getDays(long createtime) {
        long now = System.currentTimeMillis();

        if (createtime > now) {
            createtime = now;
        }

        float days = (float) ((1.0 * now - createtime) / DAY_M_SECONDS);
        return days;
    }

    private static int getWholeDays(float days) {

        int whole_day = (int) days;
        boolean is_whole_day = days - whole_day < 0.00001;

        if (!is_whole_day || whole_day == 0) {
            whole_day++;
        }

        return whole_day;
    }

    /**
     * 反比例函数
     */
    private static float getScoreDays(float days) {
        float score = MAX_SCORE_DAYS * DAYS_OFFSET / (days + DAYS_OFFSET);
        return score;
    }

    /**
     * 反正切函数
     */
    private static float getScoreD(float d, float MAX) {
        float score = (float) (MAX * Math.atan(d) / PI_2);
        return score;
    }

    /**
     * 正比例函数
     */
    private static float getScoreHD(boolean hd) {
        float score = hd ? MAX_SCORE_HD : 0;
        return score;
    }

    /**
     * 正比例函数
     */
    private static float getScoreTimeLength(float seconds) {
        if (seconds < MIN_TIME_LENGTH) {
            seconds = MIN_TIME_LENGTH;
        }

        if (seconds > MAX_TIME_LENGTH) {
            seconds = MAX_TIME_LENGTH;
        }

        float score = MAX_SCORE_TIME_LENGTH / MAX_TIME_LENGTH * seconds;
        return score;
    }

    /**
     * 自定义函数
     */
    private static float getScoreTitle(String title) {
        if (title == null || title.trim().length() == 0) {
            return 0;
        }

        if (BadWords.isBad(title)) {
            return MAX_SCORE_TITLE;
        }

        return 0;
    }

    /**
     * 自定义函数
     */
    private static float getScoreDesc(String desc) {
        if (desc == null || desc.trim().length() == 0) {
            return 0;
        }

        if (BadWords.isBad(desc)) {
            return MAX_SCORE_DESC;
        }

        return 0;
    }

    public static void main(String[] args) {

        VideoBoost boost = new VideoBoost();
        System.out.println(boost);

        System.out.println();

        for (int i = 0; i < 60; i++) {
            Calendar time = Calendar.getInstance();
            time.add(Calendar.HOUR_OF_DAY, -1 * i);
            long time_ms = time.getTimeInMillis();

            float days = getDays(time_ms);
            int whole_days = getWholeDays(days);

            System.out.printf("days before %d hour(s): %g, whole day(s): %d\n",
                    i, days, whole_days);
        }

        System.out.println();

        for (int i = 0; i < 120; i++) {
            System.out.printf("score_days %d: %g\n", i, getScoreDays(i));
        }

        System.out.println();

        for (int i = 0; i < 30; i++) {
            System.out.printf("score_vv_d: %g, %g\n", i / 10.0, getScoreD(i,
                    MAX_SCORE_VV_D));
        }
        for (int i = 1000; i < 10000; i += 1000) {
            System.out.printf("score_*_d: %g, %g\n", 1.0 * i, getScoreD(i,
                    MAX_SCORE_VV_D));
        }

        System.out.println();

        for (int i = 0; i < 30; i++) {
            System.out.printf("score_time_length: %g, %g\n", 1.0 * i,
                    getScoreTimeLength(i));
        }
        for (float i = MAX_TIME_LENGTH / 3; i < MAX_TIME_LENGTH * 1.3; i += MAX_TIME_LENGTH / 10) {
            System.out.printf("score_time_length: %g, %g\n", 1.0 * i,
                    getScoreTimeLength(i));
        }

        System.out.println();

        // for (int i = 0; i < 100; i++) {
        // System.out.printf("score_hd: %g, %g\n", 1.0 * i, getScoreHD(i));
        // }
        // for (int i = 10000; i < 100 * 10000; i += 10000) {
        // System.out.printf("score_hd: %g, %g\n", 1.0 * i, getScoreHD(i));
        // }

        System.out.println("------------------------------------------");

        // 1148540657000 2034 0 1 919.0 悬崖边上的刺激
        VideoBoost videoBoost = getBoost(1148540657000L, 2034, 0, 1, 919,
                "悬崖边上的刺激", "", true);
        System.out.println(videoBoost);
    }
}
