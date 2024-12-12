import javax.swing.*;
import java.awt.*;

class Dot extends JComponent {
    private static int DOT_SIZE = 10; // Size of the dot
    private final Color color;

    public Dot(int width, int height, Color color) {
        this.color = color;
        setPreferredSize(new Dimension(width, height));
        DOT_SIZE = (int)(width*0.7);
        setBackground(color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color); // Color of the dot
        g.fillOval((getWidth() - DOT_SIZE) / 2, (getHeight() - DOT_SIZE) / 2, DOT_SIZE, DOT_SIZE);
    }
}