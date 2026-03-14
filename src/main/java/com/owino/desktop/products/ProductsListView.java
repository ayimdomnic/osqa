package com.owino.desktop.products;
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
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;
import org.greenrobot.eventbus.EventBus;
import com.owino.core.OSQAModel.OSQAProduct;
import com.owino.desktop.OSQANavigationEvents.OpenDashboardEvent;
import com.owino.desktop.OSQANavigationEvents.OpenFeaturesListViewEvent;
public class ProductsListView extends VBox{
    private final ObservableList<OSQAProduct> productObservableList = FXCollections.observableArrayList();
    public ProductsListView(){
        initView();
        initProducts();
    }
    private void initView() {
        var titleLabel = new Label("Products");
        titleLabel.setFont(Font.font(21));
        var productsListView = new ListView<>(productObservableList);
        productsListView.setCellFactory(item -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty){
                    setText("");
                    setGraphic(null);
                } else {
                    var container = new VBox();
                    var nameLabel = new Label(item.name());
                    var detailedContainer = new HBox(12);
                    var targetLabel = new Label(item.target());
                    var dirLabel = new Label(item.projectDir().toAbsolutePath().toString());
                    nameLabel.setFont(Font.font(17));
                    targetLabel.setFont(Font.font(15));
                    dirLabel.setFont(Font.font(15));
                    detailedContainer.getChildren().add(targetLabel);
                    detailedContainer.getChildren().add(dirLabel);
                    container.getChildren().add(nameLabel);
                    container.getChildren().add(detailedContainer);
                    var blueBackground = new Background(new BackgroundFill(Color.BLUE,new CornerRadii(12), Insets.EMPTY));
                    var blackBackground = new Background(new BackgroundFill(Color.BLACK,new CornerRadii(12),Insets.EMPTY));
                    container.setOnMouseEntered(_ -> container.setBackground(blueBackground));
                    container.setOnMouseExited(_ -> container.setBackground(blackBackground));
                    container.setBackground(blackBackground);
                    VBox.setMargin(nameLabel, new Insets(12));
                    VBox.setMargin(detailedContainer, new Insets(12));
                    setGraphic(container);
                }
            }
        });
        productsListView.getSelectionModel().selectedItemProperty().addListener((_,_,selectedProduct) -> {
            EventBus.getDefault().post(new OpenFeaturesListViewEvent(selectedProduct));
        });
        productsListView.setBorder(Border.EMPTY);
        getChildren().add(titleLabel);
        getChildren().add(productsListView);
        VBox.setMargin(titleLabel, new Insets(12));
        VBox.setMargin(productsListView, new Insets(12));
    }
    private void initProducts() {
        switch (OSQAProductDao.listProducts()){
            case Result.Success<List<OSQAProduct>> (List<OSQAProduct> products) -> {
                Platform.runLater(() -> {
                    productObservableList.removeAll();
                    productObservableList.addAll(products);
                });
            }
            case Result.Failure<List<OSQAProduct>> failure -> {
                Platform.runLater(() -> {
                    var alert = new Alert(Alert.AlertType.ERROR);
                    alert.setContentText("""
                        Failed to load products
                        Cause: %s
                        """.formatted(failure.error().getLocalizedMessage()));
                    if (alert.showAndWait().isPresent()){
                        EventBus.getDefault().post(new OpenDashboardEvent());
                    }
                });
            }
        }
    }
}
