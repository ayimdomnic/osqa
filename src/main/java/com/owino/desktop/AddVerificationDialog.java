package com.owino.desktop;
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
import javafx.application.Platform;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
public class AddVerificationDialog extends Dialog<String> {
    public AddVerificationDialog(){
        var container = new VBox();
        setTitle("Add New Verification");
        var verificationDescTextArea = new TextArea();
        verificationDescTextArea.setPromptText("Enter multiple lines of text...");
        verificationDescTextArea.setWrapText(true);
        verificationDescTextArea.setPrefRowCount(5);
        verificationDescTextArea.setPrefColumnCount(40);
        var okButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);
        container.getChildren().add(verificationDescTextArea);
        getDialogPane().setContent(container);
        Platform.runLater(verificationDescTextArea::requestFocus);
        setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return verificationDescTextArea.getText();
            }
            return null;
        });
    }
}
