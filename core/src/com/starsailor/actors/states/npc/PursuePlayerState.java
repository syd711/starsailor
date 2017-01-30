package com.starsailor.actors.states.npc;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.NPC;
import com.starsailor.actors.Player;
import com.starsailor.components.SteerableComponent;

/**
 *
 */
public class PursuePlayerState implements State<NPC> {
  @Override
  public void enter(NPC npc) {
    SteerableComponent steerableComponent = npc.getComponent(SteerableComponent.class);
    Pursue<Vector2> behaviour = new Pursue<>(steerableComponent, Player.getInstance().getComponent(SteerableComponent.class));
    behaviour.setMaxPredictionTime(0.7f);
    steerableComponent.setBehavior(behaviour);
  }

  @Override
  public void update(NPC npc) {
    float distanceToPlayer = npc.getDistanceToPlayer();
    //check if in shooting distance
    if(distanceToPlayer > npc.shipProfile.attackDistance) {
      npc.getStateMachine().changeState(new RouteState());
    }
    if(distanceToPlayer < npc.shipProfile.shootDistance) {
      npc.getStateMachine().changeState(NPCStates.FACE_PLAYER);
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
