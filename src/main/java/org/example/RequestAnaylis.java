package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class RequestAnaylis<E> extends Behaviour {
    private ACLMessage receivedMsg;

    private CfgClass cfg;
    private List<String> nb = new ArrayList<>();
    private List<Integer> nbD = new ArrayList<>();
    private List<CfgClass> neiborCfgs = new ArrayList<>();
    private List<String> neiborCfgsName = new ArrayList<>();


    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        receivedMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (receivedMsg != null) {
            try {
                analyse(nb, nbD);
            } catch (JAXBException e) {
                throw new RuntimeException(e);
            }
        } else {
            block();
        }
    }

    private void analyse(List<String> nb, List<Integer> nbD) throws JAXBException {
//        Инициализация конфига, gson, и парсинг пришедшего JSON
        nb = cfg.getNeighborAgents();
        nbD = cfg.getDistancesToNeighbors();
        log.info("Массив nb " + nb + " цепочка от " + receivedMsg.getSender().getLocalName());
        log.info("Массив nbD " + nbD+ " цепочка от " + receivedMsg.getSender().getLocalName());
        Gson gson = new Gson();
        WayDto wayDto = gson.fromJson(receivedMsg.getContent(), WayDto.class);

        nb.remove(wayDto.getInitiator());
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

        sendNext(wayDto, gson, nbD);


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


            for (int j = 0; j <= tmpNb.size() - 1; j++) {
                if (tmpNb.get(j).equals(receivedMsg.getSender().getLocalName())) {
                    tmpNb.remove(j);
                    tmpNbD.remove(j);
                }
            }

            for (int j = 0; j <= tmpNb.size() - 1; j++) {
                if (wayDto.getAllAgentsByWay().contains(tmpNb.get(j))) {
                    tmpNb.remove(j);
                    tmpNbD.remove(j);
                }
            }

                for (int j = 0; j <= tmpNb.size() - 1; j++) {
                    if (wayDto.getInitiatorCfg().getNeighborAgents().contains(wayDto.getSenderNeiborhoods().get(j))) {
                        tmpNb.remove(j);
                        tmpNbD.remove(j);
                    }
                }

                for (int j = 0; j <= tmpNb.size() - 1; j++) {
                    for (int i = 0; i <= wayDto.getAllAgentsByWay().size() - 1; i++) {
                        if (tmpNb.contains(wayDto.getAllAgentsByWay().get(i))) {
                            tmpNb.remove(j);
                            tmpNbD.remove(j);
                        }
                    }
                }
                wayDto.setAllWieghtByWay(tmpNbD);
                wayDto.setMyNeibors(tmpNb);

        }

    private void sendNext(WayDto wayDto, Gson gson, List<Integer> nbD) {
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i <= wayDto.getMyNeibors().size() - 1; i++) {
            nextAgentTo.addReceiver(new AID(wayDto.getMyNeibors().get(i), false));
            double incrementWay = wayDto.getWieght() + wayDto.getAllWieghtByWay().get(i);
            wayDto.setSenderNeiborhoods(cfg.getNeighborAgents());
            wayDto.setWieght(incrementWay);
            nextAgentTo.setContent(gson.toJson(wayDto));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + wayDto.getMyNeibors().get(i) + gson.toJson(wayDto) );
            nextAgentTo.clearAllReceiver();
        }
    }



    private void getNeiborneibors(CfgClass cfg) throws JAXBException {
        JAXBContext context =
                JAXBContext.newInstance(CfgClass.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        for (int i =0; i <= cfg.getNeighborAgents().size()-1; i++) {
            if(!cfg.getNeighborAgents().get(i).equals(receivedMsg.getSender().getLocalName())){
                switch (cfg.getNeighborAgents().get(i) ) {
                    case "Agent1":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent1Cfg.xml")));
                        neiborCfgsName.add("Agent1");
                        break;
                    case "Agent2":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent2Cfg.xml")));
                        neiborCfgsName.add("Agent2");
                        break;
                    case "Agent3":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent3Cfg.xml")));
                        neiborCfgsName.add("Agent3");
                        break;
                    case "Agent4":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent4Cfg.xml")));
                        neiborCfgsName.add("Agent4");
                        break;
                    case "Agent5":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent5Cfg.xml")));
                        neiborCfgsName.add("Agent5");
                        break;
                    case "Agent6":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent6Cfg.xml")));
                        neiborCfgsName.add("Agent6");
                        break;
                    case "Agent7":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent7Cfg.xml")));
                        neiborCfgsName.add("Agent7");
                        break;
                    case "Agent8":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent8Cfg.xml")));
                        neiborCfgsName.add("Agent8");
                        break;
                    case "Agent9":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent9Cfg.xml")));
                        neiborCfgsName.add("Agent9");
                        break;
                    case "Agent10":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent10Cfg.xml")));
                        neiborCfgsName.add("Agent10");
                        break;
                    case "Agent11":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent11Cfg.xml")));
                        neiborCfgsName.add("Agent11");
                        break;
                    case "Agent12":
                        neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent12Cfg.xml")));
                        neiborCfgsName.add("Agent12");
                        break;
                }
            }

        }
    }

}

