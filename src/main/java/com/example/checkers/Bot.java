package com.example.checkers;

import java.util.ArrayList;
import java.util.Random;

import static com.example.checkers.BoardCore.BLACK;
import static com.example.checkers.BoardCore.RED;

public class Bot {

    private int depth;       //    Глубина алгоритма минимакс
    private int player;

    public Bot() {
        depth = 2;
        player = BLACK;
    }

    public Move move(BoardCore board){
        Move[] legalMoves = board.getLegalMoves(board.getCurrentTurn());
        if (legalMoves.length == 1){
            return legalMoves[0];
        }
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Move> equalBests = new ArrayList<>();
        for (Move legalMove : legalMoves){
            int moveScore = minimax(legalMove, board.getCopy(), this.depth);
            if (moveScore > bestScore){
                bestScore = moveScore;
                equalBests.clear();
            }
            if (moveScore == bestScore){
                equalBests.add(legalMove);
            }
        }

        Random rand = new Random();
        return equalBests.get(rand.nextInt(equalBests.size()));
    }

    private int minimax(Move move, BoardCore board, int depth){
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        return minimax(move, board, depth, alpha, beta);
    }

    private int minimax(Move move, BoardCore board, int depth, int alpha, int beta){
        if (depth == 0 || board.isGameOver()){
            return board.getScore(player);
        }
        board.makeMove(move);
        if (board.getCurrentTurn() == player) {
            int v = Integer.MIN_VALUE;
            Move[] legalMoves = board.getLegalMoves(board.getCurrentTurn());
            if (legalMoves != null) {
                for (Move childMove : legalMoves) {
                    v = Math.max(v, minimax(childMove, board.getCopy(), depth-1, alpha, beta));
                    alpha = Math.max(alpha, v);
                    if (alpha >= beta){
                        break;
                    }
                }
            }
            return v;
        }
        if (board.getCurrentTurn() == getOpposite(player)){
            int v = Integer.MAX_VALUE;
            Move[] legalMoves = board.getLegalMoves(getOpposite(player));
            if (legalMoves != null) {
                for (Move child : legalMoves){
                    v = Math.min(v,minimax(child, board.getCopy(), depth-1, alpha, beta));
                    beta = Math.min(beta, v);
                    if (alpha >= beta){
                        break;
                    }
                }
            }
            return v;
        }
        throw new RuntimeException("Ошибка в алгоритме minimax");
    }

    private int getOpposite(int player) {
        if (player == RED) {
            return BLACK;
        } else {
            return RED;
        }
    }
}
