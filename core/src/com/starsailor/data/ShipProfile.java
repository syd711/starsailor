package com.starsailor.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all attributes a ship can have.
 */
public class ShipProfile {
  public String spine;
  public float scale;
  public String defaultAnimation;

  //Behaviour attributes
  public float attackDistance;
  public float shootDistance;
  public float evadeDistance;

  public float health;

  public BodyData bodyData;

  //weapons
  public List<String> weapons = new ArrayList<>();
  public List<WeaponProfile> weaponProfiles = new ArrayList<>();

  //shield
  public String shield;
  public ShieldProfile shieldProfile;

  //Steering
  public SteeringData steeringData;

  public void addWeaponProfile(WeaponProfile profile) {
    if(weaponProfiles == null) {
      weaponProfiles = new ArrayList<>();
    }
    weaponProfiles.add(profile);
  }

  @Override
  public String toString() {
    return "Ship Profile '" + spine + "'";
  }
}
