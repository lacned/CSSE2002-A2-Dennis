package towers.world;

import engine.pathing.PathingMap;
import java.util.List;

import towers.tiles.Tile;

/**
 * The board the game is played on: a grid of {@link Tile}s and everything built on them.
 */
public interface World extends PathingMap {

    /**
     * Return every {@link Tile} in the world.
     *
     * <p>Order is unspecified.</p>
     *
     * @return All tiles in the world, possibly empty but never null.
     * @ensures Modifying \result does not modify the world.
     */
    List<Tile> allTiles();

    /**
     * Add a {@link Tile} to the world, at the {@link engine.game.Position} the tile already holds.
     * If the world already has a {@link towers.tiles.Home} (i.e. {@link #getDestination() != null}) and a {@link towers.tiles.Home} is given,
     * it is not added to the world.
     *
     * @param tile The tile to place into the world.
     * @ensures The tile is contained in {@link #allTiles()}, if it is not a duplicate {@link towers.tiles.Home}.
     */
    void place(Tile tile);
}
