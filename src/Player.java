import java.util.ArrayList;

public class Player {
    private String name;
    private String color;
    public ArrayList<Piece> capturedPieces = new ArrayList<>();

    public Player(String name, String color) {
        this.name = name;
        this.color = color;
    }
    public Player(String name) {
        this(name, "White");
    }
    public Player() {
        this("Player");
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
