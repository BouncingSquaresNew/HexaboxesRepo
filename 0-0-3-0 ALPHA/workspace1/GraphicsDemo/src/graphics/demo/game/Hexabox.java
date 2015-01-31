package graphics.demo.game;

import graphics.demo.framework.Pixmap;

/**
 * Created by Ben on 11/01/2015.
 * Is a Hexabox object, which will describe a hexabox on screen
 */

public class Hexabox extends Box
{

    boolean filled; //Whether the Hexabox is filled
    double x; //Coordinates for the hexabox
    double y;
    Pixmap image;

    public Hexabox (int x, int y, Pixmap image)
    {
        super(x, y, image);
        this.filled = false;
    }
    public Hexabox (Pixmap image)
    {
        this(0, 0, image);
    }

}
