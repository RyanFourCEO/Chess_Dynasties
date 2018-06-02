package ceov2.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.Viewport;
import javax.xml.soap.Text;
import java.util.ArrayList;

public class CEOV2 extends ApplicationAdapter {

	//declare constants
	public static final int WINDOW_SIZE_X=1100;
	public static final int WINDOW_SIZE_Y=618;
	public static final int SQUARE_SIZE=60;
	public static final int LINE_WIDTH=4;
	//Declare graphics objects
	private Stage stage;
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	Texture img2;
    Skin skin;
    Sprite sprite1;
    Sprite sprite2;
    MouseVars mouseVars;
	Viewport viewport;
	Camera camera;
    //declare game variables
    LiveGame game;
    //when startGame is true, a LiveGame object will be created
    boolean startGame=false;
    //when gameIslive is true, the code for running a live game will execute
    boolean gameIsLive=false;
    //when endGame is true, the LiveGame object will be set to null
    boolean endGame=false;

	//initialization/loading of the games resources
	@Override
	public void create () {
		//camera = new OrthographicCamera(1100,618);
        mouseVars=new MouseVars();
		//viewport = new FitViewport(1100, 618, camera);
		//the stage and the skin are used for implementing buttons
		stage=new Stage(new ScreenViewport());
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		//"reset board" button initialized with the selected skin
		final TextButton button = new TextButton("Reset Board", skin, "default");
		//set buttons size and position on screen
		button.setWidth(200f);
		button.setHeight(20f);
		button.setPosition(Gdx.graphics.getWidth() /2 - 400f, Gdx.graphics.getHeight()/2 +80f);

		//the method that the button executes when clicked
		button.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				endGame=true;
				gameIsLive=false;
			}
		});

		stage.addActor(button);//this adds the button to the screen
		Gdx.input.setInputProcessor(stage);//this allows the button to be used

		//for the prototype, the game is always set to start immediately
startGame=true;
//graphics objects are initialized
		batch = new SpriteBatch();
		img = new Texture("unknown.png");
		sprite1=new Sprite(img);
sprite1.scale(-0.3f);
sprite1.setCenter(190,150);
		font= new BitmapFont();
		font.setColor(Color.RED);
        img2=new Texture("chessBoard.png");
		sprite2=new Sprite(img2);
sprite2.setSize(480,480);
sprite2.setCenter(640,290);



	}



//The main loop of the game, all graphics will be drawn here, and game logic is executed here
	@Override
	public void render () {
mouseVars.setMouseVariables();

//clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		batch.setProjectionMatrix(camera.combined);
		//draw all graphics objects

		batch.begin();
		sprite1.draw(batch);
		sprite2.draw(batch);
		font.draw(batch,"hello",250,300);



		stage.draw();
		batch.end();

		if (startGame==true){
			//game object initialized
            game=new LiveGame();
			startGame=false;
			gameIsLive=true;
		}
		if (gameIsLive==true){
			//all game logic occurs through this call
			game.performGameLogic(batch,mouseVars);
		}

if (endGame==true){
			endGame=false;
				game.state.deleteGraphics();
				game = null;

	//for now, in prototype, when a game ends a new game always begins
				startGame=true;
}




	}
	
	@Override
	public void dispose () {
		//graphics objects deleted
		batch.dispose();
		img.dispose();
		font.dispose();
		img2.dispose();
		stage.dispose();
		skin.dispose();
	}

	//later when screen resizing is supported this will be important
	@Override
	public void resize(int width, int height){
		//viewport.setScreenSize(width,height);
	}
}
