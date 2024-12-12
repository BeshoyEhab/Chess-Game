public class Move {
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;
    final Piece piece;
    final Piece capturedPiece;

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece, Piece capturedPiece) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
    }

    @Override
    public String toString() {
        String resutl = piece.name + "(" + (char) ('A' + fromCol) + (8 - fromRow) + "->" + (char) ('A' + toCol) + (8 - toRow) + ")" ;
        if (piece.name.equals("King")) {
            if (toCol - fromCol == 2) {
                resutl = "O-O";
            } else if (toCol - fromCol == -2) {
                resutl = "O-O-O";
            }
        } else if (piece.name.equals("Pawn")) {
            if (toRow == 0 || toRow == 7) {
                resutl += " = Queen";
            } else if (Math.abs(toCol - fromCol) == 1 && capturedPiece == null) {
                resutl += " e.p.";
            }
        }
        if (capturedPiece != null) {
            resutl += " x " + capturedPiece.name;
        }
        return resutl;
    }
}
