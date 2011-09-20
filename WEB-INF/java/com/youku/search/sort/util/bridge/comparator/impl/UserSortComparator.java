package com.youku.search.sort.util.bridge.comparator.impl;

import java.util.Comparator;

import com.youku.search.index.entity.User;
import com.youku.search.sort.util.bridge.comparator.AbstractSortComparator;
import com.youku.search.util.Constant;

public class UserSortComparator extends AbstractSortComparator<User> {

    {
        orderFields.put("null", Constant.Sort.SORT_SCORE);
        orderFields.put("video_count", Constant.Sort.SORT_USER_VIDEO_COUNT);
        orderFields.put("fav_count", Constant.Sort.SORT_USER_FAV_COUNT);
        orderFields.put("reg_date", Constant.Sort.SORT_USER_NEW_REG);
        orderFields
                .put("last_content_date", Constant.Sort.SORT_USER_NEW_UPDATE);
        orderFields.put("user_score", Constant.Sort.SORT_USER_SCORE);
    }

    public static final UserSortComparator I = new UserSortComparator();

    @Override
    public Comparator<User> getComparator(int orderField, boolean reverse) {

        if (reverse) {
            switch (orderField) {
            case Constant.Sort.SORT_USER_VIDEO_COUNT:
                return VIDEO_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_USER_FAV_COUNT:
                return FAV_COUNT_ORDER_REVERSE;

            case Constant.Sort.SORT_USER_NEW_REG:
                return REG_DATE_ORDER_REVERSE;

            case Constant.Sort.SORT_USER_NEW_UPDATE:
                return LAST_CONTENT_DATE_ORDER_REVERSE;

            case Constant.Sort.SORT_USER_SCORE:
                return USER_SCORE_ORDER_REVERSE;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER_REVERSE;

            }
        } else {
            switch (orderField) {
            case Constant.Sort.SORT_USER_VIDEO_COUNT:
                return VIDEO_COUNT_ORDER;

            case Constant.Sort.SORT_USER_FAV_COUNT:
                return FAV_COUNT_ORDER;

            case Constant.Sort.SORT_USER_NEW_REG:
                return REG_DATE_ORDER;

            case Constant.Sort.SORT_USER_NEW_UPDATE:
                return LAST_CONTENT_DATE_ORDER;

            case Constant.Sort.SORT_USER_SCORE:
                return USER_SCORE_ORDER;

            case Constant.Sort.SORT_SCORE:
            default:
                return SCORE_ORDER;

            }
        }
    }

    public final static Comparator<User> SCORE_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return Float.compare(u1.score, u2.score);
        }
    };

    public final static Comparator<User> SCORE_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * SCORE_ORDER.compare(u1, u2);
        }
    };

    public final static Comparator<User> VIDEO_COUNT_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return new Integer(u1.video_count).compareTo(u2.video_count);
        }
    };

    public final static Comparator<User> VIDEO_COUNT_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * VIDEO_COUNT_ORDER.compare(u1, u2);
        }
    };

    public final static Comparator<User> FAV_COUNT_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return new Integer(u1.fav_count).compareTo(u2.fav_count);
        }
    };

    public final static Comparator<User> FAV_COUNT_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * FAV_COUNT_ORDER.compare(u1, u2);
        }
    };

    public final static Comparator<User> REG_DATE_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return new Long(u1.reg_date).compareTo(u2.reg_date);
        }
    };

    public final static Comparator<User> REG_DATE_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * REG_DATE_ORDER.compare(u1, u2);
        }
    };

    public final static Comparator<User> LAST_CONTENT_DATE_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return new Long(u1.last_content_date)
                    .compareTo(u2.last_content_date);
        }
    };

    public final static Comparator<User> LAST_CONTENT_DATE_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * LAST_CONTENT_DATE_ORDER.compare(u1, u2);
        }
    };

    public final static Comparator<User> USER_SCORE_ORDER = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return new Integer(u1.user_score).compareTo(u2.user_score);
        }
    };

    public final static Comparator<User> USER_SCORE_ORDER_REVERSE = new Comparator<User>() {
        public int compare(User u1, User u2) {
            return -1 * USER_SCORE_ORDER.compare(u1, u2);
        }
    };

}
