package towers.events;

import engine.game.Removable;
import towers.events.events.Event;

import java.util.function.Consumer;

public class Listener<T extends Event> implements Removable {
    private final Consumer<T> action;
    private boolean removed = false;

    public Listener(Consumer<T> action) {
        this.action = action;
    }

    public void act(T event) {
        action.accept(event);
    }

    /**
     * Whether this entity is marked for removal.
     *
     * @return True if this entity has been marked for removal.
     */
    @Override
    public boolean isMarkedForRemoval() {
        return removed;
    }

    /**
     * Marks the listener for removal,
     * if it is not already null sets the action it is holding to null.
     *
     * @ensures {{@link #isMarkedForRemoval()}} returns true.
     */
    @Override
    public void markForRemoval() {
        removed = true;
    }
}

