package com.starsailor.actors.states.npc;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.*;
import com.starsailor.data.ShipProfile;
import com.starsailor.managers.EntityManager;

/**
 * Handling the initialization problem of npc
 */
public class NPCFactory {

  public static RoutedNPC createRoutedNPC(ShipProfile shipProfile, Route route, State state, Behaviours behaviour) {
    RoutedNPC npc = new RoutedNPC(shipProfile, route, behaviour);
    npc.createComponents(shipProfile);
    npc.getStateMachine().changeState(state);

    EntityManager.getInstance().add(npc);
    return npc;
  }

  public static GuardingNPC createGuardingNPC(ShipProfile shipProfile, NPC guardedNPC, Behaviours behaviour, Vector2 position) {
    GuardingNPC npc = new GuardingNPC(shipProfile, guardedNPC, behaviour, position);
    npc.createComponents(shipProfile);
    npc.getStateMachine().changeState(new GuardState());

    EntityManager.getInstance().add(npc);
    return npc;
  }
}
