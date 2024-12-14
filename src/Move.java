public class Move {
    public final int fromRow;
    public final int fromCol;
    public final int toRow;
    public final int toCol;
    public int[] timers;
    final Piece piece;
    final Piece capturedPiece;
    static int movesToStalemate = 0;
    boolean haveMoved;

    public Move(int fromRow, int fromCol, int toRow, int toCol, Piece piece, Piece capturedPiece, int timer1, int timer2) {
        this.fromRow = fromRow;
        this.fromCol = fromCol;
        this.toRow = toRow;
        this.toCol = toCol;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.haveMoved = piece.haveMove;
        this.timers = new int[]{timer1, timer2};
        if (piece.name.equals("Pawn") || piece.name.equals("King")) {
            movesToStalemate = 0;
        } else {
            movesToStalemate++;
        }
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

    public boolean equals(Move move) {
        return this.fromRow == move.fromRow && this.fromCol == move.fromCol && this.toRow == move.toRow && this.toCol == move.toCol && this.piece != null && this.piece.name.equals(move.piece.name) && this.piece.color.equals(move.piece.color);
    }
}
