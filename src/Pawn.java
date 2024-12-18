/**
 * Represents a Pawn piece in a chess game.
 * A Pawn has unique movement rules, including forward movement, capturing diagonally,
 * an optional two-square move on its first move, and en passant capture.
 * It can also be promoted to another piece upon reaching the opposite end of the board.
 */
class Pawn extends Piece {

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
     * @param board   the current state of the chessboard represented as a 2D array of Pieces.
     * @return true if the move is valid, false otherwise.
     */
    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Basic pawn movement logic
        int direction = color.equals("White") ? -1 : 1;

        // First move only: Pawn can move two steps
        return (toCol == fromCol && board[toRow][toCol] == null && toRow - fromRow == direction) // Common move
                || (toRow - fromRow == direction && Math.abs(toCol - fromCol) == 1 && board[toRow][toCol] != null) // Capturing diagonally
                || (toRow - fromRow == 2 * direction && fromCol == toCol && !this.haveMove
                && board[fromRow + direction][toCol] == null && board[fromRow + 2 * direction][toCol] == null); // Special two-step move
    }

    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        return this.canMove(fromRow, fromCol, toRow, toCol, board) || (move.piece != null && move.piece.name.equals("Pawn") && board[toRow][toCol] == null && move.toRow - fromRow == 0
                && toCol == move.fromCol && toRow - fromRow == (color.equals("White") ? -1 : 1) && Math.abs(move.fromRow - move.toRow) == 2); //En passant
    }

    /**
     * Promotes the Pawn to another piece when it reaches the opposite end of the board.
     * The new piece can be a Rook, Bishop, Knight, or Queen.
     *
     * @param board     the current state of the chessboard represented as a 2D array of Pieces.
     * @param row       the row of the Pawn to be promoted.
     * @param col       the column of the Pawn to be promoted.
     * @param promoteTo the type of piece to promote the Pawn to ("Rook", "Bishop", "Knight", or "Queen").
     */
    public void promote(Piece[][] board, int row, int col, String promoteTo) {
        if (row == 0 || row == 7) {
            Piece piece;
            piece = switch (promoteTo) {
                case "Rook" -> new Rook(this.color);
                case "Bishop" -> new Bishop(this.color);
                case "Knight" -> new Knight(this.color);
                case "Queen" -> new Queen(this.color);
                default -> null;
            };
            assert piece != null;
            piece.haveMove = true;
            board[row][col] = piece;
        }
    }
}
