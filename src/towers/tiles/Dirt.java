package towers.tiles;

import engine.Engine;
import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import towers.SpriteGallery;
import towers.events.ListenerManager;
import towers.events.events.Event;

/**
 * A dirt tile, representing one possible path for enemies to follow.
 * Buildings cannot be built on a dirt tile.
 *
 * <p>A dirt tile is displayed as the "default" identifier in {@link SpriteGallery#dirt}.
 */
public class Dirt extends AbstractTile {
    private final SpriteGroup art = SpriteGallery.dirt;

    /**
     * Construct a dirt tile at the given position.
     *
     * @param position The position to place the tile.
     */
    public Dirt(Position position, ListenerManager listen) {
        super(position, listen);
        setSprite(art.getSprite("default"));
        listen.forEndGame(this::end);
    }

    private void end(Event event) {
        setSprite(art.getSprite("gameover"));
    }
}