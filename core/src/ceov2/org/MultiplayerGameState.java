package ceov2.org;


import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MultiplayerGameState extends GameState {
ServerCommunications serverComms;

    public MultiplayerGameState(int colour,String army, String oppArmy, ServerCommunications serverComms){
        super(true);
        yourArmy = army;
        this.oppArmy = oppArmy;
        this.serverComms = serverComms;
        //set the colour of the user
        colourOfUser = colour;
        initFont();
        Piece.InitAllMoveTypes();
        loadArmies(colour, army, oppArmy);
        //if the player is not player one, the board is flipped so their pieces are still located on the
        //bottom of the board
        if (colour != 1) {
            flipBoard = !flipBoard;
        }
        loadGraphics();
        setBoard();
        turnJustStarted = true;
        testAndExecuteAbilities();
        turnJustStarted = false;
        updatePieceMoveSets();
        findAllValidMoves();
    }

    @Override
    void runGame(SpriteBatch batch, MouseVars mouseVars) {
            //see if the player has clicked on/selected a piece
            processMouseInputClick(mouseVars);
            //If it is the user's turn, see if the player has released the mouse while selecting a piece
            //if so, the piece may be moved.
            if (playerTurn == colourOfUser) {
                processMouseInputRelease(mouseVars);
            } else {
                //make sure no pieces are currently selected
                if (mouseVars.mouseReleased) {
                    if (piecesOnBoard[selectedPieceLocx][selectedPieceLocy] != -1) {
                        allPiecesOnBoard.get(piecesOnBoard[selectedPieceLocx][selectedPieceLocy]).unselect();
                        pieceSelected = false;
                    }
                }
            }
            //draw everything
            batch.begin();
            drawAll(batch, mouseVars);
            batch.end();
    }

    void executeMove(int xTarget, int yTarget, Piece pieceMoving, int movetype){
        if (playerTurn == colourOfUser) {
            sendMoveToServer(xTarget,yTarget,pieceMoving);
        }
        super.executeMove(xTarget,yTarget,pieceMoving,movetype);

    }

    void sendMoveToServer(int xTarget, int yTarget, Piece pieceMoving){

        //take the location of the piece moving, and the location of the square targeted and put them into a string
        //String is in format "####" where the first two numbers are the x and y coords of the piece moving, and
        //the next two are the coords of the square being targeted
        String move = String.valueOf(pieceMoving.xLocation) + String.valueOf(pieceMoving.yLocation) + String.valueOf(xTarget) + String.valueOf(yTarget);
        //convert move to hex
        move = StringUtils.convertToHex(move);
        //create the message to be sent to server, and send it
        String message = "MOVE " + move;
        serverComms.sendMessageToServer(message + "\n");
    }

    void checkIfGameOver(){
        super.checkIfGameOver();
        if (gameOver) {
            if (serverComms != null) {
                serverComms.sendMessageToServer("RANKED_MATCH_OVER\n");
            }
        }
    }

}
