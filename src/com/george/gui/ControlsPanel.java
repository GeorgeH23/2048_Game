package com.george.gui;
import com.company.DrawUtils;
import com.company.Game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControlsPanel extends GuiPanel {

    private int backButtonWidth = 220;
    private int controlsX = 80;
    private int controlsY = 180;

    private String title = "Controls";
    private Font titleFont = Game.main.deriveFont(48f);
    private Font scoreFont = Game.main.deriveFont(20f);

    public ControlsPanel(){
        super();
        GuiButton backButton = new GuiButton(Game.WIDTH / 2 - backButtonWidth / 2, 500, backButtonWidth, 60);
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GuiScreen.getInstance().setCurrentPanel("Menu");
            }
        });
        backButton.setText("Back");
        add(backButton);
    }

    private void drawControls(Graphics2D g){
        g.setColor(Color.BLACK);
        g.setFont(scoreFont);

        g.drawString("W", controlsX, controlsY);
        g.setColor(Color.darkGray);
        g.drawString(" - Vol++", controlsX + 12, controlsY);
        g.setColor(Color.BLACK);
        g.drawString("S", controlsX, controlsY + 20);
        g.setColor(Color.darkGray);
        g.drawString(" - Vol--", controlsX + 12, controlsY + 20);
        g.setColor(Color.BLACK);
        g.drawString("P", controlsX, controlsY + 40);
        g.setColor(Color.darkGray);
        g.drawString(" - Pause Music", controlsX + 12, controlsY + 40);
        g.setColor(Color.BLACK);
        g.drawString("R", controlsX, controlsY + 60);
        g.setColor(Color.darkGray);
        g.drawString(" - Resume Music", controlsX + 12, controlsY + 60);
        g.setColor(Color.BLACK);
        g.drawString("N", controlsX, controlsY + 80);
        g.setColor(Color.darkGray);
        g.drawString(" - Next Song", controlsX + 12, controlsY + 80);
        g.setColor(Color.BLACK);
        g.drawString("M", controlsX, controlsY + 100);
        g.setColor(Color.darkGray);
        g.drawString(" - Mute Music", controlsX + 12, controlsY + 100);
        g.setColor(Color.BLACK);
        g.drawString("\nRight Arrow", controlsX, controlsY + 140);
        g.setColor(Color.darkGray);
        g.drawString(" - Slide Right", controlsX + 132, controlsY + 140);
        g.setColor(Color.BLACK);
        g.drawString("Left Arrow", controlsX, controlsY + 160);
        g.setColor(Color.darkGray);
        g.drawString(" - Slide Left", controlsX + 132, controlsY + 160);
        g.setColor(Color.BLACK);
        g.drawString("Up Arrow", controlsX, controlsY + 180);
        g.setColor(Color.darkGray);
        g.drawString(" - Slide Up", controlsX + 132, controlsY + 180);
        g.setColor(Color.BLACK);
        g.drawString("Down Arrow", controlsX, controlsY + 200);
        g.setColor(Color.darkGray);
        g.drawString(" - Slide Down", controlsX + 132, controlsY + 200);
    }

    @Override
    public void update(){
    }

    @Override
    public void render(Graphics2D g){
        super.render(g);
        g.setColor(new Color(0xd86304));
        g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, DrawUtils.getMessageHeight(title, titleFont, g) + 40);
        drawControls(g);
    }
}