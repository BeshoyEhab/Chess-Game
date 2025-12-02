package GameManager;

import AI.*;
import Pieces.*;
import Utilities.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.*;

/// Represents a complete chess game application with a graphical user
/// interface. This class manages the entire chess game lifecycle, including: -
/// Utilities.Player management and initialization - Board state tracking -
/// Utilities.Move validation and execution - GameManager.Game state detection
/// (checkmate, stalemate) - Timer management - Saving and restoring game
/// progress
///
/// The game supports features such as: - Custom player names and colors -
/// Configurable game timer - Pieces.Piece movement and capture - Special moves
/// (castling, en passant, pawn promotion) - Undo functionality -
/// GameManager.Game state persistence
///
/// @author Team 57
/// @version 1.0
/// @since 2024-12-15
public class Game extends JFrame {

    public GameLogic gameLogic = new GameLogic();

    /// Defines the duration of the game timer for each player in minutes. Default
    /// value is 10 minutes. Can be configured during game initialization.
    private int timerDuration = 10;

    /**
     * Tracks the currently selected chess piece during user interaction.
     * Will be null if no piece is currently selected.
     */
    private Piece selectedPiece = null;

    /**
     * Stores the row and column indices of the currently selected chess piece.
     * Default values are -1 when no piece is selected.
     */
    private int selectedRow = -1, selectedCol = -1;

    /**
     * Defines the primary color used for one set of chessboard squares in the UI.
     * Defaults to light gray to provide visual contrast.
     */
    private final Color color = new Color(118, 150, 86);

    private AIPlayer ai;

    /**
     * Manages the graphical user interface for the chessboard and handles user
     * interactions.
     * Initialized with custom board dimensions, color, and timer duration.
     */
    public ChessBoard board = new ChessBoard(new int[] { 800, 800 }, color, timerDuration);

    public Game() {
    }

    /**
     * Main entry point for the chess game application.
     * Launches a new game instance.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        new Game().startGame();
    }

    /**
     * Updates the icons (piece images) on the chessboard based on the current board
     * state.
     */
    private void updateIcons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameLogic.boardState[i][j] != null) {
                    board.addSquare(i, j, gameLogic.boardState[i][j].getIcon());
                }
            }
        }
    }

    /**
     * Continuously monitors the game for checkmate or stalemate conditions.
     * If either condition is met, displays an appropriate message and provides
     * restart options.
     */
    private void isGameOver() {
        new Thread(() -> {
            while (true) {
                if (gameLogic.isCheckmate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                    EventQueue.invokeLater(() -> {
                        Path path = Paths.get("lastSave.txt");
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        gameLogic.moves.clear();
                        board.whiteTimer.stop();
                        board.blackTimer.stop();
                        JOptionPane.showMessageDialog(this,
                                (gameLogic.currentPlayer.getColor().equals("White") ? "Black" : "White") + " has won!");

                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?",
                                "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            board.dispose();
                            resetGame();
                        } else {
                            System.exit(0);
                        }
                    });
                    break;
                } else if (gameLogic.isStalemate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                    EventQueue.invokeLater(() -> {
                        Path path = Paths.get("lastSave.txt");
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        gameLogic.moves.clear();
                        board.whiteTimer.stop();
                        board.blackTimer.stop();
                        JOptionPane.showMessageDialog(this, "It's stalemate!");
                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?",
                                "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            board.dispose();
                            resetGame();
                        } else {
                            System.exit(0);
                        }
                    });
                    break;
                } else if (board.whiteTimeRemaining <= 0 || board.blackTimeRemaining <= 0) {
                    EventQueue.invokeLater(() -> {
                        if (ai != null)
                            ai.cancel(true);
                        Path path = Paths.get("lastSave.txt");
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        gameLogic.moves.clear();
                        board.whiteTimer.stop();
                        board.blackTimer.stop();
                        JOptionPane.showMessageDialog(this, gameLogic.currentPlayer.getColor() + " is out of time.");
                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?",
                                "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            board.dispose();
                            resetGame();
                        } else {
                            System.exit(0);
                        }
                    });
                    break;
                }
                try {
                    // noinspection BusyWait
                    Thread.sleep(100); // Checkmate check interval
                } catch (InterruptedException e) {
                    // noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Starts the chess game. Checks for any saved games and prompts the user to
     * continue from the last save or begin a new game.
     */
    private void startGame() {
        Path path = Paths.get("lastSave.txt");
        if (Files.exists(path)) {
            int choice = JOptionPane.showConfirmDialog(this, "A saved game was found. Do you want to continue?",
                    "Continue From Last Game", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                gameLogic.restoreMovesFromFile();
                timerDuration = gameLogic.timerDuration;
            } else {
                initializeNewGame();
            }
        } else {
            initializeNewGame();
        }
        initGame();
        if (!gameLogic.moves.isEmpty()) {
            Move lastMove = gameLogic.moves.getLast();
            board.whiteTimeRemaining = lastMove.timers[0];
            board.blackTimeRemaining = lastMove.timers[1];
            board.updateWhiteTimer(board.whiteTimeRemaining);
            board.updateBlackTimer(board.blackTimeRemaining);
            updateCapturedPiecesPanel();
            board.setLastMove(lastMove.fromRow, lastMove.fromCol, lastMove.toRow, lastMove.toCol);
            board.clearHighlights();
            highlightCheck();
            board.repaint();
        }
    }

    /**
     * Initializes a new chess game. Prompts the players for their names, colors,
     * and timer duration.
     */
    private void initializeNewGame() {
        // Prompt for player details
        String[] opts = { "AI", "1vs1" };
        String playAI = (String) JOptionPane.showInputDialog(this, "What do you want with?", "Play With",
                JOptionPane.QUESTION_MESSAGE, null, opts, opts[0]);
        if (playAI == null || playAI.equals("AI")) {
            playAI = "AI";
            gameLogic.player2 = new AI_Minimax();
            gameLogic.player2.setName("AI MiniMax");
        }

        gameLogic.player1.setName(JOptionPane.showInputDialog(this, "Player 1 name:"));
        if (gameLogic.player1.getName() == null || gameLogic.player1.getName().trim().isEmpty()) {
            gameLogic.player1.setName("Player 1");
        }

        if (!playAI.equals("AI")) {
            gameLogic.player2.setName(JOptionPane.showInputDialog(this, "Player 2 name:"));
            if (gameLogic.player2.getName() == null || gameLogic.player2.getName().trim().isEmpty()) {
                gameLogic.player2.setName("Player 2");
            }
        }

        Object[] options = { "White", "Black" };
        gameLogic.player1.setColor(
                (String) JOptionPane.showInputDialog(this, gameLogic.player1.getName() + " color:", "Color Selection",
                        JOptionPane.PLAIN_MESSAGE, null, options, "White"));

        if (gameLogic.player1.getColor() == null) {
            gameLogic.player1.setColor("White"); // Default to White if no selection
        }

        gameLogic.player2.setColor(gameLogic.player1.getColor().equals("White") ? "Black" : "White");
        if (gameLogic.player2.getColor().equals("White")) {
            String aiColor = gameLogic.currentPlayer.getColor();
            ai = new AIPlayer(gameLogic.currentPlayer.getColor().equals("White"), this, aiColor);
            ai.execute();
        }

        // Prompt for timer duration
        String timerInput = JOptionPane.showInputDialog(this, "Enter timer duration in minutes (default 10):");
        try {
            timerDuration = Integer.parseInt(timerInput);
            if (timerDuration <= 0 || timerDuration > 60)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            timerDuration = 10; // Default timer duration
        }
        gameLogic.timerDuration = timerDuration;

        gameLogic.currentPlayer = gameLogic.player1.getColor().equals("White") ? gameLogic.player1 : gameLogic.player2;
        if (gameLogic.currentPlayer instanceof AI_Minimax) {
            ai = new AIPlayer(true, this, "White");
            ai.execute();
        }
    }

    /**
     * Initializes the chessboard and user interface for the game.
     * Sets up event listeners and player information panels.
     */
    private void initGame() {
        EventQueue.invokeLater(() -> {
            // Initialize the chessboard
            board = new ChessBoard(new int[] { 800, 800 }, color, timerDuration);
            updateIcons();
            setCH();
            board.setVisible(true);

            // Add KeyListener for undo functionality
            board.addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_U) {
                        undo();
                    }
                }
            });
            board.setFocusable(true);
            board.requestFocusInWindow();

            // Add player name based on color
            // Add player name based on color
            if ("White".equals(gameLogic.player1.getColor())) {
                board.setPlayerNames(gameLogic.player1.getName(), gameLogic.player2.getName());
            } else {
                board.setPlayerNames(gameLogic.player2.getName(), gameLogic.player1.getName());
            }

            board.switchTimers(gameLogic.currentPlayer.getColor().equals("White") ? "Black" : "White");

            board.addUndoListener(e -> undo());
            board.addRestartListener(e -> {
                int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?",
                        "Restart Game", JOptionPane.YES_NO_OPTION);
                if (restartOption == JOptionPane.YES_OPTION) {
                    board.dispose();
                    resetGame();
                }
            });

            board.enableDragAndDrop(new ChessBoard.MoveListener() {
                @Override
                public void onMove(int fromRow, int fromCol, int toRow, int toCol) {
                    handleDragMove(fromRow, fromCol, toRow, toCol);
                }

                @Override
                public void onDragStart(int row, int col) {
                    Piece p = gameLogic.boardState[row][col];
                    if (p != null && gameLogic.currentPlayer.getColor().equals(p.color)) {
                        assistant(row, col);
                        board.highlightSquare(row, col, Color.YELLOW);
                        board.repaint();
                    }
                }

                @Override
                public void onDragEnd() {
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            board.removeDot(i, j, gameLogic.boardState[i][j]);
                        }
                    }
                }
            });
            isGameOver();
        });
    }

    /**
     * Sets up mouse click listeners for each square on the chessboard to handle
     * piece movements.
     */
    private void setCH() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int finalI = i;
                int finalJ = j;
                board.getSquare(i, j).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(finalI, finalJ);
                    }
                });
            }
        }
    }

    /**
     * Handles the logic when a chessboard square is clicked.
     * Manages piece selection, movement, highlighting, and verifies game states
     * like check.
     *
     * @param row The row index of the clicked square.
     * @param col The column index of the clicked square.
     */
    private void handleClick(int row, int col) {
        board.clearHighlights();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.removeDot(i, j, gameLogic.boardState[i][j]);
            }
        }
        if (!(gameLogic.currentPlayer instanceof AI_Minimax)) {
            if (selectedPiece == null) {
                // Select a piece
                selectedPiece = gameLogic.boardState[row][col];
                if (selectedPiece != null) {
                    selectedRow = row;
                    selectedCol = col;
                    if (gameLogic.currentPlayer.getColor().equals(selectedPiece.color)) {
                        assistant();
                    }
                }
            } else {
                // Attempt to move the selected piece
                if (gameLogic.checkValidateMove(selectedRow, selectedCol, row, col, board.whiteTimeRemaining,
                        board.blackTimeRemaining)) {
                    movePiece(selectedRow, selectedCol, row, col);
                }
                selectedPiece = gameLogic.boardState[row][col];
                if (selectedPiece != null) {
                    selectedRow = row;
                    selectedCol = col;
                    if (gameLogic.currentPlayer.getColor().equals(selectedPiece.color)) {
                        assistant();
                    }
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
        }
        if (gameLogic.currentPlayer instanceof AI_Minimax && !AIPlayer.isThinking()) {
            String aiColor = gameLogic.currentPlayer.getColor();
            ai = new AIPlayer(gameLogic.currentPlayer.getColor().equals("White"), this, aiColor);
            ai.execute();
        }

        highlightCheck();

        board.highlightSquare(row, col, Color.YELLOW);
        board.repaint();
        board.revalidate();
    }

    private void handleDragMove(int fromRow, int fromCol, int toRow, int toCol) {
        if (gameLogic.checkValidateMove(fromRow, fromCol, toRow, toCol, board.whiteTimeRemaining,
                board.blackTimeRemaining)) {
            movePiece(fromRow, fromCol, toRow, toCol);

            if (gameLogic.currentPlayer instanceof AI_Minimax && !AIPlayer.isThinking()) {
                String aiColor = gameLogic.currentPlayer.getColor();
                ai = new AIPlayer(gameLogic.currentPlayer.getColor().equals("White"), this, aiColor);
                ai.execute();
            }

            board.clearHighlights();
            highlightCheck();
            board.repaint();
            board.revalidate();
        } else {
            board.repaint();
        }
    }

    /**
     * Shows a dialog to the player to choose a piece to promote the pawn to.
     * 
     * @param row Row of the pawn that needs promotion.
     * @param col Column of the pawn that needs promotion.
     */
    private void showPromotionDialog(int row, int col) {
        Object[] options = { "Queen", "Rook", "Bishop", "Knight" };
        String selectedOption = (String) JOptionPane.showInputDialog(
                this,
                "Choose a piece to promote your pawn to:",
                "Pawn Promotion",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (selectedOption != null) {
            switch (selectedOption) {
                case "Queen":
                    gameLogic.boardState[row][col] = new Queen(gameLogic.boardState[row][col].color);
                    break;
                case "Rook":
                    gameLogic.boardState[row][col] = new Rook(gameLogic.boardState[row][col].color);
                    break;
                case "Bishop":
                    gameLogic.boardState[row][col] = new Bishop(gameLogic.boardState[row][col].color);
                    break;
                case "Knight":
                    gameLogic.boardState[row][col] = new Knight(gameLogic.boardState[row][col].color);
                    break;
            }
            gameLogic.moves.getLast().promoteTo = selectedOption;
        } else {
            gameLogic.boardState[row][col] = new Queen(gameLogic.boardState[row][col].color);
            gameLogic.moves.getLast().promoteTo = "Queen";
        }
        board.removeSquare(row, col);
        board.addSquare(row, col, gameLogic.boardState[row][col].getIcon());
    }

    public void highlightCheck() {
        if (gameLogic.underCheck(gameLogic.boardState, "White")) {
            board.highlightSquare(gameLogic.getWhiteKingPosition()[0], gameLogic.getWhiteKingPosition()[1], Color.RED);
        } else if (gameLogic.underCheck(gameLogic.boardState, "Black")) {
            board.highlightSquare(gameLogic.getBlackKingPosition()[0], gameLogic.getBlackKingPosition()[1], Color.RED);
        }
    }

    /**
     * Highlights valid moves for the currently selected chess piece.
     * Highlights valid target squares and attackable squares based on piece type
     * and rules.
     */
    private void assistant() {
        assistant(selectedRow, selectedCol);
    }

    private void assistant(int row, int col) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (gameLogic.checkValidateMove(row, col, i, j, board.whiteTimeRemaining,
                        board.blackTimeRemaining)) {
                    if (gameLogic.boardState[i][j] == null) {
                        board.addDot(i, j, calculateMidColor(Color.WHITE, color));
                    } else {
                        board.highlightSquare(i, j, calculateMidColor(Color.WHITE, Color.red));
                    }
                }
            }
        }
    }

    /**
     * Moves a piece from one square to another, performs castling if applicable,
     * and saves the move history to file.
     * * @param fromRow The starting row of the piece.
     * 
     * @param fromCol The starting column of the piece.
     * @param toRow   The target row for the piece.
     * @param toCol   The target column for the piece.
     */
    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boolean castling = false;
        gameLogic.moves.add(new Move(fromRow, fromCol, toRow, toCol, gameLogic.boardState[fromRow][fromCol],
                gameLogic.boardState[toRow][toCol],
                board.whiteTimeRemaining, board.blackTimeRemaining));

        int dir = (toCol - fromCol > 0 ? 1 : -1);
        if (gameLogic.boardState[fromRow][fromCol].name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
            castling = !gameLogic.underCheck(gameLogic.boardState, gameLogic.currentPlayer.getColor());
        } else if (gameLogic.boardState[fromRow][fromCol].name.equals("Pawn") && (Math.abs(toCol - fromCol) == 1)
                && gameLogic.boardState[toRow][toCol] == null) {
            gameLogic.currentPlayer.capturedPieces.add(gameLogic.boardState[fromRow][toCol]);
            gameLogic.moves.removeLast();
            Move move = gameLogic.moves.getLast();
            gameLogic.boardState[move.toRow][move.toCol] = null;
            board.removeSquare(move.toRow, move.toCol);
            gameLogic.moves.add(new Move(fromRow, fromCol, toRow, toCol, gameLogic.boardState[fromRow][fromCol],
                    gameLogic.boardState[fromRow][toCol],
                    board.whiteTimeRemaining, board.blackTimeRemaining));
        } else if (gameLogic.boardState[toRow][toCol] != null) {
            gameLogic.currentPlayer.capturedPieces.add(gameLogic.boardState[toRow][toCol]);
        }
        updateCapturedPiecesPanel();

        gameLogic.boardState[toRow][toCol] = gameLogic.boardState[fromRow][fromCol];
        gameLogic.boardState[fromRow][fromCol] = null;
        gameLogic.boardState[toRow][toCol].haveMove = true;

        board.moveSquare(fromRow, fromCol, toRow, toCol, gameLogic.boardState[toRow][toCol].getIcon());

        if (castling) {
            if (dir > 0) {
                movePiece(fromRow, 7, toRow, 5);
            } else {
                movePiece(fromRow, 0, toRow, 3);
            }
            gameLogic.moves.removeLast();
        }

        if (gameLogic.boardState[toRow][toCol].name.equals("Pawn")) {
            if ((toRow == 0 && gameLogic.boardState[toRow][toCol].color.equals("White"))
                    || (toRow == 7 && gameLogic.boardState[toRow][toCol].color.equals("Black"))) {
                showPromotionDialog(toRow, toCol);
                gameLogic.movesToStalemate = 0;
            } else {
                gameLogic.movesToStalemate = gameLogic.updateStalemateCounter(gameLogic.boardState[toRow][toCol]);
            }
        } else {
            gameLogic.movesToStalemate = gameLogic.updateStalemateCounter(gameLogic.boardState[toRow][toCol]);
        }

        System.out.print(
                gameLogic.moves.size() % 2 == 1 ? (gameLogic.moves.size() + 1) / 2 + ") " + gameLogic.moves.getLast()
                        : "\t | " + gameLogic.moves.getLast() + "\n");
        board.switchTimers(gameLogic.currentPlayer.getColor());
        gameLogic.currentPlayer = gameLogic.currentPlayer.getColor().equals(gameLogic.player1.getColor())
                ? gameLogic.player2
                : gameLogic.player1;

        board.setLastMove(fromRow, fromCol, toRow, toCol);
        gameLogic.saveMovesToFile();
    }

    /**
     * Calculates a midway color between white and the given color.
     *
     * @param color2 The second color in the calculation.
     * @return The calculated middle color.
     */
    private Color calculateMidColor(Color color1, Color color2) {
        int r = (color1.getRed() + color2.getRed()) / 2;
        int g = (color1.getGreen() + color2.getGreen()) / 2;
        int b = (color1.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }

    /**
     * Reverts the last move made in the game, restoring the previous board state.
     * Handles undoing special moves such as castling and en passant.
     */
    /**
     * Reverts the last move made in the game, restoring the previous board state.
     * Handles undoing special moves such as castling and en passant.
     */
    private void undo() {
        board.clearHighlights();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.removeDot(i, j, gameLogic.boardState[i][j]);
            }
        }
        Move move = gameLogic.undo();
        if (move != null) {
            board.moveSquare(move.toRow, move.toCol, move.fromRow, move.fromCol, move.piece.getIcon());

            if (move.piece.name.equals("Pawn") && (Math.abs(move.toCol - move.fromCol) == 1)
                    && move.capturedPiece == null) {
                board.addSquare(move.fromRow, move.toCol, gameLogic.boardState[move.fromRow][move.toCol].getIcon());
            } else if (move.piece.name.equals("King")) {
                if (move.toCol - move.fromCol == 2) {
                    board.moveSquare(move.fromRow, 5, move.fromRow, 7, gameLogic.boardState[move.fromRow][7].getIcon());
                } else if (move.toCol - move.fromCol == -2) {
                    board.moveSquare(move.toRow, 3, move.fromRow, 0, gameLogic.boardState[move.fromRow][0].getIcon());
                }
            }

            if (move.capturedPiece != null) {
                board.addSquare(move.toRow, move.toCol, move.capturedPiece.getIcon());
            } else {
                board.removeSquare(move.toRow, move.toCol);
            }

            updateCapturedPiecesPanel();
            board.switchTimers(gameLogic.currentPlayer.getColor());
            board.updateWhiteTimer(move.timers[0]);
            board.updateBlackTimer(move.timers[1]);

            if (gameLogic.currentPlayer instanceof AI_Minimax) {
                undo();
            }

            if (!gameLogic.moves.isEmpty()) {
                Move lastMove = gameLogic.moves.getLast();
                board.setLastMove(lastMove.fromRow, lastMove.fromCol, lastMove.toRow, lastMove.toCol);
            } else {
                board.setLastMove(-1, -1, -1, -1); // Clear highlight
            }
        }
        board.repaint();
        board.revalidate();
    }

    /**
     * Updates the display of captured pieces in the right panel.
     */
    private void updateCapturedPiecesPanel() {
        board.whiteCapturedPanel.removeAll();
        for (Piece piece : gameLogic.player1.capturedPieces) {
            JLabel pieceIcon = new JLabel(
                    new ImageIcon(piece.getIcon().getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            board.whiteCapturedPanel.add(pieceIcon);
        }

        board.blackCapturedPanel.removeAll();
        for (Piece piece : gameLogic.player2.capturedPieces) {
            JLabel pieceIcon = new JLabel(
                    new ImageIcon(piece.getIcon().getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH)));
            board.blackCapturedPanel.add(pieceIcon);
        }

        // Repaint the right panel to reflect changes
        board.rightPanel.revalidate();
        board.rightPanel.repaint();
    }

    /**
     * Resets the game board and state to its initial configuration.
     */
    private void resetGame() {
        board.removeAll();
        gameLogic.reset();
        initGame();
    }
}
