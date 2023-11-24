package org.example;

import com.google.gson.Gson;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BactoToBeh extends Behaviour {
    @Override
    public void action() {
        ACLMessage backMsg1 = getAgent().receive(MessageTemplate.MatchPerformative(ACLMessage.PROPOSE));
        if (backMsg1!=null) {
//            Проход по полученному пути в обратную сторону
            backWayGoing(backMsg1);
        }else {
            block();
        }
    }

    private void backWayGoing(ACLMessage backMsg1) {
        log.info("Обратный путь" + backMsg1);
        Gson gson = new Gson();
        BackWayDto backWayDto = gson.fromJson(backMsg1.getContent(), BackWayDto.class);
        if (backWayDto.getBackWay().size() > 1) {
            backWayDto.getBackWay().remove(backWayDto.getBackWay().size() - 1);
            ACLMessage backway1 = new ACLMessage(ACLMessage.PROPOSE);
            backway1.addReceiver(new AID(backWayDto.getBackWay().get(backWayDto.getBackWay().size() - 1), false));
            backway1.setContent(gson.toJson(backWayDto));
            getAgent().send(backway1);
        }
        else {
            ACLMessage toInitiateMsg = new ACLMessage(ACLMessage.REQUEST);
            toInitiateMsg.addReceiver(new AID(backWayDto.getBackWay().get(0), false));
            toInitiateMsg.setContent(gson.toJson(backWayDto));
            log.info("Пересылка к инициатору " + toInitiateMsg);
            getAgent().send(toInitiateMsg);
        }
    }

    @Override
    public boolean done() {
        return false;
    }
}
