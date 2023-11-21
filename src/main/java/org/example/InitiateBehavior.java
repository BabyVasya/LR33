package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class InitiateBehavior extends FSMBehaviour {
    private CfgClass cfg;
    public InitiateBehavior(CfgClass cfg) {
        this.cfg = cfg;
    }
    @Override
    public void onStart() {
        this.registerFirstState(new StartInitiateBeh(cfg), "1");
        this.registerLastState(new StartInitiateBeh(cfg), "1");
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
                for (int i =0; i <= cfg.getNeighborAgents().size()-1; i++) {
                    startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(i), false));
                    WayDto wayDto = new WayDto(cfg.getDistancesToNeighbors().get(i), cfg.getTargetAgentId(), Arrays.asList(myAgent.getLocalName()));
                    wayDto.setInitiator(myAgent.getLocalName());
                    Gson gson = new Gson();
                    startMsg.setContent(gson.toJson(wayDto));
                    log.info(startMsg.toString());
                    getAgent().send(startMsg);
                    startMsg.clearAllReceiver();
                }

            }
        }
    }

    public static class WaitAndAnalysBeh extends WakerBehaviour {


        public WaitAndAnalysBeh(Agent a, long timeout) {
            super(a, timeout);
        }
        @Override
        protected void onWake() {

        }

    }
}
