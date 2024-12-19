package Pieces;

import Utilities.*;

import javax.swing.*;
import java.io.File;

/// Represents an abstract base class for chess pieces in a chess game.
/// This class provides common properties and methods for all chess pieces,
/// including name, color, icon representation, and movement validation.
/// Each chess piece type (Pawn, Rook, Knight, etc.) will extend this abstract class
/// and implement its own movement rules.
///
/// @author Team 57
/// @version 1.0
public abstract class Piece {
    /**
     * The name of the chess piece (e.g., "Pawn", "Rook", "King").
     */
    public String name;

    /**
     * The color of the chess piece, either "White" or "Black".
     */
    public String color;

    /**
     * The visual icon representing the piece, loaded from an image file.
     */
    public ImageIcon icon;

    /**
     * Flag to indicate if the piece has moved during the game.
     * Useful for special moves like castling or pawn's first move.
     */
    public boolean haveMove = false;

    /**
     * Base path for loading piece icons, constructed using the user's directory.
     */
    protected static String basePath = "assets" + File.separator;

    /// Constructor for creating a chess piece with a name and color.
    /// Initializes the piece's name, color, and loads its corresponding icon image.
    /// The icon is loaded from a file named with the first character of the color
    /// and the piece's name (e.g., "BPawn.png" for a Black Pawn).
    ///
    /// @param name The name of the chess piece
    /// @param color The color of the piece ("White" or "Black")
    public Piece(String name, String color) {
        this.name = name;
        this.color = color;
        this.icon = new ImageIcon(basePath + color.charAt(0) + name +".png");
    }

    /**
     * Returns the visual icon representing the chess piece.
     *
     * @return The ImageIcon for the piece
     */
    public ImageIcon getIcon() {
        return icon;
    }

    /// Abstract method to validate if a piece can move from one position to another.
    /// Each specific piece type (Pawn, Rook, Knight, etc.) must implement its own
    /// movement rules by overriding this method.
    ///
    /// @param fromRow The starting row of the move
    /// @param fromCol The starting column of the move
    /// @param toRow The destination row of the move
    /// @param toCol The destination column of the move
    /// @param board The current state of the chessboard
    /// @return true if the move is valid, false otherwise

    public abstract boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);

    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, Move move) {
        return this.canMove(fromRow, fromCol, toRow, toCol, board);
    }

    /// Creates and returns the initial setup of a standard chess board.
    /// Positions all pieces in their starting locations for a new chess game:
    /// - Black pieces on rows 0 and 1
    /// - White pieces on rows 6 and 7
    ///
    /// @return A 2D array representing the initial chess board configuration
    public static Piece[][] getInitialSetup() {
        Piece[][] board = new Piece[8][8];

        board[0][0] = new Rook("Black");
        board[0][1] = new Knight("Black");
        board[0][2] = new Bishop("Black");
        board[0][3] = new Queen("Black");
        board[0][4] = new King("Black");
        board[0][5] = new Bishop("Black");
        board[0][6] = new Knight("Black");
        board[0][7] = new Rook("Black");

        for (int i = 0; i < 8; i++) {
            board[1][i] = new Pawn("Black");
            board[6][i] = new Pawn("White");
        }

        board[7][0] = new Rook("White");
        board[7][1] = new Knight("White");
        board[7][2] = new Bishop("White");
        board[7][3] = new Queen("White");
        board[7][4] = new King("White");
        board[7][5] = new Bishop("White");
        board[7][6] = new Knight("White");
        board[7][7] = new Rook("White");

        return board;
    }
}