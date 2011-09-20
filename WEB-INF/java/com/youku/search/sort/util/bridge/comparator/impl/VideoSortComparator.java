package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Video;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class VideoSortComparator extends AbstractSortComparator<Video> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("createtime", Constant.Sort.SORT_NEW);
        orderFields.put("total_pv", Constant.Sort.SORT_PV);
        orderFields.put("total_comment", Constant.Sort.SORT_COMMENT);
        orderFields.put("total_fav", Constant.Sort.SORT_FAV);
    }

    public static final VideoSortComparator I = new VideoSortComparator();

    @Override
    public Comparator<Video> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_NEW:
                return CREATE_TIME_ORDER_REVERSE;

            case Constant.Sort.SORT_PV:
                return TOTAL_PV_ORDER_REVERSE;

            case Constant.Sort.SORT_COMMENT:
                return TOTAL_COMMENT_ORDER_REVERSE;

            case Constant.Sort.SORT_FAV:
                return TOTAL_FAV_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;

            }
        } else {
            switch (orderField) {
            case Constant.Sort.SORT_NEW:
                return CREATE_TIME_ORDER;

            case Constant.Sort.SORT_PV:
                return TOTAL_PV_ORDER;

            case Constant.Sort.SORT_COMMENT:
                return TOTAL_COMMENT_ORDER;

            case Constant.Sort.SORT_FAV:
                return TOTAL_FAV_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;

            }
        }
    }

    public final static Comparator<Video> SCORE_ORDER = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return Float.compare(v1.score, v2.score);
        }
    };

    public final static Comparator<Video> SCORE_ORDER_REVERSE = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return -1 * SCORE_ORDER.compare(v1, v2);
        }
    };

    public final static Comparator<Video> CREATE_TIME_ORDER = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return new Long(v1.createtime).compareTo(v2.createtime);
        }
    };

    public final static Comparator<Video> CREATE_TIME_ORDER_REVERSE = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return -1 * CREATE_TIME_ORDER.compare(v1, v2);
        }
    };

    public final static Comparator<Video> TOTAL_PV_ORDER = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return new Integer(v1.total_pv).compareTo(v2.total_pv);
        }
    };

    public final static Comparator<Video> TOTAL_PV_ORDER_REVERSE = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return -1 * TOTAL_PV_ORDER.compare(v1, v2);
        }
    };

    public final static Comparator<Video> TOTAL_COMMENT_ORDER = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return new Integer(v1.total_comment).compareTo(v2.total_comment);
        }
    };

    public final static Comparator<Video> TOTAL_COMMENT_ORDER_REVERSE = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return -1 * TOTAL_COMMENT_ORDER.compare(v1, v2);
        }
    };

    public final static Comparator<Video> TOTAL_FAV_ORDER = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return new Integer(v1.total_fav).compareTo(v2.total_fav);
        }
    };

    public final static Comparator<Video> TOTAL_FAV_ORDER_REVERSE = new Comparator<Video>() {
        public int compare(Video v1, Video v2) {
            return -1 * TOTAL_FAV_ORDER.compare(v1, v2);
        }
    };

}
