package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;

import static danogl.collisions.Layer.BACKGROUND;

public class PepseGameManager extends GameManager {
    private static final Vector2 WINDOW_DIMENSIONS = new Vector2(1440,900);
    private static final String WINDOW_TITLE = "Pepse";
    private static final int groundLayer = 1;
    private static final int leafLayer = 3;
    private static final int RANDOM_BOUND = 1000000;

    /**
     * Constructor
     * @param windowTitle the name of the window
     * @param windowDimensions the sizes of the window, in pixels
     */
    public PepseGameManager(String windowTitle, Vector2 windowDimensions){
        super(windowTitle, windowDimensions);

    }

    public static void main(String[] args){
        new PepseGameManager(WINDOW_TITLE, WINDOW_DIMENSIONS).run();
    }

    /**
     * Creates a new simulator
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener
            inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);


        Sky.create(gameObjects(), windowController.getWindowDimensions(),BACKGROUND);
        int seed = new Random().nextInt(RANDOM_BOUND);
        System.out.println(seed);


        Terrain newTerrain = new Terrain(gameObjects(),groundLayer,windowController.getWindowDimensions(),seed);
        newTerrain.createInRange(0,(int) windowController.getWindowDimensions().x());
        Night.create(gameObjects(),windowController.getWindowDimensions(), Layer.FOREGROUND,30);
        GameObject sun = Sun.create(gameObjects(),BACKGROUND+2, windowController.getWindowDimensions(), 60);
        GameObject sunHalo = SunHalo.create(gameObjects(),BACKGROUND+1, sun, new Color(255, 255, 0, 20));
        sunHalo.addComponent(x -> sunHalo.setCenter(sun.getCenter()));
        Tree newTrees = new Tree(newTerrain, gameObjects(), leafLayer, windowController.getWindowDimensions(),seed);
        newTrees.createInRange(0,(int) windowController.getWindowDimensions().x());

        gameObjects().layers().shouldLayersCollide(leafLayer, groundLayer, true);

    }
}
