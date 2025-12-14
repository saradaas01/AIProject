package TicTacToe;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class GameController {

    @FXML private GridPane boardGrid;

    @FXML private RadioButton xRadio, oRadio;
    @FXML private ComboBox<String> difficultyCombo, evalCombo;
    @FXML private Button startButton, resetButton;

    @FXML private Label statusLabel, statusMiniLabel, aiInfoLabel, scoreLabel;

    private ToggleGroup playerToggleGroup;

    private Board board;
    private Player humanPlayer;
    private Player aiPlayer;
    private Player currentPlayer;

    private Button[][] cellButtons = new Button[3][3];

    private int humanWins = 0;
    private int aiWins = 0;
    private int draws = 0;

    @FXML
    public void initialize() {

        // Toggle group for X/O
        playerToggleGroup = new ToggleGroup();
        xRadio.setToggleGroup(playerToggleGroup);
        oRadio.setToggleGroup(playerToggleGroup);
        xRadio.setSelected(true);

        // Difficulty
        difficultyCombo.getItems().addAll("Easy", "Medium", "Hard");
        difficultyCombo.getSelectionModel().select("Medium");

        // Evaluation choice
        evalCombo.getItems().addAll("Classical heuristic", "ML evaluation");
        evalCombo.getSelectionModel().select(0);


        buildBoardGrid();
        resetGameState();
        updateScoreLabel();

    }

    private void buildBoardGrid() {
        boardGrid.getChildren().clear();
        boardGrid.getColumnConstraints().clear();
        boardGrid.getRowConstraints().clear();

        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(33.33);
            boardGrid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(33.33);
            boardGrid.getRowConstraints().add(row);
        }

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {

                Button btn = new Button();
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                btn.getStyleClass().add("cell-button");

                // keep board big
                btn.prefWidthProperty().bind(boardGrid.widthProperty().divide(3).subtract(30));
                btn.prefHeightProperty().bind(boardGrid.heightProperty().divide(3).subtract(30));

                final int row = r;
                final int col = c;

                btn.setOnAction(e -> handleHumanMove(row, col));

                cellButtons[r][c] = btn;
                boardGrid.add(btn, c, r);
            }
        }
    }

    @FXML
    private void onStartGame() {
        humanPlayer = xRadio.isSelected() ? Player.X : Player.O;
        aiPlayer = humanPlayer.opposite();

        resetGameState();

        statusLabel.setText("Game started. You are " + humanPlayer + ".");
        statusMiniLabel.setText("Difficulty: " + difficultyCombo.getValue()
                + " | Eval: " + evalCombo.getValue());
        aiInfoLabel.setText("-");

        // Human is MAX, but X always moves first.
        // If human chose O, AI (as X) plays first.
        currentPlayer = Player.X;
        if (currentPlayer == aiPlayer) {
            aiMove();
        }
    }

    @FXML
    private void onResetClicked() {
        resetGameState();
        statusLabel.setText("Welcome to Tic Tac Toe AI.");
        statusMiniLabel.setText("Ready.");
        aiInfoLabel.setText("-");
        // If you want reset scores too, uncomment:
        // humanWins = 0; aiWins = 0; draws = 0; updateScoreLabel();
    }

    private void resetGameState() {
        board = new Board();
        currentPlayer = Player.X;

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                cellButtons[r][c].setDisable(false);
                cellButtons[r][c].setText("");
            }
        }
    }

    private void handleHumanMove(int row, int col) {
        if (board == null) {
            statusMiniLabel.setText("Click Start Game first.");
            return;
        }
        if (currentPlayer != humanPlayer) {
            return; // not your turn
        }
        if (!board.isEmptyCell(row, col)) {
            return; // already used
        }

        playMove(row, col, humanPlayer);

        if (checkGameOver()) return;

        currentPlayer = aiPlayer;
        aiMove();
    }

    private void aiMove() {
        if (board.isTerminal()) return;
        if (currentPlayer != aiPlayer) return;

        // Show all possible AI evaluations
        List<String> evals = AlphaBeta.getAllMoveEvaluations(
                board,
                humanPlayer,
                aiPlayer,
                getDepthFromDifficulty()
        );

        StringBuilder sb = new StringBuilder("AI Evaluations:\n");
        for (String s : evals) sb.append(s).append("\n");
        aiInfoLabel.setText(sb.toString());

        // Choose best move (AI = MIN)
        Move bestMove = AlphaBeta.findBestMoveForAI(
                board,
                humanPlayer,
                aiPlayer,
                getDepthFromDifficulty()
        );

        if (bestMove == null) return;

        playMove(bestMove.getRow(), bestMove.getCol(), aiPlayer);

        if (checkGameOver()) return;

        currentPlayer = humanPlayer;
    }


    private void playMove(int row, int col, Player player) {
        board.setCell(row, col, player);
        cellButtons[row][col].setText(player.toString());
    }

    private boolean checkGameOver() {
        Player winner = board.getWinner();

        if (winner != Player.EMPTY) {
            if (winner == humanPlayer) {
                humanWins++;
                statusLabel.setText("🎉 You win!");
            } else {
                aiWins++;
                statusLabel.setText("AI wins.");
            }
            updateScoreLabel();
            disableBoard();
            return true;
        }

        if (board.isFull()) {
            draws++;
            statusLabel.setText("Draw.");
            updateScoreLabel();
            disableBoard();
            return true;
        }

        return false;
    }

    private void disableBoard() {
        for (Button[] row : cellButtons) {
            for (Button btn : row) {
                btn.setDisable(true);
            }
        }
    }

    private void updateScoreLabel() {
        scoreLabel.setText(
                "You " + humanWins + " : " + aiWins + " AI   |   Draws " + draws
        );
    }

    /**
     * Use difficulty + evaluation choice to pick the AI move.
     * HUMAN is MAX, AI is MIN.
     */
    private Move computeBestMoveUsingAI() {
        int depth = getDepthFromDifficulty();
        String eval = evalCombo.getValue();

        Move move;
        if ("Classical heuristic".equals(eval)) {
            move = AlphaBeta.findBestMoveForAI(board, humanPlayer, aiPlayer, depth);
        } else {
            // later we change this to use ML evaluation
            move = AlphaBeta.findBestMoveForAI(board, humanPlayer, aiPlayer, depth);
            statusMiniLabel.setText("Using classical evaluation (ML not implemented yet).");
        }

        return move;
    }

    private int getDepthFromDifficulty() {
        String diff = difficultyCombo.getValue();
        if (diff == null) return 3;

        switch (diff) {
            case "Easy":
                return 2;
            case "Hard":
                return 6;
            default:
                return 4; // Medium
        }
    }
}
