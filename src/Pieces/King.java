package Pieces;

/**
 * Represents a King piece in a chess game.
 * The King can move one square in any direction and has the ability to castle under specific conditions.
 */
class King extends Piece {

    /**
     * Constructs a King with the specified color.
     *
     * @param color the color of the King ("White" or "Black").
     */
    public King(String color) {
        super("King", color);
    }

    /**
     * Determines if the King can move from its current position to a target position
     * on the chessboard according to chess rules, including castling.
     *
     * @param fromRow the starting row of the King.
     * @param fromCol the starting column of the King.
     * @param toRow   the target row for the King.
     * @param toCol   the target column for the King.
     * @param board   the current state of the chessboard represented as a 2D array of 
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // King moves one square in any direction
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = fromCol - toCol;

        // Normal move: one square in any direction
        if ((rowDiff <= 1 && Math.abs(colDiff) <= 1 && rowDiff + Math.abs(colDiff) != 0)) {
            return true;
        }

        // Queen-side castling
        if (!this.haveMove && board[fromRow][0] != null && board[fromRow][0].color.equals(this.color)
                && board[fromRow][0].name.equals("Rook") && !board[fromRow][0].haveMove
                && colDiff == 2 && rowDiff == 0) {
            for (int i = 1; i < 4; i++) {
                if (board[fromRow][i] != null) {
                    return false;
                }
            }
            return true;
        }

        // King-side castling
        if (!this.haveMove && board[fromRow][7] != null && board[fromRow][7].color.equals(this.color)
                && board[fromRow][7].name.equals("Rook") && !board[fromRow][7].haveMove
                && colDiff == -2 && rowDiff == 0) {
            for (int i = 1; i < 3; i++) {
                if (board[fromRow][7 - i] != null) {
                    return false;
                }
            }
            return true;
        }

        // If none of the conditions are met, the move is invalid
        return false;
    }
}
