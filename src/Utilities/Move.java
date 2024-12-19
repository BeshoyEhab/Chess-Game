package Utilities;

import Pieces.*;
/**
 * Represents a chess move with detailed information about the piece's movement.
 * Tracks the source and destination coordinates, piece information, and move-related states.
 */
public class Move {
    /** Row index of the piece's starting position. */
    public int fromRow;

    /** Column index of the piece's starting position. */
    public int fromCol;

    /** Row index of the piece's destination position. */
    public int toRow;

    /** Column index of the piece's destination position. */
    public int toCol;

    /** Array to store timers associated with the move. */
    public int[] timers;

    /** The piece being moved. */
    public Piece piece;

    /** The piece that was captured during this move, if any. */
    public Piece capturedPiece;

    /** Evaluation score for the move. */
    public int eval;

    /** Indicates whether the piece has previously moved. */
    public boolean haveMoved;

    public String promoteTo;

    /**
     * Constructs a new Move with detailed move information.
     *
     * @param fromRow Row index of the starting position
     * @param fromCol Column index of the starting position
     * @param toRow Row index of the destination position
     * @param toCol Column index of the destination position
     * @param piece The piece being moved
     * @param capturedPiece The piece that is captured (if any)
     * @param timer1 First timer value associated with the move
     * @param timer2 Second timer value associated with the move
     */
    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece, Piece capturedPiece, int timer1, int timer2) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.haveMoved = piece != null && piece.haveMove;
        this.timers = new int[]{timer1, timer2};
    }

    /**
     * Generates a string representation of the move.
     * Handles special cases like castling, en passant, and piece promotion.
     *
     * @return A descriptive string of the move
     */
    @Override
    public String toString() {
        if (piece == null) return "(" + this.fromRow + "," + this.fromCol + ") -> (" + this.toRow + "," + this.toCol + ")";
        String result = piece.name + "(" + (char) ('A' + fromCol) + (8 - fromRow) + "->" + (char) ('A' + toCol) + (8 - toRow) + ")" ;
        if (piece.name.equals("King")) {
            if (toCol - fromCol == 2) {
                result = "O-O";
            } else if (toCol - fromCol == -2) {
                result = "O-O-O";
            }
        } else if (piece.name.equals("Pawn")) {
            if (toRow == 0 || toRow == 7) {
                result += " = " + promoteTo;
            } else if (Math.abs(toCol - fromCol) == 1 && capturedPiece == null) {
                result += " e.p.";
            }
        }
        if (capturedPiece != null) {
            result += " x " + capturedPiece.name;
        }
        return result;
    }

    /**
     * Checks if this move is equal to another move.
     * Compares start and end positions, piece name, and piece color.
     *
     * @param move The move to compare against
     * @return true if moves are considered equal, false otherwise
     */
    public boolean equals(Move move) {
        return this.fromRow == move.fromRow && this.fromCol == move.fromCol &&
                this.toRow == move.toRow && this.toCol == move.toCol &&
                this.piece != null && this.piece.name.equals(move.piece.name) &&
                this.piece.color.equals(move.piece.color) &&
                this.capturedPiece == null && move.capturedPiece == null;
    }
}