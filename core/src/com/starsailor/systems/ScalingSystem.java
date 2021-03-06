package com.starsailor.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.starsailor.actors.GameEntity;
import com.starsailor.components.ScalingComponent;
import com.starsailor.components.SpineComponent;

import java.util.List;

public class ScalingSystem extends PauseableIteratingSystem {
  private ComponentMapper<ScalingComponent> skalingsMap = ComponentMapper.getFor(ScalingComponent.class);

  public ScalingSystem() {
    super(Family.all(ScalingComponent.class).get());
  }

  public void process(GameEntity entity, float deltaTime) {
    ScalingComponent scalingComponent = skalingsMap.get(entity);
    scalingComponent.updateValue();

    List<SpineComponent> spineComponents = entity.getComponents(SpineComponent.class);
    for(SpineComponent spineComponent : spineComponents) {
      if(spineComponent.isEnabled()) {
        float scaleX = spineComponent.getSkeleton().getRootBone().getScaleX();
        if(scaleX != scalingComponent.getCurrentValue()) {
          spineComponent.getSkeleton().getRootBone().setScale(scalingComponent.getCurrentValue());
        }
      }
    }
  }
}