package GameManager;

import Pieces.*;
import Utilities.*;
import AI.AI_Minimax;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class GameLogic {
    public final Player player1 = new Player();
    public Player player2 = new Player();
    public Player currentPlayer = player1;

    public final ArrayList<Move> moves = new ArrayList<>();
    public Piece[][] boardState = Piece.getInitialSetup();

    private int[] whiteKingPosition = new int[] { 7, 3 };
    private int[] blackKingPosition = new int[] { 0, 3 };
    public int movesToStalemate = 0;

    public int[] getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public int[] getBlackKingPosition() {
        return blackKingPosition;
    }

    public int updateStalemateCounter(Piece movedPiece) {
        if (movedPiece.name.equals("Pawn")) {
            return 0;
        }
        return movesToStalemate + 1;
    }

    public int timerDuration = 10; // Default

    public GameLogic() {
        // Initialize players or other state if needed
    }

    public void reset() {
        boardState = Piece.getInitialSetup();
        player1.capturedPieces.clear();
        player2.capturedPieces.clear();
        moves.clear();
        currentPlayer = player1;
        movesToStalemate = 0;
        whiteKingPosition = new int[] { 7, 3 };
        blackKingPosition = new int[] { 0, 3 };
    }

    public boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol, int whiteTime, int blackTime) {
        boolean result = false;
        Piece selectedPiece = boardState[fromRow][fromCol];
        Piece piece = boardState[toRow][toCol];
        if (selectedPiece != null && currentPlayer.getColor().equals(selectedPiece.color) &&
                ((boardState[toRow][toCol] == null) || (boardState[toRow][toCol] != null &&
                        !selectedPiece.color.equals(boardState[toRow][toCol].color)))
                &&
                selectedPiece.canMove(fromRow, fromCol, toRow, toCol, boardState,
                        (!moves.isEmpty() && moves.getLast() != null) ? moves.getLast()
                                : new Move(fromRow, fromCol, toRow, toCol, selectedPiece, null,
                                        whiteTime, blackTime))) {
            if (selectedPiece.name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
                int dir = (toCol - fromCol > 0 ? 1 : -1);
                if (underCheck(boardState, currentPlayer.getColor())) {
                    return false;
                }
                if (!checkValidateMove(fromRow, fromCol, toRow, toCol - dir, whiteTime, blackTime)) {
                    return false;
                }
            }
            boardState[toRow][toCol] = boardState[fromRow][fromCol];
            boardState[fromRow][fromCol] = null;
            if (!underCheck(boardState, currentPlayer.getColor())) {
                result = true;
            }
            boardState[fromRow][fromCol] = boardState[toRow][toCol];
            boardState[toRow][toCol] = piece;
        }
        return result;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol, int whiteTime, int blackTime) {
        boolean castling = false;
        moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[toRow][toCol],
                whiteTime, blackTime));

        int dir = (toCol - fromCol > 0 ? 1 : -1);
        if (boardState[fromRow][fromCol].name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
            castling = !underCheck(boardState, currentPlayer.getColor());
        } else if (boardState[fromRow][fromCol].name.equals("Pawn") && (Math.abs(toCol - fromCol) == 1)
                && boardState[toRow][toCol] == null) {
            currentPlayer.capturedPieces.add(boardState[fromRow][toCol]);
            moves.removeLast();
            Move move = moves.getLast();
            boardState[move.toRow][move.toCol] = null;
            // Note: UI update for removing square needs to be handled by Game.java or via
            // callback
            moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[fromRow][toCol],
                    whiteTime, blackTime));
        } else if (boardState[toRow][toCol] != null) {
            currentPlayer.capturedPieces.add(boardState[toRow][toCol]);
        }

        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = null;
        boardState[toRow][toCol].haveMove = true;

        if (castling) {
            if (dir > 0) {
                movePiece(fromRow, 7, toRow, 5, whiteTime, blackTime);
            } else {
                movePiece(fromRow, 0, toRow, 3, whiteTime, blackTime);
            }
            moves.removeLast();
        }

        saveMovesToFile();
    }

    public Move undo() {
        if (!moves.isEmpty()) {
            Player player = currentPlayer.getColor().equals(player1.getColor()) ? player2 : player1;
            Move move = moves.removeLast();

            boardState[move.fromRow][move.fromCol] = move.piece;
            boardState[move.fromRow][move.fromCol].haveMove = move.haveMoved;
            boardState[move.toRow][move.toCol] = move.capturedPiece;

            if (move.piece.name.equals("Pawn") && (Math.abs(move.toCol - move.fromCol) == 1)
                    && move.capturedPiece == null) {
                boardState[move.fromRow][move.toCol] = new Pawn(move.piece.color.equals("White") ? "Black" : "White");
                boardState[move.fromRow][move.toCol].haveMove = true;
                player.capturedPieces.removeLast();
            } else if (move.piece.name.equals("King")) {
                if (move.toCol - move.fromCol == 2) {
                    boardState[move.fromRow][7] = new Rook(move.piece.color);
                    boardState[move.fromRow][5] = null;
                    boardState[move.fromRow][7].haveMove = true;
                } else if (move.toCol - move.fromCol == -2) {
                    boardState[move.fromRow][0] = new Rook(move.piece.color);
                    boardState[move.fromRow][3] = null;
                    boardState[move.fromRow][0].haveMove = true;
                }
            }

            if (move.capturedPiece != null) {
                player.capturedPieces.removeLast();
            }

            currentPlayer = currentPlayer.getColor().equals(player1.getColor()) ? player2 : player1;
            if (currentPlayer instanceof AI_Minimax) {
                return undo(); // Recursive undo for AI
            }

            saveMovesToFile();
            return move;
        }
        return null;
    }

    public boolean underCheck(Piece[][] Pieces, String color) {
        updateKingPosition();
        int[] position = color.equals("White") ? whiteKingPosition : blackKingPosition;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Pieces[i][j] != null && !Pieces[i][j].color.equals(Pieces[position[0]][position[1]].color)
                        && Pieces[i][j].canMove(i, j, position[0], position[1], Pieces)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateKingPosition() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j] != null && boardState[i][j].name.equals("King")) {
                    if (boardState[i][j].color.equals("White")) {
                        whiteKingPosition = new int[] { i, j };
                    } else {
                        blackKingPosition = new int[] { i, j };
                    }
                }
            }
        }
    }

    public boolean isCheckmate() {
        if (!underCheck(boardState, currentPlayer.getColor()))
            return false;
        return playerCantMove();
    }

    public boolean isStalemate() {
        if (!underCheck(boardState, currentPlayer.getColor()) && (movesToStalemate >= 50 || isThreefoldRepetition()))
            return true;
        return playerCantMove();
    }

    private boolean playerCantMove() {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol, 0, 0)) { // Time doesn't matter for
                                                                                       // validation here
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean isThreefoldRepetition() {
        if (moves.size() < 12)
            return false;
        return moves.getLast().equals(moves.get(moves.size() - 5))
                && moves.getLast().equals(moves.get(moves.size() - 9))
                && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 6))
                && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 10));
    }

    public void saveMovesToFile() {
        try {
            new FileWriter("lastSave.txt", false).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (Move move : moves) {
            try (FileWriter writer = new FileWriter("lastSave.txt", true)) {
                writer.write(
                        move.fromRow + "," + move.fromCol + "," + move.toRow + "," + move.toCol + "," + move.timers[0]
                                + "," + move.timers[1] + (move.promoteTo == null ? "" : "," + move.promoteTo) + "\n");
            } catch (java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
        savePlayers();
    }

    public void savePlayers() {
        try (FileWriter writer = new FileWriter("players.txt", false)) {
            writer.write(player1.getName() + "," + player1.getColor() + "," + timerDuration + "\n");
            writer.write(player2.getName() + "," + player2.getColor() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readPlayers() {
        Path path = Paths.get("players.txt");
        try {
            java.util.List<String> lines = Files.readAllLines(path);
            String[] parts = lines.getFirst().split(",");
            player1.setName(parts[0].trim());
            player1.setColor(parts[1].trim());
            timerDuration = Integer.parseInt(parts[2].trim());
            parts = lines.get(1).split(",");
            if (parts[0].trim().equals("AI MiniMax"))
                player2 = new AI_Minimax();
            player2.setName(parts[0].trim());
            player2.setColor(parts[1].trim());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void restoreMovesFromFile() {
        Path path = Paths.get("lastSave.txt");
        readPlayers();
        try {
            java.util.List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length < 6)
                    continue;
                int fromRow = Integer.parseInt(parts[0].trim());
                int fromCol = Integer.parseInt(parts[1].trim());
                int toRow = Integer.parseInt(parts[2].trim());
                int toCol = Integer.parseInt(parts[3].trim());
                int whiteTime = Integer.parseInt(parts[4].trim());
                int blackTime = Integer.parseInt(parts[5].trim());
                String promoteTo = parts.length > 6 ? parts[6].trim() : null;
                Move move = new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol],
                        boardState[toRow][toCol], whiteTime, blackTime);
                moves.add(move);
                if (boardState[toRow][toCol] != null) {
                    currentPlayer.capturedPieces.add(boardState[toRow][toCol]);
                }
                currentPlayer = currentPlayer.getColor().equals(player1.getColor()) ? player2 : player1;
                boardState[toRow][toCol] = boardState[fromRow][fromCol];
                boardState[toRow][toCol].haveMove = true;
                boardState[fromRow][fromCol] = null;
                // Handle pawn promotion
                if (boardState[move.toRow][move.toCol].name.equals("Pawn")) {
                    if (move.toRow == 7 || move.toRow == 0) {
                        String color = boardState[move.toRow][move.toCol].color;
                        boardState[move.toRow][move.toCol] = switch (promoteTo != null ? promoteTo : "Queen") {
                            case "Queen" -> new Queen(color);
                            case "Rook" -> new Rook(color);
                            case "Bishop" -> new Bishop(color);
                            case "Knight" -> new Knight(color);
                            default -> new Pawn(color);
                        };
                    } else if (Math.abs(move.toCol - move.fromCol) == 1 && move.capturedPiece == null) {
                        boardState[move.fromRow][move.toCol] = null;
                    }
                } else if (move.piece.name.equals("King")) {
                    if (move.toCol - move.fromCol == 2) {
                        boardState[move.fromRow][5] = boardState[move.fromRow][7];
                        boardState[move.fromRow][7] = null;
                    } else if (move.toCol - move.fromCol == -2) {
                        boardState[move.fromRow][3] = boardState[move.fromRow][0];
                        boardState[move.fromRow][0] = null;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
