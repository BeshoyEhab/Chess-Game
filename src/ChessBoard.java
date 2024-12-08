//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//
//public class ChessBoard extends JFrame {
//    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8
//    private JPanel gridPanel;
//    private Color color;
//    private int[] dims;
//    private Piece[][] boardState = Piece.getInitialSetup();
//    private Piece selectedPiece;
//    private int selectedRow=-1, selectedCol=-1;
//
//    public ChessBoard(int[] dims, Color color) {
//        this.color = color;
//        this.dims = dims;
//        setTitle("Chess Board");
//        setSize(this.dims[0], this.dims[1]);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setResizable(false);
//
//        JPanel boardPanel = new JPanel();
//        boardPanel.setLayout(new BorderLayout());
//
//        gridPanel = getJPanel(this.color); // Initialize grid panel
//
//        // Add row labels on the left
//        JPanel rowLabels = new JPanel(new GridLayout(BOARD_SIZE, 1));
//        for (int i = BOARD_SIZE - 1; i >= 0; i--) {
//            JLabel label = new JLabel(String.valueOf(i + 1), SwingConstants.CENTER);
//            label.setFont(new Font("Arial", Font.BOLD, 16));
//            rowLabels.add(label);
//        }
//
//        // Add column labels on the bottom
//        JPanel colLabels = new JPanel(new GridLayout(1, BOARD_SIZE));
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            JLabel label = new JLabel(String.valueOf((char) ('A' + i)), SwingConstants.CENTER);
//            label.setFont(new Font("Arial", Font.BOLD, 16));
//            colLabels.add(label);
//        }
//
//        boardPanel.add(rowLabels, BorderLayout.WEST);
//        boardPanel.add(gridPanel, BorderLayout.CENTER);
//        boardPanel.add(colLabels, BorderLayout.SOUTH);
//
//        add(boardPanel);
//    }
//
//    private JPanel getJPanel(Color color) {
//        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
//        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//
//        // Add chessboard squares
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            for (int j = 0; j < BOARD_SIZE; j++) {
//                JPanel square = getJPanel(color, i, j);
//
//                gridPanel.add(square);
//            }
//        }
//
//        return gridPanel;
//    }
//
//    private JPanel getJPanel(Color color, int i, int j) {
//        JPanel square = new JPanel();
//        if ((i + j) % 2 == 0) {
//            square.setBackground(Color.WHITE);
//        } else {
//            square.setBackground(color);
//        }
//
//        int finalI = i;
//        int finalJ = j;
//        square.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                handleSquareClick(finalI, finalJ);
//            }
//        });
//        return square;
//    }
//
//    private JPanel getJPanel(JLabel icon, int i, int j) {
//        JPanel square = new JPanel();
//        if ((i + j) % 2 == 0) {
//            square.setBackground(Color.WHITE);
//        } else {
//            square.setBackground(color);
//        }
//        square.add(icon);
//
//        int finalI = i;
//        int finalJ = j;
//        square.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                handleSquareClick(finalI, finalJ);
//            }
//        });
//        return square;
//    }
//
//    private void addDot(int row, int col) {
//        // Convert chessboard coordinates to array indices
//        // Rows are reversed in the layout
//        int rowIndex = BOARD_SIZE - 1 - row;
//
//        Component component = gridPanel.getComponent(col + BOARD_SIZE * rowIndex);
//
//        if (component instanceof JPanel square) {
//            // Debug: Print square background color
//            System.out.println("Square Background Color: " + square.getBackground());
//
//            square.setLayout(new BorderLayout());
//            Dot dot = new Dot((dims[0]-20)/8, (dims[1]-20)/8, calculateMidColor(Color.WHITE, this.color));
//
//            // Add the dot to the center of the square
//            square.add(dot, BorderLayout.CENTER);
//
//            // Revalidate and repaint the grid panel to ensure layout updates correctly
//            gridPanel.revalidate();
//            gridPanel.repaint();
//
//            System.out.println("Dot added at position (" + row + ", " + col + ")");
//        }
//    }
//
//    private Color calculateMidColor(Color color1, Color color2) {
//        int r = (color1.getRed() + color2.getRed()) / 2;
//        int g = (color1.getGreen() + color2.getGreen()) / 2;
//        int b = (color1.getBlue() + color2.getBlue()) / 2;
//        return new Color(r, g, b);
//    }
//
//    private void handleSquareClick(int row, int col) {
//        if (selectedPiece == null) {
//            // Select a piece
//            selectedPiece = boardState[row][col];
//            if (selectedPiece != null) {
//                selectedRow = row;
//                selectedCol = col;
//                highlightSquare(row, col, Color.YELLOW);
//            }
//        } else {
//            // Attempt to move the selected piece
//            if (selectedPiece.canMove(selectedRow, selectedCol, row, col, boardState)) {
//                movePiece(selectedRow, selectedCol, row, col);
//            }
//            clearHighlights();
//            selectedPiece = null;
//            selectedRow = -1;
//            selectedCol = -1;
//        }
//    }
//
//    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
//        int toIndex = toRow * BOARD_SIZE + toCol;
//        int fromIndex = fromRow * BOARD_SIZE + fromCol;
//
//        boardState[toRow][toCol] = boardState[fromRow][fromCol];
//        boardState[fromRow][fromCol] = null;
//
//        if (boardState[toRow][toCol] != null) {
//            gridPanel.remove(toIndex);
//            gridPanel.add(getJPanel(new JLabel(boardState[toRow][toCol].getIcon()), toRow, toCol), toIndex);
//            gridPanel.remove(fromIndex);
//            gridPanel.add(getJPanel(color, fromRow, fromCol));
//        }
//
//        repaint();
//        revalidate();
//    }
//
//    private void highlightSquare(int row, int col, Color color) {
//        gridPanel.getComponent(row * BOARD_SIZE + col).setBackground(color);
//    }
//
//    private void clearHighlights() {
//        for (int i = 0; i < BOARD_SIZE; i++) {
//            for (int j = 0; j < BOARD_SIZE; j++) {
//                gridPanel.getComponent(i * BOARD_SIZE + j).setBackground((i + j) % 2 == 0 ? Color.WHITE : Color.BLUE);
//            }
//        }
//    }
//}
//
//class Dot extends JComponent {
//    private static int DOT_SIZE = 10; // Size of the dot
//    private static Color color;
//
//    public Dot(int width, int height, Color color) {
//        Dot.color = color;
//        setPreferredSize(new Dimension(width, height));
//        DOT_SIZE = (int)(width*0.4);
//        System.out.println(DOT_SIZE);
//        setBackground(color);
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        g.setColor(color); // Color of the dot
//        g.fillOval((getWidth() - DOT_SIZE) / 2, (getHeight() - DOT_SIZE) / 2, DOT_SIZE, DOT_SIZE);
//    }
//}
//
//// Abstract Piece class
//abstract class Piece {
//    protected String name;
//    protected String color; // "White" or "Black"
//    protected ImageIcon icon;
//    protected boolean haveMove = false;
//
//    public Piece(String name, String color, String iconPath) {
//        this.name = name;
//        this.color = color;
//        this.icon = new ImageIcon(iconPath);
//    }
//
//    public ImageIcon getIcon() {
//        return icon;
//    }
//
//    public abstract boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);
//
//    // Initial setup of the chessboard
//    public static Piece[][] getInitialSetup() {
//        Piece[][] board = new Piece[8][8];
//        String basePath = "src/assets/";
//
//        board[0][0] = new Rook("Black", basePath + "BRook.png");
//        board[0][1] = new Knight("Black", basePath + "BKnight.png");
//        board[0][2] = new Bishop("Black", basePath + "BBishop.png");
//        board[0][3] = new Queen("Black", basePath + "BQueen.png");
//        board[0][4] = new King("Black", basePath + "BKing.png");
//        board[0][5] = new Bishop("Black", basePath + "BBishop.png");
//        board[0][6] = new Knight("Black", basePath + "BKnight.png");
//        board[0][7] = new Rook("Black", basePath + "BRook.png");
//        for (int i = 0; i < 8; i++) {
//            board[1][i] = new Pawn("Black", basePath + "BPawn.png");
//            board[6][i] = new Pawn("White", basePath + "WPawn.png");
//        }
//        board[7][0] = new Rook("White", basePath + "WRook.png");
//        board[7][1] = new Knight("White", basePath + "WKnight.png");
//        board[7][2] = new Bishop("White", basePath + "WBishop.png");
//        board[7][3] = new Queen("White", basePath + "WQueen.png");
//        board[7][4] = new King("White", basePath + "WKing.png");
//        board[7][5] = new Bishop("White", basePath + "WBishop.png");
//        board[7][6] = new Knight("White", basePath + "WKnight.png");
//        board[7][7] = new Rook("White", basePath + "WRook.png");
//
//        return board;
//    }
//}
//
//// Specific piece classes
//class Pawn extends Piece {
//    public Pawn(String color, String iconPath) {
//        super("Pawn", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // Basic pawn movement logic
//        int direction = color.equals("White") ? -1 : 1;
//
//        if ((toCol == fromCol && board[toRow][toCol] == null && toRow - fromRow == direction) //Common move
//        || (board[toRow][toCol] != null && toRow - fromRow == direction && Math.abs(toCol - fromCol) == 1) //Eating the piece
//        || (!this.haveMove && board[toRow+1][toCol] == null && board[toRow+2][toCol] == null && toRow - fromRow == 2*direction)) //First move only in pawn can be 2 steps
//        {
//            return true;
//        }
//        return false;
//    }
//}
//
//class Rook extends Piece {
//    public Rook(String color, String iconPath) {
//        super("Rook", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // Basic rook movement logic
//        int rowDiff = Math.abs(fromRow - toRow);
//        int colDiff = Math.abs(fromCol - toCol);
//
//        if (fromCol == toCol && rowDiff != 0) {
//            // Check if the path is clear
//            int rowStep = (toRow - fromRow) / rowDiff;
//            for (int i = 1; i < rowDiff; i++) {
//                if (board[fromRow + i * rowStep][fromCol] != null) {
//                    return false;
//                }
//            }
//            return true;
//        } else if (fromRow == toRow && colDiff != 0) {
//            // Check if the path is clear
//            int colStep = (toCol - fromCol) / colDiff;
//            for (int i = 1; i < colDiff; i++) {
//                if (board[toRow][fromRow + i * colStep] != null) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//}
//
//class Knight extends Piece {
//    public Knight(String color, String iconPath) {
//        super("Knight", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // Knight movement logic
//        int rowDiff = Math.abs(fromRow - toRow);
//        int colDiff = Math.abs(fromCol - toCol);
//        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
//    }
//}
//class Bishop extends Piece {
//    public Bishop(String color, String iconPath) {
//        super("Bishop", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // Bishop moves diagonally: |rowDiff| == |colDiff|
//        int rowDiff = Math.abs(fromRow - toRow);
//        int colDiff = Math.abs(fromCol - toCol);
//
//        if (rowDiff == colDiff && rowDiff != 0) {
//            // Check if the path is clear
//            int rowStep = (toRow - fromRow) / rowDiff;
//            int colStep = (toCol - fromCol) / colDiff;
//            for (int i = 1; i < rowDiff; i++) {
//                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//}
//
//class Queen extends Piece {
//    public Queen(String color, String iconPath) {
//        super("Queen", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // Queen combines Rook and Bishop moves
//        int rowDiff = Math.abs(fromRow - toRow);
//        int colDiff = Math.abs(fromCol - toCol);
//
//        if (rowDiff == colDiff && rowDiff != 0) {
//            // Check if the path is clear
//            int rowStep = (toRow - fromRow) / rowDiff;
//            int colStep = (toCol - fromCol) / colDiff;
//            for (int i = 1; i < rowDiff; i++) {
//                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
//                    return false;
//                }
//            }
//            return true;
//        } else if (fromCol == toCol && rowDiff != 0) {
//            // Check if the path is clear
//            int rowStep = (toRow - fromRow) / rowDiff;
//            for (int i = 1; i < rowDiff; i++) {
//                if (board[fromRow + i * rowStep][fromCol] != null) {
//                    return false;
//                }
//            }
//            return true;
//        } else if (fromRow == toRow && colDiff != 0) {
//            // Check if the path is clear
//            int colStep = (toCol - fromCol) / colDiff;
//            for (int i = 1; i < colDiff; i++) {
//                if (board[toRow][fromRow + i * colStep] != null) {
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
//}
//
//class King extends Piece {
//    public King(String color, String iconPath) {
//        super("King", color, iconPath);
//    }
//
//    @Override
//    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
//        // King moves one square in any direction
//        int rowDiff = Math.abs(fromRow - toRow);
//        int colDiff = Math.abs(fromCol - toCol);
//
//
//        return (rowDiff <= 1 && colDiff <= 1) && board[toRow][toCol] == null;
//    }
//}


import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class ChessBoard extends JFrame {
    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8
    private JPanel[][] squares = new JPanel[BOARD_SIZE][BOARD_SIZE];
    private Piece[][] boardState = Piece.getInitialSetup();
    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;
    private Color color;
    private String currentPlayer = "White";

    public ChessBoard(int[] dims, Color color) {
        setTitle("Chess Board");
        setSize(dims[0], dims[1]);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.color = color;

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
                JPanel square = new JPanel(new BorderLayout());
                square.setBackground((i + j) % 2 == 0 ? this.color : Color.WHITE);
                int row = i, col = j; // Capture variables for listener

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
                        handleSquareClick(row, col);
                    }
                });

                squares[i][j] = square;
                boardPanel.add(square);
            }
        }
        add(rowLabels, BorderLayout.WEST);
        add(colLabels, BorderLayout.SOUTH);

        add(boardPanel, BorderLayout.CENTER);
    }

    private void handleSquareClick(int row, int col) {
        if (selectedPiece == null) {
            // Select a piece
            selectedPiece = boardState[row][col];
            if (selectedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                highlightSquare(row, col, Color.YELLOW);
            }
        } else {
            // Attempt to move the selected piece
            if (currentPlayer.equals(selectedPiece.color) &&
                    ((boardState[row][col] == null) ||
                    (boardState[row][col] != null && !selectedPiece.color.equals(boardState[row][col].color))) &&
                    selectedPiece.canMove(selectedRow, selectedCol, row, col, boardState)) {
                movePiece(selectedRow, selectedCol, row, col);
                currentPlayer = currentPlayer.equals("White") ? "Black" : "White";
            }
            clearHighlights();
            selectedPiece = null;
            selectedRow = -1;
            selectedCol = -1;
        }
    }

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = null;
        boardState[toRow][toCol].haveMove = true;

        squares[toRow][toCol].removeAll();
        if (boardState[toRow][toCol] != null) {
            squares[toRow][toCol].add(new JLabel(boardState[toRow][toCol].getIcon()));
        }
        squares[fromRow][fromCol].removeAll();

        repaint();
        revalidate();
    }

    private void highlightSquare(int row, int col, Color color) {
        squares[row][col].setBackground(color);
    }

    private void clearHighlights() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                squares[i][j].setBackground((i + j) % 2 == 0 ? color : Color.WHITE);
            }
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ChessBoard chessBoard = new ChessBoard(new int[]{800, 800}, Color.BLUE);
            chessBoard.setVisible(true);
        });
    }
}

// Abstract Piece class
abstract class Piece {
    protected String name;
    protected String color; // "White" or "Black"
    protected ImageIcon icon;
    protected boolean haveMove = false;

    public Piece(String name, String color, String iconPath) {
        this.name = name;
        this.color = color;
        this.icon = new ImageIcon(iconPath);
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public abstract boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board);

    // Initial setup of the chessboard
    public static Piece[][] getInitialSetup() {
        Piece[][] board = new Piece[8][8];
        String basePath = "src/assets/";

        board[0][0] = new Rook("Black", basePath + "BRook.png");
        board[0][1] = new Knight("Black", basePath + "BKnight.png");
        board[0][2] = new Bishop("Black", basePath + "BBishop.png");
        board[0][3] = new King("Black", basePath + "BKing.png");
        board[0][4] = new Queen("Black", basePath + "BQueen.png");
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
        board[7][3] = new King("White", basePath + "WKing.png");
        board[7][4] = new Queen("White", basePath + "WQueen.png");
        board[7][5] = new Bishop("White", basePath + "WBishop.png");
        board[7][6] = new Knight("White", basePath + "WKnight.png");
        board[7][7] = new Rook("White", basePath + "WRook.png");

        return board;
    }
}

// Specific piece classes
class Pawn extends Piece {
    public Pawn(String color, String iconPath) {
        super("Pawn", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Basic pawn movement logic
        int direction = color.equals("White") ? -1 : 1;

        if ((toCol == fromCol && board[toRow][toCol] == null && toRow - fromRow == direction) //Common move
                || (board[toRow][toCol] != null && toRow - fromRow == direction && Math.abs(toCol - fromCol) == 1) //Eating the piece
                || (!this.haveMove && board[fromRow+direction][toCol] == null && board[fromRow+2*direction][toCol] == null && toRow - fromRow == 2*direction && fromCol == toCol)) //First move only in pawn can be 2 steps
        {
            return true;
        }
        return false;
    }
}

class Rook extends Piece {
    public Rook(String color, String iconPath) {
        super("Rook", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Basic rook movement logic
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (fromCol == toCol && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromRow == toRow && colDiff != 0) {
            // Check if the path is clear
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < colDiff; i++) {
                if (board[toRow][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

class Knight extends Piece {
    public Knight(String color, String iconPath) {
        super("Knight", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Knight movement logic
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);
        return (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
    }
}
class Bishop extends Piece {
    public Bishop(String color, String iconPath) {
        super("Bishop", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Bishop moves diagonally: |rowDiff| == |colDiff|
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff == colDiff && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

class Queen extends Piece {
    public Queen(String color, String iconPath) {
        super("Queen", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // Queen combines Rook and Bishop moves
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);

        if (rowDiff == colDiff && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromCol == toCol && rowDiff != 0) {
            // Check if the path is clear
            int rowStep = (toRow - fromRow) / rowDiff;
            for (int i = 1; i < rowDiff; i++) {
                if (board[fromRow + i * rowStep][fromCol] != null) {
                    return false;
                }
            }
            return true;
        } else if (fromRow == toRow && colDiff != 0) {
            // Check if the path is clear
            int colStep = (toCol - fromCol) / colDiff;
            for (int i = 1; i < colDiff; i++) {
                if (board[toRow][fromCol + i * colStep] != null) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}

class King extends Piece {
    public King(String color, String iconPath) {
        super("King", color, iconPath);
    }

    @Override
    public boolean canMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board) {
        // King moves one square in any direction
        int rowDiff = Math.abs(fromRow - toRow);
        int colDiff = Math.abs(fromCol - toCol);


        return (rowDiff <= 1 && colDiff <= 1);
    }
}