/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nio
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import javax.swing.*;

public class FractalExplorer {
    /**
     * width and height of square fractal.
     */
    private int displaySize = 0;
    /**
     * the reference of actual image we use to display, update, and compute the fractals.
     */
    private JImageDisplay displayImage;
    /**
     * using base class reference so that we can show other kinds of fractals in the future.
     */
    private FractalGenerator fractalBase;
    /**
     * specifying the range of complex plane that we are currently displaying.
     */
    private Rectangle2D.Double complexPlane;
   
    /**
     * a helper method to draw fractals
     * i indicates current x-coordinate
     * j indicates current y-coordinate
     * transfer (i,j) to (xCoord,yCoord) to calculate number of iterations
     * draw different color according to number of iterations
     */
    private void drawFractal()
    {
        for(int i = 0; i < displaySize; i++) 
        {
            for(int j = 0; j < displaySize; j++)
            {
                int numIterations = 0;
                double xCoord = FractalGenerator.getCoord(complexPlane.x, complexPlane.x+complexPlane.width, displaySize, i);
                double yCoord = FractalGenerator.getCoord(complexPlane.y, complexPlane.y+complexPlane.height, displaySize, j);
                numIterations = fractalBase.numIterations(xCoord, yCoord);
                if(numIterations == -1)
                {
                    displayImage.drawPixel(i, j, 0);
                }
                else
                {
                    float hue = 0.7f + (float)numIterations / 200f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);
                    displayImage.drawPixel(i, j, rgbColor);
                }
            }
        }
        displayImage.repaint();
    }
    /**
     * inner class to handle ActionListener event fro the reset button
     * reset range to the initial range specified by the generator
     */
    private class ActionListenerHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            fractalBase.getInitialRange(complexPlane);
            drawFractal();
        }
    }
    /**
     * inner class to handle MouseListener, only mouseClick event
     * first transfer the current integer coordinates into double precision to a range
     * then call recenterAndZoomRange() and redraw
     */
    private class MouseListenerHandler extends MouseAdapter
    {
        public void mouseClicked(MouseEvent e)
        {
            double xCoord = FractalGenerator.getCoord(complexPlane.x, complexPlane.x+complexPlane.width, displaySize, e.getX());
            double yCoord = FractalGenerator.getCoord(complexPlane.x, complexPlane.x+complexPlane.width, displaySize, e.getY());
            fractalBase.recenterAndZoomRange(complexPlane, xCoord, yCoord, 0.5);
            drawFractal();
        }
    }
     /**
     * constructor set display size; initialize range and fractal
     * range is initialized by getInitialRange() method of Mandelbrot class
     * @param size 
     */
    public FractalExplorer(int size)
    {
        displaySize = size;
        complexPlane = new Rectangle2D.Double();
        fractalBase = new Mandelbrot();
        fractalBase.getInitialRange(complexPlane);
    }
    /**
     * this method initializes the Swing GUI:
     * a Jframe containing JImageDisplay and a button
     * register inner class event handlers to proper components
     */
    public void createAndShowGUI()
    {
        JFrame mainWindow = new JFrame("Fractal Explorer");
        displayImage = new JImageDisplay(displaySize,displaySize);
        displayImage.addMouseListener(new MouseListenerHandler());
        mainWindow.getContentPane().add(displayImage,BorderLayout.CENTER);
        JButton button = new JButton("Reset Display");
        button.addActionListener(new ActionListenerHandler());
        mainWindow.getContentPane().add(button,BorderLayout.SOUTH);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();
        mainWindow.setVisible(true);
        mainWindow.setResizable(false);
    }
    
    /**
     * main function
     * for now, it's simple: create GUI and draw picture
     */
    public static void main(String[] args)
    {
        FractalExplorer myExplorer = new FractalExplorer(600);
        myExplorer.createAndShowGUI();
        myExplorer.drawFractal();
    }
}
