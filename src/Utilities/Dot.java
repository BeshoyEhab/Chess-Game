package Utilities;

import javax.swing.*;
import java.awt.*;

/// A custom Swing component representing a centered dot with configurable size and color.
/// This class creates a circular dot that is positioned in the center of its component space.
/// The dot's size is dynamically calculated based on the component's width, and it can be
/// initialized with a specific color.
/// The dot is typically used for visual indicators in graphical interfaces, such as
/// highlighting possible moves or marking special locations on a game board.
///
/// @author Team 57
/// @version 1.0
/// @since 2024-12-15
/// @see JComponent
class Dot extends JComponent {
    /**
     * The size of the dot, calculated as a percentage of the component's width.
     * Defaults to 70% of the component width.
     */
    private static int DOT_SIZE = 10;

    /**
     * The color of the dot.
     * Determines the fill color when the dot is painted.
     */
    private final Color color;

    /// Constructs a new Dot component with specified dimensions and color.
    /// The dot's size is dynamically calculated as 70% of the component's width.
    /// The component is configured with the given width, height, and color.
    ///
    /// @param width The width of the component containing the dot
    /// @param height The height of the component containing the dot
    /// @param color The color used to fill the dot
    public Dot(int width, int height, Color color) {
        this.color = color;
        setPreferredSize(new Dimension(width, height));
        DOT_SIZE = (int)(width*0.7);
        setBackground(color);
    }

    /// Overrides the default painting method to draw a centered dot.
    /// The dot is drawn as a filled oval positioned exactly in the center of the component.
    /// The dot's size is calculated based on the component's width, and it uses
    /// the color specified during construction.
    ///
    /// @param g The Graphics context used for painting the component
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(color); // Color of the dot
        g.fillOval((getWidth() - DOT_SIZE) / 2, (getHeight() - DOT_SIZE) / 2, DOT_SIZE, DOT_SIZE);
    }
}