package towers;

import engine.game.Removable;
import towers.events.ListenerManager;
import towers.events.events.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * A collection of removables that automatically empties when the cleanup event is fired.
 */
public class SelfCleaningCollection<T extends Removable> {
    private final List<T> removables = new ArrayList<>();

    /**
     * Construct a new collection bound to this listener.
     *
     * @param listen A listener manager that triggers the cleanup event.
     */
    public SelfCleaningCollection(ListenerManager listen) {
        listen.forCleanup(this::cleanup);
    }

    /**
     * Add the given removable instance to the collection.
     *
     * @param removable An instance of removable.
     */
    public void add(T removable) {
        removables.add(removable);
    }

    private void cleanup(Event event) {
        for (int i = removables.size() - 1; i >= 0; i -= 1) {
            if (removables.get(i).isMarkedForRemoval()) {
                removables.remove(i);
            }
        }
    }

    /**
     * Collect a copy of all removables in the collection.
     *
     * @return A copy of removables.
     */
    public List<T> toList() {
        return new ArrayList<>(removables);
    }
}
