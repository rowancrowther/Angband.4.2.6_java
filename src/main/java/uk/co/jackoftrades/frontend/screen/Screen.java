package uk.co.jackoftrades.frontend.screen;

import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class Screen {
    private final Scene mainScene;

    public Screen(@NotNull Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        menuBar.getMenus().addAll(fileMenu);

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
        stage.show();
    }
}
