import javax.swing.*;
import java.awt.*;

public class Game extends JFrame{
    public ChessBoard board;
    private String playerName1;
    private String playerName2;
    private String playerColor;

    public static void main(String[] args) {

        new Game().startGame();
    }

    private void checkForCheckmate() {
        new Thread(() -> {
            while (true) {
                if (board.checkMate()) {
                    EventQueue.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, (board.currentPlayer.equals("White") ? "Black" : "White") + " has won!");

                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            startGame();
                        } else {
                            System.exit(0);
                        }
                    });
                    break;
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

    private void startGame() {
        EventQueue.invokeLater(() -> {
            // Prompt for player details
            playerName1 = JOptionPane.showInputDialog(this, "Player 1 name:");
            if (playerName1 == null || playerName1.trim().isEmpty()) {
                playerName1 = "Player 1";
            }
            playerName2 = JOptionPane.showInputDialog(this, "Player 2 name:");
            if (playerName2 == null || playerName2.trim().isEmpty()) {
                playerName2 = "Player 2";
            }

            Object[] options = {"White", "Black"};
            playerColor = (String) JOptionPane.showInputDialog(this, "Player 1 color:", "Color Selection",
                    JOptionPane.PLAIN_MESSAGE, null, options, "White");

            if (playerColor == null) {
                playerColor = "White"; // Default to White if no selection
            }

            // Initialize the chessboard
            board = new ChessBoard(new int[]{800, 800}, Color.LIGHT_GRAY);
            board.setVisible(true);

            // Add player name based on color
            JLabel player1NameLabel = new JLabel(playerName1, SwingConstants.CENTER);
            player1NameLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel player2NameLabel = new JLabel(playerName2, SwingConstants.CENTER);
            player2NameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            if ("White".equals(playerColor)) {
                board.rightPanel.add(player2NameLabel, 0);
                board.rightPanel.add(player1NameLabel, 2);
            } else {
                board.rightPanel.add(player1NameLabel, 0);
                board.rightPanel.add(player2NameLabel, 2);
            }

            checkForCheckmate();
        });
    }
}
