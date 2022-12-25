package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.Sky;
import pepse.world.Terrain;

import java.awt.*;

import static danogl.collisions.Layer.BACKGROUND;

public class PepseGameManager extends GameManager {
    private static final Vector2 WINDOW_DIMENSIONS = new Vector2(1440,910);
    private static final String WINDOW_TITLE = "Pepse";

    public PepseGameManager(String windowTitle, Vector2 windowDimensions){
        super(windowTitle, windowDimensions);
    }

    public static void main(String[] args){
        new PepseGameManager(WINDOW_TITLE, WINDOW_DIMENSIONS).run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener
            inputListener, WindowController windowController) {

        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        Sky.create(gameObjects(), windowController.getWindowDimensions(),BACKGROUND);

        /**test terrain heights**/
        Terrain newTerrain = new Terrain(gameObjects(),1,windowController.getWindowDimensions(),1);
        newTerrain.createInRange(0,(int) windowController.getWindowDimensions().x());
//        for(int x=0; x<windowController.getWindowDimensions().x(); x += 30){
//            System.out.println(x + ": " + newTerrain.groundHeightAt(x));
//        }
    }
}
