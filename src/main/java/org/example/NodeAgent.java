package org.example;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Slf4j
public class NodeAgent extends Agent {
    @Override
    protected void setup() {
        CfgClass cfg = null;
        {
            try {
                JAXBContext context =
                        JAXBContext.newInstance(CfgClass.class);
                Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
                switch (getLocalName()) {
                    case "Agent1":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent1Cfg.xml"));
                        log.info(String.valueOf(cfg.getInitiator()));
                        log.info(String.valueOf(cfg.getDistancesToNeighbors()));
                        log.info(String.valueOf(cfg.getNeighborAgents()));
                        break;
                    case "Agent2":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent2Cfg.xml"));
                        log.info(String.valueOf(cfg.getInitiator()));
                        log.info(String.valueOf(cfg.getDistancesToNeighbors()));
                        log.info(String.valueOf(cfg.getNeighborAgents()));
                        break;
                    case "Agent3":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent3Cfg.xml"));
                        log.info(String.valueOf(cfg.getInitiator()));
                        log.info(String.valueOf(cfg.getDistancesToNeighbors()));
                        log.info(String.valueOf(cfg.getNeighborAgents()));
                        break;
                    case "Agent4":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent4Cfg.xml"));
                        log.info(String.valueOf(cfg.getInitiator()));
                        log.info(String.valueOf(cfg.getDistancesToNeighbors()));
                        log.info(String.valueOf(cfg.getNeighborAgents()));
                        break;
                    case "Agent5":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent5Cfg.xml"));
                        log.info(String.valueOf(cfg.getInitiator()));
                        log.info(String.valueOf(cfg.getDistancesToNeighbors()));
                        log.info(String.valueOf(cfg.getNeighborAgents()));
                        break;
                }


            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        addBehaviour(new InitiateBehavior(cfg));
        addBehaviour(new RequestAnaylis(cfg));
    }
}

