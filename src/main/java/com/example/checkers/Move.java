package com.example.checkers;

//Класс описания хода
public class Move {

    private int fromRow, fromCol; // Откуда ходим
    private int toRow, toCol; // Куда ходим
    private int betweenRow, betweenCol; // Если, происходит съедение фишки, то эти координаты описывают фишку, которую мы собираемся съесть

    public Move(int fromRow, int fromCol, int toRow, int toCol) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        if (isJump()) {
            betweenRow = (fromRow + toRow) / 2;
            betweenCol = (fromCol + toCol) / 2;
        }
    }

    //    Является ли ход поеданием фишки
    boolean isJump() {
        return Math.abs(fromRow - toRow) == 2;
    }

    public int getFromRow() {
        return fromRow;
    }

    public void setFromRow(int fromRow) {
        this.fromRow = fromRow;
    }

    public int getFromCol() {
        return fromCol;
    }

    public void setFromCol(int fromCol) {
        this.fromCol = fromCol;
    }

    public int getToRow() {
        return toRow;
    }

    public void setToRow(int toRow) {
        this.toRow = toRow;
    }

    public int getToCol() {
        return toCol;
    }

    public void setToCol(int toCol) {
        this.toCol = toCol;
    }

    public int getBetweenRow() {
        return betweenRow;
    }

    public void setBetweenRow(int betweenRow) {
        this.betweenRow = betweenRow;
    }

    public int getBetweenCol() {
        return betweenCol;
    }

    public void setBetweenCol(int betweenCol) {
        this.betweenCol = betweenCol;
    }
}
