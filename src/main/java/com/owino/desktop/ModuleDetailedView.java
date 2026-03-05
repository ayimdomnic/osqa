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
import java.nio.file.Path;
import java.util.Optional;
import com.owino.core.Result;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import com.owino.core.OSQAModel;
import javafx.scene.layout.VBox;
import com.owino.conf.OSQAConfig;
import com.owino.settings.SettingDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.core.OSQAModel.OSQATestSpec;
import com.owino.core.OSQAModel.OSQATestCase;
public class ModuleDetailedView extends VBox {
    public static final Insets MARGIN = new Insets(8,22,8,22);
    public ModuleDetailedView(OSQAModule module){
        var moduleTitleLabel = new Label();
        var moduleDescriptionLabel = new Label();
        moduleTitleLabel.setText(module.name());
        moduleDescriptionLabel.setText(module.description());
        moduleTitleLabel.setFont(Font.font(47));
        moduleTitleLabel.setFont(Font.font(15));
        var testCases = module.testCases();
        ObservableList<OSQATestCase> testCasesList = FXCollections.observableList(testCases);
        ListView<OSQATestCase> testCaseListView = new ListView<>(testCasesList);
        Optional<Path> appDirOptional = switch (SettingDao.getAppDataDir()){
            case Result.Success<Path> (Path path) -> Optional.of(path);
            case Result.Failure<Path> failure -> {
                IO.println("Failed to load app dir: " + failure.error().getLocalizedMessage());
                yield Optional.empty();
            }
        };
        if (appDirOptional.isPresent()){
            testCaseListView.setCellFactory(item -> new ListCell<>(){
                @Override
                protected void updateItem(OSQATestCase testCase, boolean empty) {
                    super.updateItem(testCase, empty);
                    if (empty || testCase == null){
                        setText("");
                        setGraphic(null);
                    } else {
                        Optional<OSQATestSpec> optionalTestSpect = switch (OSQAConfig.loadTestCaseSpec(testCase)){
                            case Result.Success<OSQATestSpec> (OSQATestSpec testSpec) -> Optional.of(testSpec);
                            case Result.Failure<OSQATestSpec> failure -> {
                                IO.println("Failed to load test spec for test case " + testCase.title() + " " + failure.error().getLocalizedMessage());
                                yield Optional.empty();
                            }
                        };
                        if (optionalTestSpect.isEmpty()){
                            setText("");
                            setGraphic(null);
                        } else {
                            var testSpec = optionalTestSpect.get();
                            var container = new VBox();
                            container.getChildren().add(new Label(testSpec.action()));
                            for (OSQAModel.OSQAVerification verification : testSpec.verifications()) {
                                var listContainer = new BorderPane();
                                listContainer.setLeft(new Label(verification.description()));
                                var checkbox = new CheckBox();
                                checkbox.setSelected(verification.order() == 1);
                                listContainer.setRight(checkbox);
                                container.getChildren().add(listContainer);
                                VBox.setMargin(listContainer,MARGIN);
                            }
                            setGraphic(container);
                        }

                    }
                }
            });
        }
        var addTestCaseButton = new Button("Add Test Case");
        addTestCaseButton.setOnAction(event -> {
            var dialog = new AddVerificationDialog();
            Optional<String> inputResult = dialog.showAndWait();
            if (inputResult.isPresent()){
                var verificationDesc = inputResult.get();
                var newVerification = new OSQAModel.OSQAVerification(0,verificationDesc);
                var firstTestCase = testCases.getFirst();
                Optional<OSQATestSpec> optionalTestSpect = switch (OSQAConfig.loadTestCaseSpec(firstTestCase)){
                    case Result.Success<OSQATestSpec> (OSQATestSpec testSpec) -> Optional.of(testSpec);
                    case Result.Failure<OSQATestSpec> failure -> {
                        IO.println("Failed to load test spec for test case " + firstTestCase.title() + " " + failure.error().getLocalizedMessage());
                        yield Optional.empty();
                    }
                };
                if (optionalTestSpect.isPresent()){
                    var verifications = optionalTestSpect.get().verifications();
                    verifications.add(newVerification);
                    var updatedTestSpec = new OSQATestSpec(optionalTestSpect.get().uuid(),optionalTestSpect.get().action(),verifications);
                    Result<Void> overwriteResult = OSQAConfig.overwriteSpecFile(updatedTestSpec,testCases.getFirst());
                    switch (overwriteResult){
                        case Result.Success<Void> _ -> {
                            var alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setHeaderText("Verifications for this test have been updated successfully!");
                            var dialogResult = alert.showAndWait();
                            if (dialogResult.isPresent()){
                                fireEvent(AppEvents.openModuleDetailedViewEvent(module));
                            }
                        }
                        case Result.Failure<Void> failure -> {
                            var alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Failed to add this verification, an error occurred!");
                            alert.setContentText("Cause: " + failure.error().getLocalizedMessage());
                            alert.show();
                        }
                    }
                }

            }
        });
        getChildren().add(moduleTitleLabel);
        getChildren().add(moduleDescriptionLabel);
        getChildren().add(testCaseListView);
        getChildren().add(addTestCaseButton);
        VBox.setMargin(moduleTitleLabel,MARGIN);
        VBox.setMargin(moduleDescriptionLabel,MARGIN);
    }
}
