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
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import org.greenrobot.eventbus.EventBus;
import com.owino.desktop.OSQANavigationEvents.OpenProductFormEvent;
public class WelcomeView extends StackPane {
    public WelcomeView(){
        initView();
    }
    private void initView() {
        var welcomeAscii = """
                 _____ _____  _____  ___
                |  _  /  ___||  _  |/ _ \\
                | | | \\ `--. | | | / /_\\ \\
                | | | |`--. \\| | | |  _  |
                \\ \\_/ /\\__/ /\\ \\/' / | | |
                 \\___/\\____/  \\_/\\_\\_| |_/
                
                Welcome to OSQA!
                """;
        VBox itemsContainer = new VBox(22);
        var ascii = new Text(welcomeAscii);
        ascii.setFont(Font.font(25));
        ascii.setFill(Color.BLUE);
        itemsContainer.getChildren().add(ascii);
        Button createProductButton = new Button("Register Software Product");
        HBox buttonsContainer = new HBox(21);
        buttonsContainer.getChildren().add(createProductButton);
        itemsContainer.getChildren().add(buttonsContainer);
        getChildren().add(itemsContainer);
        setMargin(itemsContainer, new Insets(45));
        createProductButton.setOnAction(_ -> EventBus.getDefault().post(new OpenProductFormEvent()));

    }
}
