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

                        break;
                    case "Agent2":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent2Cfg.xml"));

                        break;
                    case "Agent3":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent3Cfg.xml"));

                        break;
                    case "Agent4":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent4Cfg.xml"));
                        break;
                    case "Agent5":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent5Cfg.xml"));
                        break;
                    case "Agent6":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent6Cfg.xml"));
                        break;
                    case "Agent7":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent7Cfg.xml"));
                        break;
                    case "Agent8":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent8Cfg.xml"));
                        break;
                    case "Agent9":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent9Cfg.xml"));
                        break;
                    case "Agent10":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent10Cfg.xml"));
                        break;
                    case "Agent11":
                        cfg = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent11Cfg.xml"));
                        break;
                }


            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        addBehaviour(new InitiateBehavior(cfg));
        addBehaviour(new RequestAnaylis(cfg));
        addBehaviour(new BackToInitiatorBeh(cfg));
    }
}

