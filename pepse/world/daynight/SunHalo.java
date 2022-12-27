package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import java.awt.*;

public class SunHalo {
    private static final float haloSize = 1.5f;

    /**
     * Creates a halo around a given sun object
     * @param gameObjects collection of all current game objects already added
     * @param layer the layer in which to add the halo
     * @param sun the sun object itself
     * @param color the color of the halo
     * @return the created halo object (demanded by API)
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, GameObject sun, Color color){

        GameObject sunHalo = new GameObject(sun.getCenter(), sun.getDimensions().mult(haloSize),
                new OvalRenderable(color));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag("sunHalo");

        gameObjects.addGameObject(sunHalo,layer);

        return sunHalo;
    }
}
