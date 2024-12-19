package AI;

import Pieces.*;
import Utilities.*;
import GameManager.Game;

import javax.swing.SwingWorker;
import java.awt.*;
import java.util.ArrayList;

public class AIPlayer extends SwingWorker<Move, Void> {
    private final ArrayList<Move> moves;
    private final boolean isMaximizingPlayer;
    private final Game game;
    private static volatile boolean isProcessing = false;
    private final String aiColor; // Store the AI's color when move calculation started

    public AIPlayer(boolean isMaximizingPlayer, Game game, String aiColor) {
        this.moves = new ArrayList<>(game.moves);
        this.isMaximizingPlayer = isMaximizingPlayer;
        this.game = game;
        this.aiColor = aiColor;
        game.board.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    public static boolean isThinking() {
        return isProcessing;
    }

    @Override
    protected Move doInBackground() {
        if (isProcessing) {
            return null;
        }
        isProcessing = true;
        return AI_Minimax.getBestMove(moves, 3, isMaximizingPlayer);
    }

    @Override
    protected void done() {
        try {
            Move move = get();
            // Only proceed if it's still AI's turn and the colors match
            if (move != null &&
                    game.currentPlayer instanceof AI_Minimax &&
                    game.currentPlayer.getColor().equals(aiColor)) {

                EventQueue.invokeLater(() -> {
                    game.board.clearHighlights();
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            game.board.removeDot(i, j, game.boardState[i][j]);
                        }
                    }
                    Piece[][] boardState = game.boardState;
                    move.piece = boardState[move.fromRow][move.fromCol];
                    move.capturedPiece = boardState[move.toRow][move.toCol];
                    move.haveMoved = true;

                    // Make the move
                    game.movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);
                    game.board.switchTimers(game.currentPlayer.getColor());
                    game.currentPlayer = aiColor.equals(game.player1.getColor()) ?
                            game.player2 : game.player1;

                    // Log the move
                    System.out.print(game.moves.size() % 2 == 1 ?
                            (game.moves.size()+1)/2 + ") " + game.moves.getLast() :
                            "\t | " + game.moves.getLast() + "\n");

                    // Update the board display
                    game.highlightCheck();

                    game.board.highlightSquare(move.toRow, move.toCol, Color.GREEN);
                    game.board.repaint();
                    game.board.revalidate();
                    game.board.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            isProcessing = false;
        }
    }
}