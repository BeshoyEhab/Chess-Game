package Pieces;

import Utilities.Move;

/**
 * Represents a Pawn piece in a chess game.
 * A Pawn has unique movement rules, including forward movement, capturing diagonally,
 * an optional two-square move on its first move, and en passant capture.
 * It can also be promoted to another piece upon reaching the opposite end of the board.
 */
public class Pawn extends Piece {

    /**
     * Constructs a Pawn with the specified color.
     *
     * @param color the color of the Pawn ("White" or "Black").
     */
    public Pawn(String color) {
        super("Pawn", color);
    }

    /**
     * Determines if the Pawn can move from its current position to a target position
     * on the chessboard according to chess rules.
     *
     * @param fromRow the starting row of the Pawn.
     * @param fromCol the starting column of the Pawn.
     * @param toRow   the target row for the Pawn.
     * @param toCol   the target column for the Pawn.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Basic pawn movement logic
        int direction = color.equals("White") ? -1 : 1;

        // First move only: Pawn can move two steps
        return (toCol == fromCol && board[toRow][toCol] == null && toRow - fromRow == direction) // Common move
                || (toRow - fromRow == direction && Math.abs(toCol - fromCol) == 1 && board[toRow][toCol] != null) // Capturing diagonally
                || (toRow - fromRow == 2 * direction && fromCol == toCol && (fromRow == 1 || fromRow == 6)
                && board[fromRow + direction][toCol] == null && board[fromRow + 2 * direction][toCol] == null); // Special two-step move
    }

    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        return this.canMove(fromRow, fromCol, toRow, toCol, board) || (move != null && move.piece != null && move.piece.name.equals("Pawn") && board[toRow][toCol] == null && move.toRow == fromRow
                && toCol == move.fromCol && toRow - fromRow == (color.equals("White") ? -1 : 1) && Math.abs(fromCol - toCol) == 1 && Math.abs(move.fromRow - move.toRow) == 2); //En passant
    }
}
