package ceov2.org;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MoveSquares {
    long timeSincePieceSelected;
    int boardSize;
    float[][] rgba = new float[32][4];
    float[][] rgba2 = new float[32][4];
    int[] special = new int[32];
    char[] symbols = new char[32];

    MoveSquares(int bs, PieceMoveType[] p) {
        timeSincePieceSelected = 0;
        boardSize = bs;
        for (int i = 0; i < p.length; i++) {
                rgba[i] = p[i].getColor()[0];
                rgba2[i] = p[i].getColor()[1];
        }
    }

    private void drawMoveRect(ShapeRenderer shapeRenderer, int movetype, int x, int y, int state, int time) {

    }
}
