package com.company;

import com.george.gui.*;

import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class Game extends JPanel implements KeyListener, MouseListener, MouseMotionListener, Runnable {

    private static final long serialVersionUID = 1L;
    public static final int WIDTH = GameBoard.BOARD_WIDTH + 40;
    public static final int HEIGHT = 630;
    public static final Font main = new Font("Monospaced", Font.BOLD, 28);
    private Thread game;
    private boolean running;
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

    //private GameBoard board;
    private GuiScreen screen;

    private AudioHandler audio;
    private int volume = -10;

    public Game(){
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        screen = GuiScreen.getInstance();
        screen.add("Menu", new MainMenuPanel());
        screen.add("Play", new PlayPanel());
        screen.add("Leaderboards", new LeaderboardsPanel());
        screen.add("Controls", new ControlsPanel());
        screen.setCurrentPanel("Menu");

        audio = AudioHandler.getInstance();
        audio.load("MainSong.mp3", "BG");
        audio.load("Shinedown.mp3", "SD");
        audio.adjustVolume("BG", -10);
        audio.play("BG", Clip.LOOP_CONTINUOUSLY);
    }

    private void update(){

        screen.update();
        setFramePosition();
        changeSong();
        volumeUpdate();
        Keyboard.update();

    }

    private void setFramePosition(){
        if (audio.isRunning("BG")){
            audio.setFramePosition("SD", 0);
        }
        if (audio.isRunning("SD")){
            audio.setFramePosition("BG", 0);
        }
    }

    private void changeSong(){
        if (!Keyboard.pressed[KeyEvent.VK_N] && Keyboard.prev[KeyEvent.VK_N]) {

            System.out.println("Next Song>>>");
            if (audio.isRunning("BG") || audio.isRunning("SD")) {
                if (audio.isRunning("BG")) {
                    audio.stop("BG");
                    audio.adjustVolume("SD", volume);
                    audio.play("SD", Clip.LOOP_CONTINUOUSLY);
                } else {
                    audio.stop("SD");
                    audio.adjustVolume("BG", volume);
                    audio.play("BG", Clip.LOOP_CONTINUOUSLY);
                }
            }
        }
    }

    private void volumeUpdate(){
        String name = "";

        if (audio.isRunning("BG") || audio.getFramePosition("BG") != 0){
            name = "BG";
        }
        if (audio.isRunning("SD") || audio.getFramePosition("SD") != 0){
            name = "SD";
        }

        if (!Keyboard.pressed[KeyEvent.VK_S] && Keyboard.prev[KeyEvent.VK_S]) {

            System.out.println("Volume --");
            if (volume >= -50){
                volume -= 5;
            } else {
                volume = -50;
            }
            audio.adjustVolume(name, volume);
        }

        if (!Keyboard.pressed[KeyEvent.VK_W] && Keyboard.prev[KeyEvent.VK_W]) {

            System.out.println("Volume ++");
            if (volume <= 0){
                volume += 5;
            } else {
                volume = 0;
            }
            audio.adjustVolume(name, volume);
        }

        if (!Keyboard.pressed[KeyEvent.VK_P] && Keyboard.prev[KeyEvent.VK_P]) {

            System.out.println("Paused");
            audio.pause(name);
        }

        if (!Keyboard.pressed[KeyEvent.VK_R] && Keyboard.prev[KeyEvent.VK_R]) {

            System.out.println("Resuming...");
            audio.resume(name, Clip.LOOP_CONTINUOUSLY);
        }
    }

    private void render(){
        Graphics2D g = (Graphics2D) image.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0,0,WIDTH,HEIGHT);
        // render board
        screen.render(g);
        g.dispose();

        Graphics2D g2d = (Graphics2D) getGraphics();
        g2d.drawImage(image,0,0,null);
        g2d.dispose();
    }

    @Override
    public void run() {
        int fps = 0;
        int updates = 0;
        long fpsTimer = System.currentTimeMillis();
        double nsPerUpdate = 1000000000.0 / 60;     // track number of nano seconds between updates

        // last update time in nanoseconds
        double then = System.nanoTime();
        double unprocessed = 0;

        while (running) {

            boolean shouldRender = false;
            double now = System.nanoTime();
            unprocessed += (now - then) / nsPerUpdate;
            then = now;

            // update queue
            while (unprocessed >= 1) {
                updates++;
                update();
                unprocessed--;
                shouldRender = true;
            }

            // render
            if (shouldRender) {
                fps++;
                render();
                shouldRender = false;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // FPS Timer
        if (System.currentTimeMillis() - fpsTimer > 1000){
            System.out.printf("%d fps %d updates", fps, updates);
            fps = 0;
            updates = 0;
            fpsTimer += 1000;
        }
    }

    public synchronized void start(){
        if (running){
            return;
        }
        running = true;
        game = new Thread(this, "game");
        game.start();
    }

    public synchronized void stop(){
        if (!running){
            return;
        }
        running = false;
        System.exit(0);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Keyboard.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        Keyboard.keyReleased(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        screen.mousePressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        screen.mouseReleased(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        screen.mouseDragged(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        screen.mouseMoved(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
