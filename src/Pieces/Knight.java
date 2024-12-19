package Pieces;

/**
 * Represents a Knight piece in a chess game.
 * The Knight moves in an L-shape: two squares in one direction and one square perpendicular to that,
 * or one square in one direction and two squares perpendicular to that.
 */
public class Knight extends Piece {

    /**
     * Constructs a Knight with the specified color.
     *
     * @param color the color of the Knight ("White" or "Black").
     */
    public Knight(String color) {
        super("Knight", color);
    }

    /**
     * Determines if the Knight can move from its current position to a target position
     * on the chessboard according to chess rules.
     *
     * @param fromRow the starting row of the Knight.
     * @param fromCol the starting column of the Knight.
     * @param toRow   the target row for the Knight.
     * @param toCol   the target column for the Knight.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Knight movement logic
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
}
