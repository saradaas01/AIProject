package TicTacToe;

public class EvaluationClassic {

    public static int evaluate(Board board, Player humanPlayer) {
        Player aiPlayer = humanPlayer.opposite();
        Player winner = board.getWinner();

        if (winner == humanPlayer) return 1000;
        if (winner == aiPlayer) return -1000;
        if (board.isFull()) return 0;

        int score = 0;


        if (board.getCell(1, 1) == humanPlayer) score += 3;
        else if (board.getCell(1, 1) == aiPlayer) score -= 3;


        int[][] corners = { {0,0}, {0,2}, {2,0}, {2,2} };
        for (int[] c : corners) {
            Player p = board.getCell(c[0], c[1]);
            if (p == humanPlayer) score += 1;
            else if (p == aiPlayer) score -= 1;
        }

        score += evalLine(board, humanPlayer, 0,0,0,1,0,2);
        score += evalLine(board, humanPlayer, 1,0,1,1,1,2);
        score += evalLine(board, humanPlayer, 2,0,2,1,2,2);

        score += evalLine(board, humanPlayer, 0,0,1,0,2,0);
        score += evalLine(board, humanPlayer, 0,1,1,1,2,1);
        score += evalLine(board, humanPlayer, 0,2,1,2,2,2);

        score += evalLine(board, humanPlayer, 0,0,1,1,2,2);
        score += evalLine(board, humanPlayer, 0,2,1,1,2,0);

        return score;
    }

    private static int evalLine(Board board, Player human, int r1,int c1,int r2,int c2,int r3,int c3) {
        Player ai = human.opposite();

        Player[] line = {
                board.getCell(r1,c1),
                board.getCell(r2,c2),
                board.getCell(r3,c3)
        };

        int humanCount = 0;
        int aiCount = 0;

        for (Player p : line) {
            if (p == human) humanCount++;
            else if (p == ai) aiCount++;
        }

        if (humanCount> 0 && aiCount > 0) return 0;

        int val = 0;

        if (humanCount == 2 && aiCount == 0) val+= 6;
        else if (humanCount == 1 && aiCount == 0) val+= 2;

        if (aiCount == 2 && humanCount == 0) val-= 5;
        else if (aiCount == 1 && humanCount == 0) val-= 2;

        return val;
    }
}
