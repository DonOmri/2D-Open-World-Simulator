package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Moon { //todo should sun and moon extend a basic planetary object?
    private static final float SIZE_ADJUSTER = 0.15f;
    private static final float INITIAL_LOCATION_X_ADJUSTER = 0.5f;
    private static final float INITIAL_LOCATION_Y_ADJUSTER = 1.9f;
    private static final float X_RADIUS_ADJUSTER = 0.5f;
    private static final float Y_RADIUS_ADJUSTER = 0.8f;
    private static final float LOCATION_ADJUSTER = -0.5f;
    private static final float CENTER_ADJUSTER = 0.5f;
    private static final float INITIAL_CYCLE_VALUE = 0f;
    private static final float FINAL_CYCLE_VALUE = 2f; //2*PI radians in a circle

    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength){
        float radius = windowDimensions.y() * SIZE_ADJUSTER;
        Vector2 moonSize = new Vector2(radius, radius);
        Vector2 initialLocation = new Vector2(windowDimensions.x() * INITIAL_LOCATION_X_ADJUSTER,
                windowDimensions.y() * INITIAL_LOCATION_Y_ADJUSTER);

        GameObject moon = new GameObject(initialLocation, moonSize, new OvalRenderable(Color.LIGHT_GRAY));
        moon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        moon.setTag("moon");

        float xRadius = X_RADIUS_ADJUSTER * windowDimensions.x();
        float yRadius = Y_RADIUS_ADJUSTER * windowDimensions.y();

        //transitions between sun location
        new Transition<>(moon, rad -> moon.setCenter(new Vector2(
                (float) (xRadius * (Math.cos((rad + LOCATION_ADJUSTER) * Math.PI)) +
                        CENTER_ADJUSTER * windowDimensions.x()),
                (float) -(yRadius * Math.sin((rad + LOCATION_ADJUSTER) * Math.PI)) + windowDimensions.y())),
                INITIAL_CYCLE_VALUE, FINAL_CYCLE_VALUE, Transition.LINEAR_INTERPOLATOR_FLOAT, cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,null);

        gameObjects.addGameObject(moon,layer);

        return moon;
    }
}
