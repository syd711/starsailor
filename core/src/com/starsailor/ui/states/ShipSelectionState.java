package com.starsailor.ui.states;

import com.starsailor.GameStateManager;
import com.starsailor.ui.UIManager;
import com.starsailor.ui.stages.GameStage;
import com.starsailor.ui.stages.hud.HudStage;

/**
 * Entered when the user selects a ship
 */
public class ShipSelectionState extends UIState {
  private HudStage hudStage;

  public ShipSelectionState() {
    hudStage = UIManager.getInstance().getHudStage();
  }

  @Override
  public void enter(GameStage entity) {
    if(isContextMenuEnabled()) {
      GameStateManager.getInstance().setPaused(true);
      hudStage.getContextMenu().show();
    }
  }

  @Override
  public void exit(GameStage entity) {
    if(isContextMenuEnabled()) {
      GameStateManager.getInstance().setPaused(false);
      hudStage.getContextMenu().hide();
    }
  }

  /**
   * The selection not always triggers the context menu
   */
  private boolean isContextMenuEnabled() {
    return !hudStage.getWeaponsPanel().isActive();
  }
}
