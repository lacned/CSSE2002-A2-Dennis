package towers.tiles;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import towers.SpriteGallery;
import towers.events.ListenerManager;
import towers.events.events.EnemyMove;

/**
 * A home tile, representing the destination that enemies are trying to reach.
 * Buildings cannot be built on a home tile.
 *
 * <p>A home tile is displayed as the "village" identifier in {@link SpriteGallery#dirt}.
 */
public class Home extends AbstractTile {
    private final SpriteGroup art = SpriteGallery.dirt;
    private final ListenerManager listen;

    /**
     * Construct a home tile at the given position.
     *
     * @param pos The position to place the tile.
     */
    public Home(Position pos, ListenerManager listen) {
        super(pos, listen);
        setSprite(art.getSprite("village"));
        trackListener(listen.forEnemyMove(this::enemyMove));
        this.listen = listen;
    }

    private void enemyMove(EnemyMove event) {
        if(event.getEnemy().grid().overlaps(this)) {
            listen.emitEndGame();
        }
    }
}
