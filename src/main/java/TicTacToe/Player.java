package TicTacToe;

public enum Player {
    X, O, EMPTY;

    public Player opposite() {
        if (this == X) return O;
        if (this == O) return X;
        return EMPTY;
    }
}
