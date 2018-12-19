package ceov2.org;


import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class MultiplayerGameState extends GameState {
ServerCommunications serverComms;

    public MultiplayerGameState(){

    }

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

    //make a move based on a String containing the location, and the destination of the move
    //example string 1765, where (1,7) is the initial location of the piece, and (6,5) is the location
    //the piece is moving to
    void executeMoveFromServer(String move) {
        //the String must be exactly 4 characters long or it won't execute
        if (move.length() == 4) {
            //array storing the four integers
            int[] integers = new int[4];
            //the string may be 4 character but be invalid "12t2" for example
            boolean validInputString = true;
            //the four integers
            int currentPiecex;
            int currentPiecey;
            int moveLocx;
            int moveLocy;
            //loop through the 4 characters
            for (int x = 0; x != 4; x++) {
                //test if the character is a number form 0-7
                if (StringUtils.isValidNumber(move.charAt(x))) {
                    //if it is, put it's integer value into the integers array
                    integers[x] = Integer.valueOf(String.valueOf(move.charAt(x)));
                } else {
                    //if it's not valid the string is set to invalid and no further things of substance
                    //will execute
                    validInputString = false;
                }
            }
            //put the array values into individual integers, purely for readability
            currentPiecex = integers[0];
            currentPiecey = integers[1];
            moveLocx = integers[2];
            moveLocy = integers[3];
            //if the input string was valid, continue on
            if (validInputString) {
                //if the input string was valid, but the move is illegal on the board, it won't occur
                boolean validMove = false;
                //find the index of the piece moving
                int indexOfPieceMoving = piecesOnBoard[currentPiecex][currentPiecey];
                //find the location of the move in the 15x15 moveset array
                int moveLocXOnMoveset = moveLocx + 7 - currentPiecex;
                int moveLocYOnMoveset = moveLocy + 7 - currentPiecey;
                //see if the move is valid
                if (allPiecesOnBoard.get(indexOfPieceMoving).validMoves[moveLocXOnMoveset][moveLocYOnMoveset]) {
                    validMove = true;
                }
                //find the movetype the piece is using
                int movetypePieceUsing = allPiecesOnBoard.get(indexOfPieceMoving).moveset[moveLocXOnMoveset][moveLocYOnMoveset];
                //if the move is valid, execute the move
                if (validMove) {
                    executeMove(moveLocx, moveLocy, allPiecesOnBoard.get(indexOfPieceMoving), movetypePieceUsing);
                    updateBoard();
                }
            }
        }


        // if (allPiecesOnBoard.get(piecesOnBoard).validMoves[xOffset][yOffset] ) {
        //    validMove = true;
        // }
        //TODO
    }

}
