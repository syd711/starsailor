package com.starsailor.actors;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.states.npc.NPCStates;
import com.starsailor.components.ComponentFactory;
import com.starsailor.components.RoutingComponent;
import com.starsailor.components.SelectionComponent;
import com.starsailor.components.collision.NPCCollisionComponent;
import com.starsailor.data.ShipProfile;

/**
 * Common superclass for all NPC.
 * We assume that they are instances of Spine.
 */
public class NPC extends Ship implements Selectable {
  public RoutingComponent routingComponent;
  public SelectionComponent selectionComponent;
  public NPCCollisionComponent collisionComponent;

  private State<NPC> defaultState;

  //not necessarily set
  private Route route;

  public NPC(ShipProfile profile, State<NPC> defaultState, Vector2 position) {
    super(profile, position);
    this.shipProfile = profile;
    this.defaultState = defaultState;
  }

  /**
   * Constructor used for ships that guard a routing ship
   * @param shipProfile
   * @param formationOwner
   */
  public NPC(ShipProfile shipProfile, NPC formationOwner, State<NPC> defaultState, Vector2 position) {
    this(shipProfile, defaultState, position);
    this.formationOwner = formationOwner;
  }

  /**
   * Constructor used for ships that have been spawned from a route point
   * @param shipProfile
   * @param route
   */
  public NPC(ShipProfile shipProfile, Route route, State<NPC> defaultState, Vector2 position) {
    this(shipProfile, defaultState, position);
    this.route = route;
    this.route.npc = this;
  }

  @Override
  public void createComponents(ShipProfile profile, Fraction fraction) {
    super.createComponents(profile, fraction);
    collisionComponent = ComponentFactory.addNPCCollisionComponent(this);
    selectionComponent = ComponentFactory.addSelectionComponent(this);

    if(this.route != null) {
      routingComponent = ComponentFactory.addRoutingComponent(this, route);
    }

    getStateMachine().setInitialState(NPCStates.IDLE);
  }

  @Override
  public State getDefaultState() {
    return defaultState;
  }

  @Override
  public SelectionComponent getSelectionComponent() {
    return selectionComponent;
  }

  @Override
  public void setSelected(boolean b) {
    selectionComponent.setSelected(b);
    spriteComponent.setEnabled(b);
  }

  @Override
  public boolean isSelected() {
    return selectionComponent.isSelected();
  }
}
