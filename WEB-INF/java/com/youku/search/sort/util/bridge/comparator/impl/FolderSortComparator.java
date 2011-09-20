package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.Folder;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class FolderSortComparator extends AbstractSortComparator<Folder> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("update_time", Constant.Sort.SORT_NEW);
        orderFields.put("total_pv", Constant.Sort.SORT_PV);
        orderFields.put("video_count", Constant.Sort.SORT_FOLDER_VIDEOCOUNT);
        // orderFields.put("total_seconds", Constant.Sort.SORT_SECONDS);
        orderFields.put("total_comment", Constant.Sort.SORT_COMMENT);
    }

    public static final FolderSortComparator I = new FolderSortComparator();

    @Override
    public Comparator<Folder> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_NEW:
                return UPDATE_TIME_ORDER_REVERSE;

            case Constant.Sort.SORT_PV:
                return TOTAL_PV_ORDER_REVERSE;

            case Constant.Sort.SORT_FOLDER_VIDEOCOUNT:
                return VIDEO_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_COMMENT:
                return TOTAL_COMMENT_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;
            }

        } else {
            switch (orderField) {
            case Constant.Sort.SORT_NEW:
                return UPDATE_TIME_ORDER;

            case Constant.Sort.SORT_PV:
                return TOTAL_PV_ORDER;

            case Constant.Sort.SORT_FOLDER_VIDEOCOUNT:
                return VIDEO_COUNT_ORDER;

            case Constant.Sort.SORT_COMMENT:
                return TOTAL_COMMENT_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;
            }
        }
    }

    public final static Comparator<Folder> SCORE_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return Float.compare(f1.score, f2.score);
        }
    };

    public final static Comparator<Folder> SCORE_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * SCORE_ORDER.compare(f1, f2);
        }
    };

    public final static Comparator<Folder> UPDATE_TIME_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return new Long(f1.update_time).compareTo(f2.update_time);
        }
    };

    public final static Comparator<Folder> UPDATE_TIME_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * UPDATE_TIME_ORDER.compare(f1, f2);
        }
    };

    public final static Comparator<Folder> TOTAL_PV_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return new Integer(f1.total_pv).compareTo(f2.total_pv);
        }
    };

    public final static Comparator<Folder> TOTAL_PV_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * TOTAL_PV_ORDER.compare(f1, f2);
        }
    };

    public final static Comparator<Folder> VIDEO_COUNT_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return new Integer(f1.video_count).compareTo(f2.video_count);

        }
    };

    public final static Comparator<Folder> VIDEO_COUNT_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * VIDEO_COUNT_ORDER.compare(f1, f2);
        }
    };

    public final static Comparator<Folder> TOTAL_SECONDS_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {

            try {
                double t1 = Double.parseDouble(f1.total_seconds);
                double t2 = Double.parseDouble(f2.total_seconds);

                return Double.compare(t1, t2);

            } catch (Exception e) {
            }

            return 0;
        }
    };

    public final static Comparator<Folder> TOTAL_SECONDS_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * TOTAL_SECONDS_ORDER.compare(f1, f2);
        }
    };

    public final static Comparator<Folder> TOTAL_COMMENT_ORDER = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return new Integer(f1.total_comment).compareTo(f2.total_comment);
        }
    };

    public final static Comparator<Folder> TOTAL_COMMENT_ORDER_REVERSE = new Comparator<Folder>() {
        public int compare(Folder f1, Folder f2) {
            return -1 * TOTAL_COMMENT_ORDER.compare(f1, f2);
        }
    };

}
