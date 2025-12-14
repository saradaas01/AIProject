package TicTacToe;

import java.util.Random;

public class MLP {

    private final int inputSize;
    private final int hiddenSize;
    private final double learningRate;

    private final double[] hidden;
    private final double[] output;

    private final double[][] w1;  // input → hidden
    private final double[] b1;

    private final double[] w2;    // hidden → output
    private double b2;

    public MLP(int inputSize, int hiddenSize, double learningRate) {
        this.inputSize = inputSize;
        this.hiddenSize = hiddenSize;
        this.learningRate = learningRate;

        hidden = new double[hiddenSize];
        output = new double[1];

        w1 = new double[inputSize][hiddenSize];
        b1 = new double[hiddenSize];

        w2 = new double[hiddenSize];
        b2 = 0.0;

        initializeWeights();
    }

    private void initializeWeights() {
        Random r = new Random();
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                w1[i][j] = r.nextDouble() * 0.2 - 0.1; // small random
            }
        }
        for (int j = 0; j < hiddenSize; j++) {
            w2[j] = r.nextDouble() * 0.2 - 0.1;
        }
    }

    private double sigmoid(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    private double sigmoidDer(double x) {
        return x * (1.0 - x);
    }

    /** forward pass: returns prediction as +1 or -1 **/
    private int forward(int[] input) {
        // hidden layer
        for (int j = 0; j < hiddenSize; j++) {
            double sum = b1[j];
            for (int i = 0; i < inputSize; i++) {
                sum += input[i] * w1[i][j];
            }
            hidden[j] = sigmoid(sum);
        }

        // output layer
        double sumOut = b2;
        for (int j = 0; j < hiddenSize; j++) {
            sumOut += hidden[j] * w2[j];
        }
        output[0] = sigmoid(sumOut);

        // map sigmoid output [0,1] → {-1, +1}
        return output[0] >= 0.5 ? 1 : -1;
    }

    /** One training step on a single sample **/
    public void train(int[] input, int target) {
        // forward
        int predClass = forward(input);
        double targetSig = (target == 1 ? 1.0 : 0.0);

        // output layer error
        double errorOut = targetSig - output[0];
        double dOut = errorOut * sigmoidDer(output[0]);

        // hidden layer deltas
        double[] dHidden = new double[hiddenSize];
        for (int j = 0; j < hiddenSize; j++) {
            dHidden[j] = dOut * w2[j] * sigmoidDer(hidden[j]);
        }

        // update hidden → output weights
        for (int j = 0; j < hiddenSize; j++) {
            w2[j] += learningRate * dOut * hidden[j];
        }
        b2 += learningRate * dOut;

        // update input → hidden weights
        for (int i = 0; i < inputSize; i++) {
            for (int j = 0; j < hiddenSize; j++) {
                w1[i][j] += learningRate * dHidden[j] * input[i];
            }
        }
        for (int j = 0; j < hiddenSize; j++) {
            b1[j] += learningRate * dHidden[j];
        }
    }

    public int predict(int[] input) {
        return forward(input);
    }
}
