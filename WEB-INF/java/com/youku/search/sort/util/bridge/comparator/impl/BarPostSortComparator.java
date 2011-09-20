package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.BarPost;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class BarPostSortComparator extends AbstractSortComparator<BarPost> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("post_time", Constant.Sort.SORT_BARPOST_NEW);
    }

    public static final BarPostSortComparator I = new BarPostSortComparator();

    @Override
    public Comparator<BarPost> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_BARPOST_NEW:
                return POST_TIME_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;
            }

        } else {
            switch (orderField) {
            case Constant.Sort.SORT_BARPOST_NEW:
                return POST_TIME_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;
            }
        }
    }

    public final static Comparator<BarPost> SCORE_ORDER = new Comparator<BarPost>() {
        public int compare(BarPost p1, BarPost p2) {
            if (p1.score > p2.score) {
                return 1;
            } else if (p1.score < p2.score) {
                return -1;
            } else {
                return 0;
            }
        }
    };

    public final static Comparator<BarPost> SCORE_ORDER_REVERSE = new Comparator<BarPost>() {
        public int compare(BarPost p1, BarPost p2) {
            return -1 * SCORE_ORDER.compare(p1, p2);
        }
    };

    public final static Comparator<BarPost> POST_TIME_ORDER = new Comparator<BarPost>() {
        public int compare(BarPost p1, BarPost p2) {
            return new Long(p1.post_time).compareTo(p2.post_time);
        }
    };

    public final static Comparator<BarPost> POST_TIME_ORDER_REVERSE = new Comparator<BarPost>() {
        public int compare(BarPost p1, BarPost p2) {
            return -1 * POST_TIME_ORDER.compare(p1, p2);
        }
    };

}
