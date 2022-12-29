package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

public class Leaf extends Block {
    private static final float START_ROTATION_BOUND = 5f;
    private static final float START_CHANGE_SIZE_BOUND = 7f;
    private static final float LEAF_MIN_SIZE = 0.8f;
    private static final float LEAF_MAX_ROTATION = 30f;
    private static final float FADEOUT_TIME = 20f;
    private static final int CHANGE_SIZE_TIME = 2;
    private static final int ROTATE_TIME = 3;
    private static final int MOVE_VERTICALLY_TIME = 1;
    private static final int LEAF_DOWNFALL_SPEED = 30;
    private static final int MIN_TIME_ON_TREE = 5;
    private static final int MAX_TIME_ON_TREE = 150;
    private static final int MIN_REBORN_SUSPENSION = 1;
    private static final int MAX_REBORN_SUSPENSION = 5;
    private static final int STOP_LEAF = 0;
    private static final int OPAQUE = 1;
    private final Random random;
    private final Vector2 leafCenter;
    private final Supplier<Float> leafMoveDirection;
    private Transition<Float> moveLeafVertically;
    private Transition<Float> rotateLeaf;
    private Transition<Float> changeSize;

    /**
     * Constructor
     * @param topLeftCorner the coordinate from where start measure the leaf
     * @param renderable visual representation of the object
     * @param random random object, containing the simulator seed
     */
    public Leaf(Vector2 topLeftCorner, Renderable renderable, Random random) {
        super(topLeftCorner, renderable);
        this.random = random;
        this.leafCenter = this.getCenter();
        this.leafMoveDirection =  () -> random.nextFloat(15,30) * (random.nextBoolean() ? 1 : -1);
        //all numbers above are for visual purposes only and has no actual meaning code-wise

        beginLifeCycle();
    }

    /**
     * Manages the leaves life cycle of:
     * move in place -> fall to the ground with horizontal movement while fading out -> complete fade out,
     * wait random time and go back to the tree -> repeat
     */
    private void beginLifeCycle(){
        //1. bring leaf to its original state
        this.setCenter(this.leafCenter);
        this.renderer().setOpaqueness(OPAQUE);

        //2. animate leaf in-place
        new ScheduledTask(this, random.nextFloat(START_ROTATION_BOUND), false, this::rotateLeaf);
        new ScheduledTask(this, random.nextFloat(START_CHANGE_SIZE_BOUND), false, this::changeLeafSize);

        //3. drop leaf, remove it and eventually bring it back to stage 1
        float arbitrary = random.nextFloat(MIN_TIME_ON_TREE,MAX_TIME_ON_TREE);
        new ScheduledTask(this, arbitrary, false, this::dropLeaf);
        new ScheduledTask(this, FADEOUT_TIME + arbitrary +
                random.nextFloat(MIN_REBORN_SUSPENSION, MAX_REBORN_SUSPENSION), false, this::beginLifeCycle);
    }

    /**
     * Continuously rotates leaf in its place
     */
    private void rotateLeaf(){
        this.rotateLeaf = new Transition<>(this, this.renderer()::setRenderableAngle,
                this.renderer().getRenderableAngle(), LEAF_MAX_ROTATION,Transition.LINEAR_INTERPOLATOR_FLOAT,
                ROTATE_TIME, Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * Continuously changes the leaf size
     */
    private void changeLeafSize(){
        this.changeSize = new Transition<>
                (this, (size) -> this.setDimensions(new Vector2(size, size)), (float) Leaf.SIZE,
                Leaf.SIZE * LEAF_MIN_SIZE, Transition.LINEAR_INTERPOLATOR_FLOAT,CHANGE_SIZE_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
    }

    /**
     * Drops leaf from tree and animates its horizontal movement and fadeout
     */
    private void dropLeaf(){
        this.renderer().fadeOut(FADEOUT_TIME);
        this.transform().setVelocityY(LEAF_DOWNFALL_SPEED);
        moveLeafVertically = new Transition<>(this, x -> this.transform().setCenter(x, this.getCenter().y()),
                this.getCenter().x(),this.getCenter().x() + leafMoveDirection.get(),
                Transition.LINEAR_INTERPOLATOR_FLOAT, MOVE_VERTICALLY_TIME,
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
