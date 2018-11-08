package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

//contains the location of the mouse and what actions the mouse is performing
//finds the new mouse variables every time the main loop executes
public class MouseVars {
    int mousePosx;
    int mousePosy;
    int realMousePosx;
    int realMousePosy;
    boolean mouseClicked;
    boolean mouseReleased;

    public MouseVars() {

    }

    public void setMouseVariables(int baseScreenHeight, int baseScreenWidth, int screenHeight, int screenWidth) {

        //find the mouse cursor's position (these values are used for doing things at the location of the mouse cursor
        //for example drawing something)
        realMousePosx = Gdx.input.getX();
        //mouse coordinate's 0,0 is at the top left of the window, this sets the position to be relative to the bottom left
        realMousePosy = baseScreenHeight - Gdx.input.getY();

        //find the position of the mouse on screen as though the screen is always baseScreenWidth by baseScreenHeight
        //this keeps the values of the mouse's position consistent for when resizing occurs
        //these values are used for detecting what actors the mouse is interacting with
        mousePosx = (int) (Gdx.input.getX() / (screenWidth / (double) baseScreenWidth));
        //mouse coordinate's 0,0 is at the top left of the window, this sets the position to be relative to the bottom left
        mousePosy = (int) (baseScreenHeight - Gdx.input.getY() / (screenHeight / (double) baseScreenHeight));

        //if the mouse button is pressed down
        if (mouseReleased == true) {
            mouseReleased = false;
        }
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) == true) {
            //set mouseClicked=true
            mouseClicked = true;
            mouseReleased = false;
        } else {
            //if the mouse was clicked, and the mouse button is no longer pressed, then mouseReleased=true
            if (mouseClicked == true) {
                mouseClicked = false;
                mouseReleased = true;
            }
        }
    }

    void unSetMouseVariables() {
        mouseClicked = false;
        mouseReleased = false;
        mousePosy = 0;
        mousePosx = 0;
        realMousePosy = 0;
        realMousePosx = 0;
    }

    //convenience method for testing
    void printMouseLoc() {
        System.out.println("x " + mousePosx);
        System.out.println("y " + mousePosy);
    }
}
