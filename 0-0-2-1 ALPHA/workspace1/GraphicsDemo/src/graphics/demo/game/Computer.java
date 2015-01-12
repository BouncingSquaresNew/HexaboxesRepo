package graphics.demo.game;

/**
 * Created by Ben on 11/01/2015.
 */
public class Computer extends User
{
    int red;
    int blue;
    int green;
    boolean user;
    String name;
    Computer(int red, int blue, int green, boolean computer, String name, int playerNumber)
    {
        super(red, blue, green, computer, name);
    }
    Computer()
    {
        super(40, 80, 80, false, "HAL");
    }
    Computer(int red, int blue, int green)
    {
        super(red, blue, green, false, "HAL");
    }
}

