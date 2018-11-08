package ceov2.org;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelWithShader extends Label {

    public LabelWithShader(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public LabelWithShader(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public LabelWithShader(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public LabelWithShader(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public LabelWithShader(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    //draw the label, but with a distance field shader
    public void draw(Batch batch, float parentAlpha) {
        batch.end();
        batch.setShader(Shaders.distanceFieldShader);
        Shaders.prepareDistanceFieldShader();
        batch.begin();
        super.draw(batch, parentAlpha);
        batch.end();
        batch.setShader(Shaders.defaultShader);
        batch.begin();
    }

}
