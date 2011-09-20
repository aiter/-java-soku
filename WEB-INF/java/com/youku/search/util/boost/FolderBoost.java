package com.youku.search.util.boost;

import java.util.Calendar;

import com.youku.search.util.boost.util.BadWords;

public class FolderBoost {

    public static final int DAY_SECONDS = 24 * 3600;
    public static final int DAY_M_SECONDS = DAY_SECONDS * 1000;

    public static final float PI_2 = (float) (Math.PI / 2);

    public static final float DAYS_OFFSET = 5;
    public static final int MAX_VideoCount = 200;
    public static final float MAX_TIME_LENGTH_AVG = 3600;

    public static final float MAX_SCORE_VideoCount = 20;
    public static final float MAX_SCORE_PlayCount = 25;
    public static final float MAX_SCORE_TimeLength = 0;
    public static final float MAX_SCORE_UpdateTime = 25;
    public static final float MAX_SCORE_TimeLengthAvg = 30;
    public static final float MAX_SCORE_TITLE = -10;
    public static final float MAX_SCORE_DESC = -10;

    //
    public static final float MAX_SCORE = MAX_SCORE_VideoCount
            + MAX_SCORE_PlayCount + MAX_SCORE_TimeLength + MAX_SCORE_UpdateTime
            + MAX_SCORE_TimeLengthAvg;

    public static final float NORM_FACTOR = (float) (0.8 / MAX_SCORE);

    // 视频数 ，总播放数，总时长，最后更新时间
    public float video_count;// 得分：视频数
    public float play_count;// 得分：总播放数
    public float time_length;// 得分：总时长
    public float time_length_avg;// 得分：平均时长
    public float update_time;// 得分：最后更新时间
    public float title;// 得分：标题
    public float desc;// 得分：描述

    public float all() {// 总分
        float all = video_count + play_count + time_length + time_length_avg
                + update_time + title + desc;

        return all < 0 ? 0 : all;
    }

    public static FolderBoost getBoost(int video_count, int play_count,
            float time_length, long update_time, String title, String desc) {

        FolderBoost boost = new FolderBoost();

        // 视频数，总播放数，总时长，平均时长, 最后更新时间
        float score_video_count = getScoreVideoCount(video_count);
        float score_play_count = getScorePlayCount(play_count);
        float score_time_length = getScoreTimeLength(time_length);
        float score_time_length_avg = getScoreTimeLengthAvg(time_length,
                video_count);

        // 最后更新时长
        float days = getDays(update_time);
        float score_update_time = getScoreUpdateTime(days);

        // 标题、描述
        float score_title = getScoreTitle(title);
        float score_desc = getScoreDesc(desc);

        // OK!
        boost.video_count = score_video_count;
        boost.play_count = score_play_count;
        boost.time_length = score_time_length;
        boost.time_length_avg = score_time_length_avg;
        boost.update_time = score_update_time;
        boost.title = score_title;
        boost.desc = score_desc;

        //
        norm(boost);

        return boost;
    }

    public static void norm(FolderBoost boost) {
        boost.video_count *= NORM_FACTOR;
        boost.play_count *= NORM_FACTOR;
        boost.time_length *= NORM_FACTOR;
        boost.time_length_avg *= NORM_FACTOR;
        boost.update_time *= NORM_FACTOR;
        boost.title *= NORM_FACTOR;
        boost.desc *= NORM_FACTOR;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("FolderBoost: { ");

        builder.append("video_count: " + video_count);
        builder.append(", ");

        builder.append("play_count: " + play_count);
        builder.append(", ");

        builder.append("time_length: " + time_length);
        builder.append(", ");

        builder.append("time_length_avg: " + time_length_avg);
        builder.append(", ");

        builder.append("update_time: " + update_time);
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

    /**
     * 正比例函数
     */
    private static float getScoreVideoCount(int count) {

        count = Math.max(count, 0);
        count = Math.min(count, MAX_VideoCount);

        final double factor = MAX_SCORE_VideoCount / MAX_VideoCount;

        return (float) (factor * count);
    }

    /**
     * 反正切函数
     */
    private static float getScorePlayCount(int count) {
        count = count < 0 ? 0 : count;

        return (float) (MAX_SCORE_PlayCount * Math.atan(count) / PI_2);
    }

    /**
     * 反比例函数
     */
    private static float getScoreUpdateTime(float days) {
        return MAX_SCORE_UpdateTime * DAYS_OFFSET / (days + DAYS_OFFSET);
    }

    /**
     * 反正切函数
     */
    private static float getScoreTimeLength(float length) {
        length = length < 0 ? 0 : length;
        return (float) (MAX_SCORE_TimeLength * Math.atan(length) / PI_2);
    }

    /**
     * 正比例函数
     */
    private static float getScoreTimeLengthAvg(float time_length,
            int video_count) {

        if (video_count <= 0 || time_length <= 0) {
            return 0;
        }

        float time_length_avg = time_length / video_count;

        if (time_length_avg > MAX_TIME_LENGTH_AVG) {
            time_length_avg = MAX_TIME_LENGTH_AVG;
        }

        return MAX_SCORE_TimeLengthAvg / MAX_TIME_LENGTH_AVG * time_length_avg;
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

        // 2009-01-15 09:17:41
        Calendar calendar = Calendar.getInstance();
        calendar.set(2009, 0, 15, 9, 17, 41);

        for (int i = 0; i <= MAX_VideoCount; i++) {
            float video_count = getScoreVideoCount(i);

            FolderBoost boost = FolderBoost.getBoost(i, 5015, 3726,
                    DAY_M_SECONDS, "", "");

            System.out.println(i + ", " + video_count + ", "
                    + boost.video_count + ", " + boost.all());
        }

        System.out.println();

    }
}
