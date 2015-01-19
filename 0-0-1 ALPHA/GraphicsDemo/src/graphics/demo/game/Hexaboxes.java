package graphics.demo.game;

/* Name: BouncingSquares.java
 * Date: 07/03/2014
 * Author: Benedict Winchester
 * Notes: NOTE denotes code to be corrected,
 * 		FIX denotes code which requires immidate attention
 * 		NB denotes important comments for code readers
 * 		OBS denotes obsolete code or variables which were either removed, or will be :)
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
/* NB These can be altered by placing the .ttf file in the /res folder and changing NB(1) */


public class Hexaboxes extends Screen {

//	Variable decleration
	static String versionNumber = "1"; //The current app version number
	public int tester = 0; //Which tester level (0 = nightly, 1 = alpha, 2 = beta, 3 = published)
	public boolean initialise = true; //This variable is used to ensure that the background is drawn the first time

//	This generates a new ArrayList for the values of the square objects
//	ArrayList<Object> shapes = new ArrayList<Object>(); //An array list containing squares, and eventually, all free moving objects

//	This generates the images for the objects
	Graphics g = game.getGraphics();
	public static Pixmap background; //The image of the square

    public Hexaboxes(Game game)
    {
        super(game); //Initialises the game
        Assets.font = Typeface.createFromAsset(game.getAssets(), "fonts/roboto-light.ttf"); //The font face is taken
    }

//	The intro part only takes a couple of seconds and isn't skippable

	@Override
    public void update(float deltaTime)
    {
        update(); //The main update method
        boolean backUpdate = false; //Create a boolean which will control whether to update the background
//      This is the touch event handling code
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents(); //The arraylist is defined
//      Cycles through the touchEvents, and if it's a touch down, adds it to the arraylist
        ArrayList<Object> touchDowns = new ArrayList<Object>(); //Initialises an arryalist for the touch down events
        for (int i = 0; i < touchEvents.size(); i++)
        {
        	TouchEvent touchEventI = (TouchEvent)touchEvents.get(i);
        	if (touchEventI.type == TouchEvent.TOUCH_DRAGGED) //Only drag events are registered
        	{
        		touchDowns.add(touchEventI); //Add touchEventI to the arraylist of touchdown events
        		backUpdate = true; //Refresh the background
        	}
        	else if (touchEventI.type == TouchEvent.TOUCH_UP) //If they released the line
        	{
        		backUpdate = true; //Then refresh the background anyway
        	}
        }
//      Update the backdrop only if a line has been added or if the app has just been opened
        if(backUpdate == true
        || initialise == true)
        {
        	updateDrawBackdrop(); //Draw the background
        }

//      Get the most recent touchDownEvent, and draw a line from the centre to it
        for (int i = 0; i < touchDowns.size(); i ++)
        {
        	TouchEvent touchDownI = (TouchEvent)touchDowns.get(i); //Grab the touch Event
            lineDraw(touchDownI, UtilConstants.SCREEN_HEIGHT / 2, UtilConstants.SCREEN_WIDTH / 2); //Draw the line
        }

//      TouchEvent event = touchEvents.get(i); //The touch events are cycled through
        updateDev(touchDowns.size()); //This method will update the developer information, such as screen orientation etc.

    }

    //just consists of a help screen image
    @Override
    public void present(float deltaTime)
    {
//    	updateDrawObject();
//      This code draws the squares onto the screen
    }


    public void update() //The main updater method
    {

    }

//  The dev update method
    public void updateDev(int touchDownSize)
    {
    	DecimalFormat df = new DecimalFormat("#.00"); //Creates a decimal rounder, that will round numbers to 2dp
    	int accelX = (int)(game.getInput().getAccelX()); //Grabs the screen orientation variables
    	int accelY = (int)(game.getInput().getAccelY());
    	int accelZ = (int)(game.getInput().getAccelZ());
    	g.drawText("" + touchDownSize, 60, 60, 20, Color.WHITE); //Draw the number of touch Events
    	initialise = false; //The app has been opened already and the background drawn
    }

//	double accelX = game.getInput().getAccelX(); How to grab orrientation
    public void updateDrawBackdrop()
    {
    	g.drawRect(0, 0, UtilConstants.SCREEN_HEIGHT, UtilConstants.SCREEN_WIDTH, Color.rgb(47,79,79));
    	g.drawText("HEXABOXES", 40, 40, 40, Color.WHITE);
    	devDraw();
    }

//  This method will draw a line to a touch event from initial coordinates
    public void lineDraw(TouchEvent touchDownI, int y, int x)
    {
    	g.drawLine(y, x, touchDownI.x, touchDownI.y, Color.WHITE);
    }

//  This method will draw the developer information :D
    public void devDraw()
    {

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
}
