package Pieces;

/**
 * Represents a Rook piece in a chess game.
 * The Rook moves any number of squares along a rank (row) or file (column),
 * as long as the path is clear.
 */
public class Rook extends Piece {

    /**
     * Constructs a Rook with the specified color.
     *
     * @param color the color of the Rook ("White" or "Black").
     */
    public Rook(String color) {
        super("Rook", color);
    }

    /**
     * Determines if the Rook can move from its current position to a target position
     * on the chessboard according to chess rules.
     *
     * @param fromRow the starting row of the Rook.
     * @param fromCol the starting column of the Rook.
     * @param toRow   the target row for the Rook.
     * @param toCol   the target column for the Rook.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Basic rook movement logic
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (fromCol == toCol && rowDiff != 0) {
            // Vertical movement: Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromRow == toRow && colDiff != 0) {
            // Horizontal movement: Check if the path is clear
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < colDiff; i++) {
                if (board[toRow][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
