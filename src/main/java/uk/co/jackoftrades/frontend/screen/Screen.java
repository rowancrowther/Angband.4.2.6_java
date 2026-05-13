package uk.co.jackoftrades.frontend.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.jetbrains.annotations.CheckReturnValue;
import org.jetbrains.annotations.NotNull;

public class Screen {
    private Scene mainScene;
    private final Label statusLabel = new Label("Initialising game...");
    private final Label welcomeLabel = new Label("Welcome to Java Angband v4.2.6");
    private final TermData termData = new TermData();
    private int tileWidth = 1;
    private int tileHeight = 1;

    private final GraphicsContext graphicsContext;
    private final Canvas canvas;
    private final Stage stage;
    private MenuBar menuBar;

    public Screen(@NotNull Stage stage) {
        this.stage = stage;
        canvas = new Canvas(800, 600);
        graphicsContext = canvas.getGraphicsContext2D();

        redraw();
    }

    public void redraw() {
        menuBar = new MenuBar();
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

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 800, 600);

        bigcursWin(50, 75);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("TerminalVector", 12));
        graphicsContext.fillText(welcomeLabel.getText(), 250, 250);
        graphicsContext.fillText(statusLabel.getText(), 300, 300);

        VBox vBox = new VBox(menuBar, canvas);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setPrefSize(800, 600);

        mainScene = new Scene(vBox, 800, 600);
        mainScene.setRoot(vBox);

        stage.setTitle("Java Angband v4.2.6");
        stage.setScene(mainScene);
        stage.sizeToScene();
        stage.centerOnScreen();

        welcomeLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        welcomeLabel.setFont(Font.font("TerminalVector", 12));
        statusLabel.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        statusLabel.setFont(Font.font("TerminalVector", 12));

        stage.show();
    }

    public void setStatusLabelText(String text) {
        statusLabel.setText(text);

        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 800, 600);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(Font.font("TerminalVector", 12));
        graphicsContext.fillText(welcomeLabel.getText(), 250, 250);
        graphicsContext.fillText(statusLabel.getText(), 300, 300);
        stage.show();
    }

    public Label getStatusLabel() {
        return statusLabel;
    }

    @CheckReturnValue
    private @NotNull MenuItem windowMenuItem() {
        MenuItem windowMenuItem = new MenuItem("Window");
        windowMenuItem.setOnAction(e -> {
        });
        return windowMenuItem;
    }

    @CheckReturnValue
    private @NotNull MenuItem newGameMenuItem() {
        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(e -> {
        });
        return newGameMenuItem;
    }

    @CheckReturnValue
    private @NotNull MenuItem exitMenuHandler() {
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            System.exit(0);
        });
        return exitMenuItem;
    }

    private void bigcursWin(int x, int y) {
        Rect rect;
        int tileWid;
        int tileHgt;

        if (termData.isMapActive()) {
            cursWin(x, y);
            return;
        } else {
            tileWid = termData.getTileWidth();
            tileHgt = termData.getTileHeight();
        }

        long left = x * tileWid + termData.getSizeOW1();
        long right = left + tileWid * tileWidth;
        long top = y * tileHgt + termData.getSizeOH1();
        long bottom = top + tileHgt * tileHeight;

        rect = new Rect(left, top, right, bottom);

        frameRect(rect, Color.YELLOW);
    }

    private void cursWin(int x, int y) {
    }

    // Drawing methods
    private void frameRect(@NotNull Rect rect, @NotNull Color colour) {
        graphicsContext.setStroke(colour);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(rect.getLeft(), rect.getTop(), rect.getRight() - rect.getLeft(), rect.getBottom() - rect.getTop());
    }
}