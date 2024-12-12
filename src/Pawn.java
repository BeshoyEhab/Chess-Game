class Pawn extends Piece {
    public Pawn(String color, String iconPath) {
        super("Pawn", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        // Basic pawn movement logic
        int direction = color.equals("White") ? -1 : 1;

        //First move only in pawn can be 2 steps
        return (toCol == fromCol && board[toRow][toCol] == null && toRow - fromRow == direction) //Common move
                || (toRow - fromRow == direction && Math.abs(toCol - fromCol) == 1 && board[toRow][toCol] != null) //Eating the piece
                || (toRow - fromRow == 2 * direction && fromCol == toCol && !this.haveMove && board[fromRow + direction][toCol] == null && board[fromRow + 2 * direction][toCol] == null) //Special move in the first move only
                || (move.piece != null && move.piece.name.equals("Pawn") && board[toRow][toCol] == null && move.toRow-fromRow == 0 && toCol == move.fromCol && toRow-fromRow == direction && Math.abs(move.fromRow - move.toRow) == 2); //En passing
    }

    public void promote(Piece[][] board, int row, int col, String promoteTo) {
        if (row == 0 || row == 7) {
            Piece piece;
            String path = basePath + (this.color.equals("White") ? "W" : "B") + promoteTo + ".png";
            piece = switch (promoteTo) {
                case "Rook" -> new Rook(this.color, path);
                case "Bishop" -> new Bishop(this.color, path);
                case "Knight" -> new Knight(this.color, path);
                case "Queen" -> new Queen(this.color, path);
                default -> null;
            };
            assert piece != null;
            piece.haveMove = true;
            board[row][col] = piece;
        }
    }
}