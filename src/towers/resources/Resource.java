package towers.resources;

import engine.EngineState;
import engine.game.Tickable;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;

/**
 * The currency {@link towers.buildings.Tower}s are paid for with, regenerating over time.
 *
 * <p>The clamp is applied on construction and after every change, so regeneration cannot exceed the
 * maximum and spending cannot go below the minimum.
 *
 * @invariant min &le; {@link #getCurrent()} &le; max
 * @invariant min &le; max
 */
public class Resource implements Tickable {
    /**
     * The number of ticks between one automatic increase and the next.
     */
    public static final int RESOURCE_INTERVAL = 400;
    /**
     * How much the value increases each time {@link #RESOURCE_INTERVAL} ticks have elapsed.
     */
    public static final int RESOURCE_INCREMENT = 1;

    /**
     * The value a new game starts at, so how many towers the player can build immediately.
     */
    public static final int DEFAULT_STARTING_RESOURCES = 4;

    /**
     * The lowest value a resource may hold, the floor spending is clamped to.
     */
    public static final int DEFAULT_MIN_RESOURCES = 0;

    /**
     * The highest value a resource may hold, the ceiling regeneration is clamped to.
     */
    public static final int DEFAULT_MAX_RESOURCES = 9;

    private int current;
    private final int min;
    private final int max;
    private final TickTimer timer = new RepeatingTimer(RESOURCE_INTERVAL);

    /**
     * Construct a resource with the given starting value and bounds, and start its regeneration
     * timer.
     *
     * <p>The starting value is clamped immediately, so a caller asking for one outside the bounds
     * gets the nearest legal value rather than a resource violating its own invariant.
     *
     * @param initial The value to start at.
     * @param min     The lowest value this resource may hold.
     * @param max     The highest value this resource may hold.
     * @requires min &le; max
     * @ensures min &le; {@link #getCurrent()} &le; max
     */
    public Resource(int initial, int min, int max) {
        assert min <= max;
        this.current = initial;
        this.min = min;
        this.max = max;
        enforceBounds();
    }

    private void enforceBounds() {
        if (current > max) {
            current = max;
        }
        if (current < min) {
            current = min;
        }
    }

    /**
     * How much of the resource is currently available to spend.
     *
     * @return The current value.
     * @ensures min &le; \result &le; max
     */
    public int getCurrent() {
        return current;
    }

    /**
     * Deduct the given cost from the current value, clamping the result back into range.
     *
     * <p>Affordability is not checked, a cost larger than the current value will clamp to the minimum.
     * Deciding whether a purchase goes ahead is the job of {@link ResourceManager#spend(int)}.
     *
     * @param cost The amount to deduct.
     * @ensures min &le; {@link #getCurrent()} &le; max
     */
    public void spend(int cost) {
        this.current -= cost;
        enforceBounds();
    }

    /**
     * Progress the resource by one tick, regenerating as required.
     *
     * <p>Each time the resource regenerated, the value increases by {@link #RESOURCE_INCREMENT}
     * and is clamped back into range, so a resource already at its maximum stays there.
     *
     * @param state The state of the engine, including the mouse, keyboard information and
     *              dimensions of the window.
     * @ensures min &le; {@link #getCurrent()} &le; max
     */
    public void tick(EngineState state) {
        timer.tick();
        if (timer.isFinished()) {
            current += RESOURCE_INCREMENT;
            enforceBounds();
        }
    }
}
