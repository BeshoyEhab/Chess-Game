class Rook extends Piece {
    public Rook(String color) {
        super("Rook", color);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        // Basic rook movement logic
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (fromCol == toCol && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromRow == toRow && colDiff != 0) {
            // Check if the path is clear
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