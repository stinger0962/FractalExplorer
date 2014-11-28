/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * 
 *client class, create and process GUI
 * @author Nio
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

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
     * indicates the number of ROWs not painted during a redraw process
     * WARNING: to avoid any concurrent access, ONLY access it from Event Dispatch Thread 
    */
    private int rowsRemaining;
   
    /**
     * a helper method to draw fractals
     * create a SwingWorker object for every single ROW in the image
     * implement worker's execute method 
     */
    private void drawFractal()
    {
        //for each ROW in the displayImage
        for(int j = 0; j < displaySize; j++) 
        {
            FractalWorker worker = new FractalWorker(j);
            //schedule this worker for execution on a SwingWorker thread
            worker.execute();
        }
    }
    /**
     * inner class extends SwingWorker, in order to handle the following time-consuming event:
     * computing color values for a single ROW of the fractal
     * the reason it only processes one ROW is to generate multiple threads, thus fastening rendering
     * if the program is running on a PC with multiple processors
     * WARNING: interactions with swing components is only allowed in process method
     */
    private class FractalWorker extends SwingWorker<Object, Object>
    {
        //Y-coordinate of the row
        private int yCoord;
        // integer array to store RGB values for each pixel in that row
        private int[] rgbValues;
        
        //constructor take Y-coordinate and store it
        public FractalWorker(int yCoord)
        {
            this.yCoord = yCoord;
        }
        //only compute the RGB values of pixel, don't paint! Remember the Swing restriction
        @Override
        protected Object doInBackground()
        {
            //allocate the array of integer, which is used to store RGBValues of the row
            rgbValues = new int[displaySize];
            //compute RGB value for each pixel in the row with index this.yCoord
            //i indicates current xCoord
            //transfer (i,this.Ycoord) from int to double (xCoord,yCoord) in order to calculate number of iterations
            //get the number of iterations
            for(int i = 0; i < displaySize; i++) 
            {
                int numIterations = 0;
                double xCoord = FractalGenerator.getCoord(complexPlane.x, complexPlane.x+complexPlane.width, displaySize, i);
                double yCoord = FractalGenerator.getCoord(complexPlane.y, complexPlane.y+complexPlane.height, displaySize, this.yCoord);
                numIterations = fractalBase.numIterations(xCoord, yCoord);
                //calculate RGBValue, store it to the array
                if(numIterations == -1)
                {
                    rgbValues[i] = 0;
                }
                else
                {
                    float hue = 0.7f + (float)numIterations / 200f;
                    rgbValues[i] = Color.HSBtoRGB(hue, 1f, 1f);
                }
            }
            return null;
        }
        /**
         * called when background task is finished
         * paint each pixel in that ROW
         */
        @Override
        protected void done() 
        {
            for(int i = 0; i < displaySize; i++)
            {
                //paint the pixel with computed RGB value
                displayImage.drawPixel(i, this.yCoord, rgbValues[i]);
            }
            //use partial update version of JComponent.repaint method
            //only repaint a small region start at(0, this.yCoord) with width of displaySize and height of 1
            displayImage.repaint(0, 0, this.yCoord, displaySize, 1);
        }

        
        
        
    }
    /**
     * inner class to handle various events
     * ActionListener event from the reset button - reset image
     * 
     */
    private class ActionListenerHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() instanceof JButton)
            {
                //distinguish between the two buttons, using their action command
                String command = e.getActionCommand();
                //event source is save button
                if(command.equals("Save"))
                {
                    //create a JFileChooser instance and set up its properties
                    JFileChooser saveFileChooser = new JFileChooser();
                    FileFilter filterExtension = new FileNameExtensionFilter("PNG images", "png");
                    saveFileChooser.setFileFilter(filterExtension);
                    saveFileChooser.setAcceptAllFileFilterUsed(false);
                    //JFileChooser.showSaveDialog return int, indicating user choice.
                    int option = saveFileChooser.showSaveDialog(displayImage); //chooser's position depends on displayImage.
                    //if user chooses a file, then go ahead
                    if(option == JFileChooser.APPROVE_OPTION)
                    {
                        //the Image to save
                        BufferedImage image = displayImage.getImage();
                        //simple step to ensure File name is correctly displayed with extension png
                        String fileName = saveFileChooser.getSelectedFile().getAbsolutePath();
                        int positionOfDot = fileName.lastIndexOf(".");
                        if(!(positionOfDot == -1))
                            fileName = fileName.substring(0, positionOfDot);
                        //File where the image is to be written into
                        File file = new File(fileName + ".png");
                        try
                        {
                            //javax.imageio.ImageIO has a static write method to save image
                            //it takes a Image to be written, an informal name of the format, and a File to be written to as parameters
                            //it throws IOException if an error occurs during writing
                            ImageIO.write(image,"png" , file);
                        }
                        catch(IOException exp)
                        {
                            //use javax.swing.showMessageDialog to show a message window
                            JOptionPane.showMessageDialog(displayImage, exp.getMessage(), "Cannot Save Image", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                //event source is reset button
                else if(command.equals("Reset"))
                {
                    fractalBase.getInitialRange(complexPlane);
                    drawFractal();
                }  
            }
            //if event source is JComboBox, specify a fractal and its range, then redraw
            else if(e.getSource() instanceof JComboBox)
            {
                JComboBox fractalCombo = (JComboBox)e.getSource();
                if(fractalCombo.getSelectedItem().toString().equals("Mandelbrot"))
                {
                    fractalBase = new Mandelbrot();
                }
                else if(fractalCombo.getSelectedItem().toString().equals("Tricorn"))
                {
                    fractalBase = new Tricorn();
                }
                else if(fractalCombo.getSelectedItem().toString().equals("Burning Ship"))
                {
                    fractalBase = new BurningShip();
                }
                //set up initial range for specific fractal and redraw
                fractalBase.getInitialRange(complexPlane);
                drawFractal();
            }
            
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
        //specify its initial range
        fractalBase.getInitialRange(complexPlane);
    }
    /**
     * this method initializes the Swing GUI:
     * a JFrame containing JImageDisplay, two buttons at bottom,
     * and a JPanel at top which includes a JLabel and a JComboBox
     * register inner class event handlers to proper components
     */
    public void createAndShowGUI()
    {
        JFrame mainWindow = new JFrame("Fractal Explorer");
        //create a JPanel for JLabel and JComboBox
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Fractal: "));
        JComboBox fractalComboBox = new JComboBox();
        //use JComboBox.addItem method 
        //in order to use JComboBox.getSelectedItem() to return Object
        fractalComboBox.addItem(new Mandelbrot());
        fractalComboBox.addItem(new Tricorn());
        fractalComboBox.addItem(new BurningShip());
        //create an object of inner class ActionListenerHandler to handle multiple events.
        ActionListenerHandler multipleHandler = new ActionListenerHandler();
        fractalComboBox.addActionListener(multipleHandler);
        //add JComboBox to JPanel, add it to JFrame, positioning North
        topPanel.add(fractalComboBox);
        mainWindow.getContentPane().add(topPanel,BorderLayout.NORTH);
        //create a JImageDisplay, register to MouseHandler, add it to JFrame
        displayImage = new JImageDisplay(displaySize,displaySize);
        displayImage.addMouseListener(new MouseListenerHandler());
        mainWindow.getContentPane().add(displayImage,BorderLayout.CENTER);
        //create a JPanel with two buttons, register to the common ActionHandler and set their action command
        JPanel bottomPanel = new JPanel();
        JButton saveButton = new JButton("Save Image");
        saveButton.setActionCommand("Save");
        saveButton.addActionListener(multipleHandler);
        
        JButton resetButton = new JButton("Reset Display");
        resetButton.setActionCommand("Reset");
        resetButton.addActionListener(multipleHandler);
        
        bottomPanel.add(saveButton);
        bottomPanel.add(resetButton);
        //add JPanel to JFrame, positioning South
        mainWindow.getContentPane().add(bottomPanel,BorderLayout.SOUTH);
        //set up the utilities of JFrame
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.pack();//Causes this Window to be sized to fit the preferred size and layouts of its subcomponents. 
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
