package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BackWayDto {
    private double weightOfWay;
    private String wayScircit;
    private List<String> backWay = new ArrayList<>();
    private int indexnext;

    public int getIndexnext() {
        return indexnext;
    }

    public void setIndexnext(int indexnext) {
        this.indexnext = indexnext;
    }

    public List<String> getBackWay() {
        return backWay;
    }

    public String getWayScircit() {
        return wayScircit;
    }

    public void setWayScircit(String wayScircit) {
        this.wayScircit = wayScircit;
    }

    public void setBackWay(List<String> backWay) {
        this.backWay = backWay;
    }
}
