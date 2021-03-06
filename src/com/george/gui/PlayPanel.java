package com.george.gui;

import com.company.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PlayPanel extends GuiPanel {

    private GameBoard board;
    private BufferedImage info;
    private ScoreManager scores;
    private Font scoreFont;
    private Font actualScore;
    private long stop = 0;
    private long vol = 0;
    private boolean stopFlag = false;
    private boolean volFlag = false;

    //Game Over
    private GuiButton tryAgain;
    private GuiButton mainMenu;
    private GuiButton screenShot;
    private int smallButtonWidth = 160;
    private int spacing = 20;
    private int largeButtonWidth = smallButtonWidth * 2 + spacing;
    private int buttonHeight = 50;
    private boolean added;
    private int alpha;
    private Font gameOverFont;
    private boolean screenshot;
    private GuiButton backButton;
    private AudioHandler audio;

    public PlayPanel() {
        scoreFont = Game.main.deriveFont(18f);
        actualScore = new Font("Impact", Font.PLAIN, 28);
        gameOverFont = Game.main.deriveFont(70f);
        board = new GameBoard(Game.WIDTH / 2 - GameBoard.BOARD_WIDTH / 2, Game.HEIGHT - GameBoard.BOARD_HEIGHT - 20);
        scores = board.getScores();
        info = new BufferedImage(Game.WIDTH, 200, BufferedImage.TYPE_INT_RGB);

        backButton = new GuiButton(20, 126,60, 26 );
        mainMenu = new GuiButton(Game.WIDTH / 2 - largeButtonWidth / 2, 450, largeButtonWidth, buttonHeight);
        tryAgain = new GuiButton(mainMenu.getX(), mainMenu.getY() - spacing - buttonHeight, smallButtonWidth, buttonHeight);
        screenShot = new GuiButton(tryAgain.getX() + tryAgain.getWidth() + spacing, tryAgain.getY(), smallButtonWidth,buttonHeight);

        backButton.setText("Menu");
        tryAgain.setText("Try Again");
        screenShot.setText("Screenshot");
        mainMenu.setText("Back To Main Menu");

        audio = AudioHandler.getInstance();

        tryAgain.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.getScores().reset();
                board.reset();
                alpha = 0;

                remove(tryAgain);
                remove(screenShot);
                remove(mainMenu);
                add(backButton);

                added = false;
            }
        });

        screenShot.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                screenshot = true;
            }
        });

        mainMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Menu");
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!MainMenuPanel.wasPressed) {
                    MainMenuPanel.wasPressed = true;
                    board.getScores().saveGame();
                    board.setHasStarted(false);
                    GuiScreen.getInstance().setCurrentPanel("Menu");
                    board.getScores().loadGame();
                }
            }
        });
    }

    private void drawGui(Graphics2D g){
        long elapsedMS = System.nanoTime() / 1000000;
        // Format the times
        String timeF = DrawUtils.formatTime(scores.getTime());
        String bestTimeF = DrawUtils.formatTime(scores.getBestTime());

        // Draw it
        Graphics2D g2d = (Graphics2D) info.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, info.getWidth(), info.getHeight());
        g2d.setColor(Color.darkGray);
        g2d.setFont(actualScore);
        g2d.drawString("" + scores.getCurrentScore(), 30, 40);
        g2d.setColor(new Color(0xff9900));
        g2d.setFont(scoreFont);
        g2d.drawString("Best: " + scores.getCurrentTopScore(),
                Game.WIDTH - DrawUtils.getMessageWidth("Best: " + scores.getCurrentTopScore(), scoreFont, g2d) - 30, 40);
        if (!bestTimeF.equals("596:31:23:647")){
            g2d.drawString("Fastest: " + bestTimeF,
                    Game.WIDTH - DrawUtils.getMessageWidth("Fastest: " + bestTimeF, scoreFont, g2d) - 20, 90);
        } else {
            g2d.drawString("Fastest: N/A",
                    Game.WIDTH - DrawUtils.getMessageWidth("Fastest: N/A", scoreFont, g2d) - 20, 90);
        }
        g2d.setColor(Color.black);
        g2d.drawString("Time: " + timeF, 30, 90);

        if (Keyboard.pressed[KeyEvent.VK_P] && Keyboard.prev[KeyEvent.VK_P]) {
            stop = elapsedMS;
            stopFlag = true;
        }
        if ((elapsedMS <= (stop + 1500)) && stopFlag) {
            g2d.setFont(scoreFont.deriveFont(Font.BOLD));
            g2d.setFont(scoreFont.deriveFont(16f));
            g2d.setColor(Color.darkGray);
            g2d.drawString("Pausing music...", 30, 115);
        }

        if (Keyboard.pressed[KeyEvent.VK_R] && Keyboard.prev[KeyEvent.VK_R]) {
            stop = elapsedMS;
            stopFlag = false;
        }
        if ((elapsedMS <= (stop + 1500)) && !stopFlag) {
            g2d.setFont(scoreFont.deriveFont(Font.BOLD));
            g2d.setFont(scoreFont.deriveFont(16f));
            g2d.setColor(new Color(0xff9900));
            g2d.drawString("Resuming music...", 30, 115);
        }
        if (Keyboard.pressed[KeyEvent.VK_S] && Keyboard.prev[KeyEvent.VK_S]){
            vol = elapsedMS;
            volFlag = true;
        }
        if (elapsedMS <= vol + 150 && volFlag){
            g2d.setFont(scoreFont.deriveFont(Font.BOLD));
            g2d.setFont(scoreFont.deriveFont(16f));
            g2d.setColor(Color.darkGray);
            g2d.drawString("VOL--", 90, 144);
        }
        if (Keyboard.pressed[KeyEvent.VK_W] && Keyboard.prev[KeyEvent.VK_W]){
            vol = elapsedMS;
            volFlag = false;
        }
        if (elapsedMS <= vol + 150 && !volFlag){
            g2d.setFont(scoreFont.deriveFont(Font.BOLD));
            g2d.setFont(scoreFont.deriveFont(16f));
            g2d.setColor(new Color(0xff9900));
            g2d.drawString("VOL++", 90, 144);
        }

        g2d.dispose();
        g.drawImage(info, 0, 0, null);

    }

    public void drawGameOver(Graphics2D g) {
        remove(backButton);

        g.setColor(new Color(222,222,222, alpha));
        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT);
        g.setColor(Color.red);
        g.setFont(gameOverFont);
        g.drawString("GAME OVER!", Game.WIDTH / 2 - DrawUtils.getMessageWidth("GAME OVER!", gameOverFont, g) / 2, 250);
    }

    @Override
    public void update(){
        board.update();
        if (board.isDead()){
            alpha++;
            if (alpha > 170){
                alpha = 170;
            }
        }
    }

    @Override
    public void render(Graphics2D g) {
        drawGui(g);
        board.render(g);
        if (screenshot) {
            BufferedImage bI = new BufferedImage(Game.WIDTH, Game.HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) bI.getGraphics();
            g2d.setColor(Color.white);
            g2d.fillRect(0,0, Game.WIDTH, Game.HEIGHT);
            drawGui(g2d);
            board.render(g2d);

            // Saving SCREENSHOT
            try {
                // Format the present date
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
                String date = dateTimeFormatter.format(LocalDate.now()) + "_";
                // Format the present time of day
                dateTimeFormatter = DateTimeFormatter.ofPattern("HH.mm.ss");
                String time = dateTimeFormatter.format(LocalTime.now()) + " ";
                // Write the screenshot to disk
                ImageIO.write(bI, "gif", new File(System.getProperty("user.home") + "\\Desktop", "Screenshot_" + date + time + ".gif"));
            } catch (Exception e){
                e.printStackTrace();
            }
            screenshot = false;
        }
        if (board.isDead()){
            if (!added){
                added = true;
                remove(backButton);
                add(mainMenu);
                add(screenShot);
                add(tryAgain);
            }
            drawGameOver(g);
        }
        super.render(g);
    }
}
