package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.*;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;
import static danogl.collisions.Layer.BACKGROUND;
import static danogl.collisions.Layer.DEFAULT;

public class PepseGameManager extends GameManager {
    private static final Vector2 WINDOW_DIMENSIONS = new Vector2(1440,900);
    private static final String WINDOW_TITLE = "Pepse";
    private static final int GROUND_LAYER = -3;
    private static final int LEAVES_LAYER = -1;
    private static final int RANDOM_BOUND = 1000000;
    private static final int ASTRONOMICAL_OBJECT_CYCLE_LENGTH = 60;
    private static final int DAYNIGHT_CYCLE_LENGTH = 30;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final int X0 = 0;
    private Terrain gameTerrain;

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
        int seed = new Random().nextInt(RANDOM_BOUND); //todo enter this in XX


        System.out.println(seed); //todo remember to remove

        createNonBlocks();
        createRandomEnvironment(seed); //todo the seed here is XX

        Avatar.create(gameObjects(), DEFAULT, new Vector2(WINDOW_DIMENSIONS.x() * 0.5f, gameTerrain.groundHeightAt(WINDOW_DIMENSIONS.x() * 0.5f) -30f), inputListener, imageReader);

        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(DEFAULT, GROUND_LAYER, true);
    }

    /**
     * Creates all non-block objects: sky, night, sun, sun halo and moon
     */
    private void createNonBlocks(){
        Sky.create(gameObjects(), WINDOW_DIMENSIONS, BACKGROUND);
        Night.create(gameObjects(), WINDOW_DIMENSIONS, Layer.FOREGROUND, DAYNIGHT_CYCLE_LENGTH);

        GameObject sun = Sun.create(gameObjects(),BACKGROUND + 1, WINDOW_DIMENSIONS,
                ASTRONOMICAL_OBJECT_CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(gameObjects(),BACKGROUND + 2, sun, SUN_HALO_COLOR);
        sunHalo.addComponent(x -> sunHalo.setCenter(sun.getCenter()));

        GameObject moon = Moon.create(gameObjects(),BACKGROUND + 1, WINDOW_DIMENSIONS,
                ASTRONOMICAL_OBJECT_CYCLE_LENGTH);
    }

    /**
     * Randomly creates the intractable environment: terrain and trees
     * @param seed a generated seed to be based on in the generation
     */
    private void createRandomEnvironment(int seed){
        //create terrain
        gameTerrain = new Terrain(gameObjects(), GROUND_LAYER, WINDOW_DIMENSIONS, seed);
        gameTerrain.createInRange(X0, (int) WINDOW_DIMENSIONS.x());

        //create trees
        Tree newTrees = new Tree(gameTerrain, gameObjects(), LEAVES_LAYER, seed);
        newTrees.createInRange(X0, (int) WINDOW_DIMENSIONS.x());
    }
}
