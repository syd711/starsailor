package com.starsailor.managers;

import com.badlogic.gdx.ai.steer.behaviors.*;
import com.badlogic.gdx.ai.steer.limiters.AngularLimiter;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.ai.steer.limiters.LinearLimiter;
import com.badlogic.gdx.ai.steer.limiters.NullLimiter;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.steer.utils.rays.CentralRayWithWhiskersConfiguration;
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.starsailor.actors.FormationOwner;
import com.starsailor.actors.NPC;
import com.starsailor.components.RoutingComponent;
import com.starsailor.components.SteerableComponent;
import com.starsailor.util.box2d.Box2dRadiusProximity;
import com.starsailor.util.box2d.Box2dRaycastCollisionDetector;

import static com.starsailor.Game.world;
import static com.starsailor.util.Settings.MPP;

/**
 * Used to apply steering behaviours to entities
 */
public class SteeringManager {

  public static void setRouteMemberSteering(NPC npc) {
    SteerableComponent sourceSteering = npc.getComponent(SteerableComponent.class);

    Arrive<Vector2> arrive = new Arrive<>(sourceSteering, npc.getTargetLocation());
    arrive.setLimiter(new LinearLimiter(sourceSteering.getMaxLinearAcceleration(), sourceSteering.getMaxLinearSpeed()));
    arrive.setTimeToTarget(1f);
    arrive.setArrivalTolerance(0.01f);
    arrive.setDecelerationRadius(5);

    LookWhereYouAreGoing lookWhereYouAreGoingSB = new LookWhereYouAreGoing<>(sourceSteering);
    lookWhereYouAreGoingSB.setLimiter(new AngularLimiter(sourceSteering.getMaxAngularAcceleration(), sourceSteering.getMaxAngularSpeed()));
    lookWhereYouAreGoingSB.setTimeToTarget(6f);
    lookWhereYouAreGoingSB.setAlignTolerance(0.001f);
    lookWhereYouAreGoingSB.setDecelerationRadius(MathUtils.PI);

    BlendedSteering<Vector2> blendedSB = new BlendedSteering<Vector2>(sourceSteering);
    blendedSB.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSB.add(arrive, 1f);
    blendedSB.add(getRayCastCollider(sourceSteering), 0.8f);
    blendedSB.add(lookWhereYouAreGoingSB, 0.5f);

    npc.steerableComponent.setBehavior(blendedSB);
  }

  public static void setFormationOwnerSteering(FormationOwner formationOwner) {
    Array<Vector2> wayPoints = formationOwner.routingComponent.getWayPoints(formationOwner.bodyComponent.body.getPosition());

    LinePath<Vector2> linePath = new LinePath<>(wayPoints, false);
    FollowPath followPathSB = new FollowPath<>(formationOwner.steerableComponent, linePath, 1);
    followPathSB.setTimeToTarget(0.1f);
    followPathSB.setArrivalTolerance(0.01f);
    followPathSB.setDecelerationRadius(5);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<Vector2>(formationOwner.steerableComponent);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(followPathSB, 1f);

    formationOwner.steerableComponent.setBehavior(followPathSB);
  }

  public static void setRouteSteering(SteerableComponent sourceSteering, RoutingComponent routingComponent, Vector2 origin) {
    Array<Vector2> wayPoints = routingComponent.getWayPoints(origin);

    LinePath<Vector2> linePath = new LinePath<>(wayPoints, false);
    FollowPath followPathSB = new FollowPath<>(sourceSteering, linePath, 1);
    followPathSB.setTimeToTarget(0.1f);
    followPathSB.setArrivalTolerance(0.01f);
    followPathSB.setDecelerationRadius(5);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<Vector2>(sourceSteering);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(followPathSB, 1f);
    blendedSteering.add(getRayCastCollider(sourceSteering), 1f);
    blendedSteering.add(getCollisionAvoidance(sourceSteering), 0.2f);
    blendedSteering.add(getLookWhereYouAreGoing(sourceSteering), 0.1f);

    sourceSteering.setBehavior(blendedSteering);
  }

  public static void setAttackSteering(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    //we use a smaller collision avoidance here!
    Box2dRadiusProximity proximity = new Box2dRadiusProximity(sourceSteering, world, sourceSteering.getBoundingRadius() * MPP);
    CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(sourceSteering, proximity);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<>(sourceSteering);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(getFace(sourceSteering, targetSteering), 0.5f);
    blendedSteering.add(collisionAvoidanceSB, 0.8f);
    blendedSteering.add(getArrive(sourceSteering, targetSteering), 0.5f);

    sourceSteering.setBehavior(blendedSteering);
  }

  public static Wander<Vector2> setWanderSteering(NPC npc) {
    SteerableComponent sourceSteering = npc.getComponent(SteerableComponent.class);
    npc.steerableComponent.setIndependetFacing(false);

    Wander<Vector2> wanderSB = new Wander<>(sourceSteering);
    wanderSB.setLimiter(new LinearAccelerationLimiter(sourceSteering.getMaxLinearAcceleration()));
    wanderSB.setFaceEnabled(false);
    wanderSB.setAlignTolerance(0.001f);
    wanderSB.setDecelerationRadius(1);
    wanderSB.setTimeToTarget(0.1f);
    wanderSB.setWanderOffset(3);
    wanderSB.setWanderOrientation(3);
    wanderSB.setWanderRadius(0.5f);
    wanderSB.setWanderRate(MathUtils.PI2 * 4);

    sourceSteering.setBehavior(wanderSB);

    return wanderSB;
  }

  public static void setFaceSteering(SteerableComponent sourceSteering, SteerableComponent targetSteering, float boundingRadius) {
    Box2dRadiusProximity proximity = new Box2dRadiusProximity(sourceSteering, world, boundingRadius * MPP);
    CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(sourceSteering, proximity);

    final Face<Vector2> faceSB = new Face<>(sourceSteering, targetSteering);
    faceSB.setTimeToTarget(0.01f);
    faceSB.setAlignTolerance(0.0001f);
    faceSB.setDecelerationRadius(MathUtils.degreesToRadians * 120);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<>(sourceSteering);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(faceSB, 0.5f);
    blendedSteering.add(collisionAvoidanceSB, 1f);

    sourceSteering.setBehavior(blendedSteering);
  }

  public static void setMissileSteering(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    final Face<Vector2> faceSB = new Face<>(sourceSteering, targetSteering);
    faceSB.setTimeToTarget(100f);
    faceSB.setAlignTolerance(0.001f);
    faceSB.setDecelerationRadius(MathUtils.degreesToRadians * 120);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<>(sourceSteering);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(faceSB, 0.4f);

    sourceSteering.setBehavior(blendedSteering);
  }

  public static void setFleeSteering(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<>(sourceSteering);
    blendedSteering.setLimiter(NullLimiter.NEUTRAL_LIMITER);
    blendedSteering.add(getFlee(sourceSteering, targetSteering), 0.5f);
    blendedSteering.add(getFace(sourceSteering, targetSteering), 0.5f);

    sourceSteering.setBehavior(blendedSteering);
  }

  public static void setFollowTargetSteering(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    Arrive<Vector2> arriveSB = new Arrive<>(sourceSteering, targetSteering);

    Face<Vector2> faceSB = new Face<>(sourceSteering, targetSteering);
    faceSB.setTimeToTarget(0.1f);
    faceSB.setAlignTolerance(0.001f);
    faceSB.setDecelerationRadius(MathUtils.degreesToRadians * 180);

    BlendedSteering<Vector2> blendedSteering = new BlendedSteering<>(sourceSteering);
    blendedSteering.add(arriveSB, 1f);
    blendedSteering.add(getLookWhereYouAreGoing(sourceSteering), 1f);

    sourceSteering.setBehavior(blendedSteering);
  }


  //------------------ Helper -------------------------------------


  private static Arrive<Vector2> getArrive(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    Arrive<Vector2> arriveSB = new Arrive<>(sourceSteering, targetSteering);
    arriveSB.setLimiter(new LinearLimiter(sourceSteering.getMaxLinearAcceleration(), sourceSteering.getMaxLinearSpeed()));
    arriveSB.setTimeToTarget(0.1f);
    arriveSB.setArrivalTolerance(0.2f);
    arriveSB.setDecelerationRadius(10);
    return arriveSB;
  }

  private static Flee<Vector2> getFlee(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    Flee<Vector2> fleeSB = new Flee<>(sourceSteering, targetSteering);
    fleeSB.setLimiter(new LinearLimiter(sourceSteering.getMaxLinearAcceleration(), sourceSteering.getMaxLinearSpeed()));
    return fleeSB;
  }

  private static RaycastObstacleAvoidance<Vector2> getRayCastCollider(SteerableComponent sourceSteering) {
    CentralRayWithWhiskersConfiguration<Vector2> rayConfiguration = new CentralRayWithWhiskersConfiguration<>(sourceSteering, 300 * MPP,
        100 * MPP, 55 * MathUtils.degreesToRadians);
    RaycastCollisionDetector<Vector2> raycastCollisionDetector = new Box2dRaycastCollisionDetector(sourceSteering.getBody());
    RaycastObstacleAvoidance<Vector2> raycastObstacleAvoidanceSB = new RaycastObstacleAvoidance<>(sourceSteering, rayConfiguration,
        raycastCollisionDetector, 1000 * MPP);

    return raycastObstacleAvoidanceSB;
  }

  private static Face<Vector2> getFace(SteerableComponent sourceSteering, SteerableComponent targetSteering) {
    Face<Vector2> faceSB = new Face<>(sourceSteering, targetSteering);
    faceSB.setTimeToTarget(10f);
    faceSB.setAlignTolerance(0.001f);
    faceSB.setDecelerationRadius(MathUtils.degreesToRadians * 180);
    return faceSB;
  }

  private static CollisionAvoidance<Vector2> getCollisionAvoidance(SteerableComponent sourceSteering) {
    Box2dRadiusProximity proximity = new Box2dRadiusProximity(sourceSteering, world, sourceSteering.getBoundingRadius() * MPP);
    CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(sourceSteering, proximity);
    return collisionAvoidanceSB;
  }

  private static LookWhereYouAreGoing<Vector2> getLookWhereYouAreGoing(SteerableComponent sourceSteering) {
    LookWhereYouAreGoing<Vector2> lookWhereYouAreGoingSB = new LookWhereYouAreGoing<>(sourceSteering);
    lookWhereYouAreGoingSB.setTimeToTarget(0.1f);
    lookWhereYouAreGoingSB.setAlignTolerance(0.01f);
    lookWhereYouAreGoingSB.setDecelerationRadius(MathUtils.PI);
    return lookWhereYouAreGoingSB;
  }
}
