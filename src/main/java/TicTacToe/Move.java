package TicTacToe;

public class Move {
    private int row;
    private int col;
    private int score;

    public Move(int r, int c) {
        this.row = r;
        this.col = c;
    }

    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getScore() { return score; }
    public void setScore(int s) { score = s; }
}
