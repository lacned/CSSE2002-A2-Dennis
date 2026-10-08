package towers.buildings;

import engine.art.sprites.SpriteGroup;
import engine.game.Direction;
import engine.game.Position;

import engine.pathing.DirectPathFinder;
import towers.GameEntity;
import towers.SpriteGallery;
import towers.enemies.Enemy;
import towers.events.ListenerManager;
import towers.events.events.RenderEvent;
import towers.events.events.EnemyMove;
import towers.events.events.Event;

/**
 * A projectile to fire at an {@link Enemy}.
 *
 * <p>Note: a bomb aims once at construction and never re-aims, so a fast moving enemy can be
 * missed.
 *
 * <p>A bomb is displayed as the "default" identifier in {@link SpriteGallery#bomb}.
 *
 * <p>A bomb's render order should be set to 2.
 *
 * @invariant A bomb's direction never changes after construction.
 */
public class Bomb extends GameEntity {
    /**
     * How far a bomb moves along its {@link Direction} each tick, in screen pixels.
     */
    public static final int SPEED = 1;
    private static final SpriteGroup art = SpriteGallery.bomb;
    private final Direction direction;
    private final ListenerManager listen;

    /**
     * Construct a bomb at the given {@link Position}, aimed at the given target as the
     * first step of the straight line path a {@link DirectPathFinder} produces.
     *
     * @param pos    The position to create the bomb at.
     * @param target The position of the enemy being fired at.
     * @ensures The bomb's direction points from pos towards target and will not change.
     */
    public Bomb(Position pos, Position target, ListenerManager listen) {
        super(pos);
        setSprite(art.getSprite("default"));
        direction = aim(target);
        setRenderOrder(2);
        trackListener(listen.forTick(this::tick));
        trackListener(listen.forEnemyMove(this::enemyMove));
        trackListener(listen.forRender(this::render));
        this.listen = listen;
    }

    /**
     * The {@link Direction} pointing from this bomb towards the given target, taken as the first
     * step of the straight line path a {@link DirectPathFinder} produces.
     *
     * @param target The position being aimed at.
     * @return The direction from this bomb to the target.
     */
    private Direction aim(Position target) {
        return new DirectPathFinder(target).findPath(this).nextDirection(this);
    }

    public void tick(Event event) {
        applySpeed();
        if (isOutOfBounds()) {
            markForRemoval();
        }
    }

    public void enemyMove(EnemyMove event) {
        if (this.grid().overlaps(event.getEnemy())) {
            event.getEnemy().markForRemoval();
            markForRemoval();
            new VisualEffect(event.getEnemy(), SpriteGallery.explosion, listen);
        }
    }

    private void render(RenderEvent event) {
        event.render(this);
    }

    private void applySpeed() {
        setPosition(screen().shift(direction, SPEED));
    }
}
