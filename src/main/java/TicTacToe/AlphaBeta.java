package TicTacToe;

import java.util.*;

public class AlphaBeta {

    /** Return ALL evaluation scores for each AI move **/
    public static List<String> getAllMoveEvaluations(Board board,
                                                     Player humanPlayer,
                                                     Player aiPlayer,
                                                     int depth) {
        List<String> out = new ArrayList<>();

        for (Move move : board.getLegalMoves()) {
            Board next = new Board(board);
            next.setCell(move.getRow(), move.getCol(), aiPlayer);

            int score = alphaBeta(next,
                    humanPlayer,
                    humanPlayer,
                    depth-1,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE);

            out.add("AI move ("+(move.getRow()+1)+","+(move.getCol()+1)+") → Score = " + score);
        }

        return out;
    }


    /** AI = MINIMIZING player **/
    public static Move findBestMoveForAI(Board board,
                                         Player humanPlayer,
                                         Player aiPlayer,
                                         int depth) {
        int bestScore = Integer.MAX_VALUE;
        Move bestMove = null;

        for (Move move : board.getLegalMoves()) {

            Board next = new Board(board);
            next.setCell(move.getRow(), move.getCol(), aiPlayer);

            int score = alphaBeta(next,
                    humanPlayer,
                    humanPlayer,
                    depth - 1,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE);

            move.setScore(score);

            if (score < bestScore || bestMove == null) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }


    /** Alpha–beta search **/
    private static int alphaBeta(Board board,
                                 Player playerToMove,
                                 Player maxPlayer,
                                 int depth,
                                 int alpha,
                                 int beta) {

        if (depth == 0 || board.isTerminal()) {
            return EvaluationClassic.evaluate(board, maxPlayer);
        }

        boolean isMaxNode = (playerToMove == maxPlayer);

        if (isMaxNode) {
            // HUMAN TURN (MAX)
            int maxEval = Integer.MIN_VALUE;

            for (Move move : board.getLegalMoves()) {
                Board next = new Board(board);
                next.setCell(move.getRow(), move.getCol(), playerToMove);

                int eval = alphaBeta(next,
                        playerToMove.opposite(),
                        maxPlayer,
                        depth - 1,
                        alpha,
                        beta);

                maxEval = Math.max(maxEval, eval);
                alpha = Math.max(alpha, eval);

                if (beta <= alpha) break;
            }

            return maxEval;

        } else {
            // AI TURN (MIN)
            int minEval = Integer.MAX_VALUE;

            for (Move move : board.getLegalMoves()) {
                Board next = new Board(board);
                next.setCell(move.getRow(), move.getCol(), playerToMove);

                int eval = alphaBeta(next,
                        playerToMove.opposite(),
                        maxPlayer,
                        depth - 1,
                        alpha,
                        beta);

                minEval = Math.min(minEval, eval);
                beta = Math.min(beta, eval);

                if (beta <= alpha) break;
            }

            return minEval;
        }
    }
}
