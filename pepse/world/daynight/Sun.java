package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Sun {

    private static final float LOCATION_ADJUSTER = 0.5f;
    private static final float CENTER_ADJUSTER = 0.5f;
    private static final float INITIAL_CYCLE_VALUE = 0f;
    private static final float FINAL_CYCLE_VALUE = 2f; //2*PI radians in a circle
    public static GameObject create(GameObjectCollection gameObjects, int layer, Vector2 windowDimensions,
                                    float cycleLength){
        float radius = windowDimensions.y()*0.15f;
        Vector2 initialLocation = new Vector2(windowDimensions.x()*0.5f, windowDimensions.y()*0.1f);
        GameObject sun = new GameObject(initialLocation, new Vector2(radius,radius),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");


        float xRadius = 0.5f * windowDimensions.x();
        float yRadius = 0.8f * windowDimensions.y();
        
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
