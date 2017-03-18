package com.starsailor.ui.states;

import com.starsailor.actors.Player;
import com.starsailor.GameStateManager;
import com.starsailor.managers.SelectionManager;
import com.starsailor.ui.UIManager;
import com.starsailor.ui.stages.GameStage;
import com.starsailor.ui.stages.hud.HudStage;

/**
 *
 */
public class HudState extends UIState {
  @Override
  public void enter(GameStage entity) {
    HudStage hudStage = UIManager.getInstance().getHudStage();
    hudStage.getContextMenu().hide();

    GameStateManager.getInstance().setPaused(false);
    SelectionManager.getInstance().resetSelection();
    Player.getInstance().switchToDefaultState();
  }
}
