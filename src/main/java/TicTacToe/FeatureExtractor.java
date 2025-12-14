package TicTacToe;

public class FeatureExtractor {

    public static int[] extractFeatures(Board board, Player humanPlayer) {
        // We assume dataset was built from X’s perspective.
        // So we treat X as "reference player".
        Player aiPlayer = humanPlayer.opposite();

        int f1_X_count = 0;
        int f2_O_count = 0;
        int f3_X_almost = 0;
        int f4_O_almost = 0;
        int f5_X_center = 0;
        int f6_X_corners = 0;

        // Count X and O
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Player p = board.getCell(r, c);
                if (p == Player.X) f1_X_count++;
                else if (p == Player.O) f2_O_count++;
            }
        }

        // X center
        if (board.getCell(1, 1) == Player.X) {
            f5_X_center = 1;
        }

        // X corners
        int[][] corners = { {0,0}, {0,2}, {2,0}, {2,2} };
        for (int[] cc : corners) {
            if (board.getCell(cc[0], cc[1]) == Player.X) {
                f6_X_corners++;
            }
        }

        // Almost-win lines for X and O
        int[][] lines = {
                {0,0, 0,1, 0,2},
                {1,0, 1,1, 1,2},
                {2,0, 2,1, 2,2},
                {0,0, 1,0, 2,0},
                {0,1, 1,1, 2,1},
                {0,2, 1,2, 2,2},
                {0,0, 1,1, 2,2},
                {0,2, 1,1, 2,0}
        };

        for (int[] L : lines) {
            int xr = 0, or = 0, empty = 0;

            Player p1 = board.getCell(L[0], L[1]);
            Player p2 = board.getCell(L[2], L[3]);
            Player p3 = board.getCell(L[4], L[5]);

            Player[] arr = { p1, p2, p3 };
            for (Player p : arr) {
                if (p == Player.X) xr++;
                else if (p == Player.O) or++;
                else empty++;
            }

            if (xr == 2 && empty == 1) f3_X_almost++;
            if (or == 2 && empty == 1) f4_O_almost++;
        }

        return new int[] {
                f1_X_count,
                f2_O_count,
                f3_X_almost,
                f4_O_almost,
                f5_X_center,
                f6_X_corners
        };
    }
}
