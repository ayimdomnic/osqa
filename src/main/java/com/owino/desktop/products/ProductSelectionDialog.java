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
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import com.owino.core.OSQAModel.OSQAProduct;
public class ProductSelectionDialog extends Dialog<OSQAProduct> {
    private final ComboBox<OSQAProduct> productComboBox = new ComboBox<>();
    private final Label errorLabel = new Label();
    public ProductSelectionDialog(){
        initView();
        initProducts();
    }
    private void initView() {
        setTitle("Select Product");
        var container = new VBox();
        var titleLabel = new Label("Select product to proceed");
        titleLabel.setFont(Font.font(21));
        errorLabel.setTextFill(Color.RED);
        var okButtonType = new ButtonType("Proceed", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        productComboBox.setCellFactory(item -> new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct product, boolean empty) {
                super.updateItem(product, empty);
                if (empty || product == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var nameLabel = new Label(product.name() + " (" + product.target() + ")");
                    setGraphic(nameLabel);
                }
            }
        });
        productComboBox.setButtonCell(new ListCell<>(){
            @Override
            protected void updateItem(OSQAProduct selectedProduct, boolean empty) {
                super.updateItem(selectedProduct, empty);
                if (empty || selectedProduct == null){
                    setText("");
                    setGraphic(null);
                } else {
                    var nameLabel = new Label(selectedProduct.name() + " (" + selectedProduct.target() + ")");
                    nameLabel.setTextFill(Color.BLUE);
                    setGraphic(nameLabel);
                }
            }
        });
        container.getChildren().add(titleLabel);
        container.getChildren().add(productComboBox);
        container.getChildren().add(errorLabel);
        VBox.setMargin(titleLabel, new Insets(12));
        VBox.setMargin(productComboBox, new Insets(12));
        VBox.setMargin(errorLabel, new Insets(12));
        productComboBox.setMinWidth(300);
        container.setMinWidth(350);
        getDialogPane().setContent(container);
        setResultConverter(buttonType -> {
            if (buttonType == okButtonType){
                return productComboBox.getValue();
            }
            return null;
        });
    }
    private void initProducts() {
        switch (OSQAProductDao.listProducts()){
            case Result.Success<List<OSQAProduct>> (List<OSQAProduct> products) -> productComboBox.getItems().addAll(products);
            case Result.Failure<List<OSQAProduct>> failure -> errorLabel.setText("Failed to load products: " + failure.error().getLocalizedMessage());
        }
    }
}
