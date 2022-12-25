package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.PerlinNoise;

import javax.print.attribute.standard.ColorSupported;
import java.awt.*;
import java.util.function.Supplier;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Terrain {
    private static final float TERRAIN_ADJUSTER = 2f/3;
    private static final int MIN_GROUND_HEIGHT = 90;
    private static int max_ground_height;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private final GameObjectCollection gameObjects;
    private final int groundLayer;
    private final Vector2 windowDimensions;
    private float groundHeightAtX0;
    private PerlinNoise perlinNoise;

    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed){
        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        this.windowDimensions = windowDimensions;

        groundHeightAtX0 = this.windowDimensions.y() * TERRAIN_ADJUSTER;

        max_ground_height = (int) (windowDimensions.y() * 0.5f);
        perlinNoise = new PerlinNoise(seed);
    }

    public float groundHeightAt(float x){
//        double initialGroundHeight = (windowDimensions.y() - (3 * Block.SIZE)) - 500 * Math.abs(0.5*(Math.sin(0.005*x)));
        double initialGroundHeight = 700 - 2000 * perlinNoise.noise(x/90);
        System.out.println(initialGroundHeight);
        initialGroundHeight = Math.min(Math.floor(initialGroundHeight / Block.SIZE) * Block.SIZE,windowDimensions.y() - MIN_GROUND_HEIGHT);
//        initialGroundHeight = Math.max(initialGroundHeight,max_ground_height);
        return (float) initialGroundHeight;
    }

    public void createInRange(int minX, int maxX){

        for(int x=minX; x<maxX; x += Block.SIZE){
            int height = (int) groundHeightAt(x);

            for(int y=height; y< windowDimensions.y(); y+= Block.SIZE){
                gameObjects.addGameObject(new Block(new Vector2(x,y),new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))),1);
            }
        }
    }

}
