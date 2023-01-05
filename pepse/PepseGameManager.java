package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.*;
import pepse.world.trees.Leaf;
import pepse.world.trees.Tree;
import java.awt.*;
import java.util.Random;
import static danogl.collisions.Layer.BACKGROUND;
import static danogl.collisions.Layer.DEFAULT;

public class PepseGameManager extends GameManager {
    private static final int GROUND_LAYER = -3;
    private static final int LEAVES_LAYER = -1;
    private static final int RANDOM_BOUND = 1000000;
    private static final int ASTRONOMICAL_OBJECT_CYCLE_LENGTH = 60;
    private static final int DAYNIGHT_CYCLE_LENGTH = 30;
    private static final int DEFAULT_TREE = 120;
    private static final Vector2 WINDOW_DIMENSIONS = new Vector2(960,600);
    private static final String WINDOW_TITLE = "Pepse";
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final float avatarInitialXLocation = WINDOW_DIMENSIONS.x() * 0.5f;
    private static Avatar avatar;
    private static int seed;
    private static int topBound = (int) WINDOW_DIMENSIONS.x();
    private static int bottomBound = 0;
    private static Terrain gameTerrain;


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
     * @param imageReader Contains a single method: readImage, which reads an imag
     * Creates a new simulatore from disk.
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
        seed = new Random().nextInt(RANDOM_BOUND);

        createNonBlocks();
        createRandomEnvironment(seed, (int) (-1 * WINDOW_DIMENSIONS.x()), (int) (2 * WINDOW_DIMENSIONS.x()));

        avatar = Avatar.create(gameObjects(), DEFAULT, new Vector2(avatarInitialXLocation,
                gameTerrain.groundHeightAt(WINDOW_DIMENSIONS.x() * 0.5f)), inputListener, imageReader);

        setCamera(new Camera(avatar, WINDOW_DIMENSIONS.mult(0.5f).subtract(avatar.getCenter()),
                WINDOW_DIMENSIONS, WINDOW_DIMENSIONS));

        gameObjects().layers().shouldLayersCollide(LEAVES_LAYER, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(DEFAULT, GROUND_LAYER, true);
        gameObjects().layers().shouldLayersCollide(DEFAULT, LEAVES_LAYER - 1, true);
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
    private void createRandomEnvironment(int seed, int minX, int maxX){
        //create terrain
        gameTerrain = new Terrain(gameObjects(), GROUND_LAYER, WINDOW_DIMENSIONS, seed);
        gameTerrain.createInRange(minX, maxX);

        //create trees
        Tree newTrees = new Tree(gameTerrain, gameObjects(), LEAVES_LAYER, seed, new float[]
                {avatarInitialXLocation - 3 * Block.SIZE, avatarInitialXLocation + 4 * Block.SIZE});
        newTrees.createInRange(minX, maxX);

        if(!newTrees.treesInGame()){
            newTrees.spawnTree(DEFAULT_TREE);
        }
    }

    /**
     * update function that is called every frame
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        var currentX = (int) avatar.getTopLeftCorner().x();

        if (currentX > topBound){
            removeRandomEnvironment(bottomBound - (int) WINDOW_DIMENSIONS.x(), bottomBound);
            topBound += WINDOW_DIMENSIONS.x();
            bottomBound += WINDOW_DIMENSIONS.x();
            createRandomEnvironment(seed, topBound, topBound + (int) WINDOW_DIMENSIONS.x());
        }

        if (currentX < bottomBound){
            removeRandomEnvironment(topBound, topBound + (int) WINDOW_DIMENSIONS.x());
            topBound -= WINDOW_DIMENSIONS.x();
            bottomBound -= WINDOW_DIMENSIONS.x();
            createRandomEnvironment(seed, bottomBound - (int) WINDOW_DIMENSIONS.x(), bottomBound);
        }
    }

    /**
     * removes all game objects created during CreateRandomEnvironment from the game
     * @param minX minimum x to start delete from
     * @param maxX maximum x to delete
     */
    private void removeRandomEnvironment(int minX, int maxX){
        for (GameObject obj: gameObjects()) {
            if(obj.getCenter().x() >= minX && obj.getCenter().x() < maxX){
                if (obj instanceof Leaf) gameObjects().removeGameObject(obj, LEAVES_LAYER);
                else {
                    gameObjects().removeGameObject(obj, GROUND_LAYER);
                    gameObjects().removeGameObject(obj, LEAVES_LAYER - 1); //for logs
                }
            }
        }
    }
}
