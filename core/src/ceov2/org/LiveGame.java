package ceov2.org;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import sun.util.cldr.CLDRLocaleDataMetaInfo;

//this class creates the gamestate object(which actually holds the state of the game)
//and also creates the menu for the gamestate object
public class LiveGame {
GameState state;
Menu menu;
boolean gameOver=false;
boolean multiplayerGame=false;
    public LiveGame(InputMultiplexer inputMultiplexer){
state=new GameState();
loadGameMenu(inputMultiplexer);
}
//colour =1 means the user is white, colour =2 means the user is black
public LiveGame(InputMultiplexer inputMultiplexer,int colour,String army,String oppArmy,ServerCommunications serverComms){
    state=new GameState(colour,army,oppArmy,serverComms);
    loadGameMenu(inputMultiplexer);
    multiplayerGame=true;
}

    void performGameLogic(SpriteBatch batch, MouseVars mouseVars){
        updateMenuObjects();
    menu.stage.getViewport().apply();
    menu.stage.draw();
    if (multiplayerGame==true){
        state.runMultiplayerGame(batch, mouseVars);
    }else {
        state.runGame(batch, mouseVars);
    }
}

    void unselectAll(){
    state.unselectAll();
    }

    void loadGameMenu(InputMultiplexer inputMultiplexer){
menu=new Menu(inputMultiplexer);



        //return to main menu button
        ClickListener clickListener=new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
gameOver=true;
            }
        };
        menu.addButton("Return to Main Menu",200,30,100,100,clickListener);




        //flip board button
        clickListener=new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                    state.flipBoard=!state.flipBoard;

            }
        };
        menu.addButton("Flip Board",200,30,100,20,clickListener);


        //create two text areas, while in loop we will update these to have helpful information about each piece on them
        menu.addTextArea(300,100,0,250);
		menu.addTextArea(300,100,0,140);


    }

    void updateMenuObjects(){
        updateTextAreas();
    }

    void updateTextAreas(){
        menu.allTextAreas.get(0).setText(state.getCurrentlySelectedPieceName()+"\n"+state.getCurrentlySelectedPieceDescription());
        menu.allTextAreas.get(1).setText(state.getCurrentlySelectedPieceLore());
    }

    void reloadGraphics(){
        state.reloadGraphics();
    }
    void deleteGraphics(){
    state.deleteGraphics();
    menu.dispose();

    }
}
