package ceov2.org;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

//this class creates the gamestate object(which actually holds the state of the game)
//and also creates the menu for the gamestate object
public class LiveGame {
    GameState state;
    GameState sim;
    int[] squareMouseIsHoveredOver = {-1,-1};
    int indexOfSelectedPiece;
    ArrayList<ArrayList<Integer>> listOfThingsToDraw = new ArrayList<ArrayList<Integer>>();


    Menu menu;
    boolean gameOver = false;
    boolean multiplayerGame = false;
    //holds the step we are currently in for calculating and displaying move previews
    //this allows the logic for the whole process to be separated to different steps,
    //each step occurring during it's own tick. Whenever a step is completed this is increased by 1
    //so the next step will be executed on the following tick.
    int stepCounterForMoveDisplayPreviews = 0;

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
        detectIfPieceSelected();
        detectIfMousePosChanged(mouseVars);
        detectAndDisplayMovePreviews(mouseVars);

        //if user has done something and the projected move has changed, set loopnumber back to 0

    }
    void detectIfPieceSelected(){
        if (state.pieceSelected == true){
            indexOfSelectedPiece = state.selectedPiece;
        }
    }
    void detectIfMousePosChanged(MouseVars mouseVars){
        if (state.pieceSelected == true){
            int[] mousePosOnBoard = state.findSquareMouseIsOn(mouseVars.mousePosx,mouseVars.mousePosy);
            if (mousePosOnBoard[0] != squareMouseIsHoveredOver[0] || mousePosOnBoard[1] != squareMouseIsHoveredOver[1]){
                stepCounterForMoveDisplayPreviews = 0;
                squareMouseIsHoveredOver[0] = mousePosOnBoard[0];
                squareMouseIsHoveredOver[1] = mousePosOnBoard[1];
            }
        }else{
            //if a piece is not selected, return "stepCounterForMoveDisplayPreviews" to 0, and set the square the mouse
            //is on to -1. This prevents the "detectAndDisplayMovePreviews" method does not unnecessarily execute
            //and that once the next "detectAndDisplayMovePreviews" call occurs, the "stepCounterForMoveDisplayPreviews"
            //will be at 0
            stepCounterForMoveDisplayPreviews = 0;
            squareMouseIsHoveredOver[0] = -1;
            squareMouseIsHoveredOver[1] = -1;
        }
    }
    void detectAndDisplayMovePreviews(MouseVars mouseVars){
        long startTime = System.currentTimeMillis();
        if(squareMouseIsHoveredOver[0] != -1 && squareMouseIsHoveredOver[1] != -1) {
            switch (stepCounterForMoveDisplayPreviews) {
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
                    sim.executeArrayOfMoves(state.allMovesMade);
                    System.out.println("part 4 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
                case 4:
                    sim.projectHoveredMove(mouseVars,indexOfSelectedPiece);
                    System.out.println("part 5 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
                case 5:
                    listOfThingsToDraw = findDifference(state, sim);
                    System.out.println("part 6 Time  = " + ((System.currentTimeMillis() - startTime))+"ms");
                    break;
            }
            if (stepCounterForMoveDisplayPreviews != 6) {
                stepCounterForMoveDisplayPreviews++;
            } else {
                state.drawDifference(state,sim,listOfThingsToDraw);
            }
        }

    }
    ArrayList<ArrayList<Integer>> findDifference(GameState main, GameState sim) {
        //return thing
        ArrayList<ArrayList<Integer>> toDraw = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> a = new ArrayList<Integer>();
        //toDraw Morale Change from move
        //index of array to enter
        int color = main.colourOfUser;
        int notColor;
        if (color == 1) {
            notColor = 0;
        } else {
            notColor = 1;
        }
        int mDiff = main.moraleTotals[color] - sim.moraleTotals[color];
        //changetype
        a.add(0);
        a.add(main.moraleTotals[color]);
        a.add(sim.moraleTotals[color]);
        a.add(mDiff);
        a.clear();

        //check if loss
        if (sim.moraleTotals[color] <= 0 && sim.moraleTotals[notColor] <= 0) {
            a.add(2);
        } else if (sim.moraleTotals[color] <= 0) {
            a.add(1);
        } else {
            a.add(0);
        }
        toDraw.add(a);
        int pieceX, pieceY, simX, simY, xDiff, yDiff;
        //find piece location change
        for (int i = 0; i <= state.allPiecesOnBoard.size() - 1; i++) {
            simX = sim.allPiecesOnBoard.get(i).xLocation;
            simY = sim.allPiecesOnBoard.get(i).yLocation;
            pieceX = main.allPiecesOnBoard.get(i).xLocation;
            pieceY = main.allPiecesOnBoard.get(i).yLocation;
            xDiff = simX - pieceX;
            yDiff = simY - pieceY;
            //draw movement
            if (xDiff != 0 && yDiff != 0) {
                a.add(1);
                a.add(pieceX);
                a.add(pieceY);
                a.add(simX);
                a.add(simY);
                a.add(xDiff);
                a.add(yDiff);
                //movetypeofMove
                a.add(state.allPiecesOnBoard.get(i).moveset[xDiff + 7][yDiff + 7]);
            }
            //draw deaths
            if (state.allPiecesOnBoard.get(i).captured) {
                a.add(2);
                a.add(simX);
                a.add(simY);
                toDraw.add(a);
            }
        }
        //get new pieces created, draw them
        //TODO make it so that pieces have indicators where they are summoned instead of where they end up
        for (int i = main.allPiecesOnBoard.size(); i <= sim.allPiecesOnBoard.size(); i++) {
            //get created pieces
        }
        return toDraw;
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
