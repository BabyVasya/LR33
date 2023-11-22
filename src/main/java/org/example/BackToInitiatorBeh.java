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
//            BackWayDto backWayDto = new BackWayDto();
//            List<String> tmp = new ArrayList<>(Arrays.stream(backMsg.getContent().substring(1, backMsg.getContent().length() - 1).split(", "))
//                    .collect(Collectors.toList()));
//            List<String> tmp2 = Arrays.stream(backMsg.getContent().substring(1, backMsg.getContent().length() - 1).split(", "))
//                    .collect(Collectors.toList());
//            backWayDto.setWayScircit(backMsg.getContent());
//            backWayDto.setBackWay(tmp2);
//            log.info("Dto " + backWayDto.getBackWay());
//            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
//            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
//            Gson gson = new Gson();
//            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
//            backway1.addReceiver(new AID(tmp.get(tmp.size()-2), false));
//            backway1.setContent(gson.toJson(backWayDto));
//            log.info("Послали "+ backway1 );
//            getAgent().send(backway1);
        }
        if (backMsg1!=null) {
            log.info("Проходим дальше " + backMsg1.getContent());
//            Gson gson = new Gson();
//            BackWayDto backWayDto = gson.fromJson(backMsg1.getContent(), BackWayDto.class);
//            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
//            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
//            backway1.addReceiver(new AID(backWayDto.getBackWay().get(backWayDto.getBackWay().size()-1), false));
//            backway1.setContent(gson.toJson(backWayDto));
//            log.info("На следующую итерацию "+ backway1 );
//            if (backWayDto.getBackWay().size() > 1) {
//                getAgent().send(backway1);
//            } else {
//                ACLMessage toInitiateMsg = new ACLMessage(ACLMessage.REQUEST);
//            }
        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
