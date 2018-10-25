package ceov2.org;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

//this class creates the gamestate object(which actually holds the state of the game)
//and also creates the menu for the gamestate object
public class LiveGame {
    GameState state;
    GameState sim;
    int[] squareMouseIsHoveredOver = {-1,-1};


    Menu menu;
    boolean gameOver = false;
    boolean multiplayerGame = false;
    int loopNumber = 0;

    public LiveGame(InputMultiplexer inputMultiplexer) {
        state = new GameState();
        loadGameMenu(inputMultiplexer);
    }

    //colour =1 means the user is white, colour =2 means the user is black
    public LiveGame(InputMultiplexer inputMultiplexer, int colour, String army, String oppArmy, ServerCommunications serverComms) {
        state = new GameState(colour, army, oppArmy, serverComms);
        loadGameMenu(inputMultiplexer);
        multiplayerGame = true;
    }

    void performGameLogic(SpriteBatch batch, MouseVars mouseVars) {
        updateMenuObjects();
        menu.stage.getViewport().apply();
        menu.stage.draw();
        if (multiplayerGame) {
            state.runMultiplayerGame(batch, mouseVars);
        } else {
            state.runGame(batch, mouseVars);
        }
        detectIfMousePosChanged(mouseVars);
        detectAndDisplayMovePreviews(mouseVars);

        //if user has done something and the projected move has changed, set loopnumber back to 0

    }

    void detectIfMousePosChanged(MouseVars mouseVars){
        if (state.pieceSelected == true){
            int[] mousePosOnBoard = state.findSquareMouseIsOn(mouseVars.mousePosx,mouseVars.mousePosy);

            if (mousePosOnBoard[0] != squareMouseIsHoveredOver[0] || mousePosOnBoard[1] != squareMouseIsHoveredOver[1]){
                loopNumber = 0;
                squareMouseIsHoveredOver[0] = mousePosOnBoard[0];
                squareMouseIsHoveredOver[1] = mousePosOnBoard[1];
            }
        }
    }
    void detectAndDisplayMovePreviews(MouseVars mouseVars){
        long startTime = System.currentTimeMillis();

        if(squareMouseIsHoveredOver[0] != -1 && squareMouseIsHoveredOver[1] != -1) {
            switch (loopNumber) {
                case 0:
                    sim = new GameState(true);
                    System.out.println("part 1 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
                case 1:
                    sim.loadArmiesNoGraphics();
                    System.out.println("part 2 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
                case 2:
                    sim.setBoard();
                    System.out.println("part 3 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
                case 3:
                    System.out.println("part 4 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    sim.executeArrayOfMoves(state.allMovesMade);
                    break;
                case 4:
                    sim.projectHoveredMove(mouseVars);
                    break;
            }
            if (loopNumber != 5) {
                loopNumber++;
            } else {
                state.drawDifference(state, sim);
            }
        }

    }


    void unselectAll() {
        state.unselectAll();
    }

    void loadGameMenu(InputMultiplexer inputMultiplexer) {
        menu = new Menu(inputMultiplexer);

        //return to main menu button
        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameOver = true;
            }
        };
        menu.addButton("Return to Main Menu", 200, 30, 100, 100, clickListener);

        //flip board button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state.flipBoard = !state.flipBoard;
            }
        };
        menu.addButton("Flip Board", 200, 30, 100, 20, clickListener);

        //create two text areas, while in loop we will update these to have helpful information about each piece on them
        menu.addTextArea(300, 100, 0, 250);
        menu.addTextArea(300, 100, 0, 140);
    }

    void updateMenuObjects() {
        updateTextAreas();
    }

    void updateTextAreas() {
        menu.allTextAreas.get(0).setText(state.getCurrentlySelectedPieceName() + "\n" + state.getCurrentlySelectedPieceDescription());
        menu.allTextAreas.get(1).setText(state.getCurrentlySelectedPieceLore());
    }

    void reloadGraphics() {
        state.reloadGraphics();
    }

    void deleteGraphics() {
        state.deleteGraphics();
        menu.dispose();
    }
}
