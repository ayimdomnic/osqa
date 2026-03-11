package com.owino.desktop.dashboard;
/*
 * Copyright (C) 2026 Samuel Owino
 *
 * OSQA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OSQA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OSQA.  If not, see <https://www.gnu.org/licenses/>.
 */
import com.owino.OSQANavigationEvents.OpenFeatureDetailedViewEvent;
import com.owino.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.OSQANavigationEvents.HomeEvent;
import com.owino.desktop.features.FeatureDetailedView;
import com.owino.desktop.features.FeatureFormView;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import javafx.stage.Stage;
public class DashboardView extends SplitPane {
    private final Stage stage;
    public DashboardView(Stage stage){
        this.stage = stage;
        EventBus.getDefault().register(this);
        initView();
    }
    private void initView() {
        getItems().add(new MainMenuView());
        getItems().add(new WelcomeView(stage));
        setOrientation(Orientation.HORIZONTAL);
        setDividerPositions(0.1f);
        setStyle("-fx-divider-color: #cccccc; -fx-divider-width: 1;");
    }
    @Subscribe
    public void handleHomeNavEvent(HomeEvent event){
        Platform.runLater(() -> {
            getItems().removeFirst();
            getItems().removeLast();
            getItems().add(new MainMenuView());
            getItems().add(new WelcomeView(stage));
        });
    }
    @Subscribe
    public void openFeatureFormEvent(OpenFeatureFormEvent event){
        Platform.runLater(() -> {
            getItems().removeFirst();
            getItems().removeLast();
            getItems().add(new MainMenuView());
            getItems().add(new FeatureFormView());
        });
    }
    @Subscribe
    public void openModuleDetailedViewEvent(OpenFeatureDetailedViewEvent event){
        getItems().removeFirst();
        getItems().removeLast();
        getItems().add(new MainMenuView());
        getItems().add(new FeatureDetailedView(event.selectedModule()));
    }
}
