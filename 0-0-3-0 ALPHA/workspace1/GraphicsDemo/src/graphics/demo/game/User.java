package graphics.demo.game;

/**
 * Created by Ben on 11/01/2015.
 */
public class User
{
    int red;
    int green;
    int blue;
    int score;
    boolean user;
    String name;
    User (int red, int green, int blue, boolean user, String name)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.user = user;
        this.name = name;
        this.score = 0;
    }
    User ()
    {
        this(0, 0, 0, true, "USER");
    }
}
