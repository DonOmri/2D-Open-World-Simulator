package pepse.world;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;
import java.awt.*;
import java.util.function.Function;

public class Terrain {
    private static final float TERRAIN_ADJUSTER = 2f/3;
    private static final int MIN_GROUND_HEIGHT = 90;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private final PerlinNoise perlinNoise;
    private final Function<Float, Double> heightFunc;
    private float groundHeightAtX0; //todo why do we need that

    /**
     * Constructor
     * @param gameObjects all current gameObjects
     * @param groundLayer the layer in which the ground terrain should be created
     * @param windowDimensions dimensions of the window
     * @param seed a random generated seed to randomly create the terrain
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed){
        //todo: in most seeds, this function creates bad surface
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = this.windowDimensions.y() * TERRAIN_ADJUSTER;

        this.perlinNoise = new PerlinNoise(seed);
        this.heightFunc = x -> 700 - 2000 * perlinNoise.noise(x/90);
        System.out.println("seed in terrain: " + seed);
    }

    /**
     * Determines the height of a location, by using perlin noise function
     * @param x the location (horizontally) in which to determine the height of the terrain
     * @return the terrain height, as a float (return type required by given API)
     */
    public float groundHeightAt(float x){
        double initialGroundHeight = heightFunc.apply(x); //use perlin noise to determine basic height
        initialGroundHeight = Math.min(Math.floor(initialGroundHeight / Block.SIZE) * Block.SIZE,
                windowDimensions.y() - MIN_GROUND_HEIGHT);
        return (float) initialGroundHeight;
    }

    /**
     * Creates all blocks (2D) in a given horizontal range
     * @param minX x pixel to start from
     * @param maxX x pixel to end at
     */
    public void createInRange(int minX, int maxX){

        for(int x=minX; x<maxX; x += Block.SIZE){
            int height = (int) groundHeightAt(x); //determine height for current x

            for(int y=height; y< windowDimensions.y(); y+= Block.SIZE){
                gameObjects.addGameObject(new Block(new Vector2(x,y),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))),
                        groundLayer);
            }
        }
    }

}
