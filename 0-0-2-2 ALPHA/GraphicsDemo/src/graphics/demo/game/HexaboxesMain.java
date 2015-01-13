package graphics.demo.game;

/* Name: Hexaboxes.java
 * Date: 11/01/2015
 */

import graphics.demo.framework.Game;
import graphics.demo.framework.Graphics;
import graphics.demo.framework.InBounds;
import graphics.demo.framework.Input.TouchEvent;
import graphics.demo.framework.Pixmap;
import graphics.demo.framework.Screen;
import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import android.graphics.Typeface;


public class HexaboxesMain extends Screen {

/* Variable declaration */
	private static String versionNumber = "0.0.2.2"; //The current app version number

    //	This generates a new ArrayList for the values of the square objects
    private ArrayList<Hexabox> hexaboxes = new ArrayList<Hexabox>(); //An array list containing the hexaboxes
    private ArrayList<Line> lines = new ArrayList<Line>();
    private ArrayList<User> users = new ArrayList<User>();

	private Pixmap background; //The image of the background
    private Graphics g = game.getGraphics(); //Import the graphics
    private Typeface font = Typeface.createFromAsset(game.getAssets(), "fonts/roboto-light.ttf"); //The font face is taken

    /*
     * Class declaration, honouring screen extension
     */
    public HexaboxesMain(Game game)
    {
        super(game);
        initialise();
    }

//	The intro part only takes a couple of seconds and isn't skippable

	@Override
    public void update(float deltaTime)
    {
        userMove();
        updateDraw();
    }

    public void present(float deltaTime) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }

    /*
     * This method will call the touchEvents to determine the number of users,
     * And then create users with default profiles,
     * And add them to the ArrayList.
     * It will then add a set number of Hexaboxes objects, in accordance with the level
     */
    private void initialise ()
    {
        int n = userDialogue();
        usersAdd(n);
        if(n == 1)
        {
            Computer hal = new Computer();
            users.add(hal);
        }
        hexaboxesAdd(10);
    }

    /*
     * This will present a user dialogue to the user, where they can choose the number of players
     * It will then confirm the selection with inBounds events
     * If the user doesn't click on a button, it will then assume a two player mode
     */
    private int userDialogue()
    {
        int nUsers = 0;
        int width = UtilConstants.SCREEN_WIDTH;
        int height = UtilConstants.SCREEN_HEIGHT;
        g.drawRect (0, 0, width, height, Color.WHITE);
        g.drawText ("1 Player", width / 6, height / 6, 40, Color.rgb(255, 0, 0), font, 5);
        g.drawText ("2 Players", width / 6, 2 * height / 6, 40, Color.rgb(0,255,0), font, 5);
        g.drawText ("3 Players", width / 6, 3 * height / 6, 40, Color.rgb(0,100,255), font, 5);
        g.drawText ("4 Players", width / 6, 4 * height / 6, 40, Color.rgb(100,100,0), font, 5);
        TouchEvent touchEventUsers = returnTouchUpEvent();
        for(int i = 1; i <= 4; i ++)
        {
            if (InBounds.inBounds(touchEventUsers, width / 6, i * height / 6, 5 * width / 6, (1 + i) * height / 6))
            {
                nUsers = i;
            }
            else if (!InBounds.inBounds(touchEventUsers, width / 6, i * height / 6, 5 * width / 6, (1 + i) * height / 6)
                        && i == 4)
            {
                nUsers = 2;
            }
        }
        return nUsers;
    }

    /*
     * This method will get the list of touch events, and find a Touch Up Event,
     * By cycling through them until one is found
     */
    private TouchEvent returnTouchUpEvent ()
    {
        boolean touchUp = false;
        TouchEvent touchUpEvent = new TouchEvent();
        while (!touchUp)
        {
            List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
            for (int i = 0; i < touchEvents.size() ; i++)
            {
                TouchEvent touchEventCurrent = touchEvents.get (i);
                if (touchEventCurrent.type == TouchEvent.TOUCH_UP)
                {
                    touchUp = true;
                    touchUpEvent = touchEventCurrent;
                }
            }
        }
        return touchUpEvent;
    }

    /*
     * This method does pertty much the same thing, but will return a Touch_Down event,
     * Rather than a Touch_Up
     */
    private TouchEvent returnTouchDownEvent ()
    {
        boolean touchDown = false;
        TouchEvent touchDownEvent = new TouchEvent ();
        while(!touchDown)
        {
            List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
            for(int i = 0; i < touchEvents.size(); i ++)
            {
                TouchEvent touchEventCurrent = touchEvents.get (i);
                if(touchEventCurrent.type == TouchEvent.TOUCH_DOWN)
                {
                    touchDown = true;
                    touchDownEvent = touchEventCurrent;
                }
            }
        }
        return touchDownEvent;
    }

    /*
     * This method will create default users and add them to the ArrayList
     */
    private void usersAdd(int n)
    {
        for(int i = 0; i < n; i++)
        {
            User defaultUser = new User();
            defaultUser.red = 255;
            users.add(defaultUser);
        }
    }

    /*
     * This method will add default hexaboxes to the arraylist, up to n
     */
    private void hexaboxesAdd(int n)
    {
        for(int i = 0; i < n; i ++)
        {
            Hexabox calcHexabox = new Hexabox (i, 0, 0);
            hexaboxes.add(i, calcHexabox);
        }
    }

    /*
     * This method will handle the move for a user OR a computer
     * By determining whether a user is a computer,#
     * And then passing the object into a method to handle the move
     */
    private void userMove()
    {
        for(int i = 0; i < users.size(); i++)
        {
            User userToPlay = users.get(i);
            if(userToPlay.user)
            {
                userMoveHandler(userToPlay);
            }
            else
            {
                Computer hal = (Computer)users.get(i);
            }
        }
    }

    /*
     * This method will handle the move for a user,
     * It will call methods, which will return a touch down event, followed by a touch up event
     * It will use the coordinates of these to create a line object, starting at the down and ending at the up
     * It will then add this to the arraylist
     */
    private void userMoveHandler(User userToPlay)
    {
        TouchEvent userMoveTouchDown = returnTouchDownEvent();
        TouchEvent userMoveTouchUp = returnTouchUpEvent();
        Line userLine = new Line (userMoveTouchDown.x, userMoveTouchDown.y, userMoveTouchUp.x, userMoveTouchUp.y, userToPlay.red, userToPlay.green, userToPlay.blue);
        lines.add(userLine);
    }

    private void updateDraw()
    {
        g.clear(0);
        for(int i = 0; i < lines.size(); i ++)
        {
            Line lineToDraw = lines.get(i);
            g.drawLine(lineToDraw.startX, lineToDraw.startY, lineToDraw.endX, lineToDraw.endY, Color.rgb(lineToDraw.red, lineToDraw.green, lineToDraw.blue));
        }
    }


}
