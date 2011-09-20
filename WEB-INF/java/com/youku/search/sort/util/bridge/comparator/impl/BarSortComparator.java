package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Bar;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class BarSortComparator extends AbstractSortComparator<Bar> {
    public static final BarSortComparator I = new BarSortComparator();

    @Override
    public int getOrderField(String orderField) {
        return Constant.Sort.SORT_SCORE;
    }

    @Override
    public Comparator<Bar> getComparator(int orderField, boolean reverse) {
        return SCORE_ORDER_REVERSE;
    }

    public final static Comparator<Bar> SCORE_ORDER = new Comparator<Bar>() {
        public int compare(Bar p1, Bar p2) {
            return Float.compare(p1.score, p2.score);
        }
    };

    public final static Comparator<Bar> SCORE_ORDER_REVERSE = new Comparator<Bar>() {
        public int compare(Bar p1, Bar p2) {
            return -1 * SCORE_ORDER.compare(p1, p2);
        }
    };
}
