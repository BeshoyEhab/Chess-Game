import javax.swing.*;
import java.awt.*;

/**
 * Represents a graphical chess board implementation using Java Swing.
 * Manages the visual board, timer functionality, and board state.
 */
public class ChessBoard extends JFrame {
    /** Panel containing timer and other right-side components. */
    public JPanel rightPanel = new JPanel(new GridLayout(6, 1));

    /** Remaining time for White player in deciseconds (1 decisecond = 0.1 seconds). */
    public int whiteTimeRemaining;

    /** Remaining time for Black player in deciseconds (1 decisecond = 0.1 seconds). */
    public int blackTimeRemaining;

    /** Timer for tracking White player's time. */
    public Timer whiteTimer;

    /** Timer for tracking Black player's time. */
    public Timer blackTimer;

    /** Panel displaying captured pieces for the white player. */
    public JPanel whiteCapturedPanel = new JPanel(new GridLayout(2, 8));

    /** Panel displaying captured pieces for the black player. */
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
     * Constructs a new ChessBoard with specified dimensions, color, and game duration.
     *
     * @param dims Array containing board width and height
     * @param color Background color for alternating squares
     * @param minutes Total game time for each player in minutes
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
        board.setLayout(new BorderLayout());
        board.setPreferredSize(new Dimension(dims[0], dims[1]));
        rowLabels.setPreferredSize(new Dimension(dims[0]/40, dims[1]));
        colLabels.setPreferredSize(new Dimension(dims[0], dims[1]/40));

        blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
        whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));

        rightPanel.add(blackCapturedPanel);
        rightPanel.add(blackTimerLabel);
        rightPanel.add(whiteTimerLabel);
        rightPanel.add(whiteCapturedPanel);

        // Initialize timers
        initTimers();
        board.add(rowLabels, BorderLayout.WEST);
        board.add(colLabels, BorderLayout.SOUTH);
        board.add(boardPanel, BorderLayout.CENTER);
        add(board, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Initializes game timers for White and Black players.
     * Sets up timer behavior, including countdown and time-out handling.
     */
    private void initTimers() {
        whiteTimer = new Timer(100, e -> {
            if (whiteTimeRemaining > 0) {
                whiteTimeRemaining--;
                updateTimerLabel(whiteTimerLabel, whiteTimeRemaining);
            } else {
                ((Timer) e.getSource()).stop(); // Stop the timer when time runs out
                JOptionPane.showMessageDialog(this, "White player is out of time!");
            }
        });

        blackTimer = new Timer(100, e -> {
            if (blackTimeRemaining > 0) {
                blackTimeRemaining--;
                updateTimerLabel(blackTimerLabel, blackTimeRemaining);
            } else {
                ((Timer) e.getSource()).stop(); // Stop the timer when time runs out
                JOptionPane.showMessageDialog(this, "Black player is out of time!");
            }
        });

        whiteTimer.start(); // White player starts first
    }

    /**
     * Updates a timer label with formatted time display.
     *
     * @param label Label to be updated
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
     *
     * @param i Row index of the square
     * @param j Column index of the square
     * @return A JPanel representing a chess board square
     */
    private JPanel setSquare(int i, int j) {
        JPanel square = new JPanel(new BorderLayout());
        square.setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);

        return square;
    }

    /**
     * Retrieves a specific square from the chess board.
     *
     * @param i Row index of the square
     * @param j Column index of the square
     * @return JPanel representing the requested square
     */
    public JPanel getSquare(int i, int j) {
        return squares[i][j];
    }

    /**
     * Adds a dot marker to a specific board square.
     *
     * @param row Row index of the square
     * @param col Column index of the square
     * @param color Color of the dot
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
     *
     * @param fromRow Source row index
     * @param fromCol Source column index
     * @param toRow Destination row index
     * @param toCol Destination column index
     * @param icon Piece's icon to be displayed
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
