import java.util.ArrayList;
import java.util.List;

public class game {
    private board board;
    private int width;
    private int height;
    private List<Integer> color1;
    private List<Integer> color2;
    private Piece selectedPiece;
    private List<Move> history;


    private List<Move> history = new ArrayList<>();
    public boolean checkValidate(Piece piece, String targetPosition) {
        // Get the piece's allowed moves based on current game state
        List<String> allowedMoves = piece.getAllowedMoves(this.getBoardState());

        // Check if the target position is in the allowed moves
        if (!allowedMoves.contains(targetPosition)) {
            return false; // Invalid move
        }

        // Simulate the move and check if the king is under attack
        if (isKingUnderCheckAfterMove(piece, targetPosition)) {
            return false; // Move is invalid as it leaves the king in check
        }

        return true; // Valid move
    }

    // Simulate move and check if king is under attack
    private boolean isKingUnderCheckAfterMove(Piece piece, String targetPosition) {
        // Save current state
        String currentPosition = piece.getPosition();
        Piece capturedPiece = this.getBoard().getPieceAt(targetPosition);

        // Simulate the move
        this.getBoard().movePiece(piece, targetPosition);

        // Check if king is under attack
        boolean isInCheck = isKingUnderCheck(piece.getColor());

        // Undo the simulated move
        this.getBoard().movePiece(piece, currentPosition);
        this.getBoard().placePiece(capturedPiece, targetPosition);

        return isInCheck;
    }

    // Check if the player's king is under check
    private boolean isKingUnderCheck(String playerColor) {
        // Find the player's king
        Piece king = this.getBoard().findKing(playerColor);

        // Check if any opponent piece can attack the king's position
        for (Piece opponentPiece : this.getBoard().getOpponentPieces(playerColor)) {
            if (opponentPiece.getAllowedMoves(this.getBoardState()).contains(king.getPosition())) {
                return true; // King is under attack
            }
        }
        return false; // King is safe
    }
    public void addToHistory(String pieceName, String start, String end) {
        history.add(new Move(pieceName, start, end));
    }
    public void displayHistory() {
        for (Move move : history) {
            System.out.println(move);
        }
    }
}
