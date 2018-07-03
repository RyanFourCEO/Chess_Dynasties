package ceov2.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;

public class CEOV2 extends ApplicationAdapter {

	//declare constants(unused, will be removed later in all likelihood)
	public static final int WINDOW_SIZE_X=1100;
	public static final int WINDOW_SIZE_Y=618;
	public static final int SQUARE_SIZE=77;
	public static final int LINE_WIDTH=4;
	//Declare graphics objects
	public Menu menu;

	SpriteBatch batch;
	Texture img;
	BitmapFont font;
    Sprite sprite1;
    MouseVars mouseVars;

    //unused probably important later
	Viewport viewport;
	Camera camera;

    //declare primary game objects, "
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
    int currentGameState =0;
	//initialization/loading of the games resources
	@Override
	public void create () {
		//load the menu, this same menu is used for all areas of the game currently
menu=new Menu();
        //load the menu based on the current game state (which is currently at 0)
loadCurrentMenu();
//mouseVars holds the mouse's variables, it's position and whether or not it is clicked
//every time the main loop executes this class finds the variables again
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
//System.out.println(mouseVars.mousePosx+" x y "+mouseVars.mousePosy);
//clear the screen
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		batch.begin();

		batch.draw(sprite1,sprite1.getX(),sprite1.getY(),sprite1.getWidth(),sprite1.getHeight());

		batch.end();
updateTextAreas();
		//draw the menu objects
batch.begin();
menu.stage.draw();
batch.end();

//based on the state of the game, execute certain code
switch (currentGameState){

	case 0:
//nothing necessary for the main menu, so far
		break;

	case 1:
		game.performGameLogic(batch,mouseVars);
		break;

	case 2:
		armyMaker.runArmyMaker(batch,mouseVars);
		break;



}





	}
	
	@Override
	public void dispose () {
		//graphics objects deleted
		batch.dispose();
		img.dispose();
		font.dispose();
		menu.dispose();
	}

	//later when screen resizing is supported this will be important
	@Override
	public void resize(int width, int height){
		//viewport.setScreenSize(width,height);
	}


//the menu class, creates a stage and makes buttons for whatever state the game is in
	public class Menu {
		//what gets drawn to the screen, it holds all UI elements, just buttons for now
		Stage stage;
		//array of button methods
		ArrayList<ClickListener> allClickListeners=new ArrayList<ClickListener>();
		//array of buttons
		ArrayList<TextButton> allButtons=new ArrayList<TextButton>();
		//array of textfields
	    ArrayList<TextArea> allTextAreas =new ArrayList<TextArea>();
	    int[] textAreaIndexes=new int[10];
		Skin skin;

		public Menu(){
			//set up all the code the buttons execute when they are clicked
			loadClickListeners();
			//initialise the stage
			stage=new Stage(new ScreenViewport());
			//the stage uses the default button skins for now
			skin = new Skin(Gdx.files.internal("uiskin.json"));
			//allow the stage to collect mouse input
			Gdx.input.setInputProcessor(stage);
		}
//load all button code into an array to be given to whatever buttons we wish
void loadClickListeners(){
			//index 0
			 //start game button code
			allClickListeners.add(new ClickListener(){
				@Override
				public void clicked(InputEvent event, float x, float y){
					//set other objects not used by the game to be null
					setAllObjectsNull();
					//game object created
					game=new LiveGame();
					//state set to 1, which means game is currently being played
					currentGameState =1;
					//load the menu objects for the current stat
					loadCurrentMenu();
				}
			});
			//index 1
			 //enter armybuilding mode button code
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
				 	armyMaker=new ArmyMaker();
				 	currentGameState=2;
				 	loadCurrentMenu(
					);
				 }
			 });
			 //index 2
			 //reset the board button
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
				 	game.state.deleteGraphics();
				 	game=null;
				 	game=new LiveGame();
				 }
			 });
			 //index 3
             //return to main menu button
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
				 	//delete objects no longer in use
				 	setAllObjectsNull();
					 currentGameState=0;
					 loadCurrentMenu();
				 }
			 });
			 //index 4
        //increase page button for armyMaker
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
				 	if (armyMaker!=null) {
				 		if (armyMaker.page<10) {
							armyMaker.page++;
						}
					}
				 }
			 });
              //index 5
			 //decrease page button for armyMaker
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
					 if (armyMaker!=null) {
					 	if (armyMaker.page>1) {
							armyMaker.page--;
						}
					 }
				 }
			 });

			 //index 6
			 //save army button for armyMaker
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
					 if (armyMaker!=null) {
						 armyMaker.saveArmy();
					 }
				 }
			 });

			 //index 7
			 //start editing army 2
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
					 if (armyMaker!=null) {
						 armyMaker.armyBeingEdited=2;
						 armyMaker.loadCurrentArmy();
					 }
				 }
			 });

			 //index 8
			 //start editing army 1
			 allClickListeners.add(new ClickListener(){
				 @Override
				 public void clicked(InputEvent event, float x, float y){
					 if (armyMaker!=null) {
						 armyMaker.armyBeingEdited=1;
						 armyMaker.loadCurrentArmy();
					 }
				 }
			 });

			 //index 9
			 //flip board button
	allClickListeners.add(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			if (game!=null) {
				game.state.flipBoard=!game.state.flipBoard;
			}
		}
	});

	         //index 10
	         //execute move based on notation
	//flip board button
	allClickListeners.add(new ClickListener(){
		@Override
		public void clicked(InputEvent event, float x, float y){
			if (game!=null) {
				game.state.testThenExecuteMove(allTextAreas.get(2).getText());
				allTextAreas.get(2).setText("");
			}
		}
	});
		 }


public void addTextArea(int width,int height, int positionx,int positiony,int index){
allTextAreas.add(new TextArea("",skin,"default"));
allTextAreas.get(allTextAreas.size()-1).setPosition(positionx,positiony);
allTextAreas.get(allTextAreas.size()-1).setSize(width,height);
stage.addActor(allTextAreas.get(allTextAreas.size()-1));
textAreaIndexes[allTextAreas.size()-1]=index;
}

public void addTextArea(String text, int width,int height, int positionx,int positiony,int index){
	allTextAreas.add(new TextArea(text,skin,"default"));
	allTextAreas.get(allTextAreas.size()-1).setPosition(positionx,positiony);
	allTextAreas.get(allTextAreas.size()-1).setSize(width,height);
	stage.addActor(allTextAreas.get(allTextAreas.size()-1));
	textAreaIndexes[allTextAreas.size()-1]=index;
}
//add a button to the menu with the following variables deciding all it's factors
//buttonText is the text the button displays
//height and width are the size of the button
//positionx and y are the positions of the button on the stage, and thus on the screen
//buttonIndex is the index in the array of clickListeners, this variable assigns the button
//to execute the code contained in the clickListener
public void addButton(String buttonText,int width, int height, int positionx, int positiony, int buttonIndex){


			//"reset board" button initialized with the selected skin
			allButtons.add(new TextButton(buttonText, skin, "default"));
			//set buttons size and position on screen
			allButtons.get(allButtons.size()-1).setWidth(width);
			allButtons.get(allButtons.size()-1).setHeight(height);
			allButtons.get(allButtons.size()-1).setPosition(positionx, positiony);
			allButtons.get(allButtons.size()-1).addListener(allClickListeners.get(buttonIndex));
			//add the button to the stage so it can be drawn to the screen
			stage.addActor(allButtons.get(allButtons.size()-1));

		}

		void dispose(){
			skin.dispose();
			stage.dispose();
		}
	}



	//load the menu as specified by the currentGameState
	void loadCurrentMenu(){
		//clear the array of buttons
		menu.allButtons.clear();
		//clear the array of textAreas
		menu.allTextAreas.clear();
		//remove all UI elements from the stage
		menu.stage.clear();
		//load the menu based on the current game state
		//0=main menu, 1=game menu, 2=army building menu
		if (currentGameState==0){
			loadMainMenu();
		}

		if (currentGameState==1){
			loadGameMenu();
		}

		if (currentGameState==2){
			loadArmyBuildingMenu();
		}


	}

	void updateTextAreas(){
		for(int x=0;x!=menu.allTextAreas.size();x++){
//do a switch statement for the index of the text area, the index determines which information should go in
//the text area
			switch(menu.textAreaIndexes[x]){


				//text area that needs no update, the user can enter stuff, and stuff will happen based
				//entirely on user input
				case 0:

					break;

                //name of piece/ability description text area
				case 1:
					//depending on the currentGameState, the textArea may get it's information from different objects
					//in this case, the text field gets it's information from either the LiveGame object, or the armyMaker
					//object
					switch (currentGameState) {
						case 1:
							menu.allTextAreas.get(x).setText(game.state.allPiecesOnBoard.get(game.state.pieceLastSelected).name+"\n"+game.state.allPiecesOnBoard.get(game.state.pieceLastSelected).abilityDescription);
							break;

						case 2:
							menu.allTextAreas.get(x).setText(armyMaker.allPieces.get(armyMaker.lastPieceUserSelected).name+"\n"+armyMaker.allPieces.get(armyMaker.lastPieceUserSelected).abilityDescription);

							break;
					}
					break;
                //lore of a piece text area
				case 2:
					switch (currentGameState) {
						case 1:
							menu.allTextAreas.get(x).setText(game.state.allPiecesOnBoard.get(game.state.pieceLastSelected).loreWriting);
							break;

						case 2:
							menu.allTextAreas.get(x).setText(armyMaker.allPieces.get(armyMaker.lastPieceUserSelected).loreWriting);
							break;

					}
					break;

			}
		}
	}
	//load the main menu's buttons
	void loadMainMenu(){
		menu.addButton("Start Game",200,30,100,100,0);
		menu.addButton("Army Building",200,30,100,50,1);
	}
	//load the game menu's buttons
	void loadGameMenu(){
		menu.addButton("Reset Board",200,30,100,60,2);
		menu.addButton("Return to Main Menu",200,30,100,100,3);
		menu.addButton("Flip Board",200,30,100,20,9);
		menu.addButton("execute move",150,20,950,100,10);
		menu.addTextArea(300,100,0,250,1);
		menu.addTextArea(300,100,0,140,2);
		menu.addTextArea("Copy paste move notation here, and press button to execute move",150,100,950,180,0);

	}
	//load the army building menu's buttons
	void loadArmyBuildingMenu(){
		menu.addButton("Return to Main Menu",200,30,100,100,3);
		menu.addButton("increase page",150,30,500,500,4);
		menu.addButton("decrease page",150,30,700,500,5);
		menu.addButton("Save Army",150,30,900,130,6);
		menu.addButton("army 1",150,30,400,15,8);
		menu.addButton("army 2",150,30,570,15,7);
		menu.addTextArea(300,100,0,250,1);
		menu.addTextArea(300,100,0,140,2);
	}
	//set all objects to null so we can enter a new part of the game without old parts being loaded
	void setAllObjectsNull(){
		if (game!=null) {
			game.state.deleteGraphics();
			game=null;
		}
		if (armyMaker!=null){
			armyMaker.deleteGraphics();
			armyMaker=null;
		}
	}





}
