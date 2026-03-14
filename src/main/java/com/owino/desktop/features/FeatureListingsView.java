package com.owino.desktop.features;
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
import java.util.List;
import com.owino.core.Result;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import com.owino.core.OSQAConfig;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.owino.core.OSQAModel.OSQAFeature;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureFormEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeatureDetailedViewEvent;
public class FeatureListingsView extends VBox {
    public FeatureListingsView(OSQAProduct product){
        var titleLabel = new Label("Product Features: (" + product.name() + ")");
        titleLabel.setFont(Font.font(21));
        var appDir = product.projectDir();
        List<OSQAFeature> features = switch (OSQAConfig.listFeatures(appDir)){
            case Result.Success<List<OSQAFeature>> (List<OSQAFeature> featuresValue) -> featuresValue;
            case Result.Failure<List<OSQAFeature>> failure -> {
                IO.println("Failed to load feature list:" + failure.error().getLocalizedMessage());
                yield List.of();
            }
        };
        if (features.isEmpty()){
            var noDataViewLabel = new Label("Empty Features List");
            noDataViewLabel.setFont(Font.font(21));
            var addFeatureButton = new Button("Register New Feature");
            addFeatureButton.setOnAction(_ -> EventBus.getDefault().post(new OpenFeatureFormEvent()));
            setAlignment(Pos.CENTER);
            getChildren().add(noDataViewLabel);
            getChildren().add(addFeatureButton);
            VBox.setMargin(noDataViewLabel, new Insets(12));
            VBox.setMargin(addFeatureButton, new Insets(12));
            return;
        }
        ObservableList<OSQAFeature> listViewContents = FXCollections.observableList(features);
        var listView = new ListView<>(listViewContents);
        listView.setCellFactory(_ -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAFeature feature, boolean empty) {
                super.updateItem(feature, empty);
                if (empty || feature == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var featureItemContainer = new VBox(10);
                    var nameLabel = new Label(feature.name());
                    var descriptionLabel = new Label(feature.description());
                    descriptionLabel.setMaxWidth(700);
                    descriptionLabel.setWrapText(true);
                    featureItemContainer.getChildren().addAll(nameLabel, descriptionLabel, new Separator());
                    VBox.setMargin(nameLabel,new Insets(12,12,3,12));
                    VBox.setMargin(descriptionLabel,new Insets(3,12,6,12));
                    var blueBackground = new Background(new BackgroundFill(Color.BLUE,new CornerRadii(12),Insets.EMPTY));
                    var blackBackground = new Background(new BackgroundFill(Color.BLACK,new CornerRadii(12),Insets.EMPTY));
                    featureItemContainer.setOnMouseEntered(_ -> featureItemContainer.setBackground(blueBackground));
                    featureItemContainer.setOnMouseExited(_ -> featureItemContainer.setBackground(blackBackground));
                    setGraphic(featureItemContainer);
                }
            }
        });
        listView.setBorder(Border.EMPTY);
        var featureSelectionModel = listView.getSelectionModel();
        featureSelectionModel.setSelectionMode(SelectionMode.SINGLE);
        var featureSelectedItemProp = featureSelectionModel.selectedItemProperty();
        featureSelectedItemProp.addListener((_, _,selectedFeature) -> {
            if (selectedFeature != null){
                EventBus.getDefault().post(new OpenFeatureDetailedViewEvent(selectedFeature));
            }
        });
        var listViewContainer = new VBox(listView);
        getChildren().add(titleLabel);
        getChildren().add(listViewContainer);
        setMargin(titleLabel,new Insets(12,12,12,12));
        setMargin(listViewContainer,new Insets(12,12,12,12));
    }
}
