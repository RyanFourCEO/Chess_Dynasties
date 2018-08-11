package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;

//the mainMenu class, creates a stage and makes buttons for whatever state the game is in
public class Menu {
    //what gets drawn to the screen, it holds all UI elements, just buttons for now
    Stage stage;
    //array of buttons
    ArrayList<TextButton> allButtons=new ArrayList<TextButton>();
    //array of textfields
    ArrayList<TextArea> allTextAreas =new ArrayList<TextArea>();

    Skin skin;

    public Menu(InputMultiplexer inputMultiplexer){

        //initialise the stage
        stage=new Stage(new ScreenViewport());
        //the stage uses the default button skins for now
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        //add the stage to the input multiplexer, this allows the stage to receive mouse input
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);

    }




    public void addTextArea(int width,int height, int positionx,int positiony){
        allTextAreas.add(new TextArea("",skin,"default"));
        allTextAreas.get(allTextAreas.size()-1).setPosition(positionx,positiony);
        allTextAreas.get(allTextAreas.size()-1).setSize(width,height);
        stage.addActor(allTextAreas.get(allTextAreas.size()-1));
    }

    public void addTextArea(String text, int width,int height, int positionx,int positiony,int index){
        allTextAreas.add(new TextArea(text,skin,"default"));
        allTextAreas.get(allTextAreas.size()-1).setPosition(positionx,positiony);
        allTextAreas.get(allTextAreas.size()-1).setSize(width,height);
        stage.addActor(allTextAreas.get(allTextAreas.size()-1));
    }
    //add a button to the mainMenu with the following variables deciding all it's factors
//buttonText is the text the button displays
//height and width are the size of the button
//positionx and y are the positions of the button on the stage, and thus on the screen
//buttonIndex is the index in the array of clickListeners, this variable assigns the button
//to execute the code contained in the clickListener
    public void addButton(String buttonText,int width, int height, int positionx, int positiony,ClickListener clickListener){


        //"reset board" button initialized with the selected skin
        allButtons.add(new TextButton(buttonText, skin, "default"));
        //set buttons size and position on screen
        allButtons.get(allButtons.size()-1).setWidth(width);
        allButtons.get(allButtons.size()-1).setHeight(height);
        allButtons.get(allButtons.size()-1).setPosition(positionx, positiony);
        allButtons.get(allButtons.size()-1).addListener(clickListener);
        //add the button to the stage so it can be drawn to the screen
        stage.addActor(allButtons.get(allButtons.size()-1));

    }

    //make all UI components invisible and incapable of processing input
    void disable(){
        for(int x=0;x!=allButtons.size();x++){
            allButtons.get(x).setVisible(false);
        }
        for(int x=0;x!=allTextAreas.size();x++){
            allTextAreas.get(x).setVisible(false);
        }
    }
    //make all UI components visible and capable of processing input
    void enable(){
        for(int x=0;x!=allButtons.size();x++){
            allButtons.get(x).setVisible(true);
        }
        for(int x=0;x!=allTextAreas.size();x++){
            allTextAreas.get(x).setVisible(true);
        }
    }



    void dispose(){
        skin.dispose();
        stage.dispose();
    }
}
