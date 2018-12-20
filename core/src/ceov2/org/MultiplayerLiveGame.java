package ceov2.org;

import com.badlogic.gdx.InputMultiplexer;

public class MultiplayerLiveGame extends LiveGame {


    public MultiplayerLiveGame(InputMultiplexer inputMultiplexer, int colour, String army, String oppArmy, ServerCommunications serverComms) {
        state = new MultiplayerGameState(colour, army, oppArmy, serverComms);
        loadGameMenu(inputMultiplexer);
    }

}
