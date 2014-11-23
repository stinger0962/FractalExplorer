/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nio
 */

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;


public class JImageDisplay extends JComponent {
    private BufferedImage bImage;
    //public accessor 
    public BufferedImage getImage()
    {
        return bImage;
    }
    /**
     * constructor initialize a bufferedimage with width and height
     * also call JComponent.setPreferredSize() method 
     * so that the entire image is displayed
     * @param width width of the image
     * @param height height of the image
     */
    public JImageDisplay(int width, int height)
    {
        bImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
        Dimension dms = new Dimension(width, height);
        super.setPreferredSize(dms);
    }
    /**
     * @override JComponent.paintComponent() method
     * draw the image onto the component
     * @param g 
     */
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(bImage, 0, 0, bImage.getWidth(), bImage.getHeight(), null);
    }
    /**
     * set all pixels in the image data to black
     */
    public void clearImage()
    {
        for(int i = 0; i < bImage.getWidth(); i++)
        {
            for(int j = 0; j < bImage.getHeight(); j++)
            {
                bImage.setRGB(i, j, 0);
            }
        }
    }
    /**
     * set a pixel to have a specific color
     * @param x x-coordinate of the pixel
     * @param y y-coordinate of the pixel
     * @param rgbColor RGB color
     */
    public void drawPixel(int x, int y, int rgbColor)
    {
        bImage.setRGB(x, y, rgbColor);
    }
}
