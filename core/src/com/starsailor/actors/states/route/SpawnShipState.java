package com.starsailor.actors.states.route;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.NPC;
import com.starsailor.actors.Route;
import com.starsailor.actors.states.NPCStates;
import com.starsailor.components.RouteComponent;
import com.starsailor.data.ShipProfile;
import com.starsailor.managers.EntityManager;

/**
 *
 */
public class SpawnShipState implements State<Route> {
  @Override
  public void enter(Route route) {
    RouteComponent routeComponent = route.routeComponent;
    ShipProfile shipProfile = routeComponent.shipProfile;
    NPC npc = new NPC(shipProfile, route, NPCStates.IDLE);
    EntityManager.getInstance().add(npc);
    Vector2 spawnPoint = routeComponent.spawnPoint.position;
    Gdx.app.log(getClass().getName(), "Route '" + routeComponent.name + "': spawned ship " + routeComponent.shipProfile + " at " + spawnPoint);
    npc.getStateMachine().changeState(NPCStates.ROUTE);
  }

  @Override
  public void update(Route entity) {

  }

  @Override
  public void exit(Route entity) {

  }

  @Override
  public boolean onMessage(Route entity, Telegram telegram) {
    return false;
  }
}