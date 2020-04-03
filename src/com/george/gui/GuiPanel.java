package com.george.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class GuiPanel {

    private ArrayList<GuiButton> buttons;

    public GuiPanel() {
        this.buttons = new ArrayList<GuiButton>();
    }

    public void update(){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).update();
        }
    }

    public void render(Graphics2D g){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).render(g);
        }
    }

    public void add(GuiButton button){
        buttons.add(button);
    }

    public void remove(GuiButton button){
        buttons.remove(button);
    }

    public void mousePressed(MouseEvent e){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).mousePressed(e);
        }

    }

    public void mouseReleased(MouseEvent e){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).mouseReleased(e);
        }

    }

    public void mouseDragged(MouseEvent e){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).mouseDragged(e);
        }

    }

    public void mouseMoved(MouseEvent e){
        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).mouseMoved(e);
        }

    }
}
