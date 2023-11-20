package org.example;

import jade.core.AID;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
public class RequestAnaylis extends Behaviour {
    private String endAgent;
    private boolean endFlag;
    private CfgClass cfg;
    private int min;
    private List<String> nb;
    private List<Integer> nbD;
    private List<String> wayListAgent = new ArrayList<>();
    private List<Integer> wayListWight = new ArrayList<>();
    private ACLMessage processMsg;
    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        processMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if(processMsg != null ) {
            log.info("новая интерация");
            nb = cfg.getNeighborAgents();
            nbD = cfg.getDistancesToNeighbors();
            if(nb.contains(cfg.getTargetAgentId())) {
                endFlag = true;
                log.info("Найдена нужная вершина");
                return;
            }
            log.info("before " + nb + " " + nbD);
            int backIndex = nb.indexOf(processMsg.getSender().getLocalName());
            nb.remove(backIndex);
            nbD.remove(backIndex);
            if(nb.isEmpty() && nbD.isEmpty()) {
               endFlag = true;
               log.info("Тупик");
               return;
            }
            log.info("After" + nb + " " + nbD);
            min = IntStream.range(0, nbD.size())
                    .reduce((i, j) -> nbD.get(i) < nbD.get(j) ? i : j).getAsInt();
            log.info("Good way " + min );
            ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
            nextAgentTo.addReceiver(new AID(nb.get(min), false));
            nextAgentTo.setContent("Next turn to " +  nb.get(min) + " with way " + nbD.get(min));
            log.info(nextAgentTo.toString());
            wayListAgent.add(nb.get(min));
            wayListWight.add(nbD.get(min));
            getAgent().send(nextAgentTo);
        }
    }

    @Override
    public boolean done() {
        return endFlag;
    }
}
