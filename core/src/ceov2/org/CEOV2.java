package ceov2.org;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.net.SocketHints;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CEOV2 extends ApplicationAdapter implements InputProcessor {

	//declare constants
	public static final int MAIN_MENU_STATE=0;
	public static final int GAME_IS_LIVE_STATE =1;
	public static final int ARMY_BUILDING_STATE=2;

    //allows multiple inputprocessors to be used at once
	InputMultiplexer inputMultiplexer;
	//Declare graphics objects

	//The main menu is the menu the user starts at
	public Menu mainMenu;
	boolean mainMenuDisabled=false;

	//the options menu will always be available to the user
    public Menu optionsMenu;


    boolean currentlyInOptionsMenu=false;


	SpriteBatch batch;
	Texture img;
	BitmapFont font;
    Sprite sprite1;
    MouseVars mouseVars;

    //unused probably important later
	Viewport viewport;
	Camera camera;

    //declare primary game objects,
	// "game" controls what happens when a player enters a game
    LiveGame game;
    //armyMaker controls what happens when a player is making their army
    ArmyMaker armyMaker;
    //current state tells what "mode" the game is in
	//depending on the state of the game different menus will be loaded
	//and different logic will be executed. If a player is in a game
	//the game logic will be executed, etc
	//when is is equal to 0, that means the player is at the main menu
    //when is equal to 1, that means a game is occurring
	//when it is equal to 2, that means armies are being made
    int currentGameState=MAIN_MENU_STATE;
	//initialization/loading of the games resources
	@Override
	public void create () {


		try {
		String ipAddress="127.0.0.1";
		ServerSocketHints serverSocketHint = new ServerSocketHints();
		serverSocketHint.acceptTimeout = 500;
		ServerSocket serverSocket = Gdx.net.newServerSocket(Protocol.TCP,ipAddress, 9021, serverSocketHint);
		SocketHints socketHints = new SocketHints();
		socketHints.connectTimeout = 500;
		//Socket socket2 = serverSocket.accept(socketHints);

			Socket socket = Gdx.net.newClientSocket(Protocol.TCP, ipAddress, 9022, socketHints);
			System.out.println("hello");
			String test = "testeroo \n";
			try {
				// write our entered message to the stream
				socket.getOutputStream().write(test.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}

		Socket socket2 = serverSocket.accept(null);
		try {
			BufferedReader buffer = new BufferedReader(new InputStreamReader(socket2.getInputStream()));
			System.out.println(buffer.readLine());
		} catch (IOException e) {
			e.printStackTrace();
		}


		}catch(GdxRuntimeException e){
			System.out.println("failed to connect to server");
		}




		camera = new PerspectiveCamera();
		viewport = new FitViewport(1100, 618, camera);

		inputMultiplexer = new InputMultiplexer();


		//load the mainMenu, this menu is the first menu the user sees
        mainMenu =new Menu(inputMultiplexer);
        //load the options menu, this is just one button that can bring up further options for the player
		//including screen resolution options and audio options
		optionsMenu=new Menu(inputMultiplexer);
        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(this);

        //load the main menu's UI components
        loadMainMenuUIComponents();
        //load the single button that the user can press to bring up their various options
		loadOptionsMenuUIComponents();

//mouseVars holds the mouse's variables, it's position and whether or not it is clicked
//every time the main loop executes this object finds the variables again
        mouseVars=new MouseVars();

//graphics objects are initialized
		batch = new SpriteBatch();
		img = new Texture(Gdx.files.internal("unknown.png"),true);
		img.setFilter(Texture.TextureFilter.MipMapLinearNearest,Texture.TextureFilter.Linear);
		sprite1=new Sprite(img);
        sprite1.setSize(200,200);
        sprite1.setCenter(1000,379);
		font= new BitmapFont();
		font.setColor(Color.RED);
	}

//The main loop of the game, all graphics will be drawn here, and game logic is executed here
	@Override
	public void render () {


		//collect the mouse variable for this frame
mouseVars.setMouseVariables();
//clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//this just draws f3's stupid drawing
		batch.begin();
		batch.draw(sprite1,sprite1.getX(),sprite1.getY(),sprite1.getWidth(),sprite1.getHeight());
		batch.end();

		//draw the options menu
batch.begin();
	optionsMenu.stage.getViewport().apply();
	optionsMenu.stage.draw();
batch.end();

//based on the state of the game, execute certain code
switch (currentGameState){
//if in the main menu, ensure the main menu is enabled and draw the main menu components
	case MAIN_MENU_STATE:
		if (mainMenuDisabled==true){
			mainMenuDisabled=false;
			mainMenu.enable();
		}
			mainMenu.stage.getViewport().apply();
			mainMenu.stage.draw();
		break;

	case GAME_IS_LIVE_STATE:

			game.performGameLogic(batch, mouseVars);
		if (game.gameOver==true) {
			setAllObjectsNull();
			currentGameState=MAIN_MENU_STATE;
		}
		break;

	case ARMY_BUILDING_STATE:
		armyMaker.runArmyMaker(batch,mouseVars);
		if (armyMaker.exitArmyMaker==true){
			setAllObjectsNull();
			currentGameState=MAIN_MENU_STATE;
		}
		break;

}

}
	
	@Override
	public void dispose () {
		//graphics objects deleted
		batch.dispose();
		img.dispose();
		font.dispose();
		mainMenu.dispose();
	}

	//later when screen resizing is supported this will be important,currently does nothing
	@Override
	public void resize(int width, int height){
		viewport.update(width,height);
	}


	@Override
	public boolean keyDown(int keycode) {
if (Gdx.input.isKeyPressed(Keys.ESCAPE)){
//bringUpOptionMenu();
}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}





	//load the main menu's buttons
	void loadMainMenuUIComponents(){
//this clickListener starts a game
		ClickListener clickListener=new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				hideMainMenu();
				//set other objects not used by the game to be null
				setAllObjectsNull();
				//game object created
				game=new LiveGame();
				game.loadGameMenu(inputMultiplexer);
				//state set to 1, which means game is currently being played
				currentGameState= GAME_IS_LIVE_STATE;
			}
		};
		mainMenu.addButton("Start Game",200,30,100,100,clickListener);

		//this clickListener enters army building mode
		clickListener=new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				hideMainMenu();
				//set other objects not used by the game to be null
				setAllObjectsNull();
				armyMaker=new ArmyMaker(inputMultiplexer);
				currentGameState=ARMY_BUILDING_STATE;
			}
		};
		mainMenu.addButton("Army Building",200,30,100,50,clickListener);
	}

	//hides the main menu, this should be called whenever the player moves to a new part of the game
	//so that they don't see the main menu for no reason
	void hideMainMenu(){
		mainMenu.disable();
		mainMenuDisabled=true;
	}

	//set all objects to null so we can enter a new part of the game without old parts being loaded
	void setAllObjectsNull(){
		if (game!=null) {
			game.deleteGraphics();
			game=null;
		}
		if (armyMaker!=null){
			armyMaker.deleteGraphics();
			armyMaker=null;
		}
	}

	void loadOptionsMenuUIComponents(){
		ClickListener clickListener=new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y){
				//set other objects not used by the game to be null
					bringUpOptionMenu();
				}

		};
		optionsMenu.addButton("settings",80,20,10,20,clickListener);
	}

	void bringUpOptionMenu(){
		unselectAll();
		currentlyInOptionsMenu=!currentlyInOptionsMenu;
		if (currentlyInOptionsMenu==true) {
			//optionsMenu.addButton();
		}
	}
	//based on the state of the game, unselect everything in the current area of the game
	//this is so that when the user presses escape to look at the menu, they don't
	//stay having a piece selected, for example.

	void unselectAll(){
		//based on the state of the game, unselect everything in the current area of the game
		switch (currentGameState){

			case MAIN_MENU_STATE:

				break;

			case GAME_IS_LIVE_STATE:
				game.unselectAll();
				break;

			case ARMY_BUILDING_STATE:
				armyMaker.unselectAll();
				break;



		}
	}

}
