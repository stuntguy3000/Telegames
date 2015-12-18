package me.stuntguy3000.java.telegames.util;

import java.awt.*;
import java.awt.image.BufferedImage;

// @author Luke Anderson | stuntguy3000
public class ImageUtil {
    public static BufferedImage imageToBufferedImage(Image im) {
        BufferedImage bi = new BufferedImage(im.getWidth(null), im.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics bg = bi.getGraphics();
        bg.drawImage(im, 0, 0, null);
        bg.dispose();
        return bi;
    }
}
    