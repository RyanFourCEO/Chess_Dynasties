package ceov2.org;

import java.util.ArrayList;

public class PieceMoveType {
    //this determines what happens when a piece moves
    int moveType;
    //boolean blockable: can the movetype move through other pieces?
    boolean blockable;
    //target booleans: what can the movetype target? Empty squares, allies and/or enemies?
    boolean emptyIsTarget;
    boolean allyIsTarget;
    boolean enemyIsTarget;
    //this is for things like pawn's 2 square forward move, that can only be used on the very first turn
    boolean oneTimeUse=false;

    public PieceMoveType(int moveType,boolean blockable,boolean emptyIsTarget,boolean allyIsTarget,boolean enemyIsTarget ){
   this.moveType=moveType;
   this.blockable=blockable;
   this.emptyIsTarget=emptyIsTarget;
   this.allyIsTarget=allyIsTarget;
   this.enemyIsTarget=enemyIsTarget;
    }

    void setIsOneTimeUse(){
        oneTimeUse=true;
}
//see if a square is a valid target for this movetype
    boolean checkIsValidTarget(int target,int playerTurn){
        boolean isValidTarget=false;
        if (target==0&&emptyIsTarget==true){
            isValidTarget=true;
        }

        if (playerTurn==1) {
            if (target == 1 && allyIsTarget == true) {
                isValidTarget = true;
            }
            if (target == 2 && enemyIsTarget == true) {
                isValidTarget = true;
            }
        }else{
            if (target == 2 && allyIsTarget == true) {
               isValidTarget = true;
            }
            if (target == 1 && enemyIsTarget == true) {
                isValidTarget = true;
            }
       }
    return isValidTarget;
}
//execute a move
    void executeMove(int xTarget, int yTarget, int xLoc, int yLoc,GameState state){
        //if the piece moves, and has a one time use movetype, this method removes the one time use moves so they can't be used again
if (moveType!=0){
    updatePieceMoveset(xLoc,yLoc,state.piecesOnBoard,state.allPiecesOnBoard);
}
switch (moveType){
    case 0:
//movetype 0, the empty square movetype
        break;
    case 1:
//movetype 1, the standard movetype, moves a piece to a square, if the square is occupied the piece is taken
        if (state.piecesOnBoard[xTarget][yTarget]>=0) {
            capturePiece(state.allPiecesOnBoard.get(state.piecesOnBoard[xTarget][yTarget]),state.moraleTotals);
        }
        //the square the piece is targetting becomes occupied by the piece, i.e. it moves there
        state.boardState[xTarget][yTarget]=state.boardState[xLoc][yLoc];
        state.piecesOnBoard[xTarget][yTarget]=state.piecesOnBoard[xLoc][yLoc];
        //the square the piece was moving from becomes unoccupied
        state.boardState[xLoc][yLoc]=0;
        state.piecesOnBoard[xLoc][yLoc]=-1;
        //the piece moving has it's internal location changed
        state.allPiecesOnBoard.get(state.piecesOnBoard[xTarget][yTarget]).setLocation(xTarget,yTarget);

        break;
}
//used for pieces like pawns which can only move 2 squares on their very first move, this will likely be replaced
        //by a more elegant solution later


    }

    void updatePieceMoveset(int xLoc,int yLoc, int[][] piecesOnBoard, ArrayList<Piece> allPiecesOnBoard){
        for(int x=0;x!=15;x++) {
            for(int y=0;y!=15;y++){
                int movetype=allPiecesOnBoard.get(piecesOnBoard[xLoc][yLoc]).moveset[x][y];
                int blockable;
                if (movetype>1000&&movetype<2000){
                    movetype=movetype%1000;
                    blockable=1;
                }else{
                    blockable=0;
                }
                if (Piece.allMoveTypes[blockable][movetype].oneTimeUse==true){
                  allPiecesOnBoard.get(piecesOnBoard[xLoc][yLoc]).moveset[x][y]=0;

                }
            }

        }
    }

    void capturePiece(Piece piece, int[] moraleTotals){
        piece.capture();
        if(piece.isWhite==true){
            moraleTotals[0]-=piece.moraleCost+piece.moralePenalty;
        }else{
            moraleTotals[1]-=piece.moraleCost+piece.moralePenalty;
        }
    }
}
