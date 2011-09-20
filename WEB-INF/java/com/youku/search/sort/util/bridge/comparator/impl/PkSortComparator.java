package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Pk;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class PkSortComparator extends AbstractSortComparator<Pk> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("total_pv", Constant.Sort.SORT_PK_PV);
        orderFields.put("begintime", Constant.Sort.SORT_PK_NEW);
        orderFields.put("vote_count", Constant.Sort.SORT_PK_VOTE_COUNT);
        orderFields.put("actor_count", Constant.Sort.SORT_PK_ACTOR_COUNT);
        orderFields.put("video_count", Constant.Sort.SORT_PK_VIDEO_COUNT);
    }

    public static final PkSortComparator I = new PkSortComparator();

    @Override
    public Comparator<Pk> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_PK_PV:
                return TOTAL_PV_ORDER_REVERSE;

            case Constant.Sort.SORT_PK_NEW:
                return BEGINTIME_ORDER_REVERSE;

            case Constant.Sort.SORT_PK_VOTE_COUNT:
                return VOTE_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_PK_ACTOR_COUNT:
                return ACTOR_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_PK_VIDEO_COUNT:
                return VIDEO_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;

            }
        } else {
            switch (orderField) {
            case Constant.Sort.SORT_PK_PV:
                return TOTAL_PV_ORDER;

            case Constant.Sort.SORT_PK_NEW:
                return BEGINTIME_ORDER;

            case Constant.Sort.SORT_PK_VOTE_COUNT:
                return VOTE_COUNT_ORDER;

            case Constant.Sort.SORT_PK_ACTOR_COUNT:
                return ACTOR_COUNT_ORDER;

            case Constant.Sort.SORT_PK_VIDEO_COUNT:
                return VIDEO_COUNT_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;

            }
        }
    }

    public final static Comparator<Pk> SCORE_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return Float.compare(p1.score, p2.score);
        }
    };

    public final static Comparator<Pk> SCORE_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * SCORE_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<Pk> TOTAL_PV_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return new Integer(p1.total_pv).compareTo(p2.total_pv);
        }
    };

    public final static Comparator<Pk> TOTAL_PV_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * TOTAL_PV_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<Pk> BEGINTIME_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return new Long(p1.begintime).compareTo(p2.begintime);
        }
    };

    public final static Comparator<Pk> BEGINTIME_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * BEGINTIME_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<Pk> VOTE_COUNT_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return new Integer(p1.vote_count).compareTo(p2.vote_count);
        }
    };

    public final static Comparator<Pk> VOTE_COUNT_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * VOTE_COUNT_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<Pk> ACTOR_COUNT_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return new Integer(p1.actor_count).compareTo(p2.actor_count);
        }
    };

    public final static Comparator<Pk> ACTOR_COUNT_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * ACTOR_COUNT_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<Pk> VIDEO_COUNT_ORDER = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return new Integer(p1.video_count).compareTo(p2.video_count);
        }
    };

    public final static Comparator<Pk> VIDEO_COUNT_ORDER_REVERSE = new Comparator<Pk>() {
        public int compare(Pk p1, Pk p2) {
            return -1 * VIDEO_COUNT_ORDER.compare(p1, p2);
        }
    };

}
