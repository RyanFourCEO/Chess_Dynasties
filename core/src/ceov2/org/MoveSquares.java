package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MoveSquares {
    int[][][] allMoveSquares;
    long timeSincePieceSelected;
    int boardSize;
    Texture glow;
    Texture moveTextures;
    Texture[] highlights = new Texture[2];
    Texture[] reticle = new Texture[2];

    MoveSquares(int bs, int time, int size) { // takes in board size, the time variable, and the amount of movetypes there are
        timeSincePieceSelected = System.currentTimeMillis() - time;
        boardSize = bs;
        allMoveSquares = new int[size][8][8];
        glow = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true); // placeholder
    }

    void findMovesToDraw(GameState main) {
        // given gamestate
        // find the movesquares that could be drawn of EVERY PIECE in the gamestate
        // but not their specific aesthetic
        // since only one piece has a movesquare drawn at each time
        // we find that piece's movesquares in a different method
        int msX, msY;
        for (int i = 0; i <= main.allPiecesOnBoard.size(); i++) {
            for (int x = 0; x < 8; x++) {
                for (int y = 0; y < 8; y++) {
                    if (main.pieceSelected) {
                        msX = x - main.selectedPieceLocx + 7;
                        msY = y - main.selectedPieceLocy + 7;
                    } else {
                        msX = x - main.loc[0] + 7;
                        msY = y - main.loc[1] + 7;
                    }
                    allMoveSquares[i][x][y] = main.allPiecesOnBoard.get(i).moveset[msX][msY];
                }
            }
        }
    }

    private int[][][] findHowToDrawMoves(GameState main) {
        // returns a state for each move using mouse vars
        // compares valid moves
        // finds which move you are hovering over
        int[][][] ret = new int[8][8][];
        int msX, msY;
        int mouseX = main.loc[0];
        int mouseY = main.loc[1];
        int valid, type, index, hovered;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (main.pieceSelected) {
                    msX = x - main.selectedPieceLocx + 7;
                    msY = y - main.selectedPieceLocy + 7;
                    index = main.selectedPiece;
                    type = allMoveSquares[index][x][y];
                } else {
                    msX = x - mouseX + 7;
                    msY = y - mouseY + 7;
                    index = main.piecesOnBoard[mouseX][mouseY];
                    type = allMoveSquares[index][x][y];
                }

                ret[x][y][0] = type;

                if (main.loc[0] == x && main.loc[1] == y) { // if the move is a selected one
                    hovered = 1;
                } else { // if the move is valid but not selected
                    hovered = 0;
                }

                ret[x][y][1] = hovered;

                if (main.allPiecesOnBoard.get(index).validMoves[msX][msY]) {
                    valid = 1;
                } else {
                    valid = 0;
                }

                ret[x][y][2] = valid;
            }
        }
        return ret;
    }

    void drawAllMoves(int index, GameState main, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        int[][][] mtd = findHowToDrawMoves(main);
        float alpha;
        if (timeSincePieceSelected <= 500)
            alpha = 1;
        else {
            alpha = (float) Math.sin(3.142 * (1500 - timeSincePieceSelected) / 2000);
        }

        float glowAlpha = (float) Math.sin(3.142 * (1500 - timeSincePieceSelected) / 2000);

        float[][][] boardCoord = new float[2][8][8];

        //Texture[] squares = new Texture[99]; TODO finish this method
        //TODO modify the move square texture with alpha variable

        float size = main.boardSize / (float) 8;

        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (mtd[x][y][0] != 0) {
                    boardCoord[0][x][y] = (size * x) + main.boardPosX;
                    boardCoord[1][x][y] = (size * y) + main.boardPosX;
                    //draw glow
                    // TODO Modify the texture (that doesn't exist) with glowAlpha
                    //batch.draw(glow, xLoc, yLoc, size, size);

                    if (mtd[x][y][1] == 1) {
                        if (mtd[x][y][2] == 1) {
                            // valid
                            // batch.draw(highlights[1],boardCoord[0][x][y],boardCoord[1][x][y], size, size);
                            // batch.draw(reticle[1],boardCoord[0][x][y],boardCoord[1][x][y], size, size);
                        }
                        // draw either using the "valid" or the "invalid" move hover square highlight
                        else if (mtd[x][y][2] == 0) {
                            // invalid
                            // batch.draw(highlights[0],boardCoord[0][x][y],boardCoord[1][x][y], size, size);
                            // batch.draw(reticle[0],boardCoord[0][x][y],boardCoord[1][x][y], size, size);
                        }
                    }

                    if (!main.pieceSelected) {
                        //draw move square
                        //batch.draw(moveSquares[ret[x][y][0]], boardCoord[0][x][y], boardCoord[1][x][y], size, size);
                    }
                }
            }
        }
    }
}
