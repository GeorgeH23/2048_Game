package com.company;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

public class GameBoard {

    // the number of tiles on the game board
    public static final int ROWS = 4;
    public static final int COLS = 4;

    // the number of tiles at the beginning of the game
    private final int startingTiles = 2;
    private Tile[][] board;

    private boolean dead;
    private boolean won;
    private BufferedImage gameBoard;
    private BufferedImage finalBoard;
    private int x;
    private int y;

    private static int SPACING = 10;  //spacing in between tiles on the board in pixels
    public static int BOARD_WIDTH = (COLS + 1) * SPACING + COLS * Tile.WIDTH; //board width in pixels
    public static int BOARD_HEIGHT = (ROWS + 1) * SPACING + ROWS * Tile.HEIGHT; //board height in pixels

    private long elapsedMS;
    private long startTime;
    private int saveCount = 0;

    private boolean hasStarted;

    private AudioHandler audio;
    private ScoreManager scores;
    private Leaderboards lBoard;

    public GameBoard(int x, int y){

        this.x = x;
        this.y = y;
        this.board = new Tile[ROWS][COLS];
        this.gameBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);
        this.finalBoard = new BufferedImage(BOARD_WIDTH, BOARD_HEIGHT, BufferedImage.TYPE_INT_RGB);

        createBoardImage();

        audio = AudioHandler.getInstance();
        audio.load("click.wav", "click");

        lBoard = Leaderboards.getInstance();
        lBoard.loadScores();
        scores = new ScoreManager(this);
        scores.loadGame();
        scores.setBestTime(lBoard.getFastestTime());
        scores.setCurrentTopScore(lBoard.getHighScore());
        if (scores.newGame()){
            start();
            scores.saveGame();
        } else {
            for (int i = 0; i < scores.getBoard().length; i++){
                if (scores.getBoard()[i] == 0){
                    continue;
                }
                spawn(i / ROWS, i % COLS, scores.getBoard()[i]);
            }

            dead = checkDead();
            won = checkWon();
        }

    }

    public void reset(){
        board = new Tile[ROWS][COLS];
        start();
        scores.saveGame();
        dead = false;
        won = false;
        hasStarted = false;
        startTime = System.nanoTime();
        elapsedMS = 0;
        saveCount = 0;
    }

    // create the background of the Game Board
    private void createBoardImage(){
        Graphics2D g = (Graphics2D) gameBoard.getGraphics();
        g.setColor(Color.darkGray);
        g.fillRect(0,0, BOARD_WIDTH, BOARD_HEIGHT);
        g.setColor(Color.lightGray);

        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                // this local variables are drawing to an image
                int x = SPACING + SPACING * col + Tile.WIDTH * col;
                int y = SPACING + SPACING * row + Tile.HEIGHT * row;
                g.fillRoundRect(x, y, Tile.WIDTH, Tile.HEIGHT, Tile.ARC_WIDTH, Tile.ARC_HEIGHT);
            }
        }
    }

    private void start(){
        for (int i = 0; i < startingTiles; i++){
            spawnRandom();
        }
    }

    private void spawn(int row, int col, int value){
        board[row][col] = new Tile(value, getTileX(col), getTileY(row));
    }

    private void spawnRandom(){
        Random random = new Random();
        boolean notValid = true;

        while (notValid){
            int location = random.nextInt(ROWS * COLS);
            int row = location / ROWS;
            int col = location % COLS;
            Tile current = board[row][col];

            if (current == null){
                // generate a random number from 0 to 9. If the number is < 9 the new tile will have value 2
                // else if the number == 9 the new tile will have value 4. Odds = (90% 2 <--> 10% 4).
                int value = random.nextInt(10) < 9 ? 2 : 4;
                Tile tile = new Tile(value, getTileX(col), getTileY(row));
                board[row][col] = tile;
                notValid = false;
            }
        }
    }

    public int getTileX(int col){
        return SPACING + col * Tile.WIDTH + col * SPACING;
    }

    public int getTileY(int row){
        return SPACING + row * Tile.HEIGHT + row * SPACING;
    }

    public void render(Graphics2D g){
        Graphics2D g2d = (Graphics2D) finalBoard.getGraphics();
        g2d.drawImage(gameBoard, 0, 0, null);

        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                Tile current = board[row][col];
                if (current == null){
                    continue;
                }
                current.render(g2d);
            }
        }
        // this global variables are drawing to the screen
        g.drawImage(finalBoard, x, y, null);
        g2d.dispose();
    }

    public void update() {
        saveCount++;
        if (saveCount >= 120){
            saveCount = 0;
            scores.saveGame();
        }

        if (!won && !dead){
            if (hasStarted){
                elapsedMS = (System.nanoTime() - startTime) / 1_000_000;
                scores.setTime(elapsedMS);
            } else {
                startTime = System.nanoTime();
            }
        }

        checkKeys();

        if (scores.getCurrentScore() > scores.getCurrentTopScore()){
            scores.setCurrentTopScore(scores.getCurrentScore());
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null){
                    continue;
                }
                current.update();

                //reset the position (slide it across the screen)
                resetPosition(current, row, col);

                if (current.getValue() == 2048){
                    setWon(true);
                }
            }
        }
    }

    private void resetPosition(Tile current, int row, int col){
        if (current == null){
            return;
        }
        int x = getTileX(col);
        int y = getTileY(row);

        int distX = current.getX() - x;
        int distY = current.getY() - y;

        // if it needs to move less than the SLIDE_SPEED do it so you don't get flickering
        if (Math.abs(distX) < Tile.SLIDE_SPEED){
            current.setX(current.getX() - distX);
        }

        if (Math.abs(distY) < Tile.SLIDE_SPEED){
            current.setY(current.getY() - distY);
        }

        // if you're moving to the left
        if (distX < 0){
            current.setX(current.getX() + Tile.SLIDE_SPEED);
        }

        if (distY < 0){
            current.setY(current.getY() + Tile.SLIDE_SPEED);
        }

        // if you're moving to the right
        if (distX > 0){
            current.setX(current.getX() - Tile.SLIDE_SPEED);
        }

        if (distY > 0){
            current.setY(current.getY() - Tile.SLIDE_SPEED);
        }

    }

    private boolean move(int row, int col, int horizontalDirection, int verticalDirection, Direction dir){
        boolean canMove = false;
        Tile current = board[row][col];

        if (current == null){
            return false;
        }

        boolean move = true;
        int newCol = col;
        int newRow = row;

        // while the tile can slide or combine
        while (move){
            newCol += horizontalDirection;
            newRow += verticalDirection;
            if (checkOutOfBounds(dir, newRow, newCol)) {
                break;
            }
            if (board[newRow][newCol] == null){
                // the new position we're moving to is going to be equal with the current tile
                board[newRow][newCol] = current;
                // the old space occupied by the moved tile will be set to null
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                canMove = true;
            }
            else if(board[newRow][newCol].getValue() == current.getValue() && board[newRow][newCol].canCombine()){
                board[newRow][newCol].setCanCombine(false);
                board[newRow][newCol].setValue(board[newRow][newCol].getValue() * 2);
                canMove = true;
                board[newRow - verticalDirection][newCol - horizontalDirection] = null;
                board[newRow][newCol].setSlideTo(new Point(newRow, newCol));
                board[newRow][newCol].setCombineAnimation(true);
                // add to score
                scores.setCurrentScore(scores.getCurrentScore() + board[newRow][newCol].getValue());
            } else {
                move = false;
            }
        }

        return canMove;
    }

    private boolean checkOutOfBounds(Direction dir, int row, int col) {
        if (dir == Direction.LEFT) {
            return col < 0;
        } else if (dir == Direction.RIGHT) {
            return col > COLS - 1;
        } else if (dir == Direction.UP) {
            return row < 0;
        } else if (dir == Direction.DOWN) {
            return row > ROWS - 1;
        }
        return false;
    }

    private void moveTiles(Direction dir) {
        boolean canMove = false;
        int horizontalDirection = 0;
        int verticalDirection = 0;

        if (dir == Direction.LEFT) {
            horizontalDirection = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        // if the tile can move or combine move() will return true;
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.RIGHT) {
            horizontalDirection = 1;
            for (int row = 0; row < ROWS; row++) {
                // we update the columns from right to left so we can have:
                //2 2 4 8 --> * 4 4 8
                // if we update the columns from left to right we'll end up with this:
                //2 2 4 8 --> * * * 16
                for (int col = COLS - 1; col >= 0; col--) {
                    if (!canMove) {
                        // if the tile can move or combine move() will return true;
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.UP) {
            verticalDirection = -1;
            for (int row = 0; row < ROWS; row++) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        // if the tile can move or combine move() will return true;
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else if (dir == Direction.DOWN) {
            verticalDirection = 1;
            // we update the columns from down to up so we can have:
            //2 * * * --> * * * *
            //2 * * * --> 4 * * *
            //4 * * * --> 4 * * *
            //8 * * * --> 8 * * *
            // if we update the columns from up to down we'll end up with this:
            //2 * * * --> * * * *
            //2 * * * --> * * * *
            //4 * * * --> * * * *
            //8 * * * -->16 * * *
            for (int row = ROWS - 1; row >= 0; row--) {
                for (int col = 0; col < COLS; col++) {
                    if (!canMove) {
                        // if the tile can move or combine move() will return true;
                        canMove = move(row, col, horizontalDirection, verticalDirection, dir);
                    } else {
                        move(row, col, horizontalDirection, verticalDirection, dir);
                    }
                }
            }
        } else {
            System.out.println(dir + " is not a valid direction.");
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Tile current = board[row][col];
                if (current == null) {
                    continue;
                }
                current.setCanCombine(true);
            }
        }

        if (canMove) {
            audio.play("click", 0);
            audio.adjustVolume("click", -35);
            spawnRandom();
            // check dead
            setDead(checkDead());
        }
    }

    private boolean checkDead(){
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                if (board[row][col] == null){
                    return false;
                }
                boolean canCombine = checkSurroundingTiles(row, col, board[row][col]);
                if (canCombine){
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWon(){
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                if (board[row][col] == null){
                    continue;
                }
                if (board[row][col].getValue() >= 2048){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkSurroundingTiles(int row, int col, Tile current){
        if (row > 0){
            Tile check = board[row - 1][col];
            if (check == null){
                return true;
            }
            if (current.getValue() == check.getValue()){
                return true;
            }
        }
        if (row < ROWS - 1){
            Tile check = board[row + 1][col];
            if (check == null){
                return true;
            }
            if (current.getValue() == check.getValue()){
                return true;
            }
        }
        if (col > 0){
            Tile check = board[row][col - 1];
            if (check == null){
                return true;
            }
            if (current.getValue() == check.getValue()){
                return true;
            }
        }
        if (col < COLS - 1){
            Tile check = board[row][col + 1];
            if (check == null){
                return true;
            }
            if (current.getValue() == check.getValue()){
                return true;
            }
        }
        return false;
    }

    private void checkKeys(){
        if (Keyboard.typed(KeyEvent.VK_LEFT)){
            //move tiles left
            moveTiles(Direction.LEFT);
            if (!hasStarted){
                hasStarted = !dead;
            }
        }

        if (Keyboard.typed(KeyEvent.VK_RIGHT)){
            //move tiles right
            moveTiles(Direction.RIGHT);
            if (!hasStarted){
                hasStarted = !dead;
            }
        }

        if (Keyboard.typed(KeyEvent.VK_UP)){
            //move tiles up
            moveTiles(Direction.UP);
            if (!hasStarted){
                hasStarted = !dead;
            }
        }

        if (Keyboard.typed(KeyEvent.VK_DOWN)){
            //move tiles down
            moveTiles(Direction.DOWN);
            if (!hasStarted){
                hasStarted = !dead;
            }
        }
    }

    public int getHighestTileValue(){
        int value = 2;
        for (int row = 0; row < ROWS; row++){
            for (int col = 0; col < COLS; col++){
                if (board[row][col] == null){
                    continue;
                }
                if (board[row][col].getValue() > value){
                    value = board[row][col].getValue();
                }
            }
        }
        return value;
    }

    public boolean isDead(){
        return dead;
    }

    public void setDead(boolean dead){
        if (!this.dead && dead){
            lBoard.addTile(getHighestTileValue());
            lBoard.addScore(scores.getCurrentScore());
            lBoard.saveScores();
        }
        this.dead = dead;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won){
        if (!this.won && won){
            lBoard.addTime(scores.getTime());
            lBoard.saveScores();
        }
        this.won = won;
    }

    public ScoreManager getScores(){
        return scores;
    }

    public Tile[][] getBoard(){
        return board;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
}
