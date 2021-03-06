package graphics.demo.game;

/* Name: BouncingSquares.java
 * Date: 07/03/2014
 * Author: Benedict Winchester
 * Purpose: Holds all of the code for the main method :)
 */

import graphics.demo.framework.Game; //Framework for the game
import graphics.demo.framework.Graphics; //Framework for drawing objects
import graphics.demo.framework.Graphics.PixmapFormat; //Framework for drawing the pixmap images
import graphics.demo.framework.Input.TouchEvent; //Framework for touch events arrayList
import graphics.demo.framework.Pixmap; //Framework for interprating images
import graphics.demo.framework.Screen; //Framework for the screen size

import java.util.ArrayList; //Imports the ArrayList framework
import java.util.List; //Imports the list framework
import java.util.Random; //Imports the framework for generating random variables

import android.graphics.Color; //A standard list of colors is imported
import android.graphics.Typeface; //The type face fonts are imported from the res folder
/* NB These can be altered by placing the .ttf file in the /res folder and changing NB(1)
 * Notes: NOTE denotes code to be corrected,
 * FIX denotes code which requires immediate attention
 * NB denotes important comments for code readers
 * OBS denotes obsolete code or variables which were either removed, or will be :) */

public class BouncingSquares extends Screen { 

//	Variable decleration
	private static int MAX_NUM_SQUARES = 100; //The max score before you win
	private static String versionNumber = "1.7.2A"; //The current app version number
	private boolean developer = false; //Whether the nightly toggle is enabled
	private boolean nightly = false; //Whether nightly mode is enabled
	private boolean magnetEnable = false; //Whether the magnets are enabled
	private boolean christmas = false; //Whether to enable the Christmas theme
    private static int DEFAULT_NUM_SQUARES = 0; //The initial number of squares to begin with
	private static int DEFAULT_NUM_MAGNETS = 0; //The initial number of magnets to begin with
	private int difLevel = 0; //The current level (0 = endless, 1 = times)
	private static Typeface font;
	
	private static int MAX_INITIAL_SPEED = 4; //The maximum starting speed of the squares
	private int menuWidth = UtilConstants.SCREEN_WIDTH * 1 / 3; //The width of the menu is initialised
    private int menuHeight = 30; //The height of the menu is initialised NOTE to self, why not set?
    private int score  = 0; //The initial score
    private int highScore = 0; //The highscore value is initalised
    private int highTime = 0; //The fastest time they've completed it in is initialised
    private int currentNumSquares = DEFAULT_NUM_SQUARES; //The current number of squares that exist
    private int maxNumSquares = DEFAULT_NUM_SQUARES; //The maximum ever number of squares
    private int currentNumMagnets = DEFAULT_NUM_MAGNETS; //The current number of magnets that exist
    private static int maxNumMagnets = 3; //This is used to determine the number and position of the magnets
	/* This can be changed in order to easily alter the number of magnet objects */
    private int leeWay = 45; //The dimensions of the nightly toggles
    private int localCheckCurrent = 0; //The current location for the touch Event
    private int selectedElasticityValue = 0; //The current value to be taken from the Elasticity Array, and to be set to the squares
    private int keplerMode = 0; //Whether to enable planetary motion: 0 = Earth, 1 = Space, 2 = Solar
    private static int STROKE_WIDTH = 5;
    private double timerDown = 30; //The time limit allocated for the user to reach the MaxScore (MAX_NUM_SQUARES)
    private double gravityConstant = 0.1; //The screen's gravity constant
    private double keplerFreeSquareGravityConstant = 10; //The Newton's G constant
    private double solarMass = 50;
    private static double e = 0.9; //The coefficient of restitution between the squares
    private boolean touchTrueAlpha = false; //A variable is initialised, which will determine whether a square was pressed
    private boolean touchTrueBeta = false; //The same as for Alpha, but for determining whether a magnet was pressed
    private boolean errorA; //Used for debugging, NOTE Currently obsolete
    private boolean clearM = false; //Whether the magnets were cleared
    private boolean invert = false; //Whether to invert the colors
    private boolean magnetsFree = false; //Whether the magnets are fixed (false) or free to move(true)
    private boolean pro1 = false; //The following 4 "pro" toggles are used for allowing developer mode
    private boolean pro2 = false;
    private boolean pro3 = false;
    private boolean pro4 = false;
    private boolean win = false; //Whether the user has won (Initialised)
    private boolean fail = false; //Whether the user has lost (Initialised)
    private String message = null; //A message used for debugging NOTE currently obsolete
	
//	This generates a new ArrayList for the values of the square objects
    private ArrayList<Object> shapes = new ArrayList<Object>(); //An array list containing squares, and eventually, all free moving objects
    private ArrayList<Object> magnets = new ArrayList<Object>(); //An array list containing all of the magnets
	private ArrayList<Object> stars = new ArrayList<Object>(); //An array list containing all of the stars which are fixed
	private ArrayList<Object> removers = new ArrayList<Object>(); //The arraylist for removing objects
	private double[] Elasticity = new double[2]; //The array list containing the values for the elasticity
	
//	This generates the images for the objects
	private Graphics g = game.getGraphics();
    private static Pixmap image; //The image of the square
    private static Pixmap smallimage; //The image of the smaller keperian squares
    private static Pixmap magnetImage; //The image of the magnet (obsolete, NOTE as the magnets are rectangles)
    private static Pixmap backdrop;	// The image for the background
    private static Pixmap sun; //The image for the sun in "solar system" mode
	
    public BouncingSquares(Game game) 
    {    	
        super(game); //Initialises the game
        setupDefaultElasticity();
        
        // This assigns the values for the images from the assets file
    	image = g.newPixmap("ball.png", PixmapFormat.ARGB4444);
    	smallimage = g.newPixmap("smallball.png", PixmapFormat.ARGB4444);
		magnetImage = g.newPixmap("ball3.png", PixmapFormat.ARGB4444);
		backdrop = g.newPixmap("backdrop.png", PixmapFormat.ARGB4444);
		sun = g.newPixmap("sun.png", PixmapFormat.ARGB4444);

        // Grabs the typeface from the assets file
		font = Typeface.createFromAsset(game.getAssets(), "fonts/roboto-light.ttf");

        // This assigns the initial values for the squares coordinates and vectors.
        for (int b=0;b<currentNumSquares;b++) //So long as there are few squares than the maximum that can be initialised
        {
        	initialSquares(b);
        }
        Star starSol = new Star (sun, solarMass);
        stars.add(starSol);
        
//	This makes sure that none of the squares are initially colliding, and then adjusts their positioins to compensate.
        initialOverlapPrevention();
    }
    
//	The intro part only takes a couple of seconds and isn't skippable 

	@Override 
    public void update(float deltaTime) 
    {
        update(); //The main update method
//      This is the touch event handling code
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents(); //The arraylist is defined
        int len = touchEvents.size();
        for(int i = 0; i < len; i++) 
        {
        	TouchEvent event = touchEvents.get(i); //The touch events are cycled through
            if((event.type == TouchEvent.TOUCH_UP)) //If the event was a touch up event
            		// NB Here, touch down events didn't work, as a drag was interprated
	        {
//      If The Touch Event is definitely a Touch UP
           		localCheckCurrent = toggleCheck(event.x, event.y);
//           	If no toggles were pressed
            	if(localCheckCurrent == 0) //If no toggles were pressed
				{
            		developerToggle(event.x, event.y); //Check to see whether to toggle developer mode
//					Quite a long bit of code, NOTE to self, edit it down :)
				
//            		If there are no squares currently, then
//This code works if there are no squares (make a new square)
             		if(shapes.size() == 0)
            		{
             			shapesAddSquare(event.x, event.y); //Adds the square
            		}
//            		If there is atleast 1 shape,
//            		And clear is set to false (no squares off screen)
             		else if(shapes.size() >= 1)
	            	{
	            		touchTrueAlpha = false;
	            		touchTrueBeta = false;
//	            		This checks to see whether any squares
//	            		were clicked on
//	            		If a square was clicked on, then increase the score
	           			squaresTouched(event.x, event.y);
//	            		If no squares were clicked on, check for magnet events
//	            		If a magnet was pressed, cycle through this obsolete code
//    	            	Check to see wheter any magnets were pressed
	            	}
				}
	        }
        }
    }

    //just consists of a help screen image
    @Override
    public void present(float deltaTime) 
    {
    	int timerDownInt = (int)timerDown; //Calculates the timer value to 2 dp
		int timerDownDec = (int)((timerDown - timerDownInt) * 100);
    	updateDraw(timerDownInt, timerDownDec);        
//      This code draws the squares onto the screen
        
    }
    
    public void initialSquares(int b)
    {
//    	Generate random vectors and positions for the squares
    	Random random = new Random(); //Generates a new set list of random numbers
    	int x = random.nextInt(UtilConstants.SCREEN_WIDTH-image.getWidth())+1;
    	int y = random.nextInt(UtilConstants.SCREEN_HEIGHT-image.getHeight())+1;
        shapesAddSquare(x, y);
    }
    
    public static double restitution()
    {
    	return e;
    }
    
    public void update() //The main updater method
    {
//    	This code recalculates the squares' positions and their bouncing coefficients
        for(int i = 0; i < shapes.size(); i++) //The squares are cycled through
        {
        	Square squareI = (Square)shapes.get(i);
            squareI.recalculate(); //The squares are then re-calculated
            gravityCalcMethod(squareI); //Calculates the gravity factors acting on the squares
        }
        for(int i = 0; i<magnets.size(); i++) //The magnets are cycled through
        {
        	Magnet magnetI = (Magnet)magnets.get(i);
        	magnetI.recalculate(image.getWidth(), image.getHeight()); //The magnets are recalculated
            if(magnetsFree == true)
            {
    	        magnetI.xVector = gravityCalcMethodX(magnetI.xVector);
    	        magnetI.yVector = gravityCalcMethodY(magnetI.yVector);
            }
        }
//    	This loops through all of the squaresm and applies the square and kepler logic
        Star starSol = (Star)stars.get(0);
        for(int i = 0 ; i < currentNumSquares; i ++)
        {
        	squareCollideSquareLogic(i);
        	if(keplerMode == 1) //Apply kepler motion if enabled
        	{
        		keplerianFreeSquare(i);
        	}
        	if(keplerMode == 2)
        	{
        		keplerianSolarSystemSquare(i, starSol);
        	}
        }
        
//      Check whether any shapes have crashed into the sun
        for(int i = 0 ; i < removers.size() ; i ++)
        {
        	Remover removerI = (Remover)removers.get(i);
        	shapes.remove(removerI.id - i);
        	currentNumSquares = currentNumSquares - 1;
        }
        removers.clear();

//		Recalculates the squares gravity factors
//		This code is for the magnet collisions
        if(magnetEnable == true) //If the magnets are enabled
        {
	        for(int i = 0 ; i < currentNumMagnets; i ++) //loop through all of the magnets
	        {
	        	magnetCollideMagnet(i);
	        }
        }
//     	This is almost OBS (obsolete), NOTE as this recalculates the magnets positions
	    if(magnetEnable == true)
	    {
	        for(int i = 0 ; i < currentNumSquares; i ++)
		    {
	        	squareCollideMagnet(i);
		    }
	    }
//		This will enable developer mode if the combination is selected
        developer();
        
//      This is the counting down code
		countDown();

//		This determins wheter the user has won (has exceeded the maximum score limit set)
//		Or whether they've lost (failed to win in the time limit
		win();
		fail();
		highscore(); //Determines the new highscore
		hightime(); //Determines the fastest time they've won in
		shapeMax(); //Updates the maximum number of squares
    }
    
    public int toggleCheck(int eventX, int eventY)
    {
    	int localCheck = localeCheck(eventX, eventY); //The location method is called with the event coordinates, to determine the event position
/* Local Check Values for the touch Event location
 * 0 = No toggles pressed
 * 1 = Clear Squares Toggle
 * 2 = Nightly Toggle
 * 3 = Magnet Activate Toggle
 * 4 = Invert Colors Easteregg
 * 5 = Third Toggle from Right
 * 6 = DifLevel Toggle
 * 7 = Forth Toggle from Right
 * 8 = Timer Toggle
 * 9 = Fifth Toggle from Right
 */
//		If the clear toggle was pressed, clear both the squares and the magnets
   		if(localCheck == 1
		|| (localCheck == 8 && difLevel == 1)) //If the clear button was pressed
   		{
			clear(true, true);
   		}
//    	If the nightly toggle was pressed, then change the mode
    	if(localCheck == 2 && developer == true)
    	{
    		nightlyToggle();
   		}
//    	If developer mode is not on, then set the location to 0
   		else if(localCheck == 2 && developer == false)
    	{
   			localCheck = 0;
   		}
// 		If the magnet button was pressed, generate the magnets
   		else if(localCheck == 3
 		&& developer == true
  		&& magnetEnable == true
  		&& currentNumMagnets == 0) //If the magnet was false, draw them
   		{
       		for(int m = 0; m < maxNumMagnets; m++)
       		{
//       			Generate the magnets position relative to the screen constants
       			Magnet magnetM = new Magnet(UtilConstants.SCREEN_WIDTH / 2, (m+1) * UtilConstants.SCREEN_HEIGHT / (maxNumMagnets+1) - magnetImage.getHeight() / 2, 1, 0, currentNumMagnets, magnetImage);
        		magnets.add(magnetM);
//        		Add the new magnets
        		currentNumMagnets = currentNumMagnets + 1;
        	}
   		}
		else if(localCheck == 3
		&& currentNumMagnets >= 1
		&& magnetEnable == true
		&& developer == true) //If there are already magnets, remove them
		{
			clear(false, true); //clear magnets, not squares
			currentNumMagnets = 0;
		}
		else if(localCheck == 3
		&& developer == false)
		{
			localCheck = 0; //If developer mode isn't on, then set the location to 0
		}
//    	If the easterEgg was pressed, change the color inversion
    	else if (localCheck == 4)
    	{
    		invert();
    	}
//    	If the gravity toggle was pressed, enable planetary gravity
    	else if(localCheck == 5
    	&& developer == true)
    	{
    		keplerModeToggle();
    	}
    	else if(localCheck == 5
    	&& developer == false)
    	{
    		localCheck = 0;
    	}
//    	If the kepler toggle was pressed, toggle the kepler toggle mode
    	else if(localCheck == 6)
    	{
    		keplerModeToggle(); //Toggle the difficulty between 0 and 2
    	}
//    	If the magnet free toggle was pressed, toggle whether the magnets are free to move
    	else if(localCheck == 7
    	&& magnetEnable == true
		&& developer == true)
    	{
    		magnetFreeToggle();
    	}
    	else if(localCheck == 7
    	&& developer == false)
    	{
    		localCheck = 0;
    	}
//    	If the mode toggle was pressed, the level will be increased, and the timer
//    	started, meaning that they now have 30 seconds
		if(localCheck == 8)
		{
			levelToggle(); //Toggles the level with pre-adjusted settings
		}
//		If the elasticity toggle was pressed, then alter the elasticity of the squares
		if(localCheck == 9
		&& developer == true)
		{
			elasticityCycle();
		}
		else if(localCheck == 9
		&& developer == false)
		{
			localCheck = 0;
		}
//    	If the magnet enable toggle was pressed, toggle wheter the magnets are enabled
		if(localCheck == 10)
		{
			freeze(true, true);
		}
		return localCheck;
    }
    
    public void shapesAddSquare(int eventX, int eventY)
    {
    	Random random = new Random(); //Generates a new set list of random numbers
    	double Ax = eventX-(image.getWidth()/2);
		double Ay = eventY-(image.getHeight()/2);
		double AxVector = random.nextInt(2 * MAX_INITIAL_SPEED) - MAX_INITIAL_SPEED;
	 	double AyVector = random.nextInt(2 * MAX_INITIAL_SPEED) - MAX_INITIAL_SPEED;
		int name = currentNumSquares;
        double elasticity = Elasticity[selectedElasticityValue];
		if(keplerMode == 0)
		{
			Square squareA = new Square(Ax, Ay, AxVector, AyVector, name, image, 0, elasticity, elasticity);
	       	shapes.add(squareA);
		}
		else if(keplerMode == 1
				|| keplerMode == 2)
		{
			Square squareB = new Square(Ax, Ay, AxVector, AyVector, name, smallimage, 0, elasticity, elasticity);
	       	shapes.add(squareB);
		}
		currentNumSquares = currentNumSquares + 1;
		
    }
    
    public boolean touchShape (double squareX, double squareY, int width, int height, int touchX, int touchY)
    {
    	boolean touchShape = false;
    	if(touchX >= squareX 
    			&& touchX <= squareX + width
    			&& touchY >= squareY
    			&& touchY <= squareY + height)
    	{
    		touchShape = true;
    	}
    	return touchShape;
    }
    
    public void squaresTouched(int eventX, int eventY)
    {
    	touchTrueAlpha = false;
		for(int k = 0; k <= shapes.size() - 1; k++)
		{
			touchTrueAlpha = touchedShape(k, eventX, eventY);
		}
//		If a square was clicked on, then increase the score
		if(touchTrueAlpha == true)
		{
			score = score + 1;
		}
		if(touchTrueAlpha == false)
		{
			touchTrueBeta = magnetsTouched(eventX, eventY);
			touchTrueBeta = false;
			if(touchTrueBeta == false)
			{
				shapesAddSquare(eventX, eventY);
				errorA = false;
	            score = score -1;
			}
		}
    }
    
    public boolean magnetsTouched(int eventX, int eventY)
    {
    	boolean touchTrueBeta = false;
    	for(int k = 0; k + 1 <= magnets.size() - 1; k++)
		{
			Magnet magnetK = (Magnet)magnets.get(k);
			boolean touchShape = touchShape(magnetK.x, magnetK.y, magnetImage.getWidth(), magnetImage.getHeight(), eventX, eventY);
			if(touchShape == true)
    		{
    			touchTrueBeta = true;
    			errorA = true;
    		}
		}
//		If a magnet was pressed, teleport the magnet
    	return touchTrueBeta;
    }
    
    public int localeCheck(int eventX, int eventY)
    {
    	int localCheck = 0;
    	if(eventX >= UtilConstants.SCREEN_WIDTH - menuWidth
    			&& eventY >= UtilConstants.SCREEN_HEIGHT - menuHeight * 2
    			&& eventY <= UtilConstants.SCREEN_HEIGHT - menuHeight)
    	{
    		localCheck = 1; // The clear squares menu
    	}
    	if(eventX >= UtilConstants.SCREEN_WIDTH - leeWay
    			&& eventY <= leeWay)
    	{
    		localCheck = 2; //The nightly toggle
    	}
    	if(eventX >= UtilConstants.SCREEN_WIDTH - menuWidth
    			&& eventY <= UtilConstants.SCREEN_HEIGHT - menuHeight * 5
    			&& eventY >= UtilConstants.SCREEN_HEIGHT - menuHeight * 6)
    	{
    		localCheck = 3; //The Magnet Activate Toggle
    	}
    	if(eventX >= UtilConstants.SCREEN_WIDTH - menuWidth
    			&& eventY >= UtilConstants.SCREEN_HEIGHT - menuHeight)
    	{
    		localCheck = 4; //The Color Inversion EasterEgg Toggle
    	}
    	if(eventX >= UtilConstants.SCREEN_WIDTH - 3 * leeWay
    			&& eventX <= UtilConstants.SCREEN_WIDTH - 2 * leeWay
    			&& eventY <= leeWay)
    	{
    		localCheck = 5; //Third Toggle in from Right
    	}
    	if(eventX >= UtilConstants.SCREEN_WIDTH - menuWidth
    			&& eventY >= UtilConstants.SCREEN_HEIGHT - 5 * menuHeight
    			&& eventY <= UtilConstants.SCREEN_HEIGHT - 4 * menuHeight)
    	{
    		localCheck = 6; // The DifLevel Toggle
    	}
//    	if(eventX >= UtilConstants.SCREEN_WIDTH - (leeWay * 4)
//    			&& eventX <= UtilConstants.SCREEN_WIDTH - (leeWay * 3)
//    			&& eventY <= leeWay)
//    	{
//    		localCheck = 7; //Forth Toggle in pressed
//    	}
		if(eventX >= UtilConstants.SCREEN_WIDTH - menuWidth
				&& eventY >= UtilConstants.SCREEN_HEIGHT - menuHeight * 3
				&& eventY <= UtilConstants.SCREEN_HEIGHT - menuHeight * 2)
		{
			localCheck = 8; //Timer toggle
		}
//		if(eventX >= UtilConstants.SCREEN_WIDTH - 5 * leeWay
//				&& eventX <= UtilConstants.SCREEN_WIDTH - 4 * leeWay
//				&& eventY <= leeWay)
//		{
//			localCheck = 9; //Fifth Toggle from the Right
//		}
		if(eventY >= UtilConstants.SCREEN_HEIGHT - 4 * menuHeight
		&& eventY <= UtilConstants.SCREEN_HEIGHT - 3 * menuHeight
		&& eventX >= UtilConstants.SCREEN_WIDTH - menuWidth)
		{
			localCheck = 10; //Freeze menu toggle
		}
    	return localCheck;
    }
    
    public static Square teleport(Square squareTeleport)
    {
    	squareTeleport.x = UtilConstants.SCREEN_WIDTH * 3;
    	squareTeleport.y = UtilConstants.SCREEN_HEIGHT * 3;
    	squareTeleport.xVector = 0;
    	squareTeleport.yVector = 0;
    	return squareTeleport;
    }
    
    public void highscore()
    {
    	if(score >= highScore)
        {
        	highScore = score;
        }
    }
    
    public void freeze(boolean shapeFreeze, boolean magnetFreeze)//Used to freeze the objects
    {
    	for(int i = 0; i < currentNumSquares; i++) //Cycles through the squares
    	{
    		Square squareI = (Square)shapes.get(i);
    		squareI.setSquare(squareI.x, squareI.y, 0, 0);
    	}
    	for(int i = 0; i < magnets.size(); i++) //Cycles through the squares
    	{
    		Magnet magnetI = (Magnet)magnets.get(i);
    		magnetI.setMagnet(magnetI.x, magnetI.y, 0, 0);
    	}
    }
    
    public void hightime()
    {
    	if(win == true
    	&& timerDown >= highTime)
    	{
    		highTime = (int)timerDown;
    	}
    }
    
    public void shapeMax()
    {
    	if (currentNumSquares > maxNumSquares)
    	{
    		maxNumSquares = currentNumSquares;
    	}
    }
    
    public void gravityCalcMethod(Square squareI)
    {
        squareI.xVector = gravityCalcMethodX(squareI.xVector);
       	squareI.yVector = gravityCalcMethodY(squareI.yVector);
    }
    
    public static Magnet teleportM(Magnet magnetTeleport)
    {
    	magnetTeleport.x = UtilConstants.SCREEN_WIDTH * 6;
    	magnetTeleport.y = UtilConstants.SCREEN_HEIGHT * 6;
    	magnetTeleport.xVector = 0;
    	magnetTeleport.yVector = 0;
    	return magnetTeleport;
    }
    
    public double gravityCalcMethodY(double originalYVector)
    {    	
    	double gravityVectorY = 0;
    	double angle = 0;
    	angle = angleMethodY();
    	gravityVectorY = gravityCalcGeneric(angle, originalYVector);
    	return gravityVectorY;
    }
    
	public void squareCollideSquareLogic(int i)
	{
		Square squareI = (Square)shapes.get(i);
    	boolean glitching = false;
    	for(int j = i + 1 ; j < currentNumSquares; j++)
       	{
       		Square squareJ = (Square)shapes.get(j);
//	Now, this will test to see whether the squares overlap
       		if(squareI.overlaps(squareJ) == true
       		&& squareI.glitch == 0
    		&& squareJ.glitch == 0)
       		{
//If they overlap and their counter is zero,
//	Then the collision logic applies and 1 is added to the counter
        		squareI.collide(squareJ);
       			glitching = true;
       		}
//1 is then added to the counter
       		else if(squareI.overlaps(squareJ) == true)
       		{
        		glitching = true;
       		}
    	}
//If the square collides with no other squares, its counter
//is reset to 0
    	if(glitching == false)
    	{
    		squareI.glitch = 0;
    	}
    
	}
	
	public void initialOverlapPrevention()
	{
		for(int i = 0; i < currentNumSquares;i++) //SquareI is cycled
        {
    		Square squareI = (Square)shapes.get(i);
        	for(int j = 0 + i + 1; j < currentNumSquares; j++) //SquareJ is cycled through the other squares
        	{
        		Square squareJ = (Square)shapes.get(j);
//	        	NOTE change to the .overlap method so as to reduce code
        		while(squareI.overlaps(squareJ) == true)
	        	{
	        		squareI.x = squareI.x + image.getWidth() + 1; //Hence, if the squares overlap, they are moved until they no longer do so
	        		// NOTE Errors could arrise here due to the squares being re-positioned offscreen, FIX
	        	}
        	}
        }
	}
	
	public void magnetCollideMagnet(int i)
	{
		Magnet magnetI = (Magnet)magnets.get(i);
    	boolean glitching = false;
    	for(int j = i + 1 ; j < currentNumSquares; j++)
       	{
        	Magnet magnetJ = (Magnet)magnets.get(j);
//Now, this will test to see whether the squares overlap
        	if(magnetI.overlaps(magnetJ) == true
        	&& magnetI.glitch == 0
       		&& magnetJ.glitch == 0)
       		{
//  If they overlap and their counter is zero,
// 	Then the collision logic applies and 1 is added to the counter
        		magnetI.collide(magnetJ);
        		glitching = true;
        	}
//	1 is then added to the counter
        	else if(magnetI.overlaps(magnetJ) == true)
        	{
        		glitching = true;
        	}
       	}
//  If the square collides with no other squares, its counter
//  is reset to 0
    	if(glitching == false)
    	{
    		magnetI.glitch = 0;
    	}
    
	}
	
	public void squareCollideMagnet(int i)
	{
		Square squareI = (Square)shapes.get(i);
    	boolean glitching = false;
    	for(int j = 0 ; j < magnets.size(); j++)
       	{
        	Magnet magnetJ = (Magnet)magnets.get(j);
//Now, this will test to see whether the squares overlap
        	if(squareI.overlapsMagnet(magnetJ) == true
        	&& squareI.glitch == 0
       		&& magnetJ.glitch == 0)
       		{
//  If they overlap and their counter is zero,
// 	Then the collision logic applies and 1 is added to the counter
        		squareI.collideMagnet(magnetJ);
        		glitching = true;
        	}
//	1 is then added to the counter
        	else if(squareI.overlapsMagnet(magnetJ) == true)
        	{
        		glitching = true;
        	}
       	}
//  If the square collides with no other squares, its counter
//  is reset to 0
    	if(glitching == false)
    	{
    		squareI.glitch = 0;
    	}
	}
	
//	This method is for the squares floating freely, but attracted to eachother by gravitational froces
	public void keplerianFreeSquare(int i)
	{
		Square squareI = (Square)shapes.get(i); //Get square I from the ArrayList
		for (int j = i + 1 ; j < currentNumSquares ; j++)
		{
			Square squareJ = (Square)shapes.get(j); //Get squareJ from the arraylist
//			Calculate the distance between the squares
			double distX = squareI.x - squareJ.x; //Dist in X
			if(distX < 10)
			{
				distX = 10;
			}
			double distY = squareI.y - squareJ.y; //Dist in Y, where the positive value is if I is downright of J
			if(distY < 10)
			{
				distY = 10;
			}
			double distT = Math.sqrt(distX * distX + distY * distY); //Calculate the actual distance between them
			double accelG = keplerFreeSquareGravityConstant * squareI.mass * squareJ.mass / (distT * distT); //And use this to calculate the gravitational accel
			double theta = Math.atan(distY / distX); //Now calculate the angle between them
			double xAccel = accelG * Math.cos(theta); //And use this to calculate the X Acceleration
			double yAccel = accelG * Math.sin(theta); //And the Y Acceleration
			
//			If I is to the Right of J, 
			if(squareI.x > squareJ.x)
			{
				squareI.xVector = squareI.xVector - xAccel; //Then accelerate I to the left
				squareJ.xVector = squareJ.xVector + xAccel; //And J to the right
			}
			else if(squareI.x < squareJ.x) //Otherwise, if I is to the left of J
			{
				squareI.xVector = squareI.xVector + xAccel; //Accelerate I to the right
				squareJ.xVector = squareJ.xVector - xAccel; //And J to the left
			}
			
			if(squareI.y > squareJ.y) //If I is above J
			{
				squareI.yVector = squareI.yVector - yAccel; //Then I should accelerate downwards
				squareJ.yVector = squareJ.yVector + yAccel; //And J should accelerate upwards
			}
			else if(squareI.y < squareJ.y) //But if I is below J
			{
				squareI.yVector = squareI.yVector + yAccel; //Then I should accelerate upwards
				squareJ.yVector = squareJ.yVector - yAccel; //And J should acclrate downwards
			}
		}
	}
	
//  This method is for the squares in a solar systemesque layout
	public void keplerianSolarSystemSquare(int i, Star starSol)
    {
//    	Do the stuff
		keplerianFreeSquare(i); //Will call the free-floating method
    	Square squareI = (Square)shapes.get(i);
    	double distX = squareI.x - starSol.gravityX;
    	double distY = squareI.y - starSol.gravityY;
    	double distT = Math.sqrt(distX * distX + distY * distY);
    	double theta = Math.atan(distY / distX);
    	double tAccel = starSol.mass * squareI.mass * keplerFreeSquareGravityConstant / (distT * distT);
    	double xAccel = Math.abs(tAccel * Math.cos(theta));
    	double yAccel = Math.abs(tAccel * Math.sin(theta));
    	if(squareI.x > starSol.gravityX)
		{
			squareI.xVector = squareI.xVector - xAccel; //Then accelerate I to the left
		}
		else if(squareI.x < starSol.gravityX) //Otherwise, if I is to the left of J
		{
			squareI.xVector = squareI.xVector + xAccel; //Accelerate I to the right
		}
		if(squareI.y > starSol.gravityY)//If I is above J
		{
			squareI.yVector = squareI.yVector - yAccel; //Then I should accelerate downwards
		}
		else if(squareI.y < starSol.gravityY) //But if I is below J
		{
			squareI.yVector = squareI.yVector + yAccel; //Then I should accelerate upwards
		}
//      But if a square crashes into the sun: DELETE IT!!!
    	if(Math.abs(distX) < sun.getWidth() / 2
    		&& Math.abs(distY) < sun.getHeight() / 2 )
    	{
    		starSol.mass = starSol.mass + squareI.mass;
    		Remover removerI = new Remover(i);
    		removers.add(removerI);
    	}
    }
	
	public double gravityCalcMethodX(double originalXVector)
    {
    	double gravityVectorX = 0;
    	double angle = 0;
    	angle = angleMethodX();
    	gravityVectorX = gravityCalcGeneric(angle, originalXVector);
    	return gravityVectorX;
    }
    
    public void difToggle(int lower, int higher)
    {
    	if(difLevel == lower)
		{
			difLevel = higher;
			magnetEnable = true;
		}
		else if(difLevel == higher)
		{
			difLevel = lower;
			magnetEnable = false;
		}
    }
    
    public void keplerModeToggle()
    {
    	shapes.clear();
    	currentNumSquares = 0;
    	if(keplerMode == 2)
    	{
    		keplerMode = 0;
    	}
    	else if(keplerMode == 1)
    	{
    		keplerMode = 2;
    	}
    	else if(keplerMode == 0)
    	{
    		keplerMode = 1;
    	}
    }
    
    public void clear(boolean shapesClear, boolean magnetClear) //Clears the array lists
    {
    	if(shapesClear == true)
    	{
    		shapes.clear(); //Clear the squares
    		currentNumSquares = 0; //Set the squareCount to zero
    	}
    	if(magnetClear == true)
    	{
    		magnets.clear(); //Clear the magnets
    		currentNumMagnets = 0; //Set the magnetCount to zero
    	}
    }
    
    public double gravityCalcGeneric(double angle, double originalVectorGeneric)
    {
    	double returnVectorGeneric = 0;
    	returnVectorGeneric = originalVectorGeneric + ((gravityConstant/10) * angle);
    	return returnVectorGeneric;
    }
    
    public double angleMethodY()
    {
    	double angle = 0;
        float accelY = game.getInput().getAccelY();
        float accelZ = game.getInput().getAccelZ();

    	if(accelY <= 1 && accelY >= -1)
    	{
    		angle = 0;
    	}
    	else if(accelZ >= -10 && accelZ < -10 + 1)
    	{
    		angle = 0;
    	}
    	else if(accelY > 0 + 1 && accelY <= 2) 
    	{
    		angle = 2;
    	}
    	else if(accelY > 0 + 2 && accelY <= 3) 
    	{
    		angle = 3;
    	}
    	else if(accelY > 0 + 3 && accelY <= 4) 
    	{
    		angle = 4;
    	}
    	else if(accelY > 0 + 4 && accelY <= 5) 
    	{
    		angle = 5;
    	}
    	else if(accelY > 0 + 5 && accelY <= 6) 
    	{
    		angle = 6;
    	}
    	else if(accelY > 0 + 6 && accelY <= 7) 
    	{
    		angle = 7;
    	}
    	else if(accelY > 0 + 7 && accelY <= 8) 
    	{
    		angle = 8;
    	}
    	else if(accelY > 0 + 8 && accelY <= 9) 
    	{
    		angle = 9;
    	}
    	else if(accelY > 0 + 9 && accelY <= 10) 
    	{
    		angle = 10;
    	}
    	else if(accelY >= -2 && accelY < -1)
    	{
    		angle = -2;
    	}
    	else if(accelY >= -3 && accelY < -2)
    	{
    		angle = -3;
    	}
    	else if(accelY >= -4 && accelY < -3)
    	{
    		angle = -4;
    	}
    	else if(accelY >= -5 && accelY < -4)
    	{
    		angle = -5;
    	}
    	else if(accelY >= -6 && accelY < -5)
    	{
    		angle = -6;
    	}
    	else if(accelY >= -7 && accelY < -6)
    	{
    		angle = -7;
    	}
    	else if(accelY >= -8 && accelY < -7)
    	{
    		angle = -8;
    	}
    	else if(accelY >= -9 && accelY < -8)
    	{
    		angle = -9;
    	}
    	else if(accelY >= -10 && accelY < -9)
    	{
    		angle = -10;
    	}

	    return angle;
	    
    }
    
    public double angleMethodX()
    {
        double angleX = 0;
    	double accelX = game.getInput().getAccelX();
        if(accelX < -9 && accelX >= -10)
    	{
    		angleX = 10;
    	}
    	if(accelX < -8 && accelX >= -9)
    	{
    		angleX = 9;
    	}
    	if(accelX < -7 && accelX >= -8)
    	{
    		angleX = 8;
    	}
    	if(accelX < -6 && accelX >= -7)
    	{
    		angleX = 7;
    	}
    	if(accelX < -5 && accelX >= -6)
    	{
    		angleX = 6;
    	}
    	if(accelX < -4 && accelX >= -5)
    	{
    		angleX = 5;
    	}
    	if(accelX < -3 && accelX >= -4)
    	{
    		angleX = 4;
    	}
    	if(accelX < -2 && accelX >= -3)
    	{
    		angleX = 3;
    	}
    	if(accelX < -1 && accelX >= -2)
    	{
    		angleX = 2;
    	}
    	if(accelX <= 1 && accelX >= -1)
    	{
    		angleX = 0;
    	}
    	if(accelX <= 2 && accelX > 1)
    	{
    		angleX = -2;
    	}
    	if(accelX <= 3 && accelX > 2)
    	{
    		angleX = -3;
    	}
    	if(accelX <= 4 && accelX > 3)
    	{
    		angleX = -4;
    	}
    	if(accelX <= 5 && accelX > 4)
    	{
    		angleX = -5;
    	}
    	if(accelX <= 6 && accelX > 5)
    	{
    		angleX = -6;
    	}
    	if(accelX <= 7 && accelX > 6)
    	{
    		angleX = -7;
    	}
    	if(accelX <= 8 && accelX > 7)
    	{
    		angleX = -8;
    	}
    	if(accelX <= 9 && accelX > 8)
    	{
    		angleX = -9;
    	}
    	if(accelX <= 10 && accelX > 9)
    	{
    		angleX = -10;
    	}
    	return angleX;
    }
    
    public boolean touchedShape(int k, int eventX, int eventY) //Checks to see whether a square was pressed
    {
    	Square squareK = (Square)shapes.get(k);
		boolean touchShape = touchShape(squareK.x, squareK.y, image.getWidth(), image.getHeight(), eventX, eventY);
		if(touchShape == true)
		{
			touchTrueAlpha = true;
			errorA = true;
//			shapes.remove(squareK);
		}
		return touchTrueAlpha; //Returns a boolean value
    }
    
    public void developerToggle(int eventX, int eventY) //Decides whether to enable the developer mode
    {
//    	If the bottom left corner is pressed, enable pro1
    	if(eventX <= leeWay
    	&& eventY >= UtilConstants.SCREEN_HEIGHT - leeWay
    	&& pro1 == false)
    	{
    		pro1 = true;
    	}
//    	If the next event is in the top left, enable pro2, else, disable pro1
    	if(eventX <= leeWay
    	&& eventY <= leeWay
    	&& pro1 == true
    	&& pro3 == false)
    	{
    		pro2 = true;
    	}
//    	If the next event is in the bottome left again, enable pro3, else, disable pro2 and pro 1.
    	else if(eventX <= leeWay
    	&& eventY >= UtilConstants.SCREEN_HEIGHT - leeWay
    	&& pro2 == true)
    	{
    		pro3 = true;
    	}
//    	If the last event is in the top left again, enable pro4, and cycle through the developer method
    	else if(eventX <= leeWay
    	&& eventY <= leeWay
    	&& pro3 == true)
    	{
    		pro4 = true;
    	}
    }
    
    public void magnetFreeToggle() //Toggles whether the magnets are locked in vectors, or are free to move
    {
		if(magnetsFree == false)
		{
			magnetsFree = true;
		}
		else if(magnetsFree == true)
		{
			magnetsFree = false;
		}
    }

    /**
     * This method will select a new value for coefficient of restitution to use
     * for future collisions.
     */
    public void elasticityCycle()
    {
        selectedElasticityValue++;
        if(selectedElasticityValue == Elasticity.length )
		{
			selectedElasticityValue = 0;
		}

		for(int k = 0; k < shapes.size(); k++)
		{
			Square squareK = (Square)shapes.get(k);
			squareK.setBounceCoefficient(Elasticity[selectedElasticityValue]);
		}
    }
    
    public void levelToggle() //Toggles the levels through those available (currently 0 and 1 for stable)
    {
		difSet(2, 1); //Sets the difficulty level down to 1 if above 1
		difToggle(0, 1); //Toggles the mode
		timeSet(difLevel); //Sets the timer mode according to the level
    }
    
    public void difSet(int over, int setTo) //Sets the dificulty level value down to those for the stable release toggle
    {
    	if(difLevel >= over) //If the level is over that for the timer, reduce it
    	{
    		difLevel = setTo;
    	}
    }
    
    public void timeSet(int dificulty) //This will set the timer according to the level specified
    {
    	if(difLevel == 1) //If the dificulty level is 1
    	{
    		timerDown = 30; //Reset the timer
    		win = false; //The user hasn't won
    		fail = false; //Or lost, yet.. :)
    		score = 0; //Set the score to 0 so that they can't cheat
    		clear(true, true); //Clear all squares from the screen
    	}
    	if(difLevel == 0)
    	{
    		timerDown = 30;
    		win = false;
    		fail = false;
    		score = 0;
    	}
    }
    
    public void countDown() //This code handles the count down timer
    {
    	if(timerDown > 0
    			&& win == false
    			&& difLevel == 1)
    	{
    	timerDown = timerDown - 0.02;
    	}
    }
    
    public void invert() //This method inverts the color scheme when called
    {
    	if(invert == false)
		{
			invert = true;
		}
		else if(invert == true)
		{
			invert = false;
		}
    }
    
    public void win() //This code decides whether the user has won the game
    {
    	if(score >= MAX_NUM_SQUARES
    	&& fail == false
    	&& difLevel == 1)
    	{
    		win = true;
    	}
    }
    
    public void fail() //This code decides whether the user has lost the game
    {
		if(timerDown <= 0
		&& win == false
		&& difLevel == 1)
		{
			fail = true;
		}
    }
    
    public void developer() //This code works out whether to enable developer mode
    {
        if(pro4 == true)
		{
			developer = true;
		}
    }
    
    public void nightlyToggle()
    {
    	if(nightly == true)
		{
			nightly = false;
		}
		else if(nightly == false)
		{
			nightly = true;
		}
    }
    
    public boolean overlapGeneric(double x, double y, double imageX, double imageY, double targetX, double targetY, double targetImageX, double targetImageY)
    {
    	boolean overlap = false;
    	if(x <= targetX + targetImageX
    			&& targetX <= x + imageX
    			&& y <= targetY + targetImageY
    			&& targetY <= y + imageY)
    	{
    		overlap = true;
    	}
    	return overlap;			
    }
    
    public void updateDraw(int timerDownInt, int timerDownDec)
    {
    	if(invert == false)
        {
    		g.drawPixmap(backdrop, 0, 0);
    		g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
            g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 2 * menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
            g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
            g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
            g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
            if (developer == true)
			{
            	g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 6 * menuHeight, menuWidth, menuHeight, Color.rgb(00, 105, 92));
//            	g.drawText("LEVEL " + difLevel, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, Color.WHITE);
			}
			if(magnetEnable == true && developer == true)
			{
				g.drawText("ACTIVE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
			}
			else if(magnetEnable == false && developer == true)
			{
				g.drawText("OFF", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
			}
//			If in kepler free mode, draw KEPLER, otherwise draw the EARTH mode
			if(keplerMode == 0)
	    	{
	    		g.drawText("EARTH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
	        }
			if(keplerMode == 1)
	    	{
	    		g.drawText("SPACE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
	        }
//	    	If in solar system, draw the sun and write the kepler mdoe also
	    	if(keplerMode == 2)
	    	{
	    		g.drawPixmap(sun, (UtilConstants.SCREEN_WIDTH - sun.getWidth())/2, (UtilConstants.SCREEN_HEIGHT - sun.getHeight())/2);
	    		g.drawText("SOLAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
	        }
	    	
			g.drawText("B WINCH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5, 20, STROKE_WIDTH, Color.WHITE);
            g.drawText("CLEAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
			if(difLevel == 0)
			{
				g.drawText("ENDLESS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.WHITE);
			}
			if(difLevel == 1)
			{
				g.drawText("TIMED", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.WHITE);
			}
			if(difLevel == 2)
			{
				g.drawText("MAGNETS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.WHITE);
			}
			g.drawText("FREEZE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
			if(nightly == true && developer == true)
           	{
	            g.drawRect(UtilConstants.SCREEN_WIDTH - leeWay, 0, leeWay, leeWay, Color.rgb(00, 105, 92));
	            g.drawRect(UtilConstants.SCREEN_WIDTH - 2 * leeWay, 0, leeWay, leeWay, Color.rgb(00, 105, 92));
	            if(magnetEnable == false)
	            {
	            	g.drawRect(UtilConstants.SCREEN_WIDTH - 3 * leeWay, 0, leeWay, leeWay, Color.rgb(00, 105, 92));
	            }
	            if(magnetsFree == false)
	            {
	            	g.drawRect(UtilConstants.SCREEN_WIDTH - 4 * leeWay, 0, leeWay, leeWay, Color.rgb(00, 105, 92));
	            }
	            double angleY = angleMethodY();
	            g.drawText("AngleY: " + angleY, font, 20, 40, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
	            double angleX = angleMethodX();
	            g.drawText("AngleX: " + angleX, font, 20, 60, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
	            g.drawText("Squares: " + currentNumSquares, font, 20, 80, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
	           	g.drawText("v" + versionNumber + " :)", font, 20, UtilConstants.SCREEN_HEIGHT - 30, 40, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	g.drawText("Success: " + errorA, font, 20, 120, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	g.drawText("Score: " + score, font, 20, 140, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	g.drawText("HighScore: " + highScore, font, 20, 160, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	g.drawText("Magnets: " + currentNumMagnets, font, 20, 180, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	g.drawText("LocalCheck: " + localCheckCurrent, font, 20, 200, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	        	g.drawText("Elasticity: " + Elasticity[selectedElasticityValue] * 100 + "%", font, 20, 220, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
	        	if(score >= currentNumSquares + 1)
	           	{
	           		g.drawText("WINNING!", font, 20, 240, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
	           	}
	           	if(win == true)
	           	{            		
	           		g.drawText("You Have Won The Game", font, 20, 260, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
	           	}
				if(fail == true)
				{
					g.drawText("You Lost :(", font, 20, 260, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
				if(keplerMode == 1)
				{
					g.drawText("Keplerian Mode", font, 20, 280, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
           	}
           	if(nightly == false)
           	{
           		if(developer == true)
           		{
           			g.drawRect(UtilConstants.SCREEN_WIDTH - leeWay, 0, leeWay, leeWay, Color.WHITE);
           		}
                g.drawText("" + currentNumSquares, font, 20, 80, 70, STROKE_WIDTH, Color.rgb(00, 105, 92));
    	    	g.drawText("v" + versionNumber + " :)", font, 20, UtilConstants.SCREEN_HEIGHT - 30, 40, STROKE_WIDTH, Color.rgb(47, 79 ,79));
//    	    	g.drawText("Score: " + score, 20, 80, 20, Color.rgb(47, 79 ,79));
//    	    	g.drawText("HighScore: " + highScore, 20, 100, 20, Color.rgb(00, 105, 92));
    	    	g.drawText("[MAX] " + maxNumSquares, font, 20, 110, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
//    	    	g.drawText("Time Left " + timerDownInt + "." + timerDownDec, 20, 120, 20, Color.rgb(00, 105, 92));
				if(score >= currentNumSquares + 1)
    	    	{
    	    		g.drawText("WINNING!", font, 20, 140, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
    	    	}
    	    	if(win == true)
    	    	{
    	    		g.drawText("You Have Won The Game", font, 20, 160, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
    	    	}
				if(fail == true)
				{
					g.drawText("You Lose :(", font, 20, 160, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
           	}
        
        }
        else if (invert == true)
        {
        	if(nightly == true)
        	{
        		g.drawRect(0,0, UtilConstants.SCREEN_WIDTH+1, UtilConstants.SCREEN_HEIGHT+1, Color.rgb(255, 87, 34));
            	g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - menuHeight, menuWidth, menuHeight, Color.WHITE);
                g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 2 * menuHeight, menuWidth, menuHeight, Color.WHITE);
                g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight, menuWidth, menuHeight, Color.WHITE);
                g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 6 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				if(magnetEnable == true)
				{
					g.drawText("ACTIVE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
				else if(magnetEnable == false)
				{
					g.drawText("OFF", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
//				If in kepler free mode, draw KEPLER, otherwise draw the EARTH mode
				if(keplerMode == 0)
		    	{
		    		g.drawText("EARTH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
		        }
				if(keplerMode == 1)
		    	{
		    		g.drawText("SPACE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT  - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.WHITE);
		        }
//		    	If in solar system, draw the sun and write the kepler mdoe also
		    	if(keplerMode == 2)
		    	{
		    		g.drawPixmap(sun, (UtilConstants.SCREEN_WIDTH - sun.getWidth())/2, (UtilConstants.SCREEN_HEIGHT - sun.getHeight())/2);
		    		g.drawText("SOLAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 - 4 * menuHeight, 20, STROKE_WIDTH, Color.WHITE);
		        }
		    	
				g.drawText("B WINCH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 - 4 * menuHeight, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
                g.drawText("CLEAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				if(difLevel == 0)
				{
					g.drawText("ENDLESS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
				if(difLevel == 1)
				{
					g.drawText("TIMED", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
				if(difLevel == 2)
				{
					g.drawText("MAGNETS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
				}
				g.drawText("FREEZE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(255, 87, 34));
//				g.drawText("LEVEL " + difLevel, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, Color.rgb(255, 87, 34));
                g.drawText("NIGHTLY", font, 20, 300, 70, STROKE_WIDTH, Color.WHITE);
        		if(developer == true)
        		{
        			g.drawRect(UtilConstants.SCREEN_WIDTH - leeWay, 0, leeWay, leeWay, Color.WHITE);
        			g.drawRect(UtilConstants.SCREEN_WIDTH - 2 * leeWay, 0, leeWay, leeWay, Color.WHITE);
        		}
        		double angleY = angleMethodY();
	            g.drawText("AngleY: " + angleY, font, 20, 40, 20, STROKE_WIDTH, Color.WHITE);
	            double angleX = angleMethodX();
	            g.drawText("AngleX: " + angleX, font, 20, 60, 20, STROKE_WIDTH, Color.WHITE);
	            g.drawText("Shapes: " + currentNumSquares, font, 20, 80, 20, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("Version: " + versionNumber + " :)", font, 20, UtilConstants.SCREEN_HEIGHT - 30, 40, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("Success: " + errorA, font, 20, 120, 20, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("Score: " + score, font, 20, 140, 20, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("[MAX] " + maxNumSquares, font, 20, 160, 20, STROKE_WIDTH, Color.WHITE);
//    	    	g.drawText("HighScore: " + highScore, 20, 160, 20, Color.WHITE);
	        	g.drawText("Magnets: " + currentNumMagnets, font, 20, 180, 20, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("LocalCheck: " + localCheckCurrent, font, 20, 200, 20, STROKE_WIDTH, Color.WHITE);
	        	g.drawText("Elasticity: " + Elasticity[selectedElasticityValue] * 100 + "%", font, 20, 220, 20, STROKE_WIDTH, Color.WHITE);
				if(score >= currentNumSquares + 1)
	        	{
	        		g.drawText("WINNING!", font, 20, 240, 20, STROKE_WIDTH, Color.WHITE);
	        	}
	        	if(win == true)
	        	{
	        		g.drawText("You Have Won The Game", font, 20, 260, 20, STROKE_WIDTH, Color.WHITE);
	        	}
				if(fail == true)
				{
					g.drawText("You Lose :(", font, 20, 260, 20, STROKE_WIDTH, Color.WHITE);
				}
        	}
        	if(nightly == false)
        	{
        		g.drawRect(0,0, UtilConstants.SCREEN_WIDTH+1, UtilConstants.SCREEN_HEIGHT+1, Color.rgb(00, 105, 92));
            	g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - menuHeight, menuWidth, menuHeight, Color.WHITE);
                g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 2 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight, menuWidth, menuHeight, Color.WHITE);
				g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight, menuWidth, menuHeight, Color.WHITE);
	            if(developer == true)
				{
					g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight, menuWidth, menuHeight, Color.WHITE);
                	g.drawRect(UtilConstants.SCREEN_WIDTH - menuWidth, UtilConstants.SCREEN_HEIGHT - 6 * menuHeight, menuWidth, menuHeight, Color.WHITE);
                }
				if(magnetEnable == true && developer == true)
				{
					g.drawText("ACTIVE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
				else if(magnetEnable == false && developer == true)
				{
					g.drawText("OFF", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
//				If in kepler free mode, draw KEPLER, otherwise draw the EARTH mode
				if(keplerMode == 0)
		    	{
		    		g.drawText("EARTH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
		        }
				if(keplerMode == 1)
		    	{
		    		g.drawText("SPACE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT  - 4 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
		        }
//		    	If in solar system, draw the sun and write the kepler mdoe also
		    	if(keplerMode == 2)
		    	{
		    		g.drawPixmap(sun, (UtilConstants.SCREEN_WIDTH - sun.getWidth())/2, (UtilConstants.SCREEN_HEIGHT - sun.getHeight())/2);
		    		g.drawText("SOLAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5 - 4 * menuHeight, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
		        }
				g.drawText("B WINCH", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
                g.drawText("CLEAR", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
                g.drawText("FREEZE", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 3 * menuHeight - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				if(difLevel == 0)
				{
					g.drawText("ENDLESS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
				if(difLevel == 1)
				{
					g.drawText("TIMED", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
				if(difLevel == 2)
				{
					g.drawText("MAGNETS", font, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - menuHeight * 2 - 5, 20, STROKE_WIDTH, Color.rgb(00, 105, 92));
				}
//				g.drawText("LEVEL " + difLevel, UtilConstants.SCREEN_WIDTH - menuWidth + 5, UtilConstants.SCREEN_HEIGHT - 4 * menuHeight - 5, 20, Color.rgb(00, 105, 92));
                if(developer == true)
        		{
        			g.drawRect(UtilConstants.SCREEN_WIDTH - leeWay, 0, leeWay, leeWay, Color.WHITE);
        		}
                g.drawText("" + currentNumSquares, font, 20, 80, 70, STROKE_WIDTH, Color.WHITE);
            	g.drawText("v" + versionNumber + " :)", font, 20, UtilConstants.SCREEN_HEIGHT - 30, 40, STROKE_WIDTH, Color.WHITE);
//            	g.drawText("Score: " + score, 20, 80, 20, Color.WHITE);
            	g.drawText("[MAX] " + maxNumSquares, font, 20, 110, 20, STROKE_WIDTH, Color.WHITE);
//    	    	g.drawText("HighScore " + highScore, 20, 100, 20, Color.WHITE);
            	g.drawText("SQUARE", font, 20, 280, 70, STROKE_WIDTH, Color.WHITE);
//            	g.drawText("Time Left " + timerDownInt + "." + timerDownDec, 20, 120, 20, Color.WHITE);
				if(score >= currentNumSquares + 1)
            	{
            		g.drawText("WINNING!", font, 20, 140, 20, STROKE_WIDTH, Color.WHITE);
            	}
            	if(win == true)
            	{
            		g.drawText("You Have Won The Game", font, 20, 160, 20, STROKE_WIDTH, Color.WHITE);
            	}
				if(fail == true)
				{
					g.drawText("You Lose :(", font, 20, 160, 20, STROKE_WIDTH, Color.WHITE);
				}
        	}
        }
    	updateDrawShapes();
    }
	
    public void updateDrawShapes()
    {
    	for (int b=0;b<currentNumSquares;b++) 
        {
        	Square squareB = (Square)shapes.get(b);
        	squareB.drawSquare(g);
        }
        for (int c=0; c<currentNumMagnets; c++)
        {
        	if(magnetEnable == true)
        	{
		        Magnet magnetC = (Magnet)magnets.get(c);
		        if(nightly == true && invert == false)
		        {
		        	g.drawRect((int)magnetC.x, (int)magnetC.y, magnetC.magnetImage.getWidth(), magnetC.magnetImage.getHeight(), Color.rgb(255, 87, 34));
		        }
		        else if(nightly == false && invert == false)
		        {
		        	g.drawRect((int)magnetC.x, (int)magnetC.y, magnetC.magnetImage.getWidth(), magnetC.magnetImage.getHeight(), Color.rgb(00, 105, 92));
		        }
		        else if(invert == true)
		        {
		        	g.drawRect((int)magnetC.x, (int)magnetC.y, magnetC.magnetImage.getWidth(), magnetC.magnetImage.getHeight(), Color.WHITE);
		        }
        	}
        }
    }

    public void setupDefaultElasticity()
    {
        Elasticity[0] = e;
        Elasticity[1] = 1.3;
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
