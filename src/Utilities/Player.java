package Utilities;

import Pieces.Piece;

import java.util.ArrayList;

/**
 * Represents a player in the chess game.
 * A player has a name, a color representing their pieces, and a collection of captured pieces.
 */
public class Player {

    /**
     * The name of the player.
     */
    private String name;

    /**
     * The color of the player's pieces ("White" or "Black").
     */
    private String color;

    /**
     * A list of pieces captured by the player.
     */
    public ArrayList<Piece> capturedPieces = new ArrayList<>();

    /**
     * Constructs a Player with the specified name and color.
     *
     * @param name  the name of the player.
     * @param color the color of the player's pieces ("White" or "Black").
     */
    public Player(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * Constructs a Player with the specified name and a default color of "White".
     *
     * @param name the name of the player.
     */
    public Player(String name) {
        this(name, "White");
    }

    /**
     * Constructs a Player with a default name of "Player" and a default color of "White".
     */
    public Player() {
        this("Player");
    }

    /**
     * Sets the name of the player.
     *
     * @param name the new name of the player.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the color of the player's pieces.
     *
     * @param color the new color of the player's pieces ("White" or "Black").
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Gets the name of the player.
     *
     * @return the name of the player.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the color of the player's pieces.
     *
     * @return the color of the player's pieces ("White" or "Black").
     */
    public String getColor() {
        return color;
    }
}
