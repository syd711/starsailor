package com.starsailor.actors.states.player;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.BlendedSteering;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.steer.behaviors.LookWhereYouAreGoing;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.actors.GameEntity;
import com.starsailor.actors.Player;
import com.starsailor.components.BodyComponent;
import com.starsailor.components.ComponentFactory;
import com.starsailor.components.SteerableComponent;
import com.starsailor.data.SteeringData;
import com.starsailor.managers.SteeringManager;
import com.starsailor.util.box2d.Box2dUtil;

/**
 * State used for navigating the player
 */
public class FollowClickState implements State<Player> {
  private ClickTarget clickTarget;

  @Override
  public void enter(Player player) {
    if(clickTarget == null) {
      clickTarget = new ClickTarget(player.targetCoordinates);
      SteeringManager.setFollowClickTargetSteering(player.steerableComponent, clickTarget.steerableComponent);
    }
    else {
      clickTarget.update(player.targetCoordinates);
    }
  }


  @Override
  public void update(Player entity) {

  }

  @Override
  public void exit(Player entity) {

  }

  @Override
  public boolean onMessage(Player entity, Telegram telegram) {
    return false;
  }


  public class ClickTarget extends GameEntity {
    BodyComponent bodyComponent;
    SteerableComponent steerableComponent;

    ClickTarget(Vector2 worldCoordinates) {
      bodyComponent = ComponentFactory.addBodyComponent(this, Box2dUtil.clickBody(worldCoordinates));
      steerableComponent = ComponentFactory.addSteerableComponent(this, bodyComponent.body, new SteeringData());
      ComponentFactory.addPlayerTargetCollisionComponent(this);
    }

    void update(Vector2 worldCoordinates) {
      bodyComponent.setWorldPosition(worldCoordinates);
    }
  }
}
