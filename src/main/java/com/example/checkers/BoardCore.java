package com.example.checkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Класс логики игры, в нем хранится все данные о текущем состоянии игры (координаты каждой фигуры)
//Красные ходят "вверх" по доске (номер строки увеличивается)
//Черные ходят "вниз" по доске (номер строки уменьшается)
public class BoardCore {

    static final int EMPTY = 0, RED = 1, RED_KING = 2, BLACK = 3, BLACK_KING = 4; //Каждому состоянию поля доски соответствует число
    int[][] board = new int[8][8];
    private int currentTurn;

    BoardCore() {
        start();
    }

//    Первые три строки - черные
//    Последние три строки - красные
    void start() {
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (r % 2 == c % 2) {
                    if (r < 3)
                        board[r][c] = BLACK;
                    else if (r > 4)
                        board[r][c] = RED;
                    else
                        board[r][c] = EMPTY;
                } else
                    board[r][c] = EMPTY;
            }
        }
        this.currentTurn = RED;
    }

    //    Получение фишки по координатам
    int get(int row, int col) {
        return board[row][col];
    }

    //    Сделать ход
    void makeMove(Move move) {
        int fromRow = move.getFromRow();
        int fromCol = move.getFromCol();
        int toRow = move.getToRow();
        int toCol = move.getToCol();

//        Передвигание фишки
        board[toRow][toCol] = board[fromRow][fromCol];
        board[fromRow][fromCol] = EMPTY;

//        Если ход является поеданием какой-то фишки, то необходимо удалить съеденную фишку с доски
        if (move.isJump()) {
            board[move.getBetweenRow()][move.getBetweenCol()] = EMPTY;
        }

//        Если фишку поставили на край доски, значит необходимо сделать ее королевой
        if (toRow == 0 && board[toRow][toCol] == RED)
            board[toRow][toCol] = RED_KING;
        if (toRow == 7 && board[toRow][toCol] == BLACK)
            board[toRow][toCol] = BLACK_KING;

        if (move.isJump()) {
            Move[] legalJumpsFrom = getLegalJumpsFrom(currentTurn, move.getToRow(), move.getToCol());
            if (legalJumpsFrom == null) {
                transferTurn();
            }
        } else {
            transferTurn();
        }
    }

    //    Метод возвращает массив доступных ходов для игрока или null, если доступных ходов нет.
//    Вернувшийся массив будет состоять либо полностью из обычных ходов,
//    либо из ходов поедания фишек, потому что в правилах сказанно, что игрок ОБЯЗАН съесть фишку, если это возможно
    Move[] getLegalMoves(int player) {

        if (player != RED && player != BLACK) return null;
        int playerKing = player + 1; // Какого цвета король у игрока
        List<Move> legalMoves = new ArrayList<>();
        List<Move> legalJumps = new ArrayList<>();
//        Проходимся по всей доске и проверяем любой возможный ход на доступность
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == player || board[r][c] == playerKing) {
                    Move jump1 = new Move(r, c, r + 2, c + 2);
                    Move jump2 = new Move(r, c, r - 2, c + 2);
                    Move jump3 = new Move(r, c, r + 2, c - 2);
                    Move jump4 = new Move(r, c, r - 2, c - 2);
                    Move move1 = new Move(r, c, r + 1, c + 1);
                    Move move2 = new Move(r, c, r - 1, c + 1);
                    Move move3 = new Move(r, c, r + 1, c - 1);
                    Move move4 = new Move(r, c, r - 1, c - 1);

                    if (canJump(player, jump1)) legalJumps.add(jump1);
                    if (canJump(player, jump2)) legalJumps.add(jump2);
                    if (canJump(player, jump3)) legalJumps.add(jump3);
                    if (canJump(player, jump4)) legalJumps.add(jump4);
                    if (canMove(player, move1)) legalMoves.add(move1);
                    if (canMove(player, move2)) legalMoves.add(move2);
                    if (canMove(player, move3)) legalMoves.add(move3);
                    if (canMove(player, move4)) legalMoves.add(move4);
                }
            }
        }

        Move[] moveArray;
        int movesSize = legalMoves.size();
        int jumpsSize = legalJumps.size();
//        Если оба списка пустые, значит доступных ходов нет
        if (movesSize == 0 && jumpsSize == 0) return null;
//        Если есть доступные ходы на съедение, то возращаем их, иначе возвращаем обычные ходы
        if (jumpsSize != 0) {
            moveArray = new Move[jumpsSize];
            for (int i = 0; i < jumpsSize; i++) moveArray[i] = legalJumps.get(i);
        } else {
            moveArray = new Move[movesSize];
            for (int i = 0; i < movesSize; i++) moveArray[i] = legalMoves.get(i);
        }

        return moveArray;
    }

    //    Список доступных поеданий для конкретной фишки
    Move[] getLegalJumpsFrom(int player, int r, int c) {

        if (player != RED && player != BLACK) return null;
        int playerKing = player + 1; // Какого цвета король у игрока

        ArrayList<Move> moves = new ArrayList<>();
        if (board[r][c] == player || board[r][c] == playerKing) {
            Move jump1 = new Move(r, c, r + 2, c + 2);
            Move jump2 = new Move(r, c, r - 2, c + 2);
            Move jump3 = new Move(r, c, r + 2, c - 2);
            Move jump4 = new Move(r, c, r - 2, c - 2);

            if (canJump(player, jump1)) moves.add(jump1);
            if (canJump(player, jump2)) moves.add(jump2);
            if (canJump(player, jump3)) moves.add(jump3);
            if (canJump(player, jump4)) moves.add(jump4);
        }

        if (moves.size() == 0) return null;

        Move[] moveArray = new Move[moves.size()];
        for (int i = 0; i < moves.size(); i++) moveArray[i] = moves.get(i);

        return moveArray;
    }

    //    Проверка легальности съедения фишки
    private boolean canJump(int player, Move move) {
        int r1 = move.getFromRow(), r2 = move.getBetweenRow(), r3 = move.getToRow();
        int c1 = move.getFromCol(), c2 = move.getBetweenCol(), c3 = move.getToCol();

        if (r3 < 0 || r3 >= 8 || c3 < 0 || c3 >= 8) return false;
        if (board[r3][c3] != EMPTY) return false;

        if (player == RED) {
            if (board[r1][c1] == RED && r3 > r1) return false;  // red может двигаться только вверх.
            return board[r2][c2] == BLACK || board[r2][c2] == BLACK_KING;
        } else {
            if (board[r1][c1] == BLACK && r3 < r1) return false;  // black может двигаться только вниз.
            return board[r2][c2] == RED || board[r2][c2] == RED_KING;
        }
    }

    //    Проверка легальности обычного хода
    private boolean canMove(int player, Move move) {

        int r1 = move.getFromRow(), r2 = move.getToRow();
        int c1 = move.getFromCol(), c2 = move.getToCol();

        if (r2 < 0 || r2 >= 8 || c2 < 0 || c2 >= 8) return false; // Нельзя пойти за пределы доски
        if (board[r2][c2] != EMPTY) return false; // Нельзя пойти не на пустое поле

        if (player == RED) {
            return board[r1][c1] == RED_KING || r2 <= r1;  // red фигура может двигаться только вверх.
        } else {
            return board[r1][c1] == BLACK_KING || r2 >= r1;  // black фигура может двигаться только вниз.
        }
    }

    private void transferTurn() {
        if (currentTurn == RED) {
            currentTurn = BLACK;
        } else {
            currentTurn = RED;
        }
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public int getCurrentTurn() {
        return currentTurn;
    }

    public void setCurrentTurn(int currentTurn) {
        this.currentTurn = currentTurn;
    }

    public BoardCore getCopy() {
        BoardCore copy = new BoardCore();
        int[][] boardCopy = Arrays.stream(board)
                .map(int[]::clone)
                .toArray(int[][]::new);

        copy.setCurrentTurn(getCurrentTurn());
        copy.setBoard(boardCopy);
        return copy;
    }

    public int pieceCount(int player) {
        int counter = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == player)
                    counter++;
            }
        }
        return counter;
    }

    public boolean isGameOver() {
        int countRed = 0;
        int countBlack = 0;
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                if (board[r][c] == RED || board[r][c] == RED_KING)
                    countRed++;

                if (board[r][c] == BLACK || board[r][c] == BLACK_KING)
                    countBlack++;
            }
        }

        return countRed == 0 || countBlack == 0;
    }

    int getScore(int player){
        // "бесконечное" значение для выигрыша
        if (pieceCount(getOpposite(player)) == 0) {
            return Integer.MAX_VALUE;
        }
        // "негативная бесконечность" для проигрыша
        if (pieceCount(player) == 0){
            return Integer.MIN_VALUE;
        }
        // разница между подсчетом фигур при двойном подсчете королей
        return pieceScore(player) - pieceScore(getOpposite(player));
    }

    private int pieceScore(int player){
        return pieceCount(player) + pieceCount(player + 1);
    }

    private int getOpposite(int player) {
        if (player == RED) {
            return BLACK;
        } else {
            return RED;
        }
    }

}