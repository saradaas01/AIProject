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

        playerToggleGroup = new ToggleGroup();
        xRadio.setToggleGroup(playerToggleGroup);
        oRadio.setToggleGroup(playerToggleGroup);
        xRadio.setSelected(true);

        difficultyCombo.getItems().addAll("Easy", "Medium", "Hard");
        difficultyCombo.getSelectionModel().select("Medium");

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

        resetGameState();  // clears board only

        // If human is X -> human starts
        // If human is O -> AI starts
        currentPlayer = Player.X;

        if (currentPlayer == aiPlayer) {
            aiMove(); // AI makes the first move
        }

        statusLabel.setText("Game started. You are " + humanPlayer + ".");
        statusMiniLabel.setText("Difficulty: " + difficultyCombo.getValue() +
                " | Eval: " + evalCombo.getValue());
    }


    @FXML
    private void onResetClicked() {
        // Clear board
        resetGameState();

        // Reset UI labels
        statusLabel.setText("Welcome to Tic Tac Toe AI.");
        statusMiniLabel.setText("Ready.");
        aiInfoLabel.setText("-");

        // Reset settings to default
        xRadio.setSelected(true);
        difficultyCombo.getSelectionModel().select("Medium");
        evalCombo.getSelectionModel().select(0);
         humanWins = 0;
         aiWins = 0;
         draws = 0;
         updateScoreLabel();
    }


    private void resetGameState() {
        board = new Board();
        currentPlayer = Player.X;

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 3; c++) {
                cellButtons[r][c].setDisable(false);
                cellButtons[r][c].setText("");
            }
    }

    private void handleHumanMove(int row, int col) {
        if (currentPlayer != humanPlayer) return;
        if (!board.isEmptyCell(row, col)) return;

        playMove(row, col, humanPlayer);

        if (checkGameOver()) return;

        currentPlayer = aiPlayer;
        aiMove();
    }

    private void aiMove() {
        if (board.isTerminal()) return;

        Move bestMove = computeBestMovePlaceholder();

        playMove(bestMove.getRow(), bestMove.getCol(), aiPlayer);
        aiInfoLabel.setText("AI played (" + (bestMove.getRow()+1) + "," + (bestMove.getCol()+1) + ")");

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
        for (Button[] row : cellButtons)
            for (Button btn : row)
                btn.setDisable(true);
    }

    private void updateScoreLabel() {
        scoreLabel.setText("You " + humanWins + " : " + aiWins + " AI   |   Draws " + draws);
    }

    /** Placeholder AI – will be replaced with alpha-beta */
    private Move computeBestMovePlaceholder() {
        return board.getLegalMoves().get(0);
    }
}
