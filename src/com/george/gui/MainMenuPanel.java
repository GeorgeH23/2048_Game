package com.george.gui;

import com.company.DrawUtils;
import com.company.Game;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenuPanel extends GuiPanel {

    public static boolean wasPressed;

    private Font titleFont = Game.main.deriveFont(100f);
    private Font creatorFont = Game.main.deriveFont(15f);
    private String title = "2048";
    private String creator = "powered by G.H. Medi@";
    private int buttonWidth = 220;
    // private int spacing = 90;
    private int buttonHeight = 60;

    public MainMenuPanel(){
        super();
        GuiButton playButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 220, buttonWidth, buttonHeight);
        GuiButton scoresButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 290, buttonWidth, buttonHeight);
        GuiButton controlsButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 360, buttonWidth, buttonHeight);
        GuiButton quitButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, 430, buttonWidth, buttonHeight);

        playButton.setText("Play");
        scoresButton.setText("Leaderboards");
        controlsButton.setText("Controls");
        quitButton.setText("Quit");

        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                GuiScreen.getInstance().setCurrentPanel("Play");
                wasPressed = false;
            }
        });

        scoresButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Leaderboards");
            }
        });


        controlsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Controls");
            }
        });

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        add(playButton);
        add(scoresButton);
        add(controlsButton);
        add(quitButton);
    }

    @Override
    public void render(Graphics2D g) {
        super.render(g);
        g.setFont(titleFont);
        g.setColor(new Color(0xd86304));
        g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, 150);
        g.setColor(Color.black);
        g.setFont(creatorFont);
        g.drawString(creator, 10, Game.HEIGHT - 10);
    }
}
