import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

/**
 * Represents a chess game application with a graphical user interface.
 * Handles the chess game logic, board rendering, player interactions, and game states like checkmate and stalemate.
 */
public class Game extends JFrame{

    /**
     * The first player in the chess game. Contains player details like name and color.
     */
    private final Player player1 = new Player();

    /**
     * The second player in the chess game. Contains player details like name and color.
     */
    private final Player player2 = new Player();

    /**
     * Duration of the chess timer for each player in minutes.
     */
    private int timerDuration = 10;

    /**
     * The currently selected chess piece, or null if no piece is selected.
     */
    private Piece selectedPiece = null;

    /**
     * The row and column indices of the currently selected chess piece.
     */
    private int selectedRow = -1, selectedCol = -1;

    /**
     * Current position of the white king on the chessboard, stored as a row and column index.
     */
    private int[] whiteKingPosition = new int[]{7, 3};

    /**
     * Current position of the black king on the chessboard, stored as a row and column index.
     */
    private int[] blackKingPosition = new int[]{0, 3};

    /**
     * A list of all moves made in the game, stored as Move objects.
     */
    private final ArrayList<Move> moves = new ArrayList<>();

    /**
     * Two-dimensional array representing the current state of the chessboard.
     * Stores Piece objects for each square on the board.
     */
    private final Piece[][] boardState = Piece.getInitialSetup();

    /**
     * The player whose turn it is to make a move.
     */
    private Player currentPlayer = player1;

    /**
     * The primary color used for one set of chessboard squares in the UI.
     */
    private final Color color = Color.LIGHT_GRAY;

    /**
     * Represents the graphical user interface for the chessboard and handles interactions.
     */
    public ChessBoard board = new ChessBoard(new int[]{800, 800}, color, timerDuration);
        public static void main(String[] args) {
            new Game().startGame();
        }

    /**
     * Updates the icons (piece images) on the chessboard based on the current board state.
     */
    private void updateIcons() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (boardState[i][j] != null)
                        board.addSquare(i, j, boardState[i][j].getIcon());
                }
            }
        }

    /**
     * Continuously monitors the game for checkmate or stalemate conditions.
     * If either condition is met, displays an appropriate message and provides restart options.
     */
    private void checkForCheckmate() {
            new Thread(() -> {
                while (true) {
                    if (isCheckmate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                        EventQueue.invokeLater(() -> {
                            Path path = Paths.get("lastSave.txt");
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            for (int i = 0; i < moves.size(); i++)
                                moves.removeLast();
                            board.whiteTimer.stop();
                            board.blackTimer.stop();
                            JOptionPane.showMessageDialog(this, (currentPlayer.getColor().equals("White") ? "Black" : "White") + " has won!");

                            int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION);
                            boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                            if (restartChoice) {
                                board.dispose();
                                initGame();
                            } else {
                                System.exit(0);
                            }
                        });
                        break;
                    } else if (isStalemate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                        EventQueue.invokeLater(() -> {
                            Path path = Paths.get("lastSave.txt");
                            try {
                                Files.deleteIfExists(path);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            for (int i = 0; i < moves.size(); i++)
                                moves.removeLast();
                            board.whiteTimer.stop();
                            board.blackTimer.stop();
                            JOptionPane.showMessageDialog(this, "It's stalemate!");
                            int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION);
                            boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                            if (restartChoice) {
                                board.dispose();
                                initGame();
                            } else {
                                System.exit(0);
                            }
                        });
                    }
                    try {
                        //noinspection BusyWait
                        Thread.sleep(100); // Checkmate check interval
                    } catch (InterruptedException e) {
                        //noinspection CallToPrintStackTrace
                        e.printStackTrace();
                    }
                }
            }).start();
        }

    /**
     * Starts the chess game. Checks for any saved games and prompts the user to continue from the last save or begin a new game.
     */
    private void startGame() {
            Path path = Paths.get("lastSave.txt");
            if (Files.exists(path)) {
                int choice = JOptionPane.showConfirmDialog(this, "A saved game was found. Do you want to continue?", "Continue From Last Game", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    restoreMovesFromFile();
                } else {
                    initializeNewGame();
                }
            } else {
                initializeNewGame();
            }
            initGame();
        }

    /**
     * Initializes a new chess game. Prompts the players for their names, colors, and timer duration.
     */
    private void initializeNewGame() {
            // Prompt for player details
            player1.setName(JOptionPane.showInputDialog(this, "Player 1 name:"));
            if (player1.getName() == null || player1.getName().trim().isEmpty()) {
                player1.setName("Player 1");
            }

            player2.setName(JOptionPane.showInputDialog(this, "Player 2 name:"));
            if (player2.getName() == null || player2.getName().trim().isEmpty()) {
                player2.setName("Player 2");
            }

            Object[] options = {"White", "Black"};
            player1.setColor((String) JOptionPane.showInputDialog(this, player1.getName() + " color:", "Color Selection",
                    JOptionPane.PLAIN_MESSAGE, null, options, "White"));

            if (player1.getColor() == null) {
                player1.setColor("White"); // Default to White if no selection
            }

            player2.setColor(player1.getColor().equals("White") ? "Black" : "White");

            // Prompt for timer duration
            String timerInput = JOptionPane.showInputDialog(this, "Enter timer duration in minutes (default 10):");
            try {
                timerDuration = Integer.parseInt(timerInput);
                if (timerDuration <= 0 || timerDuration > 60) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                timerDuration = 10; // Default timer duration
            }

            currentPlayer = player1;
        }

    /**
     * Initializes the chessboard and user interface for the game.
     * Sets up event listeners and player information panels.
     */
    private void initGame() {
            EventQueue.invokeLater(() -> {
                // Initialize the chessboard
                board = new ChessBoard(new int[]{800, 800}, color, timerDuration);
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
                JLabel player1NameLabel = new JLabel(player1.getName(), SwingConstants.CENTER);
                player1NameLabel.setFont(new Font("Arial", Font.BOLD, 20));
                JLabel player2NameLabel = new JLabel(player2.getName(), SwingConstants.CENTER);
                player2NameLabel.setFont(new Font("Arial", Font.BOLD, 20));

                if ("White".equals(player1.getColor())) {
                    board.rightPanel.add(player2NameLabel, 0);
                    board.rightPanel.add(player1NameLabel);
                } else {
                    board.rightPanel.add(player1NameLabel, 0);
                    board.rightPanel.add(player2NameLabel);
                }

                board.switchTimers(currentPlayer.getColor().equals("White") ? "Black" : "White");
                checkForCheckmate();
            });
        }

    /**
     * Sets up mouse click listeners for each square on the chessboard to handle piece movements.
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
     * Manages piece selection, movement, highlighting, and verifies game states like check.
     *
     * @param row The row index of the clicked square.
     * @param col The column index of the clicked square.
     */
    private void handleClick(int row, int col) {
            board.clearHighlights();
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    board.removeDot(i, j, boardState[i][j]);
                }
            }
            if (selectedPiece == null) {
                // Select a piece
                selectedPiece = boardState[row][col];
                if (selectedPiece != null) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer.getColor().equals(selectedPiece.color)) {
                        assistant();
                    }
                }
            } else {
                // Attempt to move the selected piece
                if (checkValidateMove(selectedRow, selectedCol, row, col)) {
                    movePiece(selectedRow, selectedCol, row, col);
                    System.out.print(moves.size() % 2 == 1 ? (moves.size()+1)/2 + ") " + moves.getLast().toString() : "\t | " + moves.getLast().toString() + "\n");
                    if (selectedPiece.name.equals("Pawn") && (row == 0 || row == 7)) {
                        selectedPiece.promote(boardState, row, col, "Queen");
                        board.removeSquare(row, col);
                        board.addSquare(row, col, boardState[row][col].getIcon());
                    }
                    board.switchTimers(currentPlayer.getColor());
                    currentPlayer = currentPlayer.getColor().equals(player1.getColor()) ? player2 : player1;
                }
                selectedPiece = boardState[row][col];
                if (selectedPiece != null) {
                    selectedRow = row;
                    selectedCol = col;
                    if (currentPlayer.getColor().equals(selectedPiece.color)) {
                        assistant();
                    }
                } else {
                    selectedRow = -1;
                    selectedCol = -1;
                }
            }
            if (underCheck(boardState, "White")) {
                board.highlightSquare(whiteKingPosition[0], whiteKingPosition[1], Color.RED);
            } else if (underCheck(boardState, "Black")) {
                board.highlightSquare(blackKingPosition[0], blackKingPosition[1], Color.RED);
            }

            board.highlightSquare(row, col, Color.YELLOW);
            board.repaint();
            board.revalidate();
        }

    /**
     * Updates the positions of the white and black kings based on the current board state.
     */
    private void updateKingPosition() {
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (boardState[i][j] != null && boardState[i][j].name.equals("King")) {
                        if (boardState[i][j].color.equals("White")) {
                            whiteKingPosition = new int[]{i, j};
                        } else {
                            blackKingPosition = new int[]{i, j};
                        }
                        break;
                    }
                }
            }
        }

    /**
     * Checks whether the king of the specified color is in check.
     *
     * @param Pieces The current state of the chessboard as a 2D array of pieces.
     * @param color The color of the king to check ("White" or "Black").
     * @return True if the specified king is in check; false otherwise.
     */
    private boolean underCheck(Piece[][] Pieces, String color) {
            updateKingPosition();
            int[] position = color.equals("White") ? whiteKingPosition : blackKingPosition;
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (Pieces[i][j] != null && !Pieces[i][j].color.equals(Pieces[position[0]][position[1]].color) && Pieces[i][j].canMove(i, j, position[0], position[1], Pieces,(!moves.isEmpty() && moves.getLast() != null) ? moves.getLast() : new Move(position[0], position[1], i, j, Pieces[position[0]][position[1]], null, timerDuration, timerDuration))) {
                        return true;
                    }
                }
            }
            return false;
        }

    /**
     * Checks if the same board position has occurred three times, indicating a potential draw by repetition.
     *
     * @return True if threefold repetition is detected; false otherwise.
     */
    private boolean isThreefoldRepetition() {
            if (moves.size() < 11) return false;
            return moves.getLast().equals(moves.get(moves.size() - 5)) && moves.getLast().equals(moves.get(moves.size() - 9))
                    && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 6)) && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 10));
        }

    /**
     * Determines if the game is in a checkmate state, where the current player has no legal moves and their king is in check.
     *
     * @return True if checkmate is detected; false otherwise.
     */
    public boolean isCheckmate() {
            // Enhanced checkmate detection
            if (!underCheck(boardState, currentPlayer.getColor())) return false;

            return playerCantMove();
        }

    /**
     * Determines if the game is in a stalemate state where the current player has no legal moves but is not in check.
     *
     * @return True if stalemate is detected; false otherwise.
     */
    public boolean isStalemate() {
            if (Move.movesToStalemate >= 50 || isThreefoldRepetition()) return true;
            // Stalemate occurs when a player has no legal moves but is not in check
            if (underCheck(boardState, currentPlayer.getColor())) return false;

            return playerCantMove();
        }

    /**
     * Checks if the current player has any legal moves available.
     *
     * @return True if the player cannot make any legal moves; false otherwise.
     */
    private boolean playerCantMove() {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Highlights valid moves for the currently selected chess piece.
     * Highlights valid target squares and attackable squares based on piece type and rules.
     */
    private void assistant() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkValidateMove(selectedRow, selectedCol, i, j)) {
                    if (boardState[i][j] == null) {
                        board.addDot(i, j, calculateMidColor(color));
                    } else {
                        board.highlightSquare(i, j, calculateMidColor(Color.red));
                    }
                }
            }
        }
    }
    /**
     * Validates whether a move is legal for the current player according to chess rules.
     *
     * @param fromRow The starting row of the piece.
     * @param fromCol The starting column of the piece.
     * @param toRow   The target row for the piece.
     * @param toCol   The target column for the piece.
     * @return True if the move is valid; false otherwise.
     */
    private boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol) {
        boolean result = false;
        Piece selectedPiece = boardState[fromRow][fromCol];
        Piece piece = boardState[toRow][toCol];
        if (selectedPiece != null && currentPlayer.getColor().equals(selectedPiece.color) &&
                ((boardState[toRow][toCol] == null) || (boardState[toRow][toCol] != null &&
                        !selectedPiece.color.equals(boardState[toRow][toCol].color))) &&
                selectedPiece.canMove(fromRow, fromCol, toRow, toCol, boardState, (!moves.isEmpty() && moves.getLast() != null) ? moves.getLast() : new Move(fromRow, fromCol, toRow, toCol, selectedPiece, null, board.whiteTimeRemaining, board.blackTimeRemaining))) {
            int dir = (toCol-fromCol > 0 ? 1 : -1);
            if (selectedPiece.name.equals("King") && (Math.abs(toCol - fromCol) > 1)) {
                if (underCheck(boardState, currentPlayer.getColor())) {
                    return false;
                }
                if (!checkValidateMove(fromRow, fromCol, toRow, toCol-dir)) {
                    return false;
                }
                fromCol += dir;
            }
            boardState[toRow][toCol] = boardState[fromRow][fromCol];
            boardState[fromRow][fromCol] = null;
            if (!underCheck(boardState, currentPlayer.getColor())) {
                result = true;
            }
            boardState[fromRow][fromCol] = boardState[toRow][toCol];
            boardState[toRow][toCol] = piece;
        }
        return result;
    }

    /**
     * Moves a piece from one square to another, performs castling if applicable,
     * and saves the move history to file.
     *
     * @param fromRow The starting row of the piece.
     * @param fromCol The starting column of the piece.
     * @param toRow   The target row for the piece.
     * @param toCol   The target column for the piece.
     */
    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boolean castling = false;
        moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[toRow][toCol], board.whiteTimeRemaining, board.blackTimeRemaining));

        int dir = (toCol - fromCol > 0 ? 1 : -1);
        if (boardState[fromRow][fromCol].name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
            castling = !underCheck(boardState, currentPlayer.getColor());
        } else if (boardState[fromRow][fromCol].name.equals("Pawn") && (Math.abs(toCol - fromCol) == 1) && boardState[toRow][toCol] == null) {
            moves.removeLast();
            Move move = moves.getLast();
            boardState[move.toRow][move.toCol] = null;
            board.removeSquare(move.toRow, move.toCol);
            moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[fromRow][toCol], board.whiteTimeRemaining, board.blackTimeRemaining));
        }
        if (boardState[toRow][toCol] != null)
            currentPlayer.capturedPieces.add(boardState[toRow][toCol]);

        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = null;
        boardState[toRow][toCol].haveMove = true;

        board.moveSquare(fromRow, fromCol, toRow, toCol, boardState[toRow][toCol].getIcon());

        if (castling) {
            if (dir > 0) {
                movePiece(fromRow, 7, toRow, 5);
            } else {
                movePiece(fromRow, 0, toRow, 3);
            }
            moves.removeLast();
        }
        saveMovesToFile();
    }

    /**
     * Calculates a midway color between white and the given color.
     *
     * @param color2 The second color in the calculation.
     * @return The calculated middle color.
     */
    private Color calculateMidColor(Color color2) {
        int r = (Color.WHITE.getRed() + color2.getRed()) / 2;
        int g = (Color.WHITE.getGreen() + color2.getGreen()) / 2;
        int b = (Color.WHITE.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }

    /**
     * Reverts the last move made in the game, restoring the previous board state.
     * Handles undoing special moves such as castling and en passant.
     */
    private void undo() {
        board.clearHighlights();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.removeDot(i, j, boardState[i][j]);
            }
        }
        if (!moves.isEmpty()) {
            Move move = moves.removeLast();

            boardState[move.fromRow][move.fromCol] = move.piece;
            boardState[move.fromRow][move.fromCol].haveMove = move.haveMoved;
            board.moveSquare(move.toRow, move.toCol, move.fromRow, move.fromCol, move.piece.getIcon());
            boardState[move.toRow][move.toCol] = move.capturedPiece;

            if (move.capturedPiece != null) {
                board.addSquare(move.toRow, move.toCol, move.capturedPiece.getIcon());
                currentPlayer.capturedPieces.removeLast();
                System.out.println(currentPlayer.capturedPieces);
            }

            if (move.piece.name.equals("Pawn") && (Math.abs(move.toCol - move.fromCol) == 1) && move.capturedPiece == null) {
                boardState[move.fromRow][move.toCol] = new Pawn(move.piece.color.equals("White") ? "Black" : "White");
                boardState[move.fromRow][move.toCol].haveMove = true;
                board.addSquare(move.fromRow, move.toCol, boardState[move.fromRow][move.toCol].getIcon());
            } else if (move.piece.name.equals("King")) {
                if (move.toCol - move.fromCol == 2) {
                    movePiece(move.fromRow, 5, move.fromRow, 7);
                } else if (move.toCol - move.fromCol == -2) {
                    movePiece(move.fromRow, 3, move.fromRow, 0);
                }
                moves.removeLast();
            }

            board.switchTimers(currentPlayer.getColor());
            currentPlayer = currentPlayer.getColor().equals(player1.getColor()) ? player2 : player1;
            board.whiteTimeRemaining = moves.isEmpty() ? timerDuration*600 :moves.getLast().timers[0];
            board.blackTimeRemaining = moves.isEmpty() ? timerDuration*600 :moves.getLast().timers[1];
            board.repaint();
            board.revalidate();
            saveMovesToFile();
        }
    }

    /**
     * Loads the saved game moves from a file and restores the chessboard state.
     */
    private void restoreMovesFromFile() {
        Path path = Paths.get("lastSave.txt");
        try {
            java.util.List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                int fromRow = Integer.parseInt(parts[0].trim());
                int fromCol = Integer.parseInt(parts[1].trim());
                int toRow = Integer.parseInt(parts[2].trim());
                int toCol = Integer.parseInt(parts[3].trim());
                int whiteTime = Integer.parseInt(parts[4].trim());
                int blackTime = Integer.parseInt(parts[5].trim());
                moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[toRow][toCol], whiteTime, blackTime));
                boardState[toRow][toCol] = boardState[fromRow][fromCol];
                boardState[fromRow][fromCol] = null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        readPlayers();
        if (!moves.isEmpty()) {
            Move lastMove = moves.getLast();
            board.whiteTimeRemaining = lastMove.timers[0];
            board.blackTimeRemaining = lastMove.timers[1];
            currentPlayer = moves.size() % 2 == 0 ? player1 : player2;
        }
    }
    /**
     * Saves the current game state and moves to a file for later restoration.
     */
    private void saveMovesToFile() {
        try {
            new FileWriter("lastSave.txt", false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Move move : moves) {
            try (FileWriter writer = new FileWriter("lastSave.txt", true)) {
                writer.write( move.fromRow + "," + move.fromCol + "," + move.toRow + "," + move.toCol + "," + move.timers[0] + "," + move.timers[1] + "\n");
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
        savePlayers();
    }

    /**
     * Saves the details of both players, including their names and colors, to a file.
     */
    private void savePlayers() {
        try (FileWriter writer = new FileWriter("players.txt", false)) {
            writer.write(player1.getName() + "," + player1.getColor() + "," + timerDuration + "\n");
            writer.write(player2.getName() + "," + player2.getColor() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads player information (name, color, and timer duration) from a file.
     */
    private void readPlayers() {
        Path path = Paths.get("players.txt");
        try {
            java.util.List<String> lines = Files.readAllLines(path);
            String[] parts = lines.getFirst().split(",");
            player1.setName(parts[0].trim());
            player1.setColor(parts[1].trim());
            timerDuration = Integer.parseInt(parts[2].trim());
            parts = lines.get(1).split(",");
            player2.setName(parts[0].trim());
            player2.setColor(parts[1].trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
