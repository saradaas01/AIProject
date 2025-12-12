package TicTacToe;

import java.util.ArrayList;
import java.util.List;

public class Board {

    private Player[][] cells;
    public static final int SIZE = 3;

    public Board() {
        cells = new Player[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = Player.EMPTY;
            }
        }
    }

    // Copy constructor (useful for AI search)
    public Board(Board other) {
        cells = new Player[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                cells[r][c] = other.cells[r][c];
            }
        }
    }

    public Player getCell(int row, int col) {
        return cells[row][col];
    }

    public void setCell(int row, int col, Player p) {
        cells[row][col] = p;
    }

    public boolean isEmptyCell(int row, int col) {
        return cells[row][col] == Player.EMPTY;
    }

    public List<Move> getLegalMoves() {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == Player.EMPTY) {
                    moves.add(new Move(r, c));
                }
            }
        }
        return moves;
    }

    public boolean isFull() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (cells[r][c] == Player.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    // Returns X, O, or EMPTY if no winner yet. (We’ll handle draw separately.)
    public Player getWinner() {
        // rows
        for (int r = 0; r < SIZE; r++) {
            if (cells[r][0] != Player.EMPTY &&
                    cells[r][0] == cells[r][1] &&
                    cells[r][1] == cells[r][2]) {
                return cells[r][0];
            }
        }

        // columns
        for (int c = 0; c < SIZE; c++) {
            if (cells[0][c] != Player.EMPTY &&
                    cells[0][c] == cells[1][c] &&
                    cells[1][c] == cells[2][c]) {
                return cells[0][c];
            }
        }

        // main diagonal
        if (cells[0][0] != Player.EMPTY &&
                cells[0][0] == cells[1][1] &&
                cells[1][1] == cells[2][2]) {
            return cells[0][0];
        }

        // other diagonal
        if (cells[0][2] != Player.EMPTY &&
                cells[0][2] == cells[1][1] &&
                cells[1][1] == cells[2][0]) {
            return cells[0][2];
        }

        return Player.EMPTY; // no winner yet
    }

    public boolean isTerminal() {
        return getWinner() != Player.EMPTY || isFull();
    }
}
