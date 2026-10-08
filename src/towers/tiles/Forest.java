package towers.tiles;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import towers.SpriteGallery;
import towers.events.ListenerManager;
import towers.events.events.Event;

/**
 * A forest tile, representing an obstacle that enemies cannot path through.
 * Buildings cannot be built on a forest tile.
 *
 * <p>A forest tile is displayed as the "default" identifier in {@link SpriteGallery#forest}.
 *
 * @invariant {@link #canWalkThrough()} is always false.
 */
public class Forest extends AbstractTile {
    private final SpriteGroup art = SpriteGallery.forest;

    /**
     * Construct a forest tile at the given position.
     *
     * @param position The position to place the tile.
     */
    public Forest(Position position, ListenerManager listen) {
        super(position, listen);
        setSprite(art.getSprite("default"));
        listen.forEndGame(this::end);
    }

    private void end(Event event) {
        setSprite(art.getSprite("gameover"));
    }

    /**
     * Whether enemies may walk through this tile, which for a forest is never.
     *
     * @return False, regardless of what is built on this tile.
     */
    @Override
    public boolean canWalkThrough() {
        return false;
    }
}
