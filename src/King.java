class King extends Piece {
    public King(String color) {
        super("King", color);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        // King moves one square in any direction
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = fromCol - toCol;

        if ((rowDiff <= 1 && Math.abs(colDiff) <= 1 && rowDiff+Math.abs(colDiff) != 0)) { //Normal move
            return true;
        }
        if (!this.haveMove && board[fromRow][0] != null && board[fromRow][0].color.equals(this.color) && board[fromRow][0].name.equals("Rook") && !board[fromRow][0].haveMove && colDiff == 2 && rowDiff == 0) { //Queen side Castling
            for (int i = 1; i < 4; i++) {
                if (board[fromRow][i] != null) {
                    return false;
                }
            }
            return true;
        }
        if (!this.haveMove && board[fromRow][7] != null && board[fromRow][7].color.equals(this.color) && board[fromRow][7].name.equals("Rook") && !board[fromRow][7].haveMove && colDiff == -2 && rowDiff == 0) { //King side Castling
            for (int i = 1; i < 3; i++) {
                if (board[fromRow][7 - i] != null) {
                    System.out.println(board[fromRow][7 - i].name);
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}