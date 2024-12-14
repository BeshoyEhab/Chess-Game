import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame {
    public JPanel rightPanel = new JPanel(new GridLayout(4, 1));
    public int whiteTimeRemaining; // 30 minutes in seconds * 10 for accuracy for White
    public int blackTimeRemaining; // 30 minutes in seconds * 10 for accuracy for Black
    public Timer whiteTimer;
    public Timer blackTimer;
    private final JLabel whiteTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);
    private final JLabel blackTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);
    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8

    private final JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private final Color color;
    private final int[] dims;

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

        rightPanel.add(blackTimerLabel);
        rightPanel.add(whiteTimerLabel);

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

    private void updateTimerLabel(JLabel label, int timeInSeconds) {
        int minutes = timeInSeconds / 600;
        int seconds = (timeInSeconds % 600) / 10;
        int hundredths = timeInSeconds % 10;

        label.setText(String.format("%02d:%02d.%d", minutes, seconds, hundredths));
    }

    private JPanel setSquare(int i, int j) {
        JPanel square = new JPanel(new BorderLayout());
        square.setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);

        return square;
    }

    public JPanel getSquare(int i, int j) {
        return squares[i][j];
    }

    public void addDot(int row, int col, Color color) {
        squares[row][col].setLayout(new BorderLayout());
        Dot dot = new Dot((dims[0] - 20) / 8 - 20, (dims[1] - 20) / 8 - 20, color);

        // Add the dot to the center of the square
        squares[row][col].add(dot);
    }

    public void removeDot(int row, int col, Piece piece) {
        removeSquare(row, col);
        if (piece != null) {
            addSquare(row, col, piece.getIcon());
        }
    }

    public void removeSquare(int row, int col) {
        squares[row][col].removeAll();
    }

    public void moveSquare(int fromRow, int fromCol, int toRow, int toCol, ImageIcon icon) {
        removeSquare(toRow, toCol);
        addSquare(toRow, toCol, icon);
        removeSquare(fromRow, fromCol);
    }

    public void addSquare(int row, int col, ImageIcon icon) {
        JLabel pieceLabel = new JLabel(icon);
        pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        squares[row][col].add(pieceLabel);
        squares[row][col].setBackground((row + col) % 2 == 0 ? Color.WHITE : this.color);
    }

    public void highlightSquare(int row, int col, Color color) {
        squares[row][col].setBackground(color);
    }

    public void clearHighlights() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);
            }
        }
    }

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