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
    private static final int SPAWN_TREE_BOUND = 10;
    private static final int IS_A_LEAF_BOUND = 6;
    private static final int NOT_A_LEAF = 5;
    private static final int LOG_ARBITRARY_SIZE = 6;
    private static final int LOG_MINIMUM_SIZE = 7;
    private static final int CREATE_TREE = 0;
    private static final float LEAVES_MATRIX_ADJUSTER = 2/3f;
    private static final int MIN = 0;
    private static final int MAX = 1;
    private static final Color BASE_WOOD_COLOR = new Color(100, 50, 20);
    private static final Color BASE_LEAF_COLOR = new Color(50, 200, 30);
    private final int logLayer;
    private final int leafLayer;
    private final Terrain terrain;
    private final GameObjectCollection gameObjects;
    private final Random random;
    private final float[] avatarRange;
    private boolean spawnTree;

    /**
     * Constructor
     * @param terrain the terrain object
     * @param gameObjects all current added game objects
     * @param leafLayer the layer in which to add the leaves
     * @param seed the random seed used to create the terrain
     */
    public Tree(Terrain terrain, GameObjectCollection gameObjects, int leafLayer, int seed, float[] avatarRange) {
        this.terrain = terrain;
        this.gameObjects = gameObjects;
        this.logLayer = leafLayer - 1;
        this.leafLayer = leafLayer;
        this.random = new Random(seed);
        this.avatarRange = avatarRange;
    }

    /**
     * Creates trees in a given x's range
     * @param minX the starting point from which to create the trees
     * @param maxX the ending point
     */
    public void createInRange(int minX, int maxX) {
        for (int x = minX ; x < maxX; x += Block.SIZE){
            if (random.nextInt(SPAWN_TREE_BOUND) == CREATE_TREE &&
                    !(avatarRange[MIN] <= x && x <= avatarRange[MAX])){
                spawnTree(x);
                spawnTree = true;
            }
        }
    }

    public void spawnTree(int x) {
        int groundHeight = (int) terrain.groundHeightAt(x);
        int blocksInLog = random.nextInt(LOG_ARBITRARY_SIZE) + LOG_MINIMUM_SIZE;

        createLog(x, groundHeight, blocksInLog);
        createLeaves(x,groundHeight - blocksInLog * Block.SIZE, blocksInLog);
    }

    /**
     * Creates the log of the tree
     * @param x x location of the log blocks
     * @param bottomY the height of the lowest log block
     * @param blocksInLog number of log blocks to create
     */
    private void createLog(int x, int bottomY, int blocksInLog){
        for(int y = bottomY - Block.SIZE; y > bottomY - (blocksInLog * Block.SIZE); y -= Block.SIZE){
            Renderable woodRenderable =
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_WOOD_COLOR));
            gameObjects.addGameObject(new Block(new Vector2(x, y), woodRenderable), logLayer);
        }
    }

    /**
     * Creates the leaves of the tree
     * @param middleX the x coordinate of the log, which is the middle of the tree top
     * @param topY the y coordinate of the
     * @param blocksInLog size of the log (int blocks, each is Block.SIZE * Block.SIZE)
     */
    private void createLeaves(int middleX, int topY, int blocksInLog) {
        int leavesMatrixSize = (int) (LEAVES_MATRIX_ADJUSTER * blocksInLog);
        Vector2 topLeftLeaf = leavesMatrixSize % 2 == 0 ? //is the matrix size even?

                //if it does, create the top left leaf based on the actual size
                new Vector2((int) (middleX - 0.5 * leavesMatrixSize * Block.SIZE),
                (int) (topY - 0.5 * leavesMatrixSize * Block.SIZE)) :

                //if it doesn't, shift top left leaf by 1 block, so leaves will be aligned with other blocks
                new Vector2((int) (middleX - 0.5 * (leavesMatrixSize - 1) * Block.SIZE),
                (int) (topY - 0.5 * (leavesMatrixSize - 1) * Block.SIZE));

        //create the leaves
        for(int row = (int) topLeftLeaf.y(); row < topLeftLeaf.y() + leavesMatrixSize * Block.SIZE;
            row += Block.SIZE){
            for(int col = (int) topLeftLeaf.x(); col < topLeftLeaf.x() + leavesMatrixSize * Block.SIZE ;
                col += Block.SIZE){
                if (random.nextInt(IS_A_LEAF_BOUND) == NOT_A_LEAF) continue;
                Renderable leafRenderable =
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_LEAF_COLOR));
                    gameObjects.addGameObject(new Leaf(new Vector2(col, row), leafRenderable, random),
                            leafLayer);
            }
        }
    }

    public boolean treesInGame(){
        return spawnTree;
    }
}





