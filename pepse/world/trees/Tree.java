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
    private static final int IS_THERE_A_LEAF_BOUND = 6;
    public static final int LOG_ARBITRARY_SIZE = 6;
    private static final int LOG_MINIMUM_SIZE = 7;
    private static final int CREATE_TREE = 0;
    private final int logLayer;
    private final int leafLayer;
    private static final Color BASE_WOOD_COLOR = new Color(100, 50, 20);
    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);
    private final Terrain terrain;
    private final GameObjectCollection gameObjects;
    private final Random random;

    public Tree(Terrain terrain, GameObjectCollection gameObjects, int leafLayer, Vector2 windowDimensions, int seed) {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.logLayer = leafLayer - 1;
        this.leafLayer = leafLayer;
        this.random = new Random(seed);
    }

    public void createInRange(int minX, int maxX) {
        for (int x = minX ; x < maxX; x += Block.SIZE){
            if (random.nextInt(SPAWN_TREE_BOUND) == CREATE_TREE){
                int groundHeight = (int) terrain.groundHeightAt(x);
                int blocksInLog = random.nextInt(LOG_ARBITRARY_SIZE) + LOG_MINIMUM_SIZE;

                createLog(x, groundHeight, blocksInLog);
                createLeaves(x,groundHeight - blocksInLog * Block.SIZE, blocksInLog);
            }
        }
    }

    private void createLog(int x, int bottomY, int blocksInLog){
        for(int y = bottomY - Block.SIZE; y > bottomY - (blocksInLog * Block.SIZE); y -= Block.SIZE){
            Renderable woodRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_WOOD_COLOR));
            gameObjects.addGameObject(new Block(new Vector2(x, y), woodRenderable), logLayer);
        }
    }

    private void createLeaves(int middleX, int topY, int blocksInLog) { //todo magic numbers
        int leavesMatrixSize = 2 * blocksInLog / 3; //2 thirds of the log height - size of matrix
        Vector2 topLeftLeaf = leavesMatrixSize % 2 == 0 ?
                new Vector2((int) (middleX - 0.5 * leavesMatrixSize * Block.SIZE),
                (int) (topY -  0.5 * leavesMatrixSize * Block.SIZE)) :
                new Vector2((int) (middleX - 0.5 * (leavesMatrixSize - 1) * Block.SIZE),
                (int) (topY -  0.5 * (leavesMatrixSize - 1) * Block.SIZE));

        for(int row = (int) topLeftLeaf.y(); row < topLeftLeaf.y() +
                leavesMatrixSize * Block.SIZE ;row += Block.SIZE){
            for(int col = (int) topLeftLeaf.x(); col < topLeftLeaf.x() +
                    leavesMatrixSize * Block.SIZE ;col += Block.SIZE){
                if (random.nextInt(IS_THERE_A_LEAF_BOUND) < 5) {
                    Renderable leafRenderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR));
                    gameObjects.addGameObject(new Leaf(new Vector2(col, row), leafRenderable, gameObjects, random), leafLayer);
                }
            }
        }
    }

}





