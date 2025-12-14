package TicTacToe;

@FunctionalInterface
public interface BoardEvaluator {
    int evaluate(Board board, Player humanPlayer);
}
