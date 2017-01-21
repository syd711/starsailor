package com.starsailor.actors;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.starsailor.components.*;
import com.starsailor.data.WeaponProfile;
import com.starsailor.managers.EntityManager;

/**
 * Entity for bullets
 */
public class Bullet extends GameEntity {
  public SpriteComponent spriteComponent;
  public PositionComponent positionComponent;
  public BulletDamageComponent bulletDamageComponent;
  public SteerableComponent steerableComponent;
  public BodyComponent bodyComponent;

  public WeaponProfile weaponProfile;
  public Entity owner;
  public Ship target;

  public Bullet(WeaponProfile weaponProfile, Ship owner, Ship target) {
    this.weaponProfile = weaponProfile;
    this.owner = owner;
    this.target = target;

    spriteComponent = ComponentFactory.addSpriteComponent(this, weaponProfile.name);
    positionComponent = ComponentFactory.addPositionComponent(this);
    positionComponent.setPosition(owner.getCenter());
    bulletDamageComponent = ComponentFactory.addBulletDamageComponent(this, 10);
    bodyComponent = ComponentFactory.addBulletBodyComponent(this, owner.getCenter(), owner instanceof Player);

    if(weaponProfile.steeringData != null) {
      steerableComponent = ComponentFactory.addSteerableComponent(this, bodyComponent.body, weaponProfile.steeringData);
    }

    ComponentFactory.addBulletCollisionComponent(this);
    Gdx.app.log(getClass().getName(), owner + " is firing " + this + " at " + target);
  }

  public boolean is(String weaponType) {
    return weaponProfile.name.equalsIgnoreCase(weaponType);
  }

  public boolean isOwner(Entity entity) {
    return owner.equals(entity);
  }

  public void applyCollisionWith(Ship npc) {
    BodyComponent component = npc.getComponent(BodyComponent.class);

    //use my linear velocity
    Vector2 linearVelocity = bodyComponent.body.getLinearVelocity();
    float impactFactor = weaponProfile.impactFactor;
    Vector2 force = new Vector2(linearVelocity.x*impactFactor, linearVelocity.y*impactFactor);
    //to apply it on the target
    component.body.applyForceToCenter(force.x, force.y, true);
    if(!isOwner(npc)) {
      EntityManager.getInstance().destroy(this);
    }
  }
}
