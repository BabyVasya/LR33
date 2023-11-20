package org.example;

import jade.core.AID;
import jade.core.behaviours.FSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InitiateBehavior extends FSMBehaviour {

    CfgClass cfg;
    public InitiateBehavior(CfgClass cfg) {
        this.cfg = cfg;
    }
    @Override
    public void onStart() {
        this.registerFirstState(new StartInitiateBeh(this.cfg), "StartInitiateBeh");
    }

    public static class StartInitiateBeh extends OneShotBehaviour {
        CfgClass cfg;
        public StartInitiateBeh(CfgClass cfg) {
            this.cfg = cfg;
        }
        @Override
        public void action() {
//            String neigh1 = cfg.getNeighborAgents().get(1);
//            ACLMessage startMsg = new ACLMessage(ACLMessage.INFORM);
//            startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(0), false));
//            startMsg.addReceiver(new AID(cfg.getNeighborAgents().get(1), false));
//            startMsg.setContent("Initiate agent " + myAgent.getLocalName() + "sened to ");
//            log.info(startMsg.getContent());
        }
    }
}
