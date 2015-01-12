package graphics.demo.game;

/**
 * Created by Ben on 11/01/2015.
 * Is a Hexabox object, which will describe a hexabox on screen
 */

public class Hexabox  {

    int number; //Each Hexabox will be labeled with a number
    boolean filled; //Whether the Hexabox is filled
    double x; //Coordinates for the hexabox
    double y;

    public Hexabox (int number, int x, int y)
    {
        this.number = number;
        this.filled = false;
        this.x = x;
        this.y = y;
    }
    public Hexabox (int number)
    {
        this(number, 0, 0);
    }

}
