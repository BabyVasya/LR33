package org.example;

import java.util.ArrayList;
import java.util.List;

public class WayDto {
    private boolean tupik = false;
    private String initiator;
    private double wieght;
    private String findingAgent;
    private List<String> allAgentsByWay = new ArrayList<>();
    private List<Integer> allWieghtByWay = new ArrayList<>();
    private CfgClass initiatorCfg;
    private CfgClass anyCfg;

    public List<Integer> getAllWieghtByWay() {
        return allWieghtByWay;
    }

    public void setAllWieghtByWay(List<Integer> allWieghtByWay) {
        this.allWieghtByWay = allWieghtByWay;
    }
    private List<String> myNeibors = new ArrayList<>();

    public List<String> getMyNeibors() {
        return myNeibors;
    }

    public void setMyNeibors(List<String> myNeibors) {
        this.myNeibors = myNeibors;
    }

    private List<String> senderNeiborhoods = new ArrayList<>();
    private List<String> neiborNeibors = new ArrayList<>();

    public List<String> getNeiborNeibors() {
        return neiborNeibors;
    }

    public void setNeiborNeibors(List<String> neiborNeibors) {
        this.neiborNeibors = neiborNeibors;
    }

    public CfgClass getAnyCfg() {
        return anyCfg;
    }

    public List<String> getSenderNeiborhoods() {
        return senderNeiborhoods;
    }

    public void setSenderNeiborhoods(List<String> senderNeiborhoods) {
        this.senderNeiborhoods = senderNeiborhoods;
    }

    public void setAnyCfg(CfgClass anyCfg) {
        this.anyCfg = anyCfg;
    }

    public boolean isTupik() {
        return tupik;
    }

    public void setTupik(boolean tupik) {
        this.tupik = tupik;
    }

    public CfgClass getInitiatorCfg() {
        return initiatorCfg;
    }

    public void setInitiatorCfg(CfgClass initiatorCfg) {
        this.initiatorCfg = initiatorCfg;
    }

    public double getWieght() {
        return wieght;
    }

    public void setWieght(double wieght) {
        this.wieght = wieght;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public String getFindingAgent() {
        return findingAgent;
    }

    public void setFindingAgent(String findingAgent) {
        this.findingAgent = findingAgent;
    }

    public List<String> getAllAgentsByWay() {
        return allAgentsByWay;
    }

    public void setAllAgentsByWay(List<String> allAgentsByWay) {
        this.allAgentsByWay = allAgentsByWay;
    }
}
