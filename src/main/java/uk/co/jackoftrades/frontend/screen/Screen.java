/*
 * Copyright (c) 1987-2022 Angband contributors.
 *
 * This work is free software; you can redistribute it and/or modify it
 * under the terms of either:
 *
 * a) the GNU General Public License as published by the Free Software
 *    Foundation, version 2, or
 *
 * b) the Angband licence:
 *    This software may be copied and distributed for educational, research,
 *    and not for profit purposes provided that this copyright and statement
 *    are included in all such copies.  Other copyrights may also apply.
 *
 *    Java code copyright (c) Rowan Crowther 2026
 */

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
import uk.co.jackoftrades.frontend.screen.enums.TermXtraEventEnum;
import uk.co.jackoftrades.frontend.screen.hooks.TermXtraWin;

/**
 * The JavaFX realisation of a game window: it owns the {@link Stage}, the drawing
 * {@link Canvas}/{@link GraphicsContext}, the menu bar and the logical {@link Term}
 * it renders. This is the concrete front end that replaces the C original's
 * platform-specific window code ({@code main-win.c} et al.) — the place where the
 * abstract terminal model meets actual pixels.
 *
 * @author ClaudeCode
 */
public class Screen {
    /**
     * The current JavaFX scene shown on the stage.
     *
     * @author ClaudeCode
     */
    private Scene mainScene;
    /**
     * Status line label, updated as the game reports progress.
     *
     * @author ClaudeCode
     */
    private final Label statusLabel = new Label("Initialising game...");
    /**
     * Welcome/title label shown on the initial screen.
     *
     * @author ClaudeCode
     */
    private final Label welcomeLabel = new Label("Welcome to Java Angband v4.2.6");
    /**
     * The logical terminal this screen draws.
     *
     * @author ClaudeCode
     */
    private final Term term;
    /**
     * Horizontal tile scale (in cells) for the cursor/graphics.
     *
     * @author ClaudeCode
     */
    private int tileWidth = 1;
    /**
     * Vertical tile scale (in cells) for the cursor/graphics.
     *
     * @author ClaudeCode
     */
    private int tileHeight = 1;

    /**
     * Whether a palette-based colour mode is in use.
     *
     * @author ClaudeCode
     */
    private boolean paletted = false;
    /**
     * Whether the display is limited to 16 colours.
     *
     * @author ClaudeCode
     */
    private boolean colours16 = false;

    /**
     * The 2D graphics context used for all drawing on {@link #canvas}.
     *
     * @author ClaudeCode
     */
    private final GraphicsContext graphicsContext;
    /**
     * The canvas the terminal contents are painted onto.
     *
     * @author ClaudeCode
     */
    private final Canvas canvas;
    /**
     * The JavaFX stage (top-level window) hosting this screen.
     *
     * @author ClaudeCode
     */
    private final Stage stage;
    /**
     * The window's menu bar.
     *
     * @author ClaudeCode
     */
    private MenuBar menuBar;

    /**
     * Build the screen on the given stage: create the canvas, wire up a fresh
     * {@link Term} and {@link TermData}, and perform the initial draw.
     *
     * @param stage the JavaFX stage to host this window
     * @author ClaudeCode
     */
    public Screen(@NotNull Stage stage) {
        this.stage = stage;
        canvas = new Canvas(800, 600);
        graphicsContext = canvas.getGraphicsContext2D();

        term = new Term();
        term.termInit(800, 600, 0);
        TermData termData = new TermData();
        termData.termDataLink(term);

        redraw();
    }

    /**
     * Reset the window to a blank black canvas under the menu bar.
     *
     * @author ClaudeCode
     */
    public void clear() {
        Canvas canvas = new Canvas(800, 600);
        GraphicsContext graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(0, 0, 800, 600);

        VBox vBox = new VBox(menuBar, canvas);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.setPrefSize(800, 600);

        mainScene = new Scene(vBox, 800, 600);
        stage.setScene(mainScene);
    }

    /**
     * Rebuild and show the whole window: menu bar (File/Windows menus), the black
     * canvas, the cursor frame and the welcome/status text, then size and centre
     * the stage. Called once at construction and whenever a full repaint is needed.
     *
     * @author ClaudeCode
     */
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

    /**
     * Update the status line text and repaint the welcome/status area.
     *
     * @param text the new status message
     * @author ClaudeCode
     */
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

    /**
     * @return the status-line label
     * @author ClaudeCode
     */
    public Label getStatusLabel() {
        return statusLabel;
    }

    /**
     * Build the "Window" menu item (currently a no-op action placeholder).
     *
     * @return the configured menu item
     * @author ClaudeCode
     */
    @CheckReturnValue
    private @NotNull MenuItem windowMenuItem() {
        MenuItem windowMenuItem = new MenuItem("Window");
        windowMenuItem.setOnAction(e -> {
        });
        return windowMenuItem;
    }

    /**
     * Build the "New Game" menu item, which clears the terminal when chosen.
     *
     * @return the configured menu item
     * @author ClaudeCode
     */
    @CheckReturnValue
    private @NotNull MenuItem newGameMenuItem() {
        MenuItem newGameMenuItem = new MenuItem("New Game");
        newGameMenuItem.setOnAction(e -> {
            TermXtraWin win = new TermXtraWin();
            win.doSomething(TermXtraEventEnum.TERM_XTRA_CLEAR, 0);
        });
        return newGameMenuItem;
    }

    /**
     * Build the "Exit" menu item, which terminates the JVM when chosen.
     *
     * @return the configured menu item
     * @author ClaudeCode
     */
    @CheckReturnValue
    private @NotNull MenuItem exitMenuHandler() {
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            System.exit(0);
        });
        return exitMenuItem;
    }

    /**
     * Draw the large (tile-sized) cursor highlight at the given cell. Falls back
     * to {@link #cursWin(int, int)} when the reduced-scale map is active;
     * otherwise computes the tile's pixel rectangle (offsetting by the window
     * borders) and frames it in yellow. Java port of the C {@code Term_bigcurs}.
     *
     * @param x the cell column
     * @param y the cell row
     * @author ClaudeCode
     */
    private void bigcursWin(int x, int y) {
        Rect rect;
        long tileWid;
        long tileHgt;

        if (term.getTermData().isMapActive()) {
            cursWin(x, y);
            return;
        } else {
            tileWid = term.getTermData().getTileWidth();
            tileHgt = term.getTermData().getTileHeight();
        }

        long left = (long) x * tileWid + term.getTermData().getSizeOW1();
        long right = left + tileWid * tileWidth;
        long top = (long) y * tileHgt + term.getTermData().getSizeOH1();
        long bottom = top + tileHgt * tileHeight;

        rect = new Rect(left, top, right, bottom);

        frameRect(rect, Color.YELLOW);
    }

    /**
     * Draw the normal (single-cell) cursor highlight at the given cell. Currently
     * a stub awaiting the single-cell cursor rendering.
     *
     * @param x the cell column
     * @param y the cell row
     * @author ClaudeCode
     */
    private void cursWin(int x, int y) {
    }

    // Drawing methods

    /**
     * Stroke the outline of a rectangle in the given colour.
     *
     * @param rect   the rectangle to frame
     * @param colour the stroke colour
     * @author ClaudeCode
     */
    private void frameRect(@NotNull Rect rect, @NotNull Color colour) {
        graphicsContext.setStroke(colour);
        graphicsContext.setLineWidth(2);
        graphicsContext.strokeRect(rect.getLeft(), rect.getTop(), rect.getRight() - rect.getLeft(), rect.getBottom() - rect.getTop());
    }
}