package graphics.demo.game;

import graphics.demo.framework.Pixmap;

/**
 * Created by Ben on 23/12/2014.
 */
public class Shape
{
    private double x;
    private double y;
    private double xVector;
    private double yVector;
    private double mass;
    private static Pixmap image;

    public Shape(double x, double y, double xVector, double yVector, double mass, Pixmap image)
    {
        this.x = x;
        this.y = y;
        this.xVector = xVector;
        this.yVector = yVector;
        this.mass = mass;
        this.image = image;
    }



}
