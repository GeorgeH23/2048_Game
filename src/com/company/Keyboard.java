package com.company;

import java.awt.event.KeyEvent;

public class Keyboard {

    public static boolean[] pressed = new boolean[256];
    public static boolean[] prev = new boolean[256];

    private Keyboard(){

    }

    public static void update(){
        for (int i = 0; i < 9; i++){
            if (i == 0){
                prev[KeyEvent.VK_LEFT] = pressed[KeyEvent.VK_LEFT];
            }
            if (i == 1){
                prev[KeyEvent.VK_RIGHT] = pressed[KeyEvent.VK_RIGHT];
            }
            if (i == 2){
                prev[KeyEvent.VK_UP] = pressed[KeyEvent.VK_UP];
            }
            if (i == 3){
                prev[KeyEvent.VK_DOWN] = pressed[KeyEvent.VK_DOWN];
            }
            if (i == 4){
                prev[KeyEvent.VK_Q] = pressed[KeyEvent.VK_Q];
            }
            if(i == 4) {
                prev[KeyEvent.VK_S] = pressed[KeyEvent.VK_S];
            }
            if(i == 5) {
                prev[KeyEvent.VK_W] = pressed[KeyEvent.VK_W];
            }
            if(i == 6) {
                prev[KeyEvent.VK_P] = pressed[KeyEvent.VK_P];
            }
            if(i == 7) {
                prev[KeyEvent.VK_R] = pressed[KeyEvent.VK_R];
            }
            if(i == 8) {
                prev[KeyEvent.VK_N] = pressed[KeyEvent.VK_N];
            }
        }
    }

    public static void keyPressed(KeyEvent e){
        pressed[e.getKeyCode()] = true;
    }

    public static void keyReleased(KeyEvent e){
        pressed[e.getKeyCode()] = false;
    }

    public static boolean typed(int keyEvent){
        return !pressed[keyEvent] && prev[keyEvent];
    }
}
