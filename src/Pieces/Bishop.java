package Pieces;

/**
 * Represents a Bishop piece in a chess game.
 * The Bishop moves diagonally any number of squares, as long as the path is clear.
 */
public class Bishop extends Piece {

    /**
     * Constructs a Bishop with the specified color.
     *
     * @param color the color of the Bishop ("White" or "Black").
     */
    public Bishop(String color) {
        super("Bishop", color);
    }

    /**
     * Determines if the Bishop can move from its current position to a target position
     * on the chessboard according to chess rules.
     *
     * @param fromRow the starting row of the Bishop.
     * @param fromCol the starting column of the Bishop.
     * @param toRow   the target row for the Bishop.
     * @param toCol   the target column for the Bishop.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Bishop moves diagonally: |rowDiff| == |colDiff|
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff == colDiff && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
