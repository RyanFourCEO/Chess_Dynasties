package ceov2.org;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
//this class contains all the information a piece has
public class Piece {
    //static array of all movetypes
    static ArrayList<PieceMoveType> allMoveTypes=new ArrayList<PieceMoveType>();
    //boards location on screen
    static final int boardLocationx=400;
    static final int boardLocationy=50;
    //initialize all the movetypes
    static void InitAllMoveTypes(){
        allMoveTypes.add(new PieceMoveType(0,false,false,false,false));//index 0 in arraylist, this movetype is just an empty square, it will have no functionality

        allMoveTypes.add(new PieceMoveType(1,true,true,false,true));//index 1 in arraylist, this is the move/attack movetype

        allMoveTypes.add(new PieceMoveType(1,true,true,false,false));//index 2 in arraylist, this is the move only movetype

        allMoveTypes.add(new PieceMoveType(1,true, false, false,true));//index 3 in arrayList, this is the attack only movetype

        allMoveTypes.add(new PieceMoveType(1,false,true, false,true));// index 4 in arrayList, this is the unblockable move/attack movetype

        allMoveTypes.add(new PieceMoveType(1,true,true, false,false));// index 5 in arrayList, this is move from starting position movetype
        allMoveTypes.get(5).setIsOneTimeUse();
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
        //execute the move
        allMoveTypes.get(movetype).executeMove(xTarget,yTarget,xLocation,yLocation,state);
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

            filePath+=name+"White"+".png";
        }else{
            flipMoveset();
            filePath+=name+"Black"+".png";
        }
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

        sprite.draw(batch);
    }
//delete the graphics, used when the game ends
void deleteGraphics(){
        pieceImage.dispose();
}

}
