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
        this.moves = new ArrayList<>(game.gameLogic.moves);
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
        if (isCancelled())
            AI_Minimax.isTimeRemaining = false;
        return AI_Minimax.getBestMove(moves, 3, isMaximizingPlayer);
    }

    @Override
    protected void done() {
        try {
            if (isCancelled()) {
                // Task was cancelled, handle accordingly
                game.board.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                return;
            }
            Move move = get();
            // Only proceed if it's still AI's turn and the colors match
            if (move != null &&
                    game.gameLogic.currentPlayer instanceof AI_Minimax &&
                    game.gameLogic.currentPlayer.getColor().equals(aiColor)) {

                EventQueue.invokeLater(() -> {
                    game.board.clearHighlights();
                    for (int i = 0; i < 8; i++) {
                        for (int j = 0; j < 8; j++) {
                            game.board.removeDot(i, j, game.gameLogic.boardState[i][j]);
                        }
                    }
                    Piece[][] boardState = game.gameLogic.boardState;
                    move.piece = boardState[move.fromRow][move.fromCol];
                    move.capturedPiece = boardState[move.toRow][move.toCol];
                    move.timers[0] = game.board.whiteTimeRemaining;
                    move.timers[1] = game.board.blackTimeRemaining;
                    move.haveMoved = true;

                    // Make the move
                    game.movePiece(move.fromRow, move.fromCol, move.toRow, move.toCol);
                    if (move.piece.name.equals("King")) {
                        game.gameLogic.movesToStalemate++;
                    } else if (move.piece.name.equals("Pawn")) {
                        if ((move.toRow == 0 && move.piece.color.equals("White"))
                                || (move.toRow == 7 && move.piece.color.equals("Black"))) {
                            game.gameLogic.boardState[move.toRow][move.toCol] = new Queen(move.piece.color);
                            game.gameLogic.moves.getLast().promoteTo = "Queen";
                            game.board.removeSquare(move.toRow, move.toCol);
                            game.board.addSquare(move.toRow, move.toCol,
                                    game.gameLogic.boardState[move.toRow][move.toCol].getIcon());
                            game.gameLogic.movesToStalemate = 0;
                        } else {
                            game.gameLogic.movesToStalemate++;
                        }
                    } else {
                        game.gameLogic.movesToStalemate = 0;
                    }
                    game.board.switchTimers(game.gameLogic.currentPlayer.getColor());
                    game.gameLogic.currentPlayer = aiColor.equals(game.gameLogic.player1.getColor())
                            ? game.gameLogic.player2
                            : game.gameLogic.player1;

                    // Log the move is now handled in Game.movePiece
                    // System.out.print(game.gameLogic.moves.size() % 2 == 1
                    // ? (game.gameLogic.moves.size() + 1) / 2 + ") " +
                    // game.gameLogic.moves.getLast()
                    // : "\t | " + game.gameLogic.moves.getLast() + "\n");

                    // Apply standard last move highlight
                    game.board.clearHighlights();

                    // Update the board display
                    game.highlightCheck();
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