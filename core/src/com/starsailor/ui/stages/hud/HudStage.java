package com.starsailor.ui.stages.hud;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.starsailor.Game;
import com.starsailor.ui.stages.hud.navigation.NavigationPanel;
import com.starsailor.ui.stages.hud.selection.ContextMenu;
import com.starsailor.ui.stages.hud.weapons.WeaponsPanel;

/**
 * The general game stage for menus and side panels.
 */
public class HudStage extends Stage {

  private final NavigationPanel navigationPanel;
  private final WeaponsPanel weaponsPanel;
  private final ContextMenu contextMenu;

  public HudStage() {
    Game.inputManager.addInputProcessor(this);

    //add panels and context menu
    weaponsPanel = new WeaponsPanel();
    navigationPanel = new NavigationPanel();
    contextMenu = new ContextMenu();

    addActor(weaponsPanel);
    addActor(navigationPanel);
    addActor(contextMenu);
  }

  public ContextMenu getContextMenu() {
    return contextMenu;
  }

  public WeaponsPanel getWeaponsPanel() {
    return weaponsPanel;
  }

  public NavigationPanel getNavigationPanel() {
    return navigationPanel;
  }
}
