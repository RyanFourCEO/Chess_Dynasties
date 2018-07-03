package ceov2.org;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import java.util.ArrayList;

//this class contains all the information a piece has
public class Piece {
    static final String vertexShader=Gdx.files.internal("VertShader.txt").readString();
    static final String fragmentShader=Gdx.files.internal("FragmentShader.txt").readString();
    static ShaderProgram shaderProgram=new ShaderProgram(vertexShader,fragmentShader);
    static ShaderProgram defaultShader=SpriteBatch.createDefaultShader();
      //static array of all movetypes
      static final PieceMoveType[][] allMoveTypes =new PieceMoveType[2][12];
      //all the pieces in the game, any time a piece gets added, that pieces name must be added here
      static final String[] allPieces={"Rook","Pawn","Bishop","Queen","King","Knight","Advisor","Ceasefire Armaments","Bait","Cannon","Conscript","Hired Blade","Slime","Warship"};
      //the number of total pieces in the game, this must be updated along with the above array of strings
      static final int pieceCounter=14;
      static int moveTypeCounter=0;
      //stores the index number of any movetype. A movetype may be the 5th index in the allMoveTypes array, but
      //it may be the 17th movetype on the "movetypes" file. This array is used to connect those two numbers
      //i.e moveTypeIndexes[5]=17 This is only used when loading in pieces from files, a file may say to use movetype
      //17 from the file, when the game currently only has 9 movetypes implemented
      static final int[] moveTypeIndexes=new int[12];

    //initialize all the movetypes
    static void InitAllMoveTypes(){
moveTypeCounter=0;

        //index 0 in array, this movetype is just an empty square, it will have no functionality
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,false,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeIndexes[moveTypeCounter]=0;
        moveTypeCounter++;

        //index 1 in array, this is the move/attack movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true,false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeIndexes[moveTypeCounter]=1;
        moveTypeCounter++;

        //index 2 in array, this is the move only movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeIndexes[moveTypeCounter]=2;
        moveTypeCounter++;

        //index 3 in array, this is the attack only movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true, false, false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        moveTypeIndexes[moveTypeCounter]=3;
        moveTypeCounter++;

        // index 4 in array, this is move from starting position movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true,true, false,false);
        allMoveTypes[0][moveTypeCounter].setIsOneTimeUse();
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,false,false,false);
        allMoveTypes[1][moveTypeCounter].setIsOneTimeUse();
        moveTypeIndexes[moveTypeCounter]=4;
        moveTypeCounter++;

        //index 5 in array, this is the ranged attack movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(2,true, false, false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(2,false,false,false,true);
        moveTypeIndexes[moveTypeCounter]=5;
        moveTypeCounter++;

        //index 6 in array, this is the ranged attack/move movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(2,true, true, false,true,false,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(2,false,true,false,true,false,false,false);
        moveTypeIndexes[moveTypeCounter]=6;
        moveTypeCounter++;

        //index 7 in array, this is the cannon movetype, can jump over 1 piece when moving, must jump over 1 piece when attacking
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(true,1, true, false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(true,1,true,false,true);
        moveTypeIndexes[moveTypeCounter]=7;
        moveTypeCounter++;

        //index 8 in array, this is the swap movetype, swap places with a piece
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(3,false, false, true,false,false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(3,true,false,true,false,false,false);
        moveTypeIndexes[moveTypeCounter]=13;
        moveTypeCounter++;

        //index 9 in array, this is the sacrifice self movetype
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(4,false, true, true,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(4,false,true,true,true);
        moveTypeIndexes[moveTypeCounter]=16;
        moveTypeCounter++;

        //index 10 in array, this is an ability target movetype, it can't be used as a move but is used by abilities to cause effects
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(0,false, false, false,false);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(0,false,false,false,false);
        moveTypeIndexes[moveTypeCounter]=20;
        moveTypeCounter++;

        //index 11 in array, this is an ability target movetype, it can be used as a move/attack
        allMoveTypes[0][moveTypeCounter]=new PieceMoveType(1,true, true, false,true);
        allMoveTypes[1][moveTypeCounter]=new PieceMoveType(1,false,true,false,true);
        moveTypeIndexes[moveTypeCounter]=21;
        moveTypeCounter++;
    }

//the StaticMoveset of a piece, 0 means no movement on that location, >0 gives the index of the movetype in the array allMoveTypes
//this array will change very little
    int[][] staticMoveset=new int[15][15];
//the moveset of a piece that can change, changes based on abilities and maybe other things in the future
//the changeableMoveSet trumps the moveset, if both moveset arrays contain a move on a square, the changeable moveset
//movetype will be used over the moveset
    int[][] changeableMoveset=new int[15][15];
//the moveset array, this array combines the staticMoveset and the changeableMoveset array into one
//at the start of each turn
    int[][] moveset =new int[15][15];
    //the valid moves a piece has on a given turn
    boolean[][] validMoves=new boolean[15][15];


    String name;

    String abilityDescription;

    String loreWriting;

    //a piece's graphics objects
    Texture pieceImage;
    Sprite sprite;
    //locations of the piece on the board
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
    //what type of move the piece was targeted by
    int moveTypeTargetedBy=0;
    int timesTargeted=0;
    //did this piece just make a move?
    boolean justMoved=false;
    //did this piece just get captured?
    boolean justCaptured=false;
    //did this piece just perform a capture?
    boolean justGotCapture=false;
    //did this piece just get targeted?
    boolean justTargeted=false;
    //which piece is targeting this piece?
    Piece pieceTargetedBy;
    //did this piece just use a movetype
    boolean justUsedMovetype=false;
    //index of the movetype just used
    int movetypeUsed=0;

    int moveTypeCapturedBy;
    int[] moveTypesPieceHas;
    int[] moveTypesUsed;

    //status booleans, depending on the status effects a piece has they can't do certain things
    boolean abilitiesDisabled=false;
    boolean movesDisabled=false;
    boolean protectedFromAttacks=false;
    boolean protectedFromAbilities=false;
    boolean protectedFromStatuses=false;
    boolean immovable=false;
    boolean cantTargetKing=false;

    //if this is ever set to true, the piece will be captured at the end of the turn
    boolean setToBeRemovedFromBoard=false;

    boolean capturedByStatus=false;
    int statusCapturedBy=0;
    ArrayList<PieceAbility> allAbilities=new ArrayList<PieceAbility>();

    ArrayList<StatusEffect> allStatuses=new ArrayList<StatusEffect>();

    //initialize a piece object
    public Piece(String name,boolean isWhite){
        //using the piece's name, load all it's variables in from a file
        loadPiecesVariablesFromFile(name);
        //set the piece's colour
        setColour(isWhite);
        //set the piece's image
        setImage();
        //find which movetypes the piece has
        findMovetypesPieceHas();
        //update the statuses for the very first turn
        for(int x=0;x!=allStatuses.size();x++) {
            updateStatusBooleans(allStatuses.get(x));
        }

    }
   //find all the movetypes a piece has
   //untested, might not work, but also is currently unused
    void findMovetypesPieceHas(){
        int[] moveTypesPieceHas=new int[15];
        int numberOfMoveTypesPieceHas=0;
        //loop through a piece's moveset
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
                //check to see if the movetype that was just added has not yet been added before
                boolean newMoveType=true;
                for(int z=0;z!=numberOfMoveTypesPieceHas;z++) {
                    if (staticMoveset[x][y]==moveTypesPieceHas[z]){
                        newMoveType=false;
                    }
                }

                //if the movetype that was added is new, and is not 0, increase the numberOfMoveTypesPieceHas
                //also add the new movetype to an array which stores which movetypes a piece has
                if(staticMoveset[x][y]!=0) {
                    if (newMoveType == true) {
                        moveTypesPieceHas[numberOfMoveTypesPieceHas] = staticMoveset[x][y];
                        numberOfMoveTypesPieceHas++;
                    }
                }

            }
        }
        this.moveTypesPieceHas=moveTypesPieceHas;

    }

    void loadPiecesVariablesFromFile(String name){
        //load the entire csv text fileinto String fileText
        String fileText="";
        fileText=Gdx.files.internal("PieceInfo\\Pieces.txt").readString();
        //separatedLines will hold all the values of each line in the csv
        String[] separatedLines=null;
        String newLine = "\n";
        //loop through the fileText string to find how many newLine characters there are
        int counter=0;
        for(int x=0;x!=fileText.length();x++){
            if (String.valueOf(fileText.charAt(x)).equals(newLine)){
                counter++;
            }
        }
        //TODO this may only work some of the time, due to String things
        String split="\r\n";
        //separate the file into each of it's lines and store each line in separatedLines
        for(int x=0;x!=counter;x++){
            separatedLines=fileText.split(split);
        }

        int linePieceStartsOn=0;
        //loop through every line to find the name of the piece we are loading from the file
        for(int x=0;x!=counter;x++){
            if(separatedLines[x].contains(","+name+",")){
                //if we find it we mark the index of the line the name was found on
                linePieceStartsOn=x;
                break;
            }
        }

        //variables needed for the next loop
        int quotationCounter=0;
        String singlePieceVariable="";
        //holds all the variables that get loaded in from the file
        String[] pieceVariablesFromFile=new String[10];
        //how many variables have been loaded from the file
        int variableCounter=0;
        //how many quotations are clumped together
        int adjacentQuotationCounter=0;
        //this loop loads all the variables a piece needs from a txt file in csv format
        //loop until all 10 variables have been loaded
        while (variableCounter<9) {
            //loop through every character of the line we are currently looking at
            for (int x = 0; x != separatedLines[linePieceStartsOn].length(); x++) {
                //if the character is a quotation, increase the quotation counters
                if (separatedLines[linePieceStartsOn].charAt(x) == '"') {
                    quotationCounter++;
                    adjacentQuotationCounter++;
                }else{
                    adjacentQuotationCounter=0;
                }
                //if the character is a comma
                if (separatedLines[linePieceStartsOn].charAt(x) == ',') {
                    //if the # of quotations is even, then the comma is outside the fields, and is
                    //one of the commas that separates values (CSV). If we find a comma
                    //that is separating two fields, we load the value of the String up until then
                    //into the pieceVariablesFromFile array
                    if (quotationCounter % 2 == 0) {
                        //add the String variable to the array
                        pieceVariablesFromFile[variableCounter++]= singlePieceVariable;
                        //reset the String
                        singlePieceVariable="";
                    }else {
                        //if the comma was not separating two fields, and was within a field
                        //it is added to the String
                        singlePieceVariable += String.valueOf(separatedLines[linePieceStartsOn].charAt(x));
                    }
                }else{
                    //if the character was not a comma, and is not a quotation
                    if (separatedLines[linePieceStartsOn].charAt(x)!='"') {
                        //it is added to the String
                        singlePieceVariable+=String.valueOf(separatedLines[linePieceStartsOn].charAt(x));
                    }else{
                        //if there are exactly two adjacent quotations, we add a single quotation
                        //to the String
                        if (adjacentQuotationCounter==2){
                            singlePieceVariable+= String.valueOf(separatedLines[linePieceStartsOn].charAt(x));
                        }
                    }

                }
            }

            //if we have reached the final variable, place the string into the array
            if (variableCounter==9){
                pieceVariablesFromFile[variableCounter++]= singlePieceVariable;
            }
            //increase linePieceStartsOn, so that we can start looping through the next line's characters
            //this is only used if a piece's fields are not all stored on a single line, which happens
            //in the case of pawn(and many other pieces):
            //10,Pawn,,"1 orthogonally forward only to move, 1 diagonally forward only to attack, may move exactly 2 orthogonally forward from start","2|1|0|0|0|0|0|0|0|4
            //1|1|0|0|0|0|0|0|0|2
            //1|0|1|0|0|0|0|1|0|3",,Yes,,▪Common,Promoter
            linePieceStartsOn++;
            //add this to the String so we know where the extra lines are
            singlePieceVariable+="lineSeparatorString";
        }
        moraleCost=Integer.valueOf(pieceVariablesFromFile[0]);
        this.name=pieceVariablesFromFile[1];
        abilityDescription=pieceVariablesFromFile[2].replace("lineSeparatorString","\n");
        loreWriting=pieceVariablesFromFile[5].replace("lineSeparatorString","\n");
//pieceVariablesFromFile[4];



        //split the moveset notation into their respective lines
        String[] movesetLines = pieceVariablesFromFile[4].split("lineSeparatorString");
        //calculate how many moveset notation lines there are
        int y=pieceVariablesFromFile[4].length()-pieceVariablesFromFile[4].replace("lineSeparatorString","lineSeparatorStrin").length();
        //loop through all the lines and set the piece's moveset based on the notation lines
        for(int x=0;x!=y+1;x++){
            if (movesetLines[x].length()>0) {
                setStaticMoveset(movesetLines[x]);
            }
        }
        //split the ability notation into the respective lines
        String[] abilityLines = pieceVariablesFromFile[7].split("lineSeparatorString");
        //calculate how many ability notation lines there are
        y=pieceVariablesFromFile[7].length()-pieceVariablesFromFile[7].replace("lineSeparatorString","lineSeparatorStrin").length();

//loop through all ability notation lines and set the piece's abilities
        for(int x=0;x!=y+1;x++){
            if (abilityLines[x].length()>0) {
                setAbility(abilityLines[x]);
            }
        }
    }

    void setAbility(String line){
        //calculate the number of triggers an ability has,
        //the number of triggers will be equal to the number of occurrences of |
        int numOfTriggers=0;
        for(int x=0;x!=line.length();x++) {
            if (line.charAt(x)=='|') {
                numOfTriggers++;
            }
        }

        //split the line into it's respective effects and triggers
        String[] effectsAndTriggers=line.split("\\|");
        //the first index in the above array will be the ability effect notation
        String effect=effectsAndTriggers[0];
        //the remaining indices will we the ability trigger notation, and we'll place those into this array
        String[] onlyTriggers=new String[numOfTriggers];
for(int x=0;x!=numOfTriggers;x++){
    onlyTriggers[x]=effectsAndTriggers[x+1];
}

//calculate the number of effect variables, it will be equal to the number of occurrences of :
int numOfEffectVariables=0;
        for(int x=0;x!=effect.length();x++) {
            if (effect.charAt(x)==':') {
               numOfEffectVariables++;
            }
        }
//split the effect notation into it's variables
String[] effectVariables=effect.split(":");
//the first index in the array will be the index of the effect
int effectIndex=Integer.valueOf(effectVariables[0]);
//the remaining indices will be the effect variables, we'll store them in this array
String[] onlyEffectStrengthVariables=new String[numOfEffectVariables];
for(int x=0;x!=numOfEffectVariables;x++){
    onlyEffectStrengthVariables[x]=effectVariables[x+1];
}

//we'll store all the trigger variables in this array
String[][] triggerVariables=new String[numOfTriggers][];
for(int x=0;x!=numOfTriggers;x++){
    triggerVariables[x]=onlyTriggers[x].split(":");
}
//we'll separate the above array into it's two smaller arrays
//the trigger index array
int[] triggerIndexes=new int[numOfTriggers];
//the trigger requirement array
int[] triggerRequirement=new int[numOfTriggers];
//loop through all triggers and store the variables in the new arrays
for(int x=0;x!=numOfTriggers;x++){
    triggerIndexes[x]=Integer.valueOf(triggerVariables[x][0]);
    //some triggers have no requirements, in which case we don't bother with triggerrequirements
    if (onlyTriggers[x].contains(":")) {
        triggerRequirement[x] = Integer.valueOf(triggerVariables[x][1]);
    }
}
//TODO seems functional, but needs more testing
//Using all the variables we have calculated above, initialize a new Ability
allAbilities.add(new PieceAbility(numOfTriggers,triggerIndexes,triggerRequirement,numOfEffectVariables,effectIndex,onlyEffectStrengthVariables));
}

    //set the moveset array values, given a String
    void setStaticMoveset(String line){
        //9 vertical lines means that we are dealing with linear movements,10 variables, 1 range, 8 direction, 1 movetype
        //10 vertical lines means we are dealing with knight-like movements,11 variables, 2 coordinates, 8 directions, 1 movetype
        //1 vertical line means we are dealing with an area movement, 2 variables, 1 radius,1 movetype
        int commaCounter=0;
        for(int x=0;x!=line.length();x++){
            if (line.charAt(x)=='|'){
                commaCounter++;
            }
        }

        //split the line string into it's 10 or 11 smaller strings
        String[] separated = line.split("\\|");


        if (commaCounter==1){
            setMovesetSquare(Integer.valueOf(separated[0]),Integer.valueOf(separated[1]));
        }
        if (commaCounter==9){
            setMovesetLines(Integer.valueOf(separated[0]),convertToBoolean(separated[1]),convertToBoolean(separated[2]),convertToBoolean(separated[3]),convertToBoolean(separated[4]),convertToBoolean(separated[5]),convertToBoolean(separated[6]),convertToBoolean(separated[7]),convertToBoolean(separated[8]),Integer.valueOf(separated[9]));
        }
        if (commaCounter==10){
            setMovesetCoord(Integer.valueOf(separated[0]),Integer.valueOf(separated[1]),convertToBoolean(separated[2]),convertToBoolean(separated[3]),convertToBoolean(separated[4]),convertToBoolean(separated[5]),convertToBoolean(separated[6]),convertToBoolean(separated[7]),convertToBoolean(separated[8]),convertToBoolean(separated[9]),Integer.valueOf(separated[10]));
        }
    }
//set the moveset array to being all 0s
    void setChangeableMovesetEmpty(){
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
                    changeableMoveset[x][y]=0;
            }
        }
    }
    /*
update the "moveset" array for a piece, by adding a linear movement
the integer movetype indicates the type of movement the piece gets, 1 is move/attack for example.
depending on the values of the booleans, different parts of the array moveset will be changed
for example, a rook with north=true but all other booleans false would only be able to move forwards
*/
    private void setMovesetLines(int range, boolean north, boolean northeast, boolean east, boolean southeast, boolean south, boolean southwest, boolean west, boolean northwest, int movetype){
       movetype=findMovetypeIndex(movetype);
        if (south == true) {
            for (int y = 0; y != 7; y++) {
                if (Math.abs(7 - y) <= range) {
                    staticMoveset[7][y] = movetype;
                }
            }
        }
        if (north == true) {
            for (int y = 8; y != 15; y++) {
                if (Math.abs(7 - y) <= range) {
                    staticMoveset[7][y] = movetype;
                }
            }
        }
        if (east == true) {
            for (int x = 8; x != 15; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[x][7] = movetype;
                }
            }
        }
        if (west == true) {
            for (int x = 0; x != 7; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[x][7] = movetype;
                }
            }
        }

        if (northeast==true){
            for (int x = 8; x != 15; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[x][x] = movetype;
                }
            }
        }
        if (southwest==true){
            for (int x = 8; x != 15; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[14-x][14-x] = movetype;
                }
            }
        }
        if (northwest==true){
            for (int x = 8; x != 15; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[14-x][x] = movetype;
                }
            }
        }
        if (southeast==true){
            for (int x = 8; x != 15; x++) {
                if (Math.abs(7 - x) <= range) {
                    staticMoveset[x][14-x] = movetype;
                }
            }
        }


    }
    /*
    update the "moveset" array for a piece, by adding a knight-like movement
    coord1 and coord2 tell the method which parts of array moveset to change
    for example, for knight the coord variables would be 1 and 2. because a knight moves up 2 and over 1
    the integer movetype indicates the type of movement the piece gets, 1 is move/attack for example.
    depending on the values of the booleans, different parts of the array moveset will be changed
    for example, a knight with upRight true but all other booleans false would only be able to move
    to a square 2 up and 1 right of itself.
    */
    private void setMovesetCoord(int coord1, int coord2, boolean upRight, boolean rightUp, boolean rightDown, boolean downRight, boolean downLeft, boolean leftDown, boolean leftUp, boolean upLeft, int movetype) {
        movetype=findMovetypeIndex(movetype);
        int biggerNumber;
        int smallerNumber;
        //make sure the bigger and smaller numbers are always in the same variables.
        //this ensures that if coord1 and coord2's values are swapped they still produce the same moveset
        //i.e 1,2 is the same as 2,1
        if (coord1>coord2){
            biggerNumber=coord1;
            smallerNumber=coord2;
        }else{
            biggerNumber=coord2;
            smallerNumber=coord1;
        }

        if (upRight==true){
            staticMoveset[7+smallerNumber][7+biggerNumber]=movetype;
        }
        if (rightUp==true){
            staticMoveset[7+biggerNumber][7+smallerNumber]=movetype;
        }

        if (rightDown==true){
            staticMoveset[7+biggerNumber][7-smallerNumber]=movetype;
        }
        if (downRight==true){
            staticMoveset[7+smallerNumber][7-biggerNumber]=movetype;
        }

        if (downLeft==true){
            staticMoveset[7-smallerNumber][7-biggerNumber]=movetype;
        }
        if (leftDown==true){
            staticMoveset[7-biggerNumber][7-smallerNumber]=movetype;
        }

        if (leftUp==true){
            staticMoveset[7-biggerNumber][7+smallerNumber]=movetype;
        }
        if (upLeft==true){
            staticMoveset[7-smallerNumber][7+biggerNumber]=movetype;
        }

    }
    //update the "moveset" array for a piece by adding an area movement, size indicates the radius of the area a
    //piece should be able to move to, the integer movetype indicates the type of movement the piece gets
    //1 is move/attack for example.
    private void setMovesetSquare(int size, int movetype){
        movetype=findMovetypeIndex(movetype);
        for(int x=7-size;x<=7+size;x++){
            for(int y=7-size;y<=7+size;y++){
                staticMoveset[x][y]=movetype;
            }
        }
        staticMoveset[7][7]=0;
    }

    //uses the static array moveTypeIndexes to find the proper index a movetype has
    int findMovetypeIndex(int movetype){
        int realMoveType=0;
        for(int x=0;x!=Piece.moveTypeCounter;x++){
            if (Piece.moveTypeIndexes[x]==movetype%1000){
                realMoveType=x;
                break;
            }
        }
        //if the movetype is unblockable, aka greater than 1000, the realMoveType should be increased by 1000 as well
        if (movetype>1000){
            realMoveType+=1000;
        }
        return realMoveType;
    }
//set the location of the piece on board (0-7)
    void setLocation(int x, int y){
        xLocation=x;
        yLocation=y;

    }

//adds a status effect to the piece
    void addStatusEffect(int status,int length){
        int newStatusIndex=allStatuses.size();
        allStatuses.add(new StatusEffect(status,length));
        updateStatusBooleans(allStatuses.get(newStatusIndex));
    }
//combine the static moveset array and the changeable moveset array into the array the piece will use for movement this turn
//
    void setMoveset(){
        //reset the moveset to being completely empty
     for(int x=0;x!=15;x++) {
         for(int y=0;y!=15;y++){
             moveset[x][y]=0;
         }
     }
        for(int x=0;x!=15;x++) {
            for(int y=0;y!=15;y++){
                if (staticMoveset[x][y]!=0){
                    moveset[x][y]=staticMoveset[x][y];
                }
                if (changeableMoveset[x][y]!=0){
                    moveset[x][y]=changeableMoveset[x][y];
                }
            }
        }


    }
//updates Status effects, reduces their duration and applies the effects on the piece
    void updateStatuses(int playerTurn){
        //turns survived incremented
        turnsSurvived++;
        if (isWhite==true&&playerTurn==2){
            updateAllStatuses();
        }
        if (isWhite==false&&playerTurn==1) {
            updateAllStatuses();
        }


    }

    void updateAllStatuses(){
        //status booleans reset
        resetStatusBooleans();
//loop through all status effects
        for(int x=0;x!=allStatuses.size();x++){
//if the status is timebased it's duration is decreased
            if(allStatuses.get(x).timeBased==true){
                allStatuses.get(x).statusEffectLength--;
            }
//if the duration reaches 0 the status is set to be removed
            if(allStatuses.get(x).statusEffectLength==0){
                allStatuses.get(x).setToBeRemoved();
            }
//the status effect has it's effects applied to the piece
            updateStatusBooleans(allStatuses.get(x));
        }
//statuses that have been set to be removed are removed
        for(int x=allStatuses.size()-1;x>=0;x--){
            if(allStatuses.get(x).setToBeRemoved ==true){
                allStatuses.remove(x);
            }
        }
    }
//set all status booleans to false
    void resetStatusBooleans(){
        abilitiesDisabled=false;
        movesDisabled=false;
        protectedFromAttacks=false;
        protectedFromAbilities=false;
        protectedFromStatuses=false;
        immovable=false;
        cantTargetKing=false;
    }
//set all status booleans based on the index of the status
    void updateStatusBooleans(StatusEffect status){
        switch (status.index){
            //stun status
            case 0:
               abilitiesDisabled=true;
               movesDisabled=true;

                break;
//root status
            case 1:
                immovable=true;
                break;
//silence status
            case 2:
                abilitiesDisabled=true;
                break;
//shielded status
            case 3:
                protectedFromAttacks=true;
                break;
//phase out status, whatever the heck that even means, james
            case 4:
                //phase out, whatever the heck that means, james
                break;
//setToBeRemovedFromBoard status, piece dies in x turns
            case 5:
                if(protectedFromStatuses==false) {
                    if (status.statusEffectLength == 0) {
                        setToBeRemovedFromBoard = true;
                        capturedByStatus = true;
                        statusCapturedBy = status.index;
                    }
                }
                break;
//invulnerable status
            case 6:
                resetStatusBooleans();
                removeAllStatusesButInvulnerable();
                protectedFromAttacks=true;
                protectedFromAbilities=true;
                protectedFromStatuses=true;

                break;
//ghosted, wow is this a pain to add to the game, will add later maybe
            case 7:

                break;
//armour
            case 8:
//nothing needs to happen here for armour
                break;
//magic armour
            case 9:
//nothing needs to happen here for armour
                break;
//can't target king status
            case 10:
                cantTargetKing=true;
                break;

//magic immune status
            case 13:
                protectedFromAbilities=false;
                break;

        }

    }
//currently invulnerable status removes all other statuses, so that is what this method does
    void removeAllStatusesButInvulnerable(){
        for(int x=allStatuses.size()-1;x>=0;x--){
            if(allStatuses.get(x).index!=6){
                allStatuses.remove(x);
            }
        }
    }

//this method is called when a piece moves, if they have any moves that can only be used as their first turn
    //they are no longer usable
    void removeOneTimeMovesMoves(){
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
               int blockable=0;
                if (staticMoveset[x][y]>1000&& staticMoveset[x][y]<2000){
                    blockable=1;
                }
                int movetype= staticMoveset[x][y]%1000;
                if (allMoveTypes[blockable][movetype].oneTimeUse==true){
                    staticMoveset[x][y]=0;
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
        filePath+=name.replace(" ","")+".png";
pieceImage=new Texture(Gdx.files.internal(filePath),true);
//pieceImage=new Texture(Gdx.files.internal(filePath));
        pieceImage.setFilter(Texture.TextureFilter.MipMapLinearLinear,Texture.TextureFilter.Linear);
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
    //used for opponent's pieces, currently used for black pieces, this method ensures pieces like pawns move in the
    //right direction
    void flipMoveset(){
        int[][] tempMoveset=new int[15][15];
        for(int x=0;x!=15;x++){
            for(int y=0;y!=15;y++){
                tempMoveset[x][y]= staticMoveset[14-x][14-y];
            }
        }
        staticMoveset =tempMoveset;
}
    //draw the piece to the screen
    void draw(SpriteBatch batch,int boardLocationx,int boardLocationy, int xLocToDraw,int yLocToDraw){

        sprite.setSize((72),(72));
        if (selected==true){
            sprite.setCenter(Gdx.input.getX(),618-Gdx.input.getY());
        }else {
            sprite.setCenter(xLocToDraw, yLocToDraw);
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

    //if a string has is "1", return true, otherwise return false
    boolean convertToBoolean(String convert){
        boolean converted=false;
        if (Integer.valueOf(convert)==1){
            converted=true;
        }
        return converted;
    }
}
