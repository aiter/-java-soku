package com.youku.search.sort.servlet;

public class ChangeParam {
    public static final int GROUP_NULL = -1;

    public static final int BACK_FALSE = 0;
    public static final int BACK_TRUE = 1;

    public int group;
    public boolean back;

    @Override
    public String toString() {
        return "group: " + group + ", back: " + back;
    }
}