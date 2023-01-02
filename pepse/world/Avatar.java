package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import pepse.PepseGameManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.HashSet;

public class Avatar extends GameObject{
    private static final Vector2 AVATAR_DIMENSIONS = new Vector2(30, 50); //todo temporary
    private static final HashMap<String, Renderable> AVATAR_RENDERABLE = new HashMap<>();
    private static final float MOVE_SPEED = 250;
    private static final Renderable TEMP = new RectangleRenderable(Color.MAGENTA);
    private static UserInputListener inputListener;
    private static final Vector2 BASIC_GRAVITY = Vector2.DOWN;
    private boolean isJumping;
    private float lastY;
    private Transition<Float> jump;
    private static final float JUMP_HEIGHT = -(6 * Block.SIZE);
    private static float flyEnergy = 100;
    private static final float MAX_ENERGY = 100;
    private static final float ENERGY_CHANGE = 0.5f;

    private static GameObject energyText;
    private static TextRenderable textRenderable;

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

        Avatar avatar = new Avatar(topLeftCorner, AVATAR_DIMENSIONS, TEMP);
        gameObjects.addGameObject(avatar, layer);


        textRenderable = new TextRenderable(flyEnergy + "");
        textRenderable.setColor(Color.BLACK);

        energyText = new GameObject(Vector2.ZERO, new Vector2(100, 100), textRenderable);
        energyText.renderer().setRenderable(textRenderable);
        gameObjects.addGameObject(energyText);

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

        //get user input and update avatar movement accordingly
        Vector2 movementDir = checkInput().add(BASIC_GRAVITY);
        setVelocity(movementDir.mult(MOVE_SPEED));

        //if y coordinate stays still for 2 frames in a row, user is not jumping
        if(this.getCenter().y() == lastY) {
            isJumping = false;
            flyEnergy = Math.min(flyEnergy + ENERGY_CHANGE, MAX_ENERGY);
        }

        lastY = this.getCenter().y(); //update the last frame to contain this frame's y coordinate
        textRenderable.setString(flyEnergy + "");
    }

    /**
     * Handles user input
     * @return the vector in which the avatar should move
     */
    private Vector2 checkInput(){ //todo jumping mechanism is not accurate
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) movementDir = Vector2.LEFT;
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) movementDir = Vector2.RIGHT;
        if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            if (inputListener.isKeyPressed(KeyEvent.VK_SHIFT)) {
                if (flyEnergy > 0) {
                    movementDir = Vector2.UP.mult(3);
                    flyEnergy -= ENERGY_CHANGE;
                    System.out.println(flyEnergy);
                }
            }
            else if (!isJumping) { //jump only if user not jumping already
                isJumping = true; //disable jump until you land back
                System.out.println("jump you stupid");
                //jumping will not be calculated by movementDir - instead, it uses Transition that alters
                // avatar's center, as it looks smoother. at end of transition, it'll be deleted
                jump = new Transition<>(this, (y) -> this.setCenter(new Vector2(this.getCenter().x(), y)),
                        lastY, lastY + JUMP_HEIGHT, Transition.LINEAR_INTERPOLATOR_FLOAT, 0.3f,
                        Transition.TransitionType.TRANSITION_ONCE, () -> this.removeComponent(jump));

                //todo interpolator should be sin(k*x), whereas 2<k<10
            }
        }


        return movementDir;
    }
}
