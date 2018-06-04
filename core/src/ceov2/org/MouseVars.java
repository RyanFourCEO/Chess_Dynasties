package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

//contains the location of the mouse and what actions the mouse is performing
//finds the new mouse variables every time the main loop executes
public class MouseVars {
    int mousePosx;
    int mousePosy;
    boolean mouseClicked;
    boolean mouseReleased;
    public MouseVars(){

    }

    public void setMouseVariables(){
        //set the mouse cursor's position
        mousePosx=Gdx.input.getX();
        //mouse coordinate's 0,0 is at the top left of the window, this sets the position to be relative to the bottom left
        mousePosy=618-Gdx.input.getY();
        //if the mouse button is pressed down

        if (mouseReleased==true){
            mouseReleased=false;
        }
        if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)==true) {
            //set mouseClicked=true
            mouseClicked=true;
            mouseReleased=false;
        }else{
            //if the mouse was clicked, and the mouse button is no longer pressed, then mouseReleased=true
            if (mouseClicked==true) {
                mouseClicked = false;
                mouseReleased = true;
            }
        }
    }
}
