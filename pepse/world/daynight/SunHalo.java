package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import java.awt.*;

public class SunHalo {
    private static final float haloSize = 1.5f;

    /**
     * Creates a new sun halo object and adds it to the created game objects
     * @param gameObjects all current game objects
     * @param layer the layer in which to crate the sun halo
     * @param sun the sun object to link the sun halo to
     * @param color the color of the halo
     * @return the created SunHalo object (required in PDF)
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
