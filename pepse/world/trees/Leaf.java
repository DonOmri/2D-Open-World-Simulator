package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;


public class Leaf extends Block {
    private final Vector2 topLeftCorner;
    private final Renderable renderable;
    private final GameObjectCollection gameObjects;

    public Leaf(Vector2 topLeftCorner, Renderable renderable, GameObjectCollection gameObjects) {
        super(topLeftCorner, renderable);
        this.topLeftCorner = topLeftCorner;
        this.renderable = renderable;
        this.gameObjects = gameObjects;

        gameObjects.addGameObject(this);

    }


}
