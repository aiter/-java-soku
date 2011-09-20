package com.youku.soku.newext.util;

public class Cost {
    public long start;
    public long end;

    public Cost() {
        start = System.currentTimeMillis();
    }

    public Cost(long start) {
        this.start = start;
    }

    public Cost(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public void updateStart() {
        start = System.currentTimeMillis();
    }

    public void updateEnd() {
        end = System.currentTimeMillis();
    }

    public long getCost() {
        return end - start;
    }

    @Override
    public String toString() {
        return "cost: " + getCost();
    }

    public static void main(String[] args) throws Exception {
        Cost cost = new Cost();
        Thread.sleep(200);
        cost.updateEnd();
        System.out.println(cost);
    }
}
