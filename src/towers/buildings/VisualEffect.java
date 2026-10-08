package towers.buildings;

import engine.art.sprites.SpriteGroup;
import engine.game.Position;
import engine.timing.RepeatingTimer;
import engine.timing.TickTimer;
import towers.GameEntity;
import towers.events.ListenerManager;
import towers.events.events.Event;
import towers.events.events.RenderEvent;

public class VisualEffect extends GameEntity {
    public final int ANIMATION_INTERVAL = 8;
    private final SpriteGroup art;
    private final TickTimer timer = new RepeatingTimer(ANIMATION_INTERVAL);
    private int index = 0;
    final private int finalAnimIndex;

    public VisualEffect(Position pos, SpriteGroup art, ListenerManager listen) {
        super(pos);
        this.art = art;
        finalAnimIndex = art.getSprites().size() - 1;
        setSprite(art.getSprite(index + ""));
        this.setRenderOrder(2);
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forRender(this::render));
    }

    private void nextAnimIndex() {
        index += 1;
        if (index > finalAnimIndex) {
            index = finalAnimIndex;
            markForRemoval();
        }
    }

    private void updateSprite() {
        nextAnimIndex();
        setSprite(art.getSprite(index + ""));
    }

    private void render(RenderEvent event) {
        event.render(this);
    }

    /**
     * Ticks the internal state of the bomb forward.
     * Including:
     * <ul>
     * <li>Moving the bomb forward.</li>
     * <li>Ticking forward the sprite based on the Bomb.ANIMATION_INTERVAL so it animates over time</li>
     * <li>Marks the bomb for removal if it would tick the animation forward and can not as it was already on its final sprite.</li>
     * <li>Marks the bomb for removal if it is out of bounds.</li>
     * </ul>
     */
    public void tick(Event event) {
        timer.tick();
        if (timer.isFinished()) {
            updateSprite();
        }
        if (isOutOfBounds()) {
            markForRemoval();
        }
    }
}