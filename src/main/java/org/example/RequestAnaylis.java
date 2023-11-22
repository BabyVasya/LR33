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


        if (myAgent.getLocalName().equals(wayDto.getFindingAgent())) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            log.info("Найдена нужная вершина" + gson.toJson(wayDto));
            ACLMessage backMsg = new ACLMessage(ACLMessage.CONFIRM);
            backMsg.addReceiver(new AID(wayDto.getAllAgentsByWay().get(wayDto.getAllAgentsByWay().size()-1), false));
            backMsg.setContent(gson.toJson(wayDto));
            log.info("Отсылка результата " + backMsg);
            getAgent().send(backMsg);

        }

        if (nb.contains(receivedMsg.getSender().getLocalName()) && nb.size() == 1) {
            tupik(wayDto, gson);
        }

        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
        List<String> tmpNb = nb.stream().collect(Collectors.toList());

        log.info("tmpNb перед циклами " + tmpNb);
        tmpNb.removeAll(wayDto.getInitiatorCfg().getNeighborAgents());
        for (int j=0; j<=tmpNb.size()-1; j++) {
            if(tmpNb.get(j).equals(receivedMsg.getSender().getLocalName())) {
                log.info("Удаляю моего отправителя " + receivedMsg.getSender().getLocalName() + " " + !tmpNb.get(j).equals(receivedMsg.getSender().getLocalName()) + " " + tmpNb);
                tmpNb.remove(j);
            }
        }

        for (int j=0; j<=tmpNb.size()-1; j++) {
            if(wayDto.getAllAgentsByWay().contains(tmpNb.get(j))) {
                log.info("Удаляю повторения " + wayDto.getAllAgentsByWay() + " " + !wayDto.getAllAgentsByWay().contains(tmpNb.get(j)) + " " + tmpNb);
                tmpNb.remove(j);
            }
        }
        log.info("tmpNb после циклов" + tmpNb);

        for (int i = 0; i <= tmpNb.size() - 1; i++) {
            log.info("Добавляю получателя " + tmpNb.get(i));
            nextAgentTo.addReceiver(new AID(tmpNb.get(i), false));
            double incrementWay = wayDto.getWieght() + nbD.get(i);
            wayDto.setWieght(incrementWay);
            nextAgentTo.setContent(gson.toJson(wayDto));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + tmpNb.get(i) + gson.toJson(wayDto) );
            nextAgentTo.clearAllReceiver();
        }


    }

        @Override
        public boolean done () {
            return false;
        }

        private void tupik(WayDto wayDto, Gson gson){
            log.info("Тупик");
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            ACLMessage backMsg = new ACLMessage(ACLMessage.REFUSE);
            backMsg.addReceiver(new AID(wayDto.getInitiator(), false));
            backMsg.setContent(gson.toJson(wayDto));
            log.info("Отсылка результата  с тупиком " + backMsg);
            getAgent().send(backMsg);
        }
        private void foundAgent() {

        }
}

