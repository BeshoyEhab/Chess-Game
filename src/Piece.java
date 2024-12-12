import javax.swing.*;

// Abstract Piece class
abstract class Piece {
    protected String name;
    protected String color; // "White" or "Black"
    protected ImageIcon icon;
    protected boolean haveMove = false;
    protected static String basePath = "assets/";

    public Piece(String name, String color, String iconPath) {
        this.name = name;
        this.color = color;
        this.icon = new ImageIcon(iconPath);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void promote(Piece[][] board, int row, int col, String promoteTo){}

    public abstract boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move);

    // Initial setup of the chessboard
    public static Piece[][] getInitialSetup() {
        Piece[][] board = new Piece[8][8];

        board[0][0] = new Rook("Black", basePath + "BRook.png");
        board[0][1] = new Knight("Black", basePath + "BKnight.png");
        board[0][2] = new Bishop("Black", basePath + "BBishop.png");
        board[0][3] = new Queen("Black", basePath + "BQueen.png");
        board[0][4] = new King("Black", basePath + "BKing.png");
        board[0][5] = new Bishop("Black", basePath + "BBishop.png");
        board[0][6] = new Knight("Black", basePath + "BKnight.png");
        board[0][7] = new Rook("Black", basePath + "BRook.png");
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn("Black", basePath + "BPawn.png");
            board[6][i] = new Pawn("White", basePath + "WPawn.png");
        }
        board[7][0] = new Rook("White", basePath + "WRook.png");
        board[7][1] = new Knight("White", basePath + "WKnight.png");
        board[7][2] = new Bishop("White", basePath + "WBishop.png");
        board[7][3] = new Queen("White", basePath + "WQueen.png");
        board[7][4] = new King("White", basePath + "WKing.png");
        board[7][5] = new Bishop("White", basePath + "WBishop.png");
        board[7][6] = new Knight("White", basePath + "WKnight.png");
        board[7][7] = new Rook("White", basePath + "WRook.png");

        return board;
    }
}