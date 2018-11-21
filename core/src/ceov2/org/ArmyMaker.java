package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.ArrayList;

//this class allows players to construct their own armies using all pieces in the game
//in the future this will only allow units that a player's collection contains to be used
public class ArmyMaker {
    //the armyMaker menu
    Menu menu;
    boolean exitToMainMenu;
    //graphics objects declared
    Texture boardImage;
    Sprite boardSprite;
    BitmapFont font;
    //controls what pieces you currently have access to, the first 10 pieces are on page 1, the
    //next 10 are one page 2 etc.
    //how many columns of pieces are shown in one page
    int pageX = 5;
    //how many rows of pieces are shown in one page
    int pageY = 2;
    //when we have many pieces, not all pieces can be shown on one screen
    //this allows the user to switch pages to view more pieces
    int page = 1;
    int armyBeingEdited = 1;
    //the morale of the current army, can never go above 500
    int moraleTotal;
    final int moraleAllowed = 500;
    String army;
    //this is written to the screen, if the player tries to save an invalid army it tells them why
    //if they saved a valid army it tells them it was saved
    String errorMessage = "";
    String successMessage = "";
    //String containing all the names of pieces in the army
    String[][] armyPiece;
    //arraylist containing all pieces in the game, in the future this will only contain
    //pieces in the player's collection, pieces in this array can be freely added to the army
    ArrayList<Piece> allPieces = new ArrayList<Piece>();
    //unimportant
    int[][] moveset = new int[15][15];

    //the location of the mouse on the grid of the army (2x8 grid)
    int[] mouseLocOnArmy = new int[2];
    //location of the mouse on the grid of the collection (pageX x pageY grid)
    int[] mouseLocOnCollection = new int[2];
    //if a piece in the army is selected
    boolean pieceInArmySelected = false;
    //the location of the piece in the army that is selected
    int selectedPieceLocation[] = new int[2];
    //if a piece in the collection is selected
    boolean pieceInCollectionSelected = false;

    //the last piece selected by the user, show stuff based on what this piece is
    int lastPieceUserSelected = 0;

    //the name of the piece in the collection that is selected
    String selectedCollectionPiece = "";

    //constructor, called once when the player enters the armyMaker
    public ArmyMaker(InputMultiplexer inputMultiplexer) {
        armyPiece = new String[2][8];

        //load the menu for army making
        loadArmyMakingMenu(inputMultiplexer);

        Piece.InitAllMoveTypes();
        //load all pieces in the game so they may be added to the army and drawn
        loadAllPieces();
//load the current army file
        loadCurrentArmy();
//load graphics objects
        loadTextures();
//calculate the morale of the current army
        calculateMorale();
    }

    //This constructor is not used for when the player enters the armyMaker, but is used when the player enters the
//level editor, this constructor calls only general methods
    public ArmyMaker() {
        armyPiece = new String[8][8];

        Piece.InitAllMoveTypes();
        //load all pieces in the game so they may be added to the army and drawn
        loadAllPieces();
        //load graphics objects
        loadTextures();
    }

    //called every run through of the main loop
    public void run(SpriteBatch batch, MouseVars mouseVars) {
        //update and draw the menu
        updateMenuObjects();
        menu.stage.getViewport().apply();
        menu.stage.draw();

        //find the mouse's location on the army grid
        mouseLocOnArmy = findSquareMouseIsOn(mouseVars.mousePosx, mouseVars.mousePosy, 60, 400, 50, 8, 2);
        //perform logic based on the mouse location on the army grid and the mouse variables
        processMouseInputArmy(mouseVars, mouseLocOnArmy, 8, 2);
        //find the mouse's location on the collection grid
        mouseLocOnCollection = findSquareMouseIsOn(mouseVars.mousePosx, mouseVars.mousePosy, 60, 470, 360, pageX, pageY);
        //perform logic based on the mouse's locations on both grids and the mouse variables
        processMouseInputCollection(mouseVars, mouseLocOnCollection, mouseLocOnArmy, 8, 2);
        //draw everything
        batch.begin();
        drawAll(batch, mouseVars);
        batch.end();
    }

    void loadArmyMakingMenu(InputMultiplexer inputMultiplexer) {
        menu = new Menu(inputMultiplexer);
        ClickListener clickListener;


        //return to main menu button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                exitToMainMenu = true;
            }
        };
        menu.addButton("Return to Main Menu", 200, 30, 100, 100, clickListener);


        //increase page button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (page < 10) {
                    page++;
                }
            }
        };
        menu.addButton("increase page", 150, 30, 500, 500, clickListener);


        //decrease page button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (page > 1) {
                    page--;
                }
            }
        };
        menu.addButton("decrease page", 150, 30, 700, 500, clickListener);


        //save army button

        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSetup();
            }
        };
        menu.addButton("Save Army", 150, 30, 900, 130, clickListener);


        //change to be editing army 1 button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                armyBeingEdited = 1;
                loadCurrentArmy();
            }
        };
        menu.addButton("army 1", 150, 30, 400, 15, clickListener);


        //change to be editing army two button
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                armyBeingEdited = 2;
                loadCurrentArmy();
            }
        };
        menu.addButton("army 2", 150, 30, 570, 15, clickListener);


        //create two text areas, while in loop we will update these to have helpful information about each piece on them
        menu.addTextArea(300, 100, 0, 250);
        menu.addTextArea(300, 100, 0, 140);
    }

    void updateMenuObjects() {
        updateTextAreas();
    }

    void updateTextAreas() {
        menu.allTextAreas.get(0).setText(allPieces.get(lastPieceUserSelected).name + "\n" + allPieces.get(lastPieceUserSelected).abilityDescription);
        menu.allTextAreas.get(1).setText(allPieces.get(lastPieceUserSelected).loreWriting);
    }

    //loops through all pieces in the army and calculates the morale the army has used
    void calculateMorale() {
        moraleTotal = 0;
        for (int y = 0; y != 2; y++) {
            for (int x = 0; x != 8; x++) {
                for (int a = 0; a != allPieces.size(); a++) {
                    if (allPieces.get(a).name.equals(armyPiece[y][x])) {
                        moraleTotal += allPieces.get(a).moraleCost;
                    }
                }
            }
        }
    }

    //loads graphics objects
    void loadTextures() {
        //load font texture
        Texture texture = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        //create font using above texture
        font = new BitmapFont(Gdx.files.internal("Fonts\\ArialDistanceField2.fnt"), new TextureRegion(texture), false);
        font.setColor(Color.BLACK);

        //create board texture
        boardImage = GraphicsUtils.loadTexture("Board.png");
        TextureRegion boardRegion = new TextureRegion(boardImage, 0, (int) (boardImage.getHeight() * 0.25), boardImage.getWidth(), (int) (boardImage.getHeight() * 0.25));
        boardSprite = new Sprite(boardRegion);
        boardSprite.setSize(480, 120);
        boardSprite.setCenter(640, 110);
    }

    //load all pieces currently in the game
    void loadAllPieces() {
        for (int x = 0; x != Piece.pieceCounter; x++) {
            allPieces.add(new Piece(Piece.allPieces[x], true));
        }
    }

    //load the army file of the player
    void loadCurrentArmy() {
        successMessage = "";
        errorMessage = "";
        //load the string from the file, the String will look something like this
        //rook,knight,pawn,pawn, (etc for all 16 pieces in an army)
        army = Gdx.files.internal("UserFiles\\armies\\army" + String.valueOf(armyBeingEdited) + ".txt").readString();
        String[] separated = new String[16];
        //separate the names of pieces into an array, get rid of the commas
        for (int x = 0; x != 16; x++) {
            separated = army.split(",");
        }
        //load the piece names into a 2x8 array
        int counter = 0;
        for (int y = 0; y != 2; y++) {
            for (int x = 0; x != 8; x++) {
                armyPiece[y][x] = separated[counter++];
            }
        }
        calculateMorale();
    }

    //draw all graphics
    void drawAll(SpriteBatch batch, MouseVars mouseVars) {
        drawBoard(batch);
        //draw text
        drawText(batch);
        //draw the army
        drawArmyPieces(batch, mouseVars);
        //draw the collection
        drawCollectionPieces(batch, mouseVars);
        //draw PieceInfo
        drawPieceInfo(batch);
    }

    //draw pieceInfo, currently just a potato way to show a piece's moveset
    void drawPieceInfo(SpriteBatch batch) {
        String line = "";
        String spacing = "  ";
        batch.end();
        batch.begin();
        Shaders.prepareDistanceFieldShader();
        batch.setShader(Shaders.distanceFieldShader);
        for (int y = 14; y >= 0; y--) {
            for (int x = 14; x >= 0; x--) {

                if (x == 7 && y == 7) {
                    line += "x  ";
                } else {
                    if (String.valueOf(Piece.moveTypeIndexes[allPieces.get(lastPieceUserSelected).staticMoveset[x][y] % 1000]).length() == 2) {
                        spacing = " ";
                    } else {
                        spacing = "  ";
                    }
                    line += String.valueOf(Piece.moveTypeIndexes[allPieces.get(lastPieceUserSelected).staticMoveset[x][y] % 1000]) + spacing;
                }
            }
            font.draw(batch, line, 20, 600 - 15 * (14 - y));
            line = "";
        }

        batch.end();
        batch.begin();
        batch.setShader(Shaders.defaultShader);
    }

    //loop through all army pieces and draw them
    void drawArmyPieces(SpriteBatch batch, MouseVars mouseVars) {
        for (int y = 0; y != 2; y++) {
            for (int x = 0; x != 8; x++) {
                int xCenter;
                int yCenter;
                int size = 56;
//if the piece is selected it will be drawn where the cursor is
                if (pieceInArmySelected && selectedPieceLocation[0] == x && selectedPieceLocation[1] == y) {
                    xCenter = mouseVars.mousePosx;
                    yCenter = mouseVars.mousePosy;
                    size = (int) (56 * 1.2);
//Otherwise it will be drawn on the army grid
                } else {
                    xCenter = 400 + 60 * x + 30;
                    yCenter = 50 + 60 * y + 30;
                }
                drawPiece(batch, armyPiece[y][x], xCenter, yCenter, size);
            }
        }
    }

    void drawBoard(SpriteBatch batch) {
        boardSprite.draw(batch);
    }

    void drawText(SpriteBatch batch) {
        batch.end();
        batch.begin();
        Shaders.prepareDistanceFieldShader();
        batch.setShader(Shaders.distanceFieldShader);
        String text = "total morale allowed: " + String.valueOf(moraleAllowed);
        font.draw(batch, text, 900, 50);
        text = "total morale used: " + String.valueOf(moraleTotal);
        font.draw(batch, text, 900, 100);
        text = "page: " + String.valueOf(page);
        font.draw(batch, text, 800, 550);
        text = "currently editing army " + String.valueOf(armyBeingEdited);
        font.draw(batch, text, 730, 30);
        text = errorMessage;
        font.setColor(Color.RED);
        font.draw(batch, text, 400, 200);
        font.setColor(Color.BLACK);
        text = successMessage;
        font.setColor(Color.GREEN);
        font.draw(batch, text, 400, 200);
        font.setColor(Color.BLACK);
        batch.end();
        batch.begin();
        batch.setShader(Shaders.defaultShader);
    }

    //draw the pieces currently visible on the collection
    void drawCollectionPieces(SpriteBatch batch, MouseVars mouseVars) {
        int xLocation = 0;
        int yLocation = 0;
        //depending on the page, certain pieces should be drawn
        //indexToDrawFrom controls which pieces get drawn
        int indexToDrawFrom = page * pageX * pageY - pageX * pageY;
//if indexToDrawFrom is greater than the size of the pieces array, then we are on a page with no pieces to display
        if (indexToDrawFrom > allPieces.size()) {
            //draw nothing
        } else {
            //loop through pieces and draw them
            int numberOfPiecesToDraw;
            if ((allPieces.size() - indexToDrawFrom) > (pageX * pageY)) {
                numberOfPiecesToDraw = 10;
            } else {
                numberOfPiecesToDraw = allPieces.size() % (pageX * pageY);
            }
            for (int x = indexToDrawFrom; x != indexToDrawFrom + numberOfPiecesToDraw; x++) {
                allPieces.get(x).drawSpecificLoc2(batch, 60, 500 + 60 * xLocation, 450 - 60 * yLocation);
                if (xLocation == pageX - 1) {
                    xLocation = 0;
                    yLocation++;
                } else {
                    xLocation++;
                }
            }
        }

        if (pieceInCollectionSelected) {
            for (int x = 0; x != allPieces.size(); x++) {
                if (allPieces.get(x).name.equals(selectedCollectionPiece)) {
                    allPieces.get(x).drawSpecificLoc2(batch, (int) (60 * 1.2), mouseVars.mousePosx, mouseVars.mousePosy);
                }
            }
        }
    }

    //draw a single piece
    void drawPiece(SpriteBatch batch, String name, int xCenter, int yCenter) {
        //loop through all pieces until we find the name of the piece that should be drawn, and draw it
        for (int x = 0; x != allPieces.size(); x++) {
            if (allPieces.get(x).name.equals(name)) {
                allPieces.get(x).drawSpecificLoc2(batch, 56, xCenter, yCenter);
                break;
            }
        }
    }

    void drawPiece(SpriteBatch batch, String name, int xCenter, int yCenter, int size) {
        //loop through all pieces until we find the name of the piece that should be drawn, and draw it
        for (int x = 0; x != allPieces.size(); x++) {
            if (allPieces.get(x).name.equals(name)) {
                allPieces.get(x).drawSpecificLoc2(batch, size, xCenter, yCenter);
                break;
            }
        }
    }


    //find the location of the mouse within a grid with square size "size" and location on screen "locationx/locationy"
    //the grid being "gridx" squares long and "gridy" square tall
    //this method returns the mouse's location in the specified grid
    public int[] findSquareMouseIsOn(int mousex, int mousey, int size, int locationx, int locationy, int gridx, int gridy) {
        //the location of the mouse in the grid is specified by these variables
        //if they remain at -1 then the mouse is not within the grid
        int xLoc = -1;
        int yLoc = -1;
        //test to see if the mouse is within the bounds of the grid
        if (mousex > locationx && mousex < locationx + gridx * size) {
            //loop through all possible x locations in the grid and increase xLoc's value
            //every time the mouse is still farther along the grid
            for (int x = 0; x != gridx; x++) {
                //if the mouses location is still farther along the grid, increase xLoc by 1
                if (mousex > locationx + size * x) {
                    xLoc++;
                } else {
                    //otherwise we have found the location of the mouse in the grid and we end this loop
                    break;
                }
            }
        }
        //test to see if the mouse is within the bounds of the grid
        if (mousey > locationy && mousey < locationy + gridy * size) {
            //loop through all possible x locations in the grid and increase xLoc's value
            //every time the mouse is still farther along the grid
            for (int y = 0; y != gridy; y++) {
                //if the mouses location is still farther along the grid, increase xLoc by 1
                if (mousey > locationy + size * y) {
                    yLoc++;
                } else {
                    //otherwise we have found the location of the mouse in the grid and we end this loop
                    break;
                }
            }
        }
        //set the loc array to the locations found
        int[] loc = new int[2];
        loc[0] = xLoc;
        loc[1] = yLoc;
        return loc;
    }

    //if the mouse is clicked and is on a piece in the army, select that piece, if the mouse is released
    //and their is a piece selected, change the location of the selected piece
    void processMouseInputArmy(MouseVars mouseVars, int loc[], int gridx, int gridy) {

        if (mouseVars.mouseClicked) {
            //if loc is actually on the board
            if (loc[0] >= 0 && loc[1] >= 0 && loc[0] <= gridx && loc[1] <= gridy) {
                //if they haven't already selected a piece, the player can pick up a new piece
                if (pieceInArmySelected == false && pieceInCollectionSelected == false) {
                    //find the index of the piece the user selected and store it is lastPieceUserSelected, this
                    //has no importance to the actual code going on here, the variable "lastPieceUserSelected"
                    //is only used to show the user information about the piece they selected
                    if (armyPiece[loc[1]][loc[0]].length() > 0) {
                        for (int x = 0; x != allPieces.size(); x++) {
                            if (allPieces.get(x).name.equals(armyPiece[loc[1]][loc[0]])) {
                                lastPieceUserSelected = x;
                            }
                        }
                    }
                    //set the location of the selected piece
                    selectedPieceLocation[0] = loc[0];
                    selectedPieceLocation[1] = loc[1];
                    //and set this true
                    pieceInArmySelected = true;
                }

            }
        }
//if the mouse is released
        if (mouseVars.mouseReleased) {
            //if there is a piece selected
            if (pieceInArmySelected) {
                //see if the selected piece is being released on the army
                if (loc[0] >= 0 && loc[1] >= 0 && loc[0] <= gridx && loc[1] <= gridy) {
                    //swap the piece's location with the location the mouse was released on
                    swapPieces(loc);
                } else {
                    //if the mouse is released off of the board, the piece is removed from the board
                    removePiece();
                }
            }
//set that a piece is no longer selected
            pieceInArmySelected = false;
        }

    }

    //if the mouse is clicked and is on a piece in the collection, select that piece, if the mouse is released
    //and their is a piece selected, and the location the mouse is released is on the army, put that piece into
    //the army
    void processMouseInputCollection(MouseVars mouseVars, int[] collectionGridLoc, int[] armyGridLoc, int gridx, int gridy) {

//if the mouse is clicked
        if (mouseVars.mouseClicked) {
            //if a piece isn't already selected
            if (pieceInCollectionSelected == false && pieceInArmySelected == false) {
                //if collectionGridLoc is in the collection grid
                if (collectionGridLoc[0] >= 0 && collectionGridLoc[1] >= 0 && collectionGridLoc[0] < pageX && collectionGridLoc[1] < pageY) {
                    //calculate which piece in the allPieces array is being selected (if any)
                    //index holds the value of the index of the piece in the allPieces array
                    int newY = pageY - 1 - collectionGridLoc[1];
                    int index = newY * pageX + collectionGridLoc[0];
                    index += page * pageX * pageY - pageX * pageY;
                    //if the index actually falls within the array
                    if (index < allPieces.size()) {
                        //set that a piece in the collection has been selected, and set it's name
                        selectedCollectionPiece = allPieces.get(index).name;
                        lastPieceUserSelected = index;
                        pieceInCollectionSelected = true;
                    }
                }
            }
        }

        //if the mouse is released
        if (mouseVars.mouseReleased) {
            //if their was a piece selected
            if (pieceInCollectionSelected) {
                //if the selected piece is located over the army
                if (armyGridLoc[0] >= 0 && armyGridLoc[1] >= 0 && armyGridLoc[0] <= gridx && armyGridLoc[1] <= gridy) {
                    //place the piece in the army location the mouse was released on
                    placePiece(armyGridLoc);
                }
            }
//set that a piece is no longer selected
            pieceInCollectionSelected = false;
        }

    }

    //swap the locations of two pieces, 1 location being the location of the selected piece (selectedPieceLocation)
//and the other being the location the selected piece is being moved to
    void swapPieces(int[] newLocations) {
        resetMessages();
        String temp = armyPiece[newLocations[1]][newLocations[0]];
        armyPiece[newLocations[1]][newLocations[0]] = armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]];
        armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]] = temp;
        //just in case the morale goes above 500 this is called, and if the morale goes above 500 the
        //swap is reverted
        calculateMorale();
        if (moraleTotal > 500) {
            temp = armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]];
            armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]] = armyPiece[newLocations[1]][newLocations[0]];
            armyPiece[newLocations[1]][newLocations[0]] = temp;
        }
        calculateMorale();
    }

    //remove a piece from the army and recalculate the army morale
    void removePiece() {
        resetMessages();
        armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]] = "";
        calculateMorale();
    }

    //place a piece on a location
    void placePiece(int[] locationToPlace) {
        resetMessages();
        //temp exists solely to revert the state of the army in case the player goes above 500 morale
        String temp = armyPiece[locationToPlace[1]][locationToPlace[0]];
        armyPiece[locationToPlace[1]][locationToPlace[0]] = selectedCollectionPiece;

        //if the morale goes above 500, revert the placement
        calculateMorale();
        if (moraleTotal > 500) {
            armyPiece[locationToPlace[1]][locationToPlace[0]] = temp;
        }
        calculateMorale();
    }

    //test to make sure the army made is valid, if it is write it to the army file
    void saveSetup() {
        int numberOfKings = 0;
        boolean emptySquare = false;
        army = "";
        //loop through all pieces and put them into a string
        for (int y = 0; y != 2; y++) {
            for (int x = 0; x != 8; x++) {
                if (y == 1 && x == 7) {
                    army += armyPiece[y][x];
                } else {
                    army += armyPiece[y][x] + ",";
                }
                //if a piece's name in the array of names is "King" then increase numberOfKings
                if (armyPiece[y][x].equals("King")) {
                    numberOfKings++;
                }
                //if there is no name for a piece in the array, that square is empty
                if (armyPiece[y][x].equals("")) {
                    emptySquare = true;
                }
            }
        }
        //check to see if the army made is valid, i.e has exactly one king and has no empty squares
        if (numberOfKings == 1) {

            if (emptySquare == false) {
                FileHandle file = Gdx.files.local("UserFiles\\armies\\army" + String.valueOf(armyBeingEdited) + ".txt");
                file.writeString(army, false);
                //if the army made is valid, set a friendly success message to let them know it was saved
                successMessage = "Army successfully saved";
                errorMessage = "";
            } else {
                //if the army is invalid, set the appropriate error message
                errorMessage = "Army not saved: There can be no empty squares in your army";
                successMessage = "";
            }
        } else {
            errorMessage = "Army not saved: all armies must have exactly 1 king";
            successMessage = "";
        }
    }

    //reset the messages to the user when they are no longer applicable
    void resetMessages() {
        errorMessage = "";
        successMessage = "";
    }

    void unselectAll() {
        pieceInCollectionSelected = false;
        pieceInArmySelected = false;
    }

    void reloadGraphics() {
        deleteAllNonMenuGraphics();
        loadTextures();
        for (int x = 0; x != allPieces.size(); x++) {
            allPieces.get(x).setImage();
        }

    }

    void deleteGraphics() {
        menu.dispose();
        font.dispose();
        for (int x = 0; x != allPieces.size(); x++) {
            allPieces.get(x).deleteGraphics();
        }
    }

    //same as above but menu graphics untouched
    void deleteAllNonMenuGraphics() {
        font.dispose();
        for (int x = 0; x != allPieces.size(); x++) {
            allPieces.get(x).deleteGraphics();
        }
    }

}

