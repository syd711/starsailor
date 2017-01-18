package com.nima.actors.states.npc;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.nima.actors.NPC;
import com.nima.actors.states.NPCStates;
import com.nima.components.SteerableComponent;
import com.nima.systems.behaviours.FaceToPlayerBehaviour;

/**
 *
 */
public class FacePlayerState implements State<NPC> {
  @Override
  public void enter(NPC npc) {
    SteerableComponent steerableComponent = npc.getComponent(SteerableComponent.class);
    FaceToPlayerBehaviour face = new FaceToPlayerBehaviour(npc);
    steerableComponent.setBehavior(null);
    steerableComponent.setFaceBehaviour(face);
  }

  @Override
  public void update(NPC npc) {
    float distanceToPlayer = npc.distanceToPlayer();
    //check if in shooting distance
    if(distanceToPlayer > npc.shipProfile.shootDistance) {
      npc.getStateMachine().changeState(NPCStates.PURSUE_PLAYER);
    }
    if(distanceToPlayer < npc.shipProfile.evadeDistance) {
      npc.getStateMachine().changeState(NPCStates.EVADE_PLAYER);
    }
  }

  @Override
  public void exit(NPC npc) {

  }

  @Override
  public boolean onMessage(NPC npc, Telegram telegram) {
    return false;
  }
}
