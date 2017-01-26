package com.starsailor.data;

/**
 * Contains all data of a weapon
 */
public class WeaponProfile {
  public enum Types {
    LASER, MISSILE, PHASER, MINE, FLARES
  }

  public Types type;
  public float rechargeTimeMillis;
  public float durationMillis; //for constant shooting
  public float damage;
  public float forceFactor;
  public float torque;
  public float impactFactor;
  public String name;

  //custom fields depending on the weapon
  public float activationDistance;

  //steering if available
  public SteeringData steeringData;

  //box2d data
  public BodyData bodyData;

  @Override
  public String toString() {
    return "Weapon Profile '" + name + "'";
  }
}
