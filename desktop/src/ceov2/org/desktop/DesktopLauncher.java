package ceov2.org.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ceov2.org.CEOV2;

public class DesktopLauncher {


	public static void main (String[] arg) {
	//config contains information about how the game window should be made
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//set game window title, width and height
		config.title="Chess Dynasties";
		config.width=1100;
		config.height=618;
		config.resizable=false;
		//create window
		new LwjglApplication(new CEOV2(), config);


	}
}
