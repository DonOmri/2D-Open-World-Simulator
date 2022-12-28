package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import java.util.Random;


public class Leaf extends Block {
    private final Random random;

    private static final float LEAF_MIN_SIZE = 0.8f;
    private static final float LEAF_MAX_ROTATION = 30f;
    private static final float FADEOUT_TIME = 20f;

    public Leaf(Vector2 topLeftCorner, Renderable renderable, GameObjectCollection gameObjects, Random random) {
        super(topLeftCorner, renderable);
        this.random = random;

        new ScheduledTask(this, random.nextInt(5), false, () -> new Transition<>
                (this, this.renderer()::setRenderableAngle, this.renderer().getRenderableAngle(),
                LEAF_MAX_ROTATION, Transition.LINEAR_INTERPOLATOR_FLOAT, 3,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null));

        new ScheduledTask(this, random.nextInt(5), false, () -> new Transition<>
                (this, (size) -> this.setDimensions(new Vector2(size, size)), (float) Leaf.SIZE,
                Leaf.SIZE * LEAF_MIN_SIZE, Transition.LINEAR_INTERPOLATOR_FLOAT,2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null));

        leafCycle();
    }

    private void leafCycle(){
        new ScheduledTask(this, random.nextInt(10), false,
                () -> {this.renderer().fadeOut(FADEOUT_TIME);
                       this.transform().setVelocityY(30);
                       float x = this.getCenter().x();
                       new Transition<>(this, pos -> this.transform().setCenter(pos, this.getCenter().y()),
                               x,x+15, Transition.LINEAR_INTERPOLATOR_FLOAT,1,
                               Transition.TransitionType.TRANSITION_BACK_AND_FORTH,null);});

    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        this.transform().setVelocityY(0);
        super.onCollisionEnter(other, collision);
    }
}
