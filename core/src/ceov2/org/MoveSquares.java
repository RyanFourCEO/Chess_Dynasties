package ceov2.org;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MoveSquares {
    int[][][] allMoveSquares;
    long timeSincePieceSelected;
    int boardSize;

    MoveSquares(int bs, int time, int size) {
        timeSincePieceSelected = System.currentTimeMillis() - time;
        boardSize = bs;
        allMoveSquares = new int[size][8][8];
    }

    void findMovesToDraw(GameState main) {
        // given gamestate
        // find the movesquares of EVERY PIECE in the gamestate
        // but not their specific aesthetic

        //            ShapeRenderer shapeRenderer = new ShapeRenderer();
        //            Gdx.gl.glLineWidth(boardSize / (float) 60);
        //            shapeRenderer.setAutoShapeType(true);
        //            Gdx.gl.glEnable(GL20.GL_BLEND);

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

    int[][][] findHowToDrawMoves(GameState main) {
        // returns a state for each move using mouse vars
        // compares valid moves
        // finds which move you are hovering over
        int[][][] ret = new int[8][8][];
        float alpha;
        float size = 0;
        float offset = 0;
        if (timeSincePieceSelected <= 500)
            alpha = 1;
        else {
            alpha = (float) Math.sin(3.142 * (1500.0 - timeSincePieceSelected) / 2000.0);
        }
        int msX, msY;
        int mouseX = main.loc[0];
        int mouseY = main.loc[1];
        int valid;
        int type;
        int index;
        int hovered;
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
        float alpha = 1;
        if (!main.pieceSelected) {
            alpha /= 2.0;
        }
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                if (mtd[x][y][0] != 0) {
                    if (mtd[x][y][1] == 1) {
                        drawSelectedHighlight(shapeRenderer, x, y, mtd[x][y][2]);
                    }
                    //draw move square
                }
            }
        }
    }

    private void drawMoveSquare(SpriteBatch batch, Texture texture, float xLoc, float yLoc, float alpha, float size) {
        batch.draw(texture, xLoc, yLoc, size, size);
        //TODO needs way to use ALPHA, probably IN THE SUPERMETHOD TO THIS
    }

    private void drawSelectedHighlight(ShapeRenderer shapeRenderer, int x, int y, int valid) {
        float size = boardSize / (float) 9;
        float[] color = {0, 0, 0};
        if (valid == 0) {
            size = boardSize / (float) 18;
            color[0] = 1;
        } else if (valid == 1) {
            size = boardSize / (float) 11.25;
            color[2] = 1;
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color[0], color[1], color[2], (float) 0.5);
        shapeRenderer.rect(x, y, size, size);
        shapeRenderer.end();
    }

    private void drawGlow(SpriteBatch batch, Texture texture, float xLoc, float yLoc, float size) { // takes a sprite and
        float alpha = 1;
        alpha = (float) Math.sin(3.142 * (1500.0 - timeSincePieceSelected) / 2000.0);
        batch.draw(texture, xLoc, yLoc, size, size);
        //TODO needs way to use ALPHA, probably IN THE SUPERMETHOD TO THIS
    }
}
