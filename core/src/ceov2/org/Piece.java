package ceov2.org;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

//this class contains all the information a piece has
public class Piece {
    static String vertexShader;
    static String fragmentShader;
    static ShaderProgram shaderProgram;
    static ShaderProgram defaultShader;
    //static array of all movetypes
    static PieceMoveType[][] allMoveTypes=new PieceMoveType[2][5];
static int moveTypeCounter=0;
    //boards location on screen
    static final int boardLocationx=400;
    static final int boardLocationy=50;
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
    boolean captured=false;
    int moraleCost;
    int moralePenalty;
    //if the piece is currently selected by the player
    boolean selected=false;
    //colour of the piece
    boolean isWhite;

    //initialize a piece with a moveset array and a name
    public Piece(int[][] moveset,String name){
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
               this.moveset[x][y]=moveset[x][y];
            }
        }

        this.name=name;
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
//execute a move
    void executeMove(int xTarget,int yTarget,GameState state){
   //find the location of the move in the 15*15 moveset array
        int xOffset=xTarget-xLocation+7;
        int yOffset=yTarget-yLocation+7;
        //find what type of move the piece is doing
        int movetype=moveset[xOffset][yOffset];
        int blockable;
        if (movetype>1000&&movetype<2000){
            movetype=movetype%1000;
blockable=1;
        }else{
            blockable=0;
        }
        //execute the move
        allMoveTypes[blockable][movetype].executeMove(xTarget,yTarget,xLocation,yLocation,state);
//the piece is no longer selected, as it has executed a move
        selected=false;
    }

    void select(){
        selected=true;
}

    void unselect(){
        selected=false;
}
//set the colour and graphics of a piece
    void setImage(boolean isWhite){
        this.isWhite=isWhite;
        String filePath="pieces\\";
        if (isWhite==true){


        }else{
            flipMoveset();

        }
        filePath+=name+"White"+".png";
pieceImage=new Texture(filePath);
       // pieceImage.setFilter(Texture.TextureFilter.Linear,Texture.TextureFilter.Linear);
sprite=new Sprite(pieceImage);

    }

    void capture(){
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
//delete the graphics, used when the game ends
void deleteGraphics(){
        pieceImage.dispose();
}

}
