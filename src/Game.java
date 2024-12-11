import javax.swing.*;
import java.awt.*;


public class Game extends JFrame{
    public ChessBoard board;
    private String playerName;
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
                            resetGame(new int[]{800, 800});
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

    private void resetGame(int[] size) {
        EventQueue.invokeLater(() -> {
            board = new ChessBoard(size, Color.LIGHT_GRAY);
            board.setVisible(true);
            checkForCheckmate();
        });
    }

    private void startGame() {
        EventQueue.invokeLater(() -> {
            // Prompt for player details
            playerName = JOptionPane.showInputDialog(this, "Enter your name:");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "Player";
            }

            Object[] options = {"White", "Black"};
            playerColor = (String) JOptionPane.showInputDialog(this, "Choose your color:", "Color Selection",
                    JOptionPane.PLAIN_MESSAGE, null, options, "White");

            if (playerColor == null) {
                playerColor = "White"; // Default to White if no selection
            }

            // Initialize the chessboard
            board = new ChessBoard(new int[]{800, 800}, Color.LIGHT_GRAY);
            board.setVisible(true);

            // Add player name based on color
            JLabel playerNameLabel = new JLabel(playerName, SwingConstants.CENTER);
            if ("White".equals(playerColor)) {
                board.add(playerNameLabel, BorderLayout.SOUTH);
            } else {
                board.add(playerNameLabel, BorderLayout.NORTH);
            }

            board.pack(); // Adjust layout
            checkForCheckmate();
        });
    }
}
