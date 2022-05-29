package Game2048.GUI;

import Game2048.AI.ExpectiMax;
import Game2048.Game.Board2048;
import Game2048.Game.Board2048.Directions;
import javafx.application.*;
import javafx.concurrent.Task;
import javafx.scene.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.event.*;
import javafx.scene.input.*;
import javafx.scene.text.*;
import javafx.geometry.*;

public class Gui2048 extends Application {
    private String outputBoard;
    private Board2048 board;

    private static final int TILE_WIDTH = 106;

    private static final int TEXT_SIZE_LOW = 55;
    private static final int TEXT_SIZE_MID = 45;
    private static final int TEXT_SIZE_HIGH = 40;
    private static final int TEXT_SIZE_EXTREME = 30;


    private static final Color COLOR_EMPTY = Color.rgb(238, 228, 218, 0.35);
    private static final Color COLOR_2 = Color.rgb(238, 228, 218);
    private static final Color COLOR_4 = Color.rgb(237, 224, 200);
    private static final Color COLOR_8 = Color.rgb(242, 177, 121);
    private static final Color COLOR_16 = Color.rgb(245, 149, 99);
    private static final Color COLOR_32 = Color.rgb(246, 124, 95);
    private static final Color COLOR_64 = Color.rgb(246, 94, 59);
    private static final Color COLOR_128 = Color.rgb(237, 207, 114);
    private static final Color COLOR_256 = Color.rgb(237, 204, 97);
    private static final Color COLOR_512 = Color.rgb(237, 200, 80);
    private static final Color COLOR_1024 = Color.rgb(237, 197, 63);
    private static final Color COLOR_2048 = Color.rgb(237, 194, 46);
    private static final Color COLOR_OTHER = Color.BLACK;

    private static final Color[] COLORS = new Color[] {
            COLOR_EMPTY, COLOR_2, COLOR_4, COLOR_8, COLOR_16, COLOR_32, COLOR_64, COLOR_128, COLOR_256, COLOR_512,
            COLOR_1024, COLOR_2048, COLOR_OTHER

    };
    private static final Color COLOR_GAME_OVER = Color.rgb(238, 228, 218, 0.73);


    private static final Color COLOR_VALUE_LIGHT = Color.rgb(249, 246, 242);


    private static final Color COLOR_VALUE_DARK = Color.rgb(119, 110, 101);



    private StackPane mainPane = new StackPane();
    private GridPane pane;
    final Text gameName = new Text("2048");
    Text scoreText = new Text("Очки: 0");
    Rectangle[][] tileGrid;
    Text[][] tileText;
    int scoreValue = 0;
    GridPane gameOverPane;
    Text gameOverText;
    Scene mainScene;
    final int defaultSize = 4;
    Text mySignature = new Text("Виталий Ледовской 535");
    boolean isAIEnabled = false;
    AiTask aiTask;
    Text depthText;
    int depth = 7;

    //java fx
    @Override
    public void start(Stage primaryStage) {

        processArgs(getParameters().getRaw().toArray(new String[0]));


        pane = new GridPane();
        pane.setAlignment(Pos.CENTER);
        pane.setPadding(new Insets(11.5, 12.5, 13.5, 14.5));
        pane.setStyle("-fx-background-color: rgb(187, 173, 160)");
        pane.setHgap(15);
        pane.setVgap(15);
        pane.setOnKeyPressed(normalKeyHandler);

        initGrid();
        mainPane.getChildren().add(pane);
        mainScene = new Scene(mainPane);
        primaryStage.setTitle("курсовая");
        primaryStage.setScene(mainScene);
        primaryStage.show();
        pane.requestFocus();
        pane.setDisable(false);
        printDirections();
    }


    private void initGrid() {
        gameName.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 40));
        scoreText.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 25));
        pane.add(gameName, 0, 0, board.getBoardSize() / 2, 1);//add game name text
        pane.add(scoreText, (board.getBoardSize() + 1) / 2, 0, board.getBoardSize() / 2, 1);//add score text
        pane.setHalignment(gameName, HPos.CENTER);
        pane.setHalignment(scoreText, HPos.CENTER);

        tileGrid = new Rectangle[board.getBoardSize()][board.getBoardSize()];
        tileText = new Text[board.getBoardSize()][board.getBoardSize()];
        scoreValue = (int) board.getScore();

        int[][] numberGrid = board.getBoard();

        scoreText.setText("Score: " + scoreValue);

        int tileSize = TILE_WIDTH * defaultSize / board.getBoardSize();

        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {
                tileGrid[row][column] =
                        new Rectangle(tileSize, tileSize);
                pane.add(tileGrid[row][column], column, row + 1);
            }
        }

        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {
                tileText[row][column] = new Text("");

                pane.setHalignment(tileText[row][column], HPos.CENTER);
                pane.add(tileText[row][column], column, row + 1);
            }
        }

        mySignature.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 15));
        pane.setHalignment(mySignature, HPos.RIGHT);
        pane.add(mySignature, board.getBoardSize() - 2, board.getBoardSize() + 1,
                2, 1);


        depthText = new Text("Глубина корней " + depth);
        depthText.setFont(Font.font("Comic Sans MS", FontPosture.ITALIC, 15));
        pane.add(depthText, 0, board.getBoardSize() + 1,
                2, 1);
        update();
    }


    private void update() {
        scoreValue = (int) board.getScore();


        int[][] numberGrid = board.getBoard();


        scoreText.setText("Score: " + scoreValue);

        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {//determine color of grid based value
                if (numberGrid[row][column] >= 13)
                    tileGrid[row][column].setFill(COLOR_OTHER);
                else
                    tileGrid[row][column].setFill(COLORS[numberGrid[row][column]]);

            }
        }


        for (int row = 0; row < board.getBoardSize(); row++) {
            for (int column = 0; column < board.getBoardSize(); column++) {
                if (numberGrid[row][column] == 0)
                    tileText[row][column].setText("");

                else {
                    tileText[row][column].setText(
                            Integer.toString((int) Math.pow(2, numberGrid[row][column])));
                    if (numberGrid[row][column] < 7) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_LOW));
                    }

                    if (numberGrid[row][column] >= 7 &&
                            numberGrid[row][column] < 10) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_MID));
                    }

                    if (numberGrid[row][column] >= 10 &&
                            numberGrid[row][column] < 14) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_HIGH));
                    }

                    if (numberGrid[row][column] >= 14) {
                        tileText[row][column].setFont(
                                Font.font("Times New Roman",
                                        FontWeight.BOLD, TEXT_SIZE_EXTREME));
                    }

                    if (numberGrid[row][column] < 3)
                        tileText[row][column].setFill(COLOR_VALUE_DARK);
                    else
                        tileText[row][column].setFill(COLOR_VALUE_LIGHT);
                }

            }
        }
        if (!board.checkIfCanGo()) {
            disableAI();
            if (gameOverPane == null) {
                gameOverPane = new GridPane();
                gameOverPane.setStyle(
                        "-fx-background-color: rgb(238, 228, 218,0.73)");
                gameOverText = new Text("Не ставьте 2");
                gameOverText.setFont(Font.font("Comic Sans MS", 40));
                gameOverText.setFill(Color.BLACK);
                gameOverPane.add(gameOverText, 0, 0);
                gameOverPane.setAlignment(Pos.CENTER);
                gameOverPane.setHalignment(gameOverText, HPos.CENTER);
                mainPane.getChildren().add(gameOverPane);
            }
        }
        if (isAIEnabled)
            runAI();
    }


    private void restart() {
        board = new Board2048();
        pane.requestFocus();
        pane.setDisable(false);
        update();
        if (gameOverPane != null) {
            mainPane.getChildren().remove(gameOverPane);
            gameOverPane = null;
        }
    }


    private void switchDepthOfSearch() {
        if (depth == 7)
            depth = 9;
        else if (depth == 9)
            depth = 5;
        else if (depth == 5)
            depth = 7;
        depthText.setText("Depth of Search: " + depth);
    }


    private void enableAI() {
        isAIEnabled = true;
        runAI();
        pane.setOnKeyPressed(null);
        pane.setOnKeyPressed(limitedKeyHandler);
    }


    private void disableAI() {
        stopAI();
        isAIEnabled = false;
        pane.setOnKeyPressed(null);
        pane.setOnKeyPressed(normalKeyHandler);
    }


    private void runAI() {

        if (board.checkIfCanGo()) {
            aiTask = new AiTask(board, depth);
            aiTask.setOnSucceeded((succeededEvent) -> {
                if (aiTask.getValue() != null) {
                    board.move(aiTask.getValue().getRotateValue());
                    update();
                }
            });
            Thread t = new Thread(aiTask);
            t.setDaemon(true);
            t.start();
        }
    }


    private void stopAI() {
        aiTask.cancel();
    }


    EventHandler<KeyEvent> normalKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case UP:
                    board.move(Directions.UP.getRotateValue());
                    update();
                    break;

                case DOWN:
                    board.move(Directions.DOWN.getRotateValue());
                    update();
                    break;

                case LEFT:
                    board.move(Directions.LEFT.getRotateValue());
                    update();
                    break;

                case RIGHT:
                    board.move(Directions.RIGHT.getRotateValue());
                    update();
                    break;

                case R:
                    restart();
                    break;

                case C:
                    switchDepthOfSearch();
                    break;

                case ENTER:

                    if (!board.checkIfCanGo()) {
                        restart();
                    }
                    enableAI();
                    break;

                default:
                    break;
            }
        }
    };


    EventHandler<KeyEvent> limitedKeyHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent keyEvent) {
            switch (keyEvent.getCode()) {
                case C:
                    switchDepthOfSearch();
                    break;

                case ENTER:
                    disableAI();
                    break;

                default:
                    break;
            }
        }
    };


    public class AiTask extends Task<Directions> {

        Board2048 m_gameBoard;
        int m_depth = 7;


        public AiTask(Board2048 originalBoard, int depth) {
            this.m_gameBoard = originalBoard;
            m_depth = depth;
        }

        @Override
        protected Directions call() throws Exception {
            ExpectiMax expectiMax = new ExpectiMax(m_gameBoard, m_gameBoard.getScore(), m_depth);
            Board2048.Directions bestDirection = expectiMax.computeDecision();
            return bestDirection;
        }
    }


    public void printDirections() {
        System.out.println("Ипользуйте стрелочки для передвижения, c для смены глубины дерева");
        System.out.println("c для смены глубины дерева");
        System.out.println("ENTER для включения AI");
    }



    private void processArgs(String[] args) {
        String inputBoard = null;
        int boardSize = 0;


        if ((args.length % 2) != 0) {

            System.exit(-1);
        }


        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("-i")) {

                inputBoard = args[i + 1];
            } else if (args[i].equals("-o")) {

                outputBoard = args[i + 1];
            } else if (args[i].equals("-s")) {

                boardSize = Integer.parseInt(args[i + 1]);
            } else {
                System.exit(-1);
            }
        }


        if (outputBoard == null)
            outputBoard = "2048.board";

        if (boardSize < 2)
            boardSize = 4;


        try {
            if (inputBoard != null)
                board = new Board2048();
            else
                board = new Board2048();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() +
                    " was thrown while creating a " +
                    "Board from file " + inputBoard);
            System.out.println("Either your Board(String, Random) " +
                    "Constructor is broken or the file isn't " +
                    "formated correctly");
            System.exit(-1);
        }
    }



}
