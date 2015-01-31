package graphics.demo.game;

/* Name: Hexaboxes.java
 * Date: 11/01/2015
 */

import graphics.demo.framework.Game;
import graphics.demo.framework.Graphics;
import graphics.demo.framework.Input.TouchEvent;
import graphics.demo.framework.Pixmap;
import graphics.demo.framework.Screen;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.Typeface;

import static android.provider.Settings.Global.getString;


public class HexaboxesMain extends Screen {

    /* Variable declaration */
    private static String versionNumber = "0.0.3.0"; //The current app version number

    //	This generates a new ArrayList for the values of the square objects
    private ArrayList<Hexabox> hexaboxes = new ArrayList<Hexabox>(); //An array list containing the hexaboxes
    private ArrayList<Line> lines = new ArrayList<Line>();
    private ArrayList<User> users = new ArrayList<User>();

    private Pixmap background; //The image of the background
    private Graphics g = game.getGraphics(); //Import the graphics
    private Typeface font = Typeface.createFromAsset(game.getAssets(), "fonts/roboto-light.ttf"); //The font face is taken

    private int mode = 0;
    private int initialIteration = 0;
    private int userToMove;

    /*
     * Class declaration, honouring screen extension
     */
    public HexaboxesMain(Game game)
    {
        super(game);
    }

//	The intro part only takes a couple of seconds and isn't skippable

    /*
     * The update method is overrided, and a a switch is used to determine the outcome
     * If 0, then the game has just begun, and so draw the animation etc.
     * If 1, then the menu needs to be drawn
     * If 2, then wait for a touchUp event, and when found, use it to determine the number of users
     * When 3 or 4, the game is playing, and the lines coordinates are used
     */
    @Override
    public void update(float deltaTime)
    {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        switch (mode)
        {
            case 0 :
                drawInitial();
                break;
            case 1 :
                drawMenu('U');
                mode = 2;
                break;
            case 2 :
                if (findTouchUpBoolean(touchEvents))
                {
                    TouchEvent touchUpEvent = findTouchUpEvent(touchEvents);
                    int menuResult = getUserMenu (touchUpEvent);
                    if(menuResult != 0)
                    {
                        createUsers (menuResult);
                        mode = 3;
                    }
                }
                break;
            case 3 :
                if(findTouchDownBoolean(touchEvents))
                {
                    User userCurrentDownMove = users.get(userToMove);
                    TouchEvent touchDownEventMove = findTouchDownEvent(touchEvents);
                    addLineDown (touchDownEventMove, userCurrentDownMove);
                    mode = 4;
                }
                break;
            case 4 :
                if(findTouchUpBoolean(touchEvents))
                {
                    TouchEvent touchUpEventMove = findTouchUpEvent(touchEvents);
                    addLineUp (touchUpEventMove);
                    userToMove ++;
                    if(userToMove == users.size())
                    {
                        userToMove = 0;
                    }
                    mode = 3;
                }
                else if(findTouchDragBoolean(touchEvents))
                {
                    User userCurrentDragMove = users.get(userToMove);
                    TouchEvent touchDragEventMove = findTouchDragEvent(touchEvents);
                    addLineDrag (touchDragEventMove, userCurrentDragMove);
                }
                break;

        }
        updateDraw();
    }

    /*
     * This method will draw a startup animation etc
     * When the startup is finished, move onto the menu
     */
    public void drawInitial()
    {
        g.drawRect (0, 0, UtilConstants.SCREEN_WIDTH, UtilConstants.SCREEN_HEIGHT, Color.rgb(47, 79, 79));
        initialIteration++;
        if(initialIteration > 0)
        {
            mode = 1;
        }
    }

    /*
     * This method will draw varying user menus, with a switch to determine which is to be drawn
     */
    private void drawMenu(char menuMode)
    {
        int width = UtilConstants.SCREEN_WIDTH;
        int height = UtilConstants.SCREEN_HEIGHT;
        switch (menuMode)
        {
            case 'U' :
                drawUserMenu(width, height);
        }
    }

    /*
     * This method will draw a user chooser dialogue when called
     */
    private void drawUserMenu(int width, int height)
    {
        g.drawRect(0, 0, width, height, Color.rgb(224, 224, 224));
        g.drawText("1 PLAYER", 20, height / 6, 65, Color.rgb(30, 136, 229), font, 10);
        g.drawText("2 PLAYERS", 20, 2 * height / 6, 65, Color.rgb(224,81,30), font, 10);
        g.drawText("3 PLAYERS", 20, 3 * height / 6, 65, Color.rgb(67, 160 ,71), font, 10);
        g.drawText("4 PLAYERS", 20, 4 * height / 6, 65, Color.rgb(69, 90, 100), font, 10);
        g.drawText("5 PLAYERS", 20, 5 * height / 6, 65, Color.rgb(211, 47, 47), font, 5 );
        g.drawText("6 PLAYERS", 20, 6 * height / 6, 65, Color.rgb(94, 53, 177), font, 5 );
    }

    /*
     * This method will search for touchUpEvents, and return a true value if one is found
     */
    private boolean findTouchUpBoolean (List touchEvents)
    {
        boolean touchUpFound = false;
        for (int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent) touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_UP)
            {
                touchUpFound = true;
            }
        }
        return touchUpFound;
    }

    /*
     * In order to avoid out of bounds errors, the boolean method will confirm if touchUpEvents exist,
     * While this method will then be called, and will find touchUpEvents from the list, and return them
     * It will always return the most RECENT touchUpEvent
     */
    private TouchEvent findTouchUpEvent(List touchEvents)
    {
        TouchEvent touchEventReturn = new TouchEvent ();
        for (int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent) touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_UP)
            {
                touchEventReturn = touchEventCurrent;
            }
        }
        return touchEventReturn;
    }

    /*
     * This method will take a TouchEvent, and determine which user was chosen
     * It will then return the chosen number of users
     */
    private int getUserMenu (TouchEvent touchUpEvent) {
        int width = UtilConstants.SCREEN_WIDTH;
        int height = UtilConstants.SCREEN_HEIGHT;
        int nUsers = 0;
        if (touchUpEvent.y < height / 6)
        {
            nUsers = 1;
        }
        else if (touchUpEvent.y >= height / 6
                && touchUpEvent.y < 2 * height / 6)
        {
            nUsers = 2;
        }
        else if (touchUpEvent.y >= 2 * height / 6
                && touchUpEvent.y < 3 * height / 6)
        {
            nUsers = 3;
        }
        else if (touchUpEvent.y >= 3 * height / 6
                && touchUpEvent.y < 4 * height / 6)
        {
            nUsers = 4;
        }
        else if (touchUpEvent.y >= 4 * height / 6
                && touchUpEvent.y < 5 * height / 6)
        {
            nUsers = 5;
        }
        else if(touchUpEvent.y >= 5 * height / 6
                && touchUpEvent.y <= height)
        {
            nUsers = 6;
        }
        return nUsers;
    }

    /*
     * This method will create a number of users, equal to that inputted, and add them to the arrayList.
     * It will also make sure to clear the arrayList before hand
     */
    private void createUsers (int nUsers)
    {
        users.clear();
        switch(nUsers)
        {
            case 6 :
                User user6 = new User(94, 53, 177, true, "USER" + 6);
                users.add(user6);
            case 5 :
                User user5 = new User(211, 47, 47, true, "USER" + 5);
                users.add(0, user5);
            case 4 :
                User user4 = new User(69, 90, 100, true, "USER" + 4);
                users.add(0, user4);
            case 3 :
                User user3 = new User(67, 160, 71, true, "USER" + 3);
                users.add(0, user3);
            case 2 :
                User user2 = new User(224, 81, 30, true, "USER" + 2);
                users.add(0, user2);
            case 1 :
                User user1 = new User(30, 136, 229, true, "USER" + 1);
                users.add(0, user1);
                users.get(0);
                break;
        }
        userToMove = 0;
    }

    /*
     * This method will search for touchDownEvents, and return a true value if one is found
     * It's basically the same as the touchUpBoolean method :)
     */
    private boolean findTouchDownBoolean (List touchEvents)
    {
        boolean touchDownFound = false;
        for (int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent) touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_DOWN)
            {
                touchDownFound = true;
            }
        }
        return touchDownFound;
    }

    /*
     * The exact same code as for finding a touchUpEvent,
     * But it will return a touchDown event instead :D
     */
    private TouchEvent findTouchDownEvent(List touchEvents)
    {
        TouchEvent touchEventReturn = new TouchEvent ();
        for (int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent) touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_DOWN)
            {
                touchEventReturn = touchEventCurrent;
                break;
            }
        }
        return touchEventReturn;
    }

    /*
     * This method will create a line (of no length),
     * From just the touchDownCo-ordinates :)
     */
    private void addLineDown(TouchEvent touchDownEvent, User userCurrent)
    {
        Line lineToAdd = new Line(touchDownEvent.x, touchDownEvent.y, userCurrent.red, userCurrent.green, userCurrent.blue);
        lines.add(lineToAdd);
    }

    /*
     * This method will look to see if there are any touchDrag events,
     * And return a true value if there are
     */
    private boolean findTouchDragBoolean(List touchEvents)
    {
        boolean touchDragEventFound = false;
        for(int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent)touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_DRAGGED)
            {
                touchDragEventFound = true;
                break;
            }
        }
        return touchDragEventFound;
    }

    /*
     * This method will find and return the last touchDrag event from the List
     */
    private TouchEvent findTouchDragEvent(List touchEvents)
    {
        TouchEvent touchEventReturn = new TouchEvent();
        for(int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent touchEventCurrent = (TouchEvent)touchEvents.get(i);
            if(touchEventCurrent.type == touchEventCurrent.TOUCH_DRAGGED)
            {
                touchEventReturn = touchEventCurrent;
                break;
            }
        }
        return touchEventReturn;
    }

    /*
     * For drag events, this method will be called.
     * It will alter the end coordinates of the line to where the user has dragged to
     */
    private void addLineDrag(TouchEvent touchDragEvent, User currentUser)
    {
        Line lineCurrent = lines.get(lines.size() - 1);
        lineCurrent.endX = touchDragEvent.x;
        lineCurrent.endY = touchDragEvent.y;
        lineCurrent.red = currentUser.red;
        lineCurrent.green = currentUser.green;
        lineCurrent.blue = currentUser.blue;
    }

    /*
     * Once a User has released their finger from the screen,
     * The coordinates of the touchUp event will be used as the end point of the line :)
     */
    private void addLineUp(TouchEvent touchUpEvent)
    {
        Line lineToAlter = lines.get(lines.size() - 1);
        lineToAlter.endX = touchUpEvent.x;
        lineToAlter.endY = touchUpEvent.y;
    }

    /*
     * Depending on the mode switch, this method will draw lines and hexaboxes to the screen
     */
    private void updateDraw ()
    {
        int width = UtilConstants.SCREEN_WIDTH;
        int height = UtilConstants.SCREEN_HEIGHT;
        switch(mode)
        {
            case 3 :
                g.drawRect(0, 0, width, height, Color.rgb(224,224,224));
                for(int i = 0; i < lines.size(); i ++)
                {
                    Line lineToDraw = lines.get(i);
                    g.drawLine(lineToDraw.startX, lineToDraw.startY, lineToDraw.endX, lineToDraw.endY, Color.rgb(lineToDraw.red, lineToDraw.green, lineToDraw.blue));
                }
            case 4 :
                g.drawRect(0, 0, width, height, Color.rgb(224,224,224));
                for(int i = 0; i < lines.size(); i ++)
                {
                    Line lineToDraw = lines.get(i);
                    g.drawLine(lineToDraw.startX, lineToDraw.startY, lineToDraw.endX, lineToDraw.endY, Color.rgb(lineToDraw.red, lineToDraw.green, lineToDraw.blue));
                }
        }
    }

    public void present(float deltaTime) {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void dispose() {

    }

}
