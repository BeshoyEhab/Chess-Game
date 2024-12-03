import javax.swing.*;
import java.awt.*;

public class ChessBoard extends JFrame {
    private static final int BOARD_SIZE = 8; // Standard chessboard is 8x8

    public ChessBoard() {
        setTitle("Chess Board");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new BorderLayout());

        JPanel gridPanel = getJPanel();

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
            JLabel label = new JLabel(String.valueOf((char)('A' + i)), SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            colLabels.add(label);
        }

        boardPanel.add(rowLabels, BorderLayout.WEST);
        boardPanel.add(gridPanel, BorderLayout.CENTER);
        boardPanel.add(colLabels, BorderLayout.SOUTH);

        add(boardPanel);
    }

    private static JPanel getJPanel() {
        JPanel gridPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // Add chessboard squares
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JPanel square = new JPanel();
                if ((i + j) % 2 == 0) {
                    square.setBackground(Color.WHITE);
                } else {
                    square.setBackground(Color.GRAY);
                }
                gridPanel.add(square);
            }
        }
        return gridPanel;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ChessBoard chessBoard = new ChessBoard();
            chessBoard.setVisible(true);
        });
    }
}