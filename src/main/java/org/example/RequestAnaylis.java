package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
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


    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        receivedMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (receivedMsg != null) {
            analyse(nb, nbD);
        }
    }

    private void analyse(List<String> nb, List<Integer> nbD) {
        nb = cfg.getNeighborAgents();
        nbD = cfg.getDistancesToNeighbors();
        log.info("Массив nb " + nb + " цепочка от " + receivedMsg.getSender().getLocalName());
        log.info("Массив nbD " + nbD+ " цепочка от " + receivedMsg.getSender().getLocalName());
        Gson gson = new Gson();
        WayDto wayDto = gson.fromJson(receivedMsg.getContent(), WayDto.class);
        nb.remove(wayDto.getInitiator());
        log.info("Новая интерация с поиском " + wayDto.getFindingAgent() + " информация на текущей интерации " + gson.toJson(wayDto) + " цепочка идет от " + receivedMsg.getSender().getLocalName());
        if(myAgent.getLocalName().equals("Agent10")) {
            log.info("Я тут");
        }

        if (nb.contains(receivedMsg.getSender().getLocalName()) && nb.size() == 1) {
            log.info("Тупик");
            endFlag = true;

        }
        if (myAgent.getLocalName().equals(wayDto.getFindingAgent())) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            log.info("Найдена нужная вершина" + gson.toJson(wayDto));
            counter++;
            if(counter ==4) {
                endFlag = true;
            }

        }
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
        for (int i = 0; i <= nb.size() - 1; i++) {
            if (!nb.get(i).equals(receivedMsg.getSender().getLocalName()) && !wayDto.getAllAgentsByWay().contains(nb.get(i))) {
                log.info("Добавляю получателя " + nb.get(i));
                nextAgentTo.addReceiver(new AID(nb.get(i), false));
            }
            double incrementWay = wayDto.getWieght() + nbD.get(i);
            wayDto.setWieght(incrementWay);
            nextAgentTo.setContent(gson.toJson(wayDto));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + nb.get(i) + gson.toJson(wayDto));
            nextAgentTo.clearAllReceiver();
        }


    }

        @Override
        public boolean done () {
            return endFlag;
        }
}

