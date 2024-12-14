class Bishop extends Piece {
    public Bishop(String color) {
        super("Bishop", color);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
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