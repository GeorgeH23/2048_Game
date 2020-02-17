package com.company;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Tile {

    public static final int WIDTH = 100;
    public static final int HEIGHT = 100;
    public static final int SLIDE_SPEED = 30;
    public static final int ARC_WIDTH = 10;
    public static final int ARC_HEIGHT = 10;

    private int value;
    private BufferedImage tileImage;
    private Color background;
    private Color text;
    private Font font;
    private Point slideTo;
    private int x;
    private int y;

    // Animation variables *******************************
    private boolean beginningAnimation = true;
    private double scaleFirst = 0.1;
    private BufferedImage beginningImage;

    private boolean combineAnimation = false;
    private double scaleCombine = 1.2;
    private BufferedImage combineImage;
//***************************************
    private boolean canCombine = true;


    public Tile(int value, int x, int y){
        this.value = value;
        this.x = x;
        this.y = y;
        slideTo = new Point(x, y);
        tileImage = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
        this.beginningImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        this.combineImage = new BufferedImage(WIDTH * 2, HEIGHT * 2, BufferedImage.TYPE_INT_ARGB);
        drawImage();
    }

    private void drawImage(){
        Graphics2D g = (Graphics2D) tileImage.getGraphics();
        if (value == 2){
            background = new Color(0xeee4da);
            text = new Color(0x776e65);
        } else if(value == 4){
            background = new Color(0xede0c8);
            text = new Color(0x776e65);
        } else if(value == 8){
            background = new Color(0xf2b179);
            text = new Color(0xf9f6f2);
        } else if(value == 16){
            background = new Color(0xf59563);
            text = new Color(0xf9f6f2);
        } else if(value == 32){
            background = new Color(0xf67c5f);
            text = new Color(0xf9f6f2);
        } else if(value == 64){
            background = new Color(0xf65e3b);
            text = new Color(0xf9f6f2);
        } else if(value == 128){
            background = new Color(0xedcf72);
            text = new Color(0xf9f6f2);
        } else if(value == 256){
            background = new Color(0xedcc61);
            text = new Color(0xf9f6f2);
        } else if(value == 512){
            background = new Color(0xedc850);
            text = new Color(0xf9f6f2);
        } else if(value == 1024){
            background = new Color(0xedc53f);
            text = new Color(0xf9f6f2);
        } else if(value == 2048){
            background = new Color(0xedc22e);
            text = new Color(0xf9f6f2);
        } else if(value == 4096){
            background = new Color(0xed2320);
            text = new Color(0xf9f6f2);
        } else if(value == 8192){
            background = new Color(0xf65e3b);
            text = new Color(0xffffff);
        } else {
            background = Color.black;
            text = Color.white;
        }

        g.setColor(new Color(0,0,0,0));
        g.fillRect(0,0,WIDTH,HEIGHT);

        g.setColor(background);
        g.fillRoundRect(0,0,WIDTH,HEIGHT,ARC_WIDTH,ARC_HEIGHT);

        g.setColor(text);

        if (value < 16384) {
            font = Game.main.deriveFont(36f);
            g.setFont(font);
        }
        else {
            font = Game.main.deriveFont(28f);
            g.setFont(font);
        }
        g.setFont(font);

        // get the center of the tile, and move half of the message content to the left
        // in this way you'll have a centered text inside each tile
        int drawX = WIDTH / 2 - DrawUtils.getMessageWidth("" + value, font, g) / 2;
        //
        int drawY = HEIGHT / 2 + DrawUtils.getMessageHeight("" + value, font, g) / 2;
        g.drawString("" + value, drawX, drawY);
        g.dispose();
    }

    public void update(){
        if (beginningAnimation){
            // this is used for scaling
            AffineTransform transform = new AffineTransform();
            transform.translate(WIDTH / 2 - scaleFirst * WIDTH / 2, HEIGHT / 2 - scaleFirst * HEIGHT / 2);
            transform.scale(scaleFirst, scaleFirst);
            Graphics2D g2d = (Graphics2D) beginningImage.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.drawImage(tileImage, transform, null);
            scaleFirst += 20.2;
            g2d.dispose();

            if (scaleFirst >= 1){
                beginningAnimation = false;
            }
        } else if (combineAnimation){
            // this is used for scaling
            AffineTransform transform = new AffineTransform();
            transform.translate(WIDTH / 2 - scaleCombine * WIDTH / 2, HEIGHT / 2 - scaleCombine * HEIGHT / 2);
            transform.scale(scaleCombine, scaleCombine);
            Graphics2D g2d = (Graphics2D) combineImage.getGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setColor(new Color(0, 0, 0, 0));
            g2d.fillRect(0, 0, WIDTH, HEIGHT);
            g2d.drawImage(tileImage, transform, null);
            scaleCombine -= 0.03;
            g2d.dispose();

            if (scaleCombine <= 1){
                combineAnimation = false;
            }
        }
    }

    public void render(Graphics2D g){
        if (beginningAnimation){
            g.drawImage(beginningImage, x, y, null);
        } else if (combineAnimation){
            g.drawImage(combineImage, (int) (x + WIDTH / 2 - scaleCombine * WIDTH / 2),
                                      (int) (y + WIDTH / 2 - scaleCombine * WIDTH / 2), null);
        } else {
            g.drawImage(tileImage, x, y, null);
        }
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        drawImage();
    }

    public boolean canCombine() {
        return canCombine;
    }

    public void setCanCombine(boolean canCombine) {
        this.canCombine = canCombine;
    }

    public Point getSlideTo() {
        return slideTo;
    }

    public void setSlideTo(Point slideTo) {
        this.slideTo = slideTo;
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

    public boolean isCombineAnimation() {
        return combineAnimation;
    }

    public void setCombineAnimation(boolean combineAnimation) {
        this.combineAnimation = combineAnimation;
        if (combineAnimation){
            scaleCombine = 1.2;
        }
    }
}
