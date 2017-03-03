package com.starsailor.actors.states;

import com.badlogic.gdx.ai.fsm.State;
import com.starsailor.actors.NPC;
import com.starsailor.actors.Steering;
import com.starsailor.actors.states.npc.*;

/**
 *
 */
public class StateFactory {

  public static State<NPC> createState(Steering steering) {
    switch(steering) {
      case SEEK_AND_DESTROY: {
        return new RoutedSeekAndDestroyState();
      }
      case GUARD: {
        return new GuardState();
      }
      case ROUTE: {
        return new GuardState();
      }
      case ATTACK: {
        return new AttackState();
      }
      case FLEE: {
        return new FleeFromAttackerAndWaitState();
      }
      default: {
        return new RouteState();
      }
    }
  }
}
