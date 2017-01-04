package com.nima.actors;

import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.steer.behaviors.Face;
import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.nima.components.SteerableComponent;
import com.nima.util.GraphicsUtil;
import com.nima.util.Resources;

import static com.nima.util.Settings.MPP;

/**
 * A merchant spine
 */
public class Hunter extends NPC {

  public Hunter(Player player, float x, float y) {
    super(Resources.ACTOR_SPINE, Resources.ACTOR_DEFAULT_ANIMATION, 0.2f, x, y);

    Vector2 screenCenter = GraphicsUtil.getScreenCenter(getHeight());
    positionComponent.x = screenCenter.x + 360;
    positionComponent.y = screenCenter.y + 60;

    bodyComponent.body.setTransform(positionComponent.x * MPP, positionComponent.y * MPP, 0);
    bodyComponent.body.setLinearDamping(4f);

    speedComponent.setIncreaseBy(0.2f);
    speedComponent.setDecreaseBy(0.2f);

    Arrive<Vector2> arrive = new Arrive<>(steerableComponent, player.getComponent(SteerableComponent.class));
    arrive.setTimeToTarget(0.4f);
    arrive.setArrivalTolerance(3f);
    arrive.setDecelerationRadius(10);

    Face<Vector2> face = new Face(steerableComponent, player.steerableComponent);
    face.setAlignTolerance(.01f);
    face.setDecelerationRadius(MathUtils.PI);
    face.setTimeToTarget(.18f);

    Pursue pursue = new Pursue(steerableComponent, player.steerableComponent);
    pursue.setMaxPredictionTime(2f);

    steerableComponent.setBehavior(pursue);
  }
}
