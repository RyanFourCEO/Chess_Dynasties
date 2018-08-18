package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class Shaders {
    static final String vertexShader=Gdx.files.internal("VertShader.txt").readString();
    static final String fragmentShader=Gdx.files.internal("FragmentShader.txt").readString();
    static final ShaderProgram inversionShader=new ShaderProgram(vertexShader,fragmentShader);
    static final ShaderProgram defaultShader=SpriteBatch.createDefaultShader();

}
