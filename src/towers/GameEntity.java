package towers;

import engine.game.Entity;
import engine.game.Position;
import towers.events.Listener;

import java.util.ArrayList;
import java.util.List;

/**
 * An extension of entity which tracks listeners,
 * removing listeners whenever the entity is removed.
 */
public class GameEntity extends Entity {
    final List<Listener<?>> listeners = new ArrayList<>();

    public GameEntity(Position pos) {
        super(pos);
    }

    /**
     * Register a new listener to track.
     *
     * @param listener A listener to track.
     */
    public void trackListener(Listener<?> listener) {
        listeners.add(listener);
    }

    @Override
    public void markForRemoval() {
        super.markForRemoval();
        for (Listener<?> listener : listeners) {
            listener.markForRemoval();
        }
        listeners.clear();
    }
}
