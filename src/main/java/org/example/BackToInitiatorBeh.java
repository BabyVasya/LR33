package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.behaviours.ActionExecutor;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Slf4j
public class BackToInitiatorBeh extends Behaviour {
    private CfgClass cfg;
    private boolean flag;
    List<String> tmp;
    List<String> tmp2;

    public BackToInitiatorBeh(CfgClass cfg) {
        this.cfg = cfg;
    }

    @Override
    public void action() {
        ACLMessage backMsg1 = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        ACLMessage backMsg = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
        if (backMsg!=null ) {
            log.info("Получил запрос на обратный путь " + backMsg.getContent());
            BackWayDto backWayDto = new BackWayDto();
            Gson gson = new Gson();
            WayDto wayDto = gson.fromJson(backMsg.getContent(), WayDto.class);
            List<String> tmp = wayDto.allAgentsByWay;
            List<String> tmp2 = wayDto.allAgentsByWay;
            backWayDto.setWayScircit(String.valueOf(tmp2));
            backWayDto.setBackWay(tmp2);
            backWayDto.setWeightOfWay(wayDto.getWieght());
            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
            backway1.addReceiver(new AID(tmp.get(tmp.size()-2), false));
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            backway1.setContent(gson.toJson(backWayDto));
            getAgent().send(backway1);
        }
        if (backMsg1!=null) {
            Gson gson = new Gson();
            BackWayDto backWayDto = gson.fromJson(backMsg1.getContent(), BackWayDto.class);
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
            backway1.addReceiver(new AID(backWayDto.getBackWay().get(backWayDto.getBackWay().size()-1), false));
            backway1.setContent(gson.toJson(backWayDto));
            if (backWayDto.getBackWay().size() > 1) {
                getAgent().send(backway1);
            } else {
                ACLMessage toInitiateMsg = new ACLMessage(ACLMessage.REQUEST);
                toInitiateMsg.addReceiver(new AID(backWayDto.getBackWay().get(0), false));
                toInitiateMsg.setContent(gson.toJson(backWayDto));
                log.info("Пересылка к инициатору " + toInitiateMsg);
                getAgent().send(toInitiateMsg);
            }
        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
