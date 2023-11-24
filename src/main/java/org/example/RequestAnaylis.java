package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import javax.xml.bind.JAXBException;
import java.util.*;


@Slf4j
public class RequestAnaylis extends Behaviour {
    private ACLMessage receivedMsg;

    private CfgClass cfg;
    private List<String> nb = new ArrayList<>();
    private List<Integer> nbD = new ArrayList<>();


    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        receivedMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (receivedMsg != null) {
            try {
                analyse(nb, nbD);
            } catch (JAXBException | CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        } else {
            block();
        }
    }

    private void analyse(List<String> nb, List<Integer> nbD) throws JAXBException, CloneNotSupportedException {
//        Инициализация конфига, gson, и парсинг пришедшего JSON
        nb = cfg.getNeighborAgents();
        nbD = cfg.getDistancesToNeighbors();
        Gson gson = new Gson();
        WayDto wayDto = gson.fromJson(receivedMsg.getContent(), WayDto.class);

        log.info("Новая интерация с поиском " + wayDto.getFindingAgent() + " информация на текущей интерации " + gson.toJson(wayDto) + " цепочка идет от " + receivedMsg.getSender().getLocalName());
//        Проверка на найденного агента
        if (myAgent.getLocalName().equals(wayDto.getFindingAgent())) {
            foundAgent(wayDto, gson);
            return;
        }
//        Проверка на тупик
        if (nb.contains(receivedMsg.getSender().getLocalName()) && nb.size() == 1) {
            tupik(wayDto, gson);
        }
//        Удаление ненужных агентов для исключения зацикливания
        exclusionOfUnnecessary(wayDto, nb, nbD);

//        Отправка для дальнейшего поиска нужного агента
        sendNext(wayDto, gson);


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
            getAgent().send(backMsg);
        }
        private void foundAgent(WayDto wayDto, Gson gson) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            log.info("Найдена нужная вершина" + gson.toJson(wayDto));
            ACLMessage backMsg = new ACLMessage(ACLMessage.CONFIRM);
            backMsg.addReceiver(new AID(wayDto.getAllAgentsByWay().get(wayDto.getAllAgentsByWay().size()-1), false));
            backMsg.setContent(gson.toJson(wayDto));
            getAgent().send(backMsg);
        }

        private void exclusionOfUnnecessary(WayDto wayDto, List<String> nb, List<Integer> nbD) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            List<String> tmpNb = new ArrayList<>(nb);
            List<Integer> tmpNbD = new ArrayList<>(nbD);
//                Нельзя идти назад
            for (int j = 0; j <= tmpNb.size() - 1; j++) {
                    for (int i = 0; i <= wayDto.getAllAgentsByWay().size() - 1; i++) {
                        if (tmpNb.contains(wayDto.getAllAgentsByWay().get(i))) {
                            tmpNbD.remove(tmpNb.indexOf(wayDto.getAllAgentsByWay().get(i)));
                            tmpNb.remove(wayDto.getAllAgentsByWay().get(i));

                        }
                    }
            }
//           Исключение соседей отправителя
            for (int j = 0; j <= tmpNb.size() - 1; j++){
                for (int i = 0; i <= wayDto.getSenderNeiborhoods().size() - 1; i++) {
                    if (tmpNb.contains(wayDto.getSenderNeiborhoods().get(i))) {
                        tmpNbD.remove(tmpNb.indexOf(wayDto.getSenderNeiborhoods().get(i)));
                        tmpNb.remove(wayDto.getSenderNeiborhoods().get(i));

                    }
                }
            }
            wayDto.setGoodWaysWeight(tmpNbD);
            wayDto.setMyNeibors(tmpNb);

        }

    private void sendNext(WayDto wayDto, Gson gson) throws CloneNotSupportedException {
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i <= wayDto.getMyNeibors().size() - 1; i++) {
            WayDto wayDtoCopy = (WayDto) wayDto.clone();
            nextAgentTo.addReceiver(new AID(wayDtoCopy.getMyNeibors().get(i), false));
            wayDtoCopy.setSenderNeiborhoods(cfg.getNeighborAgents());
            wayDtoCopy.getAllWieghtByWay().add(wayDtoCopy.getGoodWaysWeight().get(i));
            log.info("Increment " + wayDtoCopy.getWieght() + "идем к " +wayDtoCopy.getMyNeibors().get(i) +" цепочка от  " +wayDtoCopy.getAllAgentsByWay() + " "+ wayDtoCopy.getGoodWaysWeight().get(i));
            double incrementWay = wayDtoCopy.getWieght() + wayDtoCopy.getGoodWaysWeight().get(i);
            wayDtoCopy.setWieght(incrementWay);
            log.info("Increment after" + wayDtoCopy.getWieght()+ "идем к " +wayDtoCopy.getMyNeibors().get(i)  +" цепочка от  " +wayDtoCopy.getAllAgentsByWay() + " "+ wayDtoCopy.getGoodWaysWeight().get(i));
            nextAgentTo.setContent(gson.toJson(wayDtoCopy));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + wayDtoCopy.getMyNeibors().get(i) + gson.toJson(wayDtoCopy) );
            nextAgentTo.clearAllReceiver();
        }
    }


}

