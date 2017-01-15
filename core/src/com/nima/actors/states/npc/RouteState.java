package com.nima.actors.states.npc;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.math.Vector2;
import com.nima.actors.NPC;
import com.nima.components.RoutingComponent;
import com.nima.components.SteerableComponent;
import com.nima.data.RoutePoint;

/**
 * Let the give npc follow its route.
 */
public class RouteState implements State<NPC> {
  @Override
  public void enter(NPC npc) {
    SteerableComponent sourceSteering = npc.getComponent(SteerableComponent.class);
    RoutingComponent routingComponent = npc.getComponent(RoutingComponent.class);

    RoutePoint point = routingComponent.nextTarget();
    SteerableComponent targetSteering = routingComponent.getSteeringComponent(point);

    Arrive<Vector2> behaviour = new Arrive<>(sourceSteering, targetSteering);
    behaviour.setArrivalTolerance(0.10f);
    behaviour.setDecelerationRadius(1f);
    sourceSteering.setBehavior(behaviour);
    sourceSteering.setFaceBehaviour(null);
  }

  @Override
  public void update(NPC npc) {
      //return distance < 200;
  }

  @Override
  public void exit(NPC npc) {

  }

  @Override
  public boolean onMessage(NPC npc, Telegram telegram) {
    return false;
  }
}