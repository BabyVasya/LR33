package org.example;

import java.util.ArrayList;
import java.util.List;

public class WayDto {
    private String initiator;
    private double wieght;
    private String findingAgent;
    List<String> allAgentsByWay = new ArrayList<>();

    public WayDto(double wieght, String findingAgent, List<String> allAgentsByWay) {
        this.wieght = wieght;
        this.findingAgent = findingAgent;
        this.allAgentsByWay = allAgentsByWay;
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
