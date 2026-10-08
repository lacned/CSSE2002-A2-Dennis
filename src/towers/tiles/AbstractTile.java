package towers.tiles;

import engine.game.Position;

import java.util.ArrayList;
import java.util.List;

import towers.GameEntity;
import towers.buildings.Building;
import towers.events.ListenerManager;
import towers.events.events.RenderEvent;
import towers.events.events.Event;

/**
 * One square in the world, responsible for the {@link Building}s stacked on it.
 *
 * @invariant A tile's position never changes once constructed.
 */
public abstract class AbstractTile extends GameEntity implements Tile {
    private final List<Building> buildings = new ArrayList<>();

    /**
     * Construct a tile at the given position, with nothing built on it.
     *
     * @param pos The position to place the tile.
     * @ensures {@link #getBuildings()} is empty.
     */
    public AbstractTile(Position pos, ListenerManager listen) {
        super(pos);
        trackListener(listen.forCleanup(this::cleanup));
        trackListener(listen.forRender(this::render));
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public List<Building> getBuildings() {
        // return new ArrayList<>(buildings); is our usual solution
        // but the actually immutable return usually causes some issues if
        // the tests don't expect it.
        return List.copyOf(buildings);
    }

    public boolean canWalkThrough() {
        return getBuildings().isEmpty();
    }

    private void cleanup(Event event) {
        for (int i = buildings.size() - 1; i >= 0; i -= 1) {
            if (buildings.get(i).isMarkedForRemoval()) {
                buildings.remove(i);
            }
        }
    }

    private void render(RenderEvent event) {
        event.render(this);
    }
}
