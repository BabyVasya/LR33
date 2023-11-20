package org.example;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

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
                startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(0), false));
                startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(1), false));
                startMsg.setContent(cfg.getNeighborAgents().get(0) + " " +  cfg.getNeighborAgents().get(1) );
                getAgent().send(startMsg);
                log.info(startMsg.toString());
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
