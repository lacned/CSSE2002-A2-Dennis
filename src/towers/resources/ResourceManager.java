package towers.resources;

import engine.game.GridPosition;
import engine.game.Position;
import engine.input.MouseState;
import engine.ui.Letter;
import towers.buildings.Tower;
import towers.events.ListenerManager;
import towers.events.events.RenderEvent;
import towers.events.events.Event;

/**
 * Wraps a {@link Resource} with a safe way to spend it and a {@link Letter} digit to display it.
 *
 * <p>The score is always displayed at the grid position (x,y) -> (15, 0).
 *
 * @invariant The displayed {@link Letter} is consistent with the {@link Resource} value.
 */
public class ResourceManager {
    private final Resource resource;
    private final Position scorePosition = new GridPosition(15, 0);
    private final ListenerManager listen;
    private boolean previousLeftPressed = false;
    private boolean previousRightPressed = false;

    /**
     * Construct a manager around the given {@link Resource}, updating the rendered letter to match it.
     *
     * @param resource The resource to wrap, spend from and display.
     * @ensures The displayed score matches the resource's current value.
     */
    public ResourceManager(Resource resource, ListenerManager listen) {
        this.resource = resource;
        listen.forTick(this::tick);
        listen.forRender(this::render);
        this.listen = listen;
    }

    private boolean canAfford(int cost) {
        return cost <= resource.getCurrent();
    }

    /**
     * Attempt to spend the given amount, all or nothing.
     *
     * <p>If unaffordable, nothing is deducted, so a caller ignoring the result cannot buy something
     * for free. Otherwise, the amount is deducted (via {@link Resource#spend(int)}) and the
     * displayed digit refreshed.
     *
     * @param cost The amount to attempt to spend.
     * @return True if the amount was affordable and has now been deducted.
     * @requires cost >= 0, so spending cannot be used to gain resources.
     * @ensures If \result is false then the resource is unchanged.
     * @ensures The displayed score matches the resource's current value.
     */
    public boolean spend(int cost) {
        assert cost >= 0;
        if (!canAfford(cost)) {
            return false;
        }
        resource.spend(cost);
        return true;
    }

    private boolean leftJustWentDown(MouseState mouse) {
        return !previousLeftPressed && mouse.isLeftPressed();
    }

    private boolean rightJustWentDown(MouseState mouse) {
        return !previousRightPressed && mouse.isRightPressed();
    }

    private void tick(Event event) {
        resource.tick(event.getEngine());
        final MouseState mouse = event.getEngine().getMouse();

        //we should only spend 1 attempt on the first frame a mouse button goes down
        final boolean leftDown = leftJustWentDown(mouse);
        final boolean rightDown = rightJustWentDown(mouse);
        previousLeftPressed = mouse.isLeftPressed();
        previousRightPressed = mouse.isRightPressed();
        if (leftDown && spend(Tower.COST)) {
            final Position pos = event.getEngine().getMouse().screenPosition();
            listen.emitBuildRequest(pos);
        } else if (rightDown && spend(Tower.VORTEX_COST)) {
            final Position pos = event.getEngine().getMouse().screenPosition();
            listen.emitBuildRequest(pos);
        } else {
            //do nothing
        }
    }

    private void render(RenderEvent event) {
        final Letter letter = new Letter(
                scorePosition,
                (char) ('0' + Math.min(9, resource.getCurrent()))
        );
        letter.setRenderOrder(3); //Guarantees the letter will go on top
        event.render(letter);
    }
}
