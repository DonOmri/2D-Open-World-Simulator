package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject{
    private static final Vector2 AVATAR_SIZE = new Vector2(45, 60);
    private static final float MOVE_SPEED = 250;
    private static final float FLY_SPEED = 3.5f;
    private static final float JUMP_SPEED = 3f, JUMP_TIME = 0.5f;
    private static final float TIME_BETWEEN_CLIPS = 0.2f;
    private static final int ACCELERATION_Y = 500;
    private static final int TIME_BEFORE_PANIC = 1;
    private static final String HERO_PATH = "pepse/assets/Hero";
    private static final String PNG_EXTENSION = ".png";
    private static final Vector2 BASIC_GRAVITY = Vector2.DOWN;
    private static UserInputListener inputListener;
    private static AnimationRenderable avatarStand, avatarJump, avatarWalk, avatarFly;
    private static Renderable avatarFall;
    private Vector2 lastPosition = Vector2.ZERO;
    private boolean isJumping, isFlying, isFalling;
    private static Energy energy;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        this.transform().setAccelerationY(ACCELERATION_Y);

        physics().preventIntersectionsFromDirection(Vector2.ZERO); //other objects can't pass through
    }

    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader){
        Avatar.inputListener = inputListener;
        energy = new Energy(gameObjects);

        //create animations
        avatarStand = createAvatarAnimation(imageReader, 4, "Stand", TIME_BETWEEN_CLIPS);
        avatarJump = createAvatarAnimation(imageReader, 4, "Jump", TIME_BETWEEN_CLIPS + 0.1f);
        avatarWalk = createAvatarAnimation(imageReader, 6, "Walk", TIME_BETWEEN_CLIPS);
        avatarFly = createAvatarAnimation(imageReader, 6, "Fly", TIME_BETWEEN_CLIPS);
        avatarFall = imageReader.readImage(HERO_PATH + "Fall" + PNG_EXTENSION, true);

        Avatar avatar = new Avatar(topLeftCorner.subtract(new Vector2(0, AVATAR_SIZE.y())), AVATAR_SIZE,
                avatarStand);
        gameObjects.addGameObject(avatar, layer);

        return avatar;
    }

    /**
     * Function that is called every frame of the game
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        Vector2 movementDir = checkInput().add(BASIC_GRAVITY);

        if (this.getCenter().y() == lastPosition.y()) { //either stand or walk
            energy.updateEnergy(true);

            renderer().setRenderable(this.getCenter().x() == lastPosition.x() ? avatarStand : avatarWalk);
        }

        if (isJumping && renderer().getRenderable() != avatarJump) renderer().setRenderable(avatarJump);
        else if (isFlying && renderer().getRenderable() != avatarFly) renderer().setRenderable(avatarFly);
        else if (this.getCenter().y() > lastPosition.y() && !isFalling) {
            isFalling = true;
            new ScheduledTask(this, TIME_BEFORE_PANIC, false, () -> {
                if (isFalling) renderer().setRenderable(avatarFall);
            });
        }

        if (this.getCenter().y() <= lastPosition.y()) isFalling  = false;

        setVelocity(movementDir.mult(MOVE_SPEED));

        lastPosition = this.getCenter();
    }

    /**
     * creates the animation renderable for each avatar status.
     * @param imageReader the image reader
     * @param clipsNum the number of pictures in each animation
     * @param type the status of the avatar (walking, falling etc.)
     * @param timeBetweenClips time between each picture in one animation
     * @return the animationRenderable object containing the relevant animation clips
     */
    private static AnimationRenderable createAvatarAnimation(ImageReader imageReader, int clipsNum,
                                                             String type, float timeBetweenClips){
        Renderable[] clips = new Renderable[clipsNum];
        for (int i = 0; i < clips.length; ++i) {
            clips[i] = imageReader.readImage(HERO_PATH+ type + i + PNG_EXTENSION,true);
        }

        return new AnimationRenderable(clips, timeBetweenClips);
    }

    /**
     * Handles user input
     * @return the vector in which the avatar should move
     */
    private Vector2 checkInput(){
        Vector2 movementDir = Vector2.ZERO;
        isFlying = false;

        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = Vector2.LEFT;
            renderer().setIsFlippedHorizontally(true);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            movementDir = Vector2.RIGHT;
            renderer().setIsFlippedHorizontally(false);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) movementDir = fly(movementDir);

            else if (this.getCenter().y() == lastPosition.y()) {
                isJumping = true;
                new ScheduledTask(this, JUMP_TIME, false, () -> isJumping = false);
            }
        }

        return movementDir.add(isJumping ? Vector2.UP.mult(JUMP_SPEED) : Vector2.ZERO);
    }

    /**
     * a function that makes the avatar fly.
     * @param dir the current movement direction of the avatar.
     * @return a new movement direction of flight according to whether or not the avatar is jumping.
     */
    private Vector2 fly(Vector2 dir) {
        if (energy.getFlyEnergy() > 0) {
            dir = dir.add(Vector2.UP.mult(isJumping? FLY_SPEED - JUMP_SPEED : FLY_SPEED));
            energy.updateEnergy(false);
            isFlying = true;
        }
        return dir;
    }
}
