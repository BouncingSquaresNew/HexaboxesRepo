package graphics.demo.game;

import graphics.demo.framework.Pixmap;

/**
 * Created by Ben on 17/01/2015.
 */
public class Box
{
    boolean filled; //Whether the Hexabox is filled
    double x; //Coordinates for the hexabox
    double y;
    Pixmap image;

    public Box (int x, int y, Pixmap image)
    {
        this.filled = false;
        this.x = x;
        this.y = y;
        this.image = image;
    }
    public Box (Pixmap image)
    {
        this(0, 0, image);
    }
}
