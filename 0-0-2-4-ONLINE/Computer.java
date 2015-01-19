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
        super(red, blue, green, false, "TARS");
    }
    public void makeMove(List<Hexabox> hexaboxes)
    {
        Hexabox hexaboxMove = hexaboxMoveCheck(hexaboxes);
    }
    public Hexabox hexaboxMoveCheck(List<Hexabox> hexaboxesAll)
    {
        List<Hexabox> = hexaboxesAll;
        Hexabox hexaboxReturn = new Hexabox();
        boolean hexaboxMoveFound = false;
        int progressiveLoss = 0;
        for(int i = 0; i < hexaboxes.size(); i++)
        {
            Hexabox hexaboxCurrent = hexaboxes.get(i);
            if(hexaboxRemove.numberOfLines == 5)
            {
                hexaboxReturn = hexaboxCurrent;
                hexaboxMoveFound = true;
                break;
            }
        }
        if(!hexaboxMoveFound)
        {
            for(int i = 0; i < hexaboxes.size(); i++)
            {
                Hexabox hexaboxRemove = hexaboxes.get(i);
                if(hexaboxRemove.numberOfLines == 6)
                {
                    hexaboxes.remove(i); 
                }
            }
            for(int i = 0; i < hexaboxes.size(); i++)
            {
                Hexabox hexaboxRemove = hexaboxes.get(i);
                if(hexaboxRemove.numberOfLines == 4
                && hexaboxes.size() > 1)
                {
                    hexaboxes.remove(i); 
                }
                else if(hexaboxRemove.numberOfLines == 4
                && hexaboxes.size() = 1)
                {
                    progressiveLoss = 1;
                }
            }
            switch(progressizeLoss)
            {
                case 0 :
                    randomMove();
                    break;
                case 1 :
                    weighUpMoves();   
            }
        }
        return hexaboxReturn;
    }

}

