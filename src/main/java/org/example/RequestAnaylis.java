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
    private List<String> nb = new ArrayList<>();
    private List<Integer> nbD = new ArrayList<>();
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
//        Инициализация конфига, gson, и парсинг пришедшего JSON
        nbD = cfg.getDistancesToNeighbors();
        nb = cfg.getNeighborAgents();
        Gson gson = new Gson();
        WayDto wayDto = gson.fromJson(receivedMsg.getContent(), WayDto.class);
        log.info("Before remove nb" + nb + nb.size() + " Before remove nbD" + nbD + nbD.size());
        List<String> finalNb = nb;
        int indexToRemove = 0;
        for (int i = 0; i <= nb.size()-1; i++) {
            if (nb.get(i).equals(wayDto.getInitiator())) {
                indexToRemove = i;
                log.info("indexToRemove  " + indexToRemove);
                break;
            }
        }
        nb.remove(indexToRemove);
        if (indexToRemove != - 1) {
            nbD.remove(indexToRemove);
        } else {
            log.info("элемент не наайден в списке");
        }
        log.info("After remove nb" + nb + nb.size() + " After remove nbD" + nbD + nbD.size());
        log.info("Новая интерация с поиском " + wayDto.getFindingAgent() + " информация на текущей интерации " + gson.toJson(wayDto) + " цепочка идет от " + receivedMsg.getSender().getLocalName());
//        Проверка на найденного агента
        if (myAgent.getLocalName().equals(wayDto.getFindingAgent())) {
            foundAgent(wayDto, gson);
        }
//        Проверка на тупик
        if (nb.contains(receivedMsg.getSender().getLocalName()) && nb.size() == 1) {
            tupik(wayDto, gson);
        }
//        Удаление ненужных агентов для исключения зацикливания
        List<String> onlyGoodWays = exclusionOfUnnecessary(wayDto, nb, nbD);

//        Отправка для дальнейшего поиска нужного агента
        sendNext(onlyGoodWays, wayDto, gson, nbD);
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
        private void foundAgent(WayDto wayDto, Gson gson) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            log.info("Найдена нужная вершина" + gson.toJson(wayDto));
            ACLMessage backMsg = new ACLMessage(ACLMessage.CONFIRM);
            backMsg.addReceiver(new AID(wayDto.getAllAgentsByWay().get(wayDto.getAllAgentsByWay().size()-1), false));
            backMsg.setContent(gson.toJson(wayDto));
            log.info("Отсылка результата с найденой вершиной" + backMsg);
            getAgent().send(backMsg);
            block();
        }

        private List<String> exclusionOfUnnecessary(WayDto wayDto, List<String> nb, List<Integer> nbD) {

        wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
        List<String> tmpNb = new ArrayList<>(nb);
        List<Integer> tmpNbD = new ArrayList<>(nbD);
        log.info("Size of tmpNb " + tmpNb + tmpNb.size());
        log.info("Size of tmpNbD " + tmpNbD+ tmpNbD.size());
        List<Integer> indicesToRemoveInNbD = IntStream.range(0, tmpNb.size())
                .filter(i -> tmpNb.contains(tmpNb.get(i)))
                .boxed()
                .collect(Collectors.toList());

        List<Integer> tmpNbD1 = IntStream.range(0, tmpNbD.size())
                .filter(i -> !indicesToRemoveInNbD.contains(i))
                .mapToObj(tmpNbD::get)
                .collect(Collectors.toList());

        tmpNb.removeAll(wayDto.getInitiatorCfg().getNeighborAgents());
        log.info("Efore cycle delete " + tmpNb + tmpNbD1);

        if(tmpNbD1.size()>0 && tmpNb.size()>0) {
            for (int j=0; j<=tmpNb.size()-1; j++) {
                if(tmpNb.get(j).equals(receivedMsg.getSender().getLocalName())) {
                    tmpNb.remove(j);
                    tmpNbD1.remove(j);
                    log.info("Fter delete " + tmpNb + tmpNbD1);
                }
            }

            for (int j=0; j<=tmpNb.size()-1; j++) {
                if(wayDto.getAllAgentsByWay().contains(tmpNb.get(j))) {
                    tmpNb.remove(j);
                    tmpNbD1.remove(j);
                    log.info("Fter delete " + tmpNb + tmpNbD1);
                }
            }
        }
            return  tmpNb;
    }

    private void sendNext(List<String> onlyGoodWays, WayDto wayDto, Gson gson, List<Integer> nbD) {
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i <= onlyGoodWays .size() - 1; i++) {
            nextAgentTo.addReceiver(new AID(onlyGoodWays .get(i), false));
            double incrementWay = wayDto.getWieght() + nbD.get(i);
            wayDto.setWieght(incrementWay);
            nextAgentTo.setContent(gson.toJson(wayDto));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + onlyGoodWays .get(i) + gson.toJson(wayDto) );
            nextAgentTo.clearAllReceiver();
        }
    }
}

