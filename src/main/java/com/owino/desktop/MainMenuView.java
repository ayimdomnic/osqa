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
import com.owino.core.OSQAModel.OSQAModule;
import com.owino.settings.SettingDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import com.owino.conf.OSQAConfig;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.geometry.Insets;
import javafx.scene.text.Text;
import com.owino.core.Result;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
public class MainMenuView extends VBox {
    public MainMenuView(){
        setMinWidth(200);
        setMaxWidth(300);
        setStyle("-fx-background-color: #2c3e50;");
        var moduleTitleView = new Text("OSQA");
        var menuItemMargin = new Insets(12,12,12,12);
        moduleTitleView.setFont(Font.font(21));
        getChildren().add(moduleTitleView);
        setMargin(moduleTitleView,menuItemMargin);
        var appDirResult = SettingDao.getAppDataDir();
        Optional<Path> modulesDir = switch (appDirResult){
            case Result.Success<Path> (Path appDir) -> Optional.of(appDir);
            case Result.Failure<Path> failure -> {
                IO.println(failure.error().getLocalizedMessage());
                yield Optional.empty();
            }
        };
        if (modulesDir.isPresent()) {
            List<OSQAModule> modules = switch (OSQAConfig.listModules(modulesDir.get())){
                case Result.Success<List<OSQAModule>> (List<OSQAModule> modulesValue) -> modulesValue;
                case Result.Failure<List<OSQAModule>> failure -> {
                    IO.println("Failed to load module list:" + failure.error().getLocalizedMessage());
                    yield List.of();
                }
            };
            if (!modules.isEmpty()){
                ObservableList<OSQAModule> listViewContents = FXCollections.observableList(modules);
                ListView<OSQAModule> listView = new ListView<>(listViewContents);
                listView.setCellFactory(item -> new ListCell<>(){
                    @Override
                    protected void updateItem(OSQAModule module, boolean empty) {
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
                        fireEvent(AppEvents.openModuleDetailedViewEvent(selectedModule));
                    }
                });
                var listViewContainer = new VBox(listView);
                getChildren().add(listViewContainer);
                setMargin(listViewContainer,menuItemMargin);
            }
        }
    }
}
