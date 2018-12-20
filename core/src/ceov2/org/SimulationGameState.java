package ceov2.org;

import com.badlogic.gdx.Gdx;

public class SimulationGameState extends GameState{

    public SimulationGameState(boolean neededForSomeReason){
        super(neededForSomeReason);
    }

    //load armies, does not load graphics
    void loadArmies() {
        //load the army1 file, this will contain the setup for the white army
        String army = Gdx.files.internal("UserFiles\\armies\\army1.txt").readString();
        String[] separatedNames;
        //separate the loaded string from the file into it's 16 piece names
        separatedNames = army.split(",");
        //load all the pieces corresponding to the piece names from the file
        //load all white pieces first, so they take up the first 16 places in the array
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(true, separatedNames[x]));
        }
//repeat the above for the black pieces, which are loaded from army2
        army = Gdx.files.internal("UserFiles\\armies\\army2.txt").readString();
        separatedNames = army.split(",");
        for (int x = 0; x != 16; x++) {
            allPiecesOnBoard.add(new Piece(false, separatedNames[x]));
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }

    //same as above loadArmies method, but loads armies from two strings containing all pieces
    void loadArmies(int colour, String army, String oppArmy) {
        //this if statement exists only to ensure the first 16 pieces added to the array allPiecesOnBoard
        //are white.If the user is player one, aka white, load their pieces first,otherwise load their opponent's
        //pieces
        if (colour == 1) {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = army.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(true, separated[x]));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = oppArmy.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(false, separated[x]));
            }
        } else {
            //separate the loaded string from the file into it's 16 piece names
            String[] separated = oppArmy.split(",");
            //load all the pieces corresponding to the piece names from the String
            //load all of player 1's pieces first, so they take up the first 16 places in the array
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(true, separated[x]));
            }
//repeat the above for the black pieces, which are loaded from army2
            separated = army.split(",");
            for (int x = 0; x != 16; x++) {
                allPiecesOnBoard.add(new Piece(false, separated[x]));
            }
        }
        //clear tempPieces as we no longer need to load pieces, which is tempPieces' only purpose
        tempPieces.clear();
    }

    void projectHoveredMove(int indexOfPieceMoving) {
        //if the input string was valid, continue on
        //find the location of the move in the 15x15 moveset array
        int moveLocXOnMoveset = convertBoardLocToMovesetArray(allPiecesOnBoard.get(indexOfPieceMoving).xLocation,mouseLoc[0]);

        int moveLocYOnMoveset = convertBoardLocToMovesetArray(allPiecesOnBoard.get(indexOfPieceMoving).yLocation, mouseLoc[1]);

        //find all valid moves , then see if the move is valid
        findAllValidMoves();
        //find the movetype the piece is using
        int movetypePieceUsing = allPiecesOnBoard.get(indexOfPieceMoving).moveset[moveLocXOnMoveset][moveLocYOnMoveset];
        //if the move is valid, execute the move
        if (allPiecesOnBoard.get(indexOfPieceMoving).validMoves[moveLocXOnMoveset][moveLocYOnMoveset]) {
            executeMove(mouseLoc[0], mouseLoc[1], allPiecesOnBoard.get(indexOfPieceMoving), movetypePieceUsing);
            updateBoard();
        }
    }

}
