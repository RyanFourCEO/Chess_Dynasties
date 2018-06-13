package ceov2.org;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;

//this class contains all the information a piece has
public class Piece {
    static String vertexShader;
    static String fragmentShader;
    static ShaderProgram shaderProgram;
    static ShaderProgram defaultShader;
    //static array of all movetypes
    static PieceMoveType[][] allMoveTypes=new PieceMoveType[2][5];
     //all the pieces in the game, any time a piece gets added, that pieces name must be added here
      static String[] allPieces={"Rook","Pawn","Bishop","Queen","King","Knight"};
      //the morales of all the pieces in the game, only necessary while piece files don't exist
      static int[] allMorales={50,10,30,90,0,30};
      //the number of total pieces in the game, this must be updated along with the above array of strings
      static int pieceCounter=6;
static int moveTypeCounter=0;
    //board's location on screen
    static int boardLocationx=400;
    static int boardLocationy=50;
    //initialize all the movetypes
    static void InitAllMoveTypes(){

moveTypeCounter=0;
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,false,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeCounter++;
        //index 0 in array, this movetype is just an empty square, it will have no functionality

        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true,false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeCounter++;
        //index 1 in array, this is the move/attack movetype

        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeCounter++;
        //index 2 in array, this is the move only movetype

        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true, false, false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeCounter++;
        //index 3 in array, this is the attack only movetype

        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true, false,false);
        allMoveTypes[0][moveTypeCounter].setIsOneTimeUse();
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        allMoveTypes[1][moveTypeCounter].setIsOneTimeUse();
        moveTypeCounter++;
        // index 4 in array, this is move from starting position movetype
    }

    static void initShaders(){
        vertexShader=Gdx.files.internal("VertShader.txt").readString();
        fragmentShader=Gdx.files.internal("FragmentShader.txt").readString();
        shaderProgram=new ShaderProgram(vertexShader,fragmentShader);
        defaultShader=SpriteBatch.createDefaultShader();

    }

//the moveset of a piece, 0 means no movement on that location, >0 gives the index of the movetype in the array allMoveTypes
    int[][] moveset=new int[15][15];
    //the valid moves a piece has on a given turn
    boolean[][] validMoves=new boolean[15][15];
    String name;
    //a pieces graphics objects
    Texture pieceImage;
    Sprite sprite;
    // locations of the piece on the board
    int xLocation;
    int yLocation;

    int moraleCost;
    int moralePenalty;
    //if the piece is currently selected by the player
    boolean selected=false;
    //colour of the piece
    boolean isWhite;
    //1 if white owns the piece, 2 if black owns the piece
    int playerWhoOwnsPiece=0;
    //variables keeping track of the piece's progress
    boolean captured=false;
    int numberOfMovesMade=0;
    int piecesCaptured=0;
    int turnsSurvived=0;
    int turnCaptured=0;
    boolean justCaptured=false;
    int moveTypeCapturedBy;
    int[] moveTypesPieceHas;
    int[] moveTypesUsed;

    ArrayList<PieceAbility> allAbilities=new ArrayList<PieceAbility>();


    //initialize a piece with a moveset array and a name
    public Piece(int[][] moveset,String name){
      //  int[] triggers={5};
       // allAbilities.add(new PieceAbility(1,triggers,0));

        int[] moveTypesPieceHas=new int[15];
        int numberOfMoveTypesPieceHas=0;
        //loop through a piece's moveset
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
                //set the moveset equal to the array that was sent int=
               this.moveset[x][y]=moveset[x][y];

               //check to see if the movetype that was just added has not yet been added before
               boolean newMoveType=true;
               for(int z=0;z!=numberOfMoveTypesPieceHas;z++) {
                   if (moveset[x][y]==moveTypesPieceHas[z]){
                           newMoveType=false;
                   }
               }
               //if the movetype that was added is new, and is not 0, increase the numberOfMoveTypesPieceHas
                //also add the new movetype to an array which stores which movetypes a piece has
               if(moveset[x][y]!=0) {
                   if (newMoveType == true) {
                       moveTypesPieceHas[numberOfMoveTypesPieceHas] = moveset[x][y];
                       numberOfMoveTypesPieceHas++;
                   }
               }

            }
        }
        this.moveTypesPieceHas=moveTypesPieceHas;
        this.name=name;
        setImage();
        //pieceImage=new Texture(name+".png");
    }
    //set the morale penalties a piece has
    void setMoraleValues(int cost, int penalty){
        moraleCost=cost;
        moralePenalty=penalty;
    }
//set the location of the piece on board 0-7
    void setLocation(int x, int y){
        xLocation=x;
        yLocation=y;

    }

//this method is called when a piece moves, if they have any moves that can only be used as their first turn
    //they are no longer usable
    void removeOneTimeMovesMoves(){
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
               int blockable=0;
                if (moveset[x][y]>1000&&moveset[x][y]<2000){
                    blockable=1;
                }
                int movetype=moveset[x][y]%1000;
                if (allMoveTypes[blockable][movetype].oneTimeUse==true){
                    moveset[x][y]=0;
                }
            }
        }
    }

    void select(){
        selected=true;
}

    void unselect(){
        selected=false;
}
//set the colour and graphics of a piece
    void setImage(){

        String filePath="pieces\\";
        filePath+=name+"White"+".png";
pieceImage=new Texture(filePath);
       // pieceImage.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
sprite=new Sprite(pieceImage);

    }

    void setColour(boolean isWhite){
        this.isWhite=isWhite;
if (isWhite==true){
    playerWhoOwnsPiece=1;
}else{
    playerWhoOwnsPiece=2;
}
        if (isWhite==true){

        }else{
            flipMoveset();
        }
    }

    void capture(GameState state){
        captured=true;
    }
    //used for opponent's pieces, currently used for black pieces, this method ensures pieces like pawns move in the
    //right direction
    void flipMoveset(){
        int[][] tempMoveset=new int[15][15];
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
                tempMoveset[x][y]=moveset[x][14-y];
            }
        }
        moveset=tempMoveset;
}
//draw the piece to the screen
    void draw(SpriteBatch batch){

        sprite.setSize((56),(56));
        if (selected==true){
            sprite.setCenter(Gdx.input.getX(),618-Gdx.input.getY());
        }else {
            sprite.setCenter(xLocation * 60 + 30 + boardLocationx, yLocation * 60 + 30 + boardLocationy);
        }
if (isWhite==true) {
    sprite.draw(batch);
}else{
            //if the piece is black, draw the piece with the colour inversion shaders
            batch.setShader(shaderProgram);
    sprite.draw(batch);
    batch.setShader(defaultShader);

}
    }
//draw the piece in a specific location, not the location the piece has on the board
    void drawSpecificLoc(SpriteBatch batch,int size,int centerx,int centery){
        sprite.setSize((size),(size));
        if (selected==true){
            sprite.setCenter(Gdx.input.getX(),618-Gdx.input.getY());
        }else{
            sprite.setCenter(centerx, centery);
        }
        sprite.draw(batch);
    }

//delete the graphics, used when the game ends
void deleteGraphics(){
        pieceImage.dispose();
}

}
