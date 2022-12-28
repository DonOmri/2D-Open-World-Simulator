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
    private static final int STOP_LEAF = 0;
    private Transition<Float> moveLeafVertically;
    private Transition<Float> rotateLeaf;
    private Transition<Float> changeSize;
    private final Vector2 leafCenter;

    public Leaf(Vector2 topLeftCorner, Renderable renderable, GameObjectCollection gameObjects, Random random) {
        super(topLeftCorner, renderable);
        this.random = random;
        this.leafCenter = this.getCenter();

        beginLifeCycle();
    }

    private void beginLifeCycle(){ //todo drop leaves on float times and not on int times
        this.setCenter(this.leafCenter);
        this.renderer().setOpaqueness(1);

        //when leaf is attached to tree
        new ScheduledTask(this, random.nextFloat(5), false, this::rotateLeaf);
        new ScheduledTask(this, random.nextFloat(7), false, this::changeLeafSize);

        //when leaf drops
        float arbitrary = random.nextFloat(5,150);
        new ScheduledTask(this, arbitrary, false, this::dropLeaf);
        new ScheduledTask(this, FADEOUT_TIME + arbitrary + random.nextFloat(4), false, this::beginLifeCycle);//todo do we have to get random here?
    }

    private void rotateLeaf(){
        this.rotateLeaf = new Transition<>(this, this.renderer()::setRenderableAngle,
                this.renderer().getRenderableAngle(), LEAF_MAX_ROTATION,Transition.LINEAR_INTERPOLATOR_FLOAT,
                3, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    private void changeLeafSize(){
        this.changeSize = new Transition<>
                (this, (size) -> this.setDimensions(new Vector2(size, size)), (float) Leaf.SIZE,
                Leaf.SIZE * LEAF_MIN_SIZE, Transition.LINEAR_INTERPOLATOR_FLOAT,2,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * Animates leaf movement and color when it drops from the treetop
     */
    private void dropLeaf(){
        this.renderer().fadeOut(FADEOUT_TIME);
        this.transform().setVelocityY(30);
        moveLeafVertically = new Transition<>(this, x -> this.transform().setCenter(x, this.getCenter().y()),
                this.getCenter().x(),this.getCenter().x() + (random.nextBoolean() ? 20 : -20),
                Transition.LINEAR_INTERPOLATOR_FLOAT,1,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,null);
    }

    /**
     * determines behaviour on collision of this object with another
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        this.transform().setVelocityY(STOP_LEAF);
        this.removeComponent(rotateLeaf);
        this.removeComponent(changeSize);
        this.removeComponent(moveLeafVertically);
        super.onCollisionEnter(other, collision);
    }
}
