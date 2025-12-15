package TicTacToe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MLModel {

    private static final int INPUT_SIZE = 6;   // f1..f6
    private static final int HIDDEN_SIZE = 10;

    private double[][] w1; // [hidden][input]
    private double[] b1;   // [hidden]
    private double[] w2;   // [hidden]
    private double b2;

    private boolean trained = false;

    public MLModel() {
        initWeights();
    }

    private void initWeights() {
        Random rnd = new Random(42);
        w1 = new double[HIDDEN_SIZE][INPUT_SIZE];
        b1 = new double[HIDDEN_SIZE];
        w2 = new double[HIDDEN_SIZE];

        for (int j = 0; j < HIDDEN_SIZE; j++) {
            for (int i = 0; i < INPUT_SIZE; i++) {
                w1[j][i] = (rnd.nextDouble() - 0.5) * 0.5; // small random
            }
            b1[j] = 0.0;
            w2[j] = (rnd.nextDouble() - 0.5) * 0.5;
        }
        b2 = 0.0;
    }

    /**
     * Train on CSV file:
     * columns: f1_X_count,f2_O_count,f3_X_almost_win,f4_O_almost_win,
     *          f5_X_center,f6_X_corners,label
     * label is +1 or -1
     */
    public void trainFromCsv(String path,
                             double trainRatio,
                             int epochs,
                             double learningRate) throws IOException {

        List<double[]> featureList = new ArrayList<>();
        List<Double> labelList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;

            // skip header
            line = br.readLine();

            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length != 7) continue; // skip bad rows

                double[] x = new double[INPUT_SIZE];
                for (int i = 0; i < INPUT_SIZE; i++) {
                    x[i] = Double.parseDouble(parts[i]);
                }

                // normalize counts to [0,1] (max is about 5)
                x[0] /= 5.0; // X_count
                x[1] /= 5.0; // O_count
                x[2] /= 5.0; // X_almost_win (0..3)
                x[3] /= 5.0; // O_almost_win
                x[4] /= 1.0; // X_center is already 0 or 1
                x[5] /= 5.0; // X_corners (0..4)

                double label = Double.parseDouble(parts[6]);
                label = (label >= 0) ? 1.0 : -1.0; // ensure ±1

                featureList.add(x);
                labelList.add(label);
            }
        }

        int n = featureList.size();
        if (n == 0) {
            System.out.println("No data loaded for ML model!");
            return;
        }

        // shuffle together
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Random rnd = new Random(42);
        for (int i = n - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int tmp = idx[i];
            idx[i] = idx[j];
            idx[j] = tmp;
        }

        double[][] X = new double[n][INPUT_SIZE];
        double[] Y = new double[n];
        for (int i = 0; i < n; i++) {
            X[i] = featureList.get(idx[i]);
            Y[i] = labelList.get(idx[i]);
        }

        int trainSize = (int) Math.round(trainRatio * n);
        if (trainSize < 1) trainSize = 1;
        if (trainSize > n - 1) trainSize = n - 1;
        int testSize = n - trainSize;

        // ---- training loop ----
        for (int epoch = 0; epoch < epochs; epoch++) {
            double totalLoss = 0.0;

            for (int i = 0; i < trainSize; i++) {
                double[] x = X[i];
                double target = Y[i];

                // forward
                double[] hidden = new double[HIDDEN_SIZE];
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double z = b1[j];
                    for (int k = 0; k < INPUT_SIZE; k++) {
                        z += w1[j][k] * x[k];
                    }
                    hidden[j] = Math.tanh(z);
                }

                double z2 = b2;
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    z2 += w2[j] * hidden[j];
                }
                double out = Math.tanh(z2); // in [-1,1]

                // loss = (out - target)^2
                double diff = out - target;
                totalLoss += diff * diff;

                // ---- backprop ----
                double dL_dout = 2 * diff;
                double dOut_dZ2 = 1 - out * out;
                double dL_dZ2 = dL_dout * dOut_dZ2;

                // gradients for w2 & b2
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double gradW2 = dL_dZ2 * hidden[j];
                    w2[j] -= learningRate * gradW2;
                }
                b2 -= learningRate * dL_dZ2;

                // backprop to hidden / w1 / b1
                for (int j = 0; j < HIDDEN_SIZE; j++) {
                    double dL_dHj = dL_dZ2 * w2[j];
                    double dHj_dZj = 1 - hidden[j] * hidden[j];
                    double dL_dZj = dL_dHj * dHj_dZj;

                    for (int k = 0; k < INPUT_SIZE; k++) {
                        double gradW1 = dL_dZj * x[k];
                        w1[j][k] -= learningRate * gradW1;
                    }
                    b1[j] -= learningRate * dL_dZj;
                }
            }

            // optional: print every 500 epochs
            if ((epoch + 1) % 500 == 0) {
                System.out.println("Epoch " + (epoch + 1) +
                        " avg train loss = " + (totalLoss / trainSize));
            }
        }

        // ---- test accuracy ----
        int correct = 0;
        for (int i = trainSize; i < n; i++) {
            double out = forward(X[i]);
            int pred = (out >= 0) ? 1 : -1;
            if (pred == (int) Math.signum(Y[i])) correct++;
        }
        double acc = (testSize > 0)
                ? (100.0 * correct / testSize)
                : 0.0;
        System.out.println("Test accuracy (approx): " + acc + "%");

        trained = true;
    }

    // forward only (no gradient)
    private double forward(double[] x) {
        double[] hidden = new double[HIDDEN_SIZE];
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            double z = b1[j];
            for (int k = 0; k < INPUT_SIZE; k++) {
                z += w1[j][k] * x[k];
            }
            hidden[j] = Math.tanh(z);
        }

        double z2 = b2;
        for (int j = 0; j < HIDDEN_SIZE; j++) {
            z2 += w2[j] * hidden[j];
        }
        return Math.tanh(z2); // [-1,1]
    }

    public boolean isTrained() {
        return trained;
    }

    /**
     * Evaluate board from HUMAN perspective using this ML model.
     * Internally the model is trained from X's perspective:
     *  +1 = good for X, -1 = good for O.
     * So if human is O we flip the sign.
     */
    public double evaluateBoard(Board board, Player humanPlayer) {
        double[] featsForX = extractFeaturesForX(board);

        // same normalization as in training
        featsForX[0] /= 5.0;
        featsForX[1] /= 5.0;
        featsForX[2] /= 5.0;
        featsForX[3] /= 5.0;
        // featsForX[4] is 0/1
        featsForX[5] /= 5.0;

        double scoreForX = forward(featsForX);

        if (humanPlayer == Player.X) {
            return scoreForX;
        } else if (humanPlayer == Player.O) {
            return -scoreForX;
        } else {
            return 0.0;
        }
    }

    // ===== feature extraction exactly like your CSV header =====
    private double[] extractFeaturesForX(Board board) {
        int f1_X_count = 0;
        int f2_O_count = 0;
        int f3_X_almost = 0;
        int f4_O_almost = 0;
        int f5_X_center = 0;
        int f6_X_corners = 0;

        // counts
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Player p = board.getCell(r, c);
                if (p == Player.X) f1_X_count++;
                else if (p == Player.O) f2_O_count++;
            }
        }

        // center
        if (board.getCell(1, 1) == Player.X) {
            f5_X_center = 1;
        }

        // corners
        int[][] corners = {{0,0},{0,2},{2,0},{2,2}};
        for (int[] co : corners) {
            if (board.getCell(co[0], co[1]) == Player.X) {
                f6_X_corners++;
            }
        }

        // almost-win lines
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

        for (int[] li : lines) {
            Player[] line = {
                    board.getCell(li[0], li[1]),
                    board.getCell(li[2], li[3]),
                    board.getCell(li[4], li[5])
            };

            int xCount = 0, oCount = 0;
            for (Player p : line) {
                if (p == Player.X) xCount++;
                else if (p == Player.O) oCount++;
            }

            if (xCount == 2 && oCount == 0) f3_X_almost++;
            if (oCount == 2 && xCount == 0) f4_O_almost++;
        }

        return new double[]{
                f1_X_count,
                f2_O_count,
                f3_X_almost,
                f4_O_almost,
                f5_X_center,
                f6_X_corners
        };
    }
}
