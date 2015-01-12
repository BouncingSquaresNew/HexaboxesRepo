package graphics.demo.game;

/* Name: Hexaboxes.java
 * Date: 11/01/2015
 */

import graphics.demo.framework.Audio; //Framework for making Audio
import graphics.demo.framework.Game; //Framework for the game
import graphics.demo.framework.Graphics; //Framework for drawing anything to the screen
import graphics.demo.framework.Graphics.PixmapFormat; //Framework for drawing the pixmap images
import graphics.demo.framework.InBounds; //Imports a method, which can check the position of a touch event :D
import graphics.demo.framework.Input; //Framework for any input into the app
import graphics.demo.framework.Input.TouchEvent; //Framework for touch events arrayList
import graphics.demo.framework.Music; //Framework for playing music
import graphics.demo.framework.Pixmap; //Framework for interprating images
import graphics.demo.framework.Screen; //Framework for the screen size
import graphics.demo.framework.Sound; //Framework for playing sounds :D
import graphics.demo.framework.implementation.AccelerometerHandler; //The Accelerometer syste,

import java.text.DecimalFormat; //This import contains rounding methods to round a number
import java.util.ArrayList; //Imports the ArrayList framework
import java.util.List; //Imports the list framework
import java.util.Random; //Imports the framework for generating random variables

import android.graphics.Color; //A standard list of colors is imported
import android.graphics.Typeface; //The type face fonts are imported from the res folder
import android.sax.StartElementListener;
/* NB These can be altered by placing the .ttf file in the /res folder and changing NB(1) */


public class HexaboxesMain extends Screen {

//	Variable decleration
	private static String versionNumber = "0.0.2"; //The current app version number
	private int tester = 0; //Which tester level (0 = nightly, 1 = alpha, 2 = beta, 3 = published)

    //	This generates a new ArrayList for the values of the square objects
    private ArrayList<Hexabox> hexaboxes = new ArrayList<Hexabox>(); //An array list containing the hexaboxes
    private ArrayList<Line> lines = new ArrayList<Line>();
    private ArrayList<User> users = new ArrayList<User>();

	private Pixmap background; //The image of the background
    private Graphics g = null; //Import the graphics
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

    @Override
    public void present(float deltaTime) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
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
        int n = 2;
        usersAdd(n);
        hexaboxesAdd(10);
    }

    /*
     * This method will create default users and add them to the ArrayList
     */
    private void usersAdd(int n)
    {
        for(int i = 0; i < n; i++)
        {
            User defaultUser = new User();
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
            if(userToPlay.user == true)
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
     * It will create a null touch event, and when a touch up event is recorded,
     * It will then place this into the null holder
     * The method will then draw a line, with the user's colour, at the coordinates of the touch event
     */
    private void userMoveHandler(User userToPlay)
    {
        boolean touchUp = false;
        TouchEvent userMoveTouch = new TouchEvent ();
        while(touchUp == false)
        {
            List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
            for(int i = 0; i < touchEvents.size(); i ++)
            {
                TouchEvent touchEventCurrent = touchEvents.get (i);
                if(touchEventCurrent.type == touchEventCurrent.TOUCH_UP)
                {
                    touchUp = true;
                    userMoveTouch = touchEventCurrent;
                }
            }
        }
        Line userLine = new Line (userMoveTouch.x, userMoveTouch.y, 0, 0, userToPlay.red, userToPlay.green, userToPlay.blue);
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
