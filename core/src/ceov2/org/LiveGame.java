package ceov2.org;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

//this class currently does nothing, in the future this may control a chat box between players or something
public class LiveGame {
GameState state;

public LiveGame(){
state=new GameState();
}
    void performGameLogic(SpriteBatch batch,MouseVars mouseVars){
    state.runGame(batch,mouseVars);
}

}
