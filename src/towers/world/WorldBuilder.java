package towers.world;

import engine.game.GridPosition;
import engine.game.Position;
import engine.renderer.Spatial;
import java.util.ArrayList;
import java.util.List;

import towers.events.ListenerManager;
import towers.tiles.Tile;
import towers.tiles.TileFactory;


/**
 * Builds a {@link World} from a text encoding of a map.
 *
 * <p>Each line of the text is one row of the grid and each character one tile, decoded by
 * {@link TileFactory#fromSymbol(Position, char)}. So a file beginning</p>
 *
 * <pre>
 * ffff
 * fdgf
 * </pre>
 *
 * <p>describes a border of forest with dirt and grass inside it.</p>
 */
public class WorldBuilder {
    private final ListenerManager listen;
    public WorldBuilder(ListenerManager listen) {
        this.listen = listen;
    }
    /**
     * Construct a new {@link World} containing the given tiles.
     *
     * @param tiles The tiles to place into the new world.
     * @return A new world containing exactly the given tiles.
     * @ensures The {@link World#allTiles()} method matches the given tiles.
     */
    public World fromTiles(List<Tile> tiles) {
        World world = new CastleWorld(listen);
        for (Tile tile : tiles) {
            world.place(tile);
        }
        return world;
    }

    /**
     * Decode a map from text into a list of {@link Tile}s.
     *
     * <p>The text is split on newlines. Both the number of rows and the length of each row must
     * equal {@code spatial.windowSize() / spatial.gridCellSize()}, the number of grid cells across
     * the window: an 800 pixel window with 25 pixel cells needs 32 lines of 32 characters.</p>
     *
     * <p>Each character becomes a tile at the {@link engine.game.GridPosition} given by its column
     * and row, so the character at line 3, position 10 becomes a tile at x = 9, y = 2.</p>
     *
     * @param spatial The dimensions of the window; the map must match these.
     * @param text    The text encoding of a map.
     * @return The tiles described by the text, one per character.
     * @throws WorldLoadException If the number of lines does not match the dimensions.
     * @throws WorldLoadException If any line's length does not match the dimensions.
     * @throws WorldLoadException If any character does not correspond to a tile.
     */
    public List<Tile> fromString(Spatial spatial, String text)
            throws WorldLoadException {
        int numberOfTiles = spatial.windowSize() / spatial.gridCellSize();
        String[] lines = text.split("\n");

        if (lines.length != numberOfTiles) {
            throw new WorldLoadException("Expected " + numberOfTiles
                    + " lines to match the given spatial but got " + lines.length);
        }

        List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < numberOfTiles; row++) {
            char[] currentRow = lines[row].strip().toCharArray();

            if (currentRow.length != numberOfTiles) {
                throw new WorldLoadException("Expected " + numberOfTiles
                        + " characters to match the given spatial but got "
                        + currentRow.length, row);
            }

            for (int col = 0; col < numberOfTiles; col++) {
                char symbol = currentRow[col];
                final Position position = new GridPosition(col, row);
                try {
                    final TileFactory tileFactory = new TileFactory(listen);
                    tiles.add(tileFactory.fromSymbol(position, symbol));
                } catch (IllegalArgumentException e) {
                    throw new WorldLoadException("Unknown symbol: '" + symbol + "'", row, col);
                }
            }
        }
        return tiles;
    }
}
