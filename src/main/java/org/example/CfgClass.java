package org.example;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "cfg")
public class CfgClass {
    @XmlElement
    private boolean initiator;
    @XmlElement
    private List<String> neighborAgents;
    @XmlElement
    private List<Integer> distancesToNeighbors;
    @XmlElement
    private String targetAgentId;

    public boolean isInitiator() {
        return initiator;
    }

    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }
    public boolean getInitiator() {
        return this.initiator;
    }

    public List<String> getNeighborAgents() {
        return neighborAgents;
    }

    public void setNeighborAgents(List<String> neighborAgents) {
        this.neighborAgents = neighborAgents;
    }

    public List<Integer> getDistancesToNeighbors() {
        return distancesToNeighbors;
    }

    public void setDistancesToNeighbors(List<Integer> distancesToNeighbors) {
        this.distancesToNeighbors = distancesToNeighbors;
    }

    public String getTargetAgentId() {
        return targetAgentId;
    }

    public void setTargetAgentId(String targetAgentId) {
        this.targetAgentId = targetAgentId;
    }
}
