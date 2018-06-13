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
    //this is for things like pawn's 2 square forward move, that can only be used on the very first move the piece makes
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


    void capturePiece(Piece piece, int[] moraleTotals,GameState state){
        piece.capture(state);
        if(piece.isWhite==true){
            moraleTotals[0]-=piece.moraleCost+piece.moralePenalty;
        }else{
            moraleTotals[1]-=piece.moraleCost+piece.moralePenalty;
        }
    }
}
