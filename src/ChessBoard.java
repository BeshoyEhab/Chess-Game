import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class ChessBoard extends JFrame {
    public JPanel rightPanel = new JPanel(new GridLayout(4, 1));
    public String currentPlayer = "White";
    private int whiteTimeRemaining = 30 * 60 * 10; // 30 minutes in seconds * 10 for accuracy for White
    private int blackTimeRemaining = 30 * 60 * 10; // 30 minutes in seconds * 10 for accuracy for Black
    private Timer whiteTimer;
    private Timer blackTimer;
    private final JLabel whiteTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);
    private final JLabel blackTimerLabel = new JLabel("30:00.0", SwingConstants.CENTER);
    private final ArrayList<Move> moves = new ArrayList<>();

    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8
    private final JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private final Piece[][] boardState = Piece.getInitialSetup();
    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;
    private final Color color;
    private final int[] dims;
    private int[] whiteKingPosition = new int[]{7, 3};
    private int[] blackKingPosition = new int[]{0, 3};

    public ChessBoard(int[] dims, Color color) {
        moves.add(new Move(0, 0, 0, 0, null, null));
        this.color = color;
        this.dims = dims;
        setTitle("Chess Board");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

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
                JPanel square = getSquare(i, j);

                squares[i][j] = square;
                boardPanel.add(square);
            }
        }

        JPanel board = new JPanel();
        board.setLayout(new BorderLayout());
        board.setPreferredSize(new Dimension(dims[0], dims[1]));

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

    private JPanel getSquare(int i, int j) {
        JPanel square = new JPanel(new BorderLayout());
        square.setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);
        // Capture variables for listener

        // Add piece icon if present
        if (boardState[i][j] != null) {
            JLabel pieceLabel = new JLabel(boardState[i][j].getIcon());
            pieceLabel.setHorizontalAlignment(SwingConstants.CENTER);
            square.add(pieceLabel);
        }

        // Add mouse listener for movement
        square.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleSquareClick(i, j);
            }
        });
        return square;
    }

    private void addDot(int row, int col, Color color) {
        JPanel square = squares[row][col];

        square.setLayout(new BorderLayout());
        Dot dot = new Dot((dims[0] - 20) / 8 - 20, (dims[1] - 20) / 8 - 20, color);

        // Add the dot to the center of the square
        square.add(dot);
    }

    private Color calculateMidColor(Color color2) {
        int r = (Color.WHITE.getRed() + color2.getRed()) / 2;
        int g = (Color.WHITE.getGreen() + color2.getGreen()) / 2;
        int b = (Color.WHITE.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }

    private void removeDot(int row, int col) {
        JPanel square = squares[row][col];
        square.removeAll();
        if (boardState[row][col] != null) {
            square.add(new JLabel(boardState[row][col].getIcon()));
        }
    }

    private boolean underCheck(Piece[][] Pieces, String color) {
        int[] position = new int[2];
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (boardState[i][j] != null && boardState[i][j].name.equals("King") && boardState[i][j].color.equals(color)) {
                    position[0] = i;
                    position[1] = j;
                    if (color.equals("White")) {
                        whiteKingPosition = position;
                    } else {
                        blackKingPosition = position;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (Pieces[i][j] != null && !Pieces[i][j].color.equals(Pieces[position[0]][position[1]].color) && Pieces[i][j].canMove(i, j, position[0], position[1], Pieces, moves.getLast() != null ? moves.getLast() : new Move(position[0], position[1], i, j, Pieces[position[0]][position[1]], null))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkMate() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                for (int k = 0; k < BOARD_SIZE; k++) {
                    for (int l = 0; l < BOARD_SIZE; l++) {
                        if ((boardState[i][j] != null) && checkValidateMove(i, j, k, l)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void assistant() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (checkValidateMove(selectedRow, selectedCol, i, j)) {
                    if (boardState[i][j] == null) {
                        addDot(i, j, calculateMidColor(color));
                    } else {
                        squares[i][j].setBackground(calculateMidColor(Color.red));
                    }
                }
            }
        }
    }

    private void handleSquareClick(int row, int col) {
        clearHighlights();
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                removeDot(i, j);
            }
        }
        if (selectedPiece == null) {
            // Select a piece
            selectedPiece = boardState[row][col];
            if (selectedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer.equals(selectedPiece.color)) {
                    assistant();
                }
            }
        } else {
            // Attempt to move the selected piece
            if (checkValidateMove(selectedRow, selectedCol, row, col)) {
                movePiece(selectedRow, selectedCol, row, col);
                System.out.print(moves.size() % 2 == 0 ? (moves.size()+1)/2 + ") " + moves.getLast().toString() : "\t | " + moves.getLast().toString() + "\n");
                if (selectedPiece.name.equals("Pawn") && (row == 0 || row == 7)) {
                    selectedPiece.promote(boardState, row, col, "Queen");
                    JPanel square = squares[row][col];
                    square.removeAll();
                    square.add(new JLabel(boardState[row][col].getIcon()));
                }
                if (currentPlayer.equals("White")) {
                    whiteTimer.stop();
                    blackTimer.start();
                    currentPlayer = "Black";
                    rightPanel.setBackground(Color.BLACK);
                    blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
                    for (int i = 0; i < rightPanel.getComponentCount(); i++) {
                        rightPanel.getComponent(i).setForeground(Color.WHITE);
                    }
                } else {
                    blackTimer.stop();
                    whiteTimer.start();
                    currentPlayer = "White";
                    rightPanel.setBackground(Color.WHITE);
                    blackTimerLabel.setFont(new Font("Arial", Font.BOLD, 10));
                    whiteTimerLabel.setFont(new Font("Arial", Font.BOLD, 20));
                    for (int i = 0; i < rightPanel.getComponentCount(); i++) {
                        rightPanel.getComponent(i).setForeground(Color.BLACK);
                    }
                }
            }
            selectedPiece = boardState[row][col];
            if (selectedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer.equals(selectedPiece.color)) {
                    assistant();
                }
            } else {
                selectedRow = -1;
                selectedCol = -1;
            }
        }
        if (underCheck(boardState, "White")) {
            squares[whiteKingPosition[0]][whiteKingPosition[1]].setBackground(Color.RED);
        } else if (underCheck(boardState, "Black")) {
            squares[blackKingPosition[0]][blackKingPosition[1]].setBackground(Color.RED);
        }
        highlightSquare(row, col);
        repaint();
        revalidate();
    }

    private boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol) {
        boolean result = false;
        Piece selectedPiece = boardState[fromRow][fromCol];
        Piece piece = boardState[toRow][toCol];
        if (selectedPiece != null && currentPlayer.equals(selectedPiece.color) &&
                ((boardState[toRow][toCol] == null) || (boardState[toRow][toCol] != null &&
                !selectedPiece.color.equals(boardState[toRow][toCol].color))) &&
                selectedPiece.canMove(fromRow, fromCol, toRow, toCol, boardState, moves.getLast() != null ? moves.getLast() : new Move(fromRow, fromCol, toRow, toCol, selectedPiece, null))) {
            int dir = (toCol-fromCol > 0 ? 1 : -1);
            if (selectedPiece.name.equals("King") && (Math.abs(toCol - fromCol) > 1)) {
                if (underCheck(boardState, currentPlayer)) {
                    return false;
                }
                if (!checkValidateMove(fromRow, fromCol, toRow, toCol-dir)) {
                    return false;
                }
                fromCol += dir;
            }
            boardState[toRow][toCol] = boardState[fromRow][fromCol];
            boardState[fromRow][fromCol] = null;
            if (!underCheck(boardState, currentPlayer)) {
                result = true;
            }
            boardState[fromRow][fromCol] = boardState[toRow][toCol];
            boardState[toRow][toCol] = piece;
        }
        return result;
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boolean castling = false;
        moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[toRow][toCol]));
        
        int dir = (toCol-fromCol > 0 ? 1 : -1);
        if (boardState[fromRow][fromCol].name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
            castling = !underCheck(boardState, currentPlayer);
        } else if (boardState[fromRow][fromCol].name.equals("Pawn") && (Math.abs(toCol - fromCol) == 1) && boardState[toRow][toCol] == null) {
            moves.removeLast();
            Move move = moves.getLast();
            boardState[move.toRow][move.toCol] = null;
            squares[move.toRow][move.toCol].removeAll();
            moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], null));
        }

        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = null;
        boardState[toRow][toCol].haveMove = true;

        squares[toRow][toCol].removeAll();
        squares[toRow][toCol].add(new JLabel(boardState[toRow][toCol].getIcon()));
        squares[fromRow][fromCol].removeAll();

        if (castling) {
            if (dir > 0) {
                movePiece(fromRow, 7, toRow, 5);
            } else {
                movePiece(fromRow, 0, toRow, 3);
            }
            moves.removeLast();
        }
    }

    private void highlightSquare(int row, int col) {
        squares[row][col].setBackground(Color.YELLOW);
    }

    private void clearHighlights() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? Color.WHITE : this.color);
            }
        }
    }
}