import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Game extends JFrame{
    public ChessBoard board;
    private final Player player1 = new Player();
    private final Player player2 = new Player();
    private int timerDuration;
    private Piece selectedPiece = null;
    private int selectedRow = -1, selectedCol = -1;
    private int[] whiteKingPosition = new int[]{7, 3};
    private int[] blackKingPosition = new int[]{0, 3};
    private final ArrayList<Move> moves = new ArrayList<>();
    private final Piece[][] boardState = Piece.getInitialSetup();
    private Player currentPlayer;
    private final Color color = Color.LIGHT_GRAY;

    public static void main(String[] args) {
        new Game().startGame();
    }

    private void updateIcons() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j] != null)
                    board.addSquare(i, j, boardState[i][j].getIcon());
            }
        }
    }

    private void checkForCheckmate() {
        new Thread(() -> {
            while (true) {
                if (isCheckmate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                    EventQueue.invokeLater(() -> {
                        board.whiteTimer.stop();
                        board.blackTimer.stop();
                        JOptionPane.showMessageDialog(this, (currentPlayer.getColor().equals("White") ? "Black" : "White") + " has won!");

                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            board.dispose();
                            initGame();
                        } else {
                            System.exit(0);
                        }
                    });
                    break;
                } else if (isStalemate() && (board.whiteTimer.isRunning() || board.blackTimer.isRunning())) {
                    EventQueue.invokeLater(() -> {
                        board.whiteTimer.stop();
                        board.blackTimer.stop();
                        JOptionPane.showMessageDialog(this, "It's stalemate!");

                        int restartOption = JOptionPane.showConfirmDialog(this, "Do you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION);
                        boolean restartChoice = restartOption == JOptionPane.YES_OPTION;

                        if (restartChoice) {
                            board.dispose();
                            initGame();
                        } else {
                            System.exit(0);
                        }
                    });
                }
                try {
                    //noinspection BusyWait
                    Thread.sleep(100); // Checkmate check interval
                } catch (InterruptedException e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startGame() {
        // Prompt for player details
        player1.setName(JOptionPane.showInputDialog(this, "Player 1 name:"));
        if (player1.getName() == null || player1.getName().trim().isEmpty()) {
            player1.setName("Player 1");
        }

        player2.setName(JOptionPane.showInputDialog(this, "Player 2 name:"));
        if (player2.getName() == null || player2.getName().trim().isEmpty()) {
            player2.setName("Player 2");
        }

        Object[] options = {"White", "Black"};
        player1.setColor((String) JOptionPane.showInputDialog(this, player1.getName() + " color:", "Color Selection",
                JOptionPane.PLAIN_MESSAGE, null, options, "White"));

        if (player1.getColor() == null) {
            player1.setColor("White"); // Default to White if no selection
        }

        player2.setColor(player1.getColor().equals("White") ? "Black" : "White");

        // Prompt for timer duration
        String timerInput = JOptionPane.showInputDialog(this, "Enter timer duration in minutes (default 10):");
        try {
            timerDuration = Integer.parseInt(timerInput);
            if (timerDuration <= 0 || timerDuration > 60) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            timerDuration = 10; // Default timer duration
        }

        currentPlayer = player1;
        initGame();
    }

    private void initGame() {
        EventQueue.invokeLater(() -> {
            // Initialize the chessboard
            board = new ChessBoard(new int[]{800, 800}, color, timerDuration);
            updateIcons();
            setCH();
            moves.add(new Move(-1, -1, -1, -1, null, null));
            board.setVisible(true);

            // Add player name based on color
            JLabel player1NameLabel = new JLabel(player1.getName(), SwingConstants.CENTER);
            player1NameLabel.setFont(new Font("Arial", Font.BOLD, 20));
            JLabel player2NameLabel = new JLabel(player2.getName(), SwingConstants.CENTER);
            player2NameLabel.setFont(new Font("Arial", Font.BOLD, 20));

            if ("White".equals(player1.getColor())) {
                board.rightPanel.add(player2NameLabel, 0);
                board.rightPanel.add(player1NameLabel);
            } else {
                board.rightPanel.add(player1NameLabel, 0);
                board.rightPanel.add(player2NameLabel);
            }

            checkForCheckmate();
        });
    }

    private void setCH() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                int finalI = i;
                int finalJ = j;
                board.getSquare(i, j).addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        handleClick(finalI, finalJ);
                    }
                });
            }
        }
    }

    private void handleClick(int row, int col) {
        board.clearHighlights();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board.removeDot(i, j, boardState[i][j]);
            }
        }
        if (selectedPiece == null) {
            // Select a piece
            selectedPiece = boardState[row][col];
            if (selectedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer.getColor().equals(selectedPiece.color)) {
                    assistant();
                }
            }
        } else {
            // Attempt to move the selected piece
            if (checkValidateMove(selectedRow, selectedCol, row, col)) {
                movePiece(selectedRow, selectedCol, row, col);
                System.out.print(moves.size() % 2 == 0 ? (moves.size()+1)/2 + ") " + moves.getLast().toString() : "\t | " + moves.getLast().toString() + "\n");
                if (selectedPiece.name.equals("Pawn") && (row == 0 || row == 7)) {
                    selectedPiece.promote(boardState, row, col, "Queen");
                    board.removeSquare(row, col);
                    board.addSquare(row, col, boardState[row][col].getIcon());
                }
                board.switchTimers(currentPlayer.getColor());
                currentPlayer.setColor(currentPlayer.getColor().equals("White") ? "Black" : "White");
            }
            selectedPiece = boardState[row][col];
            if (selectedPiece != null) {
                selectedRow = row;
                selectedCol = col;
                if (currentPlayer.getColor().equals(selectedPiece.color)) {
                    assistant();
                }
            } else {
                selectedRow = -1;
                selectedCol = -1;
            }
        }
        if (underCheck(boardState, "White")) {
            board.highlightSquare(whiteKingPosition[0], whiteKingPosition[1], Color.RED);
        } else if (underCheck(boardState, "Black")) {
            board.highlightSquare(blackKingPosition[0], blackKingPosition[1], Color.RED);
        }

        board.highlightSquare(row, col, Color.YELLOW);
        board.repaint();
        board.revalidate();
    }

    private void updateKingPosition() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (boardState[i][j] != null && boardState[i][j].name.equals("King")) {
                    if (boardState[i][j].color.equals("White")) {
                        whiteKingPosition = new int[]{i, j};
                    } else {
                        blackKingPosition = new int[]{i, j};
                    }
                    break;
                }
            }
        }
    }

    private boolean underCheck(Piece[][] Pieces, String color) {
        updateKingPosition();
        int[] position = color.equals("White") ? whiteKingPosition : blackKingPosition;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (Pieces[i][j] != null && !Pieces[i][j].color.equals(Pieces[position[0]][position[1]].color) && Pieces[i][j].canMove(i, j, position[0], position[1], Pieces, moves.getLast() != null ? moves.getLast() : new Move(position[0], position[1], i, j, Pieces[position[0]][position[1]], null))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isThreefoldRepetition() {
        if (moves.size() < 11) return false;
        return moves.getLast().equals(moves.get(moves.size() - 5)) && moves.getLast().equals(moves.get(moves.size() - 9))
                && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 6)) && moves.get(moves.size() - 2).equals(moves.get(moves.size() - 10));
    }

    public boolean isCheckmate() {
        // Enhanced checkmate detection
        if (!underCheck(boardState, currentPlayer.getColor())) return false;

        return playerCantMove();
    }

    public boolean isStalemate() {
        if (Move.movesToStalemate >= 50 || isThreefoldRepetition()) return true;
        // Stalemate occurs when a player has no legal moves but is not in check
        if (underCheck(boardState, currentPlayer.getColor())) return false;

        return playerCantMove();
    }

    private boolean playerCantMove() {
        for (int fromRow = 0; fromRow < 8; fromRow++) {
            for (int fromCol = 0; fromCol < 8; fromCol++) {
                for (int toRow = 0; toRow < 8; toRow++) {
                    for (int toCol = 0; toCol < 8; toCol++) {
                        if (checkValidateMove(fromRow, fromCol, toRow, toCol)) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    private void assistant() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkValidateMove(selectedRow, selectedCol, i, j)) {
                    if (boardState[i][j] == null) {
                        board.addDot(i, j, calculateMidColor(color));
                    } else {
                        board.highlightSquare(i, j, calculateMidColor(Color.red));
                    }
                }
            }
        }
    }

    private boolean checkValidateMove(int fromRow, int fromCol, int toRow, int toCol) {
        boolean result = false;
        Piece selectedPiece = boardState[fromRow][fromCol];
        Piece piece = boardState[toRow][toCol];
        if (selectedPiece != null && currentPlayer.getColor().equals(selectedPiece.color) &&
                ((boardState[toRow][toCol] == null) || (boardState[toRow][toCol] != null &&
                        !selectedPiece.color.equals(boardState[toRow][toCol].color))) &&
                selectedPiece.canMove(fromRow, fromCol, toRow, toCol, boardState, moves.getLast() != null ? moves.getLast() : new Move(fromRow, fromCol, toRow, toCol, selectedPiece, null))) {
            int dir = (toCol-fromCol > 0 ? 1 : -1);
            if (selectedPiece.name.equals("King") && (Math.abs(toCol - fromCol) > 1)) {
                if (underCheck(boardState, currentPlayer.getColor())) {
                    return false;
                }
                if (!checkValidateMove(fromRow, fromCol, toRow, toCol-dir)) {
                    return false;
                }
                fromCol += dir;
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

    private void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        boolean castling = false;
        moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], boardState[toRow][toCol]));

        int dir = (toCol - fromCol > 0 ? 1 : -1);
        if (boardState[fromRow][fromCol].name.equals("King") && (Math.abs(toCol - fromCol) == 2)) {
            castling = !underCheck(boardState, currentPlayer.getColor());
        } else if (boardState[fromRow][fromCol].name.equals("Pawn") && (Math.abs(toCol - fromCol) == 1) && boardState[toRow][toCol] == null) {
            moves.removeLast();
            Move move = moves.getLast();
            boardState[move.toRow][move.toCol] = null;
            board.removeSquare(move.toRow, move.toCol);
            moves.add(new Move(fromRow, fromCol, toRow, toCol, boardState[fromRow][fromCol], null));
        }

        boardState[toRow][toCol] = boardState[fromRow][fromCol];
        boardState[fromRow][fromCol] = null;
        boardState[toRow][toCol].haveMove = true;

        board.moveSquare(fromRow, fromCol, toRow, toCol, boardState[toRow][toCol].getIcon());

        if (castling) {
            if (dir > 0) {
                movePiece(fromRow, 7, toRow, 5);
            } else {
                movePiece(fromRow, 0, toRow, 3);
            }
            moves.removeLast();
        }
    }

    private Color calculateMidColor(Color color2) {
        int r = (Color.WHITE.getRed() + color2.getRed()) / 2;
        int g = (Color.WHITE.getGreen() + color2.getGreen()) / 2;
        int b = (Color.WHITE.getBlue() + color2.getBlue()) / 2;
        return new Color(r, g, b);
    }
}
