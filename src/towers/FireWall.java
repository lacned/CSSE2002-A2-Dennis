package towers;

import engine.Engine;
import engine.game.*;

import java.util.ArrayList;
import java.util.List;

import towers.enemies.EnemyManager;
import towers.events.ListenerManager;
import towers.resources.Resource;
import towers.resources.ResourceManager;
import towers.tiles.*;
import towers.world.World;
import towers.world.WorldBuilder;
import towers.world.WorldLoadException;

/**
 * The tower defence game. The engine progresses the game and determines what to render via this class.
 *
 * @invariant Once the game is over it never resumes.
 */
public class FireWall {
    private final ResourceManager resources;
    private final World world;
    private final EnemyManager enemies;

    /**
     * Construct a game from the given map or use the default map if the given is empty.
     *
     * <p>Creates a {@link Resource} from the {@code Resource.DEFAULT_} constants wrapped in a
     * {@link ResourceManager}, and an empty {@link EnemyManager}.</p>
     *
     * @param map    of the world to load.
     * @param listen Listener instance we wish this instance of the game to run on
     * @throws WorldLoadException If the maps contents are not a valid world encoding.
     */
    public FireWall(String map, ListenerManager listen) throws WorldLoadException {
        resources = new ResourceManager(
                new Resource(
                        Resource.DEFAULT_STARTING_RESOURCES,
                        Resource.DEFAULT_MIN_RESOURCES,
                        Resource.DEFAULT_MAX_RESOURCES
                ), listen
        );

        final WorldBuilder worldBuilder = new WorldBuilder(listen);

        final List<Tile> tiles = map.isBlank() ? generateMilliesWorld(listen)
                : worldBuilder.fromString(Engine.getSpatial(), map);
        world = worldBuilder.fromTiles(tiles);

        enemies = new EnemyManager(listen, world);
    }

    private List<Tile> generateMilliesWorld(ListenerManager listen) {
        final List<Tile> tiles = new ArrayList<>();
        final int maxTiles = Engine.DEFAULT_TILES_PER_ROW;
        for (int col = 0; col < maxTiles; col++) {
            for (int row = 0; row < maxTiles; row++) {
                Position position = new GridPosition(col, row);
                if (col == 2 && row == 0) {
                    tiles.add(new Spawner(position, listen));
                    continue;
                }
                if (col == 5 && row == 15) {
                    tiles.add(new Spawner(position, listen));
                    continue;
                }
                if (col == maxTiles / 2
                        && row == maxTiles / 2) {
                    tiles.add(new Home(position, listen));
                    continue;
                }
                if ((col == 0 || col == maxTiles - 1)
                        || (row == 0 || row == maxTiles - 1)) {
                    tiles.add(new Forest(position, listen));
                    continue;
                }
                tiles.add(new Grass(position, listen));
            }
        }
        return tiles;
    }
}
