package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Night {
    private static final float MIDNIGHT_OPACITY = 0.5f;

    /**
     * Creates a new night object and adds it to the created game objects
     * @param gameObjects all current game objects
     * @param layer the layer in which to crate the night
     * @param windowDimensions the dimensions of the window
     * @param cycleLength the amount of time (int seconds) for the night to turn from midday to midnight
     * @return the created night object (required in PDF)
     */
    public static GameObject create(GameObjectCollection gameObjects, Vector2 windowDimensions, int layer,
                                    float cycleLength){
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");

        //transitions between the night opaqueness, to illustrate night and day
        new Transition<>(night,night.renderer()::setOpaqueness,0f, MIDNIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,null);
        gameObjects.addGameObject(night,layer);

        return night;
    }
}
