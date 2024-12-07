import javax.swing.*;
import java.awt.*;



public class Game extends JFrame{
    public static void main(String[] args) {
        ChessBoard chessBoard = new ChessBoard(new int[]{800, 800}, Color.BLUE);
        chessBoard.addDot(5,5);
        EventQueue.invokeLater(() -> {
            chessBoard.setVisible(true);
        });
    }
}
