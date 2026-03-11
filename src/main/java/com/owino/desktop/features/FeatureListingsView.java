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
import com.owino.OSQANavigationEvents;
import com.owino.core.OSQAConfig;
import com.owino.core.OSQAModel;
import com.owino.core.Result;
import com.owino.settings.SettingDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import org.greenrobot.eventbus.EventBus;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
public class FeatureListingsView extends VBox {
    public FeatureListingsView(){
        var appDirResult = SettingDao.getAppDataDir();
        Optional<Path> featuresDir = switch (appDirResult){
            case Result.Success<Path> (Path appDir) -> Optional.of(appDir);
            case Result.Failure<Path> failure -> {
                IO.println(failure.error().getLocalizedMessage());
                yield Optional.empty();
            }
        };
        if (featuresDir.isPresent()) {
            List<OSQAModel.OSQAModule> modules = switch (OSQAConfig.listModules(featuresDir.get())){
                case Result.Success<List<OSQAModel.OSQAModule>> (List<OSQAModel.OSQAModule> modulesValue) -> modulesValue;
                case Result.Failure<List<OSQAModel.OSQAModule>> failure -> {
                    IO.println("Failed to load module list:" + failure.error().getLocalizedMessage());
                    yield List.of();
                }
            };
            if (!modules.isEmpty()){
                ObservableList<OSQAModel.OSQAModule> listViewContents = FXCollections.observableList(modules);
                ListView<OSQAModel.OSQAModule> listView = new ListView<>(listViewContents);
                listView.setCellFactory(item -> new ListCell<>(){
                    @Override
                    protected void updateItem(OSQAModel.OSQAModule module, boolean empty) {
                        super.updateItem(module, empty);
                        if (empty || module == null){
                            setText("");
                            setGraphic(null);
                        } else {
                            var moduleItemContainer = new VBox(10);
                            var nameLabel = new Label(module.name());
                            var ageLabel = new Label(module.description());
                            ageLabel.setStyle("-fx-text-fill: gray;");
                            moduleItemContainer.getChildren().addAll(nameLabel, ageLabel);
                            setGraphic(moduleItemContainer);
                        }
                    }
                });
                var moduleSelectionModel = listView.getSelectionModel();
                moduleSelectionModel.setSelectionMode(SelectionMode.SINGLE);
                var moduleSelectedItemProp = moduleSelectionModel.selectedItemProperty();
                moduleSelectedItemProp.addListener((_, _,selectedModule) -> {
                    if (selectedModule != null){
                        EventBus.getDefault().post(new OSQANavigationEvents.OpenFeatureDetailedViewEvent(selectedModule));
                    }
                });
                var listViewContainer = new VBox(listView);
                getChildren().add(listViewContainer);
                setMargin(listViewContainer,new Insets(12,12,12,12));
            }
        }
    }
}
