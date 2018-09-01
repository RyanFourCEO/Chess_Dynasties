package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.DistanceFieldFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Shaders {
    static final String vertexShader=Gdx.files.internal("Shaders\\VertShader.txt").readString();
    static final String fragmentShader=Gdx.files.internal("Shaders\\FragmentShader.txt").readString();
    static final String distanceFieldVertexShader=Gdx.files.internal("Shaders\\DistanceFieldVertexShader.txt").readString();
    static final String distanceFieldFragShader=Gdx.files.internal("Shaders\\DistanceFieldFragShader.txt").readString();

    static final ShaderProgram inversionShader=new ShaderProgram(vertexShader,fragmentShader);
    static final ShaderProgram defaultShader=SpriteBatch.createDefaultShader();
    static final ShaderProgram distanceFieldShader=new ShaderProgram(distanceFieldVertexShader,distanceFieldFragShader);

    static void prepareDistanceFieldShader() {
        distanceFieldShader.begin();
        distanceFieldShader.setUniformf("smoothing",(0.17f*1100)/Gdx.app.getGraphics().getWidth());
        distanceFieldShader.end();
    }




}
