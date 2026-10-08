package towers.world;

import engine.game.Position;
import engine.game.ScreenPosition;

import java.util.ArrayList;
import java.util.List;

import towers.SelfCleaningCollection;
import towers.events.ListenerManager;
import towers.tiles.Home;
import towers.tiles.Tile;


/**
 * The standard implementation of {@link World}.
 *
 * @invariant Every tile placed into the world remains in it.
 * @invariant At most one {@link Home} tile may exist at a time.
 */
public class CastleWorld implements World {
    //private final List<Tile> tiles = new ArrayList<>();
    private final SelfCleaningCollection<Tile> tiles;

    /**
     * Construct an empty world with no tiles.
     *
     * @ensures {@link #allTiles()} is empty.
     */
    public CastleWorld(ListenerManager listen) {
        tiles = new SelfCleaningCollection<>(listen);
    }

    /**
     * Return the {@link Position} enemies are trying to reach.
     *
     * @return The grid position of the home tile, or null if the world contains none.
     */
    @Override
    public Position getDestination() {
        for (Tile tile : allTiles()) {
            if (tile instanceof Home) {
                return tile.grid();
            }
        }
        return null;
    }

    /**
     * Return the position of every {@link Tile} whose {@link Tile#canWalkThrough()} is true.
     *
     * @return The screen positions of all currently walkable tiles.
     * @ensures \result excludes the position of any tile that has a building on it.
     */
    @Override
    public List<ScreenPosition> walkablePositions() {
        List<ScreenPosition> result = new ArrayList<>();
        for (Tile tile : tiles.toList()) {
            if (tile.canWalkThrough()) {
                result.add(tile.screen());
            }
        }
        return result;
    }

    @Override
    public List<Tile> allTiles() {
        return new ArrayList<>(tiles.toList());
    }


    @Override
    public void place(Tile tile) {
        if (tile instanceof Home) {
            if (getDestination() != null) {
                return;
            }
        }
        tiles.add(tile);
    }
}
