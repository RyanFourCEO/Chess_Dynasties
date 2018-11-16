package ceov2.org;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CEOV2 extends ApplicationAdapter implements InputProcessor {

    //declare constants
    public static final int DEFAULT_SCREEN_WIDTH = 1100;
    public static final int DEFAULT_SCREEN_HEIGHT = 618;

    public static final int MAIN_MENU_STATE = 0;
    public static final int GAME_IS_LIVE_STATE = 1;
    public static final int ARMY_BUILDING_STATE = 2;
    public static final int LEVEL_EDITOR_STATE = 3;
    //The main menu is the menu the user starts at
    public Menu mainMenu;
    //the options menu will always be available to the user
    public Menu optionsMenu;
    //ServerCommunications object, used to send and receive messages from the server
    //automatically decodes all messages to hex, encodes sent messages to hex
    ServerCommunications serverComms;
    //Declare graphics objects
    boolean fullScreenMode = false;
    //allows multiple inputprocessors to be used at once
    InputMultiplexer inputMultiplexer;
    boolean mainMenuDisabled = false;
    boolean currentlyInOptionsMenu = false;

    //this string is displayed to the user, the String will tell the user if they are connected or not
    String connectionMessage = "Not Connected To Server";


    SpriteBatch batch;
    BitmapFont font;
    MouseVars mouseVars;
    Pixmap pixmap;
    Texture texture;
    Sprite sprite1;

    //unused probably important later
    Viewport viewport;
    Camera camera;

    //declare primary game objects,
    // "game" controls what happens when a player enters a game
    LiveGame game;
    //armyMaker controls what happens when a player is making their army
    ArmyMaker armyMaker;
    //Level editor is for when a person is creating campaign/single-player levels
    LevelEditor levelEditor;

    //current state tells what "mode" the game is in
    //depending on the state of the game different menus will be loaded
    //and different logic will be executed. If a player is in a game
    //the game logic will be executed, etc
    //when is is equal to 0, that means the player is at the main menu
    //when is equal to 1, that means a game is occurring
    //when it is equal to 2, that means armies are being made
    //when it is equal to 3, that means levels are being edited (leveleditor)
    int currentGameState = MAIN_MENU_STATE;

    //game's language
    String language;
    Lang lang;

    //initialization/loading of the games resources
    @Override
    public void create() {
        serverComms = new ServerCommunications("127.0.0.1", 9021);

        //create semi-transparent black box texture
        pixmap = new Pixmap(290, 300, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.75f);
        pixmap.fillRectangle(0, 0, 290, 300);
        texture = new Texture(pixmap);
        sprite1 = new Sprite(texture);
        sprite1.setCenter(550, 309);
        pixmap.dispose();

        lang = new Lang("eng");

        camera = new PerspectiveCamera();
        viewport = new StretchViewport(1100, 618, camera);
        inputMultiplexer = new InputMultiplexer();

        //load the options menu, this is just one button that can bring up further options for the player
        //including screen resolution options and audio options
        optionsMenu = new Menu(inputMultiplexer);
        //load the mainMenu, this menu is the first menu the user sees
        mainMenu = new Menu(inputMultiplexer);

        Gdx.input.setInputProcessor(inputMultiplexer);
        inputMultiplexer.addProcessor(this);

        //load the main menu's UI components
        loadMainMenuUIComponents();
        //load the single button that the user can press to bring up their various options
        loadOptionsMenuUIComponents();

//mouseVars holds the mouse's variables, it's position and whether or not it is clicked
//every time the main loop executes this object finds the variables again
        mouseVars = new MouseVars();

//graphics objects are initialized
        batch = new SpriteBatch();

        texture = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("Fonts\\ArialDistanceField2.fnt"), new TextureRegion(texture), false);
        font.setColor(Color.WHITE);
    }

    //The main loop of the game, all graphics will be drawn here, and game logic is executed here
    @Override
    public void render() {

        //collect the mouse variable for this frame
        mouseVars.setMouseVariables(DEFAULT_SCREEN_HEIGHT, DEFAULT_SCREEN_WIDTH, viewport.getScreenHeight(), viewport.getScreenWidth());
//clear the screen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (currentlyInOptionsMenu == true) {
            //if the user is in the options menu, we unset the mouse variables so the user can't accidentalluy
            //make a move while doing options menu things
            mouseVars.unSetMouseVariables();
        }

        //process Server input
        processServerInput();

//based on the state of the game, execute certain code
        switch (currentGameState) {
//if in the main menu, ensure the main menu is enabled and draw the main menu components
            case MAIN_MENU_STATE:
                if (mainMenuDisabled == true) {
                    mainMenuDisabled = false;
                    mainMenu.enable();
                }
                mainMenu.stage.getViewport().apply();
                mainMenu.stage.draw();
                break;

            case GAME_IS_LIVE_STATE:

                game.performGameLogic(batch, mouseVars);
                if (game.gameOver == true) {
                    setAllObjectsNull();
                    currentGameState = MAIN_MENU_STATE;
                }
                break;

            case ARMY_BUILDING_STATE:
                armyMaker.run(batch, mouseVars);
                if (armyMaker.exitToMainMenu == true) {
                    setAllObjectsNull();
                    currentGameState = MAIN_MENU_STATE;
                }
                break;

            case LEVEL_EDITOR_STATE:
                levelEditor.run(batch, mouseVars);
                if (levelEditor.exitToMainMenu == true) {
                    setAllObjectsNull();
                    currentGameState = MAIN_MENU_STATE;
                }
                break;

        }

        if (currentlyInOptionsMenu == true) {
            //draw the black rectangle the optionsMenu is contained within
            batch.begin();
            sprite1.draw(batch);
            batch.end();
            //prepare font shader
            Shaders.prepareDistanceFieldShader();
            batch.setShader(Shaders.distanceFieldShader);
            String volume;

            //draw the menu text
            batch.begin();

            font.draw(batch, lang.getTranslation("Graphics Quality"), 440, 270);

            volume = String.valueOf((int) optionsMenu.allContainers.get(0).getActor().getValue()) + "%";
            font.draw(batch, lang.getTranslation("Effects Volume") + " " +  volume, 440, 370);

            volume = String.valueOf((int) optionsMenu.allContainers.get(1).getActor().getValue()) + "%";
            font.draw(batch, lang.getTranslation("Music Volume ") + volume, 440, 320);

            font.draw(batch, lang.getTranslation("Fullscreen Mode") + ":", 440, 420);

            font.draw(batch, lang.getTranslation("Language"), 440, 180);

            batch.end();

            batch.setShader(Shaders.defaultShader);
        }
        Shaders.prepareDistanceFieldShader();
        batch.setShader(Shaders.distanceFieldShader);
        batch.begin();
        font.setColor(Color.BLACK);
        font.draw(batch, connectionMessage, 900, 600);
        font.setColor(Color.WHITE);
        batch.end();
        batch.setShader(Shaders.defaultShader);
        //draw the optionsMenu UI components
        optionsMenu.stage.getViewport().apply();
        optionsMenu.stage.draw();

    }

    //delete graphics objects
    @Override
    public void dispose() {
        //graphics objects deleted
        batch.dispose();
        font.dispose();
        mainMenu.dispose();
        optionsMenu.dispose();
        texture.dispose();
        serverComms.closeStreams();
    }

    void reloadGraphics() {
        lang = new Lang(language);
        System.out.println(lang.getTranslation("Language"));
        System.out.println(lang.translation);
        switch (currentGameState) {
//if in the main menu, ensure the main menu is enabled and draw the main menu components
            case GAME_IS_LIVE_STATE:
                game.reloadGraphics();
                break;

            case ARMY_BUILDING_STATE:
                armyMaker.reloadGraphics();
                break;

            case LEVEL_EDITOR_STATE:
                levelEditor.reloadGraphics();
                break;
        }
    }

    //later when screen resizing is supported this will be important,currently does nothing
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        optionsMenu.stage.getViewport().update(width, height);
        mainMenu.stage.getViewport().update(width, height);
        if (game != null) {
            game.menu.stage.getViewport().update(width, height);
        }
        if (levelEditor != null) {
            levelEditor.menu.stage.getViewport().update(width, height);
        }
        if (armyMaker != null) {
            armyMaker.menu.stage.getViewport().update(width, height);
        }

    }


    @Override
    public boolean keyDown(int keycode) {
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
            toggleOptionsMenu();
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

    void processServerInput() {
        //check the state of the server
        if (serverComms.getState() == false) {
            connectionMessage = "Not Connected to Server";
        } else {
            connectionMessage = "Connected to Server";

        }
        //attempt to read any new messages the server may have sent
        serverComms.readMessages();
        //if their is a message in the queue to process
        if (serverComms.getFirstClientMessageInQueue() != null) {
            //process it, then remove it from the queue
            dealWithServerMessage(serverComms.getFirstClientMessageInQueue());
            serverComms.removeFirstClientMessageInQueue();
        }
    }

    //process a single server message
    void dealWithServerMessage(String message) {
        //split the message into it's two parts, the command, and the arguments
        String[] commandThenArgs = message.split(" ");
        String command = commandThenArgs[0];
        String args = commandThenArgs[1];
        System.out.println(command + " is executed");

        //deal with the heartBeat command
        if (command.equals("HEARTBEAT")) {
            //reply with HEARTBEAT command of our own
            serverComms.sendMessageToServer("HEARTBEAT" + "\n");
        } else {
            //deal with RANKED_MATCH_FOUND command
            if (command.equals("RANKED_MATCH_FOUND")) {
                dealWithRankedMatchFoundCommand(args);
            } else {
                if (command.equals("MOVE")) {
                    String move = StringUtils.convertFromHex(args);
                    System.out.println(move);
                    game.state.executeMoveFromServer(move);
                }
            }
        }
    }

    void dealWithRankedMatchFoundCommand(String args) {
        //count number of commas in the string, this is the number of arguments sent
        int commaNumber = StringUtils.countOccurrences(args, ',');
        //split the String by commas
        String[] splitArgs = args.split(",");
        //convert all strings from hex
        for (int x = 0; x != commaNumber + 1; x++) {
            splitArgs[x] = StringUtils.convertFromHex(splitArgs[x]);
        }
        //the first arg in the RANKEDMATCHFOUND command is the colour
        int colour = Integer.valueOf(splitArgs[0]);
        //the other args are pieces
        String army = "";
        //loop through all other args and put them in a String
        for (int x = 1; x != commaNumber + 1; x++) {
            if (x != commaNumber) {
                army += splitArgs[x] + ",";
            } else {
                army += splitArgs[x];
            }
        }
        System.out.println(army + " hmm");
        //load the army the user is using
        String army2 = Gdx.files.internal("UserFiles\\armies\\army1.txt").readString();
        hideMainMenu();
        //set other objects not used by the game to be null
        setAllObjectsNull();
        //game object created using the army sent by the server, the colour sent by the server
        //and the army the user is using. ServerComms also sent to LiveGame object so it can
        //send messages to the server
        game = new LiveGame(inputMultiplexer, colour, army2, army, serverComms);
        //state set to 1, which means game is currently being played
        currentGameState = GAME_IS_LIVE_STATE;
    }

    //load the main menu's buttons
    void loadMainMenuUIComponents() {
//this clickListener starts a game
        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideMainMenu();
                //set other objects not used by the game to be null
                setAllObjectsNull();
                //game object created
                game = new LiveGame(inputMultiplexer);
                //state set to 1, which means game is currently being played
                currentGameState = GAME_IS_LIVE_STATE;
            }
        };
        mainMenu.addButton("Practice Game", 200, 30, 100, 100, clickListener);

        //this clickListener has the player enter the multiplayer queue
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String army = Gdx.files.internal("UserFiles\\armies\\army1.txt").readString();
                String message = "ENTER_RANKED_QUEUE ";
                String[] pieces = army.split(",");
                for (int w = 0; w != 16; w++) {
                    pieces[w] = StringUtils.convertToHex(pieces[w]);
                }
                String hexArmy = "";
                for (int w = 0; w != 16; w++) {
                    if (w != 15) {
                        hexArmy += pieces[w] + ",";
                    } else {
                        hexArmy += pieces[w];
                    }
                }
                message += hexArmy;
                serverComms.sendMessageToServer(message + "\n");
            }
        };

        mainMenu.addButton("Multiplayer Game", 200, 30, 100, 200, clickListener);

        //this clickListener enters army building mode
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideMainMenu();
                //set other objects not used by the game to be null
                setAllObjectsNull();
                armyMaker = new ArmyMaker(inputMultiplexer);
                currentGameState = ARMY_BUILDING_STATE;
            }
        };
        mainMenu.addButton("Army Building", 200, 30, 100, 50, clickListener);

        //this clickListener enters level editing mode
        clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hideMainMenu();
                //set other objects not used by the game to be null
                setAllObjectsNull();
                levelEditor = new LevelEditor(inputMultiplexer);
                currentGameState = LEVEL_EDITOR_STATE;
            }
        };
        mainMenu.addButton("Level Editor", 200, 30, 100, 150, clickListener);
    }

    //hides the main menu, this should be called whenever the player moves to a new part of the game
    //so that they don't see the main menu for no reason
    void hideMainMenu() {
        mainMenu.disable();
        mainMenuDisabled = true;
    }

    //set all objects to null so we can enter a new part of the game without old parts being loaded
    void setAllObjectsNull() {
        if (game != null) {
            game.deleteGraphics();
            game = null;
        }
        if (armyMaker != null) {
            armyMaker.deleteGraphics();
            armyMaker = null;
        }
        if (levelEditor != null) {
            levelEditor.deleteGraphics();
            levelEditor = null;
        }
    }

    void loadOptionsMenuUIComponents() {
        ClickListener clickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //set other objects not used by the game to be null
                toggleOptionsMenu();
            }

        };
        optionsMenu.addButton("settings", 80, 20, 10, 20, clickListener);
    }

    void toggleOptionsMenu() {
        unselectAll();
        currentlyInOptionsMenu = !currentlyInOptionsMenu;

        if (currentlyInOptionsMenu) {
            //create a buttonGroup to put all graphics quality options into
            optionsMenu.addGroup();
            //always have exactly one button checked
            optionsMenu.allGroups.get(0).setMaxCheckCount(1);
            optionsMenu.allGroups.get(0).setMinCheckCount(1);
            optionsMenu.allGroups.get(0).setUncheckLast(true);

            //toggle fullscreen mode button
            ClickListener clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    fullScreenMode = !fullScreenMode;
                    if (fullScreenMode == true) {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    } else {
                        Gdx.graphics.setWindowedMode(1100, 618);
                    }

                }
            };
            optionsMenu.addButton("toggle", 70, 25, 590, 402, clickListener);

            //use bad graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GraphicsUtils.graphicsQuality = 0;
                    reloadGraphics();
                }
            };

            optionsMenu.addButtonToGroup(0, "bad", 50, 50, 435, 200, clickListener);

            //use decent graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GraphicsUtils.graphicsQuality = 1;
                    reloadGraphics();
                }
            };
            optionsMenu.addButtonToGroup(0, "okay", 50, 50, 495, 200, clickListener);


            //use good graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GraphicsUtils.graphicsQuality = 2;
                    reloadGraphics();
                }
            };
            optionsMenu.addButtonToGroup(0, "good", 50, 50, 555, 200, clickListener);


            //use best graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    GraphicsUtils.graphicsQuality = 3;
                    reloadGraphics();
                }
            };
            optionsMenu.addButtonToGroup(0, "best", 50, 50, 615, 200, clickListener);
            Array<TextButton2> buttons = new Array();
            buttons = optionsMenu.allGroups.get(0).getButtons();
            //check the button which is currently selected
            switch (GraphicsUtils.graphicsQuality) {

                case 0:
                    setButtonSelected(buttons, "bad");
                    break;

                case 1:
                    setButtonSelected(buttons, "okay");
                    break;

                case 2:
                    setButtonSelected(buttons, "good");
                    break;

                case 3:
                    setButtonSelected(buttons, "best");
                    break;
            }

            //create a buttonGroup to put all language options into
            optionsMenu.addGroup();
            //always have exactly one button checked
            optionsMenu.allGroups.get(1).setMaxCheckCount(1);
            optionsMenu.allGroups.get(1).setMinCheckCount(1);
            optionsMenu.allGroups.get(1).setUncheckLast(true);

            //use bad graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    language = "eng";
                    reloadGraphics();
                }
            };

            optionsMenu.addButtonToGroup(1, "ENG", 50, 50, 535, 140, clickListener);
            //use bad graphics settings button
            clickListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    language = "ssp";
                    reloadGraphics();
                }
            };

            optionsMenu.addButtonToGroup(1, "SSP", 50, 50, 595, 140, clickListener);

            Array<TextButton2> buttons2 = new Array();
            buttons2 = optionsMenu.allGroups.get(1).getButtons();
            //check the button which is currently selected
            switch (GraphicsUtils.graphicsQuality) {
                case 0:
                    setButtonSelected(buttons2, "ENG");
                    break;
                case 1:
                    setButtonSelected(buttons2, "SSP");
                    break;
            }

            //add volume sliders
            optionsMenu.addSlider(500, 340, 0, 100, 1);
            optionsMenu.addSlider(500, 290, 0, 100, 1);
        }


//if the options menu is closed, remove most of the UI components
        if (!currentlyInOptionsMenu) {
            //loop through and remove all buttons on the options menu, but the very first one (which is the
            //button that allows you to bring up the other buttons)
            for (int x = optionsMenu.allButtons.size() - 1; x != 0; x--) {
                optionsMenu.allButtons.get(x).remove();
                optionsMenu.allButtons.remove(x);
            }
            for (int x = optionsMenu.allGroups.size() - 1; x != -1; x--) {
                optionsMenu.allGroups.clear();
            }

            for (int x = optionsMenu.allContainers.size() - 1; x != -1; x--) {
                optionsMenu.allContainers.get(x).getActor().remove();
                optionsMenu.allContainers.get(x).remove();
                optionsMenu.allContainers.remove(x);
            }
        }
    }

    //set a textbutton that has text "buttontext" in an array of buttons to be selected
    void setButtonSelected(Array<TextButton2> buttons, String buttonText) {
        for (int x = 0; x != buttons.size; x++) {
            String string1 = String.valueOf(buttons.get(x).getText());
            if (string1.equals(buttonText)) {
                buttons.get(x).setChecked(true);
            }
        }
    }

    //based on the state of the game, unselect everything in the current area of the game
    //this is so that when the user presses escape to look at the menu, they don't
    //stay having a piece selected, for example.
    void unselectAll() {
        //based on the state of the game, unselect everything in the current area of the game
        switch (currentGameState) {

            case MAIN_MENU_STATE:

                break;

            case GAME_IS_LIVE_STATE:
                game.unselectAll();
                break;

            case ARMY_BUILDING_STATE:
                armyMaker.unselectAll();
                break;

            case LEVEL_EDITOR_STATE:
                levelEditor.unselectAll();
                break;

        }
    }

}
