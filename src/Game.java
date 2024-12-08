import javax.swing.*;
import java.awt.*;



public class Game extends JFrame{
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            ChessBoard chessBoard = new ChessBoard(new int[]{800, 800}, Color.BLUE);
            chessBoard.setVisible(true);
        });
    }
}
