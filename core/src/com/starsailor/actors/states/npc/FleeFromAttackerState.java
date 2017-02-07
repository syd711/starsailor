package com.starsailor.actors.states.npc;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.starsailor.actors.NPC;
import com.starsailor.managers.SteeringManager;

/**
 * Let the give npc follow its route.
 */
public class FleeFromAttackerState implements State<NPC> {
  @Override
  public void enter(NPC npc) {
    SteeringManager.setFleeSteering(npc, npc.attacking);
  }

  @Override
  public void update(NPC npc) {
    //check if the attacker is destroyed, return to default state then
    if(npc.attacking == null) {
      npc.getStateMachine().changeState(npc.getDefaultState());
      return;
    }

    float distanceTo = npc.getDistanceTo(npc.attacking);
    if(distanceTo > 1600) {
      npc.steerableComponent.setBehavior(null);
    }
    else {
      if(npc.steerableComponent.getBehavior() == null) {
        SteeringManager.setFleeSteering(npc, npc.attacking);
      }
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
