package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.*;
import danogl.util.Vector2;
import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject{
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(45, 60);
    private static final float MOVE_SPEED = 250;
    private static final float FLYING_SPEED = 3f;
    private static final float JUMPING_SPEED = 2.5f;
    private static final int NO_FLY_ENERGY = 0;
    private static final float MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;
    private static final float TIME_BETWEEN_CLIPS = 0.2f;
    private static final String ASSETS_PATH = "pepse/assets";
    private static final Vector2 BASIC_GRAVITY = Vector2.DOWN;
    private static float flyEnergy = 100;
    private static UserInputListener inputListener;
    private static GameObjectCollection gameObjects;
    private static TextRenderable textRenderable;
    private static AnimationRenderable avatarStand, avatarJump, avatarWalk, avatarFly;
    private static Renderable avatarFall, avatarParachute;
    private static GameObject parachute = null;
    private boolean isJumping;
    private Vector2 lastPosition = Vector2.ZERO;

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

        physics().preventIntersectionsFromDirection(Vector2.ZERO); //other objects can't pass through
    }

    public static Avatar create(GameObjectCollection gameObjects, int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener, ImageReader imageReader){
        Avatar.inputListener = inputListener;
        Avatar.gameObjects = gameObjects;

        avatarStand = createAvatarAnimation(imageReader, 4, "Stand");
        avatarJump = createAvatarAnimation(imageReader, 4, "Jump");
        avatarWalk = createAvatarAnimation(imageReader, 6, "Walk");
        avatarFly = createAvatarAnimation(imageReader, 0, "Fall");

        avatarFall = imageReader.readImage(ASSETS_PATH + "/HeroFall.png", true);
        avatarParachute = imageReader.readImage(ASSETS_PATH + "/HeroParachute.png", true);

        Avatar avatar = new Avatar(topLeftCorner.subtract(new Vector2(0, AVATAR_DIMENSIONS.y())), AVATAR_DIMENSIONS, avatarStand);
        gameObjects.addGameObject(avatar, layer);

        textRenderable = new TextRenderable("Energy\n" + flyEnergy);
        textRenderable.setColor(Color.BLACK);

        GameObject energyText = new GameObject(Vector2.ZERO, new Vector2(100, 100), textRenderable);
        energyText.renderer().setRenderable(textRenderable);
        gameObjects.addGameObject(energyText);

        return avatar;
    }

    private static AnimationRenderable createAvatarAnimation(ImageReader imageReader, int clipsNum, String type){
        Renderable[] clips = new Renderable[clipsNum];
        for (int i = 0; i < clips.length; i++) {
            clips[i] = imageReader.readImage(ASSETS_PATH + "/Hero" + type + i + ".png",true);
        }

        return new AnimationRenderable(clips, TIME_BETWEEN_CLIPS);
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

        Vector2 movementDir = checkInput();

        if (this.getCenter().y() == lastPosition.y()){ //either stand or walk
            flyEnergy = Math.min(flyEnergy + ENERGY_CHANGE, MAX_ENERGY);

            renderer().setRenderable(this.getCenter().x() == lastPosition.x() ? avatarStand : avatarWalk);

            if (parachute != null) gameObjects.removeGameObject(parachute, Layer.DEFAULT -1);
        }

        if (isJumping) movementDir = movementDir.add(Vector2.UP.mult(JUMPING_SPEED));
        else movementDir = movementDir.add(BASIC_GRAVITY);

        if (this.getCenter().y() > lastPosition.y()) {
            movementDir = movementDir.add(BASIC_GRAVITY);
            if(renderer().getRenderable() != avatarFall) {
                renderer().setRenderable(avatarFall);

                parachute = new GameObject(this.getTopLeftCorner().add(new Vector2(-9, -45)),Vector2.ONES.mult(60), avatarParachute);
                gameObjects.addGameObject(parachute, Layer.DEFAULT - 1);
            }
        }
        else if (this.getCenter().y() < lastPosition.y()) renderer().setRenderable(avatarJump);

        if(parachute != null) parachute.setTopLeftCorner(this.getTopLeftCorner().add(new Vector2(-9, -45)));
        setVelocity(movementDir.mult(MOVE_SPEED));

        lastPosition = this.getCenter();
        textRenderable.setString("Energy\n" + flyEnergy);
    }

    /**
     * Handles user input
     * @return the vector in which the avatar should move
     */
    private Vector2 checkInput(){
        Vector2 movementDir = Vector2.ZERO;

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
                new ScheduledTask(this, 0.7f, false, () -> isJumping = false);
            }
        }

        return movementDir;
    }

    /**
     * a function that makes the avatar fly.
     * @param dir the current movement direction of the avatar.
     * @return a new movement direction of flight according to whether or not the avatar is jumping.
     */
    private Vector2 fly(Vector2 dir) {
        if (flyEnergy > NO_FLY_ENERGY) {
            dir = dir.add(Vector2.UP.mult(isJumping? FLYING_SPEED - JUMPING_SPEED : FLYING_SPEED));
            flyEnergy -= ENERGY_CHANGE;
        }
        return dir;
    }
}
