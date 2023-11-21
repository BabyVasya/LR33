package org.example;

import jade.core.AID;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class RequestAnaylis<E> extends Behaviour {
    private ACLMessage receivedMsg;
    private String endAgent;
    private boolean endFlag;
    private CfgClass cfg;
    private List<String> nb;
    private List<Integer> nbD;
    private int counter;
    private List<Object> way = new ArrayList<>();
    private int msgSize;

    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        receivedMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (receivedMsg != null) {
            msgSize =0;
            wayParsing(receivedMsg.getContent());
            analyse(nb, nbD);
        }
    }

    private void analyse(List<String> nb, List<Integer> nbD) {
        counter++;
        Data finding = new Data();
        log.info("Новая интерация с поиском " + finding.getCfgFind());
        nb = cfg.getNeighborAgents();
        nbD = cfg.getDistancesToNeighbors();
        if (nb.isEmpty() && nbD.isEmpty()) {
            log.info("Тупик");
            return;
        }
        if (nb.contains(finding.getCfgFind())) {
            endFlag = true;
            log.info("Найдена нужная вершина");
        }
        log.info("before " + nb + " " + nbD);
        log.info("Agent sender delete " + receivedMsg.getSender().getLocalName());
        int indexOfPrev = nb.indexOf(receivedMsg.getSender().getLocalName());
        nb.remove(indexOfPrev);
        nbD.remove(indexOfPrev);
        if (nb.isEmpty() && nbD.isEmpty()) {
            log.info("Тупик");
            return;
        }
        log.info("After" + nb + " " + nbD);
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        for (int j = 0; j <= msgSize - 1; j++) {
            way.add(myAgent.getLocalName());
            way.add(nb.get(j));
            way.add(nbD.get(j));
        }
        for (int i = 0; i <= nb.size() - 1; i++) {
            log.info(nb.size() + " size ");
            log.info("i = " + i);
            nextAgentTo.addReceiver(new AID(nb.get(i), false));
            nextAgentTo.setContent(String.valueOf(way));
            msgSize++;
            if (msgSize == nb.size()) {
                getAgent().send(nextAgentTo);
                log.info(nextAgentTo.toString());
            }
        }


    }

    private void wayParsing(String message) {
        if (counter > 0) {
            counter++;
            way.clear();
            String content = message.substring(1, message.length() - 1).trim();
            String[] elements = content.split(", ");
            for (String element : elements) {
                if (element.startsWith("Agent")) {
                    way.add(element);
                } else {
                    way.add(Integer.parseInt(element));
                }
            }
            log.info("Result of parsing way  " + way);
        }
    }
        @Override
        public boolean done () {
            return endFlag;
        }
}

