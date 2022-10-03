package com.example.checkers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


class Board extends Canvas {

    private BoardCore boardCore;
    private Button info;
    private Button newGameButton;
    private Bot bot;
    boolean gameInProgress; //Идет ли игра?
    int currentPlayer; //чей ход
    int selectedRow, selectedCol; //координаты выбранной фигуры
    Move[] availableMoves; //массив в котором содержатся все доступные ходы для текущего игрока

    Board(Button info, Button newGameButton) {
        super(560, 560);
        this.info = info;
        this.newGameButton = newGameButton;
        boardCore = new BoardCore();
        bot = new Bot();
        doNewGame();
    }

    //запуск новой игры
    void doNewGame() {
        boardCore.start(); //Расстановка фигур по местам
        currentPlayer = BoardCore.RED; //Человек ходит первый
        availableMoves = boardCore.getLegalMoves(BoardCore.RED); //Получение всех доступных ходов
        selectedRow = -1; //В начале игры ни одна фигура не выбрана
        info.setText("Сделайте ход!");
        gameInProgress = true;
        newGameButton.setDisable(true);
        drawBoard();
    }

    void gameOver(String str) {
        info.setText(str);
        newGameButton.setDisable(false);
        gameInProgress = false;
    }

    //    Нажатие на поле
    void doClickSquare(int row, int col) {
//        Если нажатие произошло на фигуру, которая может сделать ход, то отрисовываем возможные варианты хода для этой фигуры
        for (Move availableMove : availableMoves)
            if (availableMove.getFromRow() == row && availableMove.getFromCol() == col) {
                selectedRow = row;
                selectedCol = col;
                info.setText("Нажмите, чтобы сделать ход");
                drawBoard();
                return;
            }
//        Если фишка не выбрана
        if (selectedRow < 0) {
            info.setText("Выберите фишку для хода");
            return;
        }
//        Если выбрана какая-то фишка и нажатие произошло на доступную для хода позицию, то делаем ход
        for (Move availableMove : availableMoves)
            if (availableMove.getFromRow() == selectedRow && availableMove.getFromCol() == selectedCol
                    && availableMove.getToRow() == row && availableMove.getToCol() == col) {
                doMakeMove(availableMove);
                return;
            }
        info.setText("Нажмите, чтобы сделать ход");
    }

    void doMakeMove(Move move) {
        boardCore.makeMove(move);
//        Если ход, был поеданием фишки, то возможно игрок может сделать еще один ход
        if (move.isJump()) {
            availableMoves = boardCore.getLegalJumpsFrom(currentPlayer, move.getToRow(), move.getToCol());
            if (availableMoves != null) {
                if (currentPlayer == BoardCore.RED) {
                    info.setText("Вам необходимо съесть еще одну фишку");
                } else {
                    boardCore.setCurrentTurn(currentPlayer);
                    doMakeMove(bot.move(boardCore));
                }

                selectedRow = move.getToRow(); // Так как ход единственный, выбираем его
                selectedCol = move.getToCol();
                drawBoard();
                return;
            }
        }
//        Передача хода боту или игроку, в зависимости от предыдущего хода
        if (currentPlayer == BoardCore.RED) {
            currentPlayer = BoardCore.BLACK;
            boardCore.setCurrentTurn(currentPlayer);
            availableMoves = boardCore.getLegalMoves(currentPlayer);
            if (availableMoves == null) {
                gameOver("Вы победили!!!");
            } else {
                doMakeMove(bot.move(boardCore));
            }
        } else {
            currentPlayer = BoardCore.RED;
            boardCore.setCurrentTurn(currentPlayer);
            availableMoves = boardCore.getLegalMoves(currentPlayer);
            if (availableMoves == null)
                gameOver("Вы проиграли. Восстание машин не за горами...");
            else if (availableMoves[0].isJump())
                info.setText("Вам необходимо съесть фишку");
            else
                info.setText("Сделайте ход");
        }
//        Ни одна фигура не выбрана
        selectedRow = -1;
        drawBoard();
    }

    //    Отрисовка доски с выделением доступных ходов
    public void drawBoard() {
        GraphicsContext g = getGraphicsContext2D();
//        Проходимся по всей доске, если стоит какая-то фишка, то отрисовываем ее
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r % 2 == c % 2)
                    g.setFill(Color.LIGHTGRAY);
                else
                    g.setFill(Color.GRAY);
                g.fillRect(c * 70, r * 70, 70, 70);

                int piece = boardCore.get(r, c);
                if (piece == BoardCore.RED) {
                    g.setFill(Color.RED);
                    g.fillOval(7 + c * 70, 7 + r * 70, 56, 56);
                } else if (piece == BoardCore.BLACK) {
                    g.setFill(Color.BLACK);
                    g.fillOval(7 + c * 70, 7 + r * 70, 56, 56);
                } else if (piece == BoardCore.RED_KING) {
                    g.setFill(Color.RED);
                    g.fillOval(8 + c * 70, 8 + r * 70, 56, 56);
                    g.setFill(Color.WHITE);
                    g.setFont(Font.font(24));
                    g.fillText("K", 29 + c * 70, 43 + r * 70);
                } else if (piece == BoardCore.BLACK_KING) {
                    g.setFill(Color.BLACK);
                    g.fillOval(8 + c * 70, 8 + r * 70, 56, 56);
                    g.setFill(Color.WHITE);
                    g.setFont(Font.font(24));
                    g.fillText("K", 29 + c * 70, 43 + r * 70);
                }
            }
        }
//        Если игра продолжается, выделяем доступные ходы
        if (gameInProgress) {
            g.setStroke(Color.BLACK);
            g.setLineWidth(2);

            for (Move i : availableMoves) {
                g.strokeRect(i.getFromCol() * 70, i.getFromRow() * 70, 69, 69);
            }
            // если фигура выбрана, то мы рисуем вокруг нее желтую рамку, а вокруг
            // клеток, куда она может пойти, зеленую
            if (selectedRow >= 0) {
                g.setStroke(Color.YELLOW);
                g.setLineWidth(2);
                g.strokeRect(selectedCol * 70, selectedRow * 70, 69, 69);
                g.setStroke(Color.LIME);
                g.setLineWidth(2);
                for (Move availableMove : availableMoves) {
                    if (availableMove.getFromCol() == selectedCol && availableMove.getFromRow() == selectedRow) {
                        g.strokeRect(availableMove.getToCol() * 70, availableMove.getToRow() * 70, 69, 69);
                    }
                }
            }
        }
    }

    //    Отработка нажатия по доске
    public void mousePressed(MouseEvent evt) {
        if (!gameInProgress)
            info.setText("Нажмите кнопку \"Начать новую игру\".");
        else {
            int col = (int) ((evt.getX()) / 70);
            int row = (int) ((evt.getY()) / 70);
            if (col >= 0 && col < 8 && row >= 0 && row < 8)
                doClickSquare(row, col);
        }
    }
}