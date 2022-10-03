package com.example.checkers;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;


public class Checkers extends Application {

    private Button newGameButton;
    private Button info;
    private Board board;

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) {
//        настраиваем кнопку новой игры, кнопку информации и саму доску
        newGameButtonSettings();
        infoSettings();
        boardSettings();
//        настраиваем отображение
        Pane root = new Pane();
        root.setPrefWidth(650);
        root.setPrefHeight(800);
        root.getChildren().addAll(board, newGameButton, info);
        root.setStyle("-fx-background-color: #06a29d");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Шашки");
        stage.show();
    }

    private void newGameButtonSettings() {
        newGameButton = new Button("Начать новую игру");
        newGameButton.relocate(50, 720);
        newGameButton.setManaged(false);
        newGameButton.resize(560,50);
        newGameButton.fontProperty().setValue(Font.font(24));
        newGameButton.setOnAction(e -> board.doNewGame() );
    }

    private void boardSettings() {
//        в конструкторе доски происходит запуск новой игры
        board = new Board(info, newGameButton);
        board.setOnMousePressed(e -> board.mousePressed(e) );
        board.relocate(50,50);
    }

    private void infoSettings() {
        info = new Button();
        info.relocate(50, 630);
        info.setManaged(false);
        info.resize(560,50);
        info.fontProperty().setValue(Font.font(24));
    }
}

