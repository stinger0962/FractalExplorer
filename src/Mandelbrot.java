
import java.awt.geom.Rectangle2D;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nio
 */
public class Mandelbrot extends FractalGenerator{
    public static final int MAX_IERATIONS = 2000;
    public void getInitialRange(Rectangle2D.Double range)
    {
        range.x = -2;
        range.y = -1.5;
        range.width = 3;
        range.height = 3;
    }
    /**
     * implement the iteration function and return times of iteration
     * @param x x-coordinate of the current pixel
     * @param y y-coordinate of the current pixel
     * @return the number of iterations
     */
    public int numIterations(double x, double y)
    {
        int i = 0;
        //re,im indicate coordintes of current pixel
        //nextRe, nextIm indicate coordintes of next pixel
        double re = 0.0, im = 0.0, nextRe = 0.0, nextIm = 0.0;
        while(i < 2000 && ((nextRe * nextRe + nextIm * nextIm) < 2*2))
        {
            nextRe = re * re - im * im + x;
            nextIm  = 2 * re * im + y;
            re = nextRe;
            im = nextIm;
            ++i;
        }
        if(i == 2000)
            return -1;
        else
            return i;
        
    }
}
