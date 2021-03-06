package com.starsailor.editor.ui;

import com.starsailor.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class WeaponDataFormPane extends FormPane {

  private BodyData bodyData;
  private SteeringData steeringData;
  private SpineData spineData;

  public WeaponDataFormPane(MainPane mainPane) {
    super(mainPane, Arrays.asList("bodyData", "steeringData"));
  }


  private List<String> getIgnoreListForWeapon(WeaponData weaponData) {
    WeaponData.Types type = WeaponData.Types.valueOf(weaponData.getType().toUpperCase());

    switch(type) {
      case LASER: {
        return Arrays.asList("type", "sprite", "spineData", "durationMillis", "torque", "bulletDelay", "activationDistance");
      }
      case MISSILE: {
        return Arrays.asList("type", "sprite", "spineData", "durationMillis", "torque");
      }
      case PHASER: {
        return Arrays.asList("type", "sprite", "spineData", "torque", "bodyData", "steeringData", "activationDistance", "forceFactor");
      }
      case MINE: {
        return Arrays.asList("type", "sprite", "spineData", "durationMillis", "torque");
      }
      case FLARES: {
        return Arrays.asList("type", "sprite", "spineData", "category", "durationMillis", "activationDistance");
      }
    }
    return Collections.emptyList();
  }

  @Override
  public void setData(GameData gameData) throws Exception {
    super.setData(gameData);
    if(gameData == null) {
      return;
    }

    WeaponData weaponData = (WeaponData) gameData;
    boolean extendable = ((WeaponData) gameData).getParent().getId() != 30000;


    createSection(weaponData, "Bullet Data", getIgnoreListForWeapon(weaponData), false, null);

    if(weaponData.getBodyData() != null) {
      bodyData = new BodyData(weaponData.getBodyData());
      if(!weaponData.isBodyDataExtended()) {
        bodyData = weaponData.getBodyData();
      }

      createSection(bodyData, "Body Data", extendable, new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
          if(observable instanceof BooleanProperty) {
            boolean selected = (boolean) newValue;
            if(selected) {
              weaponData.setBodyData(null);
            }
            else {
              weaponData.setBodyData(bodyData);
            }
          }
        }
      });
    }

    spineData = new SpineData(weaponData.getSpineData());
    if(!weaponData.isSpineDataExtended()) {
      spineData = weaponData.getSpineData();
    }

    createSection(spineData, "Spine Data", extendable, new ChangeListener() {
      @Override
      public void changed(ObservableValue observable, Object oldValue, Object newValue) {
        if(observable instanceof BooleanProperty) {
          boolean selected = (boolean) newValue;
          if(selected) {
            weaponData.setSpineData(null);
          }
          else {
            weaponData.setSpineData(spineData);
          }
        }
      }
    });

    if(weaponData.getSteeringData() != null) {
      steeringData = new SteeringData(weaponData.getSteeringData());
      if(!weaponData.isSteeringDataExtended()) {
        steeringData = weaponData.getSteeringData();
      }

      createSection(steeringData, "Steering Data", extendable, new ChangeListener() {
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
          if(observable instanceof BooleanProperty) {
            boolean selected = (boolean) newValue;
            if(selected) {
              weaponData.setSteeringData(null);
            }
            else {
              weaponData.setSteeringData(steeringData);
            }
          }
        }
      });
    }
  }
}
