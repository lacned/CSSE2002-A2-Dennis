package towers.tiles;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;
import towers.SpriteGallery;
import towers.events.ListenerManager;
import towers.events.events.Event;


/**
 * A spawner tile, representing the camp that enemies emerge from.
 * Buildings cannot be built on a spawner tile.
 *
 * <p>A spawner tile is displayed as the "camp" identifier in {@link SpriteGallery#dirt}.
 *
 * @invariant A spawner produces at most one enemy per tick.
 */
public class Spawner extends AbstractTile {
    /**
     * The number of ticks between one enemy being produced and the next.
     */
    public static final int SPAWN_INTERVAL = 100;
    private final SpriteGroup art = SpriteGallery.dirt;
    private final TickTimer timer = new RepeatingTimer(SPAWN_INTERVAL);
    private final ListenerManager listen;
    private boolean gameOver = false;

    /**
     * Construct a spawner tile at the given position, with its spawn timer already running.
     *
     * @param pos The position to place the tile.
     */
    public Spawner(Position pos, ListenerManager listen) {
        super(pos, listen);
        setSprite(art.getSprite("camp"));
        this.listen = listen;
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forEndGame(this::endGame));
    }

    private void endGame(Event event) {
        gameOver = true;
    }

    private void tick(Event event) {
        super.tick(event.getEngine());
        if(gameOver) {
            return;
        }
        timer.tick();
        if (timer.isFinished()) {
            listen.emitSpawnEnemy(this);
        }
    }
}
