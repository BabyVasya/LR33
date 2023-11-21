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
            List<String> tmp = new ArrayList<>(Arrays.stream(backMsg.getContent().substring(1, backMsg.getContent().length() - 1).split(", "))
                    .collect(Collectors.toList()));
            List<String> tmp2 = Arrays.stream(backMsg.getContent().substring(1, backMsg.getContent().length() - 1).split(", "))
                    .collect(Collectors.toList());
            backWayDto.setBackWay(tmp2);
            log.info("Dto " + backWayDto.getBackWay());
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            Gson gson = new Gson();
            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
            backway1.addReceiver(new AID(tmp.get(tmp.size()-2), false));
            backway1.setContent(String.valueOf(backWayDto.getBackWay()));
            log.info("Послали "+ backway1 );
            getAgent().send(backway1);
        }
        if (backMsg1!=null) {
            log.info("Проходим дальше " + backMsg1.getContent());
            BackWayDto backWayDto = new BackWayDto();
            List<String> tmp2 = Arrays.stream(backMsg1.getContent().substring(1, backMsg1.getContent().length() - 1).split(", "))
                    .collect(Collectors.toList());
            log.info("Dto1 " + tmp2.get(tmp2.size()-1) + tmp2.size());
            backWayDto.setBackWay(tmp2);
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size()-1);
            Gson gson = new Gson();
            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
            backway1.addReceiver(new AID(tmp2.get(tmp2.size()-1), false));
            backway1.setContent(String.valueOf(backWayDto.getBackWay()));
            log.info("На следующую итерацию "+ backway1 );
            if (backWayDto.getBackWay().size() > 1) {
                getAgent().send(backway1);
            } else {
                ACLMessage toInitiatorMsg = new ACLMessage(ACLMessage.AGREE);
                toInitiatorMsg.addReceiver();
            }

        }

    }

    @Override
    public boolean done() {
        return false;
    }
}
