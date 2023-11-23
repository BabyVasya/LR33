package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
public class InitiateBehavior extends FSMBehaviour {
    private CfgClass cfg;

    public InitiateBehavior(CfgClass cfg) {
        this.cfg = cfg;
    }

    @Override
    public void onStart() {
        this.registerFirstState(new StartInitiateBeh(cfg), "1");
        this.registerState(new Timeout(myAgent, 3000), "2");
        this.registerLastState(new GetResult(), "3");

        this.registerDefaultTransition("1", "2");
        this.registerDefaultTransition("2", "3");
    }

    public static class StartInitiateBeh extends OneShotBehaviour {
        CfgClass cfg;

        public StartInitiateBeh(CfgClass cfg) {
            this.cfg = cfg;
        }

        @Override
        public void action() {
            if (this.cfg.getInitiator() == true) {
                ACLMessage startMsg = new ACLMessage(ACLMessage.INFORM);
                for (int i = 0; i <= cfg.getNeighborAgents().size() - 1; i++) {
                    startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(i), false));
                    WayDto wayDto = new WayDto();
                    wayDto.setAllAgentsByWay(Arrays.asList(myAgent.getLocalName()));
                    wayDto.setWieght(cfg.getDistancesToNeighbors().get(i));
                    wayDto.setFindingAgent(cfg.getTargetAgentId());
                    wayDto.setInitiator(myAgent.getLocalName());
                    wayDto.setInitiatorCfg(cfg);
                    Gson gson = new Gson();
                    startMsg.setContent(gson.toJson(wayDto));
                    log.info(startMsg.toString());
                    getAgent().send(startMsg);
                    startMsg.clearAllReceiver();
                }

            }
        }
    }


        private static class Timeout extends WakerBehaviour {

            public Timeout(Agent a, long timeout) {
                super(a, timeout);
            }

            @Override
            protected void onWake() {
                log.info("Ожидание законилось, далее пойдет обработка результатов ");
            }
        }

        private static class GetResult extends Behaviour {

            private List<Object> resultCircuit = new ArrayList<>();
            private List<Double> resultWight = new ArrayList<>();
            private List<Object> resultCircuitTupik = new ArrayList<>();
            private List<Double> resultWightTupik = new ArrayList<>();
            @Override
            public void action() {
                ACLMessage resultMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
                ACLMessage resultMsg0 = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
                if (resultMsg != null) {
                    log.info("Инициатор получил сообщение с итоговым путем " + resultMsg);
                    Gson gson = new Gson();
                    BackWayDto backWayDto = gson.fromJson(resultMsg.getContent(), BackWayDto.class);
                    resultCircuit.add(backWayDto.getWayScircit());
                    resultWight.add(backWayDto.getWeightOfWay());
                    log.info("Лист " + resultCircuit + resultWight);
                    int index = IntStream.range(0, resultWight.size())
                            .reduce((i, j) -> resultWight.get(i) < resultWight.get(j) ? i : j).orElse(-1);
                    log.info("Лучший путь " + resultWight.get(index) + resultCircuit.get(index));
                }
                else if (resultMsg0 != null) {
                    log.info("Инициатор получил сообщение с тупиковым путем " + resultMsg);
                    Gson gson = new Gson();
                    WayDto wayDto = gson.fromJson(resultMsg0.getContent(), WayDto.class);
                    resultCircuitTupik.add(wayDto.getAllAgentsByWay());
                    resultWightTupik.add(wayDto.getWieght());
                    log.info("Лист с тупиками " + resultCircuitTupik + resultWightTupik);
                }
            }

            @Override
            public boolean done() {
                return false;
            }



        }

    }

