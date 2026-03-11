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
import com.owino.OSQANavigationEvents;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
public class AppToolbar extends BorderPane {
    public AppToolbar(){
        var brandLabel = new Label("OSQA");
        var addFeatureButton = new Button("New Feature");
        var homeButton = new Button("Home");
        var rightMostContainer = new HBox();
        styleBrandLabel(brandLabel);
        rightMostContainer.getChildren().add(addFeatureButton);
        rightMostContainer.getChildren().add(homeButton);
        HBox.setMargin(addFeatureButton, new Insets(6));
        HBox.setMargin(homeButton, new Insets(6));
        setRight(rightMostContainer);
        setLeft(brandLabel);
        setMargin(rightMostContainer,new Insets(6));
        setMargin(brandLabel,new Insets(6));
        homeButton.setOnAction(_ -> EventBus.getDefault().post(new OSQANavigationEvents.HomeEvent()));
        addFeatureButton.setOnAction(_ -> EventBus.getDefault().post(new OSQANavigationEvents.OpenFeatureFormEvent()));
        brandLabel.setOnMouseClicked(_ -> EventBus.getDefault().post(new OSQANavigationEvents.HomeEvent()));
    }
    private void styleBrandLabel(Label brandLabel) {
        brandLabel.setFont(Font.font(21));
        brandLabel.setTextFill(Color.BLUE);
    }
}
