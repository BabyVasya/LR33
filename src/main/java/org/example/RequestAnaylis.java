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
    private CfgClass nCfg ;
    private List<Object> neiborCfgs = new ArrayList<>();


    public RequestAnaylis(CfgClass cfg) {
        this.cfg = cfg;
    }


    @Override
    public void action() {
        receivedMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (receivedMsg != null) {
            analyse(nb, nbD);
        } else {
            block();
        }
    }

    private void analyse(List<String> nb, List<Integer> nbD) {
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
        }
//        Проверка на тупик
        if (nb.contains(receivedMsg.getSender().getLocalName()) && nb.size() == 1) {
            tupik(wayDto, gson);
        }
//        Удаление ненужных агентов для исключения зацикливания
        List<String> onlyGoodWays = exclusionOfUnnecessary(wayDto, nb);

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
            wayDto.getAllAgentsByWay().remove(myAgent.getLocalName());
            log.info("Найдена нужная вершина" + gson.toJson(wayDto));
            ACLMessage backMsg = new ACLMessage(ACLMessage.CONFIRM);
            backMsg.addReceiver(new AID(wayDto.getAllAgentsByWay().get(wayDto.getAllAgentsByWay().size()-1), false));
            backMsg.setContent(gson.toJson(wayDto));
            log.info("Отсылка результата с найденой вершиной " + backMsg);
            getAgent().send(backMsg);
        }

        private List<String> exclusionOfUnnecessary(WayDto wayDto, List<String> nb) {
            wayDto.getAllAgentsByWay().add(myAgent.getLocalName());
            List<String> tmpNb = nb.stream().collect(Collectors.toList());

            log.info("tmpNb перед циклами " + tmpNb);
            tmpNb.removeAll(wayDto.getInitiatorCfg().getNeighborAgents());
            for (int j=0; j<=tmpNb.size()-1; j++) {
                if(tmpNb.get(j).equals(receivedMsg.getSender().getLocalName())) {
                    log.info("Удаляю моего отправителя " + receivedMsg.getSender().getLocalName() + " " + tmpNb);
                    tmpNb.remove(j);
                }
            }

            for (int j=0; j<=tmpNb.size()-1; j++) {
                if(wayDto.getAllAgentsByWay().contains(tmpNb.get(j))) {
                    log.info("Удаляю повторения " + wayDto.getAllAgentsByWay() + " " + !wayDto.getAllAgentsByWay().contains(tmpNb.get(j)) + " " + tmpNb);
                    tmpNb.remove(j);
                }
            }

            for (int j=0; j<=tmpNb.size()-1; j++) {
                if(wayDto.getAllAgentsByWay().contains(wayDto.getSenderNeiborhoods().get(j))) {
                    log.info("Удаляю соседа отправителя " + wayDto.getAllAgentsByWay() + " " + !wayDto.getAllAgentsByWay().contains(tmpNb.get(j)) + " " + tmpNb);
                    tmpNb.remove(j);
                }
            }
            log.info("tmpNb после циклов " + tmpNb);
            return  tmpNb;
    }

    private void sendNext(List<String> onlyGoodWays, WayDto wayDto, Gson gson, List<Integer> nbD) {
        ACLMessage nextAgentTo = new ACLMessage(ACLMessage.INFORM);
        for (int i = 0; i <= onlyGoodWays .size() - 1; i++) {
            nextAgentTo.addReceiver(new AID(onlyGoodWays .get(i), false));
            double incrementWay = wayDto.getWieght() + nbD.get(i);
            wayDto.setSenderNeiborhoods(cfg.getNeighborAgents());
            wayDto.setNeiborNeibors();
            wayDto.setWieght(incrementWay);
            nextAgentTo.setContent(gson.toJson(wayDto));
            getAgent().send(nextAgentTo);
            log.info(myAgent.getLocalName() + " отослал информацию к " + onlyGoodWays .get(i) + gson.toJson(wayDto) );
            nextAgentTo.clearAllReceiver();
        }
    }

    private void getNeiborneibors(CfgClass cfg) throws JAXBException {
        JAXBContext context =
                JAXBContext.newInstance(CfgClass.class);
        Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
        for (int i =0; i <= cfg.getNeighborAgents().size()-1; i++) {
            switch (cfg.getNeighborAgents().get(i)) {
                case "Agent1":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent1Cfg.xml")));
                    break;
                case "Agent2":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent2Cfg.xml")));
                    break;
                case "Agent3":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent3Cfg.xml")));
                    break;
                case "Agent4":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent4Cfg.xml")));
                    break;
                case "Agent5":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent5Cfg.xml")));
                    break;
                case "Agent6":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent6Cfg.xml")));
                    break;
                case "Agent7":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent7Cfg.xml")));
                    break;
                case "Agent8":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent8Cfg.xml")));
                    break;
                case "Agent9":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent9Cfg.xml")));
                    break;
                case "Agent10":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent10Cfg.xml")));
                    break;
                case "Agent11":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent11Cfg.xml")));
                    break;
                case "Agent12":
                    neiborCfgs.add((CfgClass) jaxbUnmarshaller.unmarshal(new
                            File("src/main/resources/agent12Cfg.xml")));
                    break;
            }
        }
    }

}

