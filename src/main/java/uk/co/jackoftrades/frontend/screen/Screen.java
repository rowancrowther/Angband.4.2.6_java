package uk.co.jackoftrades.frontend.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import uk.co.jackoftrades.middle.game.Game;

public class Screen {
    private final Scene mainScene;
    private final Label statusLabel = new Label("Initialising game...");
    ;

    public Screen(@NotNull Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);
        fileMenu.getItems().add(newGameMenuItem());
        fileMenu.getItems().add(new SeparatorMenuItem());
        fileMenu.getItems().add(exitMenuHandler());

        Menu windowMenu = new Menu("Windows");
        menuBar.getMenus().addAll(windowMenu);
        windowMenu.getItems().add(windowMenuItem());

        Pane contentPane = new Pane();
        contentPane.setPrefSize(800, 600);
        contentPane.setStyle("-fx-background-color: black;");

        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(contentPane);
        root.setStyle("-fx-background-color: black;");

        mainScene = new Scene(root);

        stage.setTitle("Java Angband v4.2.6");
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.centerOnScreen();

        Label welcomeLabel = new Label("Welcome to Java Angband v4.2.6");
        welcomeLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        welcomeLabel.setFont(Font.font("TerminalVector", 12));
        statusLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        statusLabel.setFont(Font.font("TerminalVector", 12));

        VBox vBox = new VBox(10, welcomeLabel, statusLabel);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPrefSize(800, 600);

        contentPane.getChildren().addAll(vBox);

        stage.show();

        Game game = Game.getGame(statusLabel);
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    private MenuItem windowMenuItem() {
        MenuItem windowMenuItem = new MenuItem("Window");
        windowMenuItem.setOnAction(e -> {
        });
        return windowMenuItem;
    }

    private MenuItem newGameMenuItem() {
        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(e -> {
        });
        return newGameMenuItem;
    }

    private MenuItem exitMenuHandler() {
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            System.exit(0);
        });
        return exitMenuItem;
    }
}