import java.util.ArrayList;

public class AI_Minimax extends Player {
    // Evaluation constants for piece values
    private static final int PAWN_VALUE = 10;
    private static final int KNIGHT_BISHOP_VALUE = 30;
    private static final int ROOK_VALUE = 50;
    private static final int QUEEN_VALUE = 90;
    private static final int KING_VALUE = 900;

    // Depth constants
    private static final int MAX_DEPTH = 5;

    // Piece-square tables for positional evaluation
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
     * Improved minimax algorithm with alpha-beta pruning and quiescence search.
     */
    private static Move minimax(ArrayList<Move> moves, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        Move bestMove = new Move(-1, -1, -1, -1, null, null, 0, 0);

        // Base case: reached maximum depth or game-ending condition
        if (depth == 0 || isGameOver(moves, isMaximizingPlayer)) {
            bestMove.eval = quiescenceSearch(moves, alpha, beta, isMaximizingPlayer);
            return bestMove;
        }

        // Get valid moves and sort them
        String currentColor = isMaximizingPlayer ? "White" : "Black";
        ArrayList<Move> validMoves = validMoves(getBoard(moves), currentColor);
        validMoves.sort((m1, m2) -> Integer.compare(calculateMoveImportance(m2, moves, currentColor), calculateMoveImportance(m1, moves, currentColor)));

        // Maximizing player (White)
        if (isMaximizingPlayer) {
            int maxEval = Integer.MIN_VALUE;
            for (Move move : validMoves) {
                ArrayList<Move> tmpMoves = new ArrayList<>(moves);
                tmpMoves.add(move);
                System.out.println(move);
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
        int standPat = evaluate(moves);
        if (isMaximizingPlayer) {
            if (standPat >= beta) return beta;
            alpha = Math.max(alpha, standPat);
        } else {
            if (standPat <= alpha) return alpha;
            beta = Math.min(beta, standPat);
        }

        String currentColor = isMaximizingPlayer ? "White" : "Black";
        ArrayList<Move> validMoves = validMoves(getBoard(moves), currentColor);
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
     * Improved evaluation function using piece-square tables and mobility.
     */
    private static int evaluate(ArrayList<Move> moves) {
        Piece[][] board = getBoard(moves);
        int score = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece == null) continue;

                int pieceValue = getPieceValue(piece);
                int positionBonus = getPieceSquareValue(piece, i, j);

                score += piece.color.equals("White") ? (pieceValue + positionBonus) : -(pieceValue + positionBonus);
            }
        }

        score += evaluateMobility(board, "White") - evaluateMobility(board, "Black");
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
     * Evaluate piece mobility as a bonus.
     */
    private static int evaluateMobility(Piece[][] board, String color) {
        int mobilityBonus = 0;
        ArrayList<Move> validMoves = validMoves(board, color);
        mobilityBonus += validMoves.size();
        return mobilityBonus;
    }

    /**
     * Calculate move importance for move ordering.
     * Prioritizes captures and moves that put opponent in check.
     */
    private static int calculateMoveImportance(Move move, ArrayList<Move> previousMoves, String currentColor) {
        Piece[][] board = getBoard(previousMoves);
        int score = 0;

        // Capture moves are highly valued
        if (board[move.toRow][move.toCol] != null) {
            score += getPieceValue(board[move.toRow][move.toCol]);
        }

        // Check moves are also prioritized
        ArrayList<Move> tmpMoves = new ArrayList<>(previousMoves);
        tmpMoves.add(move);
        Piece[][] newBoard = getBoard(tmpMoves);

        String opponentColor = currentColor.equals("White") ? "Black" : "White";
        if (underCheck(newBoard, opponentColor)) {
            score += 50; // Bonus for check moves
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
        int adaptiveDepth = Math.min(baseDepth + (moves.size() / 10), MAX_DEPTH);
        return minimax(moves, adaptiveDepth, Integer.MIN_VALUE, Integer.MAX_VALUE, isMaximizingPlayer);
    }

    /**
     * Helper to check if the game is over (checkmate or stalemate).
     */
    private static boolean isGameOver(ArrayList<Move> moves, boolean isMaximizingPlayer) {
        Piece[][] board = getBoard(moves);
        String currentColor = isMaximizingPlayer ? "White" : "Black";

        return playerCantMove(board, currentColor, moves) || underCheck(board, currentColor);
    }

    /**
     * Get the current board state after applying the moves.
     */
    private static Piece[][] getBoard(ArrayList<Move> moves) {
        Piece[][] board = Piece.getInitialSetup();
        for (Move move : moves) {
            board[move.toRow][move.toCol] = board[move.fromRow][move.fromCol];
            board[move.fromRow][move.fromCol] = null;
        }
        return board;
    }

    /**
     * Generate all valid moves for a given color.
     */
    private static ArrayList<Move> validMoves(Piece[][] board, String color) {
        ArrayList<Move> moves = new ArrayList<>();
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                if (board[fromRow][fromCol] == null || !board[fromRow][fromCol].color.equals(color)) {
                    continue;
                }
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol, board, color, null)) {
                            moves.add(new Move(fromRow, fromCol, toRow, toCol, board[fromRow][fromCol], board[toRow][toCol], 0, 0));
                        }
                    }
                }
            }
        }
        return moves;
    }

    /**
     * Check if the player cannot make any valid moves.
     */
    private static boolean playerCantMove(Piece[][] board, String color, ArrayList<Move> moves) {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol, board, color, moves.isEmpty() ? null : moves.getLast())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Check if a move is valid based on game rules.
     */
    private static boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol, Piece[][] board, String color, Move lastMove) {
        Piece selectedPiece = board[fromRow][fromCol];
        Piece targetPiece = board[toRow][toCol];
        if (selectedPiece == null || !color.equals(selectedPiece.color)) return false;
        if (targetPiece != null && targetPiece.color.equals(selectedPiece.color)) return false;

        return selectedPiece.canMove(fromRow, fromCol, toRow, toCol, board, lastMove);
    }

    /**
     * Check if the king of a given color is under check.
     */
    private static boolean underCheck(Piece[][] board, String color) {
        int[] kingPosition = findKingPosition(board, color);
        if (kingPosition == null) return false;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = board[i][j];
                if (piece != null && !piece.color.equals(color) && piece.canMove(i, j, kingPosition[0], kingPosition[1], board, null)) {
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
