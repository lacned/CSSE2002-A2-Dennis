package test_utils.mocks;

import engine.EngineState;
import engine.game.GridPosition;
import engine.game.Position;
import engine.game.ScreenPosition;
import engine.input.KeyState;
import engine.input.MouseState;
import engine.renderer.Renderable;
import engine.renderer.Spatial;
import towers.tiles.Tile;
import towers.world.World;

import java.util.ArrayList;
import java.util.List;

public class MockWorld implements World {
    private Position endNode = new GridPosition(5, 5);

    public MockWorld() {
    }

    public MockWorld(Position endNode) {
        this.endNode = endNode;
    }

    List<Tile> tiles = new ArrayList<>();
    private int placeTileCallCount = 0;
    private int cleanupCallCount = 0;
    private int tickCallCount = 0;

    public int getPlaceTileCallCount() {
        return placeTileCallCount;
    }

    public int getCleanupCallCount() {
        return cleanupCallCount;
    }

    public int getTickCallCount() {
        return tickCallCount;
    }

    /**
     * Return all {@link Tile}s in the world.
     *
     * <p>Modifying the returned {@link List} must not modify the state of the world
     * (although modifying the tiles within the list will).</p>
     *
     * <p>The order of the {@link List} is unspecified, any ordering is suitable.</p>
     *
     * @return Returns all {@link Tile}s in the world.
     */
    @Override
    public List<Tile> allTiles() {
        return tiles;
    }

    /**
     * Place a new {@link Tile} into the world.
     *
     * <p>The tile will be placed at the position
     * specified by its {@link Tile#screen()}}.</p>
     *
     * @param tile The tile to place into the world.
     * @ensures Any calls to will reflect the existence
     * of this new tile in the world.
     */
    @Override
    public void place(Tile tile) {
        placeTileCallCount += 1;
    }


    public List<Renderable> render() {
        return List.of();
    }

    /**
     * Return the {@link Position} of the End Node.
     *
     * @return the {@link Position} of the End Node.
     */
    @Override
    public Position getDestination() {
        return null;
    }

    /**
     * Returns a list of {@link List<ScreenPosition>}s that can be pathed / walked through.
     *
     * @return a list of {@link List<ScreenPosition>}s that can be pathed / walked through.
     */
    @Override
    public List<ScreenPosition> walkablePositions() {
        return List.of();
    }
}
