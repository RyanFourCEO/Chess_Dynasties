package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;

//import sun.security.provider.ConfigFile;
//temporary for clipboard pasting stuff

//this class deals with all the logic of a live game.
public class GameState {


    String yourArmy;
    String oppArmy;

    //holds morale of both players,index 0=player 1(white)index 1=player 2(black)
    int[] moraleTotals = new int[2];
    //tells the game when the board should be flipped, this changes the functionality
    //of the findSquareMouseIsOn method, and the drawAll Method, when all the piece's get drawn
    boolean flipBoard = false;
    //colourOfUser tells what colour the user is playing as 1=white, 2=black
    int colourOfUser;
    //playerturn tells who's turn it is, 1=player 1's turn(white), 2=player 2's turn(black)
    int playerTurn = 1;
    //tells if a piece is selected by a player(they have clicked it)
    boolean pieceSelected;
    //the location of the selected piece
    int selectedPieceLocx;
    int selectedPieceLocy;
    //the index of the selected piece in the array of pieces
    int selectedPiece;
    //the index of the last piece that was selected, this is used to display that piece's
    //moveset and abilities to the user. This allows the user to click on a piece to have it's information
    //displayed
    int pieceLastSelected;
    //booleans for the state of the game
    boolean whiteWins = false;
    boolean blackWins = false;
    boolean gameOver = false;
    boolean turnJustStarted = false;
    boolean turnJustEnded = false;

    //variables storing whether a move was just used, and what move was just used,these variables are currently unused
    boolean moveTypeJustUsed = false;
    int moveTypeUsed = 0;

    //variable used for alpha of drawing moves on the board
    long timePieceLastSelected;

    //location of the mouse {x,y}
    int[] loc = new int[2];

    //self explanatory, used for drawing moves on the board
    boolean hasMouseChangedLocationsYet = false;

    int turnCounter = 0;

    Texture boardImage;
    Texture reticleTexture;
    Texture reticleTextureBlocked;
    Texture reticleTextureSelected;
    Sprite sprite;

    //the variables controlling the board
    int boardSize = 618;
    int boardPosX = 331;
    int boardPosY = 0;

    //font object for writing morale values to the screen
    BitmapFont font;

    //boardPieces is only used because Piece files do not exist yet
    ArrayList<Piece> tempPieces = new ArrayList<Piece>();
    //the array of all Pieces that are on the board
    ArrayList<Piece> allPiecesOnBoard = new ArrayList<Piece>();

    //allows messages to be sent to server
    ServerCommunications serverComms;

    //contains the information about what occupies each square of the board
    //0=empty square, 1=square occupied by white piece, 2=square occupied by black piece
    int[][] boardState = new int[8][8];

    //contains the information about what specific piece is on each square of the board
    //i.e. it is equal to the index of the piece in the array of Pieces,-1 means unoccupied
    int[][] piecesOnBoard = new int[8][8];
    ArrayList<ArrayList<Integer>> allMovesMade = new ArrayList<ArrayList<Integer>>();

    //single player practice game constructor
    public GameState() {
        initFont();
        Piece.InitAllMoveTypes();
        loadArmies();
        loadGraphics();
        setBoard();
        turnJustStarted = true;
        testAndExecuteAbilities();
        turnJustStarted = false;
        updatePieceMoveSets();
        findAllValidMoves();
    }

    //multiplayer game constructor
    //colour=1 means user is white, colour=2 means user is black
    public GameState(int colour, String army, String oppArmy, ServerCommunications serverComms) {
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

    //creates a new gamestate with all previous moves and the "projected" move executed
    public GameState(boolean NeededForSomeReason) {

    }

    void executeArrayOfMoves(ArrayList<ArrayList<Integer>> moves) {
        for (int i = 0; i < ((moves.isEmpty()) ? 0 : moves.size()); i++) {
            System.out.println(i + " hello");
            executeMoveUsingSquareLocations(moves.get(i).get(0), moves.get(i).get(1), moves.get(i).get(2), moves.get(i).get(3));
        }
    }

    void projectHoveredMove(int indexOfPieceMoving) {
        //if the input string was valid, continue on
        //find the location of the move in the 15x15 moveset array
        int moveLocXOnMoveset = loc[0] + 7 - allPiecesOnBoard.get(indexOfPieceMoving).xLocation;
        int moveLocYOnMoveset = loc[1] + 7 - allPiecesOnBoard.get(indexOfPieceMoving).yLocation;
        //find all valid moves , then see if the move is valid
        findAllValidMoves();
        //find the movetype the piece is using
        int movetypePieceUsing = allPiecesOnBoard.get(indexOfPieceMoving).moveset[moveLocXOnMoveset][moveLocYOnMoveset];
        //if the move is valid, execute the move
        if (allPiecesOnBoard.get(indexOfPieceMoving).validMoves[moveLocXOnMoveset][moveLocYOnMoveset]) {
            executeMove(loc[0], loc[1], allPiecesOnBoard.get(indexOfPieceMoving), movetypePieceUsing);
            updateBoard();
        }
    }

    //draw the difference
    void drawDifference(SpriteBatch batch, GameState main, GameState sim, DiffBetweenGameStates diff) {
        batch.begin();
        Shaders.prepareDistanceFieldShader();
        batch.setShader(Shaders.distanceFieldShader);
        //draw morale change
        if (diff.moraleDifferenceWhite != 0) {
            String moraleWhite = String.valueOf(diff.newMoraleWhite);
            if (diff.moraleDifferenceWhite < 0) {
                font.setColor(Color.GREEN);
            } else {
                font.setColor(Color.RED);
            }
            font.draw(batch, moraleWhite, 1050, 50);
        }
        if (diff.moraleDifferenceBlack != 0) {
            String moraleBlack = String.valueOf(diff.newMoraleBlack);
            if (diff.moraleDifferenceBlack < 0) {
                font.setColor(Color.GREEN);
            } else {
                font.setColor(Color.RED);
            }
            font.draw(batch, moraleBlack, 1050, 580);
        }
        font.setColor(Color.BLACK);
        batch.end();
        batch.setShader(Shaders.defaultShader);
        if (diff.indexesOfPiecesWhichHaveMoved.size() != 0) {
            for (int i = 0; i != diff.indexesOfPiecesWhichHaveMoved.size(); i++) {
                int currentPieceLocX = diff.currentPieceLocationsX[diff.indexesOfPiecesWhichHaveMoved.get(i)];
                int currentPieceLocY = diff.currentPieceLocationsY[diff.indexesOfPiecesWhichHaveMoved.get(i)];

                int newPieceLocX = diff.newPieceLocationsX[diff.indexesOfPiecesWhichHaveMoved.get(i)];
                int newPieceLocY = diff.newPieceLocationsY[diff.indexesOfPiecesWhichHaveMoved.get(i)];

                int[] currentBoardPixels = findScreenCoordinatesOfSquare(currentPieceLocX, currentPieceLocY);
                int[] newBoardPixels = findScreenCoordinatesOfSquare(newPieceLocX, newPieceLocY);

                ShapeRenderer shapeRenderer = new ShapeRenderer();
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                //TODO: Replace this with move associated color
                shapeRenderer.setColor(1, 0, 0, 1); //TODO: Add arrow to the end of line
                shapeRenderer.rectLine(currentBoardPixels[0], currentBoardPixels[1], newBoardPixels[0], newBoardPixels[1], 4);
                shapeRenderer.end();
            }
        }

        /*for (int i = 0; i > listOfThingsToDraw.size(); i++) {
            //morale change: should only be one
            if (diff.get(i).get(0) == 0) {
                int moraleChange = listOfThingsToDraw.get(i).get(3);
                switch (listOfThingsToDraw.get(i).get(4)) {
                    case 1:
                        //draw loss
                        break;
                    case 2:
                        //draw tie
                        break;
                    //default draw nothing
                }
            }*/
        //draw location changes
        // if (listOfThingsToDraw.get(i).get(0) == 1) {
        //draw the arrow between start and end
        //draw the moved piece at end
        //}
        //draw deaths
        //if (listOfThingsToDraw.get(i).get(0) == 2) {
        //draw death icon on death location
        // }
        //draw creates
        //if (listOfThingsToDraw.get(i).get(0) == 3) {
        //draw create icon on create location
        //}
        //draw attacks
        // if (listOfThingsToDraw.get(i).get(0) == 4) {
        //draw the attacks arrow
        //based on movetype
        //}
        //draw applied statuses
        // if (listOfThingsToDraw.get(i).get(0) == 5) {
        //draw the status icon on statused piece
        //}
    }
    //draw


    //this method executes every tick
    void runGame(SpriteBatch batch, MouseVars mouseVars) {
        //see if the player has clicked on a piece, or it trying to move a piece
        //if the player has made a valid move this method will also execute it
        processMouseInputClick(mouseVars);
        processMouseInputRelease(mouseVars);
        //draw everything
        batch.begin();
        drawAll(batch, mouseVars);
        batch.end();
    }

    //version of above method for multiplayer games, minor differences
    void runMultiplayerGame(SpriteBatch batch, MouseVars mouseVars) {
        for(int x=0;x!=allMovesMade.size();x++) {
            System.out.println("move numbre " + x);
        System.out.println(allMovesMade.get(x).get(0));
            System.out.println(allMovesMade.get(x).get(1));
            System.out.println(allMovesMade.get(x).get(2));
            System.out.println(allMovesMade.get(x).get(3));
        }

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

    void initFont() {
        //load font texture
        Texture texture = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        //create font object using above texture
        font = new BitmapFont(Gdx.files.internal("Fonts\\ArialDistanceField2.fnt"), new TextureRegion(texture), false);
        font.setColor(Color.BLACK);
    }

    //put all pieces on the board, set their locations
    void setBoard() {
        //set pieceOnBoard to -1, which means the square is unoccupied
        for (int x = 0; x != 8; x++) {
            for (int y = 0; y != 8; y++) {
                piecesOnBoard[x][y] = -1;
            }
        }
        int counter = 0;
        //which row the pieces will be placed on, from 0-7
        int row = 0;
        //what colour the pieces are, 1 means white, 2 means black
        int colour = 0;

        //4*8 loop to loop through all 32 pieces
        for (int x = 0; x != 4; x++) {
            //the row and colour are set the the right numbers
            if (x == 0) {
                row = 0;
                colour = 1;
            }
            if (x == 1) {
                row = 1;
            }
            if (x == 2) {
                colour = 2;
                row = 7;
            }
            if (x == 3) {
                row = 6;
            }
            //the boards values are updated
            for (int y = 0; y < 8; y++) {
                //the array boardState is set to the colour of the piece, a value of 0 means unoccupied
                boardState[y][row] = colour;
                if (colour == 1) {
                    moraleTotals[0] += allPiecesOnBoard.get(counter).moraleCost;
                } else if (colour == 2) {
                    moraleTotals[1] += allPiecesOnBoard.get(counter).moraleCost;
                }
                //The array of pieces: each piece has its position on the board set
                allPiecesOnBoard.get(counter).setLocation(y, row);
                //the array piecesOnBoard is set to the index of the piece
                piecesOnBoard[y][row] = counter;
                counter++;
            }
        }
    }

    //find if a move is valid
    public void findIfValidMove(int x, int y, int a) {

        //if the piece has been captured, obviously it can't move
        //if movesDisabled is true, then the piece's moves have been disabled by a status effect
        if (allPiecesOnBoard.get(a).captured == false && allPiecesOnBoard.get(a).movesDisabled == false) {
            //if the piece has no movement on the square, obviously it can't move there
            if (allPiecesOnBoard.get(a).moveset[x][y] != 0) {
                //using the location of the move in the 15*15 array moveset, and the location of the piece being moved
                //the destination of the move on the board is found
                int xOffset = x - 7;
                int yOffset = y - 7;
                int xOnBoard = allPiecesOnBoard.get(a).xLocation + xOffset;
                int yOnBoard = allPiecesOnBoard.get(a).yLocation + yOffset;
                //if the destination is off the board,obviously the piece can't move there
                if (xOnBoard >= 0 && xOnBoard <= 7 && yOnBoard >= 0 && yOnBoard <= 7) {


                    boolean validTarget = false;
                    boolean targetProtected = false;
                    //if the square is occupied by a piece, test to see if the piece is protected by abilities/statuses, if so, the piece may be protected from certain movetypes, these movetypes will
                    //be set as invalid moves
                    if (piecesOnBoard[xOnBoard][yOnBoard] != -1) {
                        targetProtected = checkIfTargetIsProtected(boardState[xOnBoard][yOnBoard], allPiecesOnBoard.get(a).moveset[x][y] % 1000, xOnBoard, yOnBoard, allPiecesOnBoard.get(a));
                    }

                    //test if the square is a valid target for the piece to move to
                    //for example a piece that can move/attack can go to an empty square or an enemy occupied square
                    //but not an ally square. (if a piece is immovable, this method also checks to make sure it's moves that
                    //would cause movement are disabled)
                    validTarget = Piece.allMoveTypes[0][allPiecesOnBoard.get(a).moveset[x][y] % 1000].checkIsValidTarget(boardState[xOnBoard][yOnBoard], playerTurn, allPiecesOnBoard.get(a).immovable);

                    //if the target is not valid, the piece can't move there
                    if (validTarget && targetProtected == false) {
                        //check if the piece is blocked, (bishops can't move through other pieces)
                        boolean blocked = false;
                        //if the piece can jump over pieces, a different method is run to check the validity of the move
                        if (Piece.allMoveTypes[0][allPiecesOnBoard.get(a).moveset[x][y] % 1000].canJumpOverOnePiece == false) {
                            //some movetypes can't be blocked, so if that isn't an issue, blocked remains false
                            //a piece with moveset value greater than 1000 is unblockable
                            if (allPiecesOnBoard.get(a).moveset[x][y] < 1000) {
                                //method to check if the piece is blocked
                                blocked = checkIfPieceIsBlocked(xOnBoard, yOnBoard, allPiecesOnBoard.get(a).xLocation, allPiecesOnBoard.get(a).yLocation);
                            }
                        } else {
                            //method to check if the piece is blocked, made specifically for cannon
                            blocked = checkIfPieceIsBlockedCannonVersion(xOnBoard, yOnBoard, allPiecesOnBoard.get(a).xLocation, allPiecesOnBoard.get(a).yLocation);
                        }

                        //if the move is not blocked, the piece object's array of valid moves
                        //has that index marked true, meaning it can make that move
                        if (blocked == false) {
                            allPiecesOnBoard.get(a).validMoves[x][y] = true;
                        }
                    }
                }
            }
        }
    }

    //OVERLOAD: uses two sets of coords instead of piece index
    public void findIfValidMove(int x, int y, int x2, int y2) {

        int a = piecesOnBoard[x2][y2];

        //if the piece has been captured, obviously it can't move
        //if movesDisabled is true, then the piece's moves have been disabled by a status effect
        if (allPiecesOnBoard.get(a).captured == false && allPiecesOnBoard.get(a).movesDisabled == false && a != 0) {
            //if the piece has no movement on the square, obviously it can't move there
            if (allPiecesOnBoard.get(a).moveset[x][y] != 0) {
                //using the location of the move in the 15*15 array moveset, and the location of the piece being moved
                //the destination of the move on the board is found
                int xOffset = x - 7;
                int yOffset = y - 7;
                int xOnBoard = allPiecesOnBoard.get(a).xLocation + xOffset;
                int yOnBoard = allPiecesOnBoard.get(a).yLocation + yOffset;
                //if the destination is off the board,obviously the piece can't move there
                if (xOnBoard >= 0 && xOnBoard <= 7 && yOnBoard >= 0 && yOnBoard <= 7) {

                    boolean validTarget = false;
                    boolean targetProtected = false;
                    //if the square is occupied by a piece, test to see if the piece is protected by abilities/statuses, if so, the piece may be protected from certain movetypes, these movetypes will
                    //be set as invalid moves
                    if (piecesOnBoard[xOnBoard][yOnBoard] != -1) {
                        targetProtected = checkIfTargetIsProtected(boardState[xOnBoard][yOnBoard], allPiecesOnBoard.get(a).moveset[x][y] % 1000, xOnBoard, yOnBoard, allPiecesOnBoard.get(a));
                    }

                    //test if the square is a valid target for the piece to move to
                    //for example a piece that can move/attack can go to an empty square or an enemy occupied square
                    //but not an ally square. (if a piece is immovable, this method also checks to make sure it's moves that
                    //would cause movement are disabled)
                    validTarget = Piece.allMoveTypes[0][allPiecesOnBoard.get(a).moveset[x][y] % 1000].checkIsValidTarget(boardState[xOnBoard][yOnBoard], playerTurn, allPiecesOnBoard.get(a).immovable);


                    //if the target is not valid, the piece can't move there
                    if (validTarget && targetProtected == false) {
                        //check if the piece is blocked, (bishops can't move through other pieces)
                        boolean blocked = false;
                        //if the piece can jump over pieces, a different method is run to check the validity of the move
                        if (Piece.allMoveTypes[0][allPiecesOnBoard.get(a).moveset[x][y] % 1000].canJumpOverOnePiece == false) {
                            //some movetypes can't be blocked, so if that isn't an issue, blocked remains false
                            //a piece with moveset value greater than 1000 is unblockable
                            if (allPiecesOnBoard.get(a).moveset[x][y] < 1000) {
                                //method to check if the piece is blocked
                                blocked = checkIfPieceIsBlocked(xOnBoard, yOnBoard, allPiecesOnBoard.get(a).xLocation, allPiecesOnBoard.get(a).yLocation);
                            }
                        } else {
                            //method to check if the piece is blocked, made specifically for cannon
                            blocked = checkIfPieceIsBlockedCannonVersion(xOnBoard, yOnBoard, allPiecesOnBoard.get(a).xLocation, allPiecesOnBoard.get(a).yLocation);
                        }

                        //if the move is not blocked, the piece object's array of valid moves
                        //has that index marked true, meaning it can make that move
                        if (blocked == false) {
                            allPiecesOnBoard.get(a).validMoves[x][y] = true;
                        }
                    }
                }
            }
        }
    }

    //find all the valid moves on any given turn
    public void findAllValidMoves() {
//loop through the arrayList of pieces
        for (int a = 0; a < allPiecesOnBoard.size(); a++) {
            if ((allPiecesOnBoard.get(a).isWhite && playerTurn == 1) || (!allPiecesOnBoard.get(a).isWhite && playerTurn == 2)){
//loop through the movesets of each piece
                for (int x = 0; x != 15; x++) {
                    for (int y = 0; y != 15; y++) {
                        findIfValidMove(x, y, a);
                    }
                }
            }
        }
    }

    //call every time a move is made to refresh piece movesets to potentially new values
    public void updatePieceMoveSets(){
        for (int a = 0; a < allPiecesOnBoard.size(); a++) {
            //update the moveset of a piece, abilities may have changed it
            allPiecesOnBoard.get(a).setMoveset();
        }
    }

    //check if a piece is protected by statuses or any other reason, if the piece is protected for any reason
    //it will not be targetable by certain pieces/movetypes
    private boolean checkIfTargetIsProtected(int boardState, int moveTypeUsed, int xPosOfPiece, int yPosOfPiece, Piece pieceMoving) {
        boolean pieceIsProtected = false;
        //if the boardstate is equal to playerturn, that means the target is an ally
        if (boardState == playerTurn) {
            //if the ally piece is protected from attacks
            if (allPiecesOnBoard.get(piecesOnBoard[xPosOfPiece][yPosOfPiece]).protectedFromAttacks) {
                //and the movetype would capture an ally piece
                if (Piece.allMoveTypes[0][moveTypeUsed].movetypeCapturesAllies) {
                    //the piece is set to protected, which means the movetype can't be used on the piece
                    pieceIsProtected = true;
                }
            }
        } else {
            //same as above, but with enemy pieces
            if (allPiecesOnBoard.get(piecesOnBoard[xPosOfPiece][yPosOfPiece]).protectedFromAttacks) {
                if (Piece.allMoveTypes[0][moveTypeUsed].movetypeCapturesEnemies) {
                    pieceIsProtected = true;
                }
            }
        }
        //their is a status effect that prevents a piece from capturing king, if a piece has it, the target is set to protected
        if (allPiecesOnBoard.get(piecesOnBoard[xPosOfPiece][yPosOfPiece]).name.equalsIgnoreCase(("king"))) {
            if (pieceMoving.cantTargetKing) {
                pieceIsProtected = true;
            }
        }
        return pieceIsProtected;
    }

    //use mouse input to see if the user is clicking on or has selected a piece
    public void processMouseInputClick(MouseVars mouseVars) {
        //loc is set to the squares on the board that the mouse cursor is located at

        if (mouseVars.mouseClicked) {
            //if loc is actually on the board, we have to test to see if the square is occupied
            if (loc[0] >= 0 && loc[1] >= 0 && loc[0] <= 7 && loc[1] <= 7) {

                //this happens so that the player can click on pieces and see what they are, it has no
                //relevance on the code occurring here
                if (boardState[loc[0]][loc[1]] != 0) {
                    pieceLastSelected = piecesOnBoard[loc[0]][loc[1]];
                }

                //if the square is a piece the player owns, they might be able to pick it up
                if (boardState[loc[0]][loc[1]] == playerTurn) {
                    //if they haven't already selected a piece, the player can pick up a new piece
                    if (!pieceSelected) {
                        //the piece is selected
                        allPiecesOnBoard.get(piecesOnBoard[loc[0]][loc[1]]).select();
                        //the selected piece's location and index are stored
                        selectedPieceLocx = loc[0];
                        selectedPieceLocy = loc[1];
                        selectedPiece = piecesOnBoard[loc[0]][loc[1]];
                        //pieceSelected is set true, so 2 pieces can't be selected at once
                        pieceSelected = true;
                    }
                }
            }
        }

    }

    //use mouse input to see if the player is trying to make a move by releasing the mouse
    public void processMouseInputRelease(MouseVars mouseVars) {
        //loc is set to the squares on the board that the mouse cursor is located at
        if (mouseVars.mouseReleased) {

            boolean validMove = false;
            //find the index for the moveset array to use
            int xOffset = 7 + loc[0] - allPiecesOnBoard.get(selectedPiece).xLocation;
            int yOffset = 7 + loc[1] - allPiecesOnBoard.get(selectedPiece).yLocation;
            //if a piece is selected, we know the player has attempted to make a move
            if (pieceSelected) {
                //if the mouse is outside the board, then no move can be made
                if (loc[0] >= 0 && loc[1] >= 0 && loc[0] <= 7 && loc[1] <= 7) {
                    //check the validMoves array to see if the square targeted is valid
                    if (allPiecesOnBoard.get(selectedPiece).validMoves[xOffset][yOffset]) {
                        validMove = true;
                    }
                }
            }

            hasMouseChangedLocationsYet = false;
            //if the move was found to be valid
            if (validMove) {
                //execute the move
                executeMove(loc[0], loc[1], allPiecesOnBoard.get(selectedPiece), allPiecesOnBoard.get(selectedPiece).moveset[xOffset][yOffset]);
                //set up for the next turn
                updateBoard();

            } else {
                if (pieceSelected) {
                    //if the move was invalid, the piece is unselected
                    allPiecesOnBoard.get(piecesOnBoard[selectedPieceLocx][selectedPieceLocy]).unselect();
                }
            }
            // this happens any time the mouse is released
            pieceSelected = false;
        }
    }

    //this method is currently unused
    void loadPieces() {
        int[][] moveset = new int[15][15];
        String line = "2,3,1,1,1,1,1,1,1,1,5";
        String line2 = "1,2,1,1,1,1,1,1,1,1,4";
        String line3 = "4,1,0,1,1,0,1,1,1,1";
        // line="10,9,8,7,6,5,4,3,2,1";
//setMoveSet(moveset,line);
        // setMoveSet(moveset,line2);
        // setMoveSet(moveset,line3);
    }

    //loads the current armies
    void loadArmies() {
        //load the army1 file, this will contain the setup for the white army
        String army = Gdx.files.internal("UserFiles\\armies\\army1.txt").readString();
        String[] separatedNames;
        //separate the loaded string from the file into it's 16 piece names
        separatedNames = army.split(",");
        //load all the pieces corresponding to the piece names from the file
        //load all white pieces first, so they take up the first 16 places in the array
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(separatedNames[x], true));
        }
//repeat the above for the black pieces, which are loaded from army2
        army = Gdx.files.internal("UserFiles\\armies\\army2.txt").readString();
        separatedNames = army.split(",");
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(separatedNames[x], false));
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }

    //same as above method, but calls a piece constructor where graphics aren't loaded
    void loadArmiesNoGraphics() {
        //load the army1 file, this will contain the setup for the white army
        String army = Gdx.files.internal("UserFiles\\armies\\army1.txt").readString();
        String[] separatedNames;
        //separate the loaded string from the file into it's 16 piece names
        separatedNames = army.split(",");
        //load all the pieces corresponding to the piece names from the file
        //load all white pieces first, so they take up the first 16 places in the array
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(true, separatedNames[x]));
        }
//repeat the above for the black pieces, which are loaded from army2
        army = Gdx.files.internal("UserFiles\\armies\\army2.txt").readString();
        separatedNames = army.split(",");
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(false, separatedNames[x]));
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }

    //overload of above method, mimics below method but doesn't load graphics
    void loadArmiesNoGraphics(int colour, String army, String oppArmy){
        //this if statement exists only to ensure the first 16 pieces added to the array allPiecesOnBoard
        //are white.If the user is player one, aka white, load their pieces first,otherwise load their opponent's
        //pieces
        if (colour == 1) {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = army.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(true,separated[x]));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = oppArmy.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(false,separated[x]));
            }
        } else {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = oppArmy.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(true,separated[x]));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = army.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(false,separated[x]));
            }
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }


    //loads the two armies from "army" and "oppArmy"
    //strings are in the following format "pawn,pawn,pawn,....knight,rook"
    //if the user is white, colour=1, if the user is black colour=2
    private void loadArmies(int colour, String army, String oppArmy) {
        //this if statement exists only to ensure the first 16 pieces added to the array allPiecesOnBoard
        //are white.If the user is player one, aka white, load their pieces first,otherwise load their opponent's
        //pieces
        if (colour == 1) {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = army.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(separated[x], true));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = oppArmy.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(separated[x], false));
            }
        } else {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = oppArmy.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(separated[x], true));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = army.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(separated[x], false));
            }
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }

    //prepare the board for the next turn
    private void updateBoard() {
        turnCounter++;
        updatePieceCounters();
        turnJustEnded = true;
        testAndExecuteAbilities();
        turnJustEnded = false;

        //change whose turn it is
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }

        //reset the valid moves arrays and find the new set of valid moves

        //set all piece selected variables back to default, unselected
        allPiecesOnBoard.get(selectedPiece).unselect();
        pieceSelected = false;
        selectedPiece = 0;
        //see if each player's king is still alive, if not the player suffers a morale penalty on the start
        //of their turn
        checkIfKingLives();
        ///check if any morale values have reached 0, if so end the game
        checkIfGameOver();

        turnJustStarted = true;
        testAndExecuteAbilities();
        turnJustStarted = false;

        setAllMovesInvalid();
        updatePieceMoveSets();
        findAllValidMoves();
    }

    //same as above method, but doesn't do unnecessary things that are necessary in ordinary games
    private void updateBoardSim(){
        turnCounter++;
        updatePieceCounters();
        turnJustEnded = true;
        testAndExecuteAbilities();
        turnJustEnded = false;

        //change whose turn it is
        if (playerTurn == 1) {
            playerTurn = 2;
        } else {
            playerTurn = 1;
        }

        //see if each player's king is still alive, if not the player suffers a morale penalty on the start
        //of their turn
        checkIfKingLives();
        ///check if any morale values have reached 0, if so end the game
        checkIfGameOver();

        turnJustStarted = true;
        testAndExecuteAbilities();
        turnJustStarted = false;

        updatePieceMoveSets();
    }


    //loop through all pieces and increase their counters, this will reduce the time of their status effects and increase the
//number of turns they have survived
    private void updatePieceCounters() {
        //loop through all pieces and update their counters
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            allPiecesOnBoard.get(x).updateStatuses(playerTurn);
        }
        //some pieces are set to be removed from the board by statuses, currently only the doomed, status, this
        //loops through all pieces and removes them if they are set to be removed
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            //if a piece is set to be removed from the board(captured), and has not already been captured, it is captured
            if (allPiecesOnBoard.get(x).setToBeRemovedFromBoard && allPiecesOnBoard.get(x).captured == false) {
                capturePieceWithStatus(allPiecesOnBoard.get(x));
            }
        }

    }

    //test all an abilities triggers, and if any should be triggered, trigger them
    void testAndExecuteAbilities() {
        //loop through all pieces
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {

            //if the piece has been captured, it's abilities can't execute
            //if a piece has it's abilities disabled, it's abilities don't execute
            if (!allPiecesOnBoard.get(x).captured && !allPiecesOnBoard.get(x).abilitiesDisabled) {
                //loop through all a piece's abilities
                for (int y = 0; y != allPiecesOnBoard.get(x).allAbilities.size(); y++) {
                    //triggersMet: all a piece's trigger must be met for the ability to execute
                    //every time a trigger is met, this is increased by 1, if this value
                    //is not equal to the number of triggers, the ability doesn't trigger
                    int triggersMet = 0;
                    //loop through all a piece's triggers
                    for (int z = 0; z != allPiecesOnBoard.get(x).allAbilities.get(y).allTriggers.size(); z++) {
                        //test to see if the trigger's condition is met
                        if (checkAbilityTrigger(allPiecesOnBoard.get(x).allAbilities.get(y).allTriggers.get(z), allPiecesOnBoard.get(x))) {
                            triggersMet++;
                        }
                    }
                    //if triggersmet is equal to the number of triggers a piece's ability has, the ability is executed
                    if (triggersMet == allPiecesOnBoard.get(x).allAbilities.get(y).allTriggers.size()) {
                        executeAbilityEffect(allPiecesOnBoard.get(x).allAbilities.get(y).effect, allPiecesOnBoard.get(x));
                    }

                }
            }
        }

    }

    //All triggers listed here
    boolean checkAbilityTrigger(AbilityTrigger trigger, Piece thisPiece) {
        boolean triggered = false;
        switch (trigger.triggerIndex) {
//index 0, if the piece reaches the opposite side of the board
            case 0:
                if (thisPiece.isWhite) {

                    if (thisPiece.yLocation == 7) {
                        triggered = true;
                    }
                } else {
                    if (thisPiece.yLocation == 0) {
                        triggered = true;
                    }
                }

                break;


            //index 1 if the piece gets captured (on death effect)
            case 1:
                if (thisPiece.justCaptured) {
                    triggered = true;
                }
                break;
//index 2, if this piece kills x pieces
            case 2:
                if (thisPiece.piecesCaptured % trigger.requiredNumber == 0) {
                    if (thisPiece.justGotCapture) {
                        triggered = true;
                    }
                }
                break;

//index 3, if this piece is adjacent to x allies
            case 3:

                if (findNumberOfAlliesAdjacentTo(thisPiece.xLocation, thisPiece.yLocation, thisPiece.isWhite) >= trigger.requiredNumber) {
                    triggered = true;
                }
                break;
//index 4, if this piece is targeted by attacks x times
            case 4:
                if (thisPiece.moveTypeTargetedBy == 1) {
                    if (thisPiece.timesTargeted % trigger.requiredNumber == 0) {
                        if (thisPiece.justTargeted) {
                            triggered = true;
                        }
                    }
                }
                break;

//index 5, x turns after this piece comes into play
            case 5:
                if (thisPiece.turnsSurvived == trigger.requiredNumber) {
                    if (turnJustStarted) {
                        triggered = true;
                    }
                }
                break;
//index 6, if this piece is adjacent to EXACTLY x allies
            case 6:


                if (findNumberOfAlliesAdjacentTo(thisPiece.xLocation, thisPiece.yLocation, thisPiece.isWhite) == trigger.requiredNumber) {
                    triggered = true;
                }
                break;
//index 7, if this piece has made x moves
            case 7:
                if (thisPiece.justMoved) {
                    if (thisPiece.numberOfMovesMade == trigger.requiredNumber) {
                        triggered = true;
                    }
                }
                break;
//index 8, if movetype x is used by the piece that has the ability
            case 8:
                if (thisPiece.justUsedMovetype) {
                    if (Piece.moveTypeIndexes[thisPiece.movetypeUsed] == trigger.requiredNumber) {
                        triggered = true;
                    }
                }
                break;
//index 10, start of own turn
            case 10:
                if (playerTurn == thisPiece.playerWhoOwnsPiece) {
                    if (turnJustStarted) {
                        triggered = true;
                    }
                }
                break;
//index 11 start of opponent's turn
            case 11:
                if (playerTurn != thisPiece.playerWhoOwnsPiece) {
                    if (turnJustStarted) {
                        triggered = true;
                    }
                }
                break;
//index 12 start of either player's turns
            case 12:
                if (turnJustStarted) {
                    triggered = true;
                }
                break;
//index 13 end of player's turn
            case 13:
                if (playerTurn == thisPiece.playerWhoOwnsPiece) {
                    if (turnJustEnded) {
                        triggered = true;
                    }
                }
                break;
//index 14 end of opponent's turn
            case 14:
                if (playerTurn != thisPiece.playerWhoOwnsPiece) {
                    if (turnJustEnded) {
                        triggered = true;
                    }
                }
                break;
//index 15, end of either player's turn
            case 15:
                if (turnJustEnded) {
                    triggered = true;
                }
                break;

        }
        return triggered;
    }

    //All effects listed here, ability effects executed here
    private void executeAbilityEffect(AbilityEffect effect, Piece thisPiece) {

//this prevents infinite loops, if an ability's effect has not yet been completed it can't activate again
        if (!effect.inEffect) {
            effect.inEffect = true;
            switch (effect.effectIndex) {
//destroy self ability
                case 0:
                    capturePieceWithAbility(thisPiece.xLocation, thisPiece.yLocation, thisPiece, 0);
                    break;
//give status effect to self ability
                case 2:
                    thisPiece.addStatusEffect(Integer.valueOf(effect.effectVar2), Integer.valueOf(effect.effectVar1));
                    break;
//give status to piece which just attacked
                case 3:
                    thisPiece.pieceTargetedBy.addStatusEffect(Integer.valueOf(effect.effectVar2), Integer.valueOf(effect.effectVar1));
                    break;
//summon a piece on locations marked with movetype 20/21
                case 4:
                    for (int x = 0; x != 15; x++) {
                        for (int y = 0; y != 15; y++) {
                            if (Piece.moveTypeIndexes[thisPiece.moveset[x][y] % 1000] == 20 || Piece.moveTypeIndexes[thisPiece.moveset[x][y] % 1000] == 21) {
                                int[] locOnBoard = findLocationOnBoard(x, y, thisPiece.xLocation, thisPiece.yLocation);
                                if (locOnBoard[0] >= 0 && locOnBoard[0] <= 7 && locOnBoard[1] >= 0 && locOnBoard[1] <= 7) {
                                    if (boardState[locOnBoard[0]][locOnBoard[1]] == 0) {
                                        summonPiece(effect.effectVar1, locOnBoard[0], locOnBoard[1], thisPiece.isWhite);
                                    }
                                }
                            }
                        }
                    }
                    break;
//lose x morale on death effect
                case 5:
                    if (thisPiece.isWhite) {
                        moraleTotals[0] -= Integer.valueOf(effect.effectVar1);
                    } else {
                        moraleTotals[1] -= Integer.valueOf(effect.effectVar1);
                    }
                    break;

//give self unable to target king status
                case 6:
                    thisPiece.addStatusEffect(10, Integer.valueOf(effect.effectVar1));
                    break;
//transform ability
                case 20:
                    transformPiece(effect.effectVar1, thisPiece.xLocation, thisPiece.yLocation, thisPiece.isWhite);
                    break;
                //gain the swap places with ally move on the location of ally king aka become able to swap with ally king
                case 21:
                    thisPiece.setChangeableMovesetEmpty();

                    for (int x = 0; x != allPiecesOnBoard.size(); x++) {
                        if (allPiecesOnBoard.get(x).name.equalsIgnoreCase("king") && !allPiecesOnBoard.get(x).captured) {
                            if (allPiecesOnBoard.get(x).isWhite == thisPiece.isWhite) {
                                int xOnMovesetArray = allPiecesOnBoard.get(x).xLocation - thisPiece.xLocation + 7;
                                int yOnMovesetArray = allPiecesOnBoard.get(x).yLocation - thisPiece.yLocation + 7;
                                thisPiece.changeableMoveset[xOnMovesetArray][yOnMovesetArray] = 1008;
                            }
                        }

                    }
                    break;
//gain the ability to move to any unoccupied space not adjacent to any pieces
                case 22:
                    thisPiece.setChangeableMovesetEmpty();
                    for (int x = 0; x != 8; x++) {
                        for (int y = 0; y != 8; y++) {
                            if (boardState[x][y] == 0) {
                                if (findNumberOfPiecesAdjacentTo(x, y) == 0) {
                                    int xOnMovesetArray = x - thisPiece.xLocation + 7;
                                    int yOnMovesetArray = y - thisPiece.yLocation + 7;
                                    thisPiece.changeableMoveset[xOnMovesetArray][yOnMovesetArray] = 1002;
                                }
                            }
                        }
                    }


                    break;

            }
        }
        effect.inEffect = false;
    }

    private void executeMove(int xTarget, int yTarget, Piece pieceMoving, int movetype) {
        System.out.println(movetype +" this is the move being madE!");
        //add the move being made to the array of moves made
        addMoveToListOfMoves(pieceMoving.xLocation, pieceMoving.yLocation, xTarget, yTarget);
        //if the user is the one that made the move, the move is sent to the server
        if (playerTurn == colourOfUser) {
            //take the location of the piece moving, and the location of the square targeted and put them into a string
            //String is in format "####" where the first two numbers are the x and y coords of the piece moving, and
            //the next two are the coords of the square being targeted
            String move = String.valueOf(pieceMoving.xLocation) + String.valueOf(pieceMoving.yLocation) + String.valueOf(xTarget) + String.valueOf(yTarget);
            System.out.println(move);
            //convert move to hex
            move = StringUtils.convertToHex(move);
            //create the message to be sent to server, and send it
            String message = "MOVE " + move;
            serverComms.sendMessageToServer(message + "\n");
        }

        //find if the movetype is blockable (if it's greater than 1000 it is blockable
        int blockable;
        if (movetype > 1000 && movetype < 2000) {
            movetype %= 1000;
            blockable = 1;
        } else {
            blockable = 0;
        }
        //if the movetype is a move from starting position movetype, it is removed after the piece moves
        if (Piece.allMoveTypes[blockable][movetype].moveType != 0) {
            pieceMoving.removeOneTimeMovesMoves();
        }
        //the movetype is set to just used, and abilities may occur based on this
        pieceMoving.movetypeUsed = movetype;
        pieceMoving.justUsedMovetype = true;
        testAndExecuteAbilities();
        pieceMoving.justUsedMovetype = false;

        //piece is set to having made a move,and  abilities may occur based on this
        pieceMoving.numberOfMovesMade++;
        pieceMoving.justMoved = true;
        testAndExecuteAbilities();
        pieceMoving.justMoved = false;

        switch (Piece.allMoveTypes[blockable][movetype].moveType) {
            case 0:
//movetype 0, the empty square movetype, does nothing
                break;
            case 1:
//movetype 1, the standard movetype, attack a square, if the square is empty, move to it
                capturePieceWithMove(xTarget, yTarget, pieceMoving, movetype);
                if (boardState[xTarget][yTarget] == 0) {
                    movePiece(pieceMoving.xLocation, pieceMoving.yLocation, xTarget, yTarget);
                }
                break;
//movetype 2, the ranged attack movetype, attack a square, but do not move to it unless the square is empty
            case 2:
                if (boardState[xTarget][yTarget] == 0) {
                    movePiece(pieceMoving.xLocation, pieceMoving.yLocation, xTarget, yTarget);
                } else {
                    capturePieceWithMove(xTarget, yTarget, pieceMoving, movetype);
                }
                break;
//movetype 3, the swap movetype, swap places with a square
            case 3:
                if (boardState[xTarget][yTarget] != 0) {
                    swapPiece(pieceMoving.xLocation, pieceMoving.yLocation, xTarget, yTarget);
                }
                break;
//movetype 4, the sacrifice self movetype, captures the piece that uses it
            case 4:
                capturePieceWithMove(pieceMoving.xLocation, pieceMoving.yLocation, pieceMoving, movetype);
                break;
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
                if (validMove){
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

    //executes a move based on 4 integers which detail the location of the piece that is moving
    //and the location the piece is targeting
    void executeMoveUsingSquareLocations(int squareOfPieceMovingx, int squareOfPieceMovingy, int targetX, int targetY) {


        int indexOfPieceMoving = piecesOnBoard[squareOfPieceMovingx][squareOfPieceMovingy];
        int moveLocXOnMoveset = targetX + 7 - squareOfPieceMovingx;
        int moveLocYOnMoveset = targetY + 7 - squareOfPieceMovingy;
        int movetypePieceUsing = allPiecesOnBoard.get(indexOfPieceMoving).moveset[moveLocXOnMoveset][moveLocYOnMoveset];
        System.out.println(movetypePieceUsing + " this movetype");
        executeMove(targetX, targetY, allPiecesOnBoard.get(indexOfPieceMoving), movetypePieceUsing);
        updateBoardSim();

    }

    //add the information of a move(the location of the piece moving, and the targeted square) to the array of all moves that have been made in the game
    //this method should execute every time a move is made
    void addMoveToListOfMoves(int pieceX, int pieceY, int targetX, int targetY) {
        ArrayList<Integer> integers = new ArrayList<Integer>();
        integers.add(pieceX);
        integers.add(pieceY);
        integers.add(targetX);
        integers.add(targetY);
        allMovesMade.add(integers);
    }

    //the method that captures a piece using a movetype
    public void capturePieceWithMove(int x, int y, Piece pieceMoving, int movetypeUsed) {
        int attackTypeTargetedBy = Piece.allMoveTypes[0][movetypeUsed].moveType;
        //if the location on the board is unoccupied, this method does nothing
        if (boardState[x][y] != 0) {
//the piece is set to currently being targeted, and the timesTargeted integer is increased
            allPiecesOnBoard.get(piecesOnBoard[x][y]).justTargeted = true;
            allPiecesOnBoard.get(piecesOnBoard[x][y]).timesTargeted++;
//what movetype and what piece are targeting the piece are set
            allPiecesOnBoard.get(piecesOnBoard[x][y]).moveTypeTargetedBy = attackTypeTargetedBy;
            allPiecesOnBoard.get(piecesOnBoard[x][y]).pieceTargetedBy = pieceMoving;
//based on what the above variables are, some abilities may be triggered
            testAndExecuteAbilities();
//piece is set to no longer being targeted
            allPiecesOnBoard.get(piecesOnBoard[x][y]).justTargeted = false;
//test if the piece is armoured
            boolean armoured = testIfPieceIsArmouredAgainstMoves(allPiecesOnBoard.get(piecesOnBoard[x][y]));
//if the piece is not armoured, it will be captured
            if (!armoured) {
                //piece is set as just being captured, and abilities may now occur, specifically on death abilities

                allPiecesOnBoard.get(piecesOnBoard[x][y]).justCaptured = true;
                testAndExecuteAbilities();
                allPiecesOnBoard.get(piecesOnBoard[x][y]).justCaptured = false;
                allPiecesOnBoard.get(piecesOnBoard[x][y]).captured = true;
                //the piece moving is set to having captured a piece, abilities may occur, specifically on kill abilities
                pieceMoving.piecesCaptured++;
                pieceMoving.justGotCapture = true;
                testAndExecuteAbilities();
                pieceMoving.justGotCapture = false;

//morale values updated based on what piece was captured
                if (allPiecesOnBoard.get(piecesOnBoard[x][y]).isWhite) {
                    moraleTotals[0] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[x][y]).moralePenalty;
                } else {
                    moraleTotals[1] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[x][y]).moralePenalty;
                }
//the square is set to empty if the piece was captured
                boardState[x][y] = 0;
                piecesOnBoard[x][y] = -1;
            }

        }
    }

    void capturePieceWithAbility(int x, int y, Piece pieceWhoHasAbility, int abilityUsed) {
        //if the location on the board is unoccupied, this method does nothing
        if (boardState[x][y] != 0) {
            if (!allPiecesOnBoard.get(piecesOnBoard[x][y]).protectedFromAbilities) {
                //piece set to being targeted by an ability, abilities that trigger when being targeted may occur
                allPiecesOnBoard.get(piecesOnBoard[x][y]).justTargeted = true;
                // allPiecesOnBoard.get(piecesOnBoard[x][y]).timesTargeted++;
                testAndExecuteAbilities();
                allPiecesOnBoard.get(piecesOnBoard[x][y]).justTargeted = false;

//test if the piece is armoured against abilities
                boolean armoured = testIfPieceIsArmouredAgainstAbilities(allPiecesOnBoard.get(piecesOnBoard[x][y]));
//if not it is captured
                if (!armoured) {
                    //piece set to just being captured, on death abilities may occur
                    allPiecesOnBoard.get(piecesOnBoard[x][y]).captured = true;
                    allPiecesOnBoard.get(piecesOnBoard[x][y]).justCaptured = true;
                    testAndExecuteAbilities();
                    allPiecesOnBoard.get(piecesOnBoard[x][y]).justCaptured = false;

                    //piece who used ability has number of captures increased
                    pieceWhoHasAbility.piecesCaptured++;
                    pieceWhoHasAbility.justGotCapture = true;
                    testAndExecuteAbilities();
                    pieceWhoHasAbility.justGotCapture = false;

                    //morale values updated based on the piece that died
                    if (allPiecesOnBoard.get(piecesOnBoard[x][y]).isWhite) {
                        moraleTotals[0] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[x][y]).moralePenalty;
                    } else {
                        moraleTotals[1] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[x][y]).moralePenalty;
                    }
                    //square on board set to unoccupied
                    boardState[x][y] = 0;
                    piecesOnBoard[x][y] = -1;
                }
            }
        }
    }

    void capturePieceWithStatus(Piece pieceDying) {
        //piece set to being captured, on death effects may occur
        allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).captured = true;
        allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).justCaptured = true;
        testAndExecuteAbilities();
        allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).justCaptured = false;
        //morale totals updated based on which piece is dying
        if (allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).isWhite) {
            moraleTotals[0] -= allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).moralePenalty;
        } else {
            moraleTotals[1] -= allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).moraleCost + allPiecesOnBoard.get(piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation]).moralePenalty;
        }
        //board position set to unoccupied
        boardState[pieceDying.xLocation][pieceDying.yLocation] = 0;
        piecesOnBoard[pieceDying.xLocation][pieceDying.yLocation] = -1;
    }

    void movePiece(int currentx, int currenty, int newx, int newy) {
        //place the piece on the new square
        piecesOnBoard[newx][newy] = piecesOnBoard[currentx][currenty];
        boardState[newx][newy] = boardState[currentx][currenty];
        allPiecesOnBoard.get(piecesOnBoard[currentx][currenty]).setLocation(newx, newy);
        //remove the piece from the old square
        boardState[currentx][currenty] = 0;
        piecesOnBoard[currentx][currenty] = -1;

    }

    void swapPiece(int currentx, int currenty, int newx, int newy) {
        int tempPieceIndex = piecesOnBoard[newx][newy];
        int tempPieceColour = boardState[newx][newy];

        piecesOnBoard[newx][newy] = piecesOnBoard[currentx][currenty];
        boardState[newx][newy] = boardState[currentx][currenty];
        allPiecesOnBoard.get(piecesOnBoard[currentx][currenty]).setLocation(newx, newy);

        piecesOnBoard[currentx][currenty] = tempPieceIndex;
        boardState[currentx][currenty] = tempPieceColour;
        allPiecesOnBoard.get(tempPieceIndex).setLocation(currentx, currenty);

    }

    //summon a piece on a location
    private void summonPiece(String pieceName, int x, int y, boolean isWhite) {
        if (boardState[x][y] == 0) {
            //this will hold new piece's index in the array of pieces
            int newIndex = allPiecesOnBoard.size();
            //add a new piece object to the array
            allPiecesOnBoard.add(new Piece(pieceName, isWhite));
            //set it's location
            allPiecesOnBoard.get(newIndex).setLocation(x, y);
            piecesOnBoard[x][y] = newIndex;

            //update morale values
            if (isWhite) {
                moraleTotals[0] += allPiecesOnBoard.get(newIndex).moraleCost;
                boardState[x][y] = 1;
            } else {
                moraleTotals[1] += allPiecesOnBoard.get(newIndex).moraleCost;
                boardState[x][y] = 2;
            }
        }
    }

    private void transformPiece(String newPieceName, int x, int y, boolean isWhite) {
        //subtract morale of the piece that is about to be transformed
        if (allPiecesOnBoard.get(piecesOnBoard[x][y]).isWhite) {
            moraleTotals[0] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost;
        } else {
            moraleTotals[1] -= allPiecesOnBoard.get(piecesOnBoard[x][y]).moraleCost;
        }
        //set the piece that is being transform to being captured
        allPiecesOnBoard.get(piecesOnBoard[x][y]).captured = true;
        //find the index of the new piece (it will be equal to the size of the allPiecesOnBoard array)
        int newPieceIndex = allPiecesOnBoard.size();
        //add a new piece to the array
        allPiecesOnBoard.add(new Piece(newPieceName, isWhite));
        //depending on it's colour set the board and the new morale totals
        if (isWhite) {
            boardState[x][y] = 1;
            moraleTotals[0] += allPiecesOnBoard.get(newPieceIndex).moraleCost;
        } else {
            boardState[x][y] = 2;
            moraleTotals[1] += allPiecesOnBoard.get(newPieceIndex).moraleCost;
        }
        //set the location on the board to being occupied by the new piece
        piecesOnBoard[x][y] = newPieceIndex;
        //set the new piece's location
        allPiecesOnBoard.get(newPieceIndex).setLocation(x, y);
    }

    //find how many ally pieces any piece has adjacent to them
    int findNumberOfAlliesAdjacentTo(int xLoc, int yLoc, boolean isWhite) {
        int numberOfAdjacentAllies = 0;
//player, if this is 1 it is white pieces we are looking for, otherwise if it's 2 it is black pieces
        int player = 0;
        if (isWhite) {
            player = 1;
        } else {
            player = 2;
        }
//loop through all adjacent locations to a square
        for (int x = xLoc - 1; x <= xLoc + 1; x++) {
            for (int y = yLoc - 1; y <= yLoc + 1; y++) {
                //disclude the square the piece is on
                if (x != xLoc || y != yLoc) {
                    //disclude locations not on the board
                    if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                        //if the boardstate of that location is equals to the player (the board has an ally piece on it)
                        if (boardState[x][y] == player) {
                            //increase by 1
                            numberOfAdjacentAllies++;
                        }
                    }
                }
            }
        }

        return numberOfAdjacentAllies;
    }

    int findNumberOfPiecesAdjacentTo(int xLoc, int yLoc) {
        int numOfAdjacentPieces = 0;
        for (int x = xLoc - 1; x <= xLoc + 1; x++) {
            for (int y = yLoc - 1; y <= yLoc + 1; y++) {
                //disclude the square the piece is on
                if (x != xLoc || y != yLoc) {
                    //disclude locations not on the board
                    if (x >= 0 && x < 8 && y >= 0 && y < 8) {
                        //if the boardstate of that location is equals to the player (the board has an ally piece on it)
                        if (boardState[x][y] != 0) {
                            //increase by 1
                            numOfAdjacentPieces++;
                        }
                    }
                }
            }
        }

        return numOfAdjacentPieces;
    }

    //when a piece is attacked, this method is called to see if the piece should be protected
    boolean testIfPieceIsArmouredAgainstMoves(Piece piece) {
        boolean armoured = false;
        //loop through all statuses,
        for (int x = 0; x != piece.allStatuses.size(); x++) {
            //currently statuses 8/9 give protection against moves
            if (piece.allStatuses.get(x).index == 8 || piece.allStatuses.get(x).index == 9) {
                //if the status has more effect length than 0, the piece is set to armoured, and will not be captured
                if (piece.allStatuses.get(x).statusEffectLength > 0) {
                    armoured = true;
                    //reduce the effect length, as the armour is soon to block a movetype
                    piece.allStatuses.get(x).statusEffectLength--;
                }
            }
        }
        return armoured;
    }

    //when a piece is hit with an ability, this method is called to see if the piece should be protected
    boolean testIfPieceIsArmouredAgainstAbilities(Piece piece) {
        boolean armoured = false;
        //loop through all statuses
        for (int x = 0; x != piece.allStatuses.size(); x++) {
            //currently status 9 provides protection from abilities
            if (piece.allStatuses.get(x).index == 9) {
                //if the status has length greated than 0, the piece is set to be armoured against abilities
                if (piece.allStatuses.get(x).statusEffectLength > 0) {
                    armoured = true;
                    //status length decreased by 1, as the armour is soon to block an ability
                    piece.allStatuses.get(x).statusEffectLength--;
                }
            }
        }
        return armoured;
    }

    //check if a move is blocked by another piece
    private boolean checkIfPieceIsBlocked(int moveTargetx, int moveTargety, int pieceLocx, int pieceLocy) {
        boolean blocked = true;
        //find the difference between the target and the pieces location
        //for example, a piece on square 0,0 trying to move to 3,3 has xDiff and yDiff =3
        int xDiff = moveTargetx - pieceLocx;
        int yDiff = moveTargety - pieceLocy;

        if (xDiff == 0 || yDiff == 0 || xDiff == yDiff || xDiff == -yDiff) {
            blocked = false;
        }

        //values decreased by 1, if a piece has a xDiff of 3, that means there are only 2 squares
        //that could potentially block the piece from moving
        xDiff = decreaseAbsValueByOne(xDiff);
        yDiff = decreaseAbsValueByOne(yDiff);
        //loop through "xDiff" times until xDiff=0
        //every loop decreases xDiff and yDiff by 1, unless they are 0, in which case they stay the same
        if (xDiff != 0) {
            for (int x = xDiff; x != 0; x = decreaseAbsValueByOne(x), yDiff = decreaseAbsValueByOne(yDiff)) {
                //if the location on the board is occupied, blocked is set true
                if (boardState[pieceLocx + x][pieceLocy + yDiff] != 0) {
                    blocked = true;
                    break;
                }
            }
        } else {
            //or if, xDiff=0, loop through yDiff times
            for (int y = yDiff; y != 0; y = decreaseAbsValueByOne(y), xDiff = decreaseAbsValueByOne(xDiff)) {
                //if the location on the board is occupied, blocked is set true
                if (boardState[pieceLocx + xDiff][pieceLocy + y] != 0) {
                    blocked = true;
                    break;
                }
            }
        }

        return blocked;
    }

    //check if cannon is blocked by another piece
    private boolean checkIfPieceIsBlockedCannonVersion(int moveTargetx, int moveTargety, int pieceLocx, int pieceLocy) {
        //for cannon, there must be 1 piece on the path to target an enemy, and 0 or 1 to target an empty square
        int piecesInPath = 0;
        boolean blocked = true;

        //find the difference between the target and the pieces location
        //for example, a piece on square 0,0 trying to move to 3,3 has xDiff and yDiff =3
        int xDiff = moveTargetx - pieceLocx;
        int yDiff = moveTargety - pieceLocy;

        if (xDiff == 0 || yDiff == 0 || xDiff == yDiff || xDiff == -yDiff) {
            blocked = false;
        }

        //values decreased by 1, if a piece has a xDiff of 3, that means there are only 2 squares
        //that could potentially block the piece from moving
        xDiff = decreaseAbsValueByOne(xDiff);
        yDiff = decreaseAbsValueByOne(yDiff);
        //loop through "xDiff" times until xDiff=0
        //every loop decreases xDiff and yDiff by 1, unless they are 0, in which case they stay the same
        if (xDiff != 0) {
            for (int x = xDiff; x != 0; x = decreaseAbsValueByOne(x), yDiff = decreaseAbsValueByOne(yDiff)) {
                //if the location on the board is occupied, increase pieceInPath
                if (boardState[pieceLocx + x][pieceLocy + yDiff] != 0) {
                    piecesInPath++;

                }
            }
        } else {
            //or if, xDiff=0, loop through yDiff times
            for (int y = yDiff; y != 0; y = decreaseAbsValueByOne(y), xDiff = decreaseAbsValueByOne(xDiff)) {
                //if the location on the board is occupied, increase pieceInPath
                if (boardState[pieceLocx + xDiff][pieceLocy + y] != 0) {
                    piecesInPath++;
                }
            }
        }


//if the target is an empty square, as long as no more than 1 piece is on the path, the cannon is not blocked
        if (boardState[moveTargetx][moveTargety] == 0) {

            if (piecesInPath > 1) {
                blocked = true;

            }
        } else {
            //if the target is not an empty square, there must be exactly one piece on the path
            if (piecesInPath != 1) {

                blocked = true;
            }

        }
        return blocked;
    }

    //decrease the absolute value of any integer by 1, if it is 0, it stays at 0
    private int decreaseAbsValueByOne(int x) {
        if (x > 0) {
            x--;
        }
        if (x < 0) {
            x++;
        }
        return x;
    }

    private int[] findLocationOnBoard(int movesetLocX, int movesetLocY, int pieceLocX, int pieceLocY) {
        int[] location = new int[2];
        location[0] = movesetLocX - 7 + pieceLocX;
        location[1] = movesetLocY - 7 + pieceLocY;
        return location;
    }

    //find which square on the board the cursor is on
    public int[] findSquareMouseIsOn(int mousex, int mousey) {
        //reset these values, these will be the values of the mouse's location on screen
        int xLoc = -1;
        int yLoc = -1;
        //if the cursor is within the bounds of the board
        if (mousex > boardPosX && mousex < boardPosX + boardSize) {
            for (int x = 0; x != 8; x++) {
                //if the mouse position on screen is greater than the square's leftmost point
                if (mousex > boardPosX + (int) (boardSize / 8 * x)) {
                    xLoc++;
                } else {
                    break;
                }
            }
        }
        //if the cursor is within the bounds of the board
        if (mousey > boardPosY && mousey < boardPosY + boardSize) {
            for (int x = 0; x != 8; x++) {
                //if the mouse position on screen is greater than the square's bottommost point
                if (mousey > boardPosY + (int) (boardSize / 8 * x)) {
                    yLoc++;
                } else {
                    break;
                }
            }

        }
        //set the loc array to the locations found
        int[] luc = new int[2];
        if (xLoc != -1 && yLoc != -1) {
            if (flipBoard) {
                luc[0] = 7 - xLoc;
                luc[1] = 7 - yLoc;
            } else {
                luc[0] = xLoc;
                luc[1] = yLoc;
            }
        } else {
            luc[0] = -1;
            luc[1] = -1;
        }
        return luc;
    }

    //takes a location on the board (0-7),(0-7), and returns the screen coordinates in pixels for that square
    public int[] findScreenCoordinatesOfSquare(int squareX, int squareY) { //TODO FIX THIS SO IT WORKS WITH RESIZING
        int[] pixelCoordinates = new int[2];
        pixelCoordinates[0] = (int) ((squareX * boardSize / 8) + (boardSize / 16) + boardPosX);
        pixelCoordinates[1] = (int) ((squareY * boardSize / 8) + (boardSize / 16));
        return pixelCoordinates;
    }

    //loop through all pieces and set all their possible moves as invalid
    private void setAllMovesInvalid() {
        for (int a = 0; a != allPiecesOnBoard.size(); a++) {
            for (int x = 0; x != 15; x++) {
                for (int y = 0; y != 15; y++) {
                    allPiecesOnBoard.get(a).validMoves[x][y] = false;
                }
            }
        }
    }

    //loop through the pieces array and see if the piece named "king" has been captured
    private void checkIfKingLives() {
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            //if the piece is named "king"
            if (allPiecesOnBoard.get(x).name.equalsIgnoreCase("king")) {
                //if the king is captured
                if (allPiecesOnBoard.get(x).captured) {
                    //if the king is of the colour of the player whos turn just started
                    if (playerTurn == 1 && allPiecesOnBoard.get(x).isWhite) {
                        //reduce that players morale total
                        //   moraleTotals[0]-=15;
                    }
                    if (playerTurn == 2 && !allPiecesOnBoard.get(x).isWhite) {
                        //  moraleTotals[1]-=15;
                    }
                }
            }
        }
    }

    //if the morale of a player is 0, set the gamestate booleans according to which player hit 0 morale
    private void checkIfGameOver() {
        if (moraleTotals[0] <= 0) {
            moraleTotals[0] = 0;
            blackWins = true;
            gameOver = true;
        }
        if (moraleTotals[1] <= 0) {
            moraleTotals[1] = 0;
            gameOver = true;
            whiteWins = true;
        }

        if (gameOver) {
            if (serverComms != null) {
                serverComms.sendMessageToServer("RANKED_MATCH_OVER\n");
            }
        }

    }

    String getCurrentlySelectedPieceName() {
        String name;
        name = allPiecesOnBoard.get(pieceLastSelected).name;
        return name;
    }

    String getCurrentlySelectedPieceDescription() {
        String description;
        description = allPiecesOnBoard.get(pieceLastSelected).abilityDescription;
        return description;
    }

    String getCurrentlySelectedPieceLore() {
        String lore;
        lore = allPiecesOnBoard.get(pieceLastSelected).loreWriting;
        return lore;
    }


    private void drawAll(SpriteBatch batch, MouseVars mouseVars) {
        //draw board Sprite
        sprite.draw(batch);

        batch.end();
        if (System.currentTimeMillis() - timePieceLastSelected <= 1500) {
            if (pieceSelected) {
                drawMovesOnBoard(batch, mouseVars, allPiecesOnBoard.get(selectedPiece).moveset, allPiecesOnBoard.get(selectedPiece).validMoves);
            } else if (loc[0] >= 0 && loc[1] >= 0 && loc[0] <= 7 && loc[1] <= 7 && piecesOnBoard[loc[0]][loc[1]] != -1 && hasMouseChangedLocationsYet) {
                drawMovesOnBoard(batch, mouseVars, allPiecesOnBoard.get(piecesOnBoard[loc[0]][loc[1]]).moveset, allPiecesOnBoard.get(piecesOnBoard[loc[0]][loc[1]]).validMoves);
            }
        }
        batch.begin();
        //drawReticle(batch, mouseVars);

        drawText(batch);

        drawPieces(batch, mouseVars);
    }

    /*//indicate which piece is targeted
    //draw reticle, there has got to be a better word for this
    private void drawReticle(SpriteBatch batch, MouseVars mouseVars) {
        int[] loc = findSquareMouseIsOn(mouseVars.mousePosx, mouseVars.mousePosy);

        float xPosOfTarget = (float) (loc[0] * boardSize / 8 + boardPosX);
        float yPosOfTarget = (float) (loc[1] * boardSize / 8 + boardPosY);

        int x = selectedPieceLocx;
        int y = selectedPieceLocy;
        //for movesets
        int msX = loc[0] - selectedPieceLocx + 7;
        int msY = loc[1] - selectedPieceLocy + 7;

        boolean safe = (x >= 0 && x <= 7 && y >= 0 && y <= 7 && loc[0] >= 0 && loc[0] <= 7 && loc[1] >= 0 && loc[1] <= 7);

        //find what reticle type to draw
        boolean isMove = false;
        boolean valid = false;
        boolean blocked = false;
        //int moveType = 0;

        //set reticle types based on these
        int retType = 0;
        if (safe) {
            isMove = allPiecesOnBoard.get(selectedPiece).moveset[msX][msY] != 0;
            valid = allPiecesOnBoard.get(selectedPiece).validMoves[msX][msY];
            blocked = checkIfPieceIsBlocked(loc[0], loc[1], x, y);

            if (valid) {
                retType = 1;
            } else if (blocked && isMove) {
                retType = 2;
            } else if (isMove) {
                retType = 3;
            } else if (loc[0] == selectedPieceLocx && loc[1] == selectedPieceLocy) {
                retType = 4;
            }
            //find movetype to draw additional reticle stuff
            //moveType = allPiecesOnBoard.get(selectedPiece).moveset[msX][msY];
            //remove unblockable component
            //moveType %= 1000;
        }
        switch (retType) {
            case 1:
                batch.draw(reticleTexture, xPosOfTarget, yPosOfTarget, (float) boardSize / 8, (float) boardSize / 8);
                break;
            case 2:
                batch.draw(reticleTextureBlocked, xPosOfTarget, yPosOfTarget, (float) boardSize / 8, (float) boardSize / 8);
                break;
            case 3:
                batch.draw(reticleTextureBlocked, xPosOfTarget, yPosOfTarget, (float) boardSize / 8, (float) boardSize / 8);
                break;
            *//*case 4:
                batch.draw(reticleTextureSelected, xPosOfTarget, yPosOfTarget, (float) boardSize/8, (float) boardSize/8);
                break;*//*
        }
        //TODO: draw icons with reticle for movetypes
        //switch(moveType){ }

    }*/

    private void drawText(SpriteBatch batch) {
//start a fresh batch
        batch.end();
        batch.begin();
//prepare fond shader
        Shaders.prepareDistanceFieldShader();
        batch.setShader(Shaders.distanceFieldShader);
//draw text
        drawMoraleValues(batch);
        drawGameResult(batch);
        drawMoveSet(batch);
//end the batch and start a new one for the draw methods that occur after this one
        batch.end();
        batch.begin();
//return to default shader
        batch.setShader(Shaders.defaultShader);
    }

    private void drawMoraleValues(SpriteBatch batch) {
        String draw = "";
        //draw the morale totals on screen, if flipBoard is true, draw the morale totals in opposite locations
        //so the morale totals are always next to the proper army
        if (flipBoard == false) {
            draw = "Morale " + String.valueOf(moraleTotals[0]);
            font.draw(batch, draw, 950, 50);
            draw = "Morale " + String.valueOf(moraleTotals[1]);
            font.draw(batch, draw, 950, 580);
        } else {
            draw = "Morale " + String.valueOf(moraleTotals[0]);
            font.draw(batch, draw, 950, 580);
            draw = "Morale " + String.valueOf(moraleTotals[1]);
            font.draw(batch, draw, 950, 50);
        }
    }

    private void drawGameResult(SpriteBatch batch) {
        String draw = "";
        //if the game is over, draw a message saying who won
        if (gameOver) {
            if (blackWins) {
                draw = "Black Wins!";
            }
            if (whiteWins) {
                draw = "White Wins!";
            }
            font.draw(batch, draw, 1000, 200);
        }
    }

    private void drawMoveSet(SpriteBatch batch) { //TODO Replace the text with moveset rectangles
        //this draws the moveset of the piece to the screen
        String line = "";
        String spacing = "  ";
        if (flipBoard == false) {
            for (int y = 14; y >= 0; y--) {
                for (int x = 14; x >= 0; x--) {
                    if (x == 7 && y == 7) {
                        line += "x  ";
                    } else {
                        if (String.valueOf(Piece.moveTypeIndexes[allPiecesOnBoard.get(pieceLastSelected).staticMoveset[x][y] % 1000]).length() == 2) {
                            spacing = " ";
                        } else {
                            spacing = "  ";
                        }
                        line += String.valueOf(Piece.moveTypeIndexes[allPiecesOnBoard.get(pieceLastSelected).staticMoveset[x][y] % 1000]) + spacing;
                    }
                }
                font.draw(batch, line, 20, 600 - 15 * (14 - y));
                line = "";
            }
        } else {
            for (int y = 14; y >= 0; y--) {
                for (int x = 14; x >= 0; x--) {

                    if (x == 7 && y == 7) {
                        line += "x  ";
                    } else {
                        if (String.valueOf(Piece.moveTypeIndexes[allPiecesOnBoard.get(pieceLastSelected).staticMoveset[x][14 - y] % 1000]).length() == 2) {
                            spacing = " ";
                        } else {
                            spacing = "  ";
                        }
                        line += String.valueOf(Piece.moveTypeIndexes[allPiecesOnBoard.get(pieceLastSelected).staticMoveset[x][14 - y] % 1000]) + spacing;
                    }
                }
                font.draw(batch, line, 20, 600 - 15 * (14 - y));
                line = "";
            }
        }
    }

    private void drawMovesOnBoard(SpriteBatch batch, MouseVars mouseVars, int[][] moves, boolean[][] validMoves) {
        int state = -1;
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                int msX, msY;
                if (pieceSelected) {
                    msX = x - selectedPieceLocx + 7;
                    msY = y - selectedPieceLocy + 7;
                } else {
                    msX = x - loc[0] + 7;
                    msY = y - loc[1] + 7;
                }
                int type = moves[msX][msY];

                if (type != 0) {
                    if (validMoves[msX][msY]) { // if the move is a valid one and is a move
                        if (loc[0] == x && loc[1] == y) { // if the move is a selected one
                            state = 3;
                        } else { // if the move is valid but not selected
                            state = 1;
                        }
                    } else if (!validMoves[msX][msY]) { // if the move is not valid
                        if (loc[0] == x && loc[1] == y) { // if the move is a selected one
                            state = 2;
                        } else { // if the move is invalid and not selected
                            state = 0;
                        }
                    }
                    float xLoc = (float) (x * boardSize / 8 + boardPosX);
                    float yLoc = (float) (y * boardSize / 8 + boardPosY);
                    drawMoveOnBoard(shapeRenderer, xLoc, yLoc, state, type);
                }
            }
        }
        shapeRenderer.dispose();

    }

    private void drawMoveOnBoard(ShapeRenderer shapeRenderer, float xLoc, float yLoc, int state, int movetype) {
        float size = (float) (boardSize / 12);
        float offset = (float) (boardSize / 48);
        float alpha = 0;
        if(System.currentTimeMillis() - timePieceLastSelected <= 500)
            alpha = 1;
        else{
            alpha = (float) (1500 - System.currentTimeMillis() + timePieceLastSelected) / (float) 1000;
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //String errorMessage; TODO tutorialization error message thing
        if (state == 0) { // move is not valid
            //TODO: Replace this with move associated color
            shapeRenderer.setColor(1, 0, 0, alpha / 2);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, alpha / 4);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            //batch.draw(reticleTextureBlocked, xLoc + offset, yLoc + offset, size, size);
        } else if (state == 1) { // move is valid
            shapeRenderer.setColor(0, 0, 0, alpha / 2);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, alpha / 4);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            //batch.draw(reticleTexture, xLoc + offset, yLoc + offset, size, size);
            //batch.draw(symbol,samethingasabove);
        } else if (state == 2) { //move is selected but invalid
            size = (float) boardSize / 16;
            offset = (float) boardSize / 32;
            shapeRenderer.setColor(1, 0, 0, alpha);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(1, 0, 0, 3 * alpha / 4);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            //batch.draw(reticleTextureSelected, xLoc + offset, yLoc + offset, size, size);
        } else if (state == 3) { // move is selected and valid
            size = (float) boardSize / 8;
            offset = 0;
            shapeRenderer.setColor(0, 0, 0, alpha);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 0, 3 * alpha / 4);
            shapeRenderer.rect(xLoc + offset, yLoc + offset, size, size);
            //batch.draw(reticleTexture, xLoc + offset, yLoc + offset, size, size);
        }
        shapeRenderer.end();
    }

    private void drawPieces(SpriteBatch batch, MouseVars mouseVars) {
        //loop through all pieces that have not been captured and draw them
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            if (!allPiecesOnBoard.get(x).captured) {
                int xPosOfPiece;
                int yPosOfPiece;
                if (flipBoard) {
                    xPosOfPiece = (int) ((7 - allPiecesOnBoard.get(x).xLocation) * boardSize / 8 + boardSize / 16) + boardPosX;
                    yPosOfPiece = (int) ((7 - allPiecesOnBoard.get(x).yLocation) * boardSize / 8 + boardSize / 16) + boardPosY;
                } else {
                    xPosOfPiece = (int) (allPiecesOnBoard.get(x).xLocation * boardSize / 8 + boardSize / 16) + boardPosX;
                    yPosOfPiece = (int) (allPiecesOnBoard.get(x).yLocation * boardSize / 8 + boardSize / 16) + boardPosY;
                }
                if (!allPiecesOnBoard.get(x).selected) {
                    allPiecesOnBoard.get(x).draw(batch, xPosOfPiece, yPosOfPiece);
                } else {
                    allPiecesOnBoard.get(x).drawSpecificLoc(batch, (int) (72 * 1.1), mouseVars.mousePosx, mouseVars.mousePosy);
                }
            }
        }
    }

    private void loadGraphics() {
        boardImage = GraphicsUtils.loadTexture("Board.png");
        reticleTexture = GraphicsUtils.loadTexture("reticule.png");
        reticleTextureBlocked = GraphicsUtils.loadTexture("reticuleBlocked.png");
        reticleTextureSelected = GraphicsUtils.loadTexture("reticuleSelected.png");
        sprite = new Sprite(boardImage);
        sprite.setSize(boardSize, boardSize);
        sprite.setCenter(640, 309);
    }

    void reloadGraphics() {
        deleteGraphics();
        loadGraphics();
        initFont();
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            allPiecesOnBoard.get(x).setImage();
        }
    }


    //delete graphics objects, used when the game is being reset to avoid leaking memory
    void deleteGraphics() {
        font.dispose();
        boardImage.dispose();
        reticleTexture.dispose();
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            allPiecesOnBoard.get(x).deleteGraphics();
        }
    }

    void unselectAll() {
        pieceSelected = false;
        for (int x = 0; x != allPiecesOnBoard.size(); x++) {
            allPiecesOnBoard.get(x).selected = false;
        }
    }
}


