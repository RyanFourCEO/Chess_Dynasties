package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class GraphicsUtils {

static int graphicsQuality=3;
    static Texture loadTexture(String filePath){
        Texture texture;

        switch (graphicsQuality){
            case 0:
                texture=new Texture(Gdx.files.internal(filePath));
                break;

            case 1:
                texture=new Texture(Gdx.files.internal(filePath),true);
                texture.setFilter(Texture.TextureFilter.MipMapNearestNearest,Texture.TextureFilter.MipMapNearestNearest);
                break;

            case 2:
                texture=new Texture(Gdx.files.internal(filePath),true);
                texture.setFilter(Texture.TextureFilter.MipMapLinearNearest,Texture.TextureFilter.MipMapLinearNearest);
                break;

            case 3:
                texture=new Texture(Gdx.files.internal(filePath),true);
                texture.setFilter(Texture.TextureFilter.MipMapLinearLinear,Texture.TextureFilter.MipMapLinearLinear);
                break;

            default: texture=new Texture(filePath);
        }

        return texture;
    }
}
