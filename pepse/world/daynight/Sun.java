package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Sun {
    private static final float SIZE_ADJUSTER = 0.15f;
    private static final float INITIAL_LOCATION_X_ADJUSTER = 0.5f;
    private static final float INITIAL_LOCATION_Y_ADJUSTER = 0.1f;
    private static final float X_RADIUS_ADJUSTER = 0.5f;
    private static final float Y_RADIUS_ADJUSTER = 0.8f;
    private static final float LOCATION_ADJUSTER = 0.5f;
    private static final float CENTER_ADJUSTER = 0.5f;
    private static final float INITIAL_CYCLE_VALUE = 0f;
    private static final float FINAL_CYCLE_VALUE = 2f; //2*PI radians in a circle

    /**
     * Creates a new sun object and adds it to the created game objects
     * @param gameObjects all current game objects
     * @param layer the layer in which to crate the sun
     * @param windowDimensions the dimensions of the window
     * @param cycleLength the amount of time (int seconds) for the sun to complete a full circle
     * @return the created sun object (required in PDF)
     */
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength){
        float radius = windowDimensions.y() * SIZE_ADJUSTER;
        Vector2 sunSize = new Vector2(radius, radius);
        Vector2 initialLocation = new Vector2(windowDimensions.x() * INITIAL_LOCATION_X_ADJUSTER,
                windowDimensions.y() * INITIAL_LOCATION_Y_ADJUSTER);

        GameObject sun = new GameObject(initialLocation, sunSize, new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");

        float xRadius = X_RADIUS_ADJUSTER * windowDimensions.x();
        float yRadius = Y_RADIUS_ADJUSTER * windowDimensions.y();
        
        //transitions between sun location
        new Transition<>(sun, rad -> sun.setCenter(new Vector2(
                (float) (xRadius * (Math.cos((rad + LOCATION_ADJUSTER) * Math.PI)) +
                        CENTER_ADJUSTER * windowDimensions.x()),
                (float) -(yRadius * Math.sin((rad + LOCATION_ADJUSTER) * Math.PI)) + windowDimensions.y())),
                INITIAL_CYCLE_VALUE, FINAL_CYCLE_VALUE, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,null);

        gameObjects.addGameObject(sun,layer);

        return sun;
    }
}
