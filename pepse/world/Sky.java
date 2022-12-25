package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Sky {
    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");

    /**
     * Creates the sky block
     * @param gameObjects a collection of all current gameObjects, to add the sky to
     * @param windowDimensions dimensions of the window
     * @param skyLayer the layer in which to add the sky
     * @return returns the sky gameObjects (required in PDF)
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions,
                                    int skyLayer){
        GameObject sky = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag("sky");

        gameObjects.addGameObject(sky,skyLayer);

        return sky;
    }
}
