package towers.tiles;

import engine.game.Position;
import engine.game.Removable;

import java.util.List;

import towers.buildings.Building;

/**
 * One square in the world, responsible for the {@link Building}s stacked on it.
 *
 * @invariant A tile's position never changes once constructed.
 */
public interface Tile extends Position, Removable {


    /**
     * Add a {@link Building} on this tile, making the tile responsible for it.
     *
     * @param building The building for this tile to take responsibility for.
     * @ensures The building is contained in {@link #getBuildings()}.
     */
    void addBuilding(Building building);

    /**
     * The {@link Building}s currently on this tile.
     *
     * @return The buildings this tile is responsible for, possibly empty but never null.
     * @ensures Modifying \result does not modify the tile.
     */
    List<Building> getBuildings();

    /**
     * Whether enemies may walk through this tile.
     *
     * <p>By default, a tile is walkable exactly when nothing has been built on it, so placing any building
     * blocks the square.
     *
     * @return True if this tile can be walked through.
     */
    boolean canWalkThrough();
}
