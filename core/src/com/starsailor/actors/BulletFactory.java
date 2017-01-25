package com.starsailor.actors;

import com.badlogic.gdx.ai.steer.behaviors.Pursue;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.starsailor.components.BodyComponent;
import com.starsailor.components.ComponentFactory;
import com.starsailor.data.WeaponProfile;
import com.starsailor.managers.*;
import com.starsailor.systems.behaviours.FaceBehaviourImpl;
import com.starsailor.util.Box2dUtil;
import com.starsailor.util.Resources;
import com.starsailor.util.Settings;

/**
 *
 */
public class BulletFactory {

  public static void fireBullet(Ship owner, Ship target) {
    WeaponProfile weaponProfile = owner.shootingComponent.getActiveWeaponProfile();
    Bullet bullet = new Bullet(weaponProfile, owner, target);
    fireBullet(bullet);
    EntityManager.getInstance().add(bullet);
  }

  private static void fireBullet(Bullet bullet) {
    WeaponProfile weaponProfile = bullet.weaponProfile;
    BodyComponent bodyComponent = bullet.bodyComponent;

    if(weaponProfile.type.equals(WeaponProfile.Types.LASER)) {
      Vector2 from = Box2dUtil.toBox2Vector(bullet.owner.getCenter());
      Vector2 to = Box2dUtil.toBox2Vector(bullet.target.getCenter());
      float radianAngle = Box2dUtil.getBox2dAngle(from, to);

      Body bulletBody = bodyComponent.body;
      bulletBody.setTransform(bulletBody.getPosition().x, bulletBody.getPosition().y, radianAngle);

      float mXDir = -(float) Math.cos(radianAngle);
      float mYDir = -(float) Math.sin(radianAngle);

      float speedFactor = weaponProfile.forceFactor;
      Vector2 impulse = new Vector2(speedFactor * mXDir / Settings.PPM, speedFactor * mYDir / Settings.PPM);
      bulletBody.applyLinearImpulse(impulse, bulletBody.getPosition(), true);

      SoundManager.playSoundAtPosition(Resources.SOUND_LASER, 0.5f, new Vector3(bullet.owner.getCenter().x, bullet.owner.getCenter().y, 0));
    }
    else if(weaponProfile.type.equals(WeaponProfile.Types.MISSILE)) {
      //add dependency tracking for the target
      EntityManager.getInstance().addEntityListener(bullet);

      //configure steerable of the missile
      bullet.steerableComponent = ComponentFactory.addSteerableComponent(bullet, bodyComponent.body, weaponProfile.steeringData);
      Pursue<Vector2> behaviour = new Pursue<>(bullet.steerableComponent, bullet.target.steerableComponent);
      behaviour.setMaxPredictionTime(0f);
      bullet.steerableComponent.setBehavior(behaviour);
      bullet.steerableComponent.setFaceBehaviour(new FaceBehaviourImpl(bodyComponent.body, bullet.target.bodyComponent.body, 3f));
      bullet.steerableComponent.setEnabled(false);

      //apply initial force to the missile
      Body bulletBody = bodyComponent.body;
      Body ownerBody = bullet.owner.bodyComponent.body;
      bulletBody.setTransform(bulletBody.getPosition(), ownerBody.getAngle());

      float angle = ownerBody.getAngle();
      angle = Box2dUtil.addDegree(angle, 90);
      Vector2 force = new Vector2();
      force.x = (float) Math.cos(angle);
      force.y = (float) Math.sin(angle);
      force = force.scl(weaponProfile.forceFactor);

      bulletBody.applyForceToCenter(force, true);
    }
    else if(weaponProfile.type.equals(WeaponProfile.Types.PHASER)) {
      bullet.spriteComponent.getSprite(Textures.PHASER).setTexture(true);
      ParticleManager.getInstance().playEffect(Particles.PHASER, bullet.target.positionComponent, 0.3f);
    }

    bullet.owner.shootingComponent.updateLastBulletTime();
  }
}
