package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class LevelEditor extends ArmyMaker{

    //stores whether each piece on the board is white or black
    //true is white, false is black
    public boolean[][] colourOfPieceOnBoard =new boolean[8][8];
    //stores whether the pieces being placed on board from the collection should be white or black
    boolean colourOfPieceBeingPlaced =true;



    public LevelEditor(InputMultiplexer inputMultiplexer){
    super();
    loadLevelEditorMenu(inputMultiplexer);
    loadCurrentLevel();
    pageX=2;
    pageY=5;
}

    public void run(SpriteBatch batch, MouseVars mouseVars){

    menu.stage.getViewport().apply();
    menu.stage.draw();
    updateMenuObjects();

    //find the mouse's location on the army grid
    mouseLocOnArmy =findSquareMouseIsOn(mouseVars.mousePosx,mouseVars.mousePosy,60,400,50,8,8);
    //perform logic based on the mouse location on the army grid and the mouse variables
    processMouseInputArmy(mouseVars, mouseLocOnArmy,8,8);
    //find the mouse's location on the collection grid
    mouseLocOnCollection =findSquareMouseIsOn(mouseVars.mousePosx,mouseVars.mousePosy,60,910,180,pageX,pageY);
    //perform logic based on the mouse's locations on both grids and the mouse variables
    processMouseInputCollection(mouseVars, mouseLocOnCollection, mouseLocOnArmy,8,8);

    batch.begin();
    drawAll(batch,mouseVars);
    batch.end();
}

    void drawCollectionPieces(SpriteBatch batch,MouseVars mouseVars){
        //if the colours of pieces being placed should be black, use the inversion shader to draw pieces in the collection
        if (colourOfPieceBeingPlaced==false){
            batch.setShader(Shaders.inversionShader);
        }

        int xLocation=0;
        int yLocation=0;
        //depending on the page, certain pieces should be drawn
        //indexToDrawFrom controls which pieces get drawn
        int indexToDrawFrom=page*pageX*pageY-pageX*pageY;
//if indexToDrawFrom is greater than the size of the pieces array, then we are on a page with no pieces to display
        if (indexToDrawFrom>allPieces.size()){
            //draw nothing
        }else{
            //loop through pieces and draw them
            int numberOfPiecesToDraw=0;
            if ((allPieces.size()-indexToDrawFrom)>(pageX*pageY)){
                numberOfPiecesToDraw=10;
            }else{
                numberOfPiecesToDraw=allPieces.size()%(pageX*pageY);
            }
            for(int x=indexToDrawFrom;x!=indexToDrawFrom+numberOfPiecesToDraw;x++){
                allPieces.get(x).drawSpecificLoc(batch,60,940+60*xLocation,450-60*yLocation);
                if (xLocation==pageX-1){
                    xLocation=0;
                    yLocation++;
                }else {
                    xLocation++;
                }
            }
        }

        if (pieceInCollectionSelected==true){
            for(int x=0;x!=allPieces.size();x++){
                if (allPieces.get(x).name.equals(selectedCollectionPiece)){
                    allPieces.get(x).drawSpecificLoc(batch,60,mouseVars.mousePosx,mouseVars.mousePosy);
                }
            }
        }

        batch.setShader(Shaders.defaultShader);

    }

    void loadLevelEditorMenu(InputMultiplexer inputMultiplexer){
    menu=new Menu(inputMultiplexer);
    ClickListener clickListener;


    //return to main menu button
    clickListener=new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y){
            exitToMainMenu =true;
        }
    };
    menu.addButton("Return to Main Menu",200,30,100,100,clickListener);


    //increase page button
    clickListener=new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y){
            if (page<10) {
                page++;
            }
        }
    };
    menu.addButton("increase page",150,30,900,550,clickListener);



    //decrease page button
    clickListener=new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y){
            if (page>1){
                page--;
            }
        }
    };
    menu.addButton("decrease page",150,30,900,500,clickListener);


    //save level button

    clickListener=new ClickListener(){
        @Override
        public void clicked(InputEvent event, float x, float y){
            saveSetup();
        }
    };
    menu.addButton("Save Level",150,30,900,90,clickListener);

        //change piece colour button

        clickListener=new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                 colourOfPieceBeingPlaced=!colourOfPieceBeingPlaced;
            }
        };
        menu.addButton("swap colours",150,30,900,140,clickListener);

        //load level button
        clickListener=new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                loadLevel();
            }
        };
        menu.addButton("load Level",150,30,900,10,clickListener);



    //create two text areas, while in loop we will update these to have helpful information about each piece on them
    menu.addTextArea(300,100,0,250);
    menu.addTextArea(300,100,0,140);

        //this text area is where the user enters the name of the level they have made, so it can be saved to a file with
        //the correct name and loaded easily

        menu.addTextArea("enter name here",150,30,900,50);

    }

    //loop through all army pieces and draw them
    void drawArmyPieces(SpriteBatch batch, MouseVars mouseVars){
        for(int y=0;y!=8;y++){
            for(int x=0;x!=8;x++){
                //only draw white pieces, another loop will be used to draw all the black pieces
                if (colourOfPieceOnBoard[y][x]==true) {
                    int xCenter;
                    int yCenter;
//if the piece is selected it will be drawn where the cursor is
                    if (pieceInArmySelected == true && selectedPieceLocation[0] == x && selectedPieceLocation[1] == y) {
                        xCenter = mouseVars.mousePosx;
                        yCenter = mouseVars.mousePosy;
//Otherwise it will be drawn on the army grid
                    } else {
                        xCenter = 400 + 60 * x + 30;
                        yCenter = 50 + 60 * y + 30;
                    }
                    drawPiece(batch, armyPiece[y][x], xCenter, yCenter);
                }
            }
        }
        //set the shader so that pieces will be drawn in black
        batch.setShader(Shaders.inversionShader);
        for(int y=0;y!=8;y++){
            for(int x=0;x!=8;x++){
                //only draw black pieces, another loop will be used to draw all the black pieces
                if (colourOfPieceOnBoard[y][x]==false) {
                    int xCenter;
                    int yCenter;
//if the piece is selected it will be drawn where the cursor is
                    if (pieceInArmySelected == true && selectedPieceLocation[0] == x && selectedPieceLocation[1] == y) {
                        xCenter = mouseVars.mousePosx;
                        yCenter = mouseVars.mousePosy;
//Otherwise it will be drawn on the army grid
                    } else {
                        xCenter = 400 + 60 * x + 30;
                        yCenter = 50 + 60 * y + 30;
                    }
                    drawPiece(batch, armyPiece[y][x], xCenter, yCenter);
                }
            }
        }

        //set the shader back to default
        batch.setShader(Shaders.defaultShader);
    }


    void drawText(SpriteBatch batch){
        String text="";
        text="page: "+String.valueOf(page);
        font.draw(batch,text,800,550);

        text=errorMessage;
        font.setColor(Color.RED);
        font.draw(batch,text,400,20);
        font.setColor(Color.BLACK);

        text=successMessage;
        font.setColor(Color.GREEN);
        font.draw(batch,text,400,20);
        font.setColor(Color.BLACK);
    }
    //loads graphics objects
    void loadTextures(){
        font=new BitmapFont();
        font.setColor(Color.BLACK);
        boardImage=new Texture("Board.png");
        boardSprite =new Sprite(boardImage);
        boardSprite.setSize(480,480);
        boardSprite.setCenter(640,290);
    }

    void loadCurrentLevel(){
        for(int x=0;x!=8;x++){
            for(int y=0;y!=8;y++){
                armyPiece[x][y]="";
            }
        }
    }

    //swap the locations of two pieces, 1 location being the location of the selected piece (selectedPieceLocation)
//and the other being the location the selected piece is being moved to
    void swapPieces(int[] newLocations){
        resetMessages();
        String temp=armyPiece[newLocations[1]][newLocations[0]];
        boolean temp2=colourOfPieceOnBoard[newLocations[1]][newLocations[0]];

        armyPiece[newLocations[1]][newLocations[0]]=armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]];
        colourOfPieceOnBoard[newLocations[1]][newLocations[0]]=colourOfPieceOnBoard[selectedPieceLocation[1]][selectedPieceLocation[0]];

        armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]]=temp;
        colourOfPieceOnBoard[selectedPieceLocation[1]][selectedPieceLocation[0]]=temp2;

        calculateMorale();
    }
    //remove a piece from the level
    void removePiece(){
        resetMessages();
        armyPiece[selectedPieceLocation[1]][selectedPieceLocation[0]]="";
        calculateMorale();
    }
    //place a piece on a location
    void placePiece(int[] locationToPlace){
        resetMessages();
        armyPiece[locationToPlace[1]][locationToPlace[0]]= selectedCollectionPiece;
        colourOfPieceOnBoard[locationToPlace[1]][locationToPlace[0]]=colourOfPieceBeingPlaced;
        calculateMorale();
    }

    //write the level to a file
    void saveSetup(){
String allUnits="";
String allColours="";
        //loop through all pieces and put them into a string
        for(int y=0;y!=8;y++) {
            for(int x=0;x!=8;x++) {
                if (y==7&&x==7){
                    //end is placed here solely because the String.split method will cut off the array size
                    //if there is nothing but commas. This ensures the array the split method returns is always
                    //a size of 64
                    allUnits +=armyPiece[y][x]+"END";
                    allColours+=String.valueOf(colourOfPieceOnBoard[y][x]);
                }else {
                    allUnits += armyPiece[y][x] + ",";
                    allColours+=String.valueOf(colourOfPieceOnBoard[y][x])+",";
                }
            }
        }
        //add a line between the two strings so that all the names of the pieces are on one line
        //and all the colours of the pieces are on the other
     String fileText=allUnits+"\n"+allColours;

        //write the string to a file
        FileHandle file = Gdx.files.local("UserFiles\\Levels\\" + menu.allTextAreas.get(2).getText() + ".txt");
        file.writeString(fileText, false);

        //set the success message so the user knows everything went well
        resetMessages();
        successMessage="level: \""+menu.allTextAreas.get(2).getText()+"\" successfully saved";

    }

    void loadLevel(){
        successMessage="";
        errorMessage="";
        try {
            //load the level from a file
            army = Gdx.files.local("UserFiles\\Levels\\" + menu.allTextAreas.get(2).getText()+".txt").readString();
            String[] lines = army.split("\n");

            //collect all the piece names into an array
            String[] pieceNames = lines[0].split(",");
            //collect all the colours of the pieces into an array
            String[] pieceColours = lines[1].split(",");
            //load the piece names and colours into the 8x8 arrays
            int counter = 0;
            for (int y = 0; y != 8; y++) {
                for (int x = 0; x != 8; x++) {
                    armyPiece[y][x] = pieceNames[counter];
                    colourOfPieceOnBoard[y][x] = Boolean.valueOf(pieceColours[counter++]);
                }
            }
            //set the success message so the user knows everything went well
            resetMessages();
            successMessage = "level: \"" + menu.allTextAreas.get(2).getText() + "\" successfully loaded";
            calculateMorale();
        }catch(GdxRuntimeException e){
            resetMessages();
errorMessage="level: \"" + menu.allTextAreas.get(2).getText() + "\" not found";
        }
    }




}
