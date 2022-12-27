package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;
import java.awt.*;
import java.util.Random;

public class Tree {
    public static final int SPAWN_TREE_BOUND = 10;
    private static final int IS_THERE_A_LEAF_BOUND = 5;
    public static final int LOG_LENGTH_BOUND = 6;
    private static final int LOG_MINIMUM_SIZE = 7;
    private static final int CREATE_TREE = 0;
    private final int treeLayer;
    private static final Color BASE_WOOD_COLOR = new Color(100, 50, 20);
    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);
    private final Terrain terrain;
    private final GameObjectCollection gameObjects;
    private final Random random = new Random();

    public Tree(Terrain terrain, GameObjectCollection gameObjects, int treeLayer, Vector2 windowDimensions, int seed) {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.treeLayer = treeLayer;
    }

    public void createInRange(int minX, int maxX) {
        for (int x = minX ; x < maxX; x += Block.SIZE){
            if (random.nextInt(SPAWN_TREE_BOUND) == CREATE_TREE){
                int groundHeight = (int) terrain.groundHeightAt(x);
                int blocksInLog = random.nextInt(LOG_LENGTH_BOUND) + LOG_MINIMUM_SIZE;

                createLog(x, groundHeight, blocksInLog);
                createLeaves(x,groundHeight - blocksInLog * Block.SIZE, blocksInLog);
            }
        }
    }

    private void createLog(int xCoordinate, int bottomY, int blocksInLog){
        for(int y = bottomY - Block.SIZE; y > bottomY - (blocksInLog * Block.SIZE); y -= Block.SIZE){
            Renderable woodRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_WOOD_COLOR));
            gameObjects.addGameObject(new Block(new Vector2(xCoordinate, y), woodRenderable),treeLayer);
        }
    }

    private void createLeaves(int xCoordinate, int upperY, int blocksInLog) {
        //starting coordinate for the leaves matrix
        int treeTopBlocks = 2 * blocksInLog /3; //2 thirds of the log height - size of matrix
        Vector2 topLeftCornerOfLeaf = new Vector2((int) (xCoordinate - (0.5 * treeTopBlocks * Block.SIZE)),
                (int) (upperY -  0.5 * treeTopBlocks * Block.SIZE));

        for(int row = (int) topLeftCornerOfLeaf.y(); row < topLeftCornerOfLeaf.y() +
                treeTopBlocks * Block.SIZE ;row += Block.SIZE){
            for(int col = (int) topLeftCornerOfLeaf.x(); col < topLeftCornerOfLeaf.x() +
                    treeTopBlocks * Block.SIZE ;col += Block.SIZE){
                if (random.nextInt(IS_THERE_A_LEAF_BOUND) < 4) {
                    Renderable leafRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR));
                    gameObjects.addGameObject(new Leaf(new Vector2(col, row), leafRenderable, gameObjects), treeLayer);
                }
            }
        }
    }

}





