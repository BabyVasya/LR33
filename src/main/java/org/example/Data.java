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

public class Data {
        private CfgClass cfgFind = null;
        {
            try {
                JAXBContext context =
                        JAXBContext.newInstance(CfgClass.class);
                Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
                        cfgFind = (CfgClass) jaxbUnmarshaller.unmarshal(new
                                File("src/main/resources/agent4Cfg.xml"));

            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

    public String getCfgFind() {
        return cfgFind.getTargetAgentId();
    }
}
