package Utilities;

import Pieces.Piece;

import javax.swing.*;
import java.awt.*;

/// A graphical chess board implementation using Java Swing that manages
/// the visual board, player timers, and game state.
///
/// This class provides a comprehensive chess board interface with features including:
///
///  - 8x8 grid representation of the chess board
///
///  - Player time tracking with countdown timers
///
///  - Captured pieces display
///
///  - Visual board manipulation methods
///
/// @author Team 57
/// @version 1.0
/// @since 2024-12-15
public class ChessBoard extends JFrame {
    /**
     * Panel for displaying right-side game components like timers and captured pieces.
     * Uses a GridLayout with 6 rows to organize components vertically.
     */
    public JPanel rightPanel = new JPanel(new GridLayout(6, 1));

    /**
     * Tracks the remaining time for the White player in deciseconds.
     * One decisecond represents 0.1 seconds, allowing precise time tracking.
     */
    public int whiteTimeRemaining;

    /**
     * Tracks the remaining time for the Black player in deciseconds.
     * One decisecond represents 0.1 seconds, allowing precise time tracking.
     */
    public int blackTimeRemaining;

    /**
     * Swing Timer for tracking and updating the White player's remaining time.
     * Updates every decisecond and handles time-out scenarios.
     */
    public Timer whiteTimer;

    /**
     * Swing Timer for tracking and updating the Black player's remaining time.
     * Updates every decisecond and handles time-out scenarios.
     */
    public Timer blackTimer;

    /**
     * Panel for displaying pieces captured by the White player.
     * Uses a 2x8 GridLayout to represent captured pieces.
     */
    public JPanel whiteCapturedPanel = new JPanel(new GridLayout(2, 8));

    /**
     * Panel for displaying pieces captured by the Black player.
     * Uses a 2x8 GridLayout to represent captured pieces.
     */
    public JPanel blackCapturedPanel = new JPanel(new GridLayout(2, 8));

    /** Label displaying White player's remaining time. */
    private final JLabel whiteTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);

    /** Label displaying Black player's remaining time. */
    private final JLabel blackTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);

    /** Standard chess board size (8x8). */
    private static final int BOARD_SIZE = 8;

    /** 2D array representing the chess board squares. */
    private final JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];

    /** Background color of the board squares. */
    private final Color color;

    /** Dimensions of the board. */
    private final int[] dims;

    /**
     * Constructs a new ChessBoard with custom configuration.
     *
     * @param dims Array containing board width and height in pixels
     * @param color Background color for alternating board squares
     * @param minutes Total game time allocated for each player in minutes
     */
    public ChessBoard(int[] dims, Color color, int minutes) {
        this.color = color;
        this.dims = dims;
        this.whiteTimeRemaining = minutes * 600;
        this.blackTimeRemaining = minutes * 600;
        updateTimerLabel(whiteTimerLabel, whiteTimeRemaining);
        updateTimerLabel(blackTimerLabel, blackTimeRemaining);

        setTitle("Chess Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add row labels on the west
        JPanel rowLabels = new JPanel(new GridLayout(BOARD_SIZE, 1));
        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
            JLabel label = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            rowLabels.add(label);
        }

        // Add column labels on the bottom
        JPanel colLabels = new JPanel(new GridLayout(1, BOARD_SIZE));
        for (int i = 0; i < BOARD_SIZE; i++) {
            JLabel label = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            colLabels.add(label);
        }

        // Initialize the grid with pieces
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JPanel square = setSquare(i, j);

                squares[i][j] = square;
                boardPanel.add(square);
            }
        }

        JPanel board = new JPanel();
        JPanel space = new JPanel();
        JPanel downIndexes = new JPanel();
        board.setLayout(new BorderLayout());
        downIndexes.setLayout(new BoxLayout(downIndexes, BoxLayout.X_AXIS));
        board.setPreferredSize(new Dimension(dims[0], dims[1]));
        rowLabels.setPreferredSize(new Dimension(dims[0]/40, dims[1]));
        colLabels.setPreferredSize(new Dimension(dims[0], dims[1]/40));
        space.setPreferredSize(new Dimension(dims[0]/40, dims[1]/40));

        downIndexes.add(space);
        downIndexes.add(colLabels);

        blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
        whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        rightPanel.add(blackCapturedPanel);
        rightPanel.add(blackTimerLabel);
        rightPanel.add(whiteTimerLabel);
        rightPanel.add(whiteCapturedPanel);

        // Initialize timers
        initTimers();
        board.add(boardPanel, BorderLayout.CENTER);
        board.add(rowLabels, BorderLayout.WEST);
        board.add(downIndexes, BorderLayout.SOUTH);
        add(board, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    /// Initializes game timers for both players with countdown and time-out logic.
    /// The White player's timer starts first by default.
    /// Timer behavior:
    /// - Countdown occurs every decisecond
    /// - Displays a message when a player runs out of time
    /// - Stops the corresponding player's timer upon time exhaustion
    private void initTimers() {
        whiteTimer = new Timer(100, e -> {
            if (whiteTimeRemaining > 0) {
                whiteTimeRemaining--;
                updateTimerLabel(whiteTimerLabel, whiteTimeRemaining);
            } else {
                ((Timer) e.getSource()).stop(); // Stop the timer when time runs out
            }
        });

        blackTimer = new Timer(100, e -> {
            if (blackTimeRemaining > 0) {
                blackTimeRemaining--;
                updateTimerLabel(blackTimerLabel, blackTimeRemaining);
            } else {
                ((Timer) e.getSource()).stop(); // Stop the timer when time runs out
            }
        });

        whiteTimer.start(); // White player starts first
    }

    /**
     * Updates a timer label with a formatted time display.
     * Converts deciseconds into a minutes:seconds.tenths format.
     *
     * @param label JLabel to be updated with the current time
     * @param timeInSeconds Remaining time in deciseconds
     */
    private void updateTimerLabel(JLabel label, int timeInSeconds) {
        int minutes = timeInSeconds / 600;
        int seconds = (timeInSeconds % 600) / 10;
        int hundredths = timeInSeconds % 10;

        label.setText(String.format("%02d:%02d.%d", minutes, seconds, hundredths));
    }

    /**
     * Creates a chess board square with alternating colors.
     * Squares are colored white or the specified custom color based on their position.
     *
     * @param i Row index of the square
     * @param j Column index of the square
     * @return A JPanel representing a chess board square with appropriate background
     */
    private JPanel setSquare(int i, int j) {
        JPanel square = new JPanel(new BorderLayout());
        square.setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);

        return square;
    }

    /**
     * Retrieves a specific square from the chess board grid.
     *
     * @param i Row index of the desired square
     * @param j Column index of the desired square
     * @return JPanel representing the requested board square
     * @throws IndexOutOfBoundsException if indices are outside the board's range
     */
    public JPanel getSquare(int i, int j) {
        return squares[i][j];
    }

    /**
     * Adds a dot marker to a specified board square.
     * Useful for highlighting possible moves or indicating special squares.
     *
     * @param row Row index of the target square
     * @param col Column index of the target square
     * @param color Color of the dot to be displayed
     */
    public void addDot(int row, int col, Color color) {
        squares[row][col].setLayout(new BorderLayout());
        Dot dot = new Dot((dims[0] - 20) / 8 - 20, (dims[1] - 20) / 8 - 20, color);

        // Add the dot to the center of the square
        squares[row][col].add(dot);
    }

    /**
     * Removes a dot from a square and optionally replaces it with a piece.
     *
     * @param row Row index of the square
     * @param col Column index of the square
     * @param piece Piece to be placed after dot removal (can be null)
     */
    public void removeDot(int row, int col, Piece piece) {
        removeSquare(row, col);
        if (piece != null) {
            addSquare(row, col, piece.getIcon());
        }
    }

    /**
     * Removes all components from a specific square.
     *
     * @param row Row index of the square
     * @param col Column index of the square
     */
    public void removeSquare(int row, int col) {
        squares[row][col].removeAll();
    }

    /**
     * Moves a piece from one square to another on the board.
     * Removes the piece from the source square and places it in the destination square.
     *
     * @param fromRow Source square's row index
     * @param fromCol Source square's column index
     * @param toRow Destination square's row index
     * @param toCol Destination square's column index
     * @param icon ImageIcon representing the piece being moved
     */
    public void moveSquare(int fromRow, int fromCol, int toRow, int toCol, ImageIcon icon) {
        removeSquare(toRow, toCol);
        addSquare(toRow, toCol, icon);
        removeSquare(fromRow, fromCol);
    }

    /**
     * Adds a piece to a specific square on the board.
     *
     * @param row Row index of the square
     * @param col Column index of the square
     * @param icon Piece's icon to be displayed
     */
    public void addSquare(int row, int col, ImageIcon icon) {
        JLabel pieceLabel = new JLabel(icon);
        pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        squares[row][col].add(pieceLabel);
        squares[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : this.color);
    }

    /**
     * Highlights a specific square with a given color.
     *
     * @param row Row index of the square
     * @param col Column index of the square
     * @param color Highlight color
     */
    public void highlightSquare(int row, int col, Color color) {
        squares[row][col].setBackground(color);
    }

    /**
     * Removes all square highlights, resetting to the original board color pattern.
     */
    public void clearHighlights() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);
            }
        }
    }

    /**
     * Switches active timers between White and Black players.
     * Also updates the visual representation to indicate the current player's turn.
     *
     * @param color Color of the player whose turn is ending
     */
    public void switchTimers(String color) {
        if (color.equals("White")) {
            whiteTimer.stop();
            blackTimer.start();
            rightPanel.setBackground(Color.BLACK);
            blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
            for (int i = 0; i < rightPanel.getComponentCount(); i++) {
                rightPanel.getComponent(i).setForeground(Color.WHITE);
            }
        } else {
            blackTimer.stop();
            whiteTimer.start();
            rightPanel.setBackground(Color.WHITE);
            blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
            whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
            for (int i = 0; i < rightPanel.getComponentCount(); i++) {
                rightPanel.getComponent(i).setForeground(Color.BLACK);
            }
        }
    }
}
