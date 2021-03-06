package com.starsailor.systems;

import com.badlogic.ashley.core.Family;
import com.starsailor.actors.GameEntity;
import com.starsailor.actors.bullets.Bullet;
import com.starsailor.components.BulletDamageComponent;

public class BulletSystem extends PauseableIteratingSystem {
  public BulletSystem() {
    super(Family.all(BulletDamageComponent.class).get());
  }

  public void process(GameEntity entity, float deltaTime) {
    Bullet bullet = (Bullet) entity;
    bullet.update();
  }
}