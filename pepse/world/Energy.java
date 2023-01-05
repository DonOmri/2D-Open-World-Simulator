package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Energy {
    private static final int MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;
    private static float flyEnergy = 100;
    private static TextRenderable textRenderable;

    /**
     * Constructor
     * @param gameObjects collection of all added game objects
     */
    public Energy(GameObjectCollection gameObjects){

        textRenderable = new TextRenderable("Energy\n" + flyEnergy);
        textRenderable.setColor(Color.BLACK);

        GameObject energyText = new GameObject(Vector2.ZERO, Vector2.ONES.mult(100), textRenderable);
        energyText.renderer().setRenderable(textRenderable);
        energyText.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects.addGameObject(energyText);
    }

    /**
     * updates the energy, either decreases or increases by fixed amount
     * @param increase should the update increase or decrease the energy amount
     */
    public void updateEnergy(boolean increase){
        flyEnergy += increase ? ENERGY_CHANGE : -ENERGY_CHANGE;
        flyEnergy = Math.min(flyEnergy, MAX_ENERGY);

        textRenderable.setString("Energy\n" + flyEnergy);
    }

    /**
     * gets the current fly energy
     * @return the amount of fly energy
     */
    public float getFlyEnergy(){
        return flyEnergy;
    }
}
