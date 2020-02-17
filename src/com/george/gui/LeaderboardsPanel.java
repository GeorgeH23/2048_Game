package com.george.gui;

import com.company.DrawUtils;
import com.company.Game;
import com.company.Leaderboards;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class LeaderboardsPanel extends GuiPanel{

    private Leaderboards lBoard;
    private int buttonWidth = 100;
    private int backButtonWidth = 220;
    private int buttonSpacing = 20;
    private int buttonY = 120;
    private int buttonHeight = 50;
    private int leaderboardsX = 130;
    private int leaderboardsY = buttonY + buttonHeight + 90;

    private String title = "Leaderboards";
    private Font titleFont = Game.main.deriveFont(48f);
    private Font scoreFont = Game.main.deriveFont(26f);
    private State currentState = State.SCORE;

    private GuiButton tileButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2, buttonY, buttonWidth, buttonHeight);
    private GuiButton scoreButton = new GuiButton(Game.WIDTH / 2 - buttonWidth / 2 - tileButton.getWidth() - buttonSpacing, buttonY, buttonWidth, buttonHeight);
    private GuiButton timeButton = new GuiButton(Game.WIDTH / 2 + buttonWidth / 2 + buttonSpacing, buttonY, buttonWidth, buttonHeight);

    public LeaderboardsPanel(){
        super();
        lBoard = Leaderboards.getInstance();
        lBoard.loadScores();

        tileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentState = State.TILE;
            }
        });
        tileButton.setBtnTextColor(Color.WHITE);
        tileButton.setText("Tiles");
        add(tileButton);

        scoreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentState = State.SCORE;
            }
        });
        scoreButton.setBtnTextColor(Color.WHITE);
        scoreButton.setText("Scores");
        add(scoreButton);

        timeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentState = State.TIME;
            }
        });
        timeButton.setBtnTextColor(Color.WHITE);
        timeButton.setText("Times");
        add(timeButton);

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

    private void drawLeaderboards(Graphics2D g){
        ArrayList<String> strings = new ArrayList<String>();
        if(currentState == State.TIME){
            for(Long l : lBoard.getTopTimes()){
                strings.add(DrawUtils.formatTime(l));
            }
            timeButton.setBtnTextColor(Color.ORANGE);
            scoreButton.setBtnTextColor(Color.WHITE);
            tileButton.setBtnTextColor(Color.WHITE);
        }
        else if(currentState == State.TILE){
            strings = convertToStrings(lBoard.getTopTiles());
            tileButton.setBtnTextColor(Color.ORANGE);
            scoreButton.setBtnTextColor(Color.WHITE);
            timeButton.setBtnTextColor(Color.WHITE);
        }
        else {
            strings = convertToStrings(lBoard.getTopScores());
            scoreButton.setBtnTextColor(Color.ORANGE);
            timeButton.setBtnTextColor(Color.WHITE);
            tileButton.setBtnTextColor(Color.WHITE);
        }

        g.setColor(Color.black);
        g.setFont(scoreFont);

        for(int i = 0; i < strings.size(); i++){
            String s = "";
            if (strings.get(i).equals("596:31:23:647")){
                s = (i + 1) + ". " + "hh:mm:ss:ms";
            } else if(strings.get(i).equals("0")){
                s = (i + 1) + ". " + "No record.";
            } else {
                s = (i + 1) + ". " + strings.get(i);
            }
            g.drawString(s, leaderboardsX, leaderboardsY + i * 40);
        }
    }

    private ArrayList<String> convertToStrings(ArrayList<? extends Number> list){
        ArrayList<String> ret = new ArrayList<String>();
        for(Number n : list){
            ret.add(n.toString());
        }
        return ret;
    }

    @Override
    public void update(){

    }

    @Override
    public void render(Graphics2D g){
        super.render(g);
        g.setColor(new Color(0xd86304));
        g.drawString(title, Game.WIDTH / 2 - DrawUtils.getMessageWidth(title, titleFont, g) / 2, DrawUtils.getMessageHeight(title, titleFont, g) + 40);
        drawLeaderboards(g);
    }

    private enum State{
        SCORE,
        TILE,
        TIME
    }
}

