package org.example;

import java.util.ArrayList;
import java.util.List;


public class BackWayDto {
    private  String initiator;
    private boolean tupik = false;

    public boolean isTupik() {
        return tupik;
    }

    public void setTupik(boolean tupik) {
        this.tupik = tupik;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    private double weightOfWay;
    private String wayScircit;
    private List<String> backWay = new ArrayList<>();
    private int indexnext;

    public int getIndexnext() {
        return indexnext;
    }

    public double getWeightOfWay() {
        return weightOfWay;
    }

    public void setWeightOfWay(double weightOfWay) {
        this.weightOfWay = weightOfWay;
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
