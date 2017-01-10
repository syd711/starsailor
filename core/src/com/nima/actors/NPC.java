package com.nima.actors;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.math.Vector2;
import com.nima.components.RoutingComponent;
import com.nima.components.ShootingComponent;
import com.nima.data.RouteProfile;
import com.nima.managers.EntityManager;
import com.nima.systems.states.AttackState;
import com.nima.util.Box2dUtil;
import com.nima.util.GraphicsUtil;

import static com.nima.util.Settings.MPP;

/**
 * Common superclass for all NPC.
 * We assume that they are instances of Spine.
 */
public class NPC extends Spine {

  public StateMachine<NPC, AttackState> attackStateMachine;

  public RoutingComponent routingComponent;

  public NPC(Player player, String path, String defaultAnimation, float jsonScaling, float x, float y) {
    super(path, defaultAnimation, jsonScaling, x, y);

    Vector2 screenCenter = GraphicsUtil.getScreenCenter(getHeight());
    positionComponent.x = screenCenter.x + 360;
    positionComponent.y = screenCenter.y + 60;

    bodyComponent.body.setTransform(positionComponent.x * MPP, positionComponent.y * MPP, 0);
    bodyComponent.body.setLinearDamping(4f);

    speedComponent.setIncreaseBy(0.2f);
    speedComponent.setDecreaseBy(0.2f);


    add(new ShootingComponent());

    this.attackStateMachine = new DefaultStateMachine<>(this, AttackState.SLEEP);
  }

  //routing npc
  public NPC(RouteProfile route, String path, String defaultAnimation, float jsonScaling, float x, float y) {
    super(path, defaultAnimation, jsonScaling, x, y);

    routingComponent = new RoutingComponent();
    Vector2 startPoint = routingComponent.applyRoute(route);
    add(routingComponent);

    float angle = GraphicsUtil.getAngle(positionComponent.getPosition(), routingComponent.target);
    rotationComponent.setRotationTarget(angle);

    bodyComponent.body.setTransform(startPoint.x * MPP, startPoint.y * MPP, (float) Math.toRadians(angle));
//    bodyComponent.body.setLinearDamping(4f);

    speedComponent.setIncreaseBy(0.2f);
    speedComponent.setDecreaseBy(0.2f);

    EntityManager.getInstance().addUpdateable(this);
  }

  @Override
  public void update() {
    super.update();
    if(this.attackStateMachine != null) {
      this.attackStateMachine.update();
    }

    if(routingComponent != null) {

      if(scalingComponent.isChanging()) {
        return;
      }
      //TODO build state machine
      float currentAngle = GraphicsUtil.getAngle(positionComponent.getPosition(), routingComponent.target);
      Vector2 delta = GraphicsUtil.getDelta(currentAngle, 2f);
      Vector2 box2dDelta = Box2dUtil.toBox2Vector(delta);
      float x = box2dDelta.x;
      float y = box2dDelta.y;
      Vector2 position = bodyComponent.body.getPosition();

      if(currentAngle >= 0 && currentAngle <= 90) {
        position.x = position.x + x;
        position.y = position.y + y;
      }
      else if(currentAngle > 90 && currentAngle <= 180) {
        position.x = position.x - x;
        position.y = position.y + y;
      }
      else if(currentAngle < 0 && currentAngle >= -90) {
        position.x = position.x + x;
        position.y = position.y - y;
      }
      else if(currentAngle < -90 && currentAngle >= -180) {
        position.x = position.x - x;
        position.y = position.y - y;
      }

      bodyComponent.body.setTransform(position, bodyComponent.body.getAngle());
    }
  }
}
