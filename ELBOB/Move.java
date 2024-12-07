public class Move {
    private String pieceName;
    private String start;
    private String end;

    // Constructor
    public Move(String pieceName, String start, String end) {
        this.pieceName = pieceName;
        this.start = start;
        this.end = end;
    }

    // Getters and Setters
    public String getPieceName() { return pieceName; }
    public String getStart() { return start; }
    public String getEnd() { return end; }

    public String toString() {
        return pieceName + ": " + start + " -> " + end;
    }
}

