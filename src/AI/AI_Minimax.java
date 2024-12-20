package AI;

import Pieces.*;
import Utilities.Move;
import Utilities.Player;

import java.util.ArrayList;
import java.util.Random;

public class AI_Minimax extends Player {
    // Evaluation constants for piece values
    private static final int PAWN_VALUE = 10;
    private static final int KNIGHT_BISHOP_VALUE = 30;
    private static final int ROOK_VALUE = 50;
    private static final int QUEEN_VALUE = 90;
    private static final int KING_VALUE = 1000;

    // New constants for checkmate and check evaluation
    private static final int CHECKMATE_VALUE = 100000;
    private static final int CHECK_VALUE = 50;
    private static final int WINNING_POSITION_BONUS = 200;

    // Depth constants
    private static final int MAX_DEPTH = 5;

    //Time remaining from Game class
    public static boolean isTimeRemaining = true;

    // Pieces.Piece-square tables for positional evaluation
    private static final int[][] PAWN_TABLE = {
            { 0,  5,  5, -10, -10,  5,  5,  0},
            { 0, 10, -5,   0,   0, -5, 10,  0},
            { 0, 10, 10,  20,  20, 10, 10,  0},
            { 5, 20, 20,  25,  25, 20, 20,  5},
            {10, 20, 20,  25,  25, 20, 20, 10},
            {20, 30, 30,  50,  50, 30, 30, 20},
            {50, 50, 50,  50,  50, 50, 50, 50},
            { 0,  0,  0,   0,   0,  0,  0,  0},
    };

    private static final int[][] KNIGHT_TABLE = {
            {-50, -40, -30, -30, -30, -30, -40, -50},
            {-40, -20,   0,   5,   5,   0, -20, -40},
            {-30,   5,  10,  15,  15,  10,   5, -30},
            {-30,   0,  15,  20,  20,  15,   0, -30},
            {-30,   5,  15,  20,  20,  15,   5, -30},
            {-30,   0,  10,  15,  15,  10,   0, -30},
            {-40, -20,   0,   0,   0,   0, -20, -40},
            {-50, -40, -30, -30, -30, -30, -40, -50},
    };

    private static final int[][] KING_ENDGAME_TABLE = {
            {-50, -30, -10,   0,   0, -10, -30, -50},
            {-30, -10,  20,  30,  30,  20, -10, -30},
            {-10,  20,  40,  50,  50,  40,  20, -10},
            {  0,  30,  50,  60,  60,  50,  30,   0},
            {  0,  30,  50,  60,  60,  50,  30,   0},
            {-10,  20,  40,  50,  50,  40,  20, -10},
            {-30, -10,  20,  30,  30,  20, -10, -30},
            {-50, -30, -10,   0,   0, -10, -30, -50},
    };

    /**
     * Improved minimax algorithm with better checkmate detection
     */
    private static Move minimax(ArrayList<Move> moves, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        Move bestMove = new Move(-1, -1, -1, -1, null, null, 0, 0);

        // Check for immediate checkmate first
        String currentColor = isMaximizingPlayer ? "White" : "Black";
        String opponentColor = isMaximizingPlayer ? "Black" : "White";

        if (isCheckmate(moves, opponentColor)) {
            bestMove.eval = isMaximizingPlayer ? CHECKMATE_VALUE : -CHECKMATE_VALUE;
            return bestMove;
        }

        // Base case: reached maximum depth or game-ending condition
        if (depth == 0 || isGameOver(moves, isMaximizingPlayer) || !isTimeRemaining) {
            bestMove.eval = quiescenceSearch(moves, alpha, beta, isMaximizingPlayer);
            return bestMove;
        }

        // Get valid moves and sort them
        ArrayList<Move> validMoves = validMoves(getBoard(moves), currentColor, moves.isEmpty() ? null : moves.getLast());
        validMoves.sort((m1, m2) -> Integer.compare(calculateMoveImportance(m2, moves, currentColor),
                calculateMoveImportance(m1, moves, currentColor)));

        // Maximizing player (White)
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : validMoves) {
                ArrayList<Move> tmpMoves = new ArrayList<>(moves);
                tmpMoves.add(move);

                // Check if this move leads to checkmate
                if (isCheckmate(tmpMoves, "Black")) {
                    bestMove = move;
                    bestMove.eval = CHECKMATE_VALUE;
                    return bestMove;
                }

                Move result = minimax(tmpMoves, depth - 1, alpha, beta, false);
                if (result.eval > maxEval) {
                    maxEval = result.eval;
                    bestMove = move;
                    bestMove.eval = maxEval;
                }

                alpha = Math.max(alpha, maxEval);
                if (beta <= alpha) break;
            }
        }
        // Minimizing player (Black)
        else {
            int minEval = Integer.MAX_VALUE;
            for (Move move : validMoves) {
                ArrayList<Move> tmpMoves = new ArrayList<>(moves);
                tmpMoves.add(move);

                // Check if this move leads to checkmate
                if (isCheckmate(tmpMoves, "White")) {
                    bestMove = move;
                    bestMove.eval = -CHECKMATE_VALUE;
                    return bestMove;
                }

                Move result = minimax(tmpMoves, depth - 1, alpha, beta, true);
                if (result.eval < minEval) {
                    minEval = result.eval;
                    bestMove = move;
                    bestMove.eval = minEval;
                }

                beta = Math.min(beta, minEval);
                if (beta <= alpha) break;
            }
        }

        return bestMove;
    }

    /**
     * Quiescence search to extend evaluation for tactical moves like captures and checks.
     */
    private static int quiescenceSearch(ArrayList<Move> moves, int alpha, int beta, boolean isMaximizingPlayer) {
        int standPat = evaluate(moves, isMaximizingPlayer);
        if (isMaximizingPlayer) {
            if (standPat >= beta) return beta;
            alpha = Math.max(alpha, standPat);
        } else {
            if (standPat <= alpha) return alpha;
            beta = Math.min(beta, standPat);
        }

        String currentColor = isMaximizingPlayer ? "White" : "Black";
        ArrayList<Move> validMoves = validMoves(getBoard(moves), currentColor, moves.isEmpty() ? null : moves.getLast());
        for (Move move : validMoves) {
            if (move.capturedPiece == null) continue; // Only consider capture moves

            ArrayList<Move> tmpMoves = new ArrayList<>(moves);
            tmpMoves.add(move);
            int score = quiescenceSearch(tmpMoves, alpha, beta, !isMaximizingPlayer);

            if (isMaximizingPlayer) {
                alpha = Math.max(alpha, score);
                if (alpha >= beta) return beta;
            } else {
                beta = Math.min(beta, score);
                if (beta <= alpha) return alpha;
            }
        }
        return isMaximizingPlayer ? alpha : beta;
    }

    /**
     * Improved evaluation function with stronger emphasis on checkmate and winning positions
     */
    private static int evaluate(ArrayList<Move> moves, Boolean isMaximizingPlayer) {
        Piece[][] board = getBoard(moves);
        int score = 0;

        // Check for checkmate first
        if (isCheckmate(moves, isMaximizingPlayer ? "Black" : "White")) {
            return CHECKMATE_VALUE;
        }
        if (isCheckmate(moves, isMaximizingPlayer ? "White" : "Black")) {
            return -CHECKMATE_VALUE;
        }

        // Check for check position
        if (underCheck(board, isMaximizingPlayer ? "Black" : "White")) {
            score += CHECK_VALUE;
        }
        if (underCheck(board, isMaximizingPlayer ? "White" : "Black")) {
            score -= CHECK_VALUE;
        }

        // Material and position evaluation
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece == null) continue;

                int pieceValue = getPieceValue(piece);
                int positionBonus = getPieceSquareValue(piece, i, j);

                // Add extra bonus for pieces near enemy king
                int[] enemyKingPos = findKingPosition(board, piece.color.equals("White") ? "Black" : "White");
                if (enemyKingPos != null) {
                    int distanceToKing = Math.abs(i - enemyKingPos[0]) + Math.abs(j - enemyKingPos[1]);
                    int kingProximityBonus = (7 - distanceToKing) * 5; // More bonus for pieces closer to enemy king
                    positionBonus += kingProximityBonus;
                }

                if (piece.name.equals("Queen") && !piece.haveMove) {
                    if ((piece.color.equals("White") && i == 7) ||
                            (piece.color.equals("Black") && i == 0)) {
                        pieceValue += 20;
                    }
                }

                // Add winning position bonus for advantageous positions
                if (isWinningPosition(board, piece.color)) {
                    positionBonus += WINNING_POSITION_BONUS;
                }

                score += piece.color.equals("White") ? (pieceValue + positionBonus) : -(pieceValue + positionBonus);
            }
        }

        score += evaluateMobility(board, "White", moves.isEmpty() ? null : moves.getLast()) -
                evaluateMobility(board, "Black", moves.isEmpty() ? null : moves.getLast());

        return score;
    }

    /**
     * Get positional bonus from piece-square tables.
     */
    private static int getPieceSquareValue(Piece piece, int row, int col) {
        return switch (piece.name) {
            case "Pawn" -> PAWN_TABLE[row][col];
            case "Knight" -> KNIGHT_TABLE[row][col];
            case "King" -> KING_ENDGAME_TABLE[row][col];
            default -> 0;
        };
    }


    /**
     * New method to evaluate if a position is winning
     */
    private static boolean isWinningPosition(Piece[][] board, String color) {
        int materialAdvantage = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null) {
                    int value = getPieceValue(piece);
                    if (piece.color.equals(color)) {
                        materialAdvantage += value;
                    } else {
                        materialAdvantage -= value;
                    }
                }
            }
        }
        return materialAdvantage > QUEEN_VALUE;
    }

    /**
     * Evaluate piece mobility as a bonus.
     */
    private static int evaluateMobility(Piece[][] board, String color, Move lastMove) {
        int mobilityBonus = 0;
        ArrayList<Move> valMoves = validMoves(board, color, lastMove);
        mobilityBonus += valMoves.size();
        return mobilityBonus;
    }

    /**
     * Improved move importance calculation with stronger emphasis on checks and captures
     */
    private static int calculateMoveImportance(Move move, ArrayList<Move> previousMoves, String currentColor) {
        Piece[][] board = getBoard(previousMoves);
        int score = 0;

        // Immediate checkmate moves get the highest priority
        ArrayList<Move> tmpMoves = new ArrayList<>(previousMoves);
        tmpMoves.add(move);
        if (isCheckmate(tmpMoves, currentColor.equals("White") ? "Black" : "White")) {
            return Integer.MAX_VALUE;
        }

        // Capturing moves
        if (move.capturedPiece != null) {
            score += getPieceValue(move.capturedPiece) * 10;
            // Bonus for capturing with less valuable piece
            score += (getPieceValue(move.capturedPiece) - getPieceValue(move.piece)) * 5;
        }

        // Check moves
        Piece[][] newBoard = getBoard(tmpMoves);
        String opponentColor = currentColor.equals("White") ? "Black" : "White";
        if (underCheck(newBoard, opponentColor)) {
            score += CHECK_VALUE;
        }

        // Moves that defend king from check
        if (underCheck(board, currentColor) && !underCheck(newBoard, currentColor)) {
            score += CHECK_VALUE * 2;
        }

        // Bonus for moves towards enemy king
        int[] enemyKingPos = findKingPosition(board, opponentColor);
        if (enemyKingPos != null) {
            int distanceToKing = Math.abs(move.toRow - enemyKingPos[0]) + Math.abs(move.toCol - enemyKingPos[1]);
            score += (7 - distanceToKing) * 10;
        }

        return score;
    }

    /**
     * Get piece value for evaluation.
     */
    private static int getPieceValue(Piece piece) {
        if (piece == null) return 0;
        return switch (piece.name) {
            case "Pawn" -> PAWN_VALUE;
            case "Knight", "Bishop" -> KNIGHT_BISHOP_VALUE;
            case "Rook" -> ROOK_VALUE;
            case "Queen" -> QUEEN_VALUE;
            case "King" -> KING_VALUE;
            default -> 0;
        };
    }

    /**
     * Public method to get the best move with enhancements.
     */
    public static Move getBestMove(ArrayList<Move> moves, int baseDepth, boolean isMaximizingPlayer) {
        int adaptiveDepth = Math.min(baseDepth , MAX_DEPTH);
        Move move = minimax(moves, adaptiveDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizingPlayer);
        ArrayList<Move> validMoves = validMoves(getBoard(moves), isMaximizingPlayer ? "White" : "Black", moves.isEmpty() ? null : moves.getLast());
        if (validMoves.isEmpty()) return null;
        if (move.fromRow == -1) move = validMoves.get(new Random().nextInt(validMoves.size()));
        return move;
    }

    /**
     * Helper to check if the game is over (checkmate or stalemate).
     */
    private static boolean isGameOver(ArrayList<Move> moves, boolean isMaximizingPlayer) {
        return isCheckmate(moves, isMaximizingPlayer ? "Black" : "White") || isStalemate(moves, isMaximizingPlayer ? "Black" : "White");
    }

    private static boolean isCheckmate(ArrayList<Move> moves, String color) {
        Piece[][] board = getBoard(moves);
        return underCheck(board, color) && validMoves(board, color, moves.isEmpty() ? null : moves.getLast()).isEmpty();
    }

    private static boolean isStalemate(ArrayList<Move> moves, String color) {
        Piece[][] board = getBoard(moves);
        return !underCheck(board, color) && validMoves(board, color, moves.isEmpty() ? null : moves.getLast()).isEmpty();
    }

    /**
     * Get the current board state after applying the moves.
     */
    private static Piece[][] getBoard(ArrayList<Move> moves) {
        Piece[][] board = Piece.getInitialSetup();
        for (Move move : moves) {
            // Handle the basic move
            board[move.toRow][move.toCol] = board[move.fromRow][move.fromCol];
            board[move.fromRow][move.fromCol] = null;

            // Handle pawn promotion
            if (board[move.toRow][move.toCol].name.equals("Pawn")) {
                if (move.toRow == 7 || move.toRow == 0) {
                    // Create a new Pieces.Queen of the same color as the pawn
                    String color = board[move.toRow][move.toCol].color;
                    board[move.toRow][move.toCol] = new Queen(color);
                }
                // Handle en passant capture
                else if (Math.abs(move.toCol-move.fromCol) == 1 && move.capturedPiece == null) {
                    board[move.fromRow][move.toCol] = null;
                }
            }
            // Handle castling
            else if (move.piece.name.equals("King")) {
                if (move.toCol - move.fromCol == 2) {
                    board[move.fromRow][5] = board[move.fromRow][7];
                    board[move.fromRow][7] = null;
                } else if (move.toCol - move.fromCol == -2) {
                    board[move.fromRow][3] = board[move.fromRow][0];
                    board[move.fromRow][0] = null;
                }
            }
        }
        return board;
    }

    /**
     * Generate all valid moves for a given color.
     */
    private static ArrayList<Move> validMoves(Piece[][] board, String color, Move lastMove) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if (board[fromRow][fromCol] == null || !board[fromRow][fromCol].color.equals(color)) {
                    continue;
                }
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol, board, color, lastMove)) {
                            moves.add(new Move(fromRow, fromCol, toRow, toCol, board[fromRow][fromCol], board[toRow][toCol], 0, 0));
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Check if a move is valid based on game rules.
     */
    private static boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] boardState, String color, Move lastMove) {
        boolean result = false;
        Piece selectedPiece = boardState[fromRow][fromCol];
        Piece piece = boardState[toRow][toCol];
        if (selectedPiece != null && color.equals(selectedPiece.color) &&
                ((boardState[toRow][toCol] == null) ||
                        (boardState[toRow][toCol] != null &&
                        !selectedPiece.color.equals(boardState[toRow][toCol].color))) &&
                        selectedPiece.canMove(fromRow, fromCol, toRow, toCol, boardState, lastMove == null ? new Move(fromRow, fromCol, toRow, toCol, selectedPiece, null, 0, 0) : lastMove)) {

            if (selectedPiece.name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
                int dir = (toCol-fromCol > 0 ? 1 : -1);
                if (underCheck(boardState, color)) {
                    return false;
                }
                if (!checkValidateMove(fromRow, fromCol, toRow, toCol-dir, boardState, color, null)) {
                    return false;
                }
            }

            boardState[toRow][toCol] = boardState[fromRow][fromCol];
            boardState[fromRow][fromCol] = null;
            if (!underCheck(boardState, color)) {
                result = true;
            }
            boardState[fromRow][fromCol] = boardState[toRow][toCol];
            boardState[toRow][toCol] = piece;
        }
        return result;
    }

    /**
     * Check if the king of a given color is under check.
     */
    static boolean underCheck(Piece[][] board, String color) {
        int[] kingPosition = findKingPosition(board, color);
        if (kingPosition == null) return true;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && !piece.color.equals(board[kingPosition[0]][kingPosition[1]].color) && piece.canMove(i, j, kingPosition[0], kingPosition[1], board)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find the position of the king for a given color.
     */
    private static int[] findKingPosition(Piece[][] board, String color) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && piece.name.equals("King") && piece.color.equals(color)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }
}
