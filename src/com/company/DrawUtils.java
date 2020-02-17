package com.company;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

public class DrawUtils {

    private DrawUtils(){

    }

    public static int getMessageWidth(String message, Font font, Graphics2D g){
        g.setFont(font);
        Rectangle2D bounds = g.getFontMetrics().getStringBounds(message,g);
        return (int) bounds.getWidth();
    }

    public static int getMessageHeight(String message, Font font, Graphics2D g){
        g.setFont(font);
        if (message.length() == 0){
            return 0;
        }
        TextLayout tl = new TextLayout(message,font,g.getFontRenderContext());
        return (int) tl.getBounds().getHeight();
    }

    public static String formatTime(long milis){
        String formattedTime;

        String hourFormat = "";
        int hours = (int) (milis / 3600000);
        if (hours >= 1){
            milis -= hours * 3600000;
            if (hours < 10){
                hourFormat = "0" + hours;
            } else {
                hourFormat = "" + hours;
            }
            hourFormat += ":";
        }


        String minuteFormat;
        int minutes = (int) (milis / 60000);
        if (minutes >= 1){
            milis -= minutes * 60000;
            if (minutes < 10){
                minuteFormat = "0" + minutes;
            } else {
                minuteFormat = "" + minutes;
            }
        } else {
            minuteFormat = "00";
        }

        String secondFormat;
        int seconds = (int) (milis / 1000);
        if (seconds >= 1){
            milis -= seconds * 1000;
            if (seconds < 10){
                secondFormat = "0" + seconds;
            } else {
                secondFormat = "" + seconds;
            }
        } else {
            secondFormat = "00";
        }

        String miliFormat;
        if (milis > 99){
            miliFormat = "" + milis;
        } else if (milis > 9){
            miliFormat = "0" + milis;
        } else {
            miliFormat = "00" + milis;
        }

        formattedTime = hourFormat + minuteFormat + ":" + secondFormat + ":" + miliFormat;
        return formattedTime;
    }
}
