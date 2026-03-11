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
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
public class MainMenuView extends VBox {
    private static final Insets MENU_ITEM_MARGIN = new Insets(12,12,12,12);
    private Label productsLabel;
    private Label featuresLabel;
    private Label reportsLabel;
    public MainMenuView(){
        setMinWidth(200);
        setMaxWidth(300);
        initView();
    }
    private void initView() {
        productsLabel = new Label("Products");
        featuresLabel = new Label("Features");
        reportsLabel = new Label("Reports");
        setMenuItemStyle(productsLabel);
        setMenuItemStyle(featuresLabel);
        setMenuItemStyle(reportsLabel);
        productsLabel.setOnMouseClicked(_ -> {
            highlightEffect(productsLabel);
            unhighlightEffect(featuresLabel);
            unhighlightEffect(reportsLabel);
        });
        featuresLabel.setOnMouseClicked(_ -> {
            highlightEffect(featuresLabel);
            unhighlightEffect(productsLabel);
            unhighlightEffect(reportsLabel);
        });
        reportsLabel.setOnMouseClicked(_ -> {
            highlightEffect(reportsLabel);
            unhighlightEffect(productsLabel);
            unhighlightEffect(featuresLabel);
        });
        getChildren().add(productsLabel);
        getChildren().add(featuresLabel);
        getChildren().add(reportsLabel);
    }
    private void setMenuItemStyle(Label menuItem){
        menuItem.setFont(Font.font(17));
        setMargin(menuItem,MENU_ITEM_MARGIN);
    }
    private void highlightEffect(Label menuItem){
        menuItem.setFont(Font.font(20));
    }
    private void unhighlightEffect(Label menuItem){
        menuItem.setFont(Font.font(17));
    }
}
