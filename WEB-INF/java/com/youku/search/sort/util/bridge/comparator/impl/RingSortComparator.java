package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Ring;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class RingSortComparator extends AbstractSortComparator<Ring> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("cprice", Constant.Sort.SORT_RING_PRICE);
        orderFields.put("cdate", Constant.Sort.SORT_RING_DATE);
    }

    public static final RingSortComparator I = new RingSortComparator();

    @Override
    public Comparator<Ring> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_RING_PRICE:
                return PRICE_ORDER_REVERSE;

            case Constant.Sort.SORT_RING_DATE:
                return DATE_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;

            }
        } else {
            switch (orderField) {
            case Constant.Sort.SORT_RING_PRICE:
                return PRICE_ORDER;

            case Constant.Sort.SORT_RING_DATE:
                return DATE_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;

            }
        }
    }

    public final static Comparator<Ring> SCORE_ORDER = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return Float.compare(r1.score, r2.score);
        }
    };

    public final static Comparator<Ring> SCORE_ORDER_REVERSE = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return -1 * SCORE_ORDER.compare(r1, r2);
        }
    };

    public final static Comparator<Ring> PRICE_ORDER = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return new Integer(r1.cprice).compareTo(r2.cprice);
        }
    };

    public final static Comparator<Ring> PRICE_ORDER_REVERSE = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return -1 * PRICE_ORDER.compare(r1, r2);
        }
    };

    public final static Comparator<Ring> DATE_ORDER = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return new Long(r1.cdate).compareTo(r2.cdate);
        }
    };

    public final static Comparator<Ring> DATE_ORDER_REVERSE = new Comparator<Ring>() {
        public int compare(Ring r1, Ring r2) {
            return -1 * DATE_ORDER.compare(r1, r2);
        }
    };

}
