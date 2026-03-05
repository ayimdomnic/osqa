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
import com.owino.conf.OSQAConfig;
import com.owino.core.OSQAModel;
import com.owino.core.Result;
import com.owino.settings.SettingDao;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import tools.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public class VerificationFormView extends ScrollPane {
    private final List<OSQAModel.OSQATestCase> testCases = new ArrayList<>();
    private static final Insets MARGIN = new Insets(6,22,12,22);
    private static final Insets FIELD_MARGIN = new Insets(6,12,12,12);
    private TextArea verificationField;
    private TextArea userActionField;
    private TextField moduleTitleTextField;
    public VerificationFormView(){
        var moduleForm = initModuleForm();
        setContent(moduleForm);
        setFitToWidth(true);
    }
    private VBox initModuleForm() {
        var formContainer = new VBox();
        var header = new BorderPane();
        var titleText = new Text("Create New OSQA Verification");
        titleText.setFont(Font.font(21));
        var moduleActionContainer = new HBox(22);
        var cancelButton = new Button("Cancel");
        moduleActionContainer.getChildren().add(cancelButton);
        header.setLeft(titleText);
        header.setRight(moduleActionContainer);
        var saveButton = new Button("Save OSQA Test");
        cancelButton.setOnAction(_ -> fireEvent(AppEvents.closeModuleFormEvent()));

        var moduleDetailsContainer = new VBox();
        var moduleTitleText = new Text("Test Name:");
        moduleTitleTextField = new TextField();
        var descriptionText = new Text("Test Description:");
        var descriptionTextField = new TextField();
        moduleTitleText.setFont(Font.font(15));
        descriptionText.setFont(Font.font(15));

        moduleDetailsContainer.getChildren().add(moduleTitleText);
        moduleDetailsContainer.getChildren().add(moduleTitleTextField);
        moduleDetailsContainer.getChildren().add(descriptionText);
        moduleDetailsContainer.getChildren().add(descriptionTextField);
        moduleDetailsContainer.setBackground(Background.fill(Color.GRAY));

        VBox.setMargin(moduleTitleText,FIELD_MARGIN);
        VBox.setMargin(moduleTitleTextField,FIELD_MARGIN);
        VBox.setMargin(descriptionText,FIELD_MARGIN);
        VBox.setMargin(descriptionTextField,FIELD_MARGIN);

        formContainer.getChildren().add(header);
        formContainer.getChildren().add(moduleDetailsContainer);
        VBox.setMargin(header,MARGIN);
        VBox.setMargin(moduleDetailsContainer,MARGIN);
        moduleActionContainer.getChildren().add(saveButton);
        VBox.setMargin(saveButton,MARGIN);
        addTestCaseForm(formContainer);
        saveButton.setOnAction(_ -> {
            var testCaseTitle = "testcase";
            Optional<Path> optionalAppDir = switch (SettingDao.getAppDataDir()){
                case Result.Success<Path> (Path path) -> Optional.of(path);
                case Result.Failure<Path> failure -> {
                    IO.println("Failed to load app dir " + failure.error().getLocalizedMessage());
                    yield Optional.empty();
                }
            };
            if (optionalAppDir.isPresent()){
                var appDir = optionalAppDir.get();
                var specFile = testCaseTitle + OSQAConfig.timestampedName(LocalDateTime.now(),"json");
                var filePath = appDir.toAbsolutePath().toString().concat("/").concat(specFile);
                var testCase = new OSQAModel.OSQATestCase(UUID.randomUUID().toString(),testCaseTitle,filePath);
                var verification = new OSQAModel.OSQAVerification(0,verificationField.getText());
                var specification = new OSQAModel.OSQATestSpec(UUID.randomUUID().toString(),userActionField.getText(),List.of(verification));
                OSQAConfig.writeSpecFile(appDir,specification,specFile);
                testCases.add(testCase);
                var moduleTitle = moduleTitleTextField.getText();
                var moduleDescription = descriptionTextField.getText();
                var module = new OSQAModel.OSQAModule(
                        UUID.randomUUID().toString(),
                        moduleTitle,
                        moduleDescription,
                        "Critical",
                        testCases);
                OSQAConfig.writeModule(appDir,module);
                IO.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(module));
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success!");
                successAlert.setContentText("Module created successfully!");
                successAlert.showAndWait();
                successAlert.setOnCloseRequest(event -> {
                    fireEvent(AppEvents.closeModuleFormEvent());
                    successAlert.close();
                });
            }
        });
        return formContainer;
    }
    private void addTestCaseForm(VBox container) {
        var testCaseFormContainer = new VBox();
        var separator = new Separator();
        var userActionTitle = new Text("Describe User Action:");
        userActionField = new TextArea();
        userActionTitle.setFont(Font.font(15));
        userActionField.setFont(Font.font(15));

        testCaseFormContainer.getChildren().add(userActionTitle);
        testCaseFormContainer.getChildren().add(userActionField);
        testCaseFormContainer.getChildren().add(separator);

        VBox.setMargin(userActionTitle,FIELD_MARGIN);
        VBox.setMargin(userActionField,FIELD_MARGIN);
        container.getChildren().add(testCaseFormContainer);
        VBox.setMargin(testCaseFormContainer,MARGIN);
        testCaseFormContainer.setBackground(Background.fill(Color.GRAY));

        var verificationLabel = new Text("Verifications:");
        verificationLabel.setFont(Font.font(15));
        verificationField = new TextArea();
        var verificationSeparator = new Separator();
        testCaseFormContainer.getChildren().add(verificationLabel);
        testCaseFormContainer.getChildren().add(verificationField);
        testCaseFormContainer.getChildren().add(verificationSeparator);
        VBox.setMargin(verificationLabel,FIELD_MARGIN);
        VBox.setMargin(verificationField,FIELD_MARGIN);
    }
}
