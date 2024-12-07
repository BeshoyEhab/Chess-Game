import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame {
    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8
    private JPanel gridPanel;
    private Color color;

    public ChessBoard(int[] dims, Color color) {
        this.color = color;

        setTitle("Chess Board");
        setSize(dims[0], dims[1]);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());

        gridPanel = getJPanel(this.color); // Initialize grid panel

        // Add row labels on the left
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

        boardPanel.add(rowLabels, BorderLayout.WEST);
        boardPanel.add(gridPanel, BorderLayout.CENTER);
        boardPanel.add(colLabels, BorderLayout.SOUTH);

        add(boardPanel);
    }

    private static JPanel getJPanel(Color color) {
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add chessboard squares
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JPanel square = new JPanel();
                if ((i + j) % 2 == 0) {
                    square.setBackground(Color.WHITE);
                } else {
                    square.setBackground(color);
                }
                gridPanel.add(square);
            }
        }

        return gridPanel;
    }

    public void addDot(int row, int col) {
        // Convert chessboard coordinates to array indices
        // Rows are reversed in the layout
        int rowIndex = BOARD_SIZE - 1 - row;

        Component component = gridPanel.getComponent(col + BOARD_SIZE * rowIndex);

        if (component instanceof JPanel square) {
            // Debug: Print square background color
            System.out.println("Square Background Color: " + square.getBackground());

            square.setLayout(new BorderLayout());
            Dot dot = new Dot(square.getWidth(), square.getHeight(), 30, calculateMidColor(Color.WHITE, square.getBackground()));

            // Add the dot to the center of the square
            square.add(dot, BorderLayout.CENTER);

            // Revalidate and repaint the grid panel to ensure layout updates correctly
            gridPanel.revalidate();
            gridPanel.repaint();

            System.out.println("Dot added at position (" + row + ", " + col + ")");
        }
    }

    private Color calculateMidColor(Color color1, Color color2) {
        int r = (color1.getRed() + color2.getRed()) / 2;
        int g = (color1.getGreen() + color2.getGreen()) / 2;
        int b = (color1.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ChessBoard chessBoard = new ChessBoard(new int[]{400, 400}, Color.GRAY);
            chessBoard.setVisible(true);

            // Add a dot at position (3, 3) (0-based index)
            chessBoard.addDot(3, 3);
        });
    }
}

class Dot extends JComponent {
    private static int DOT_SIZE = 10; // Size of the dot

    public Dot(int width, int height, int dot_size, Color color) {
        setPreferredSize(new Dimension(width, height));
        DOT_SIZE = dot_size;
        System.out.println(DOT_SIZE);
        setBackground(color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED); // Color of the dot
        g.fillOval((getWidth() - DOT_SIZE) / 2, (getHeight() - DOT_SIZE) / 2, DOT_SIZE, DOT_SIZE);
    }
}