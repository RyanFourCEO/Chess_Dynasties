package ceov2.org;

import java.util.ArrayList;

//This class holds the differences between two GameState objects
//this data is currently only used to graphically show the user a preview of their moves
//aka what the new GameState will look like after they make their move, in relation to the
//old GameState
public class DiffBetweenGameStates {
//the movetype that was used that caused the differences between the gameStates
//only applicable if the gameStates being compared are exactly one move "apart"
int moveTypeUsed;
//morale totals
int currentMoraleWhite;
int currentMoraleBlack;
int newMoraleWhite;
int newMoraleBlack;
int moraleDifferenceWhite;
int moraleDifferenceBlack;
//piece locations
ArrayList<Integer> indexesOfPiecesWhichHaveMoved = new ArrayList<Integer>();
int[] currentPieceLocationsX;
int[] currentPieceLocationsY;
int[] newPieceLocationsX;
int[] newPieceLocationsY;
int[] diffBetweenPieceLocationsX;
int[] diffBetweenPieceLocationsY;
//game results
boolean lossForWhite = false;
boolean lossForBlack = false;
boolean draw = false;
//which pieces are not captured in the initial gameState, but are captured in the new GameState
boolean[] pieceCaptured;

//constructor, simply make the arrays the appropriate size to be able to hold all the pieces that exist
//in each GameState
public DiffBetweenGameStates(int currentNumOfPieces, int newNumOfPieces){
    currentPieceLocationsX = new int[currentNumOfPieces];
    currentPieceLocationsY = new int[currentNumOfPieces];
    newPieceLocationsX = new int[newNumOfPieces];
    newPieceLocationsY = new int[newNumOfPieces];
    diffBetweenPieceLocationsX = new int[currentNumOfPieces];
    diffBetweenPieceLocationsY = new int[currentNumOfPieces];
    pieceCaptured = new boolean[newNumOfPieces];
}

void setMoraleTotals(int currentMoraleWhite, int currentMoraleBlack, int newMoraleWhite,int newMoraleBlack){
    this.currentMoraleWhite = currentMoraleWhite;
    this.currentMoraleBlack = currentMoraleBlack;
    this.newMoraleWhite = newMoraleWhite;
    this.newMoraleBlack = newMoraleBlack;
    moraleDifferenceWhite = currentMoraleWhite - newMoraleWhite;
    moraleDifferenceBlack = currentMoraleBlack - newMoraleBlack;
}
//determine if the "new" GameState has resulted in a win loss or draw
void calculateLossWinDraw() {
    if ((newMoraleBlack <= 0) && (newMoraleWhite <= 0)) {
        draw = true;
    } else if (newMoraleBlack <= 0){
        lossForBlack = true;
    } else if (newMoraleWhite <= 0){
        lossForWhite = true;
    }
}

void setPieceHasMoved(int indexOfPieceThatMoved, int currentXLoc, int currentYLoc,int newXLoc,int newYLoc,int xDiff, int yDiff){
    indexesOfPiecesWhichHaveMoved.add(indexOfPieceThatMoved);
    currentPieceLocationsX[indexOfPieceThatMoved] = currentXLoc;
    currentPieceLocationsY[indexOfPieceThatMoved] = currentYLoc;
    newPieceLocationsX[indexOfPieceThatMoved] = newXLoc;
    newPieceLocationsY[indexOfPieceThatMoved] = newYLoc;
    diffBetweenPieceLocationsX[indexOfPieceThatMoved] = xDiff;
    diffBetweenPieceLocationsY[indexOfPieceThatMoved] = yDiff;
}

void setPieceHasBeenCaptured(int indexOfPieceThatWasCaptured){
    pieceCaptured[indexOfPieceThatWasCaptured] = true;
}
}
