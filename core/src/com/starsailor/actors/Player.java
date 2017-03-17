package com.starsailor.actors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.bullets.Bullet;
import com.starsailor.actors.states.npc.BattleState;
import com.starsailor.actors.states.player.PlayerState;
import com.starsailor.components.ComponentFactory;
import com.starsailor.components.ScreenPositionComponent;
import com.starsailor.managers.EntityManager;
import com.starsailor.managers.UIManager;
import com.starsailor.model.items.ShipItem;
import com.starsailor.util.GraphicsUtil;

import java.util.Collections;
import java.util.List;

/**
 * The player with all ashley components.
 */
public class Player extends Ship implements IFormationOwner<Ship> {
  private static Player instance = null;

  public Entity target;
  public Vector2 targetCoordinates;

  private boolean inBattleState = false;

  public static Player getInstance() {
    return instance;
  }

  public Player(ShipItem shipItem, Vector2 position) {
    super(shipItem, position);
    instance = this;
  }

  @Override
  public void createComponents() {
    super.createComponents();
    ComponentFactory.addPlayerCollisionComponent(this);

    //position player
    Vector2 screenCenter = GraphicsUtil.getScreenCenter(getHeight());
    add(new ScreenPositionComponent(screenCenter.x, screenCenter.y));
    shipBodyComponent.setWorldPosition(screenCenter);
  }

  @Override
  protected State getDefaultState() {
    return null;
  }

  @Override
  protected BattleState getBattleState() {
    return null;
  }

  @Override
  public void switchToBattleState(Ship enemy) {
    inBattleState = true;
    shieldSpineComponent.setEnabled(true);
    UIManager.getInstance().getHudStage().getWeaponsPanel().activate();
  }

  @Override
  public void switchToDefaultState() {
    inBattleState = false;
    shieldSpineComponent.setEnabled(false);
    UIManager.getInstance().getHudStage().getWeaponsPanel().deactivate();
  }

  @Override
  public boolean isInBattleState() {
    return inBattleState;
  }

  @Override
  public void applyDamageFor(Bullet bullet) {
    updateDamage(bullet);
//    if(SelectionManager.getInstance().getSelection() == null) {
//      SelectionManager.getInstance().setSelection((Selectable) bullet.owner);
//    }
  }

  /**
   * Moves to the given world coordinates
   * @param worldCoordinates the coordinates the user has clicked at
   */
  public void moveTo(Vector2 worldCoordinates) {
    targetCoordinates = worldCoordinates;
    target = EntityManager.getInstance().getEntityAt(worldCoordinates);
    getStateMachine().changeState(PlayerState.FOLLOW_CLICK);
    steerableComponent.setEnabled(true);
  }

  // IFormation owner interface implementation --------------------------------------

  @Override
  public List<Ship> getMembers() {
    return Collections.emptyList();
  }

  @Override
  public void addMember(Ship ship) {
    //TODO
  }

  @Override
  public void removeMember(Ship member) {
    //TODO
  }

  @Override
  public float getMaxMemberDistance() {
    return 0;
  }
}
