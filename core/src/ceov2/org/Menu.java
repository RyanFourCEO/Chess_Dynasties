package ceov2.org;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

//the mainMenu class, creates a stage and makes buttons for whatever state the game is in
public class Menu {
    //what gets drawn to the screen, it holds all UI elements, just buttons for now
    Stage stage;

    ArrayList<TextButton2> allButtons = new ArrayList<TextButton2>();

    ArrayList<TextArea> allTextAreas = new ArrayList<TextArea>();

    ArrayList<ButtonGroup> allGroups = new ArrayList<ButtonGroup>();

    ArrayList<Container<Slider>> allContainers = new ArrayList<Container<Slider>>();

    Skin skin;

    public Menu(InputMultiplexer inputMultiplexer) {
        //initialise the stage
        stage = new Stage(new StretchViewport(1100, 618));
        //the stage uses the default button skins for now
        skin = new Skin(Gdx.files.internal("uiskin.json"));
        //add the stage to the input multiplexer, this allows the stage to receive mouse input
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);


    }

    public void addTextArea(int width, int height, int positionx, int positiony) {
        allTextAreas.add(new TextArea("", skin, "default"));
        allTextAreas.get(allTextAreas.size() - 1).setPosition(positionx, positiony);
        allTextAreas.get(allTextAreas.size() - 1).setSize(width, height);
        stage.addActor(allTextAreas.get(allTextAreas.size() - 1));
    }

    public void addTextArea(String text, int width, int height, int positionx, int positiony) {
        allTextAreas.add(new TextArea(text, skin, "default"));
        allTextAreas.get(allTextAreas.size() - 1).setPosition(positionx, positiony);
        allTextAreas.get(allTextAreas.size() - 1).setSize(width, height);
        stage.addActor(allTextAreas.get(allTextAreas.size() - 1));
    }

    //add a button to the menu with the following variables deciding all it's factors
//buttonText is the text the button displays
//height and width are the size of the button
//positionx and y are the positions of the button on the stage, and on the screen
//Clicklistener is the code that actually gets executed when the button is pressed
    public void addButton(String buttonText, int width, int height, int positionx, int positiony, ClickListener clickListener) {
        //create a textButtonStyle
        TextButton2.TextButtonStyle newStyle = new TextButton2.TextButtonStyle();
        //load the distance field fond to a texture
        Texture texture = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        //create a font with the loaded texture
        newStyle.font = new BitmapFont(Gdx.files.internal("Fonts\\ArialDistanceField2.fnt"), new TextureRegion(texture), false);
        //set font colours and the drawables for the button
        newStyle.fontColor = (Color.BLACK);
        newStyle.downFontColor = (Color.WHITE);
        newStyle.up = skin.getDrawable("default-round");
        newStyle.down = skin.getDrawable("default-round-down");
        //button created and using the style created above
        TextButton2 buttonToAdd = new TextButton2(buttonText, newStyle);
        //set buttons size and position on screen
        buttonToAdd.setWidth(width);
        buttonToAdd.setHeight(height);
        buttonToAdd.setPosition(positionx, positiony);
        buttonToAdd.addListener(clickListener);
        //add the button to the stage so it can be drawn to the screen
        stage.addActor(buttonToAdd);
        //add the button to the array of buttons
        allButtons.add(buttonToAdd);

    }

    //add a ButtonGroup to the menu, a ButtonGroup contains an array of buttons
    public void addGroup() {
        allGroups.add(new ButtonGroup());
    }

    //add a button to the ButtonGroup with index "groupIndex"
//has the same functionality as the addButton method, but sets a "checked font colour"
//and adds the button to the buttonGroup
    public void addButtonToGroup(int groupIndex, String buttonText, int width, int height, int positionx, int positiony, ClickListener clickListener) {
        //create a textButtonStyle
        TextButton2.TextButtonStyle newStyle = new TextButton2.TextButtonStyle();
        //load the distance field fond to a texture
        Texture texture = new Texture(Gdx.files.internal("Fonts\\ArialDistanceField2.png"), true);
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        //create a font with the loaded texture
        newStyle.font = new BitmapFont(Gdx.files.internal("Fonts\\ArialDistanceField2.fnt"), new TextureRegion(texture), false);
        //set font colours and the drawables for the button
        newStyle.fontColor = (Color.BLACK);
        newStyle.downFontColor = (Color.WHITE);
        newStyle.checkedFontColor = Color.BLUE;
        newStyle.up = skin.getDrawable("default-round");
        newStyle.down = skin.getDrawable("default-round-down");
        //button created and using the style created above
        TextButton2 buttonToAdd = new TextButton2(buttonText, newStyle);
        //set buttons size and position on screen
        buttonToAdd.setWidth(width);
        buttonToAdd.setHeight(height);
        buttonToAdd.setPosition(positionx, positiony);
        buttonToAdd.addListener(clickListener);
        //add the button to the stage so it can be drawn to the screen
        stage.addActor(buttonToAdd);
        //add the button to the array of buttons
        allButtons.add(buttonToAdd);
        //add the button to the ButtonGroup
        allGroups.get(groupIndex).add(buttonToAdd);

    }

    //add a slider to the menu, sliders are placed into containers, and the containers set the position of the slider
    public void addSlider(int x, int y, int minValue, int maxValue, int stepSize) {
        //make a container
        Container<Slider> container = new Container<Slider>();
        //make a slider and add it to the container
        container.setActor(new Slider(minValue, maxValue, stepSize, false, skin));
        //add the container to the array of containers
        allContainers.add(container);
//set the position of the container and add it to the stage
        container.setPosition(x, y);
        stage.addActor(container);
    }

    //make all UI components invisible and incapable of processing input
    void disable() {
        for (int x = 0; x != allButtons.size(); x++) {
            allButtons.get(x).setVisible(false);
        }
        for (int x = 0; x != allTextAreas.size(); x++) {
            allTextAreas.get(x).setVisible(false);
        }

        for (int x = 0; x != allContainers.size(); x++) {
            allContainers.get(x).getActor().setVisible(false);
            allContainers.get(x).setVisible(false);
        }
    }

    //make all UI components visible and capable of processing input
    void enable() {
        for (int x = 0; x != allButtons.size(); x++) {
            allButtons.get(x).setVisible(true);
        }
        for (int x = 0; x != allTextAreas.size(); x++) {
            allTextAreas.get(x).setVisible(true);
        }
        for (int x = 0; x != allContainers.size(); x++) {
            allContainers.get(x).getActor().setVisible(true);
            allContainers.get(x).setVisible(true);
        }
    }

    void dispose() {
        skin.dispose();
        stage.dispose();
    }
}
