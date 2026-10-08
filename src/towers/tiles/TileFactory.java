package towers.tiles;

import engine.game.Position;
import towers.events.ListenerManager;

/**
 * Conversion of a character into a {@link Tile} instance.
 */
public class TileFactory {
    private final ListenerManager listen;
    public TileFactory(ListenerManager listen) {
        this.listen = listen;
    }
    /**
     * Construct the {@link Tile} the given symbol stands for, at the given position.
     *
     * <table>
     *     <tr><th>Character</th><th>Tile</th></tr>
     *     <tr><td>d</td><td>{@link Dirt}</td></tr>
     *     <tr><td>g</td><td>{@link Grass}</td></tr>
     *     <tr><td>f</td><td>{@link Forest}</td></tr>
     *     <tr><td>e</td><td>{@link Home}</td></tr>
     *     <tr><td>s</td><td>{@link Spawner}</td></tr>
     *     <caption>&nbsp;</caption>
     * </table>
     *
     * @param position The position to place the new tile at.
     * @param symbol The character identifying which kind of tile to create.
     * @return A new tile of the type the symbol stands for, never null.
     * @throws IllegalArgumentException If the symbol is not listed above.
     */
    public Tile fromSymbol(Position position, char symbol) throws IllegalArgumentException {
        return switch (symbol) {
            case 'f' -> new Forest(position, listen);
            case 'd' -> new Dirt(position, listen);
            case 'g' -> new Grass(position, listen);
            case 'e' -> new Home(position, listen);
            case 's' -> new Spawner(position, listen);
            default -> throw new IllegalArgumentException("Invalid symbol");
        };
    }
}
