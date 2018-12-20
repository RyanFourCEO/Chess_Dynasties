package ceov2.org;

public class PieceMoveType {
    //this determines what happens when a piece moves
    int moveType;
    //boolean blockable: can the movetype move through other pieces?
    boolean blockable;

    //can the movetype jump over exactly one piece?
    boolean canJumpOverOnePiece = false;
    //target booleans: what can the movetype target? Empty squares, allies and/or enemies?
    boolean emptyIsTarget;
    boolean allyIsTarget;
    boolean enemyIsTarget;

    //this is for things like pawn's 2 square forward move, that can only be used on the very first move the piece makes
    boolean oneTimeUse = false;

    //for displaying move type squares on board to user
    float[] color = new float[4];
    float[] innerColor = new float[4];

    //does the movetype cause the piece to change it's position on the board?
    boolean movetypeMovesPieceWhenTargetingAlly = true;
    boolean movetypeMovesPieceWhenTargetingEnemy = true;
    boolean movetypeMovesPieceWhenTargetingEmpty = true;

    //does the movetype cause the piece to capture a piece when it's used?
    boolean movetypeCapturesEnemies = true;
    boolean movetypeCapturesAllies = true;


    public PieceMoveType(int moveType, boolean blockable, boolean emptyIsTarget, boolean allyIsTarget, boolean enemyIsTarget, boolean movetypeMovesPieceWhenTargetingAlly, boolean movetypeMovesPieceWhenTargetingEnemy, boolean movetypeMovesPieceWhenTargetingEmpty) {
        this.moveType = moveType;
        this.blockable = blockable;
        this.emptyIsTarget = emptyIsTarget;
        this.allyIsTarget = allyIsTarget;
        this.enemyIsTarget = enemyIsTarget;
        this.movetypeMovesPieceWhenTargetingAlly = movetypeMovesPieceWhenTargetingAlly;
        this.movetypeMovesPieceWhenTargetingEnemy = movetypeMovesPieceWhenTargetingEnemy;
        this.movetypeMovesPieceWhenTargetingEmpty = movetypeMovesPieceWhenTargetingEmpty;
    }

    public PieceMoveType(int moveType, boolean blockable, boolean emptyIsTarget, boolean allyIsTarget, boolean enemyIsTarget) {
        this.moveType = moveType;
        this.blockable = blockable;
        this.emptyIsTarget = emptyIsTarget;
        this.allyIsTarget = allyIsTarget;
        this.enemyIsTarget = enemyIsTarget;

    }

    public PieceMoveType(boolean canJumpOverOnePiece, int moveType, boolean emptyIsTarget, boolean allyIsTarget, boolean enemyIsTarget) {
        this.moveType = moveType;
        blockable = false;
        this.canJumpOverOnePiece = canJumpOverOnePiece;
        this.emptyIsTarget = emptyIsTarget;
        this.allyIsTarget = allyIsTarget;
        this.enemyIsTarget = enemyIsTarget;

    }

    public PieceMoveType(int moveType, boolean blockable, boolean emptyIsTarget, boolean allyIsTarget, boolean enemyIsTarget, boolean movetypeCapturesAllies, boolean movetypeCapturesEnemies) {
        this.moveType = moveType;
        this.blockable = blockable;
        this.emptyIsTarget = emptyIsTarget;
        this.allyIsTarget = allyIsTarget;
        this.enemyIsTarget = enemyIsTarget;
        this.movetypeCapturesEnemies = movetypeCapturesEnemies;
        this.movetypeCapturesAllies = movetypeCapturesAllies;

    }

    float[][] getColor(){
        float[][] ret = new float[2][4];
        ret[0] = color;
        ret[1] = innerColor;
        return ret;
    }

    void setIsOneTimeUse() {
        oneTimeUse = true;
    }

    //see if a square is a valid target for this movetype
    boolean checkIsValidTarget(int target, int playerTurn, boolean immovable) {
        boolean isValidTarget = false;
        if (target == 0 && emptyIsTarget) {
            isValidTarget = true;
            //if the piece has the status effect that makes it immovable, and the movetype would cause it to move, the move can not be made
            //and validTarget is set to false
            if (immovable && movetypeMovesPieceWhenTargetingEmpty) {
                isValidTarget = false;
            }
        }

        if (playerTurn == 1) {
            if (target == 1 && allyIsTarget) {
                isValidTarget = !immovable || !movetypeMovesPieceWhenTargetingAlly;
            }
            if (target == 2 && enemyIsTarget) {
                isValidTarget = !immovable || !movetypeMovesPieceWhenTargetingEnemy;
            }
        } else {
            if (target == 2 && allyIsTarget) {
                isValidTarget = !immovable || !movetypeMovesPieceWhenTargetingAlly;
            }
            if (target == 1 && enemyIsTarget) {
                isValidTarget = !immovable || !movetypeMovesPieceWhenTargetingEnemy;
            }
        }
        return isValidTarget;
    }

}
