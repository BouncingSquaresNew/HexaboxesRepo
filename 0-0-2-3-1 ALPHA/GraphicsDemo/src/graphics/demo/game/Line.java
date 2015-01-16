package graphics.demo.game;

/**
 * Created by Ben on 11/01/2015.
 */
public class Line
{
    int startX;
    int startY;
    int endX;
    int endY;
    int red;
    int green;
    int blue;
    boolean drawn;
    public Line (int startX, int startY, int endX, int endY, int red, int green, int blue) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    public Line(int startX, int startY, int red, int green, int blue)
    {
        this(startX, startY, startX, startY, red, green, blue);
    }
    public Line(int startX, int startY, int endX, int endY)
    {
        this(startX, startY, endX, endY, 0, 0, 0);
    }
    public Line(int red, int green, int blue)
    {
        this(0, 0, 0, 0, red, green, blue);
    }

    public Line ()
    {
        this(0, 0, 0, 0, 0, 0, 0);
    }
}

