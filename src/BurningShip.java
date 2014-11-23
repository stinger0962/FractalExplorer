
import java.awt.geom.Rectangle2D;
import java.lang.Math;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Nio
 */
public class BurningShip extends FractalGenerator {
    public static final int MAX_IERATIONS = 2000;
    /**
     * @override
     * @param range the range to iterate
     */
    public void getInitialRange(Rectangle2D.Double range)
    {
        range.x = -2;
        range.y = -2.5;
        range.width = 4;
        range.height = 4;
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
        //take absolute value of re and im before implement
        //nextRe, nextIm indicate coordintes of next pixel
        double re = 0.0, im = 0.0, nextRe = 0.0, nextIm = 0.0;
        while(i < 2000 && ((nextRe * nextRe + nextIm * nextIm) < 2*2))
        {
            
            nextRe = Math.abs(re) * Math.abs(re) - Math.abs(im) * Math.abs(im) + x;
            nextIm  = 2 * Math.abs(re) * Math.abs(im) + y;
            re = nextRe;
            im = nextIm;
            ++i;
        }
        if(i == 2000)
            return -1;
        else
            return i;
        
    }
    public String toString()
    {
        return "Burning Ship";
    }
}
