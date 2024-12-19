package Pieces;

/**
 * Represents a Queen piece in a chess game.
 * The Queen combines the movements of both the Rook and the Bishop,
 * allowing it to move any number of squares either in a straight line or diagonally,
 * as long as the path is clear.
 */
public class Queen extends Piece {

    /**
     * Constructs a Queen with the specified color.
     *
     * @param color the color of the Queen ("White" or "Black").
     */
    public Queen(String color) {
        super("Queen", color);
    }

    /**
     * Determines if the Queen can move from its current position to a target position
     * on the chessboard according to chess rules.
     *
     * @param fromRow the starting row of the Queen.
     * @param fromCol the starting column of the Queen.
     * @param toRow   the target row for the Queen.
     * @param toCol   the target column for the Queen.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Queen combines Rook and Bishop moves
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff == colDiff && rowDiff != 0) {
            // Diagonal movement: Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromCol == toCol && rowDiff != 0) {
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
