package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Block extends GameObject {
    public static final int SIZE = 30;

    /**
     * Constructor
     * @param topLeftCorner top left corner of the block (from where to start measure its size)
     * @param renderable a renderable object used by the GameObject constructor
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);

        physics().preventIntersectionsFromDirection(Vector2.ZERO); ////other objects can't pass through
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);       //on collision, this object won't move
    }
}
